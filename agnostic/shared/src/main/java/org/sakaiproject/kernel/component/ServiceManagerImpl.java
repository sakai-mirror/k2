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

import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.ServiceManager;
import org.sakaiproject.kernel.api.ServiceManagerException;
import org.sakaiproject.kernel.api.ServiceSpec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class ServiceManagerImpl implements ServiceManager {

  private Kernel kernel;
  private Map<ServiceSpec, Object> services = new ConcurrentHashMap<ServiceSpec, Object>();
  
  

  /**
   * @param kernel
   */
  public ServiceManagerImpl(KernelImpl kernel) {
    this.kernel = kernel;
    kernel.setServiceManager(this);
  }

  /**
   * 
   */
  public void start() {
  }

  /**
   * 
   */
  public void stop() {
  }
  
  @SuppressWarnings("unchecked")
  public <T> T getService(ServiceSpec serviceSpec) {
    return (T) services.get(serviceSpec);
  }
  
  public void registerService(ServiceSpec serviceSpec, Object service) throws ServiceManagerException {
    if ( services.containsKey(serviceSpec) ) {
      throw new ServiceManagerException("Can register duplicate services");
    }
    services.put(serviceSpec, service);
  }

  public void deregisterService(ServiceSpec serviceSpec) {
    services.remove(serviceSpec);
  }

  @SuppressWarnings("unchecked")
  public <T> Collection<T> getServices(ServiceSpec serviceSpec) {
    Collection<T> matchedServices = new ArrayList<T>();
    for ( Entry<ServiceSpec, Object> e : services.entrySet()) {
      if ( serviceSpec.matches(e.getKey()) ) {
        matchedServices.add((T)e.getValue());
      }
    }
    return matchedServices;
  }
  
  

}
