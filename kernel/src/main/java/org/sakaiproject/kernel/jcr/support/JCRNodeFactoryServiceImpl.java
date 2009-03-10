/*
 * Licensed to the Sakai Foundation (SF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The SF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.sakaiproject.kernel.jcr.support;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.authz.PermissionDeniedException;
import org.sakaiproject.kernel.api.jcr.JCRConstants;
import org.sakaiproject.kernel.api.jcr.JCRService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;

import java.io.InputStream;
import java.util.GregorianCalendar;

import javax.jcr.AccessDeniedException;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;

/**
 * @author ieb This is a support service to make it easier to treat a JCR
 *         service as a Filing System.
 *
 **/
@Singleton
public class JCRNodeFactoryServiceImpl implements JCRNodeFactoryService {

  private static final Log log = LogFactory
      .getLog(JCRNodeFactoryServiceImpl.class);

  private JCRService jcrService;

  @Inject
  public JCRNodeFactoryServiceImpl(JCRService jcrService) {
    this.jcrService = jcrService;
  }

  private void populateFile(Node node, String mimeType)
      throws RepositoryException {
    // JCR Types
    if (jcrService.needsMixin(node, JCRConstants.MIX_REFERENCEABLE)) {
      node.addMixin(JCRConstants.MIX_REFERENCEABLE);
    }
    if (jcrService.needsMixin(node, JCRConstants.MIX_LOCKABLE)) {
      node.addMixin(JCRConstants.MIX_LOCKABLE);
    }
    if (jcrService.needsMixin(node, JCRConstants.MIX_SAKAIPROPERTIES)) {
      node.addMixin(JCRConstants.MIX_SAKAIPROPERTIES);
    }
    if (jcrService.needsMixin(node, JCRConstants.MIX_ACL)) {
      node.addMixin(JCRConstants.MIX_ACL);
    }
    Node resource = node.addNode(JCRConstants.JCR_CONTENT,
        JCRConstants.NT_UNSTRUCTURED);
    resource
        .setProperty(JCRConstants.JCR_LASTMODIFIED, new GregorianCalendar());
    resource.setProperty(JCRConstants.JCR_MIMETYPE,
        mimeType == null ? "application/octet-stream" : mimeType);
    resource.setProperty(JCRConstants.JCR_DATA, "");
    resource.setProperty(JCRConstants.JCR_ENCODING, "UTF-8");

  }

  private void populateFolder(Node node) throws RepositoryException {
    // JCR Types
    log.debug("Doing populate Folder");
    if (jcrService.needsMixin(node, JCRConstants.MIX_LOCKABLE)) {
      node.addMixin(JCRConstants.MIX_LOCKABLE);
    }
    if (jcrService.needsMixin(node, JCRConstants.MIX_REFERENCEABLE)) {
      node.addMixin(JCRConstants.MIX_REFERENCEABLE);
    }
    if (jcrService.needsMixin(node, JCRConstants.MIX_SAKAIPROPERTIES)) {
      node.addMixin(JCRConstants.MIX_SAKAIPROPERTIES);
    }
    if (jcrService.needsMixin(node, JCRConstants.MIX_ACL)) {
      node.addMixin(JCRConstants.MIX_ACL);
    }

    // node.setProperty(JCRConstants.JCR_LASTMODIFIED, new GregorianCalendar());

  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService#createFile
   * (java.lang.String)
   */
  public Node createFile(String filePath, String mimeType)
      throws JCRNodeFactoryServiceException {
    return createNode(filePath, mimeType, JCRConstants.NT_FILE);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService#createFolder
   * (java.lang.String)
   */
  public Node createFolder(String folderPath)
      throws JCRNodeFactoryServiceException {
    if ( folderPath.length() > 0 && folderPath.endsWith("/") ) {
      folderPath = folderPath.substring(0,folderPath.length()-2);
    }
    return createNode(folderPath, null, JCRConstants.NT_FOLDER);
  }

  /**
   * Create a new node. Nodes are of the form
   * nt:folder/nt:folder/nt:folder/nt:file nt:folders have properties nt:files
   * have properties nt:files have a nt:resource subnode
   *
   * @param id
   * @param string
   * @param collection
   * @return
   * @throws NodeFactoryServiceException
   * @throws TypeException
   */
  private Node createNode(String id, String mimeType, String type)
      throws JCRNodeFactoryServiceException {
    Node node = null;
    try {
      Session s = jcrService.getSession();
      Node n = getNodeFromSession(s, id);
      // the node might already exist
      if (n != null) {
        return n;
      }

      String vpath = getParentPath(id);
      while (n == null && !"/".equals(vpath)) {
        n = getNodeFromSession(s, vpath);
        if (n == null) {
          vpath = getParentPath(vpath);
        }
      }
      if (n == null) {
        n = s.getRootNode();
      }
      log.debug("VPath is " + vpath);
      String relPath = id.substring(vpath.length());
      // Node rootNode = s.getRootNode();
      if (relPath.startsWith("/")) {
        relPath = relPath.substring(1);
      }

      String[] pathElements = relPath.split("/");
      log.debug("RelPath is " + relPath + " split into " + pathElements.length
          + " elements ");
      for (String pathel : pathElements) {
        log.debug("       Path Element is [" + pathel + "]");
      }

      Node currentNode = n;
      for (int i = 0; i < pathElements.length; i++) {
        try {
          log.debug("Getting " + pathElements[i] + " under " + currentNode);
          currentNode = currentNode.getNode(pathElements[i]);
          if (!currentNode.isNodeType(JCRConstants.NT_FOLDER)
              && !currentNode.isNodeType(JCRConstants.NT_BASE)) {
            throw new JCRNodeFactoryServiceException(
                "Cant create collection or a folder inside a node that is not a folder "
                    + currentNode.getPath());
          }

        } catch (PathNotFoundException pnfe) {
          if (log.isDebugEnabled()) {
            log.debug("Not Found " + pnfe.getMessage() + " ");
          }
          if (i < pathElements.length - 1
              || JCRConstants.NT_FOLDER.equals(type)) {
            if (log.isDebugEnabled()) {
              log.debug("Adding Node " + pathElements[i] + " as "
                  + JCRConstants.NT_FOLDER + " to " + currentNode.getPath());
            }
            Node newNode = currentNode.addNode(pathElements[i],
                JCRConstants.NT_FOLDER);
            populateFolder(newNode);
            currentNode.save();
            currentNode = newNode;
            if (log.isDebugEnabled()) {
              log.debug("Adding Node Complete");
            }
          } else {
            if (log.isDebugEnabled()) {
              log.debug("Adding Node " + pathElements[i] + " as " + type
                  + " to " + currentNode.getPath());
            }
            Node newNode = currentNode.addNode(pathElements[i],
                JCRConstants.NT_FILE);
            populateFile(newNode, mimeType);
            currentNode.save();
            currentNode = newNode;
            if (log.isDebugEnabled()) {
              log.debug("Adding Node Complete");
            }

          }
        }
        if (currentNode.isCheckedOut()) {
          currentNode.save();
        }
      }
      node = currentNode;
      if (node == null) {
        log.error("Failed to create Node " + id + " got null ");
        throw new JCRNodeFactoryServiceException("Failed to create node " + id
            + " got null ");
      } else if (!id.equals(node.getPath())) {

        log.error("Failed to create Node " + id + " got" + node.getPath());
        throw new JCRNodeFactoryServiceException("Failed to create node " + id
            + " got " + node.getPath());
      }

    } catch (AccessDeniedException ax) {
      throw new PermissionDeniedException(ax.getMessage(), ax);
    } catch (RepositoryException rex) {
      log.warn("Unspecified Repository Failiure ", rex);
      log.error("Unspecified Repository Failiure " + rex.getMessage());
    }
    return node;

  }

  /**
   * @param s
   * @param id
   * @return
   * @throws NodeFactoryServiceException
   * @throws RepositoryException
   * @throws TypeException
   * @throws RepositoryException
   * @throws RepositoryException
   */
  private Node getNodeFromSession(Session s, String id)
      throws JCRNodeFactoryServiceException, RepositoryException {
    Item i;
    try {
      i = s.getItem(id);
    } catch (PathNotFoundException e) {
      log.debug("getNodeFromSession: Node Does Not Exist :" + id);
      return null;
    }
    Node n = null;
    if (i != null) {
      if (i.isNode()) {
        n = (Node) i;
      } else {
        throw new JCRNodeFactoryServiceException(
            "Path does not point to a node");
      }
    }
    return n;
  }

  /**
   * @param absPath
   * @return
   */
  private String getParentPath(String absPath) {
    int pre = absPath.lastIndexOf("/");
    if (pre > 0) {
      String parentPath = absPath.substring(0, pre);
      if (log.isDebugEnabled()) {
        log.debug("Parent path is [" + parentPath + "]");
      }
      return parentPath;
    }
    return "/";
  }

  public Node setInputStream(String id, InputStream in, String mimeType)
      throws JCRNodeFactoryServiceException, RepositoryException {
    Node newNode = createNode(id, mimeType, JCRConstants.NT_FILE);
    Session s = newNode.getSession();
    ValueFactory vf = s.getValueFactory();
    Value v = vf.createValue(in);
    Node contentNode = newNode.getNode(JCRConstants.JCR_CONTENT);
    contentNode.setProperty(JCRConstants.JCR_DATA, v);
    return newNode;
  }

  public InputStream getInputStream(String id) throws RepositoryException,
      JCRNodeFactoryServiceException {
    try {
      Session session = jcrService.getSession();
      Node n = getNodeFromSession(session, id);
      if (n != null) {
        Node contentNode = n.getNode(JCRConstants.JCR_CONTENT);
        Property property = contentNode.getProperty(JCRConstants.JCR_DATA);
        return property.getStream();
      }
      throw new JCRNodeFactoryServiceException(
          "Failed to open input stream for node, " + id
              + " as it does not exist");
    } catch (AccessDeniedException ax) {
      throw new PermissionDeniedException(ax.getMessage(), ax);
    }
  }

  public Node getNode(String id) throws RepositoryException,
      JCRNodeFactoryServiceException {
    try {
      Session session = jcrService.getSession();
      return getNodeFromSession(session, id);
    } catch (AccessDeniedException ax) {
      throw new PermissionDeniedException(ax.getMessage(), ax);
    }
  }
}
