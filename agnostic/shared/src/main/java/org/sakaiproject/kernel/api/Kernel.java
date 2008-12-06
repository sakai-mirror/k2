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
 * The kernel.
 */
public interface Kernel {

  /**
   * The name of the Mbean used for kernel.
   */
  String MBEAN_KERNEL = "Sakai:type=Kernel";

  /**
   * @return the service manager to register components with.
   */
  ServiceManager getServiceManager();

  /**
   * @return the component manager that starts components and stops components
   *         managing lifecycle and dependencies.
   */
  ComponentManager getComponentManager();

  /**
   * Get the component parent classloader
   * @return
   */
  ClassLoader getParentComponentClassLoader();
  
  /**
   * Get a service, bound to an API, of the same type as the API
   * 
   * @param <T>
   *          the type of the service
   * @param serviceApi
   *          the class representing the service that is also used for
   *          registration.
   * @return the service or null if none is found.
   */
  <T> T getService(Class<T> serviceApi);

}
