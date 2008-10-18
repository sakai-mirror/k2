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
import org.sakaiproject.kernel.api.ComponentDependency;
import org.sakaiproject.kernel.api.ComponentSpecificationException;
import org.sakaiproject.kernel.component.ResourceLoader;
import org.sakaiproject.kernel.component.URLComponentSpecificationImpl;
import org.sakaiproject.kernel.component.XSDValidator;
import org.sakaiproject.kernel.component.model.Component;
import org.sakaiproject.kernel.component.model.Dependency;

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
    Annotations.configureAliases(xstream, Component.class, Dependency.class);
    Component c = new Component();
    c.setActivator("activator.class");
    c.setClassPath("classpath");
    c.setDocumentation("docs");
    c.setManaged(true);
    c.setName("Some Name");
    Dependency cd = new Dependency();
    cd.setComponentName("Some Other Name");
    cd.setManaged(false);
    Dependency cd1 = new Dependency();
    cd1.setComponentName("Some Other Name");
    cd1.setManaged(false);
    List<ComponentDependency> cdl = new ArrayList<ComponentDependency>();
    cdl.add(cd);
    cdl.add(cd1);
    c.setComponentDependencies(cdl);
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
    URLComponentSpecificationImpl uc = new URLComponentSpecificationImpl(TEST_RESOURCE);
    assertNotNull(uc.getClassPathURLs());
    assertEquals(0, uc.getClassPathURLs().length);
    assertNotNull(uc.getComponentActivatorClassName());
    assertNotNull(uc.getDependencies());
    assertEquals(0,uc.getDependencies().length);
  }
  
  @Test
  public void testComplexComponentSpecificationImpl() throws IOException, ComponentSpecificationException {
    URLComponentSpecificationImpl uc = new URLComponentSpecificationImpl(TEST_COMPLEX_RESOURCE);
    assertNotNull(uc.getClassPathURLs());
    assertEquals(1, uc.getClassPathURLs().length);
    assertNotNull(uc.getComponentActivatorClassName());
    assertNotNull(uc.getDependencies());
    assertEquals(3,uc.getDependencies().length);
  }
}
