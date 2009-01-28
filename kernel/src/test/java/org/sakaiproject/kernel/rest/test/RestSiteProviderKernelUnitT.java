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
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

import org.easymock.Capture;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sakaiproject.kernel.Activator;
import org.sakaiproject.kernel.api.ComponentActivatorException;
import org.sakaiproject.kernel.api.KernelManager;
import org.sakaiproject.kernel.api.RegistryService;
import org.sakaiproject.kernel.api.authz.SubjectPermissionService;
import org.sakaiproject.kernel.api.memory.CacheManagerService;
import org.sakaiproject.kernel.api.memory.CacheScope;
import org.sakaiproject.kernel.api.serialization.BeanConverter;
import org.sakaiproject.kernel.api.session.SessionManagerService;
import org.sakaiproject.kernel.api.site.SiteService;
import org.sakaiproject.kernel.api.user.User;
import org.sakaiproject.kernel.api.user.UserResolverService;
import org.sakaiproject.kernel.api.userenv.UserEnvironmentResolverService;
import org.sakaiproject.kernel.model.RoleBean;
import org.sakaiproject.kernel.model.SiteBean;
import org.sakaiproject.kernel.rest.RestSiteProvider;
import org.sakaiproject.kernel.test.KernelIntegrationBase;
import org.sakaiproject.kernel.webapp.SakaiServletRequest;
import org.sakaiproject.kernel.webapp.test.InternalUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Unit tests for the RestSiteProvider
 */
public class RestSiteProviderKernelUnitT {

  private static boolean shutdown;
  private static Injector injector;

  @BeforeClass
  public static void beforeThisClass() throws ComponentActivatorException {
    shutdown = KernelIntegrationBase.beforeClass();
    injector = Activator.getInjector();
  }

  @AfterClass
  public static void afterThisClass() {
    KernelIntegrationBase.afterClass(shutdown);
  }

  private RegistryService registryService;
  private UserEnvironmentResolverService userEnvironmentResolverService;
  private SessionManagerService sessionManagerService;
  private SubjectPermissionService subjectPermissionService;
  private SiteService siteService;
  private UserResolverService userResolverService;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private HttpSession session;
  private CacheManagerService cacheManagerService;

  @Test
  public void testCheckId() {
  }

  /**
   * Check 409 on already exists
   * @throws ServletException
   * @throws IOException
   */
  @Test
  public void testCreateAlreadyExists() throws ServletException, IOException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("user1", "SESSION-21312312", baos);

    expect(request.getMethod()).andReturn("POST").anyTimes();
    expect(request.getParameter("id")).andReturn("sitethatexists");
    expect(siteService.siteExists("sitethatexists")).andReturn(true);
    response.reset();
    expectLastCall();
    response.sendError(409);
    expectLastCall();

    replayMocks();

    String[] elements = new String[] { "site", "create" };

    RestSiteProvider rsp = new RestSiteProvider(registryService, siteService,
        injector.getInstance(Key.get(BeanConverter.class, Names
            .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        userEnvironmentResolverService, sessionManagerService,
        subjectPermissionService);
    rsp.dispatch(elements, request, response);

    verifyMocks();
  }

  /**
   * Check create works.
   * @throws ServletException
   * @throws IOException
   */
  @Test
  public void testCreate() throws ServletException, IOException {

    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("user1", "SESSION-21312312", baos);

    expect(request.getMethod()).andReturn("POST").anyTimes();
    expect(request.getParameter("id")).andReturn("sitethatdoesnotexist");
    expect(siteService.siteExists("sitethatdoesnotexist")).andReturn(false);
    expect(request.getParameter("name")).andReturn("Name:sitethatdoesnotexist");
    expect(request.getParameter("description")).andReturn(
        "Description:sitethatdoesnotexist");
    expect(request.getParameter("type")).andReturn("Type:sitethatdoesnotexist");
    Capture<SiteBean> captureSiteBean = new Capture<SiteBean>();
    siteService.createSite(capture(captureSiteBean));
    expectLastCall();
    response.setContentType("text/plain");
    expectLastCall();

    replayMocks();

    String[] elements = new String[] { "site", "create" };

    RestSiteProvider rsp = new RestSiteProvider(registryService, siteService,
        injector.getInstance(Key.get(BeanConverter.class, Names
            .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        userEnvironmentResolverService, sessionManagerService,
        subjectPermissionService);
    rsp.dispatch(elements, request, response);

    String body = new String(baos.toByteArray(), "UTF-8");
    assertEquals("{\"response\":\"OK\"}", body);

    assertTrue(captureSiteBean.hasCaptured());
    SiteBean siteBean = captureSiteBean.getValue();
    assertNotNull(siteBean);
    assertEquals("sitethatdoesnotexist", siteBean.getId());
    assertEquals("Name:sitethatdoesnotexist", siteBean.getName());
    assertEquals("Description:sitethatdoesnotexist", siteBean.getDescription());
    assertEquals("Type:sitethatdoesnotexist", siteBean.getType());
    assertNotNull(siteBean.getOwners());
    assertEquals(1, siteBean.getOwners().length);
    assertArrayEquals(new String[]{"user1"}, siteBean.getOwners());
    verifyMocks();
  }

  /**
   * Setup mocks for any time execution.
   * @param baos
   * @throws IOException
   * 
   */
  private void setupAnyTimes(String username, String sessionID,
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

  /**
   * Set up the services and mocks.
   */
  private void setupServices() {
    KernelManager km = new KernelManager();
    registryService = km.getService(RegistryService.class);
    userEnvironmentResolverService = km
        .getService(UserEnvironmentResolverService.class);
    sessionManagerService = km.getService(SessionManagerService.class);
    subjectPermissionService = km.getService(SubjectPermissionService.class);
    cacheManagerService = km.getService(CacheManagerService.class);

    siteService = createMock(SiteService.class);
    userResolverService = createMock(UserResolverService.class);
    request = createMock(HttpServletRequest.class);
    response = createMock(HttpServletResponse.class);
    session = createMock(HttpSession.class);
  }

  /**
   * Replay mocks at the end of setup, and bind the request to the thread.
   */
  private void replayMocks() {
    replay(request, response, session, siteService, userResolverService);
    SakaiServletRequest sakaiServletRequest = new SakaiServletRequest(request,
        response, userResolverService, sessionManagerService);
    sessionManagerService.bindRequest(sakaiServletRequest);

  }

  /**
   * Reset mocks to have another go with the same setup.
   */
  @SuppressWarnings("unused")
  private void resetMocks() {
    replay(request, response, session, siteService, userResolverService);
  }

  /**
   * Check for existence of a site by ID
   * @throws ServletException
   * @throws IOException
   */
  @Test
  public void testCheckIDExists() throws ServletException, IOException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("user1", "SESSION-21312312", baos);

    SiteBean siteBean = new SiteBean();
    siteBean.setId("sitethatexists");
    siteBean.setName("name");
    siteBean.setType("type");
    siteBean.setRoles(new RoleBean[] {
        new RoleBean("maintain", new String[] { "read", "write", "remove" }),
        new RoleBean("access", new String[] { "read" }) });
    expect(siteService.getSite("sitethatexists")).andReturn(siteBean);

    replayMocks();
    String[] elements = new String[] { "site", "get", "sitethatexists" };

    RestSiteProvider rsp = new RestSiteProvider(registryService, siteService,
        injector.getInstance(Key.get(BeanConverter.class, Names
            .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        userEnvironmentResolverService, sessionManagerService,
        subjectPermissionService);
    rsp.dispatch(elements, request, response);

    String body = new String(baos.toByteArray(), "UTF-8");
    assertEquals(
        "{\"type\":\"type\",\"subjectTokens\":[\"name:maintain\",\"name:access\"],"
            + "\"roles\":[{\"permissions\":[\"read\",\"write\",\"remove\"],"
            + "\"name\":\"maintain\"},{\"permissions\":[\"read\"],"
            + "\"name\":\"access\"}],\"name\":\"name\",\"id\":\"sitethatexists\"}",
        body);

    verifyMocks();
  }

  /**
   * Check for non existance of a site.
   * @throws ServletException
   * @throws IOException
   */
  @Test
  public void testCheckIDDoesNotExists() throws ServletException, IOException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("user1", "SESSION-21312312", baos);

    expect(siteService.getSite("sitethatdoesnotexist")).andReturn(null);
    response.reset();
    expectLastCall();
    response.sendError(404);
    expectLastCall();

    replayMocks();
    String[] elements = new String[] { "site", "get", "sitethatdoesnotexist" };

    RestSiteProvider rsp = new RestSiteProvider(registryService, siteService,
        injector.getInstance(Key.get(BeanConverter.class, Names
            .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        userEnvironmentResolverService, sessionManagerService,
        subjectPermissionService);
    rsp.dispatch(elements, request, response);

    verifyMocks();
  }

  /**
   * Check for 400 if site id not specific on check.
   * @throws ServletException
   * @throws IOException
   */
  @Test
  public void testCheckIDNoSite() throws ServletException, IOException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("user1", "SESSION-21312312", baos);

    response.reset();
    expectLastCall();
    response.sendError(400, "No Site ID specified");
    expectLastCall();

    replayMocks();
    String[] elements = new String[] { "site", "get" };

    RestSiteProvider rsp = new RestSiteProvider(registryService, siteService,
        injector.getInstance(Key.get(BeanConverter.class, Names
            .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        userEnvironmentResolverService, sessionManagerService,
        subjectPermissionService);
    rsp.dispatch(elements, request, response);

    verifyMocks();
  }

  private void verifyMocks() {
    verify(request, response, session, siteService, userResolverService);
    cacheManagerService.unbind(CacheScope.REQUEST);
    cacheManagerService.unbind(CacheScope.THREAD);
  }

  /**
   * Checks that for 400 on no site id
   * @throws ServletException
   * @throws IOException
   */
  @Test
  public void testAddOwnerBadMethod1() throws ServletException, IOException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("user1", "SESSION-21312312", baos);

    expect(request.getMethod()).andReturn("POST").anyTimes();
    // check for a bad method
    response.reset();
    expectLastCall();
    response.sendError(400, "Site ID Must be specified");
    expectLastCall();

    replayMocks();

    String[] elements = new String[] { "site", "addOwner" };

    RestSiteProvider rsp = new RestSiteProvider(registryService, siteService,
        injector.getInstance(Key.get(BeanConverter.class, Names
            .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        userEnvironmentResolverService, sessionManagerService,
        subjectPermissionService);
    rsp.dispatch(elements, request, response);

    verifyMocks();

  }

  /**
   * Check for 400 o no site id specified for remove.
   * @throws ServletException
   * @throws IOException
   */
  @Test
  public void testRemoveOwnerBadMethod1() throws ServletException, IOException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("user1", "SESSION-21312312", baos);

    expect(request.getMethod()).andReturn("POST").anyTimes();
    // check for a bad method
    response.reset();
    expectLastCall();
    response.sendError(400, "Site ID Must be specified");
    expectLastCall();

    replayMocks();

    String[] elements = new String[] { "site", "removeOwner" };

    RestSiteProvider rsp = new RestSiteProvider(registryService, siteService,
        injector.getInstance(Key.get(BeanConverter.class, Names
            .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        userEnvironmentResolverService, sessionManagerService,
        subjectPermissionService);
    rsp.dispatch(elements, request, response);

    verifyMocks();

  }

  /**
   * Check for no user id specified on addOwner.
   * @throws ServletException
   * @throws IOException
   */
  @Test
  public void testAddOwnerBadMethod2() throws ServletException, IOException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("user1", "SESSION-21312312", baos);

    expect(request.getMethod()).andReturn("POST").anyTimes();
    // check for a bad method
    response.reset();
    expectLastCall();
    response.sendError(400, "User ID Must be specified");
    expectLastCall();

    replayMocks();

    String[] elements = new String[] { "site", "addOwner",
        "sitethatdoesntexist" };

    RestSiteProvider rsp = new RestSiteProvider(registryService, siteService,
        injector.getInstance(Key.get(BeanConverter.class, Names
            .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        userEnvironmentResolverService, sessionManagerService,
        subjectPermissionService);
    rsp.dispatch(elements, request, response);

    verifyMocks();

  }

  /**
   * Check for remove owner with no user ID, should be 400.
   * @throws ServletException
   * @throws IOException
   */
  @Test
  public void testRemoveOwnerBadMethod2() throws ServletException, IOException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("user1", "SESSION-21312312", baos);

    expect(request.getMethod()).andReturn("POST").anyTimes();
    // check for a bad method
    response.reset();
    expectLastCall();
    response.sendError(400, "User ID Must be specified");
    expectLastCall();

    replayMocks();

    String[] elements = new String[] { "site", "removeOwner",
        "sitethatdoesntexist" };

    RestSiteProvider rsp = new RestSiteProvider(registryService, siteService,
        injector.getInstance(Key.get(BeanConverter.class, Names
            .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        userEnvironmentResolverService, sessionManagerService,
        subjectPermissionService);
    rsp.dispatch(elements, request, response);

    verifyMocks();

  }

  /**
   * Check for a 401 on anon add owner.
   * @throws ServletException
   * @throws IOException
   */
  @Test
  public void testAddOwnerAnon() throws ServletException, IOException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes(null, "sdsdfsdfs", baos);

    expect(request.getMethod()).andReturn("POST").anyTimes();
    // check for a bad method
    response.reset();
    expectLastCall();
    response.sendError(401);
    expectLastCall();

    replayMocks();

    String[] elements = new String[] { "site", "addOwner",
        "sitethatdoesntexist", "auser" };

    RestSiteProvider rsp = new RestSiteProvider(registryService, siteService,
        injector.getInstance(Key.get(BeanConverter.class, Names
            .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        userEnvironmentResolverService, sessionManagerService,
        subjectPermissionService);
    rsp.dispatch(elements, request, response);

    verifyMocks();

  }

  /**
   * Check for a 401 on anon create site.
   * @throws ServletException
   * @throws IOException
   */
  @Test
  public void testCreateSiteAnon() throws ServletException, IOException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes(null, "sdsdfsdfs", baos);

    expect(request.getMethod()).andReturn("POST").anyTimes();
    // check for a bad method
    response.reset();
    expectLastCall();
    response.sendError(401);
    expectLastCall();

    replayMocks();

    String[] elements = new String[] { "site", "create" };

    RestSiteProvider rsp = new RestSiteProvider(registryService, siteService,
        injector.getInstance(Key.get(BeanConverter.class, Names
            .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        userEnvironmentResolverService, sessionManagerService,
        subjectPermissionService);
    rsp.dispatch(elements, request, response);

    verifyMocks();

  }

  /**
   * check for a bad method on create. (should be POST only)
   * @throws ServletException
   * @throws IOException
   */
  @Test
  public void testBadMethodAnon1() throws ServletException, IOException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes(null, "sdsdfsdfs", baos);

    expect(request.getMethod()).andReturn("GET").anyTimes();
    // check for a bad method
    response.reset();
    expectLastCall();
    response.sendError(405);
    expectLastCall();

    replayMocks();

    String[] elements = new String[] { "site", "create" };

    RestSiteProvider rsp = new RestSiteProvider(registryService, siteService,
        injector.getInstance(Key.get(BeanConverter.class, Names
            .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        userEnvironmentResolverService, sessionManagerService,
        subjectPermissionService);
    rsp.dispatch(elements, request, response);

    verifyMocks();

  }

  /**
   * Check for a bad method on addOwner should be POST only. 
   * @throws ServletException
   * @throws IOException
   */
  @Test
  public void testBadMethodAnon2() throws ServletException, IOException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes(null, "sdsdfsdfs", baos);

    expect(request.getMethod()).andReturn("GET").anyTimes();
    // check for a bad method
    response.reset();
    expectLastCall();
    response.sendError(405);
    expectLastCall();

    replayMocks();

    String[] elements = new String[] { "site", "addOwner" };

    RestSiteProvider rsp = new RestSiteProvider(registryService, siteService,
        injector.getInstance(Key.get(BeanConverter.class, Names
            .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        userEnvironmentResolverService, sessionManagerService,
        subjectPermissionService);
    rsp.dispatch(elements, request, response);

    verifyMocks();

  }

  /**
   * Check for a bad method on remove Owner should be POST only.
   * @throws ServletException
   * @throws IOException
   */
  @Test
  public void testBadMethodAnon3() throws ServletException, IOException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes(null, "sdsdfsdfs", baos);

    expect(request.getMethod()).andReturn("GET").anyTimes();
    // check for a bad method
    response.reset();
    expectLastCall();
    response.sendError(405);
    expectLastCall();

    replayMocks();

    String[] elements = new String[] { "site", "removeOwner" };

    RestSiteProvider rsp = new RestSiteProvider(registryService, siteService,
        injector.getInstance(Key.get(BeanConverter.class, Names
            .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        userEnvironmentResolverService, sessionManagerService,
        subjectPermissionService);
    rsp.dispatch(elements, request, response);

    verifyMocks();

  }

  /**
   * Test for 401 on anon remove owner.
   * @throws ServletException
   * @throws IOException
   */
  @Test
  public void testRemoveOwnerAnon() throws ServletException, IOException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes(null, "sdsdfsdfs", baos);

    expect(request.getMethod()).andReturn("POST").anyTimes();
    // check for a bad method
    response.reset();
    expectLastCall();
    response.sendError(401);
    expectLastCall();

    replayMocks();

    String[] elements = new String[] { "site", "removeOwner" };

    RestSiteProvider rsp = new RestSiteProvider(registryService, siteService,
        injector.getInstance(Key.get(BeanConverter.class, Names
            .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        userEnvironmentResolverService, sessionManagerService,
        subjectPermissionService);
    rsp.dispatch(elements, request, response);

    verifyMocks();
  }

  /**
   * Test for remove owner OK from list of 2.
   * @throws ServletException
   * @throws IOException
   */
  @Test
  public void testRemoveOwnerOK() throws ServletException, IOException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("user1", "assfsadfsdf", baos);

    expect(request.getMethod()).andReturn("POST").anyTimes();
    SiteBean siteBean = new SiteBean();
    siteBean.setId("testSiteA");
    siteBean.setName("name: testSiteA");
    siteBean.setType("type");
    siteBean.setRoles(new RoleBean[] {
        new RoleBean("maintain", new String[] { "read", "write", "remove" }),
        new RoleBean("access", new String[] { "read" }) });
    siteBean.setOwners(new String[] { "user1", "user2" });
    expect(siteService.getSite("testSiteA")).andReturn(siteBean);
    Capture<SiteBean> siteBeanCapture = new Capture<SiteBean>();
    siteService.saveSite(capture(siteBeanCapture));
    
    response.setContentType("text/plain");
    expectLastCall();
    
    replayMocks();

    String[] elements = new String[] { "site", "removeOwner", "testSiteA",
        "user2" };

    RestSiteProvider rsp = new RestSiteProvider(registryService, siteService,
        injector.getInstance(Key.get(BeanConverter.class, Names
            .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        userEnvironmentResolverService, sessionManagerService,
        subjectPermissionService);
    rsp.dispatch(elements, request, response);

    assertTrue(siteBeanCapture.hasCaptured());
    SiteBean finalSiteBean = siteBeanCapture.getValue();
    assertNotNull(finalSiteBean.getOwners());
    assertArrayEquals(new String[] {"user1"},finalSiteBean.getOwners());
    
    verifyMocks();
  }

  /**
   * Test fore remove owner from a list of 3 middle one removed.
   * @throws ServletException
   * @throws IOException
   */
  @Test
  public void testRemoveOwnerOK2() throws ServletException, IOException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("user1", "assfsadfsdf", baos);

    expect(request.getMethod()).andReturn("POST").anyTimes();
    SiteBean siteBean = new SiteBean();
    siteBean.setId("testSiteA");
    siteBean.setName("name: testSiteA");
    siteBean.setType("type");
    siteBean.setRoles(new RoleBean[] {
        new RoleBean("maintain", new String[] { "read", "write", "remove" }),
        new RoleBean("access", new String[] { "read" }) });
    siteBean.setOwners(new String[] { "user1", "user2", "user5" });
    expect(siteService.getSite("testSiteA")).andReturn(siteBean);
    Capture<SiteBean> siteBeanCapture = new Capture<SiteBean>();
    siteService.saveSite(capture(siteBeanCapture));
    
    response.setContentType("text/plain");
    expectLastCall();
    
    replayMocks();

    String[] elements = new String[] { "site", "removeOwner", "testSiteA",
        "user2" };

    RestSiteProvider rsp = new RestSiteProvider(registryService, siteService,
        injector.getInstance(Key.get(BeanConverter.class, Names
            .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        userEnvironmentResolverService, sessionManagerService,
        subjectPermissionService);
    rsp.dispatch(elements, request, response);

    assertTrue(siteBeanCapture.hasCaptured());
    SiteBean finalSiteBean = siteBeanCapture.getValue();
    assertNotNull(finalSiteBean.getOwners());
    assertArrayEquals(new String[] {"user1","user5"},finalSiteBean.getOwners());
    
    verifyMocks();
  }

  /**
   * test for remove owner no change.
   * @throws ServletException
   * @throws IOException
   */
  @Test
  public void testRemoveOwnerNoChange() throws ServletException, IOException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("user1", "assfsadfsdf", baos);

    expect(request.getMethod()).andReturn("POST").anyTimes();
    SiteBean siteBean = new SiteBean();
    siteBean.setId("testSiteA");
    siteBean.setName("name: testSiteA");
    siteBean.setType("type");
    siteBean.setRoles(new RoleBean[] {
        new RoleBean("maintain", new String[] { "read", "write", "remove" }),
        new RoleBean("access", new String[] { "read" }) });
    siteBean.setOwners(new String[] { "user1", "user2", "user5" });
    expect(siteService.getSite("testSiteA")).andReturn(siteBean);
    
    
    response.reset();
    expectLastCall();
    response.sendError(404,"User user8 is not an owner of testSiteA");
    expectLastCall();
    
    replayMocks();

    String[] elements = new String[] { "site", "removeOwner", "testSiteA",
        "user8" };

    RestSiteProvider rsp = new RestSiteProvider(registryService, siteService,
        injector.getInstance(Key.get(BeanConverter.class, Names
            .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        userEnvironmentResolverService, sessionManagerService,
        subjectPermissionService);
    rsp.dispatch(elements, request, response);

    
    verifyMocks();
  }
  /**
   * test for 404 on remove with non existant site.
   * @throws ServletException
   * @throws IOException
   */
  @Test
  public void testRemoveOwnerNoSite() throws ServletException, IOException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("user1", "assfsadfsdf", baos);

    expect(request.getMethod()).andReturn("POST").anyTimes();
    expect(siteService.getSite("testSiteA")).andReturn(null);
    
    
    response.reset();
    expectLastCall();
    response.sendError(404,"Site testSiteA not found");
    expectLastCall();
    
    replayMocks();

    String[] elements = new String[] { "site", "removeOwner", "testSiteA",
        "user8" };

    RestSiteProvider rsp = new RestSiteProvider(registryService, siteService,
        injector.getInstance(Key.get(BeanConverter.class, Names
            .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        userEnvironmentResolverService, sessionManagerService,
        subjectPermissionService);
    rsp.dispatch(elements, request, response);

    
    verifyMocks();
  }

  /**
   * Test for 403 on non owner remove site attempt.
   * @throws ServletException
   * @throws IOException
   */
  @Test
  public void testRemoveOwnerNotOwner() throws ServletException, IOException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("user1", "assfsadfsdf", baos);

    expect(request.getMethod()).andReturn("POST").anyTimes();
    SiteBean siteBean = new SiteBean();
    siteBean.setId("testSiteA");
    siteBean.setName("name: testSiteA");
    siteBean.setType("type");
    siteBean.setRoles(new RoleBean[] {
        new RoleBean("maintain", new String[] { "read", "write", "remove" }),
        new RoleBean("access", new String[] { "read" }) });
    siteBean.setOwners(new String[] { "user1notowner", "user2", "user5" });
    expect(siteService.getSite("testSiteA")).andReturn(siteBean);
    
    
    response.reset();
    expectLastCall();
    response.sendError(403);
    expectLastCall();
    
    replayMocks();

    String[] elements = new String[] { "site", "removeOwner", "testSiteA",
        "user2" };

    RestSiteProvider rsp = new RestSiteProvider(registryService, siteService,
        injector.getInstance(Key.get(BeanConverter.class, Names
            .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        userEnvironmentResolverService, sessionManagerService,
        subjectPermissionService);
    rsp.dispatch(elements, request, response);

    
    verifyMocks();
  }
  /**
   * Test for 403 on remove last owner.
   * @throws ServletException
   * @throws IOException
   */
  @Test
  public void testRemoveOwnerTooFew() throws ServletException, IOException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("user1", "assfsadfsdf", baos);

    expect(request.getMethod()).andReturn("POST").anyTimes();
    SiteBean siteBean = new SiteBean();
    siteBean.setId("testSiteA");
    siteBean.setName("name: testSiteA");
    siteBean.setType("type");
    siteBean.setRoles(new RoleBean[] {
        new RoleBean("maintain", new String[] { "read", "write", "remove" }),
        new RoleBean("access", new String[] { "read" }) });
    siteBean.setOwners(new String[] { "user1" });
    expect(siteService.getSite("testSiteA")).andReturn(siteBean);
    
    
    response.reset();
    expectLastCall();
    response.sendError(403);
    expectLastCall();
    
    replayMocks();

    String[] elements = new String[] { "site", "removeOwner", "testSiteA",
        "user1" };

    RestSiteProvider rsp = new RestSiteProvider(registryService, siteService,
        injector.getInstance(Key.get(BeanConverter.class, Names
            .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        userEnvironmentResolverService, sessionManagerService,
        subjectPermissionService);
    rsp.dispatch(elements, request, response);

    
    verifyMocks();
  }
  /**
   * Test for add when not owner 403
   * @throws ServletException
   * @throws IOException
   */
  @Test
  public void testaddOwnerNotOwner() throws ServletException, IOException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("user1", "assfsadfsdf", baos);

    expect(request.getMethod()).andReturn("POST").anyTimes();
    SiteBean siteBean = new SiteBean();
    siteBean.setId("testSiteA");
    siteBean.setName("name: testSiteA");
    siteBean.setType("type");
    siteBean.setRoles(new RoleBean[] {
        new RoleBean("maintain", new String[] { "read", "write", "remove" }),
        new RoleBean("access", new String[] { "read" }) });
    siteBean.setOwners(new String[] { "user1notowner", "user2", "user5" });
    expect(siteService.getSite("testSiteA")).andReturn(siteBean);
    
    
    response.reset();
    expectLastCall();
    response.sendError(403);
    expectLastCall();
    
    replayMocks();

    String[] elements = new String[] { "site", "addOwner", "testSiteA",
        "user2" };

    RestSiteProvider rsp = new RestSiteProvider(registryService, siteService,
        injector.getInstance(Key.get(BeanConverter.class, Names
            .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        userEnvironmentResolverService, sessionManagerService,
        subjectPermissionService);
    rsp.dispatch(elements, request, response);

    
    verifyMocks();
  }
  
  @Test
  public void testAddOwnerOK() throws ServletException, IOException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("user1", "assfsadfsdf", baos);

    expect(request.getMethod()).andReturn("POST").anyTimes();
    SiteBean siteBean = new SiteBean();
    siteBean.setId("testSiteA");
    siteBean.setName("name: testSiteA");
    siteBean.setType("type");
    siteBean.setRoles(new RoleBean[] {
        new RoleBean("maintain", new String[] { "read", "write", "remove" }),
        new RoleBean("access", new String[] { "read" }) });
    siteBean.setOwners(new String[] { "user1" });
    expect(siteService.getSite("testSiteA")).andReturn(siteBean);
    Capture<SiteBean> siteBeanCapture = new Capture<SiteBean>();
    siteService.saveSite(capture(siteBeanCapture));
    
    response.setContentType("text/plain");
    expectLastCall();
    
    replayMocks();

    String[] elements = new String[] { "site", "addOwner", "testSiteA",
        "user2" };

    RestSiteProvider rsp = new RestSiteProvider(registryService, siteService,
        injector.getInstance(Key.get(BeanConverter.class, Names
            .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        userEnvironmentResolverService, sessionManagerService,
        subjectPermissionService);
    rsp.dispatch(elements, request, response);

    assertTrue(siteBeanCapture.hasCaptured());
    SiteBean finalSiteBean = siteBeanCapture.getValue();
    assertNotNull(finalSiteBean.getOwners());
    assertArrayEquals(new String[] {"user1","user2"},finalSiteBean.getOwners());
    
    verifyMocks();
  }
  @Test
  public void testAddOwnerOK2() throws ServletException, IOException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("user1", "assfsadfsdf", baos);

    expect(request.getMethod()).andReturn("POST").anyTimes();
    SiteBean siteBean = new SiteBean();
    siteBean.setId("testSiteA");
    siteBean.setName("name: testSiteA");
    siteBean.setType("type");
    siteBean.setRoles(new RoleBean[] {
        new RoleBean("maintain", new String[] { "read", "write", "remove" }),
        new RoleBean("access", new String[] { "read" }) });
    siteBean.setOwners(new String[] { "user1","user5" });
    expect(siteService.getSite("testSiteA")).andReturn(siteBean);
    Capture<SiteBean> siteBeanCapture = new Capture<SiteBean>();
    siteService.saveSite(capture(siteBeanCapture));
    
    response.setContentType("text/plain");
    expectLastCall();
    
    replayMocks();

    String[] elements = new String[] { "site", "addOwner", "testSiteA",
        "user2" };

    RestSiteProvider rsp = new RestSiteProvider(registryService, siteService,
        injector.getInstance(Key.get(BeanConverter.class, Names
            .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        userEnvironmentResolverService, sessionManagerService,
        subjectPermissionService);
    rsp.dispatch(elements, request, response);

    assertTrue(siteBeanCapture.hasCaptured());
    SiteBean finalSiteBean = siteBeanCapture.getValue();
    assertNotNull(finalSiteBean.getOwners());
    assertArrayEquals(new String[] {"user1","user5","user2"},finalSiteBean.getOwners());
    
    verifyMocks();
  }
  @Test
  public void testAddOwnerNoChange() throws ServletException, IOException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("user1", "assfsadfsdf", baos);

    expect(request.getMethod()).andReturn("POST").anyTimes();
    SiteBean siteBean = new SiteBean();
    siteBean.setId("testSiteA");
    siteBean.setName("name: testSiteA");
    siteBean.setType("type");
    siteBean.setRoles(new RoleBean[] {
        new RoleBean("maintain", new String[] { "read", "write", "remove" }),
        new RoleBean("access", new String[] { "read" }) });
    siteBean.setOwners(new String[] { "user1", "user2", "user5" });
    expect(siteService.getSite("testSiteA")).andReturn(siteBean);
    
    
    response.reset();
    expectLastCall();
    response.sendError(409,"User user2 is already an owner of testSiteA");
    expectLastCall();
    
    replayMocks();

    String[] elements = new String[] { "site", "addOwner", "testSiteA",
        "user2" };

    RestSiteProvider rsp = new RestSiteProvider(registryService, siteService,
        injector.getInstance(Key.get(BeanConverter.class, Names
            .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        userEnvironmentResolverService, sessionManagerService,
        subjectPermissionService);
    rsp.dispatch(elements, request, response);

    
    verifyMocks();
  }
  @Test
  public void testaddOwnerNoSite() throws ServletException, IOException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("user1", "assfsadfsdf", baos);

    expect(request.getMethod()).andReturn("POST").anyTimes();
    expect(siteService.getSite("testSiteA")).andReturn(null);
    
    
    response.reset();
    expectLastCall();
    response.sendError(404,"Site testSiteA not found");
    expectLastCall();
    
    replayMocks();

    String[] elements = new String[] { "site", "addOwner", "testSiteA",
        "user8" };

    RestSiteProvider rsp = new RestSiteProvider(registryService, siteService,
        injector.getInstance(Key.get(BeanConverter.class, Names
            .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        userEnvironmentResolverService, sessionManagerService,
        subjectPermissionService);
    rsp.dispatch(elements, request, response);

    
    verifyMocks();
  }
  @Test
  public void testAddOwnerNotOwner() throws ServletException, IOException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("user1", "assfsadfsdf", baos);

    expect(request.getMethod()).andReturn("POST").anyTimes();
    SiteBean siteBean = new SiteBean();
    siteBean.setId("testSiteA");
    siteBean.setName("name: testSiteA");
    siteBean.setType("type");
    siteBean.setRoles(new RoleBean[] {
        new RoleBean("maintain", new String[] { "read", "write", "remove" }),
        new RoleBean("access", new String[] { "read" }) });
    siteBean.setOwners(new String[] { "user1notowner", "user2", "user5" });
    expect(siteService.getSite("testSiteA")).andReturn(siteBean);
    
    
    response.reset();
    expectLastCall();
    response.sendError(403);
    expectLastCall();
    
    replayMocks();

    String[] elements = new String[] { "site", "addOwner", "testSiteA",
        "user2" };

    RestSiteProvider rsp = new RestSiteProvider(registryService, siteService,
        injector.getInstance(Key.get(BeanConverter.class, Names
            .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        userEnvironmentResolverService, sessionManagerService,
        subjectPermissionService);
    rsp.dispatch(elements, request, response);

    
    verifyMocks();
  }
}
