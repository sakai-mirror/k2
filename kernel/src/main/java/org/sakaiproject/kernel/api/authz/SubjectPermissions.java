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
package org.sakaiproject.kernel.api.authz;

/**
 * Subject Permissions represents the permissions that a subject has.
 */
public interface SubjectPermissions {

  /**
   * Add a subject permission with the role that derived it.
   * 
   * @param role
   *          the role performing the grant
   * @param permission
   *          the permission
   */
  void add(String role, String permissionToken);

  /**
   * Does the Subject have this permission token
   * 
   * @param permissionToken
   *          the permission token
   * @return true if the subject does have the permission.
   */
  boolean hasPermission(String permissionToken);

  /**
   * @return the token for this subject.
   */
  String getSubjectToken();

}
