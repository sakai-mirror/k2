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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.PackageRegistryService;
import org.sakaiproject.kernel.api.ServiceSpec;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

/**
 * The Component Classloader is used for components, and will resolve classes
 * exported from other Classloaders into the package registry service. In addition it acts exactly in the same
 * way the URLClassloader operates, resolving to the parent.
 */
public class ComponentClassloader extends URLClassLoader {

  private static final Log LOG = LogFactory
      .getLog(ComponentLoaderService.class);
  private PackageRegistryService packageRegistryService;

  /**
   * @param urls
   * @param parent
   * @param factory
   */
  public ComponentClassloader(Kernel kernel, URL[] urls, ClassLoader parent,
      URLStreamHandlerFactory factory) {
    super(urls, parent, factory);
    packageRegistryService = kernel.getServiceManager().getService(
        new ServiceSpec(PackageRegistryService.class));

  }

  /**
   * 
   */
  public ComponentClassloader(Kernel kernel, URL[] urls) {
    super(urls);
    packageRegistryService = kernel.getServiceManager().getService(
        new ServiceSpec(PackageRegistryService.class));
  }

  /**
   * 
   */
  public ComponentClassloader(Kernel kernel, URL[] urls, ClassLoader parent) {
    super(urls, parent);
    packageRegistryService = kernel.getServiceManager().getService(
        new ServiceSpec(PackageRegistryService.class));
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
   */
  @Override
  protected synchronized Class<?> loadClass(String name, boolean resolve)
      throws ClassNotFoundException {
    Class<?> c = findLoadedClass(name);
    ClassNotFoundException ex = null;

    // load from exports first
    if (c == null && packageRegistryService != null) {
      ClassLoader classLoader = packageRegistryService.findClassloader(name);
      if (classLoader != null) {
        try {
          c = classLoader.loadClass(name);
          if (LOG.isDebugEnabled())
            LOG.debug("loaded " + c);
        } catch (ClassNotFoundException e) {
          ex = e;
        }
      }
    }

    //then load internally
    if (c == null) {
      try {
        c = this.findClass(name);
      } catch (ClassNotFoundException e) {
        ex = e;
      }
    }


    LOG.debug("Resolved " + name + " as " + c);
    if (c == null)
      throw ex;

    if (resolve)
      resolveClass(c);

    LOG.debug("loaded " + c + " from " + c.getClassLoader());

    return c;
  }

}
