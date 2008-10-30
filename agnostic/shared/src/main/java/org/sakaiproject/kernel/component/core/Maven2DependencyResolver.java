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

import org.sakaiproject.kernel.api.ClasspathDependency;
import org.sakaiproject.kernel.api.ComponentSpecificationException;
import org.sakaiproject.kernel.api.DependencyResolverService;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * 
 */
public class Maven2DependencyResolver implements DependencyResolverService {

  private File repo;

  /**
   * 
   */
  @Inject
  public Maven2DependencyResolver() {
    // TODO Auto-generated constructor stub
    repo = new File(new File(System.getProperty("user.home"), ".m2"),
        "repository");

  }

  /**
   * {@inheritDoc}
   * 
   * @throws ComponentSpecificationException
   * 
   * @see org.sakaiproject.kernel.api.DependencyResolverService#resolve(java.net.URL[],
   *      org.sakaiproject.kernel.api.ClasspathDependency)
   */
  public URL resolve(URL[] urls, ClasspathDependency classpathDependency)
      throws ComponentSpecificationException {
    File resource = new File(new File(new File(
        classpathDependency.getGroupId().replace('.', File.separatorChar), classpathDependency.getArtifactId()),
        classpathDependency.getVersion()), classpathDependency.getArtifactId()
        + "-" + classpathDependency.getVersion() + "."
        + classpathDependency.getType());
    File localResource = new File(repo, resource.getPath());
    if (!localResource.exists()) {
      throw new ComponentSpecificationException(
          "Resource does not exist locally " + classpathDependency+ " reslved as " + localResource.getAbsolutePath());
    }
    URL u;
    try {
      u = new URL("file://" + localResource.getCanonicalPath());
    } catch (IOException e) {
      throw new ComponentSpecificationException("Unable to create URL for  "
          + classpathDependency, e);
    }
    if (urls != null) {
      for (URL clu : urls) {
        if (u.equals(clu)) {
          return null;
        }
      }
    }
    return u;
  }

}