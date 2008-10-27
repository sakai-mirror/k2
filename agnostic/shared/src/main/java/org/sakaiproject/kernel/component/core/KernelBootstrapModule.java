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

import com.google.inject.AbstractModule;
import com.google.inject.CreationException;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import com.google.inject.spi.Message;

import org.sakaiproject.kernel.api.ClassLoaderService;
import org.sakaiproject.kernel.api.ComponentLoaderService;
import org.sakaiproject.kernel.api.ComponentManager;
import org.sakaiproject.kernel.api.DependencyResolverService;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.PackageRegistryService;
import org.sakaiproject.kernel.api.ServiceManager;
import org.sakaiproject.kernel.api.ShutdownService;
import org.sakaiproject.kernel.util.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

/**
 * A Guice module used to create the bootstrap component.
 */
public class KernelBootstrapModule extends AbstractModule {

  /**
   * Location of the kernel properties.
   */
  private final static String DEFAULT_PROPERTIES = "res://kernel.properties";

  /**
   * The properties for the kernel
   */
  private final Properties properties;

  /**
   * The kernel which the bootstrap component exists within.
   */
  private Kernel kernel;

  /**
   * Create a Guice module for the kernel bootstrap.
   * 
   * @param kernel
   *          the kernel performing the bootstrap.
   */
  public KernelBootstrapModule(Kernel kernel) {
    this.kernel = kernel;
    InputStream is = null;
    try {
      is = ResourceLoader.openResource(DEFAULT_PROPERTIES);
      properties = new Properties();
      properties.load(is);
    } catch (IOException e) {
      throw new CreationException(Arrays.asList(new Message(
          "Unable to load properties: " + DEFAULT_PROPERTIES)));
    } finally {
      try {
        if (is != null) {
          is.close();
        }
      } catch (IOException e) {
        // dont care about this.
      }
    }
  }

  /**
   * Create the bootstrap module with a kernel and supplied properties.
   * 
   * @param kernel
   * @param properties
   */
  public KernelBootstrapModule(Kernel kernel, Properties properties) {
    this.properties = properties;
    this.kernel = kernel;
  }

  /**
   * Configure the guice bindings.
   * 
   * @see com.google.inject.AbstractModule#configure()
   */
  @Override
  protected void configure() {
    Names.bindProperties(this.binder(), properties);

    bind(Kernel.class).toInstance(kernel);
    bind(ServiceManager.class).toInstance(kernel.getServiceManager());
    bind(ComponentManager.class).toInstance(kernel.getComponentManager());

    bind(KernelInjectorService.class).asEagerSingleton();
    bind(SharedClassLoaderContainer.class).asEagerSingleton();

    bind(ComponentLoaderService.class).to(ComponentLoaderServiceImpl.class).in(
        Scopes.SINGLETON);
    bind(ShutdownService.class).to(ShutdownServiceImpl.class).in(
        Scopes.SINGLETON);
    bind(PackageRegistryService.class).to(PackageRegistryServiceImpl.class).in(
        Scopes.SINGLETON);
    bind(ClassLoaderService.class).to(ClassLoaderServiceImpl.class).in(
        Scopes.SINGLETON);
    bind(DependencyResolverService.class).to(Maven2DependencyResolver.class)
        .in(Scopes.SINGLETON);
  }

  
  /**
   * @return the properties
   */
  public Properties getProperties() {
    return properties;
  }
}
