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
package org.sakaiproject.kernel.persistence;

import com.google.inject.AbstractModule;
import com.google.inject.CreationException;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import com.google.inject.spi.Message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.persistence.DataSourceService;
import org.sakaiproject.kernel.persistence.dbcp.DataSourceServiceImpl;
import org.sakaiproject.kernel.persistence.eclipselink.EntityManagerProvider;
import org.sakaiproject.kernel.persistence.geronimo.TransactionManagerProvider;
import org.sakaiproject.kernel.util.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.transaction.TransactionManager;

/**
 * Configuration module for persistence bindings.
 */
public class PersistenceModule extends AbstractModule {
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

  private static final Log LOG = LogFactory.getLog(PersistenceModule.class);
  private Properties properties;

  public PersistenceModule() {
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
   * {@inheritDoc}
   * 
   * @see com.google.inject.AbstractModule#configure()
   */
  @Override
  protected void configure() {
    Names.bindProperties(this.binder(), properties);
    // bind the base classes.
    bind(EntityManager.class).toProvider(EntityManagerProvider.class).in(
        Scopes.SINGLETON);
    bind(DataSourceService.class).to(DataSourceServiceImpl.class).in(
        Scopes.SINGLETON);
    bind(TransactionManager.class).toProvider(TransactionManagerProvider.class)
        .in(Scopes.SINGLETON);
  }
}
