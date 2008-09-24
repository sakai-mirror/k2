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
public class LoaderEnvironment {

  private static final String DEFAULT_MANAGER_CLASS = "org.sakaiproject.kernel.component.KernelManager";
  private static final String SYS_MANAGER_PROPERTY = "sakai.kernel.manager";
  private static final String ENV_MANAGER_PROPERTY = "SAKAI_KERNEL_MANAGER";
  private static final Log LOG = LogFactory.getLog(LoaderEnvironment.class);

  /**
   * get the Manager Class. This is defined in loader.properties with the key sakai.kernel.manager,
   * or as a system property of the same name or as an environment variable SAKAI_KERNEL_MANAGER
   * 
   * @return the Manager Class
   * @throws ClassNotFoundException
   */
  public static <T> T getManagerClass(ClassLoader classLoader) throws ClassNotFoundException {
    InputStream in = LoaderEnvironment.class.getResourceAsStream("loader.properties");
    Properties p = new Properties();
    try {
      p.load(in);
      in.close();
    } catch (Exception ioex) {
      LOG.warn("No Kernel Manager Properties loaded " + ioex.getMessage());
    }
    String managerClass = p.getProperty(SYS_MANAGER_PROPERTY, DEFAULT_MANAGER_CLASS);
    String sysManagerClass = System.getProperty(SYS_MANAGER_PROPERTY);
    String envManagerClass = System.getenv().get(ENV_MANAGER_PROPERTY);
    if (envManagerClass != null && envManagerClass.trim().length() > 0) {
      LOG.info("Environment Override " + envManagerClass + " replaces " + managerClass);
      managerClass = envManagerClass;
    } else if (sysManagerClass != null && sysManagerClass.trim().length() > 0) {
      LOG.info("Environment Override " + sysManagerClass + " replaces " + managerClass);
      managerClass = sysManagerClass;
    }
    LOG.info("Loading " + managerClass + " using " + classLoader);
    T clazz = (T) classLoader.loadClass(managerClass);
    LOG.info("Loaded Ok ");

    return clazz;
  }

}
