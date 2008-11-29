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
import org.sakaiproject.kernel.api.authz.SubjectService;
import org.sakaiproject.kernel.api.authz.SubjectStatement;
import org.sakaiproject.kernel.api.authz.UserSubjects;
import org.sakaiproject.kernel.api.session.Session;
import org.sakaiproject.kernel.api.userenv.UserEnvironment;

/**
 * 
 */
public class SimpleUserEnvironment implements UserEnvironment {

  private long expireTime;
  private String userid;
  /**
   * a map of group permissions keyed by groupid, the value containing the permission granted as the key and a list of roles that
   * resulted in that grant.
   */
  private UserSubjects groups;
  private SubjectService subjectService;

  /**
   * @param currentSession
   */
  public SimpleUserEnvironment(Session currentSession, SubjectService groupService, long ttl) {
    expireTime = System.currentTimeMillis() + ttl;
    userid = currentSession.getUserId();
    groups = subjectService.fetchSubjects(userid);    
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.userenv.UserEnvironment#hasExpired()
   */
  public boolean hasExpired() {
    return System.currentTimeMillis() > expireTime;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.userenv.UserEnvironment#matches(org.sakaiproject.kernel.api.authz.SubjectStatement)
   */
  public boolean matches(SubjectStatement subject) {
    switch (subject.getSubjectType()) {
    case GROUP:
      String groupToken = subject.getSubjectToken();
      if ( groups.hasSubject(groupToken)) {
        SubjectPermissions userPermissions = groups.getSubjectPermissions(groupToken);
        return userPermissions.hasPermission(subject.getPermissionToken());
      }
      return groups.hasSubject(subject.getSubjectToken());
    case USERID:
      return userid.equals(subject.getSubjectToken());
    case AUTHENTICATED:
      return ( userid != null && userid.trim().length() > 0 );
    case ANON:
      return true;
    }
    return false;
  }


}
