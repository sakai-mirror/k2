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

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * 
 */
@Entity
@Table(name = "group_permission_role")
@NamedQueries(value = { @NamedQuery(
    name = GroupPermissionRole.FINDBY_USERID, 
    query = "select gr from GroupPermissionRole gr wher gr.userId = :userId") })
public class GroupPermissionRole {

  public static final String PARAM_USERID = ":userId";
  public static final String FINDBY_USERID = "GroupPermissionRole.FindByUserId";
  private String groupId;
  private String permissionId;
  private String roleId;
  private String userId;

  /**
   * @return
   */
  public String getGroupId() {
    return groupId;
  }

  /**
   * @return
   */
  public String getPermission() {
    return permissionId;
  }

  /**
   * @return
   */
  public String getRole() {
    return roleId;
  }

  /**
   * @return the userId
   */
  public String getUserId() {
    return userId;
  }

}
