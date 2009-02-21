/*
 * Licensed to the Sakai Foundation (SF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The SF licenses this file
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

package org.sakaiproject.kernel.rest;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.KernelManager;
import org.sakaiproject.kernel.api.RegistryService;
import org.sakaiproject.kernel.api.ServiceManager;
import org.sakaiproject.kernel.api.serialization.BeanConverter;
import org.sakaiproject.kernel.api.session.SessionManagerService;
import org.sakaiproject.kernel.api.social.FriendsResolverService;
import org.sakaiproject.kernel.api.user.ProfileResolverService;
import org.sakaiproject.kernel.api.user.UserFactoryService;
import org.sakaiproject.kernel.api.userenv.UserEnvironmentResolverService;
import org.sakaiproject.kernel.component.core.guice.ServiceProvider;
import org.sakaiproject.kernel.util.PropertiesLoader;
import org.sakaiproject.kernel.webapp.Initialisable;

import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;

/**
 * A Guice module used to create the rest component.
 */
public class RestModule extends AbstractModule {

  /**
   * Location of the rest properties.
   */
  public static final String DEFAULT_PROPERTIES = "res://kernel-rest.properties";
  private Properties properties;
  private Kernel kernel;

  /**
   * Create the bootstrap module with a kernel and supplied properties.
   * 
   * @param kernel
   * @param properties
   */
  public RestModule() {

    properties = PropertiesLoader.load(this.getClass().getClassLoader(),
        DEFAULT_PROPERTIES);
  }

  /**
   * Configure the guice bindings.
   * 
   * @see com.google.inject.AbstractModule#configure()
   */
  @Override
  protected void configure() {
    Names.bindProperties(this.binder(), properties);

    // get the kernel
    KernelManager kernelManager = new KernelManager();
    kernel = kernelManager.getKernel();

    // get the service manager
    ServiceManager serviceManager = kernel.getServiceManager();
    bind(Kernel.class).toInstance(kernel);
    bind(ServiceManager.class).toInstance(serviceManager);

    // make some services available
    bind(RegistryService.class).toProvider(
        new ServiceProvider<RegistryService>(serviceManager,
            RegistryService.class));
    bind(SessionManagerService.class).toProvider(
        new ServiceProvider<SessionManagerService>(serviceManager,
            SessionManagerService.class));
    bind(UserEnvironmentResolverService.class).toProvider(
        new ServiceProvider<UserEnvironmentResolverService>(serviceManager,
            UserEnvironmentResolverService.class));
    bind(ProfileResolverService.class).toProvider(
        new ServiceProvider<ProfileResolverService>(serviceManager,
            ProfileResolverService.class));
    bind(EntityManager.class)
        .toProvider(
            new ServiceProvider<EntityManager>(serviceManager,
                EntityManager.class));
    bind(FriendsResolverService.class).toProvider(
        new ServiceProvider<FriendsResolverService>(serviceManager,
            FriendsResolverService.class));
    bind(UserFactoryService.class).toProvider(
        new ServiceProvider<UserFactoryService>(serviceManager,
            UserFactoryService.class));
    bind(BeanConverter.class)
        .toProvider(
            new ServiceProvider<BeanConverter>(serviceManager,
                BeanConverter.class));

    // activate all the services
    TypeLiteral<List<Initialisable>> initType = new TypeLiteral<List<Initialisable>>() {
    };
    bind(initType).toProvider(RestServiceListProvider.class).asEagerSingleton();

  }
}