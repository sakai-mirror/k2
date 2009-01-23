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
package org.sakaiproject.kernel.user;

import org.sakaiproject.kernel.api.user.User;

/**
 * An internal interface used for creating users.
 */
public interface UserFactoryService {

  /**
   * The Name of the userenv file in the system.
   */
  public static final String USERENV = "userenv";

  /**
   * Generate a new user, based on the external ID.
   * 
   * @param externalId
   * @return A new user with the required external ID.
   */
  User createNewUser(String externalId);

  /**
   * Generate the user Env patch based on the uuid
   * 
   * @param uuid
   *          the Unique User ID
   * @return a path to the user environment file
   */
  String getUserEnvPath(String uuid);

  /**
   * generate a template user env template, based on the userType
   * 
   * @param userType
   *          the type of user
   * @return a path into jcr where the user env can be found
   */
  String getUserEnvTemplate(String userType);

  /**
   * Get the base bath of the user environment space for the user based on the
   * UUID.
   * 
   * @param uuid
   *          the uuid of the user
   * @return the base path of the user environment storage space.
   */
  String getUserEnvironmentBasePath(String uuid);

}