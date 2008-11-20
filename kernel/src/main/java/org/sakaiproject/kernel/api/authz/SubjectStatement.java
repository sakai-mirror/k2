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
 * A Subject statement defines a subject of a {@link PermissionQuery} this may
 * be membership of a group, a specific user or some other form of environment
 * associated with the user. It is best to think of this as a Token defining,
 * rather than an instance.
 */
public interface SubjectStatement {

  public enum SubjectType {
    /**
     * The subject represents a user, the permission token will be ignored and
     * the subject token will be used for matching.
     */
    USERID(),
    /**
     * The subject represents a group, the subject token and the permission
     * token will be consulted during resolution.
     */
    GROUP(),
    /**
     * Indicates the statement represents all users in all contexts.
     */
    ANON(),
    /**
     * Indicates the statement represents all authenticated users.
     */
    AUTHENTICATED()
  }

  /**
   * @return the type of subject.
   */
  SubjectType getSubjectType();

  /**
   * @return a token that represents the subject.
   */
  String getSubjectToken();

  /**
   * @return a token that represents the permission.
   */
  String getPermissionToken();

}
