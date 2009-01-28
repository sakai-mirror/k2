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

import org.sakaiproject.kernel.api.Artifact;
import org.sakaiproject.kernel.api.DependencyScope;

/**
 * 
 */
public class SharedClassloaderArtifact implements Artifact{

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.Artifact#getArtifactId()
   */
  public String getArtifactId() {
    return "kernel-bootstrap-shared-classloader";
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.Artifact#getClassifier()
   */
  public String getClassifier() {
    return "";
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.Artifact#getGroupId()
   */
  public String getGroupId() {
    return "kernel-internal";
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.Artifact#getScope()
   */
  public DependencyScope getScope() {
    return DependencyScope.LOCAL;
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.Artifact#getType()
   */
  public String getType() {
    return "classloader";
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.Artifact#getVersion()
   */
  public String getVersion() {
    return "";
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.Artifact#isManaged()
   */
  public boolean isManaged() {
    return false;
  }
}
