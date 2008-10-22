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

import org.sakaiproject.kernel.api.session.Session;
import org.sakaiproject.kernel.api.session.SessionManagerService;
import org.sakaiproject.kernel.api.user.User;
import org.sakaiproject.kernel.api.user.UserDirectoryService;
import org.sakaiproject.kernel.api.user.UserNotDefinedException;

/**
 *
 */
public class UserDirectoryServiceImpl implements UserDirectoryService {

  private SessionManagerService sessionManagerService;

  public UserDirectoryServiceImpl(SessionManagerService sessionManagerService) {
    this.sessionManagerService = sessionManagerService;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.sakaiproject.kernel.api.user.UserDirectoryService#getCurrentUser()
   */
  public User getCurrentUser() {
    Session session = sessionManagerService.getCurrentSession();
    if (session != null) {
      String userId = session.getUserId();
      try {
        return getUser(userId);
      } catch (UserNotDefinedException e) {
        return new AnonUser();
      }
    }
    return new AnonUser();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.sakaiproject.kernel.api.user.UserDirectoryService#getUser(java.lang
   * .String)
   */
  public User getUser(String uid) throws UserNotDefinedException {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.sakaiproject.kernel.api.user.UserDirectoryService#authenticate(java
   * .lang.String, java.lang.String)
   */
  public User authenticate(String identifier, String password) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.sakaiproject.kernel.api.user.UserDirectoryService#getUserByEid(java
   * .lang.String)
   */
  public User getUserByEid(String identifier) {
    // TODO Auto-generated method stub
    return null;
  }

}
