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

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.ComponentActivator;
import org.sakaiproject.kernel.api.ComponentActivatorException;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.PackageRegistryService;
import org.sakaiproject.kernel.api.RequiresStop;
import org.sakaiproject.kernel.api.ServiceSpec;

import java.util.Collection;

/**
 * The activator for the kernel bootstrap component.
 */
public class KernelBootstrapActivator implements ComponentActivator {

  /**
   * A logger
   */
  private static final Log LOG = LogFactory
      .getLog(KernelBootstrapActivator.class);
  private static final Class<?>[] KERNEL_SERVICES = {
    KernelInjectorService.class,
    SharedClassLoaderContainer.class,
    ShutdownService.class,
    PackageRegistryService.class,
    
    
    
    // this should really be the last bootstrap, as it will load the remaining
    ComponentLoaderService.class
  };
  /**
   * The kernel in which this bootstrap was activated.
   */
  private Kernel kernel;

  /**
   * Activate the bootstrap.
   * 
   * @param kernel
   *          the kernel which is activating the bootstrap
   * @throws ComponentActivatorException
   *           if there was a problem activating the component.
   * @see org.sakaiproject.kernel.api.ComponentActivator#activate(org.sakaiproject.kernel.api.Kernel)
   */
  public void activate(Kernel kernel) throws ComponentActivatorException {
    LOG.info("Starting Shared Container");
    this.kernel = kernel;
    Injector injector = Guice.createInjector(new KernelBootstrapModule(kernel));
    for ( Class<?> c : KERNEL_SERVICES ) {
      Object s = injector.getInstance(c);
      LOG.info("Loaded "+c+" as "+s);
    }
  }

  /**
   * Deactivate this component.
   * 
   * @see org.sakaiproject.kernel.api.ComponentActivator#deactivate()
   */
  public void deactivate() {
    Collection<RequiresStop> toStop = kernel.getServiceManager().getServices(
        new ServiceSpec(RequiresStop.class, true));

    for (RequiresStop s : toStop) {
      s.stop();
    }
  }

}
