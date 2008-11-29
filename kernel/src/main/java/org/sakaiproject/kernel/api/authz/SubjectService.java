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

import com.google.inject.ImplementedBy;

import org.sakaiproject.kernel.authz.simple.SubjectServiceImpl;

/**
 * Subject service provides information on a users membership and permissions in
 * a Subject, typically a subject is a group but other subjects are possible.
 * The users may have a role in the group or simple be an explicit member of a
 * group. The user will also acquire permissions as a result of their status,
 * anon or auth. The group service need to provide intelligent caching of its
 * objects since it will be under heavy demand from the authz service.
 */
@ImplementedBy(SubjectServiceImpl.class)
public interface SubjectService {

  /**
   * <p>
   * This needs to fetch the Subjects for a user. The
   * outer map is keyed by the groupID's. The inner map is keyed by the
   * permission token, and the value contains a list of roles separated by ;
   * that granted the permission.
   * </p>
   * 
   * @param userid
   * @return Subjects for the user.
   */
  UserSubjects fetchSubjects(String userid);

}
