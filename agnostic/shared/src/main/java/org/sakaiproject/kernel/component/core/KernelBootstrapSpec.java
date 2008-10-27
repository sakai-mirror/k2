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
package org.sakaiproject.kernel.component.core;

import org.sakaiproject.kernel.api.ClasspathDependency;
import org.sakaiproject.kernel.api.ComponentDependency;
import org.sakaiproject.kernel.api.ComponentSpecification;
import org.sakaiproject.kernel.api.PackageExport;
import org.sakaiproject.kernel.util.ComponentSpecificationUtil;

import java.net.URL;

/**
 * A hand coded specification for the kernel bootstrap.
 */
public class KernelBootstrapSpec implements ComponentSpecification {

  /**
   * An XML representation of the bootstrap specification.
   */
  private static final String SPECIFICATION = "<name>"
      + KernelBootstrapSpec.class.getName()
      + "</name><classpath>kernel</classpath><dependencies/>";
  /**
   * An empty list of component dependencies.
   */
  private ComponentDependency[] componentDependencies = new ComponentDependency[0];
  private ClasspathDependency[] dependencies = new ClasspathDependency[0];
  private PackageExport[] exports = new PackageExport[0];

  /**
   * We don't want any special classloader, so return a null classpath.
   * 
   * @return a null URL array representing no classloader.
   * @see org.sakaiproject.kernel.api.ComponentSpecification#getClassPathURLs()
   */
  public URL[] getClassPathURLs() {
    return null;
  }

  /**
   * @return the name of the component activator, ie
   *         {@link KernelBootstrapActivator}.
   * @seeorg.sakaiproject.kernel.api.ComponentSpecification# 
   *                                                         getComponentActivatorClassName
   *                                                         ()
   */
  public String getComponentActivatorClassName() {
    return KernelBootstrapActivator.class.getName();
  }

  /**
   * 
   * @return an array of dependencies.
   * @see org.sakaiproject.kernel.api.ComponentSpecification#getDependencies()
   */
  public ComponentDependency[] getComponentDependencies() {
    return componentDependencies;
  }

  /**
   * The defintion in XML form.
   * 
   * @see org.sakaiproject.kernel.api.ComponentSpecification#getDefinition()
   */
  public String getDefinition() {
    return SPECIFICATION;
  }

  /**
   * @return the name of the component.
   * @see org.sakaiproject.kernel.api.ComponentSpecification#getName()
   */
  public String getName() {
    return KernelBootstrapSpec.class.getName();
  }

  /**
   * @return a description of the depenency
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
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.ComponentSpecification#getComponentClasspath()
   */
  public URL getComponentClasspath() {
    return null;
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.ComponentSpecification#isKernelBootstrap()
   */
  public boolean isKernelBootstrap() {
    return true;
  }

}
