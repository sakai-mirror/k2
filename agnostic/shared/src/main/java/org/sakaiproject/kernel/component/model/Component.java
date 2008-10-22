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
package org.sakaiproject.kernel.component.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.sakaiproject.kernel.api.ComponentDependency;

import java.util.ArrayList;
import java.util.List;

/**
 * The model for a component, mapped to the xml definition of a component.
 * Mapped to the component element.
 */
@XStreamAlias("component")
public class Component {

  /**
   * A ; seperate list of class path uri's
   */
  private String classPath;
  /**
   * The name of activator class.
   */
  private String activator;

  /**
   * A list of dependencies mapped to the dependencies element.
   */
  @XStreamAlias(value = "dependencies", impl = ArrayList.class)
  private List<ComponentDependency> componentDependencies;

  /**
   * is the component actively managed by the component manager.
   */
  private boolean managed;
  /**
   * Some documentation about the component
   */
  private String documentation;
  /**
   * The name of the component.
   */
  private String name;

  /**
   * @return the classpath
   */
  public String getClassPath() {
    return classPath;
  }

  /**
   * @param classPath
   *          the classPath to set
   */
  public void setClassPath(String classPath) {
    this.classPath = classPath;
  }

  /**
   * @return the activator class name
   */
  public String getActivator() {
    return activator;
  }

  /**
   * @param activator
   *          the activator to set
   */
  public void setActivator(String activator) {
    this.activator = activator;
  }

  /**
   * @return a list of component dependencies.
   */
  public List<ComponentDependency> getDependencies() {
    return componentDependencies;
  }

  /**
   * @param componentDependencies
   *          the componentDependencies to set
   */
  public void setComponentDependencies(
      List<ComponentDependency> componentDependencies) {
    this.componentDependencies = componentDependencies;
  }

  /**
   * @return true if the component is managed.
   */
  public boolean getManaged() {
    return managed;
  }

  /**
   * @param managed
   *          the managed to set
   */
  public void setManaged(boolean managed) {
    this.managed = managed;
  }

  /**
   * @param documentation
   *          the documentation to set
   */
  public void setDocumentation(String documentation) {
    this.documentation = documentation;
  }

  /**
   * @return the documentation
   */
  public String getDocumentation() {
    return documentation;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }
}
