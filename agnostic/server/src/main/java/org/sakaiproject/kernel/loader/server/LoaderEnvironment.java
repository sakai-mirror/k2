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
package org.sakaiproject.kernel.loader.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * Provides mechanisms to control the loader with environment settings.
 */
public final class LoaderEnvironment {

  /**
   * A private constructor.
   */
  private LoaderEnvironment() {
  }

  /**
   * The default name of the kernel lifecycle class. This is used where no other method has been
   * used to define this.
   */
  private static final String DEFAULT_LIFECYCLE_CLASS =
    "org.sakaiproject.kernel.component.KernelLifecycle";
  /**
   * The name of the system property that defines the lifecycle class. This is also used in the
   * properties file, loader.properties. System properties override loader.properties.
   */
  private static final String SYS_LIFECYCLE_PROPERTY = "sakai.kernel.lifecycle";
  /**
   * The Name of the OS environment variable that defines the lifecycle class.
   */
  private static final String ENV_LIFECYCLE_PROPERTY = "SAKAI_KERNEL_LIFECYCLE";
  /**
   * The logger.
   */
  private static final Log LOG = LogFactory.getLog(LoaderEnvironment.class);

  /**
   * get the Lifecycle Class. This is defined in loader.properties with the key
   * sakai.kernel.lifecycle, or as a system property of the same name or as an environment variable
   * SAKAI_KERNEL_LIFECYCLE
   *
   * @param <T> the type of the lifecycle class
   * @param classLoader the classloader to use to create the class.
   * @return A new instance of the lifecycle class.
   * @throws ClassNotFoundException if the class can't be found.
   */
  @SuppressWarnings("unchecked")
  public static <T> T getLifecyleClass(final ClassLoader classLoader)
      throws ClassNotFoundException {
    InputStream in = LoaderEnvironment.class.getResourceAsStream("loader.properties");
    Properties p = new Properties();
    try {
      p.load(in);
      in.close();
    } catch (Exception ioex) {
      LOG.warn("No Kernel Lifecycle Properties loaded " + ioex.getMessage());
    }
    String lifecycleClass = p.getProperty(SYS_LIFECYCLE_PROPERTY, DEFAULT_LIFECYCLE_CLASS);
    String sysLifecycleClass = System.getProperty(SYS_LIFECYCLE_PROPERTY);
    String envLifecycleClass = System.getenv().get(ENV_LIFECYCLE_PROPERTY);
    if (envLifecycleClass != null && envLifecycleClass.trim().length() > 0) {
      LOG.info("Environment Override " + envLifecycleClass + " replaces " + lifecycleClass);
      lifecycleClass = envLifecycleClass;
    } else if (sysLifecycleClass != null && sysLifecycleClass.trim().length() > 0) {
      LOG.info("Environment Override " + sysLifecycleClass + " replaces " + lifecycleClass);
      lifecycleClass = sysLifecycleClass;
    }
    LOG.info("Loading " + lifecycleClass + " using " + classLoader);
    T clazz = (T) classLoader.loadClass(lifecycleClass);
    LOG.info("Loaded Ok ");

    return clazz;
  }

}