/*******************************************************************************
 * Copyright 2008 Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.sakaiproject.kernel.test;

import static org.junit.Assert.assertNotNull;

import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.spi.Message;

import org.junit.Test;
import org.sakaiproject.kernel.KernelModule;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.KernelManager;
import org.sakaiproject.kernel.component.KernelLifecycle;
import org.sakaiproject.kernel.persistence.PersistenceModule;
import org.sakaiproject.kernel.util.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;

/**
 *
 */
public class PersistenceModuleTest {
  private static final String DEFAULT_PROPERTIES = "res://kernel-component.properties";
  private static final String LOCAL_PROPERTIES = "SAKAI_KERNEL_COMPONENT_PROPERTIES";
  private static final String SYS_LOCAL_PROPERTIES = "sakai.kernel.component.properties";

  @Test
  public void loadModule() throws Exception {
    Properties props = readSysProps();
    KernelLifecycle kernelLifecycle = new KernelLifecycle();
    kernelLifecycle.start();

    KernelManager kernelManager = new KernelManager();
    Kernel kernel = kernelManager.getKernel();

    Injector injector = Guice.createInjector(new KernelModule(kernel, props),
        new PersistenceModule(kernel));
    DataSource ds = injector.getInstance(DataSource.class);
    EntityManager em = injector.getInstance(EntityManager.class);
    TransactionManager tm = injector.getInstance(TransactionManager.class);
    assertNotNull(ds);
    assertNotNull(em);
    assertNotNull(tm);
    em.close();
  }

  private Properties readSysProps() {
    /**
     * The properties for the kernel
     */
    Properties properties = null;
    InputStream is = null;
    try {
      is = ResourceLoader.openResource(DEFAULT_PROPERTIES, this.getClass()
          .getClassLoader());
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
      }
    } catch (IOException e) {
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
    return properties;
  }
}
