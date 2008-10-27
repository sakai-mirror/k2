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

import org.sakaiproject.kernel.api.ClasspathDependency;
import org.sakaiproject.kernel.api.ComponentDependency;
import org.sakaiproject.kernel.api.ComponentSpecification;
import org.sakaiproject.kernel.api.ComponentSpecificationException;
import org.sakaiproject.kernel.api.PackageExport;
import org.sakaiproject.kernel.component.model.Component;
import org.sakaiproject.kernel.util.ComponentSpecificationUtil;
import org.sakaiproject.kernel.util.ResourceLoader;
import org.sakaiproject.kernel.util.XSDValidator;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * A Component Specification that is loaded from a xml resource specified by a
 * uri.
 */
public class URLComponentSpecificationImpl implements ComponentSpecification {

  /**
   * The location of the components XSD, which is used to validate the
   * components files.
   */
  private static final String COMPONENTS_XSD = "res://components.xsd";
  /**
   * The default specifiation.
   */
  private String specification = "<empty />";
  /**
   * The component model as loaded from the xml file.
   */
  private Component component;
  /**
   * dependencies of the component.
   */
  private ComponentDependency[] componentDependencies;
  /**
   * The classpath dependencies of this component
   */
  private ClasspathDependency[] dependencies;

  @SuppressWarnings("unused")
  private String source;
  private PackageExport[] exports;
  private URL componentClasspath;

  /**
   * Construct a URL based component specification based on the supplied string
   * representation of the URL. That URL points to a base location that contains
   * a components.xml defining classpath, component activation, and component
   * dependency.
   * 
   * @param source
   * @param d
   * @throws IOException
   * @throws ComponentSpecificationException
   */
  public URLComponentSpecificationImpl(String source, String d)
      throws ComponentSpecificationException {
    if (source != null) {
      try {
        this.componentClasspath = new URL(source);
      } catch (MalformedURLException e) {
        throw new ComponentSpecificationException(
            "The source of the component is does not represent a URL " + source,e);
      }
    }
    XStream xstream = new XStream();
    Annotations.configureAliases(xstream, Component.CLASSES);
    Reader in = null;
    InputStream xsd = null;
    try {
      specification = ResourceLoader.readResource(d);
      xsd = ResourceLoader.openResource(COMPONENTS_XSD);
      String errors = XSDValidator.validate(specification, xsd);
      if (errors.length() > 0) {
        throw new ComponentSpecificationException(
            "Components file does not conform to schema " + errors);
      }
      in = new StringReader(specification);
      component = (Component) xstream.fromXML(in);

      if (component.getActivator() == null) {
        throw new ComponentSpecificationException(
            "A component must have an activator");
      }

      List<ComponentDependency> cdeps = component.getComponentDependencies();
      if (cdeps == null) {
        componentDependencies = new ComponentDependency[0];
      } else {
        componentDependencies = cdeps.toArray(new ComponentDependency[0]);
      }
      List<ClasspathDependency> deps = component.getDependencies();
      if (deps == null) {
        dependencies = new ClasspathDependency[0];
      } else {
        dependencies = deps.toArray(new ClasspathDependency[0]);
      }
      List<PackageExport> exs = component.getExports();
      if (exs == null) {
        exports = new PackageExport[0];
      } else {
        exports = exs.toArray(new PackageExport[0]);
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

  /**
   * @return an array of URL's representing the classpath for the component.
   * @see org.sakaiproject.kernel.api.ComponentSpecification#getClassPathURLs()
   */
  public ClasspathDependency[] getClassPathDependencies() {
    return dependencies;
  }

  /**
   * @return the name of the class that is the activator for this component.
   * @see org.sakaiproject.kernel.api.ComponentSpecification#
   *      getComponentActivatorClassName()
   */
  public String getComponentActivatorClassName() {
    return component.getActivator();
  }

  /**
   * @return an array of dependencies for this component.
   * @see org.sakaiproject.kernel.api.ComponentSpecification#getComponentDependencies()
   */
  public ComponentDependency[] getComponentDependencies() {
    return componentDependencies;
  }

  /**
   * @return true if the component is actively managed by the component manager.
   *         Managed components are started as dependencies of other components.
   *         If they are not managed, the must be explicitly started.
   * @see org.sakaiproject.kernel.api.ComponentSpecification#isManaged()
   */
  public boolean isManaged() {
    return component.getManaged();
  }

  /**
   * @return the xml that was used to configure the component specification.
   * @see org.sakaiproject.kernel.api.ComponentSpecification#getDefinition()
   */
  public String getDefinition() {
    return specification;
  }

  /**
   * @return the name of the component, that must be unique.
   * @see org.sakaiproject.kernel.api.ComponentSpecification#getName()
   */
  public String getName() {
    return component.getName();
  }

  /**
   * @return a description of the component spec showing the name of this
   *         component and the components which this component depends on.
   * @see org.sakaiproject.kernel.api.ComponentSpecification#getDependencyDescription
   *      ()
   */
  public String getDependencyDescription() {
    return ComponentSpecificationUtil.formatDescription(this);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.ComponentSpecification#getDependencies()
   */
  public ClasspathDependency[] getDependencies() {
    return dependencies;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.ComponentSpecification#getExports()
   */
  public PackageExport[] getExports() {
    return exports;
  }

  /**
   * @return the url to the start of
   */
  public URL getComponentClasspath() {
    return componentClasspath;
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.ComponentSpecification#isKernelBootstrap()
   */
  public boolean isKernelBootstrap() {
    return false;
  }

}
