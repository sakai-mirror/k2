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
package org.sakaiproject.kernel.api.user;

import com.google.inject.ImplementedBy;
import com.google.inject.Singleton;

import org.sakaiproject.kernel.user.UserDirectoryServiceImpl;

/**
 *
 */
@ImplementedBy(UserDirectoryServiceImpl.class)
@Singleton
public interface UserDirectoryService {

  /**
   * @return
   */
  User getCurrentUser();

  /**
   * @param uid
   * @throws UserNotDefinedException
   * @return
   */
  User getUser(String uid) throws UserNotDefinedException;

  /**
   * @param identifier
   * @param password
   * @return
   */
  User authenticate(String identifier, String password);

  /**
   * @param identifier
   * @return
   */
  User getUserByEid(String identifier) throws UserNotDefinedException;

}
