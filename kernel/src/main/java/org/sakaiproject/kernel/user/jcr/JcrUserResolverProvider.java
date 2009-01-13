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
package org.sakaiproject.kernel.user.jcr;

import com.google.inject.Inject;

import org.sakaiproject.kernel.api.memory.Cache;
import org.sakaiproject.kernel.api.memory.CacheManagerService;
import org.sakaiproject.kernel.api.memory.CacheScope;
import org.sakaiproject.kernel.api.user.User;
import org.sakaiproject.kernel.api.user.UserInfo;
import org.sakaiproject.kernel.api.user.UserResolverProvider;
import org.sakaiproject.kernel.api.userenv.UserEnvironment;
import org.sakaiproject.kernel.api.userenv.UserEnvironmentResolverService;
import org.sakaiproject.kernel.model.UserBean;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * 
 */
public class JcrUserResolverProvider implements UserResolverProvider {

  private static final String USERCACHE = "userbean.cache";
  private EntityManager entityManager;
  private Cache<User> cache;
  private UserEnvironmentResolverService userEnvironmentResolverService;

  /**
   * 
   */
  @Inject
  public JcrUserResolverProvider(EntityManager entityManager,
      CacheManagerService cacheManagerService, UserEnvironmentResolverService userEnvironmentResolverService) {
    this.entityManager = entityManager;
    cache = cacheManagerService.getCache(USERCACHE, CacheScope.INSTANCE);
    this.userEnvironmentResolverService = userEnvironmentResolverService;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.user.UserResolverProvider#resolve(java.lang.String)
   */
  public User resolve(String eid) {
    User u = cache.get(eid);
    if (u == null) {
      Query query = entityManager.createNamedQuery(UserBean.FINDBY_EID);
      query.setParameter(UserBean.EID_PARAM, eid);
      List<?> results = query.getResultList();
      if (results.size() > 0) {
        u = (User) results.get(0);
        cache.put(eid, u);
      }
    }
    return u;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.user.UserResolverProvider#resolve(org.sakaiproject.kernel.api.user.User)
   */
  public UserInfo resolve(User user) {
    UserEnvironment ue = userEnvironmentResolverService.resolve(user);
    if ( ue != null ) {
      return ue.getUserInfo();
    }
    return null;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.Provider#getKey()
   */
  public String getKey() {
    return "jcr-user-provider-property";
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.Provider#getPriority()
   */
  public int getPriority() {
    return 0;
  }

}
