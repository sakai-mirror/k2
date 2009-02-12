/*
 * Licensed to the Sakai Foundation (SF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The SF licenses this file
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
import com.google.inject.name.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.Artifact;
import org.sakaiproject.kernel.api.ArtifactResolverService;
import org.sakaiproject.kernel.api.ClassLoaderService;
import org.sakaiproject.kernel.api.ComponentSpecification;
import org.sakaiproject.kernel.api.ComponentSpecificationException;
import org.sakaiproject.kernel.api.DependencyScope;
import org.sakaiproject.kernel.api.Exporter;
import org.sakaiproject.kernel.api.PackageExport;
import org.sakaiproject.kernel.api.PackageRegistryService;

import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates Classloaders for the framework
 */
public class ClassLoaderServiceImpl implements ClassLoaderService {

  private static final Log LOG = LogFactory
      .getLog(ClassLoaderServiceImpl.class);
  private SharedClassLoader sharedClassLoader;
  private PackageRegistryService packageRegistryService;
  private ArtifactResolverService artifactResolverService;
  private boolean classloaderIsolation;

  /**
   * @param artifactResolverService
   * 
   */
  @Inject
  public ClassLoaderServiceImpl(SharedClassLoader sharedClassLoader,
      PackageRegistryService packageRegistryService,
      ArtifactResolverService artifactResolverService,
      @Named("kernel.classloaderIsolation") boolean classloaderIsolation) {
    this.sharedClassLoader = sharedClassLoader;
    this.packageRegistryService = packageRegistryService;
    this.artifactResolverService = artifactResolverService;
    this.classloaderIsolation = classloaderIsolation;
  }

  /**
   * {@inheritDoc}
   * 
   * @throws ComponentSpecificationException
   * 
   * @see org.sakaiproject.kernel.api.ClassLoaderService#getComponentClassLoader(org.sakaiproject.kernel.api.ComponentSpecification,
   *      java.lang.ClassLoader)
   */
  public ClassLoader getComponentClassLoader(final ComponentSpecification spec)
      throws ComponentSpecificationException {
    final List<URL> urls = new ArrayList<URL>();
    if (spec.getComponentClasspath() != null) {
      urls.add(spec.getComponentClasspath());
    }
    for (Artifact artifact : spec.getDependencies()) {
      if (artifact.getScope() == null
          || DependencyScope.LOCAL.equals(artifact.getScope())) {
        URL[] u = urls.toArray(new URL[0]);
        URL url = artifactResolverService.resolve(u, artifact);
        if (url != null) {
          urls.add(url);
          LOG.warn(spec.getName() + "::Added  " + artifact
              + " to local component classloader ");
        } else {
          LOG.warn(spec.getName() + "::Did not add dependency " + artifact
              + " to local component classloader ");
        }
      }
    }
    ClassLoader cl = null;
    if (!classloaderIsolation || (spec.isKernelBootstrap() && urls.size() == 0)) {
      cl = this.getClass().getClassLoader();
      if (!classloaderIsolation) {
        LOG
            .warn("Component Classloader is not isolated, using "
                + cl
                + " which MUST contain all the kernel dependencies for this test to run ");
      }
    } else {
      cl = AccessController
          .doPrivileged(new PrivilegedAction<ComponentClassLoader>() {

            public ComponentClassLoader run() {
              return new ComponentClassLoader(packageRegistryService, urls
                  .toArray(new URL[0]), sharedClassLoader, spec
                  .getComponentArtifact());
            }

          });
    }
    // add the shared dependencies
    for (Artifact artifact : spec.getDependencies()) {
      if (DependencyScope.SHARE.equals(artifact.getScope())) {
        LOG.info(spec.getName() + "::Adding Shared Artifact " + artifact);
        sharedClassLoader.addDependency(artifact);
      }
    }

    if (cl instanceof Exporter) {
      // export the packages
      for (PackageExport pe : spec.getExports()) {
        LOG.info(spec.getName() + "::Exported [" + pe.getName() + "]");
        packageRegistryService.addExport(pe.getName(), (Exporter) cl);
      }
    }

    return cl;
  }
}
