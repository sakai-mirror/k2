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

import org.sakaiproject.kernel.api.ClasspathDependency;
import org.sakaiproject.kernel.api.DependencyResolverService;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.ServiceSpec;

import java.net.URL;
import java.net.URLStreamHandlerFactory;

/**
 * The shared classloader acts a a shared space for utility jars. It will load
 * packages first from exported space and then from shared space. Components may
 * add urls to the shared classpath. It only provides loading of classes and not
 * resources from the exported classes.
 */
public class SharedClassLoader extends ComponentClassloader {

  private DependencyResolverService dependencyResolverService;

  /**
   * @param urls
   * @param parent
   * @param factory
   */
  public SharedClassLoader(Kernel kernel, URL[] urls, ClassLoader parent,
      URLStreamHandlerFactory factory) {
    super(kernel, urls, parent, factory);
    dependencyResolverService = kernel.getServiceManager().getService(
        new ServiceSpec(DependencyResolverService.class));
  }

  /**
   * @param kernel
   * @param urls
   */
  public SharedClassLoader(Kernel kernel, URL[] urls) {
    super(kernel, urls);
  }

  /**
   * @param kernel
   * @param urls
   * @param parent
   */
  public SharedClassLoader(Kernel kernel, URL[] urls, ClassLoader parent) {
    super(kernel, urls, parent);
  }

  /**
   * @param groupId
   * @param artifactId
   * @param versionId
   * @param classifier
   */
  public void addDependency(ClasspathDependency classpathDependency) {
    URL classPathUrl = dependencyResolverService.resolve(getURLs(), classpathDependency);
    if (classPathUrl != null) {
      addURL(classPathUrl);
    }
  }

}
