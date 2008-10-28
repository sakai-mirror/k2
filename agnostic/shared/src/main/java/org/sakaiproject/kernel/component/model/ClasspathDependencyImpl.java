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
package org.sakaiproject.kernel.component.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.sakaiproject.kernel.api.ClasspathDependency;
import org.sakaiproject.kernel.api.DependencyScope;

/**
 * 
 */
@XStreamAlias("dependency")
public class ClasspathDependencyImpl implements ClasspathDependency {
  private String groupId;
  private String artifactId;
  private String version;
  private String classifier;
  private DependencyScope scope = DependencyScope.LOCAL;

  /**
   * @return the groupId
   */
  public String getGroupId() {
    return groupId;
  }

  /**
   * @param groupId
   *          the groupId to set
   */
  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  /**
   * @return the artifactId
   */
  public String getArtifactId() {
    return artifactId;
  }

  /**
   * @param artifactId
   *          the artifactId to set
   */
  public void setArtifactId(String artifactId) {
    this.artifactId = artifactId;
  }

  /**
   * @return the version
   */
  public String getVersion() {
    return version;
  }

  /**
   * @param version
   *          the version to set
   */
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * @return the classifier
   */
  public String getClassifier() {
    return classifier;
  }

  /**
   * @param classifier
   *          the classifier to set
   */
  public void setClassifier(String classifier) {
    this.classifier = classifier;
  }
  
  /**
   * @return the scope
   */
  public DependencyScope getScope() {
    return scope;
  }
  
  /**
   * @param scope the scope to set
   */
  public void setScope(DependencyScope scope) {
    this.scope = scope;
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.ClasspathDependency#getType()
   */
  public String getType() {
    return "jar";
  }
}
