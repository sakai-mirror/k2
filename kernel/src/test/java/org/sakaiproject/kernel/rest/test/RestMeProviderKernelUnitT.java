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
import static org.junit.Assert.assertTrue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sakaiproject.kernel.api.ComponentActivatorException;
import org.sakaiproject.kernel.api.KernelManager;
import org.sakaiproject.kernel.api.Registry;
import org.sakaiproject.kernel.api.RegistryService;
import org.sakaiproject.kernel.api.authz.AuthzResolverService;
import org.sakaiproject.kernel.api.jcr.JCRService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
import org.sakaiproject.kernel.api.memory.CacheManagerService;
import org.sakaiproject.kernel.api.memory.CacheScope;
import org.sakaiproject.kernel.api.rest.RestProvider;
import org.sakaiproject.kernel.api.session.SessionManagerService;
import org.sakaiproject.kernel.api.user.UserResolverService;
import org.sakaiproject.kernel.jcr.jackrabbit.sakai.SakaiJCRCredentials;
import org.sakaiproject.kernel.rest.RestMeProvider;
import org.sakaiproject.kernel.test.AuthZServiceKernelUnitT;
import org.sakaiproject.kernel.test.KernelIntegrationBase;
import org.sakaiproject.kernel.util.PathUtils;
import org.sakaiproject.kernel.util.ResourceLoader;
import org.sakaiproject.kernel.webapp.SakaiServletRequest;
import org.sakaiproject.kernel.webapp.test.InternalUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 
 */
public class RestMeProviderKernelUnitT extends KernelIntegrationBase {

  private static final Log LOG = LogFactory
      .getLog(AuthZServiceKernelUnitT.class);
  private static final String[] USERS = { "admin", "ib236" };
  private static final String TEST_USERENV = "res://org/sakaiproject/kernel/test/sampleuserenv/";
  private static boolean shutdown;

  @BeforeClass
  public static void beforeThisClass() throws ComponentActivatorException {
    shutdown = KernelIntegrationBase.beforeClass();
  }

  @AfterClass
  public static void afterThisClass() {
    KernelIntegrationBase.afterClass(shutdown);
  }

  @Test
  public void testAnonGet() throws ServletException, IOException {
    KernelManager km = new KernelManager();
    SessionManagerService sessionManagerService = km
        .getService(SessionManagerService.class);
    CacheManagerService cacheManagerService = km
        .getService(CacheManagerService.class);
    UserResolverService userResolverService = km
        .getService(UserResolverService.class);

    RegistryService registryService = km.getService(RegistryService.class);
    Registry<String, RestProvider> registry = registryService
        .getRegistry(RestProvider.REST_REGISTRY);
    RestMeProvider rmp = (RestMeProvider) registry.getMap().get("me");

    HttpServletRequest request = createMock(HttpServletRequest.class);
    HttpServletResponse response = createMock(HttpServletResponse.class);
    HttpSession session = createMock(HttpSession.class);
    
    
    expect(request.getRequestedSessionId()).andReturn("SESSIONID-123").anyTimes();
    expect(session.getId()).andReturn("SESSIONID-123").anyTimes();
    Cookie cookie = new Cookie("SAKAIID","SESSIONID-123");
    expect(request.getCookies()).andReturn(new Cookie[]{cookie}).anyTimes();


    expect(request.getAttribute("_no_session")).andReturn(null).anyTimes();
    expect(request.getSession(true)).andReturn(session).anyTimes();
    expect(request.getAttribute("_uuid")).andReturn(null).anyTimes();
    expect(session.getAttribute("_u")).andReturn(null).anyTimes();
    expect(session.getAttribute("_uu")).andReturn(null).anyTimes();
    expect(request.getLocale()).andReturn(new Locale("en", "US")).anyTimes();
    expect(session.getAttribute("sakai.locale.")).andReturn(null).anyTimes();
    response.setContentType(RestProvider.CONTENT_TYPE);
    expectLastCall().atLeastOnce();
    response.addCookie((Cookie) anyObject());
    expectLastCall().anyTimes();

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    expect(response.getOutputStream()).andReturn(new ServletOutputStream() {

      @Override
      public void write(int b) throws IOException {
        baos.write(b);
      }

    });
    expectLastCall().atLeastOnce();
    replay(request, response, session);

    SakaiServletRequest sakaiServletRequest = new SakaiServletRequest(request,
        response,  userResolverService, sessionManagerService);
    sessionManagerService.bindRequest(sakaiServletRequest);

    rmp.dispatch(new String[] { "me", "garbage" }, request, response);

    String responseString = new String(baos.toByteArray(), "UTF-8");

    System.err.println("Response Was " + responseString);
    assertTrue(responseString.indexOf("uuid : null") > 0);

    cacheManagerService.unbind(CacheScope.REQUEST);
    verify(request, response, session);

  }

  @Test
  public void testUserNoEnv() throws ServletException, IOException {
    KernelManager km = new KernelManager();
    SessionManagerService sessionManagerService = km
        .getService(SessionManagerService.class);
    CacheManagerService cacheManagerService = km
        .getService(CacheManagerService.class);
    UserResolverService userResolverService = km
        .getService(UserResolverService.class);
    RegistryService registryService = km.getService(RegistryService.class);
    Registry<String, RestProvider> registry = registryService
        .getRegistry(RestProvider.REST_REGISTRY);
    RestMeProvider rmp = (RestMeProvider) registry.getMap().get("me");
    HttpServletRequest request = createMock(HttpServletRequest.class);
    HttpServletResponse response = createMock(HttpServletResponse.class);
    HttpSession session = createMock(HttpSession.class);

    expect(request.getRequestedSessionId()).andReturn(null).anyTimes();
    expect(session.getId()).andReturn("SESSIONID-123-5").anyTimes();
    expect(request.getCookies()).andReturn(null).anyTimes();

    expect(request.getAttribute("_no_session")).andReturn(null).anyTimes();
    expect(request.getSession(true)).andReturn(session).anyTimes();
    expect(request.getAttribute("_uuid")).andReturn(null).anyTimes();
    expect(session.getAttribute("_u")).andReturn(new InternalUser("ib236"))
        .anyTimes();
    expect(session.getAttribute("_uu")).andReturn(null).anyTimes();
    expect(request.getLocale()).andReturn(new Locale("en", "US")).anyTimes();
    expect(session.getAttribute("sakai.locale.")).andReturn(null).anyTimes();
    response.addCookie((Cookie) anyObject());
    expectLastCall().anyTimes();
    response.setContentType(RestProvider.CONTENT_TYPE);
    expectLastCall().atLeastOnce();
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    expect(response.getOutputStream()).andReturn(new ServletOutputStream() {

      @Override
      public void write(int b) throws IOException {
        baos.write(b);
      }

    });
    expectLastCall().atLeastOnce();
    replay(request, response, session);

    SakaiServletRequest sakaiServletRequest = new SakaiServletRequest(request,
        response, userResolverService, sessionManagerService);
    sessionManagerService.bindRequest(sakaiServletRequest);

    rmp.dispatch(new String[] { "me", "garbage" }, request, response);

    String responseString = new String(baos.toByteArray(), "UTF-8");
    System.err.println("Response Was " + responseString);
    assertTrue(responseString.indexOf("\"ib236\"") > 0);

    cacheManagerService.unbind(CacheScope.REQUEST);
    verify(request, response, session);

  }

  @Test
  public void testUserWithEnv() throws ServletException, IOException,
      JCRNodeFactoryServiceException, LoginException, RepositoryException {

    KernelManager km = new KernelManager();
    SessionManagerService sessionManagerService = km
        .getService(SessionManagerService.class);
    CacheManagerService cacheManagerService = km
        .getService(CacheManagerService.class);
    UserResolverService userResolverService = km
        .getService(UserResolverService.class);
    AuthzResolverService authzResolverService = km
        .getService(AuthzResolverService.class);
    JCRService jcrService = km.getService(JCRService.class);
    JCRNodeFactoryService jcrNodeFactoryService = km
        .getService(JCRNodeFactoryService.class);
    // bypass security
    authzResolverService.setRequestGrant("Populating Test JSON");

    // login to the repo with super admin
    SakaiJCRCredentials credentials = new SakaiJCRCredentials();
    Session jsession = jcrService.getRepository().login(credentials);
    jcrService.setSession(jsession);

    // setup the user environment for the admin user.
    for (String userName : USERS) {
      String prefix = PathUtils.getUserPrefix(userName);
      String userEnvironmentPath = "/userenv" + prefix + "userenv";

      LOG.info("Saving " + userEnvironmentPath);
      jcrNodeFactoryService.createFile(userEnvironmentPath);
      InputStream in = ResourceLoader.openResource(TEST_USERENV + userName
          + ".json", AuthZServiceKernelUnitT.class.getClassLoader());
      jcrNodeFactoryService.setInputStream(userEnvironmentPath, in);
      jsession.save();
      in.close();
    }

    RegistryService registryService = km.getService(RegistryService.class);
    Registry<String, RestProvider> registry = registryService
        .getRegistry(RestProvider.REST_REGISTRY);
    RestMeProvider rmp = (RestMeProvider) registry.getMap().get("me");
    HttpServletRequest request = createMock(HttpServletRequest.class);
    HttpServletResponse response = createMock(HttpServletResponse.class);
    HttpSession session = createMock(HttpSession.class);

    expect(request.getRequestedSessionId()).andReturn("SESSIONID-123A").anyTimes();
    expect(session.getId()).andReturn("SESSIONID-123A").anyTimes();
    expect(request.getCookies()).andReturn(null).anyTimes();
    expect(request.getAttribute("_no_session")).andReturn(null).anyTimes();
    expect(request.getSession(true)).andReturn(session).anyTimes();
    expect(request.getAttribute("_uuid")).andReturn(null).anyTimes();
    expect(session.getAttribute("_u")).andReturn(new InternalUser("ib236"))
        .anyTimes();
    expect(session.getAttribute("_uu")).andReturn(null).anyTimes();
    expect(request.getLocale()).andReturn(new Locale("en", "US")).anyTimes();
    expect(session.getAttribute("sakai.locale.")).andReturn(null).anyTimes();
    response.addCookie((Cookie) anyObject());
    expectLastCall().anyTimes();
    response.setContentType(RestProvider.CONTENT_TYPE);
    expectLastCall().atLeastOnce();

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    expect(response.getOutputStream()).andReturn(new ServletOutputStream() {

      @Override
      public void write(int b) throws IOException {
        baos.write(b);
      }

    });
    expectLastCall().atLeastOnce();
    replay(request, response, session);

    SakaiServletRequest sakaiServletRequest = new SakaiServletRequest(request,
        response, userResolverService, sessionManagerService);
    sessionManagerService.bindRequest(sakaiServletRequest);

    rmp.dispatch(new String[] { "me", "garbage" }, request, response);

    System.err.println("=====================================FAILING HERE ");
    String responseString = new String(baos.toByteArray(), "UTF-8");
    System.err.println("Response Was " + responseString);
    assertTrue(responseString.indexOf("\"ib236\"") > 0);
    System.err.println("=====================================FAILING HERE PASED ");

    cacheManagerService.unbind(CacheScope.REQUEST);
    verify(request, response, session);

  }

}
