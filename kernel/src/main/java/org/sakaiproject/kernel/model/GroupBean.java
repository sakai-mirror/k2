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
package org.sakaiproject.kernel.model;

/**
 * A simple bean for serializing group information into an object structure
 * prior to indexing.
 */
public class GroupBean {

  private String name;
  private String description;
  private RoleBean[] roles = new RoleBean[0];

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description
   *          the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @param roles
   *          the roles to set
   */
  public void setRoles(RoleBean[] roles) {
    this.roles = roles;
  }

  /**
   * @return the roles
   */
  public RoleBean[] getRoles() {
    return roles;
  }

  public String[] getSubjectTokens() {
    if (roles == null) {
      return new String[0];
    } else {
      String[] s = new String[roles.length];
      for (int i = 0; i < roles.length; i++) {
        s[i] = roles[i].getSubjectToken(name);
      }
      return s;
    }
  }

}
