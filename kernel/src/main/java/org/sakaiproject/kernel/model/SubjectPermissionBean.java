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

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * 
 */
@Entity
@Table(name = "subject_permission")
@NamedQueries(value = { @NamedQuery(name = SubjectPermissionBean.FINDBY_SUBJECT, query = "select s from SubjectPermissionBean s where s.subjectToken = :subjectToken") })
public class SubjectPermissionBean {
  public static final String PARAM_SUBJECT = ":subjectToken";
  public static final String FINDBY_SUBJECT = "SubjectPermission.FindBySubjectToken";

  @SuppressWarnings("unused")
  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "oid")
  private long objectId;

  @Column(name = "subjectToken")
  private String subjectToken;

  @Column(name = "permissionToken")
  private String permissionToken;

  @Column(name = "role")
  private String role;

  /**
   * @return
   */
  public String getRole() {
    return role;
  }

  /**
   * @return
   */
  public String PermissionToken() {
    return permissionToken;
  }

  /**
   * @return the subjectToken
   */
  public String getSubjectToken() {
    return subjectToken;
  }
}
