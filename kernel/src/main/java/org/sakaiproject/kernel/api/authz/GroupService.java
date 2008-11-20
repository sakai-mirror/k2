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

import java.util.Map;

/**
 * Group service provides information on a users membership and permissions in a
 * group. The users may have a role in the group or simple be an explicit member
 * of a group. The user will also acquire permissions as a result of their
 * status, anon or auth. The group service need to provide intelligent caching
 * of its objects since it will be under heavy demand from the authz service.
 */
public interface GroupService {

  /**
   * <p>
   * This needs to fetch the groups for a user populating the map with the
   * permissions in each group and the roles which grant the permissions. The
   * outer map is keyed by the groupID's. The inner map is keyed by the
   * permission token, and the value contains a list of roles separated by ; that
   * granted the permission.
   * </p>
   * <p>
   * It is strongly advised that implementors of this method use lazy loading of
   * the values of the inner map, and that caching is used to reduce load on the
   * persistent store.
   * </p>
   * <p>
   * Consumers of the map should check for keys using map.containsKey(groupId)
   * before attempting to get the value, as this will avoid unnecessary load
   * attempts.
   * </p>
   *
   * @param userid
   * @return a map og groups.
   */
  Map<String, Map<String, String>> fetchGroups(String userid);

}
