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
package org.sakaiproject.kernel.api;

/**
 * Components have dependency and each dependency is specified buy a
 * ComponentSpecifiation that may be shared between multiple dependencies.
 * Sharing is not tracked within the API, but may be tracked in the underlying
 * implementation.
 */
public interface ComponentDependency {

  /**
   * The dependency of the component may be a managed component, meaning that
   * when the component is started, this dependency should be started or have an
   * internal reference count incremented. When the component that has this
   * dependency is stopped, the reference count should be decremented. If the
   * reference count is zero the dependency component should also be stopped.
   * 
   * @return true if the ComponentDepenency is a managed dependency from this
   *         components point of view.
   */
  boolean isManaged();

  /**
   * @return the Name of the component that this component depends on
   */
  String getComponentName();

}
