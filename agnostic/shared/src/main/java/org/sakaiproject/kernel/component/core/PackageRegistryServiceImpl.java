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

import org.sakaiproject.kernel.api.ComponentSpecificationException;
import org.sakaiproject.kernel.api.PackageRegistryService;

/**
 * Provides a tree implementation of the package register
 */
public class PackageRegistryServiceImpl implements
    PackageRegistryService {

  private PackageExport root = new PackageExport("root",null);

  
  
  /**
   * {@inheritDoc}
   * 
   * @throws ComponentSpecificationException
   * @see org.sakaiproject.kernel.api.ExportedPackagedRegistryService#addExport(java.lang.String,
   *      java.lang.ClassLoader)
   */
  public void addExport(String stub, ClassLoader classLoader) {
    String[] elements = stub.split("\\.");
    PackageExport p = root;
    for (String element : elements ) {
      PackageExport np = p.get(element);
      if (np == null) {
        np = new PackageExport(element,p.getClassLoader());
        p.put(element, np);
      }
      p = np;
    }
    p.setClassLoader(classLoader);
  }

  /**
   * {@inheritDoc}
   * @return 
   * 
   * @see org.sakaiproject.kernel.api.ExportedPackagedRegistryService#findClassloader(java.lang.String)
   */
  public ClassLoader findClassloader(String packageName) {
    String[] elements = packageName.split("\\.");
    PackageExport p = root;
    for (String element : elements ) {
      PackageExport np = p.get(element);
      if ( np == null ) {
        break;
      }
      p = np;
    }
    return p.getClassLoader();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.ExportedPackagedRegistryService#removeExport(java.lang.String)
   */
  public void removeExport(String stub) {
    String[] elements = stub.split("\\.");
    PackageExport p = root;
    PackageExport container = root;
    String key = null;
    for (String element : elements ) {
      PackageExport np = p.get(element);
      if ( np == null ) {
        break;
      }
      container = p;
      key = element;
      p = np;
      
    }
    if ( key != null ) {
      PackageExport child = container.get(key);
      ClassLoader parentClassloader = container.getClassLoader();
      
      if ( setChildClassLoaders(child,child.getClassLoader(),parentClassloader) == 0) {
        // if there are no other classloaders in in the child tree, remove the child tree alltogether.
        container.remove(key);
      } else {
        child.setClassLoader(parentClassloader);
      }
    }
  }

  /**
   * @param child
   * @param classLoader
   * @param classLoader2
   * @return
   */
  private int setChildClassLoaders(PackageExport child,
      ClassLoader childClassLoader, ClassLoader parentClassloader) {
    int t = 0;
    for ( PackageExport pe : child.values() ) {
      if ( pe.getClassLoader() == childClassLoader ) {
        pe.setClassLoader(parentClassloader);
      } else {
        // found a classloader that is not the child classloader, so increment the counter
        t++;
      }
      t += setChildClassLoaders(pe, childClassLoader, parentClassloader);
    }
    return t;
  }

}
