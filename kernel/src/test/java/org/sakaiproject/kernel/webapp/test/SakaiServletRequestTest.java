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
package org.sakaiproject.kernel.webapp.test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sakaiproject.kernel.api.session.Session;
import org.sakaiproject.kernel.api.user.User;
import org.sakaiproject.kernel.session.SessionImpl;
import org.sakaiproject.kernel.webapp.SakaiServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 
 */
public class SakaiServletRequestTest {

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  /**
   * Test method for
   * {@link org.sakaiproject.kernel.webapp.SakaiServletRequest#SakaiServletRequest(javax.servlet.ServletRequest)}
   * .
   */
  @Test
  public void testSakaiServletRequest() {
  }

  /**
   * Test method for
   * {@link org.sakaiproject.kernel.webapp.SakaiServletRequest#getSession()}.
   */
  @Test
  public void testGetRemoteUserNone() {
    HttpServletRequest request = createMock(HttpServletRequest.class);

    expect(request.getRemoteUser()).andReturn(null);
    expect(request.getSession(false)).andReturn(null);
    replay(request);

    SakaiServletRequest srequest = new SakaiServletRequest(request);
    assertNull(srequest.getRemoteUser());
    verify(request);
  }

  @Test
  public void testGetRemoteUserFromRequest() {
    HttpServletRequest request = createMock(HttpServletRequest.class);

    expect(request.getRemoteUser()).andReturn("ieb");
    replay(request);

    SakaiServletRequest srequest = new SakaiServletRequest(request);
    assertEquals("ieb", srequest.getRemoteUser());
    verify(request);
    reset(request);
  }

  @Test
  public void testGetRemoteUserFromSession() {
    HttpServletRequest request = createMock(HttpServletRequest.class);
    User user = new InternalUser("ieb2");
    HttpSession session = createMock(HttpSession.class);
    expect(request.getRemoteUser()).andReturn("");
    expect(request.getSession(false)).andReturn(session);
    expect(session.getAttribute(SessionImpl.USER)).andReturn(user);
    replay(request,session);

    SakaiServletRequest srequest = new SakaiServletRequest(request);
    assertEquals("ieb2", srequest.getRemoteUser());
    verify(request,session);

  }

  /**
   * Test method for
   * {@link org.sakaiproject.kernel.webapp.SakaiServletRequest#getSession()}
   * .
   */
  @Test
  public void testGetSession() {
    HttpServletRequest request = createMock(HttpServletRequest.class);

    HttpSession session = createMock(HttpSession.class);
    expect(request.getSession()).andReturn(session);
    replay(request,session);

    SakaiServletRequest srequest = new SakaiServletRequest(request);
    assertTrue(srequest.getSession() instanceof SessionImpl);
    verify(request,session);
  }

  /**
   * Test method for
   * {@link org.sakaiproject.kernel.webapp.SakaiServletRequest#getSakaiSession()}
   * .
   */
  @Test
  public void testGetSakaiSessionTrue() {
    HttpServletRequest request = createMock(HttpServletRequest.class);

    HttpSession session = createMock(HttpSession.class);
    expect(request.getSession(true)).andReturn(session);
    replay(request,session);

    SakaiServletRequest srequest = new SakaiServletRequest(request);
    assertTrue(srequest.getSession(true) instanceof SessionImpl);
    verify(request,session);
  }

  /**
   * Test method for
   * {@link org.sakaiproject.kernel.webapp.SakaiServletRequest#getSakaiSession()}
   * .
   */
  @Test
  public void testGetSakaiSessionFalse() {
    HttpServletRequest request = createMock(HttpServletRequest.class);

    HttpSession session = createMock(HttpSession.class);
    expect(request.getSession(false)).andReturn(null);
    replay(request,session);

    SakaiServletRequest srequest = new SakaiServletRequest(request);
    assertNull(srequest.getSession(false));
    verify(request,session);
  }
  /**
   * Test method for
   * {@link org.sakaiproject.kernel.webapp.SakaiServletRequest#getSakaiSession()}
   * .
   */
  @Test
  public void testGetSakaiSession() {
    HttpServletRequest request = createMock(HttpServletRequest.class);

    HttpSession session = createMock(HttpSession.class);
    expect(request.getSession(true)).andReturn(session);
    replay(request,session);

    SakaiServletRequest srequest = new SakaiServletRequest(request);
    Session s = srequest.getSakaiSession();
    assertTrue(srequest.getSession(true) instanceof SessionImpl);
    assertEquals(s, srequest.getSession());
    verify(request,session);
  }

}
