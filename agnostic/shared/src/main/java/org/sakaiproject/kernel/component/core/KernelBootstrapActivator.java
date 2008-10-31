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
import org.sakaiproject.kernel.api.ClassLoaderService;
import org.sakaiproject.kernel.api.ComponentActivator;
import org.sakaiproject.kernel.api.ComponentActivatorException;
import org.sakaiproject.kernel.api.ComponentLoaderService;
import org.sakaiproject.kernel.api.ComponentSpecificationException;
import org.sakaiproject.kernel.api.DependencyResolverService;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.KernelConfigurationException;
import org.sakaiproject.kernel.api.PackageRegistryService;
import org.sakaiproject.kernel.api.RequiresStop;
import org.sakaiproject.kernel.api.ServiceManager;
import org.sakaiproject.kernel.api.ServiceManagerException;
import org.sakaiproject.kernel.api.ServiceSpec;
import org.sakaiproject.kernel.api.ShutdownService;

import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

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
      KernelInjectorService.class, SharedClassLoaderContainer.class,
      ShutdownService.class, PackageRegistryService.class,
      DependencyResolverService.class, ClassLoaderService.class,
      ComponentLoaderService.class };
  private static final Class<?>[] EXPORTED_KERNEL_SERVICES = {
      SharedClassLoaderContainer.class, ShutdownService.class,
      PackageRegistryService.class, DependencyResolverService.class,
      ClassLoaderService.class, ComponentLoaderService.class };
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
    KernelBootstrapModule kbmodule = new KernelBootstrapModule(kernel);
    Injector injector = Guice.createInjector(kbmodule);
    for (Class<?> c : KERNEL_SERVICES) {
      Object s = injector.getInstance(c);
      if (LOG.isDebugEnabled()) {
        LOG.debug("Loaded " + c + " as " + s);
      }
    }

    ServiceManager serviceManager = kernel.getServiceManager();
    for (Class<?> c : EXPORTED_KERNEL_SERVICES) {
      Object s = injector.getInstance(c);
      try {
        serviceManager.registerService(new ServiceSpec(c), s);
      } catch (ServiceManagerException e) {
        throw new ComponentActivatorException("Failed to register service " + c
            + " cause:" + e.getMessage(), e);
      }
      if (LOG.isDebugEnabled()) {
        LOG.debug("Registerd " + c + " as " + s);
      } else {
        LOG.info("Registerd service " + c);
      }
    }

    // finally invoke the component loader
    ComponentLoaderService loader = kernel.getServiceManager().getService(
        new ServiceSpec(ComponentLoaderService.class));
    Properties p = kbmodule.getProperties();

    try {
      loader.load(p.getProperty("component.locations"), false);
    } catch (IOException e) {
      throw new ComponentActivatorException("Failed to load kernel components "
          + e.getMessage(), e);
    } catch (ComponentSpecificationException e) {
      throw new ComponentActivatorException("Failed to load kernel components "
          + e.getMessage(), e);
    } catch (KernelConfigurationException e) {
      throw new ComponentActivatorException("Failed to load kernel components "
          + e.getMessage(), e);
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
    LogFactory.release(this.getClass().getClassLoader());
  }

}
