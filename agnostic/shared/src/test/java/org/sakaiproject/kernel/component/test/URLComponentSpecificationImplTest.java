/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
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
package org.sakaiproject.kernel.component.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.sakaiproject.kernel.api.ClasspathDependency;
import org.sakaiproject.kernel.api.ComponentDependency;
import org.sakaiproject.kernel.api.ComponentSpecificationException;
import org.sakaiproject.kernel.api.PackageExport;
import org.sakaiproject.kernel.component.ResourceLoader;
import org.sakaiproject.kernel.component.URLComponentSpecificationImpl;
import org.sakaiproject.kernel.component.XSDValidator;
import org.sakaiproject.kernel.component.model.ClasspathDepencencyImpl;
import org.sakaiproject.kernel.component.model.ClasspathScope;
import org.sakaiproject.kernel.component.model.Component;
import org.sakaiproject.kernel.component.model.ComponentDependencyImpl;
import org.sakaiproject.kernel.component.model.PackageExportImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class URLComponentSpecificationImplTest {

  private static final String TEST_RESOURCE = "res://org/sakaiproject/kernel/component/test/simplecomponent.xml";
  private static final String TEST_COMPLEX_RESOURCE = "res://org/sakaiproject/kernel/component/test/complexcomponent.xml";
  private static final Log LOG = LogFactory.getLog(URLComponentSpecificationImplTest.class);
  private static final String COMPONENTS_XSD = "res://components.xsd";
  private static final String XML_DECL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
  private static final String XML_XSD = " xmlns=\"http://ns.sakaiproject.org/2008/components\" ";
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
  }
  
  @Test
  public void testOutput() throws IOException {
    XStream xstream = new XStream();
    Annotations.configureAliases(xstream, Component.CLASSES);
    Component c = new Component();
    c.setActivator("activator.class");
    c.setClassPath("classpath");
    c.setDocumentation("docs");
    c.setManaged(true);
    c.setName("Some Name");
    ComponentDependencyImpl cd = new ComponentDependencyImpl();
    cd.setComponentName("Some Other Name");
    cd.setManaged(false);
    ComponentDependencyImpl cd1 = new ComponentDependencyImpl();
    cd1.setComponentName("Some Other Name");
    cd1.setManaged(false);
    List<ComponentDependency> cdl = new ArrayList<ComponentDependency>();
    cdl.add(cd);
    cdl.add(cd1);
    c.setComponentDependencies(cdl);
    
    ClasspathDepencencyImpl cpd1 = new ClasspathDepencencyImpl();
    cpd1.setGroupId("org.sakaiproject.kernel2.agnostic");
    cpd1.setArtifactId("shared");
    cpd1.setVersion("0.1-SNAPSHOT");
    cpd1.setScope(ClasspathScope.LOCAL);

    ClasspathDepencencyImpl cpd2 = new ClasspathDepencencyImpl();
    cpd2.setGroupId("org.sakaiproject.kernel2.agnostic");
    cpd2.setArtifactId("common");
    cpd2.setVersion("0.1-SNAPSHOT");
    cpd2.setScope(ClasspathScope.SHARE);
    
    List<ClasspathDependency> cpdl = new ArrayList<ClasspathDependency>();
    cpdl.add(cpd1);
    cpdl.add(cpd2);

    c.setDependencies(cpdl);
    
    PackageExportImpl pe1 = new PackageExportImpl();
    pe1.setName("org.sakaiproject.kernel.api");
    PackageExportImpl pe2 = new PackageExportImpl();
    pe2.setName("org.sakaiproject.search.api");
    
    List<PackageExport> pel = new ArrayList<PackageExport>();
    pel.add(pe1);
    
    c.setExports(pel);
    xstream.setMode(XStream.NO_REFERENCES);
   
    String specification = attachXSD(xstream.toXML(c));
    LOG.info(specification);
    InputStream xsd = ResourceLoader.openResource(COMPONENTS_XSD);
    String errors = XSDValidator.validate(specification, xsd);
    LOG.info(errors);
    assertEquals("",errors);
    
  }

  /**
   * @param xml
   * @return
   */
  private String attachXSD(String xml) {
    int endtag = xml.indexOf('>');
    return XML_DECL+xml.substring(0,endtag)+XML_XSD+xml.substring(endtag);
  }

  /**
   * Test method for {@link org.sakaiproject.kernel.component.URLComponentSpecificationImpl#URLComponentSpecificationImpl(java.lang.String)}.
   * @throws IOException 
   */
  @Test
  public void testSimpleComponentSpecificationImpl() throws IOException, ComponentSpecificationException {
    URLComponentSpecificationImpl uc = new URLComponentSpecificationImpl(null,TEST_RESOURCE);
    assertNotNull(uc.getClassPathURLs());
    assertEquals(0, uc.getClassPathURLs().length);
    assertNotNull(uc.getComponentActivatorClassName());
    assertNotNull(uc.getDependencies());
    assertEquals(0,uc.getDependencies().length);
  }
  
  @Test
  public void testComplexComponentSpecificationImpl() throws IOException, ComponentSpecificationException {
    URLComponentSpecificationImpl uc = new URLComponentSpecificationImpl(null,TEST_COMPLEX_RESOURCE);
    assertNotNull(uc.getClassPathURLs());
    assertEquals(1, uc.getClassPathURLs().length);
    assertNotNull(uc.getComponentActivatorClassName());
    assertNotNull(uc.getDependencies());
    assertEquals(3,uc.getDependencies().length);
  }
}
