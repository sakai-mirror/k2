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

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.sakaiproject.kernel.api.authz.SubjectService;
import org.sakaiproject.kernel.api.memory.Cache;
import org.sakaiproject.kernel.api.memory.CacheManagerService;
import org.sakaiproject.kernel.api.memory.CacheScope;
import org.sakaiproject.kernel.api.session.Session;
import org.sakaiproject.kernel.api.userenv.UserEnvironment;
import org.sakaiproject.kernel.api.userenv.UserEnvironmentResolverService;

/**
 * 
 */
public class SimpleUserEnvironmentResolverService implements
    UserEnvironmentResolverService {

  private CacheManagerService cacheManagerService;
  private SubjectService subjectService;
  private long ttl;

  /**
   * @param groupService
   * 
   */
  @Inject
  public SimpleUserEnvironmentResolverService(
      CacheManagerService cacheManagerService, SubjectService subjectService,
      @Named(UserEnvironmentResolverService.TTL) long ttl) {
    this.cacheManagerService = cacheManagerService;
    this.subjectService = subjectService;
    this.ttl = ttl;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.userenv.UserEnvironmentResolverService#resolve(org.sakaiproject.kernel.api.session.Session)
   */
  public UserEnvironment resolve(Session currentSession) {
    Cache<UserEnvironment> cache = cacheManagerService.getCache("userenv",
        CacheScope.INSTANCE);
    if (cache.containsKey(currentSession.getId())) {
      UserEnvironment ue = cache.get(currentSession.getId());
      if (ue != null && !ue.hasExpired()) {
        return ue;
      }

    }
    SimpleUserEnvironment sue = new SimpleUserEnvironment(currentSession,
        subjectService, ttl);
    cache.put(currentSession.getId(), sue);
    return sue;
  }

}
