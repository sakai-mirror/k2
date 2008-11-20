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
    USERID(), GROUPMEMBER(), ANON(), AUTHENTICATED()
  }
  /**
   * @return
   */
  SubjectType getSubjectType();
  /**
   * @return
   */
  String getSubjectToken();
  /**
   * @return
   */
  String getPermissionToken();

}
