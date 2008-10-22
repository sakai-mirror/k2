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
package org.sakaiproject.kernel;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.sakaiproject.kernel.api.ComponentActivator;
import org.sakaiproject.kernel.api.ComponentActivatorException;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.RequiresStop;
import org.sakaiproject.kernel.api.ServiceManager;
import org.sakaiproject.kernel.api.ServiceManagerException;
import org.sakaiproject.kernel.api.ServiceSpec;
import org.sakaiproject.kernel.api.jcr.JCRRegistrationService;
import org.sakaiproject.kernel.api.jcr.JCRService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.api.memory.CacheManagerService;
import org.sakaiproject.kernel.api.session.SessionManagerService;
import org.sakaiproject.kernel.api.user.AuthenticationManager;
import org.sakaiproject.kernel.api.user.UserDirectoryService;

/**
 *
 */
public class Activator implements ComponentActivator {

  private static final Class<?>[] SERVICE_CLASSES = { JCRService.class,
      JCRRegistrationService.class, JCRNodeFactoryService.class,
       UserDirectoryService.class,
      AuthenticationManager.class, CacheManagerService.class,
      SessionManagerService.class };
  @SuppressWarnings("unused")
  private Kernel kernel;
  private ServiceManager serviceManager;
  private Injector injector;

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.sakaiproject.kernel.api.ComponentActivator#activate(org.sakaiproject
   * .kernel.api.Kernel)
   */
  public void activate(Kernel kernel) throws ComponentActivatorException {
    // Start the injector for the kernel

    this.kernel = kernel;
    this.serviceManager = kernel.getServiceManager();
    this.injector = Guice.createInjector(new KernelModule(kernel));
    // export the services.
    try {
      for (Class<?> serviceClass : SERVICE_CLASSES) {
        exportService(serviceClass);
      }
    } catch (ServiceManagerException e) {
      throw new ComponentActivatorException(
          "Failed to start Kernel Component ", e);
    }

  }

  /**
   * @param injector
   * @param serviceManager
   * @param serviceClass
   * @throws ServiceManagerException
   */
  private void exportService(Class<?> serviceClass)
      throws ServiceManagerException {
    serviceManager.registerService(new ServiceSpec(serviceClass), injector
        .getInstance(serviceClass));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.sakaiproject.kernel.api.ComponentActivator#deactivate()
   */
  public void deactivate() {
    for (Class<?> serviceClass : SERVICE_CLASSES) {
      retractService(serviceClass);
    }
  }

  /**
   * @param serviceClass
   */
  private void retractService(Class<?> serviceClass) {
    ServiceSpec spec = new ServiceSpec(serviceClass);
    Object service = serviceManager.getService(spec);
    serviceManager.deregisterService(spec);
    if (service instanceof RequiresStop) {
      ((RequiresStop) service).stop();
    }
  }
}
