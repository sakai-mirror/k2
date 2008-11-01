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
package org.sakaiproject.kernel.api;

import java.util.Map;


/**
 * Provides an in memory tree register for classloaders that load exported
 * packages.
 */
public interface PackageRegistryService {

  /**
   * Add a classloader for the package, creating the pathway to the package and
   * setting the classloader at this package to the supplied classloader. If
   * there is already a classloader for this precise package, then it will be
   * replaced. If there is a classloader of a child package, this classloader
   * will take precedence for all packages that do not match that classloader
   * package path, but that classloader will take precedence for any package
   * path that matches that package path.
   * 
   * @param stub
   *          the package stub that identifies this classloader
   * @param classLoader
   *          the classloader
   * @throws ComponentSpecificationException
   */
  void addExport(String stub, ClassExporter classLoader)
      throws ComponentSpecificationException;

  /**
   * Remove an classloader export, and all child exports for a package path.
   * 
   * @param stub
   *          the classloader stub.
   */
  void removeExport(String stub);

  /**
   * Find a classloader for a package
   * 
   * @param packageName
   *          the name of the package to find the classloader for.
   * @return the classloader.
   */
  ClassExporter findClassloader(String packageName);
  
  Map<String, String> getExports();
}
