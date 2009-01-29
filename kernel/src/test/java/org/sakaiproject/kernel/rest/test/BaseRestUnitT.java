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
package org.sakaiproject.kernel.rest.test;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.sakaiproject.kernel.api.KernelManager;
import org.sakaiproject.kernel.api.RegistryService;
import org.sakaiproject.kernel.api.authz.SubjectPermissionService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.api.memory.CacheManagerService;
import org.sakaiproject.kernel.api.memory.CacheScope;
import org.sakaiproject.kernel.api.session.SessionManagerService;
import org.sakaiproject.kernel.api.site.SiteService;
import org.sakaiproject.kernel.api.user.User;
import org.sakaiproject.kernel.api.user.UserResolverService;
import org.sakaiproject.kernel.api.userenv.UserEnvironmentResolverService;
import org.sakaiproject.kernel.webapp.SakaiServletRequest;
import org.sakaiproject.kernel.webapp.test.InternalUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 
 */
public class BaseRestUnitT {
  
  protected RegistryService registryService;
  protected UserEnvironmentResolverService userEnvironmentResolverService;
  protected SessionManagerService sessionManagerService;
  protected SubjectPermissionService subjectPermissionService;
  protected SiteService siteService;
  protected UserResolverService userResolverService;
  protected HttpServletRequest request;
  protected HttpServletResponse response;
  protected HttpSession session;
  protected CacheManagerService cacheManagerService;
  protected JCRNodeFactoryService jcrNodeFactoryService;

  /**
   * Set up the services and mocks.
   */
  public void setupServices() {
    KernelManager km = new KernelManager();
    registryService = km.getService(RegistryService.class);
    userEnvironmentResolverService = km
        .getService(UserEnvironmentResolverService.class);
    sessionManagerService = km.getService(SessionManagerService.class);
    subjectPermissionService = km.getService(SubjectPermissionService.class);
    cacheManagerService = km.getService(CacheManagerService.class);
    jcrNodeFactoryService = km.getService(JCRNodeFactoryService.class);

    siteService = createMock(SiteService.class);
    userResolverService = createMock(UserResolverService.class);
    request = createMock(HttpServletRequest.class);
    response = createMock(HttpServletResponse.class);
    session = createMock(HttpSession.class);
  }

  /**
   * Reset mocks to have another go with the same setup.
   */
  public void resetMocks() {
    replay(request, response, session, siteService, userResolverService);
  }



  public void verifyMocks() {
    verify(request, response, session, siteService, userResolverService);
    cacheManagerService.unbind(CacheScope.REQUEST);
    cacheManagerService.unbind(CacheScope.THREAD);
  }
  
  /**
   * Replay mocks at the end of setup, and bind the request to the thread.
   */
  public void replayMocks() {
    replay(request, response, session, siteService, userResolverService);
    SakaiServletRequest sakaiServletRequest = new SakaiServletRequest(request,
        response, userResolverService, sessionManagerService);
    sessionManagerService.bindRequest(sakaiServletRequest);

  }
  
  /**
   * Setup mocks for any time execution.
   * 
   * @param baos
   * @throws IOException
   * 
   */
  public void setupAnyTimes(String username, String sessionID,
      final ByteArrayOutputStream baos) throws IOException {
    User user = new InternalUser(username); // this is a pre-loaded user.

    expect(request.getAttribute("_no_session")).andReturn(null).anyTimes();
    expect(request.getSession(true)).andReturn(session).anyTimes();
    expect(request.getAttribute("_uuid")).andReturn(null).anyTimes();
    expect(session.getAttribute("_u")).andReturn(user).anyTimes();
    expect(session.getAttribute("_uu")).andReturn(user).anyTimes();

    expect(request.getRequestedSessionId()).andReturn(sessionID).anyTimes();
    expect(session.getId()).andReturn(sessionID).anyTimes();
    expect(session.getAttribute("check-valid")).andReturn(null).anyTimes(); // indicates
    // that
    // the
    // session
    // is
    // in
    // the
    // session
    // map
    // .
    Cookie cookie = new Cookie("SAKAIID", sessionID);
    expect(request.getCookies()).andReturn(new Cookie[] { cookie }).anyTimes();
    response.addCookie((Cookie) anyObject());
    expectLastCall().anyTimes();

    ServletOutputStream out = new ServletOutputStream() {

      @Override
      public void write(int b) throws IOException {
        baos.write(b);
      }

    };

    expect(response.getOutputStream()).andReturn(out).anyTimes();

  }





}
