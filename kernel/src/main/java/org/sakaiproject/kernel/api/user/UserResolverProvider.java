/*
 * Licensed to the Sakai Foundation (SF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The SF licenses this file
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
package org.sakaiproject.kernel.api.user;

import org.sakaiproject.kernel.api.Provider;

/**
 * A provider of resolution services.
 */
public interface UserResolverProvider extends Provider<String> {

  /**
   * @param eid
   *          the Extenal user ID
   * @return a User object, if no resolution is possible, return null.
   */
  User resolve(String eid);

  /**
   * resolve the user info from the user object.
   *
   * @param user
   * @return
   */
  UserInfo resolve(User user);

  /**
   * Resolve a UUID into a user object.
   * @param uuid the Unique User ID
   * @return a User Object
   */
  User resolveWithUUID(String uuid);
}
