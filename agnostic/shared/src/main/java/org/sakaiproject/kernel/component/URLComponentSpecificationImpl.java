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
package org.sakaiproject.kernel.component;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;

import org.sakaiproject.kernel.api.ComponentDependency;
import org.sakaiproject.kernel.api.ComponentSpecification;
import org.sakaiproject.kernel.api.ComponentSpecificationException;
import org.sakaiproject.kernel.component.model.Component;
import org.sakaiproject.kernel.component.model.Dependency;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.List;

/**
 *
 */
public class URLComponentSpecificationImpl implements ComponentSpecification {

  private static final String COMPONENTS_XSD = "res://components.xsd";
  private String specification = "<empty />";
  private Component component;
  private URL[] classPathUrls;
  private ComponentDependency[] dependencies;

  /**
   * Construct a URL based component specification based on the supplied string
   * representation of the URL. That URL points to a base location that contains
   * a components.xml defining classpath, component activation, and component
   * dependency.
   * 
   * @param d
   * @throws IOException
   * @throws ComponentSpecificationException
   */
  public URLComponentSpecificationImpl(String d)
      throws ComponentSpecificationException {
    XStream xstream = new XStream();
    Annotations.configureAliases(xstream, Component.class, Dependency.class);
    Reader in = null;
    InputStream xsd = null;
    try {
      specification = ResourceLoader.readResource(d);
      xsd = ResourceLoader.openResource(COMPONENTS_XSD);
      String errors = XSDValidator.validate(specification, xsd);
      if (errors.length() > 0) {
        throw new ComponentSpecificationException(
            "Components file does not conform ot schema " + errors);
      }
      in = new StringReader(specification);
      component = (Component) xstream.fromXML(in);

      if (component.getActivator() == null) {
        throw new ComponentSpecificationException(
            "A component must have an activator");
      }

      String classPath = component.getClassPath();
      if (classPath == null) {
        classPathUrls = new URL[0];
      } else {
        String[] cp = classPath.trim().split(";");
        classPathUrls = new URL[cp.length];
        int i = 0;
        for (String classpath : cp) {
          classPathUrls[i++] = new URL(classpath);
        }
      }
      List<ComponentDependency> deps = component.getDependencies();
      if (deps == null) {
        dependencies = new ComponentDependency[0];
      } else {
        dependencies = deps.toArray(new ComponentDependency[0]);
      }
    } catch (IOException e) {
      throw new ComponentSpecificationException(
          "Unable to load the component specification at " + d + " cause:"
              + e.getMessage(), e);
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
          // dont care about this.
        }
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.sakaiproject.kernel.api.ComponentSpecification#getClassPathURLs()
   */
  public URL[] getClassPathURLs() {
    return classPathUrls;
  }

  /*
   * (non-Javadoc)
   * 
   * @seeorg.sakaiproject.kernel.api.ComponentSpecification#
   * getComponentActivatorClassName()
   */
  public String getComponentActivatorClassName() {
    return component.getActivator();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.sakaiproject.kernel.api.ComponentSpecification#getDependencies()
   */
  public ComponentDependency[] getDependencies() {
    return dependencies;
  }


  /*
   * (non-Javadoc)
   * 
   * @see org.sakaiproject.kernel.api.ComponentSpecification#isManaged()
   */
  public boolean isManaged() {
    // TODO Auto-generated method stub
    return component.getManaged();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.sakaiproject.kernel.api.ComponentSpecification#getDefinition()
   */
  public String getDefinition() {
    return specification;
  }

  /* (non-Javadoc)
   * @see org.sakaiproject.kernel.api.ComponentSpecification#getName()
   */
  public String getName() {
    return component.getName();
  }

}
