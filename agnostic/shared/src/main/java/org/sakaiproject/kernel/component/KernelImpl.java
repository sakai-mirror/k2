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

import org.sakaiproject.kernel.api.ComponentManager;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.ServiceManager;

/**
 * The kernel implementation.
 */
public class KernelImpl implements Kernel {

  /**
   * The Component manager supporting this kernel.
   */
  private ComponentManager componentManager;
  /**
   * The Service manager containing services for this kernel.
   */
  private ServiceManager serviceManager;

  /**
   * Start the kernel. 
   */
  public void start() {
 
  }

  /**
   * Stop the kernel.
   */
  public void stop() {
 
  }

  /**
   * @return the component manager for the kernel. 
   * @see org.sakaiproject.kernel.api.Kernel#getComponentManager()
   */
  public ComponentManager getComponentManager() {
    return componentManager;
  }

  /**
   * @return the service manager for the kernel.
   * @see org.sakaiproject.kernel.api.Kernel#getServiceManager()
   */
  public ServiceManager getServiceManager() {
    return serviceManager;
  }

  /**
   * @return the parent classloader for components.
   * @see org.sakaiproject.kernel.api.Kernel#getParentComponentClassLoader()
   */
  public ClassLoader getParentComponentClassLoader() {
    return this.getClass().getClassLoader();
  }

  /**
   * This method is not exposed in the api, it sets the component manager for the kernel.
   * @param componentManagerImpl set the component manager.
   */
  protected void setComponentManager(ComponentManager componentManager) {
    this.componentManager = componentManager;
  }

  /**
   * This method is not exposed in the API, it sets the service manager.
   * @param serviceManagerImpl set the service manager
   */
  public void setServiceManager(ServiceManager serviceManager) {
    this.serviceManager = serviceManager;
  }

}
