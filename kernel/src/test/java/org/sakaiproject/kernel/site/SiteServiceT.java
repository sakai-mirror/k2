/*
 * Licensed to the Sakai Foundation (SF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The SF licenses this file
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

package org.sakaiproject.kernel.site;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.KernelManager;
import org.sakaiproject.kernel.api.session.SessionManagerService;
import org.sakaiproject.kernel.api.site.NonUniqueIdException;
import org.sakaiproject.kernel.api.site.SiteService;
import org.sakaiproject.kernel.api.user.User;
import org.sakaiproject.kernel.api.user.UserResolverService;
import org.sakaiproject.kernel.model.RoleBean;
import org.sakaiproject.kernel.model.SiteBean;
import org.sakaiproject.kernel.session.SessionImpl;
import org.sakaiproject.kernel.test.KernelIntegrationBase;
import org.sakaiproject.kernel.webapp.SakaiServletRequest;
import org.sakaiproject.kernel.webapp.test.InternalUser;

import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Unit test for SiteServiceImpl
 */
public class SiteServiceT {

  private static Kernel kernel;
  private static SiteService siteService;

  private static boolean shutdown;
  private static Random rand;
  private static SessionManagerService sessMgr;
  private static UserResolverService userRes;

  private HttpServletRequest request;
  private HttpServletResponse response;
  private HttpSession session;

  @BeforeClass
  public static void beforeClass() throws Exception {
    rand = new Random();
    shutdown = KernelIntegrationBase.beforeClass();

    KernelManager manager = new KernelManager();
    kernel = manager.getKernel();

    siteService = kernel.getService(SiteService.class);
    assertNotNull(siteService);

    sessMgr = kernel.getService(SessionManagerService.class);
    assertNotNull(sessMgr);

    userRes = kernel.getService(UserResolverService.class);
    assertNotNull(userRes);

  }

  @AfterClass
  public static void afterClass() {
    KernelIntegrationBase.afterClass(shutdown);
  }

  private void setupUser(String username) {
    request = createMock(HttpServletRequest.class);
    session = createMock(HttpSession.class);
    response = createMock(HttpServletResponse.class);

    User user = null;
    if (username != null) {
      user = new InternalUser(username);
    }
    expect(session.getAttribute(SessionImpl.USER)).andReturn(user).anyTimes();
    expect(request.getRemoteUser()).andReturn(username).anyTimes();
    expect(request.getAttribute("_no_session")).andReturn(null).anyTimes();
    expect(request.getRequestedSessionId()).andReturn("TEST-12222").anyTimes();
    Cookie cookie = new Cookie("SAKAIID", "SESSIONID-123");
    expect(request.getCookies()).andReturn(new Cookie[] { cookie }).anyTimes();
    expect(request.getSession(true)).andReturn(session).anyTimes();
    expect(session.getId()).andReturn("TEST-12222").anyTimes();
    expect(request.getAttribute("_u")).andReturn(user).anyTimes();
    expect(request.getAttribute("_uuid")).andReturn(null).anyTimes();
    expect(session.getAttribute("check-valid")).andReturn(null).anyTimes();

    response.addCookie((Cookie) anyObject());
    expectLastCall().anyTimes();
    /*
     *
     * expect(request.getRemoteUser()).andReturn("").anyTimes();
     * expect(request.getAttribute("_uuid")).andReturn(null).anyTimes();
     */
    SakaiServletRequest req = new SakaiServletRequest(request, response,
        userRes, sessMgr);
    sessMgr.bindRequest(req);
  }

  @Test
  public void createSite() {
    setupUser("testUser1");
    replay(request, response, session);
    String siteId = generateSiteId();
    SiteBean site = new SiteBean();
    site.setId(siteId);
    site.setName("Test Site 1");
    site.setDescription("Site 1 for unit testing");
    site.setType("project");

    RoleBean[] roles = new RoleBean[1];
    roles[0] = new RoleBean();
    roles[0].setName("admin");
    roles[0].setPermissions(new String[] { "read", "write", "delete" });
    site.setRoles(roles);

    siteService.createSite(site);

    SiteBean siteBean = siteService.getSite(siteId);
    assertNotNull(siteBean);
    assertEquals(site.getId(), siteBean.getId());
    assertEquals(site.getName(), siteBean.getName());
    assertEquals(site.getDescription(), siteBean.getDescription());
    verify(request, response, session);
  }

  @Test
  public void createSiteNullUser() {
    setupUser(null);
    replay(request, response, session);
    String siteId = generateSiteId();
    SiteBean site = new SiteBean();
    site.setId(siteId);
    site.setName("Test Site 1");
    site.setDescription("Site 1 for unit testing");
    site.setType("project");

    RoleBean[] roles = new RoleBean[1];
    roles[0] = new RoleBean();
    roles[0].setName("admin");
    roles[0].setPermissions(new String[] { "read", "write", "delete" });
    site.setRoles(roles);

    siteService.createSite(site);
    verify(request, response, session);
  }

  @Test
  public void createDuplicateSite() {
    setupUser("testUser1");
    replay(request, response, session);
    String siteId = generateSiteId();
    SiteBean site = new SiteBean();
    site.setId(siteId);
    site.setName("Test Site 2");
    site.setDescription("Site 2 for unit testing");
    site.setType("project");

    RoleBean[] roles = new RoleBean[1];
    roles[0] = new RoleBean();
    roles[0].setName("admin");
    roles[0].setPermissions(new String[] { "read", "write", "delete" });
    site.setRoles(roles);

    siteService.createSite(site);

    try {
      siteService.createSite(site);
      fail("Duplicate site IDs are not allowed");
    } catch (NonUniqueIdException e) {
      // this is the correct response
    }
    verify(request, response, session);
  }

  @Test
  public void getSite() {
    setupUser("testUser1");
    replay(request, response, session);
    String siteId = generateSiteId();
    SiteBean site = new SiteBean();
    site.setId(siteId);
    site.setName("Test Site 3");
    site.setDescription("Site 3 for unit testing");
    site.setType("project");

    RoleBean[] roles = new RoleBean[1];
    roles[0] = new RoleBean();
    roles[0].setName("admin");
    roles[0].setPermissions(new String[] { "read", "write", "delete" });
    site.setRoles(roles);

    siteService.createSite(site);
    SiteBean siteGet = siteService.getSite(siteId);
    assertNotNull("Site not found when searching.", siteGet);
    assertEquals(siteGet.getId(), site.getId());
    assertEquals(siteGet.getName(), site.getName());
    verify(request, response, session);
  }

  @Test
  public void updateSite() {
    setupUser("testUser1");
    replay(request, response, session);
    String siteId = generateSiteId();
    SiteBean site = new SiteBean();
    site.setId(siteId);
    site.setName("Test Site");
    site.setDescription("Site for unit testing");
    site.setType("project");

    RoleBean[] roles = new RoleBean[1];
    roles[0] = new RoleBean();
    roles[0].setName("admin");
    roles[0].setPermissions(new String[] { "read", "write", "delete" });
    site.setRoles(roles);

    siteService.createSite(site);

    site.setName("Tester Siter");
    site.setDescription("Siter forer uniter testinger");
    siteService.saveSite(site);

    SiteBean siteBean = siteService.getSite(siteId);
    assertNotNull(siteBean);
    assertEquals(site.getId(), siteBean.getId());
    assertEquals(site.getName(), siteBean.getName());
    assertEquals(site.getDescription(), siteBean.getDescription());

    verify(request, response, session);
  }

  private String generateSiteId() {
    String siteBase = "testSite-";
    siteBase += rand.nextLong();
    return siteBase;
  }
}
