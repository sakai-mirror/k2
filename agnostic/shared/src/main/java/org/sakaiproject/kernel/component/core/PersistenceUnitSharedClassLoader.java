/*******************************************************************************
 * Copyright 2008 Sakai Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.sakaiproject.kernel.component.core;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.Artifact;
import org.sakaiproject.kernel.api.ArtifactResolverService;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.PackageRegistryService;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class PersistenceUnitSharedClassLoader extends SharedClassLoader {
  private static final Log LOG = LogFactory
      .getLog(PersistenceUnitSharedClassLoader.class);

  private static final String PERSISTENCE_XML = "META-INF/persistence.xml";
  private static final String ORM_XML = "META-INF/orm.xml";
  private static final String MASTER_URL_SUFFIX = "kernel-0.1-SNAPSHOT.jar!/"
      + PERSISTENCE_XML;

  private static final DocumentBuilderFactory DOC_BUILDER_FACTORY = DocumentBuilderFactory
      .newInstance();
  private static final XPathFactory XPATH_FACTORY = XPathFactory.newInstance();
  private static final XPath XPATH = XPATH_FACTORY.newXPath();
  private static final XPathExpression XPATH_ENTITY_PU_NODE;
  private static final XPathExpression XPATH_ENTITY_CLASS_TEXT;

  private URL persistenceXMLurl;

  static {
    try {
      XPATH_ENTITY_PU_NODE = XPATH.compile("//persistence/persistence-unit");
      XPATH_ENTITY_CLASS_TEXT = XPATH
          .compile("//persistence/persistence-unit/class/text()");
    } catch (XPathExpressionException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  enum Filter {
    MASTER {
      public boolean filter(URL url) {
        return url.toExternalForm().endsWith(MASTER_URL_SUFFIX);
      }
    },

    OTHERS {
      public boolean filter(URL url) {
        return !url.toExternalForm().endsWith(MASTER_URL_SUFFIX);
      }
    };

    public abstract boolean filter(URL url);
  }

  @Inject
  public PersistenceUnitSharedClassLoader(
      PackageRegistryService packageRegistryService,
      ArtifactResolverService artifactResolverService,
      @Named(SHARED_CLASSLOADER_ARTIFACT) Artifact artifact, Kernel kernel) {
    super(packageRegistryService, artifactResolverService, artifact, kernel);
  }

  @Override
  public Enumeration<URL> getResources(final String name) throws IOException {
    Enumeration<URL> returns = null;

    if (PERSISTENCE_XML.equals(name)) {
      if (persistenceXMLurl == null) {
        try {
          final String persistenceXml = scanPersistenceXML();
          LOG.debug("persistence.xml " + persistenceXml);

          // The base directory must be empty since Hibernate will scan it
          // searching for classes.
          final File file = new File(System.getProperty("java.io.tmpdir")
              + "/blueMarinePU/" + PERSISTENCE_XML);
          file.getParentFile().mkdirs();
          final PrintWriter pw = new PrintWriter(new FileWriter(file));
          pw.print(persistenceXml);
          pw.close();
          persistenceXMLurl = new URL("file://" + file.getAbsolutePath());
          LOG.info("URL: " + persistenceXMLurl);
        } catch (ParserConfigurationException e) {
          throw new IOException(e.toString());
        } catch (SAXException e) {
          throw new IOException(e.toString());
        } catch (XPathExpressionException e) {
          throw new IOException(e.toString());
        } catch (TransformerConfigurationException e) {
          throw new IOException(e.toString());
        } catch (TransformerException e) {
          throw new IOException(e.toString());
        }
      }

      returns = new Enumeration<URL>() {
        URL url = persistenceXMLurl;

        public boolean hasMoreElements() {
          return url != null;
        }

        public URL nextElement() {
          final URL url2 = url;
          url = null;
          return url2;
        }
      };
    } else {
      returns = super.getResources(name);
    }

    return returns;
  }

  /**
   * Looks through classloader resources to find all the persistence.xml files.
   * 
   * @param filter
   * @return
   * @throws IOException
   */
  private Collection<URL> findPersistenceXMLs(final Filter filter)
      throws IOException {
    final Collection<URL> result = new ArrayList<URL>();

    for (final Enumeration<URL> e = super.getResources(PERSISTENCE_XML); e
        .hasMoreElements();) {
      final URL url = e.nextElement();

      if (filter.filter(url)) {
        result.add(url);
      }
    }

    return result;
  }

  private String scanPersistenceXML() throws IOException,
      ParserConfigurationException, SAXException, XPathExpressionException,
      TransformerConfigurationException, TransformerException {
    LOG.info("scanPersistenceXML()");
    final DocumentBuilder builder = DOC_BUILDER_FACTORY.newDocumentBuilder();
    DOC_BUILDER_FACTORY.setNamespaceAware(true);

    final URL masterURL = findPersistenceXMLs(Filter.MASTER).iterator().next();
    LOG.debug(String.format(">>>> master persistence.xml: %s", masterURL));
    final Document masterDocument = builder.parse(masterURL.toExternalForm());
    final Node puNode = (Node) XPATH_ENTITY_PU_NODE.evaluate(masterDocument,
        XPathConstants.NODE);

    for (final URL url : findPersistenceXMLs(Filter.OTHERS)) {
      LOG.info(String.format(">>>> other persistence.xml: %s", url));
      final Document document = builder.parse(url.toExternalForm());
      final NodeList nodes = (NodeList) XPATH_ENTITY_CLASS_TEXT.evaluate(
          document, XPathConstants.NODESET);

      for (int i = 0; i < nodes.getLength(); i++) {
        final String entityClassName = nodes.item(i).getNodeValue();
        LOG.info(String.format(">>>>>>>> entity class: %s", entityClassName));

        if (i == 0) {
          puNode.appendChild(masterDocument.createTextNode("\n"));
          puNode.appendChild(masterDocument.createComment(" from "
              + url.toExternalForm().replaceAll(".*/cluster/modules/", "")
              + " "));
          puNode.appendChild(masterDocument.createTextNode("\n"));
        }

        final Node child = masterDocument.createElement("class");
        child.appendChild(masterDocument.createTextNode(entityClassName));
        puNode.appendChild(child);
        puNode.appendChild(masterDocument.createTextNode("\n"));
      }
    }

    return toString(masterDocument);
  }

  private String toString(Document doc) throws TransformerException {
    StringWriter out = new StringWriter();
    // Serialization through Transform.
    DOMSource domSource = new DOMSource(doc);
    StreamResult streamResult = new StreamResult(out);
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer serializer = tf.newTransformer();
    serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
    serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "users.dtd");
    serializer.setOutputProperty(OutputKeys.INDENT, "yes");
    serializer.transform(domSource, streamResult);
    return out.toString();
  }
}
