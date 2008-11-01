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
package org.sakaiproject.componentsample;

import org.sakaiproject.componentsample.api.HelloWorldService;
import org.sakaiproject.componentsample.core.HelloWorldServiceImpl;
import org.sakaiproject.componentsample.core.InternalDateServiceImpl;
import org.sakaiproject.kernel.api.ComponentActivator;
import org.sakaiproject.kernel.api.ComponentActivatorException;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.ServiceManager;
import org.sakaiproject.kernel.api.ServiceManagerException;
import org.sakaiproject.kernel.api.ServiceSpec;

/**
 * This class brings the component up and down on demand
 */
public class Activator implements ComponentActivator {

  /**
   * We might need to know which kernel this activator is attached to, its
   * possible to have more than one in a JVM
   */
  private Kernel kernel;

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.ComponentActivator#activate(org.sakaiproject.kernel.api.Kernel)
   */
  public void activate(Kernel kernel) throws ComponentActivatorException {

    this.kernel = kernel;
    // here I want to create my services and register them
    // I could use Guice or Spring to do this, but I am going to do manual IoC
    // to keep it really simple
    InternalDateServiceImpl internalDateService = new InternalDateServiceImpl();
    HelloWorldService helloWorldService = new HelloWorldServiceImpl(
        internalDateService);

    // thats it. my service is ready to go, so lets register it
    // get the service manager
    ServiceManager serviceManager = kernel.getServiceManager();

    // create a ServiceSpecification for the class I want to register,
    // the class here MUST be a class that was exported (see component.xml)
    // otherwise
    // nothing else will be able to see it. The service manager might enforce
    // this if I get
    // arround to it.
    ServiceSpec serviceSpec = new ServiceSpec(HelloWorldService.class);

    // register the service
    try {
      serviceManager.registerService(serviceSpec, helloWorldService);
    } catch (ServiceManagerException e) {
      // oops something happened, re-throw as an activation issue
      throw new ComponentActivatorException("Failed to register service ", e);
    }

  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.ComponentActivator#deactivate()
   */
  public void deactivate() {
    // we need to remove the service (this is the short way of doing the above)
    kernel.getServiceManager().deregisterService(new ServiceSpec(HelloWorldService.class));
  }

}