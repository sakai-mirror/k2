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

import com.google.inject.AbstractModule;
import com.google.inject.CreationException;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.spi.Message;

import net.sf.json.JsonConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.ComponentManager;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.ServiceManager;
import org.sakaiproject.kernel.api.ShutdownService;
import org.sakaiproject.kernel.api.authz.ReferenceResolverService;
import org.sakaiproject.kernel.api.jcr.EventRegistration;
import org.sakaiproject.kernel.api.jcr.JCRService;
import org.sakaiproject.kernel.api.persistence.DataSourceService;
import org.sakaiproject.kernel.api.serialization.BeanConverter;
import org.sakaiproject.kernel.api.userenv.UserEnvironment;
import org.sakaiproject.kernel.authz.simple.JcrReferenceResolverService;
import org.sakaiproject.kernel.authz.simple.NullUserEnvironment;
import org.sakaiproject.kernel.authz.simple.PathReferenceResolverService;
import org.sakaiproject.kernel.component.core.guice.ServiceProvider;
import org.sakaiproject.kernel.initialization.InitializationActionProvider;
import org.sakaiproject.kernel.internal.api.InitializationAction;
import org.sakaiproject.kernel.jcr.api.internal.StartupAction;
import org.sakaiproject.kernel.jcr.jackrabbit.sakai.SakaiJCRCredentials;
import org.sakaiproject.kernel.jcr.jackrabbit.sakai.StartupActionProvider;
import org.sakaiproject.kernel.persistence.dbcp.DataSourceServiceImpl;
import org.sakaiproject.kernel.persistence.eclipselink.EntityManagerProvider;
import org.sakaiproject.kernel.persistence.geronimo.TransactionManagerProvider;
import org.sakaiproject.kernel.serialization.json.BeanJsonLibConfig;
import org.sakaiproject.kernel.serialization.json.BeanJsonLibConverter;
import org.sakaiproject.kernel.util.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import javax.jcr.Credentials;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;

/**
 * A Guice module used to create the kernel component.
 */
public class KernelModule extends AbstractModule {

  /**
   * Location of the kernel properties.
   */
  private final static String DEFAULT_PROPERTIES = "res://kernel-component.properties";

  /**
   * the environment variable that contains overrides to kernel properties
   */
  public static final String LOCAL_PROPERTIES = "SAKAI_KERNEL_COMPONENT_PROPERTIES";

  /**
   * The System property name that contains overrides to the kernel properties
   * resource
   */
  public static final String SYS_LOCAL_PROPERTIES = "sakai.kernel.component.properties";

  private static final Log LOG = LogFactory.getLog(KernelModule.class);

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
  public KernelModule(Kernel kernel) {
    this.kernel = kernel;
    InputStream is = null;
    try {
      is = ResourceLoader.openResource(DEFAULT_PROPERTIES, this.getClass()
          .getClassLoader());
      properties = new Properties();
      properties.load(is);
      LOG.info("Loaded " + properties.size() + " properties from "
          + DEFAULT_PROPERTIES);
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
    // load local properties if specified as a system property
    String localPropertiesLocation = System.getenv(LOCAL_PROPERTIES);
    String sysLocalPropertiesLocation = System
        .getProperty(SYS_LOCAL_PROPERTIES);
    if (sysLocalPropertiesLocation != null) {
      localPropertiesLocation = sysLocalPropertiesLocation;
    }
    try {
      if (localPropertiesLocation != null
          && localPropertiesLocation.trim().length() > 0) {
        is = ResourceLoader.openResource(localPropertiesLocation, this
            .getClass().getClassLoader());
        Properties localProperties = new Properties();
        localProperties.load(is);
        for (Entry<Object, Object> o : localProperties.entrySet()) {
          String k = o.getKey().toString();
          if (k.startsWith("+")) {
            String p = properties.getProperty(k.substring(1));
            if (p != null) {
              properties.put(k.substring(1), p + o.getValue());
            } else {
              properties.put(o.getKey(), o.getValue());
            }
          } else {
            properties.put(o.getKey(), o.getValue());
          }
        }
        LOG.info("Loaded " + localProperties.size() + " properties from "
            + localPropertiesLocation);
      } else {
        LOG.info("No Local Properties Override, set system property "
            + LOCAL_PROPERTIES
            + " to a resource location to override kernel properties");
      }
    } catch (IOException e) {
      LOG.info("Failed to startup ", e);
      throw new CreationException(Arrays.asList(new Message(
          "Unable to load properties: " + localPropertiesLocation)));
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
  public KernelModule(Kernel kernel, Properties properties) {
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
    ServiceManager serviceManager = kernel.getServiceManager();
    bind(Kernel.class).toInstance(kernel);
    bind(ServiceManager.class).toInstance(serviceManager);
    bind(ComponentManager.class).toInstance(kernel.getComponentManager());

    bind(ShutdownService.class).toProvider(
        new ServiceProvider<ShutdownService>(serviceManager,
            ShutdownService.class));

    // JCR setup
    TypeLiteral<List<StartupAction>> startupActionType = new TypeLiteral<List<StartupAction>>() {
    };
    bind(startupActionType).toProvider(StartupActionProvider.class);

    bind(Credentials.class).annotatedWith(
        Names.named(JCRService.NAME_CREDENTIALS)).to(SakaiJCRCredentials.class);

    // Kernel initialization
    TypeLiteral<List<InitializationAction>> initializationActionType = new TypeLiteral<List<InitializationAction>>() {
    };
    bind(initializationActionType).toProvider(
        InitializationActionProvider.class);

    TypeLiteral<Map<String, ReferenceResolverService>> resolverMap = new TypeLiteral<Map<String, ReferenceResolverService>>() {
    };
    bind(resolverMap).toProvider(ReferenceResolverServiceProvider.class);

    bind(ReferenceResolverService.class).annotatedWith(
        Names.named(PathReferenceResolverService.DEFAULT_RESOLVER)).to(
        JcrReferenceResolverService.class);

    bind(BeanConverter.class).annotatedWith(
        Names.named(BeanConverter.REPOSITORY_BEANCONVETER)).to(
        BeanJsonLibConverter.class).in(Scopes.SINGLETON);

    // config for the bean converter
    bind(Map.class).to(HashMap.class);
    bind(List.class).to(ArrayList.class);
    bind(Map[].class).to(HashMap[].class);
    bind(JsonConfig.class).annotatedWith(Names.named("SakaiKernelJsonConfig"))
        .to(BeanJsonLibConfig.class);
    
    bind(UserEnvironment.class).annotatedWith(
        Names.named(UserEnvironment.NULLUSERENV)).to(NullUserEnvironment.class)
        .in(Scopes.SINGLETON);

    TypeLiteral<List<EventRegistration>> eventList = new TypeLiteral<List<EventRegistration>>() {
    };
    bind(eventList).toProvider(EventRegistrationProvider.class);

    
    // bind in JPA
    bind(EntityManager.class).toProvider(EntityManagerProvider.class).in(
        Scopes.SINGLETON);
    bind(DataSourceService.class).to(DataSourceServiceImpl.class).in(
        Scopes.SINGLETON);
    bind(DataSource.class).toProvider(DataSourceServiceImpl.class).in(
        Scopes.SINGLETON);
    bind(TransactionManager.class).toProvider(TransactionManagerProvider.class)
        .in(Scopes.SINGLETON);

  }
}
