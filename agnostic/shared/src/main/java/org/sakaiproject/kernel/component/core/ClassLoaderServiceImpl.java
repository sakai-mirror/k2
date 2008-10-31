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

import com.google.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.ClassLoaderService;
import org.sakaiproject.kernel.api.Dependency;
import org.sakaiproject.kernel.api.ComponentSpecification;
import org.sakaiproject.kernel.api.ComponentSpecificationException;
import org.sakaiproject.kernel.api.DependencyResolverService;
import org.sakaiproject.kernel.api.DependencyScope;
import org.sakaiproject.kernel.api.PackageExport;
import org.sakaiproject.kernel.api.PackageRegistryService;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates Classloaders for the framework
 */
public class ClassLoaderServiceImpl implements ClassLoaderService {

  private static final Log LOG = LogFactory.getLog(ClassLoaderServiceImpl.class);
  private SharedClassLoader sharedClassLoader;
  private PackageRegistryService packageRegistryService;
  private DependencyResolverService dependencyResolverService;

  /**
   * @param dependencyResolverService
   * 
   */
  @Inject
  public ClassLoaderServiceImpl(SharedClassLoader sharedClassLoader,
      PackageRegistryService packageRegistryService,
      DependencyResolverService dependencyResolverService) {
    this.sharedClassLoader = sharedClassLoader;
    this.packageRegistryService = packageRegistryService;
    this.dependencyResolverService = dependencyResolverService;
    // TODO Auto-generated constructor stub
  }

  /**
   * {@inheritDoc}
   * @throws ComponentSpecificationException 
   * 
   * @see org.sakaiproject.kernel.api.ClassLoaderService#getComponentClassLoader(org.sakaiproject.kernel.api.ComponentSpecification,
   *      java.lang.ClassLoader)
   */
  public ClassLoader getComponentClassLoader(ComponentSpecification spec) throws ComponentSpecificationException {
    List<URL> urls = new ArrayList<URL>();
    if ( spec.getComponentClasspath()  != null ) {
      urls.add(spec.getComponentClasspath());
    }
    for ( Dependency dependency : spec.getDependencies() ) {
      if ( dependency.getScope() == null || DependencyScope.LOCAL.equals(dependency.getScope())) {
        URL[] u = urls.toArray(new URL[0]);
        URL url = dependencyResolverService.resolve(u, dependency);
        if ( url != null ) {
          urls.add(url);
        } else {
          LOG.warn(spec.getName()+"::Did not add dependency "+dependency+" to local component classloader ");
        }
      }
    }
    ClassLoader cl = null;
    if ( spec.isKernelBootstrap() && urls.size() == 0 ) {
      cl = this.getClass().getClassLoader();
    } else {
      cl = new ComponentClassLoader(packageRegistryService,urls.toArray(new URL[0]),sharedClassLoader);
    }
    // add the shared dependencies
    for ( Dependency dependency : spec.getDependencies() ) {
      if ( DependencyScope.SHARE.equals(dependency.getScope())) {
        LOG.info(spec.getName()+"::Adding Shared Dependency "+dependency);
        sharedClassLoader.addDependency(dependency);
      }
    }
    
    // export the packages
    for ( PackageExport pe : spec.getExports() ) {
      LOG.info(spec.getName()+"::Exported "+pe.getName());
      packageRegistryService.addExport(pe.getName(), cl);
    }
    
    return cl;
  }
}
