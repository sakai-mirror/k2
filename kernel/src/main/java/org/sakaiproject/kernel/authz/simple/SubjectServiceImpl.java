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
import org.sakaiproject.kernel.api.authz.UserSubjects;
import org.sakaiproject.kernel.api.memory.Cache;
import org.sakaiproject.kernel.api.memory.CacheManagerService;
import org.sakaiproject.kernel.api.memory.CacheScope;
import org.sakaiproject.kernel.model.GroupPermissionRole;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * 
 */
public class SubjectServiceImpl implements SubjectService{

  private Cache<UserSubjects> userSubjectCache;
  private EntityManager entityManager;
  /**
   * 
   */
  public SubjectServiceImpl(CacheManagerService cacheManagerService, EntityManager entityManager ) {
    userSubjectCache = cacheManagerService.getCache("groupUserCache", CacheScope.CLUSTERINVALIDATED);
    this.entityManager = entityManager;
  }
  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.authz.GroupService#fetchGroups(java.lang.String)
   */
  @SuppressWarnings("unchecked")
  public UserSubjects  fetchSubjects(String userid) {
    UserSubjects  userGroups = userSubjectCache.get(userid);
    if ( userGroups == null ) {
      userGroups = new UserSubjectImpl();
      
      Query q = entityManager.createNamedQuery(GroupPermissionRole.FINDBY_USERID);
      q.setParameter(GroupPermissionRole.PARAM_USERID, userid);
      
      List<GroupPermissionRole> groups =  q.getResultList();
      for(GroupPermissionRole g : groups ) {
        SubjectPermissions groupPermissions = userGroups.getSubjectPermissions(g.getGroupId());
        if ( groupPermissions == null ) {
          groupPermissions = new SubjectPermissionsImpl(g.getGroupId());
          userGroups.addSubjectPermissions(groupPermissions);
        }
        groupPermissions.add(g.getRole(),g.getPermission());
      }
    }    
    return userGroups;
  }

}
