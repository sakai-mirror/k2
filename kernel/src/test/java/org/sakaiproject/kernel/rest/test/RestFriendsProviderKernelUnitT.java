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
package org.sakaiproject.kernel.rest.test;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sakaiproject.kernel.Activator;
import org.sakaiproject.kernel.api.ComponentActivatorException;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
import org.sakaiproject.kernel.api.rest.RestProvider;
import org.sakaiproject.kernel.api.serialization.BeanConverter;
import org.sakaiproject.kernel.rest.RestFriendsProvider;
import org.sakaiproject.kernel.test.KernelIntegrationBase;
import org.sakaiproject.kernel.user.UserFactoryService;
import org.sakaiproject.kernel.util.StringUtils;
import org.sakaiproject.kernel.webapp.RestServiceFaultException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

/**
 * Unit tests for the RestSiteProvider
 */
public class RestFriendsProviderKernelUnitT extends BaseRestUnitT {

  private static final String PRIVATE_BASE_PATH = "/_private";
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

  private UserFactoryService userFactoryService;

  /**
   * Test a bad request
   * 
   * @throws ServletException
   * @throws IOException
   * @throws JCRNodeFactoryServiceException
   * @throws RepositoryException
   */
  @Test
  public void testBadRequestConnection() throws ServletException, IOException,
      RepositoryException, JCRNodeFactoryServiceException {
    setupServices();

    {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      setupAnyTimes("user1", "SESSION-21312312", baos);

      // expect(request.getParameter("friendUuid")).andReturn(null);
      // expect(request.getParameter("message")).andReturn(null);

      replayMocks();

      String[] elements = new String[] { "friend", "bad", "request" };

      RestFriendsProvider rsp = new RestFriendsProvider(registryService,
          jcrNodeFactoryService, sessionManagerService,
          userEnvironmentResolverService, userFactoryService, injector
              .getInstance(Key.get(BeanConverter.class, Names
                  .named(BeanConverter.REPOSITORY_BEANCONVETER))),
          PRIVATE_BASE_PATH);
      try {
        rsp.dispatch(elements, request, response);
        fail();
      } catch (RestServiceFaultException ex) {
        assertEquals(ex.getStatusCode(), HttpServletResponse.SC_BAD_REQUEST);
      }
      verifyMocks();
    }
    {
      resetMocks();
      // wong path,
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      setupAnyTimes("user1", "SESSION-21312312", baos);

      // expect(request.getParameter("friendUuid")).andReturn(null);
      // expect(request.getParameter("message")).andReturn(null);

      replayMocks();

      String[] elements = new String[] { "friend", "connect" };

      RestFriendsProvider rsp = new RestFriendsProvider(registryService,
          jcrNodeFactoryService, sessionManagerService,
          userEnvironmentResolverService, userFactoryService, injector
              .getInstance(Key.get(BeanConverter.class, Names
                  .named(BeanConverter.REPOSITORY_BEANCONVETER))),
          PRIVATE_BASE_PATH);
      try {
        rsp.dispatch(elements, request, response);
        fail();
      } catch (RestServiceFaultException ex) {
        assertEquals(ex.getStatusCode(), HttpServletResponse.SC_BAD_REQUEST);
      }
      verifyMocks();
    }
    {
      resetMocks();
      // wong path,
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      setupAnyTimes("user1", "SESSION-21312312", baos);

      // expect(request.getParameter("friendUuid")).andReturn(null);
      // expect(request.getParameter("message")).andReturn(null);

      replayMocks();

      String[] elements = new String[] { "friend", "connect", "badpathelement" };

      RestFriendsProvider rsp = new RestFriendsProvider(registryService,
          jcrNodeFactoryService, sessionManagerService,
          userEnvironmentResolverService, userFactoryService, injector
              .getInstance(Key.get(BeanConverter.class, Names
                  .named(BeanConverter.REPOSITORY_BEANCONVETER))),
          PRIVATE_BASE_PATH);
      try {
        rsp.dispatch(elements, request, response);
        fail();
      } catch (RestServiceFaultException ex) {
        assertEquals(ex.getStatusCode(), HttpServletResponse.SC_BAD_REQUEST);
      }
      verifyMocks();
    }
    {
      resetMocks();
      // wong path, too short
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      setupAnyTimes("user1", "SESSION-21312312", baos);

      // expect(request.getParameter("friendUuid")).andReturn(null);
      // expect(request.getParameter("message")).andReturn(null);

      replayMocks();

      String[] elements = new String[] { "friend" };

      RestFriendsProvider rsp = new RestFriendsProvider(registryService,
          jcrNodeFactoryService, sessionManagerService,
          userEnvironmentResolverService, userFactoryService, injector
              .getInstance(Key.get(BeanConverter.class, Names
                  .named(BeanConverter.REPOSITORY_BEANCONVETER))),
          PRIVATE_BASE_PATH);
      try {
        rsp.dispatch(elements, request, response);
        fail();
      } catch (RestServiceFaultException ex) {
        assertEquals(ex.getStatusCode(), HttpServletResponse.SC_BAD_REQUEST);
      }
      verifyMocks();
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.rest.test.BaseRestUnitT#setupServices()
   */
  @Override
  public void setupServices() {
    super.setupServices();
    userFactoryService = injector.getInstance(UserFactoryService.class);
  }

  /**
   * Test a bad request for a non admin user
   * 
   * @throws ServletException
   * @throws IOException
   * @throws JCRNodeFactoryServiceException
   * @throws RepositoryException
   */
  @Test
  public void testNonAdminRequestOtherConnection() throws ServletException,
      IOException, RepositoryException, JCRNodeFactoryServiceException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("user1", "SESSION-21312312", baos);

    // expect(request.getParameter("friendUuid")).andReturn("myfriend");
    // expect(request.getParameter("message")).andReturn("Hi");

    replayMocks();

    String[] elements = new String[] { "friend", "connect", "request",
        "frienduserid" };

    RestFriendsProvider rsp = new RestFriendsProvider(registryService,
        jcrNodeFactoryService, sessionManagerService,
        userEnvironmentResolverService, userFactoryService, injector
            .getInstance(Key.get(BeanConverter.class, Names
                .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        PRIVATE_BASE_PATH);
    try {
      rsp.dispatch(elements, request, response);
      fail();
    } catch (RestServiceFaultException ex) {
      assertEquals(ex.getStatusCode(), HttpServletResponse.SC_FORBIDDEN);
    }
    verifyMocks();
  }

  /**
   * Test a bad request for a non admin user
   * 
   * @throws ServletException
   * @throws IOException
   * @throws JCRNodeFactoryServiceException
   * @throws RepositoryException
   */
  @Test
  public void testAdminRequestOtherConnection() throws ServletException,
      IOException, RepositoryException, JCRNodeFactoryServiceException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("admin", "SESSION-2131sasa", baos);
    expect(request.getMethod()).andReturn("POST");

    expect(request.getParameter("friendUuid")).andReturn("MyFriend");
    expect(request.getParameter("friendType")).andReturn(null);
    expect(request.getParameter("message")).andReturn("hi");
    response.setContentType(RestProvider.CONTENT_TYPE);
    expectLastCall();

    replayMocks();

    String[] elements = new String[] { "friend", "connect", "request",
        "frienduserid" };

    RestFriendsProvider rsp = new RestFriendsProvider(registryService,
        jcrNodeFactoryService, sessionManagerService,
        userEnvironmentResolverService, userFactoryService, injector
            .getInstance(Key.get(BeanConverter.class, Names
                .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        PRIVATE_BASE_PATH);
    rsp.dispatch(elements, request, response);

    String op = baos.toString(StringUtils.UTF8);
    assertEquals("{\"response\":\"OK\"}", op);

    verifyMocks();
  }

  /**
   * Connect to the user
   * 
   * @throws ServletException
   * @throws IOException
   * @throws JCRNodeFactoryServiceException
   * @throws RepositoryException
   */
  @Test
  public void testRequestConnection() throws ServletException, IOException,
      RepositoryException, JCRNodeFactoryServiceException {
    setupServices();

    RestFriendsProvider rsp = new RestFriendsProvider(registryService,
        jcrNodeFactoryService, sessionManagerService,
        userEnvironmentResolverService, userFactoryService, injector
            .getInstance(Key.get(BeanConverter.class, Names
                .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        PRIVATE_BASE_PATH);

    // request a connection
    connect(rsp, "user2", "SESSION-2131asdassdfsdfaqwe", "request", "user1",
        "hi");

    // check that user2 has accepted
    checkFriend(rsp, "user2", "SESSION-2131asdasfaqwe",
        new String[] { "user1" }, new String[] { "PENDING" });

    // check that user1 has accepted
    checkFriend(rsp, "user1", "SESSION-2131asdassdfsdfaqwe",
        new String[] { "user2" }, new String[] { "INVITED" });

    // user 1 confirms

    connect(rsp, "user1", "SESSION-2131asdassdfsdfaqwe", "accept", "user2",
        null);

    // check that user2 has accepted
    checkFriend(rsp, "user2", "SESSION-2131asdasfaqwe",
        new String[] { "user1" }, new String[] { "ACCEPTED" });

    // check that user1 has accepted
    checkFriend(rsp, "user1", "SESSION-2131asdassdfsdfaqwe",
        new String[] { "user2" }, new String[] { "ACCEPTED" });

    // try to reinvite both should fail
    try {
      connect(rsp, "user1", "SESSION-2131asdassdfsdfaqwe", "request", "user2",
          "hi");
      fail();
    } catch (RestServiceFaultException ex) {
      assertEquals(HttpServletResponse.SC_CONFLICT, ex.getStatusCode());
    }

    try {
      connect(rsp, "user2", "SESSION-2131asdasfaqwe", "request", "user1", "hi");
      fail();
    } catch (RestServiceFaultException ex) {
      assertEquals(HttpServletResponse.SC_CONFLICT, ex.getStatusCode());
    }

    // user 1 removes
    connect(rsp, "user1", "SESSION-2131asdassdfsdfaqwe", "remove", "user2",
        null);

    // user 2 no mates
    checkFriend(rsp, "user2", "SESSION-2131asdasfaqwe", new String[] {},
        new String[] {});

    // user 1 no mates
    checkFriend(rsp, "user1", "SESSION-2131asdassdfsdfaqwe", new String[] {},
        new String[] {});

  }

  /**
   * Connect to the user
   * 
   * @throws ServletException
   * @throws IOException
   * @throws JCRNodeFactoryServiceException
   * @throws RepositoryException
   */
  @Test
  public void testRequestRejectConnection() throws ServletException,
      IOException, RepositoryException, JCRNodeFactoryServiceException {
    setupServices();

    RestFriendsProvider rsp = new RestFriendsProvider(registryService,
        jcrNodeFactoryService, sessionManagerService,
        userEnvironmentResolverService, userFactoryService, injector
            .getInstance(Key.get(BeanConverter.class, Names
                .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        PRIVATE_BASE_PATH);

    // request a connection
    connect(rsp, "user2", "SESSION-2131asdassdfsdfaqwe", "request", "user1",
        "hi");

    // check that user2 has accepted
    checkFriend(rsp, "user2", "SESSION-2131asdasfaqwe",
        new String[] { "user1" }, new String[] { "PENDING" });

    // check that user1 has accepted
    checkFriend(rsp, "user1", "SESSION-2131asdassdfsdfaqwe",
        new String[] { "user2" }, new String[] { "INVITED" });

    // user 1 rejects

    connect(rsp, "user1", "SESSION-2131asdassdfsdfaqwe", "reject", "user2",
        null);

    // check that the friend has gone
    checkFriend(rsp, "user2", "SESSION-2131asdasfaqwe", new String[] {},
        new String[] {});

    // check that the friend has gone
    checkFriend(rsp, "user1", "SESSION-2131asdassdfsdfaqwe", new String[] {},
        new String[] {});

    // user 1 removes
    try {
      connect(rsp, "user1", "SESSION-2131asdassdfsdfaqwe", "remove", "user2",
          null);
      fail();
    } catch (RestServiceFaultException ex) {
      assertEquals(HttpServletResponse.SC_NOT_FOUND, ex.getStatusCode());
    }

  }

  /**
   * Connect to the user
   * 
   * @throws ServletException
   * @throws IOException
   * @throws JCRNodeFactoryServiceException
   * @throws RepositoryException
   */
  @Test
  public void testRequestIgnoreConnection() throws ServletException,
      IOException, RepositoryException, JCRNodeFactoryServiceException {
    setupServices();

    RestFriendsProvider rsp = new RestFriendsProvider(registryService,
        jcrNodeFactoryService, sessionManagerService,
        userEnvironmentResolverService, userFactoryService, injector
            .getInstance(Key.get(BeanConverter.class, Names
                .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        PRIVATE_BASE_PATH);

    // request a connection
    connect(rsp, "user2", "SESSION-2131asdassdfsdfaqwe", "request", "user1",
        "hi");

    // check that user2 has accepted
    checkFriend(rsp, "user2", "SESSION-2131asdasfaqwe",
        new String[] { "user1" }, new String[] { "PENDING" });

    // check that user1 has accepted
    checkFriend(rsp, "user1", "SESSION-2131asdassdfsdfaqwe",
        new String[] { "user2" }, new String[] { "INVITED" });

    // user 1 rejects

    connect(rsp, "user1", "SESSION-2131asdassdfsdfaqwe", "ignore", "user2",
        null);

    // check that the friend has gone
    checkFriend(rsp, "user2", "SESSION-2131asdasfaqwe", new String[] {},
        new String[] {});

    // check that the friend has gone
    checkFriend(rsp, "user1", "SESSION-2131asdassdfsdfaqwe", new String[] {},
        new String[] {});

    // user 1 removes
    try {
      connect(rsp, "user1", "SESSION-2131asdassdfsdfaqwe", "remove", "user2",
          null);
      fail();
    } catch (RestServiceFaultException ex) {
      assertEquals(HttpServletResponse.SC_NOT_FOUND, ex.getStatusCode());
    }

  }

  /**
   * Connect to the user
   * 
   * @throws ServletException
   * @throws IOException
   * @throws JCRNodeFactoryServiceException
   * @throws RepositoryException
   */
  @Test
  public void testRequestCancelConnection() throws ServletException,
      IOException, RepositoryException, JCRNodeFactoryServiceException {
    setupServices();

    RestFriendsProvider rsp = new RestFriendsProvider(registryService,
        jcrNodeFactoryService, sessionManagerService,
        userEnvironmentResolverService, userFactoryService, injector
            .getInstance(Key.get(BeanConverter.class, Names
                .named(BeanConverter.REPOSITORY_BEANCONVETER))),
        PRIVATE_BASE_PATH);

    // request a connection
    connect(rsp, "user2", "SESSION-2131asdassdfsdfaqwe", "request", "user1",
        "hi");

    // check that user2 has accepted
    checkFriend(rsp, "user2", "SESSION-2131asdasfaqwe",
        new String[] { "user1" }, new String[] { "PENDING" });

    // check that user1 has accepted
    checkFriend(rsp, "user1", "SESSION-2131asdassdfsdfaqwe",
        new String[] { "user2" }, new String[] { "INVITED" });

    // user 1 cancel, not allowed
    try {
      connect(rsp, "user1", "SESSION-2131asdassdfsdfaqwe", "cancel", "user2",
          null);
      fail();
    } catch (RestServiceFaultException ex) {
      assertEquals(HttpServletResponse.SC_CONFLICT, ex.getStatusCode());
    }
    // user 2 is not allowed to accept, reject or ignore
    try {
      connect(rsp, "user2", "SESSION-2131asdassdfsdfaqwe", "accept", "user2",
          null);
      fail();
    } catch (RestServiceFaultException ex) {
      assertEquals(HttpServletResponse.SC_NOT_FOUND, ex.getStatusCode());
    }
    // user 2 is not allowed to accept, reject or ignore
    try {
      connect(rsp, "user2", "SESSION-2131asdassdfsdfaqwe", "reject", "user2",
          null);
      fail();
    } catch (RestServiceFaultException ex) {
      assertEquals(HttpServletResponse.SC_NOT_FOUND, ex.getStatusCode());
    }
    // user 2 is not allowed to accept, reject or ignore
    try {
      connect(rsp, "user2", "SESSION-2131asdassdfsdfaqwe", "ignore", "user2",
          null);
      fail();
    } catch (RestServiceFaultException ex) {
      assertEquals(HttpServletResponse.SC_NOT_FOUND, ex.getStatusCode());
    }

    // cancel
    connect(rsp, "user2", "SESSION-2131asdassdfsdfaqwe", "cancel", "user1",
        null);

    // check that the friend has gone
    checkFriend(rsp, "user2", "SESSION-2131asdasfaqwe", new String[] {},
        new String[] {});

    // check that the friend has gone
    checkFriend(rsp, "user1", "SESSION-2131asdassdfsdfaqwe", new String[] {},
        new String[] {});

    // all the following will fail, no connection
    try {
      connect(rsp, "user1", "SESSION-2131asdassdfsdfaqwe", "cancel", "user2",
          null);
      fail();
    } catch (RestServiceFaultException ex) {
      assertEquals(HttpServletResponse.SC_NOT_FOUND, ex.getStatusCode());
    }
    try {
      connect(rsp, "user1", "SESSION-2131asdassdfsdfaqwe", "reject", "user2",
          null);
      fail();
    } catch (RestServiceFaultException ex) {
      assertEquals(HttpServletResponse.SC_NOT_FOUND, ex.getStatusCode());
    }
    try {
      connect(rsp, "user1", "SESSION-2131asdassdfsdfaqwe", "accept", "user2",
          null);
      fail();
    } catch (RestServiceFaultException ex) {
      assertEquals(HttpServletResponse.SC_NOT_FOUND, ex.getStatusCode());
    }
    // user 1 removes
    try {
      connect(rsp, "user1", "SESSION-2131asdassdfsdfaqwe", "remove", "user2",
          null);
      fail();
    } catch (RestServiceFaultException ex) {
      assertEquals(HttpServletResponse.SC_NOT_FOUND, ex.getStatusCode());
    }

  }

  /**
   * @param rsp
   * @param string
   * @param string2
   * @param string3
   * @throws IOException
   */
  private void connect(RestFriendsProvider rsp, String user, String session,
      String action, String friend, String message) throws IOException {
    resetMocks();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes(user, session, baos);
    expect(request.getMethod()).andReturn("POST");

    expect(request.getParameter("friendUuid")).andReturn(friend);
    expect(request.getParameter("friendType")).andReturn("distant");
    expect(request.getParameter("message")).andReturn(message);
    response.setContentType(RestProvider.CONTENT_TYPE);
    expectLastCall();

    replayMocks();

    String[] elements = new String[] { "friend", "connect", action };

    rsp.dispatch(elements, request, response);

    String op = baos.toString(StringUtils.UTF8);
    assertEquals("{\"response\":\"OK\"}", op);

    verifyMocks();
  }

  /**
   * @param string
   * @param string2
   * @param strings
   * @param strings2
   * @throws IOException
   */
  private void checkFriend(RestFriendsProvider rsp, String user,
      String session, String[] friendUuids, String[] friendStatus)
      throws IOException {
    resetMocks();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes(user, session, baos);
    expect(request.getMethod()).andReturn("GET");

    expect(request.getParameter("friendUuid")).andReturn(null);
    expect(request.getParameter("friendType")).andReturn(null);
    expect(request.getParameter("message")).andReturn(null);
    response.setContentType(RestProvider.CONTENT_TYPE);
    expectLastCall();

    replayMocks();

    String[] elements = new String[] { "friend", "status" };

    rsp.dispatch(elements, request, response);

    String op = baos.toString(StringUtils.UTF8);
    JSONObject obj = JSONObject.fromObject(op);
    assertEquals("OK", obj.get("response"));
    JSONObject status = obj.getJSONObject("status");
    assertEquals(user, status.get("uuid"));
    if (friendUuids.length == 0) {
      assertTrue(!status.has("friends"));
    } else {
      JSONArray friends = status.getJSONArray("friends");
      assertEquals(friendUuids.length, friends.size());
      for (int i = 0; i < friendUuids.length; i++) {
        JSONObject friend = friends.getJSONObject(0);
        assertEquals(friendUuids[i], friend.get("friendUuid"));
        assertEquals(user, friend.get("personUuid"));
        assertEquals(friendStatus[i], friend.get("status"));
      }
    }

    verifyMocks();
  }

}
