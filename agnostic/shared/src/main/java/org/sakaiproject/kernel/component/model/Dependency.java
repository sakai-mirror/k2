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

/**
 * A dependency of a component, mapped to the dependency element (contained
 * within dependencies)
 */
@XStreamAlias("dependency")
public class Dependency implements ComponentDependency {

  /**
   * The name of the component dependency
   */
  private String componentName;
  /**
   * Should the dependency be started by the parent component.
   */
  private boolean managed;

  /**
   * create a dependency object.
   */
  public Dependency() {
  }

  /**
   * @return true if the dependency is actively managed.
   * @see org.sakaiproject.kernel.api.ComponentDependency#isManaged()
   */
  public boolean isManaged() {
    return managed;
  }

  /**
   * @param componentName
   *          the componentName to set
   */
  public void setComponentName(String componentName) {
    this.componentName = componentName;
  }

  /**
   * @return the componentName
   */
  public String getComponentName() {
    return componentName;
  }

  /**
   * @param managed
   *          the managed to set
   */
  public void setManaged(boolean managed) {
    this.managed = managed;
  }

}
