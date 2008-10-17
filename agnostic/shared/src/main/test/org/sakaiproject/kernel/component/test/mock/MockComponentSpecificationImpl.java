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
package org.sakaiproject.kernel.component.test.mock;

import org.sakaiproject.kernel.api.ComponentDependency;
import org.sakaiproject.kernel.api.ComponentSpecification;

import java.net.URL;

/**
 *
 */
public class MockComponentSpecificationImpl implements ComponentSpecification {

  /* (non-Javadoc)
   * @see org.sakaiproject.kernel.api.ComponentSpecification#getClassPathURLs()
   */
  public URL[] getClassPathURLs() {
    return new URL[0];
  }

  /* (non-Javadoc)
   * @see org.sakaiproject.kernel.api.ComponentSpecification#getComponentActivatorClassName()
   */
  public String getComponentActivatorClassName() {
    return MockComponentActivator.class.getName();
  }

  /* (non-Javadoc)
   * @see org.sakaiproject.kernel.api.ComponentSpecification#getDependencies()
   */
  public ComponentDependency[] getDependencies() {
    return new ComponentDependency[0];
  }

}