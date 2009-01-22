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
import com.google.inject.Scopes;
import com.google.inject.name.Names;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.Artifact;
import org.sakaiproject.kernel.api.ArtifactResolverService;
import org.sakaiproject.kernel.api.ClassLoaderService;
import org.sakaiproject.kernel.api.ComponentLoaderService;
import org.sakaiproject.kernel.api.ComponentManager;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.PackageRegistryService;
import org.sakaiproject.kernel.api.ServiceManager;
import org.sakaiproject.kernel.api.ShutdownService;
import org.sakaiproject.kernel.util.KernelProperties;

import java.util.Properties;

/**
 * A Guice module used to create the bootstrap component. This class configures
 * the kernel bootstrap component, using the classpath resource
 * kernel.properties. The deployer of sakai may override the kernel properties
 * using the System property SAKAI_KERNEL_PROPERTIES to specify a resource (in
 * ResourceLoader form) that contains properties to be overridden.
 */
public class KernelBootstrapModule extends AbstractModule {

  private static final Log LOG = LogFactory.getLog(KernelBootstrapModule.class);

  /**
   * The properties for the kernel
   */
  private final Properties properties;

  /**
   * The kernel which the bootstrap component exists within.
   */
  private final Kernel kernel;

  /**
   * Create a Guice module for the kernel bootstrap.
   *
   * This loads properties from res://kernel.properites and looks for the
   * environment variable SAKAI_KERNEL_PROPERTIES for the location of local
   * overrides
   *
   * @param kernel
   *          the kernel performing the bootstrap.
   */
   public KernelBootstrapModule(Kernel kernel) {
    this.kernel = kernel;
    properties = new KernelProperties().getProperties();
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
    bind(ArtifactResolverService.class).to(Maven2ArtifactResolver.class).in(
        Scopes.SINGLETON);

    bind(Artifact.class).annotatedWith(
        Names.named(SharedClassLoader.SHARED_CLASSLOADER_ARTIFACT)).to(
        SharedClassloaderArtifact.class);
  }

  /**
   * @return the properties
   */
  public Properties getProperties() {
    return properties;
  }
}
