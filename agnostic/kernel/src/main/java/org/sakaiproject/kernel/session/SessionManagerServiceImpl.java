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
package org.sakaiproject.kernel.session;

import org.sakaiproject.kernel.api.memory.Cache;
import org.sakaiproject.kernel.api.memory.CacheManagerService;
import org.sakaiproject.kernel.api.memory.CacheScope;
import org.sakaiproject.kernel.api.session.Session;
import org.sakaiproject.kernel.api.session.SessionManagerService;

/**
 * 
 */
public class SessionManagerServiceImpl implements SessionManagerService {

  /**
   * the key used for session, (every byte is sacred) 
   */
  private static final String CURRENT_SESSION = "_s";
  private static final String REQUEST_CACHE = "request";
  private static final String SESSION_CACHE = "sessions";
  /**
   * the session cache is a cluster wide cache of session, depending on the setup of the cache manger this may be replicated.
   */
  private Cache<Session> sessionCache;
  private CacheManagerService cacheManagerService;

  public SessionManagerServiceImpl(CacheManagerService cacheManagerService) {
    this.cacheManagerService = cacheManagerService;
    sessionCache = cacheManagerService.getCache(SESSION_CACHE, CacheScope.CLUSTERREPLICATED);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.session.SessionManagerService#getCurrentSession()
   */
  public Session getCurrentSession() {
    Cache<Object> requestScope = cacheManagerService.getCache(REQUEST_CACHE, CacheScope.REQUEST);
    return (Session) requestScope.get(CURRENT_SESSION);
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.session.SessionManagerService#bindSession(java.lang.String)
   */
  public void bindSession(String sessionID) {
    Session session = sessionCache.get(sessionID);
    Cache<Object> requestScope = cacheManagerService.getCache(REQUEST_CACHE, CacheScope.REQUEST);
    requestScope.put(CURRENT_SESSION, session);
  }
  
  

}
