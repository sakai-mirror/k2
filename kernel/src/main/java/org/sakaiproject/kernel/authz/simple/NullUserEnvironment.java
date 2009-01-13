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
package org.sakaiproject.kernel.authz.simple;

import org.sakaiproject.kernel.api.authz.SubjectPermissions;
import org.sakaiproject.kernel.api.authz.SubjectStatement;
import org.sakaiproject.kernel.api.authz.UserSubjects;
import org.sakaiproject.kernel.api.user.User;
import org.sakaiproject.kernel.api.user.UserInfo;
import org.sakaiproject.kernel.api.userenv.UserEnvironment;
import org.sakaiproject.kernel.user.AnonUser;
import org.sakaiproject.kernel.user.NullUserInfo;

/**
 * 
 */
public class NullUserEnvironment implements UserEnvironment {

  private User user = new AnonUser();
  private UserInfo userInfo  = new NullUserInfo(user);

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.userenv.UserEnvironment#hasExpired()
   */
  public boolean hasExpired() {
    return false;
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.userenv.UserEnvironment#matches(org.sakaiproject.kernel.api.authz.SubjectStatement)
   */
  public boolean matches(SubjectStatement subject) {
    // this might not be right, but at the moment I dont know.
    return false;
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.userenv.UserEnvironment#isSuperUser()
   */
  public boolean isSuperUser() {
    return false;
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.Sealable#seal()
   */
  public void seal() {    
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.userenv.UserEnvironment#getUser()
   */
  public User getUser() {
    return user;
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.userenv.UserEnvironment#getSubjects()
   */
  public String[] getSubjects() {
    return new String[0];
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.userenv.UserEnvironment#getUserSubjects()
   */
  public UserSubjects getUserSubjects() {
    return new UserSubjects(){

      public void addSubjectPermissions(SubjectPermissions subjectPermissions) {
      }

      public SubjectPermissions getSubjectPermissions(String subjectToken) {
        return null;
      }

      public boolean hasSubject(String groupToken) {
        return false;
      }
      
    };
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.userenv.UserEnvironment#getLocale()
   */
  public String getLocale() {
    return "en_US";
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.userenv.UserEnvironment#getUserInfo()
   */
  public UserInfo getUserInfo() {
    return userInfo;
   }
}
