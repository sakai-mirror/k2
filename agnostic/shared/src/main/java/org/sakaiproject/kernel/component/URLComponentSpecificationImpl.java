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
package org.sakaiproject.kernel.component;

import org.sakaiproject.kernel.api.ComponentDependency;
import org.sakaiproject.kernel.api.ComponentSpecification;

import java.net.URL;

/**
 *
 */
public class URLComponentSpecificationImpl implements ComponentSpecification {

  /**
   * Construct a URL based component specification based on the supplied string
   * representation of the URL. That URL points to a base location that contains
   * a components.xml defining classpath, component activation, and component
   * dependency.
   * 
   * @param d
   */
  public URLComponentSpecificationImpl(String d) {
    // TODO Auto-generated constructor stub
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.sakaiproject.kernel.api.ComponentSpecification#getClassPathURLs()
   */
  public URL[] getClassPathURLs() {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @seeorg.sakaiproject.kernel.api.ComponentSpecification#
   * getComponentActivatorClassName()
   */
  public String getComponentActivatorClassName() {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.sakaiproject.kernel.api.ComponentSpecification#getDependencies()
   */
  public ComponentDependency[] getDependencies() {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.sakaiproject.kernel.api.ComponentSpecification#isManaged()
   */
  public boolean isManaged() {
    // TODO Auto-generated method stub
    return false;
  }

}
