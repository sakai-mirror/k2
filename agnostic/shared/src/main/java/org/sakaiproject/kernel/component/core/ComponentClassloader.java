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
import org.sakaiproject.kernel.api.PackageRegistryService;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The Component Classloader is used for components, and will resolve classes
 * exported from other Classloaders into the package registry service. In
 * addition it acts exactly in the same way the URLClassloader operates,
 * resolving to the parent.
 */
public class ComponentClassLoader extends URLClassLoader {

  private static final Log LOG = LogFactory.getLog(ComponentClassLoader.class);
  private PackageRegistryService packageRegistryService;
  private static final ThreadLocal<String> spacing = new ThreadLocal<String>() {
    /**
     * {@inheritDoc}
     * 
     * @see java.lang.ThreadLocal#initialValue()
     */
    @Override
    protected String initialValue() {
      return "1";
    }
  };

  /**
   * @param urls
   * @param parent
   * @param factory
   */
  public ComponentClassLoader(PackageRegistryService packageRegistryService,
      URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
    super(urls, parent, factory);
    this.packageRegistryService = packageRegistryService;

  }

  /**
   * 
   */
  public ComponentClassLoader(PackageRegistryService packageRegistryService,
      URL[] urls) {
    super(urls);
    this.packageRegistryService = packageRegistryService;
  }

  /**
   * 
   */
  public ComponentClassLoader(PackageRegistryService packageRegistryService,
      URL[] urls, ClassLoader parent) {
    super(urls, parent);
    this.packageRegistryService = packageRegistryService;
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
          if (LOG.isDebugEnabled()) {
            LOG.debug("loaded " + c);
          }
        } catch (ClassNotFoundException e) {
          ex = e;
        }
      }
    } else {
      if (LOG.isDebugEnabled()) {
        LOG.info("Not Loading from exports ");
      }
    }

    // then load internally
    if (c == null) {
      try {
        c = this.findClass(name);
      } catch (ClassNotFoundException e) {
        ex = e;
      }
    }

    if (c == null) {
      try {
        c = getParent().loadClass(name);
      } catch (ClassNotFoundException e) {
        ex = e;
      }
    }

    if (LOG.isDebugEnabled()) {
      LOG.debug("Resolved " + name + " as " + c);
    }
    if (c == null)
      throw ex;

    if (resolve)
      resolveClass(c);

    if (LOG.isDebugEnabled()) {
      LOG.debug("loaded " + c + " from " + c.getClassLoader());
    }

    return c;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    String t = spacing.get();
    try {
      String bl = t + " :         ";
      StringBuilder sb = new StringBuilder();
      sb.append(super.toString()).append("\n");
      sb.append(bl).append("Contents :");
      for (URL u : getURLs()) {
        sb.append("\n").append(bl).append(u);
      }
      ClassLoader parent = getParent();
      Map<ClassLoader, ClassLoader> parents = new LinkedHashMap<ClassLoader, ClassLoader>();

      while (parent != null && !parents.containsKey(parent)) {
        parents.put(parent, parent);
        parent = parent.getParent();
      }
      if (t.equals("1")) {
        sb.append("\n").append(bl).append("Classloaders :");
        int i = 1;
        for (ClassLoader p : parents.keySet()) {
          String l = t + "." + i;
          spacing.set(l);
          sb.append("\n").append(l).append(" :").append(p);
          i++;
        }
      }
      return sb.toString();
    } finally {
      spacing.set(t);
    }
  }

}
