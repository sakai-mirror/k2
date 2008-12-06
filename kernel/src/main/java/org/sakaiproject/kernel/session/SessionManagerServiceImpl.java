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

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.sakaiproject.kernel.api.memory.Cache;
import org.sakaiproject.kernel.api.memory.CacheManagerService;
import org.sakaiproject.kernel.api.memory.CacheScope;
import org.sakaiproject.kernel.api.session.Session;
import org.sakaiproject.kernel.api.session.SessionManagerService;
import org.sakaiproject.kernel.webapp.SakaiServletRequest;

import javax.servlet.ServletRequest;

/**
 * 
 */
@Singleton
public class SessionManagerServiceImpl implements SessionManagerService {

  /**
   * the key used for session, (every byte is sacred)
   */
  private static final String REQUEST_CACHE = "request";
  private static final String CURRENT_REQUEST = "_r";
  private CacheManagerService cacheManagerService;

  @Inject
  public SessionManagerServiceImpl(CacheManagerService cacheManagerService) {
    this.cacheManagerService = cacheManagerService;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.session.SessionManagerService#getCurrentSession()
   */
  public Session getCurrentSession() {
    Cache<Object> requestScope = cacheManagerService.getCache(REQUEST_CACHE,
        CacheScope.REQUEST);
    SakaiServletRequest request = (SakaiServletRequest) requestScope
        .get(CURRENT_REQUEST);
    if (request == null) {
      throw new RuntimeException(
          "No Request Object has been bound to the request thread\n" +
          "   Please ensure that the Sakai Request Filter is active in web.xml\n" +
          "   or if in a test, perform a SessionManager.bindRequest as part of\n" +
          "   the invocation of the test.");
    }
    return request.getSakaiSession();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.session.SessionManagerService#bindRequest(javax.servlet.ServletRequest)
   */
  public void bindRequest(ServletRequest request) {
    if (!(request instanceof SakaiServletRequest)) {
      throw new RuntimeException(
          "Requests can only be bound by the SakaiRequestFilter ");
    }
    Cache<Object> requestScope = cacheManagerService.getCache(REQUEST_CACHE,
        CacheScope.REQUEST);
    requestScope.put(CURRENT_REQUEST, request);
  }

}
