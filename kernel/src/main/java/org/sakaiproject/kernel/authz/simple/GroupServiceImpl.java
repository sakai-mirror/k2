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

import org.sakaiproject.kernel.api.authz.GroupService;
import org.sakaiproject.kernel.api.memory.Cache;
import org.sakaiproject.kernel.api.memory.CacheManagerService;
import org.sakaiproject.kernel.api.memory.CacheScope;
import org.sakaiproject.kernel.model.GroupPermissionRole;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * 
 */
public class GroupServiceImpl implements GroupService{

  private Cache<Map<String, Map<String,String>>> userGroupCache;
  private EntityManager entityManager;
  /**
   * 
   */
  public GroupServiceImpl(CacheManagerService cacheManagerService, EntityManager entityManager ) {
    userGroupCache = cacheManagerService.getCache("groupUserCache", CacheScope.CLUSTERINVALIDATED);
    this.entityManager = entityManager;
  }
  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.authz.GroupService#fetchGroups(java.lang.String)
   */
  @SuppressWarnings("unchecked")
  public Map<String, Map<String, String>> fetchGroups(String userid) {
    Map<String, Map<String,String>>  userGroups = userGroupCache.get(userid);
    if ( userGroups == null ) {
      userGroups = new HashMap<String, Map<String,String>>();
      
      Query q = entityManager.createNamedQuery(GroupPermissionRole.FINDBY_USERID);
      q.setParameter(GroupPermissionRole.PARAM_USERID, userid);
      
      List<GroupPermissionRole> groups =  q.getResultList();
      for(GroupPermissionRole g : groups ) {
        Map<String, String> group = userGroups.get(g.getGroupId());
        if ( group == null ) {
          group = new HashMap<String, String>();
          userGroups.put(g.getGroupId(), group);
        }
        String roles = group.get(g.getPermission());
        if ( roles == null ) {
          roles = g.getRole();
        } else {
          roles = roles+ ";" +g.getRole();    
        }
        group.put(g.getPermission(), roles);
      }
    }
    
    
    // TODO Auto-generated method stub
    return null;
  }

}
