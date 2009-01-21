/*******************************************************************************
 * Copyright 2008 Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.sakaiproject.kernel.site;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.sakaiproject.kernel.api.ComponentActivator;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.KernelManager;
import org.sakaiproject.kernel.api.session.SessionManagerService;
import org.sakaiproject.kernel.api.site.NonUniqueIdException;
import org.sakaiproject.kernel.api.site.SiteService;
import org.sakaiproject.kernel.api.user.User;
import org.sakaiproject.kernel.api.user.UserResolverService;
import org.sakaiproject.kernel.component.KernelLifecycle;
import org.sakaiproject.kernel.model.RoleBean;
import org.sakaiproject.kernel.model.SiteBean;
import org.sakaiproject.kernel.session.SessionImpl;
import org.sakaiproject.kernel.test.KernelIntegrationBase;
import org.sakaiproject.kernel.util.FileUtil;
import org.sakaiproject.kernel.webapp.SakaiServletRequest;
import org.sakaiproject.kernel.webapp.test.InternalUser;

import java.io.File;

import javax.persistence.PersistenceException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 */
public class SiteServiceTest {

  protected static Kernel kernel;
  protected static SiteService siteService;

  @BeforeClass
  public static void beforeClass() throws Exception {
    KernelIntegrationBase.disableKernelStartup();
    File jcrBase = new File("target/jcr");
    File dbBase = new File("target/testdb");
    System.err
        .println("==========================================================================");
    System.err.println("Removing all previous JCR and DB traces from "
        + jcrBase.getAbsolutePath() + " " + dbBase.getAbsolutePath());

    FileUtil.deleteAll(jcrBase);
    FileUtil.deleteAll(dbBase);
    System.err
        .println("==========================================================================");

    KernelLifecycle lifecycle = new KernelLifecycle();
    lifecycle.start();

    KernelManager manager = new KernelManager();
    kernel = manager.getKernel();

    ComponentActivator activator = new org.sakaiproject.kernel.Activator();
    activator.activate(kernel);

    siteService = kernel.getService(SiteService.class);
    assertNotNull(siteService);

    SessionManagerService sessMgr = kernel
        .getService(SessionManagerService.class);
    UserResolverService userRes = kernel.getService(UserResolverService.class);
    SessionManagerService sessionManagerService = kernel
        .getService(SessionManagerService.class);

    HttpServletRequest request = createMock(HttpServletRequest.class);

    User user = new InternalUser("testUser1");
    HttpSession session = createMock(HttpSession.class);
    HttpServletResponse response = createMock(HttpServletResponse.class);
    expect(request.getRemoteUser()).andReturn("");
    expect(request.getSession(false)).andReturn(session);
    expect(session.getAttribute(SessionImpl.USER)).andReturn(user).anyTimes();

    expect(request.getRequestedSessionId()).andReturn("TEST-12222").anyTimes();
    Cookie cookie = new Cookie("SAKAIID","SESSIONID-123");
    expect(request.getCookies()).andReturn(new Cookie[]{cookie}).anyTimes();
    expect(session.getId()).andReturn("TEST-12222").anyTimes();
    expect(request.getRemoteUser()).andReturn("").anyTimes();
    expect(request.getAttribute("_uuid")).andReturn(null).anyTimes();
    expect(request.getAttribute("_no_session")).andReturn(null).anyTimes();
    replay(request, session);

    SakaiServletRequest req = new SakaiServletRequest(request, response,
        userRes, sessionManagerService);
    assertNotNull(req.getRemoteUser());
    verify(request, session);

    sessMgr.bindRequest(req);
  }

  @AfterClass
  public static void afterClass() {
    KernelIntegrationBase.enableKernelStartup();

  }

  @Test
  public void createSite() {
    SiteBean site = new SiteBean();
    site.setId("testSite1");
    site.setName("Test Site 1");
    site.setDescription("Site 1 for unit testing");
    site.setType("project");

    RoleBean[] roles = new RoleBean[1];
    roles[0] = new RoleBean();
    roles[0].setName("admin");
    roles[0].setPermissions(new String[] { "read", "write", "delete" });
    site.setRoles(roles);

    siteService.createSite(site);
  }

  @Ignore
  public void createDuplicateSite() {
    SiteBean site = new SiteBean();
    site.setId("testSite2");
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
    } catch (PersistenceException e) {
      //
    } catch (NonUniqueIdException e) {
      // this is the correct response
    }
  }

  @Ignore
  public void getSite() {
    SiteBean site = new SiteBean();
    site.setId("testSite3");
    site.setName("Test Site 3");
    site.setDescription("Site 3 for unit testing");
    site.setType("project");

    RoleBean[] roles = new RoleBean[1];
    roles[0] = new RoleBean();
    roles[0].setName("admin");
    roles[0].setPermissions(new String[] { "read", "write", "delete" });
    site.setRoles(roles);

    siteService.createSite(site);
    SiteBean siteGet = siteService.getSite("testSite3");
    assertNotNull(siteGet);
  }
}
