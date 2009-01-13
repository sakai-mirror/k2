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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import net.sf.json.JSONObject;

import org.junit.Test;
import org.sakaiproject.kernel.api.RegistryService;
import org.sakaiproject.kernel.api.rest.RestProvider;
import org.sakaiproject.kernel.api.user.Authentication;
import org.sakaiproject.kernel.registry.RegistryServiceImpl;
import org.sakaiproject.kernel.rest.RestAuthenticationProvider;
import org.sakaiproject.kernel.user.AuthenticationImpl;
import org.sakaiproject.kernel.util.XmlUtils;
import org.sakaiproject.kernel.util.rest.RestDescription;
import org.sakaiproject.kernel.webapp.test.InternalUser;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 */
public class RestAuthenticationProviderTest {

  @Test
  public void testGet() throws ServletException, IOException {
    RegistryService registryService = new RegistryServiceImpl();
    HttpServletResponse response = createMock(HttpServletResponse.class);
    HttpServletRequest request = createMock(HttpServletRequest.class);
    expect(request.getMethod()).andReturn("GET").anyTimes();
    expect(request.getParameter("l")).andReturn("0").anyTimes();
    expect(request.getRemoteUser()).andReturn(null).anyTimes();
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    expect(response.getWriter()).andReturn(pw).anyTimes();
    response.setContentType(RestProvider.CONTENT_TYPE);
    expectLastCall().anyTimes();

    replay(request, response);

    RestAuthenticationProvider a = new RestAuthenticationProvider(
        registryService);
    a.dispatch(new String[0], request, response);

    assertEquals("{response: \"OK\"}", sw.toString());

    verify(request, response);
  }

  @Test
  public void testGetWithLogin() throws ServletException, IOException {
    RegistryService registryService = new RegistryServiceImpl();
    HttpServletResponse response = createMock(HttpServletResponse.class);
    HttpServletRequest request = createMock(HttpServletRequest.class);
    expect(request.getMethod()).andReturn("GET").anyTimes();

    expect(request.getParameter("l")).andReturn("1").anyTimes();
    AuthenticationImpl authN = new AuthenticationImpl(new InternalUser("ieb"));
    expect(request.getAttribute(Authentication.REQUESTTOKEN)).andReturn(authN);

    expect(request.getRemoteUser()).andReturn(null).anyTimes();
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    expect(response.getWriter()).andReturn(pw).anyTimes();

    response.setContentType(RestProvider.CONTENT_TYPE);
    expectLastCall().anyTimes();

    replay(request, response);
    RestAuthenticationProvider a = new RestAuthenticationProvider(
        registryService);
    a.dispatch(new String[0], request, response);

    assertEquals("{response: \"OK\"}", sw.toString());

    verify(request, response);
  }

  @Test
  public void testGetWithFailedLogin() throws ServletException, IOException {
    RegistryService registryService = new RegistryServiceImpl();
    HttpServletResponse response = createMock(HttpServletResponse.class);
    HttpServletRequest request = createMock(HttpServletRequest.class);
    expect(request.getMethod()).andReturn("GET").anyTimes();

    expect(request.getParameter("l")).andReturn("1").anyTimes();
    expect(request.getAttribute(Authentication.REQUESTTOKEN)).andReturn(null);
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);

    replay(request, response);
    RestAuthenticationProvider a = new RestAuthenticationProvider(
        registryService);
    a.dispatch(new String[0], request, response);

    verify(request, response);
  }

  @Test
  public void testPost() throws ServletException, IOException {
    RegistryService registryService = new RegistryServiceImpl();
    HttpServletResponse response = createMock(HttpServletResponse.class);
    HttpServletRequest request = createMock(HttpServletRequest.class);
    expect(request.getMethod()).andReturn("POST").anyTimes();
    expect(request.getParameter("l")).andReturn("0").anyTimes();
    expect(request.getRemoteUser()).andReturn(null).anyTimes();
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    expect(response.getWriter()).andReturn(pw).anyTimes();
    response.setContentType(RestProvider.CONTENT_TYPE);
    expectLastCall().anyTimes();

    replay(request, response);
    RestAuthenticationProvider a = new RestAuthenticationProvider(
        registryService);
    a.dispatch(new String[0], request, response);

    assertEquals("{response: \"OK\"}", sw.toString());

    verify(request, response);
  }

  @Test
  public void testPostWithLogin() throws ServletException, IOException {
    RegistryService registryService = new RegistryServiceImpl();
    HttpServletResponse response = createMock(HttpServletResponse.class);
    HttpServletRequest request = createMock(HttpServletRequest.class);
    expect(request.getMethod()).andReturn("POST").anyTimes();

    expect(request.getParameter("l")).andReturn("1").anyTimes();
    AuthenticationImpl authN = new AuthenticationImpl(new InternalUser("ieb"));
    expect(request.getAttribute(Authentication.REQUESTTOKEN)).andReturn(authN);

    expect(request.getRemoteUser()).andReturn(null).anyTimes();
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    expect(response.getWriter()).andReturn(pw).anyTimes();
    response.setContentType(RestProvider.CONTENT_TYPE);
    expectLastCall().anyTimes();

    replay(request, response);
    RestAuthenticationProvider a = new RestAuthenticationProvider(
        registryService);
    a.dispatch(new String[0], request, response);

    assertEquals("{response: \"OK\"}", sw.toString());

    verify(request, response);
  }

  @Test
  public void testPostWithFailedLogin() throws ServletException, IOException {
    RegistryService registryService = new RegistryServiceImpl();
    HttpServletResponse response = createMock(HttpServletResponse.class);
    HttpServletRequest request = createMock(HttpServletRequest.class);
    expect(request.getMethod()).andReturn("POST").anyTimes();

    expect(request.getParameter("l")).andReturn("1").anyTimes();
    expect(request.getAttribute(Authentication.REQUESTTOKEN)).andReturn(null);
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);

    replay(request, response);
    RestAuthenticationProvider a = new RestAuthenticationProvider(
        registryService);
    a.dispatch(new String[0], request, response);

    verify(request, response);
  }

  @Test
  public void testDescription() throws Exception {
    RegistryService registryService = new RegistryServiceImpl();
    RestAuthenticationProvider a = new RestAuthenticationProvider(
        registryService);
    RestDescription description = a.getDescription();

    XmlUtils.parse(description.toXml());
    System.err.println(description.toHtml());
    XmlUtils.parse(description.toHtml());
    // validate the json
    JSONObject.fromObject(description.toJson());
  }
}
