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
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sakaiproject.kernel.webapp.SakaiServletResponse;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 */
public class SakaiServletResponseTest {

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

  @Test
  public void testSetSessionCookie() {
    HttpServletResponse response = createMock(HttpServletResponse.class);
    Cookie cookie = new Cookie("JSESSIONID","1");
    cookie.setPath("/a/longer/path");
    response.addCookie(cookie);
    replay(response);

    SakaiServletResponse sresponse = new SakaiServletResponse(response,"JSESSIONID");
    sresponse.addCookie(cookie);
    Assert.assertEquals("/", cookie.getPath());
    verify(response);
    
    
  }

  @Test
  public void testSetNonSessionCookie() {
    HttpServletResponse response = createMock(HttpServletResponse.class);
    Cookie cookie = new Cookie("Someting","1");
    cookie.setPath("/a/longer/path");
    response.addCookie(cookie);
    replay(response);

    SakaiServletResponse sresponse = new SakaiServletResponse(response,"JSESSIONID");
    sresponse.addCookie(cookie);
    Assert.assertEquals("/a/longer/path", cookie.getPath());
    verify(response);
  }

 
}
