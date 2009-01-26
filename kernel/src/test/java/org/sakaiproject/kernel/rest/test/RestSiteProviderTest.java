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
import static org.junit.Assert.assertEquals;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.name.Names;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sakaiproject.kernel.Activator;
import org.sakaiproject.kernel.api.ComponentActivatorException;
import org.sakaiproject.kernel.api.KernelManager;
import org.sakaiproject.kernel.api.RegistryService;
import org.sakaiproject.kernel.api.serialization.BeanConverter;
import org.sakaiproject.kernel.api.site.SiteService;
import org.sakaiproject.kernel.model.SiteBean;
import org.sakaiproject.kernel.registry.RegistryServiceImpl;
import org.sakaiproject.kernel.rest.RestSiteProvider;
import org.sakaiproject.kernel.test.KernelIntegrationBase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 */
public class RestSiteProviderTest {

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

  @Test
  public void testCheckId() {
  }

  @Test
  public void testCreateAlreadyExists() throws ServletException, IOException {
    KernelManager km = new KernelManager();

    RegistryService registryService = km.getService(RegistryService.class);

    // we will mock this up
    SiteService siteService = createMock(SiteService.class);

    HttpServletRequest request = createMock(HttpServletRequest.class);
    HttpServletResponse response = createMock(HttpServletResponse.class);

    expect(request.getMethod()).andReturn("POST").anyTimes();
    expect(request.getParameter("id")).andReturn("sitethatexists");
    expect(siteService.siteExists("sitethatexists")).andReturn(true);
    response.reset();
    expectLastCall();
    response.sendError(409,
        "{\"response\": \"Site ID [sitethatexists] exists.\"}");
    expectLastCall();

    replay(request, response, siteService);
    String[] elements = new String[] { "site", "create" };

    RestSiteProvider rsp = new RestSiteProvider(registryService, siteService,
        injector.getInstance(Key.get(BeanConverter.class, Names
            .named(BeanConverter.REPOSITORY_BEANCONVETER))));
    rsp.dispatch(elements, request, response);

    verify(request, response, siteService);
  }

  @Test
  public void testCreate() throws ServletException, IOException {
    RegistryService registryService = new RegistryServiceImpl();
    SiteService siteService = createMock(SiteService.class);
    HttpServletRequest request = createMock(HttpServletRequest.class);
    HttpServletResponse response = createMock(HttpServletResponse.class);

    expect(request.getMethod()).andReturn("POST").anyTimes();
    expect(request.getParameter("id")).andReturn("sitethatdoesnotexist");
    expect(siteService.siteExists("sitethatdoesnotexist")).andReturn(false);
    expect(request.getParameter("name")).andReturn("Name:sitethatdoesnotexist");
    expect(request.getParameter("description")).andReturn(
        "Description:sitethatdoesnotexist");
    expect(request.getParameter("type")).andReturn("Type:sitethatdoesnotexist");
    siteService.createSite((SiteBean) anyObject());
    expectLastCall();

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    ServletOutputStream out = new ServletOutputStream() {

      @Override
      public void write(int b) throws IOException {
        baos.write(b);
      }

    };

    expect(response.getOutputStream()).andReturn(out).anyTimes();

    replay(request, response, siteService);
    String[] elements = new String[] { "site", "create" };

    RestSiteProvider rsp = new RestSiteProvider(registryService, siteService,
        injector.getInstance(Key.get(BeanConverter.class, Names
            .named(BeanConverter.REPOSITORY_BEANCONVETER))));
    rsp.dispatch(elements, request, response);

    String body = new String(baos.toByteArray(), "UTF-8");
    assertEquals("{\"response\": \"OK\"}", body);

    verify(request, response, siteService);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testCheckIDExists() throws ServletException, IOException {
    RegistryService registryService = new RegistryServiceImpl();
    SiteService siteService = createMock(SiteService.class);
    HttpServletRequest request = createMock(HttpServletRequest.class);
    HttpServletResponse response = createMock(HttpServletResponse.class);

    expect(request.getParameter("id")).andReturn("sitethatexists");
    expect(siteService.siteExists("sitethatexists")).andReturn(true);

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    ServletOutputStream out = new ServletOutputStream() {

      @Override
      public void write(int b) throws IOException {
        baos.write(b);
      }

    };

    expect(response.getOutputStream()).andReturn(out).anyTimes();

    replay(request, response, siteService);
    String[] elements = new String[] { "site", "checkId" };

    RestSiteProvider rsp = new RestSiteProvider(registryService, siteService,
        injector.getInstance(Key.get(BeanConverter.class, Names
            .named(BeanConverter.REPOSITORY_BEANCONVETER))));
    rsp.dispatch(elements, request, response);

    String body = new String(baos.toByteArray(), "UTF-8");
    assertEquals("{\"response\": \"OK\"}", body);

    verify(request, response, siteService);
  }

  @Test
  public void testCheckIDDoesNotExists() throws ServletException, IOException {
    RegistryService registryService = new RegistryServiceImpl();
    SiteService siteService = createMock(SiteService.class);
    HttpServletRequest request = createMock(HttpServletRequest.class);
    HttpServletResponse response = createMock(HttpServletResponse.class);

    expect(request.getParameter("id")).andReturn("sitethatdoesnotexist");
    expect(siteService.siteExists("sitethatdoesnotexist")).andReturn(false);
    response.reset();
    expectLastCall();
    response.sendError(404);
    expectLastCall();

    replay(request, response, siteService);
    String[] elements = new String[] { "site", "checkId" };

    RestSiteProvider rsp = new RestSiteProvider(registryService, siteService,
        injector.getInstance(Key.get(BeanConverter.class, Names
            .named(BeanConverter.REPOSITORY_BEANCONVETER))));
    rsp.dispatch(elements, request, response);

    verify(request, response, siteService);
  }

}
