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
package org.sakaiproject.kernel.test;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easymock.EasyMock;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sakaiproject.kernel.api.ComponentActivatorException;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.KernelManager;
import org.sakaiproject.kernel.api.UpdateFailedException;
import org.sakaiproject.kernel.api.authz.AuthzResolverService;
import org.sakaiproject.kernel.api.authz.SubjectPermissions;
import org.sakaiproject.kernel.api.authz.UserSubjects;
import org.sakaiproject.kernel.api.jcr.JCRService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
import org.sakaiproject.kernel.api.session.SessionManagerService;
import org.sakaiproject.kernel.api.user.User;
import org.sakaiproject.kernel.api.userenv.UserEnvironment;
import org.sakaiproject.kernel.api.userenv.UserEnvironmentResolverService;
import org.sakaiproject.kernel.jcr.jackrabbit.sakai.SakaiJCRCredentials;
import org.sakaiproject.kernel.model.UserEnvironmentBean;
import org.sakaiproject.kernel.session.SessionImpl;
import org.sakaiproject.kernel.util.PathUtils;
import org.sakaiproject.kernel.util.ResourceLoader;
import org.sakaiproject.kernel.webapp.test.InternalUser;

import java.io.IOException;
import java.io.InputStream;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Test the observation mechanism. With a sample user env and a sample group.
 */
public class ObservationTest extends KernelIntegrationBase {

  private static final Log LOG = LogFactory.getLog(ObservationTest.class);
  private static final String[] USERS = { "admin", "ib236" };
  private static final String TEST_USERENV = "res://org/sakaiproject/kernel/test/sampleuserenv/";
  private static final String TEST_GROUPENV = "res://org/sakaiproject/kernel/test/samplegroup/";
  private static final String[] GROUPS = { "group1" };

  @BeforeClass
  public static void beforeThisClass() throws ComponentActivatorException,
      RepositoryException, JCRNodeFactoryServiceException, IOException {
    KernelIntegrationBase.beforeClass();

    // get some services

    KernelManager km = new KernelManager();
    Kernel kernel = km.getKernel();
    AuthzResolverService authzResolverService = kernel
        .getService(AuthzResolverService.class);
    JCRNodeFactoryService jcrNodeFactoryService = kernel
        .getService(JCRNodeFactoryService.class);
    JCRService jcrService = kernel.getService(JCRService.class);

    // bypass security
    authzResolverService.setRequestGrant("Populating Test JSON");

    // login to the repo with super admin
    SakaiJCRCredentials credentials = new SakaiJCRCredentials();
    Session session = jcrService.getRepository().login(credentials);
    jcrService.setSession(session);

    // setup the user environment for the admin user.
    for (String userName : USERS) {
      String prefix = PathUtils.getUserPrefix(userName);
      String userEnvironmentPath = "/userenv" + prefix + "userenv";

      LOG.info("Saving " + userEnvironmentPath);
      jcrNodeFactoryService.createFile(userEnvironmentPath);
      InputStream in = ResourceLoader.openResource(TEST_USERENV + userName
          + ".json", ObservationTest.class.getClassLoader());
      jcrNodeFactoryService.setInputStream(userEnvironmentPath, in);
      session.save();
      in.close();
    }

    // add some group definitions in a random place, indexing should happen as a
    // result of events.
    for (String group : GROUPS) {
      String prefix = PathUtils.getUserPrefix(group);
      // imagine this is anywhere on the content system, probably with other
      // items related to the group
      String groupPath = "/somepath" + prefix + "groupdef.json";

      jcrNodeFactoryService.createFile(groupPath);
      LOG.info("Saving " + groupPath);
      InputStream in = ResourceLoader.openResource(TEST_GROUPENV + group
          + ".json", ObservationTest.class.getClassLoader());
      jcrNodeFactoryService.setInputStream(groupPath, in);
      session.save();
      in.close();
    }

    jcrService.logout();

    // clear the security bypass
    authzResolverService.clearRequestGrant();
  }

  @AfterClass
  public static void afterClass() {
    KernelIntegrationBase.afterClass();
  }

  @Test
  public void testUserEnv() throws JCRNodeFactoryServiceException,
      UpdateFailedException, AccessDeniedException, ItemExistsException,
      ConstraintViolationException, InvalidItemStateException,
      ReferentialIntegrityException, VersionException, LockException,
      NoSuchNodeTypeException, RepositoryException {

    LOG
        .info("Starting Test ==================================================== testCheck");
    KernelManager km = new KernelManager();
    UserEnvironmentResolverService userEnvironmentResolverService = km
        .getService(UserEnvironmentResolverService.class);
    assertNotNull(userEnvironmentResolverService);
    SessionManagerService sessionManagerService = km
        .getService(SessionManagerService.class);
    assertNotNull(sessionManagerService);
    
    HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
    HttpServletResponse response = EasyMock
        .createMock(HttpServletResponse.class);
    HttpSession session = EasyMock.createMock(HttpSession.class);

    setupRequest(request, response, session, "ib236");
    replay(request, response, session);
    startRequest(request, response, "JSESSION");

    UserEnvironment userEnvironment = userEnvironmentResolverService
        .resolve(sessionManagerService.getCurrentSession());
    assertNotNull(userEnvironment);
    assertEquals("ib236", userEnvironment.getUserid());
    assertFalse(userEnvironment.hasExpired());
    
    UserEnvironmentBean userEnvironmentBean = (UserEnvironmentBean) userEnvironment;
    String[] subjects = userEnvironmentBean.getSubjects();
    assertNotNull(subjects);
    assertEquals(4, subjects.length);
    
    UserSubjects subjectsBean = userEnvironmentBean.getUserSubjects();
    for ( String subject : subjects ) {
      SubjectPermissions sp = subjectsBean.getSubjectPermissions(subject);
      
      assertNotNull("Loading "+subject+" gave null",sp);
      assertEquals(subject, sp.getSubjectToken());
      
      assertFalse(sp.hasPermission("dummypermission"));
      if ( "group1:maintain".equals(subject) || "group1:access".equals(subject))  {
        assertTrue(subject+" is missing read",sp.hasPermission("read"));
      } else {
        assertFalse(subject+" should not have had read ",sp.hasPermission("read"));
      }
    }
    
    
    endRequest();
    verify(request, response, session);

    reset(request, response, session);

    setupRequest(request, response, session, "admin");
    replay(request, response, session);
    startRequest(request, response, "JSESSION");

    userEnvironment = userEnvironmentResolverService
        .resolve(sessionManagerService.getCurrentSession());
    assertNotNull(userEnvironment);
    assertEquals("admin", userEnvironment.getUserid());
    assertFalse(userEnvironment.hasExpired());
    userEnvironmentBean = (UserEnvironmentBean) userEnvironment;
    subjects = userEnvironmentBean.getSubjects();
    assertNotNull(subjects);
    assertEquals(0, subjects.length);

    endRequest();
    verify(request, response, session);

    LOG
        .info("Completed Test ==================================================== testCheck ");
  }

  /**
   * @param request
   * @param response
   * @param session
   */
  private void setupRequest(HttpServletRequest request,
      HttpServletResponse response, HttpSession session, String userName) {

    User u = new InternalUser(userName);
    expect(request.getSession()).andReturn(session).anyTimes();
    expect(request.getSession(true)).andReturn(session).anyTimes();
    expect(request.getSession(false)).andReturn(session).anyTimes();
    expect(session.getId()).andReturn(userName + "SESSIONID-123").anyTimes();
    expect(session.getAttribute(SessionImpl.USER)).andReturn(u).anyTimes();
  }

}
