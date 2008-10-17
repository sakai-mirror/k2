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
 *
 */
public class KernelImpl implements Kernel{

  private ComponentManager componentManager;
  private ServiceManager serviceManager;

  /**
   * 
   */
  public void start() {
    // TODO Auto-generated method stub
    
  }

  /**
   * 
   */
  public void stop() {
    // TODO Auto-generated method stub
    
  }

  /* (non-Javadoc)
   * @see org.sakaiproject.kernel.api.Kernel#getComponentManager()
   */
  public ComponentManager getComponentManager() {
    return componentManager;
  }

  /* (non-Javadoc)
   * @see org.sakaiproject.kernel.api.Kernel#getServiceManager()
   */
  public ServiceManager getServiceManager() {
    return serviceManager;
  }

  /* (non-Javadoc)
   * @see org.sakaiproject.kernel.api.Kernel#getParentComponentClassLoader()
   */
  public ClassLoader getParentComponentClassLoader() {
    return this.getClass().getClassLoader();
  }

  /**
   * @param componentManagerImpl
   */
  protected void setComponentManager(ComponentManager componentManager) {
    this.componentManager = componentManager;
  }

  /**
   * @param serviceManagerImpl
   */
  public void setServiceManager(ServiceManager serviceManager) {
    this.serviceManager = serviceManager;
  }

}
