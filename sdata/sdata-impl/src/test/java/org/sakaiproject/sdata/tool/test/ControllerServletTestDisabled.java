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
package org.sakaiproject.sdata.tool.test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.KernelManager;
import org.sakaiproject.kernel.component.KernelLifecycle;
import org.sakaiproject.kernel.component.core.KernelBootstrapModule;
import org.sakaiproject.kernel.util.ResourceLoader;
import org.sakaiproject.sdata.tool.ControllerServlet;
import org.sakaiproject.sdata.tool.JCRHandler;
import org.sakaiproject.sdata.tool.JCRUserStorageHandler;
import org.sakaiproject.sdata.tool.api.Handler;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 */
public class ControllerServletTestDisabled {

  private static KernelLifecycle kl;

  @BeforeClass
  public static void beforeClass() throws IOException {
    assertNotNull(ControllerServletTestDisabled.class.getClassLoader()
        .getResourceAsStream("kernel-component.properties"));

    @SuppressWarnings("unused")
    String s = ResourceLoader.readResource("res://kernel-component.properties",
        ControllerServletTestDisabled.class.getClassLoader());
    System.setProperty(KernelBootstrapModule.SYS_LOCAL_PROPERTIES,
        "res://integration-kernel.properties");
    kl = new KernelLifecycle();
    kl.start();

    KernelManager km = new KernelManager();
    @SuppressWarnings("unused")
    Kernel k = km.getKernel();

  }

  @AfterClass
  public static void afterClass() {
    if (kl != null) {
      kl.stop();
    }
    System.clearProperty(KernelBootstrapModule.SYS_LOCAL_PROPERTIES);
  }

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

  @Test
  public void testServletInit() throws ServletException {
    ServletConfig config = createMock(ServletConfig.class);

    replay(config);
    ControllerServlet controllerServlet = new ControllerServlet();
    controllerServlet.init(config);

    verify(config);
  }

  @Test
  public void testServletRequest() throws ServletException, IOException {
    ServletConfig config = createMock(ServletConfig.class);
    HttpServletRequest request = createMock(HttpServletRequest.class);
    HttpServletResponse response = createMock(HttpServletResponse.class);

    expect(request.getPathInfo()).andReturn("/checkRunning");
    expect(request.getPathInfo()).andReturn("/f/test34a/sas/info.txt");
    expect(request.getPathInfo()).andReturn("/p/myinfo.txt");
    expect(request.getPathInfo()).andReturn("/pmissmatch/sdfsd/sdf/cds.xt/");
    
    // now the sevice call.
    
    replay(config, request,response);
    ControllerServlet controllerServlet = new ControllerServlet();
    controllerServlet.init(config);

    // invoke with the request configured for jcr
    Handler handler = controllerServlet.getHandler(request);
    assertNotNull(handler);
    assertSame(controllerServlet.getNullHandler(), handler);

    handler = controllerServlet.getHandler(request);
    assertNotNull(handler);
    assertEquals(JCRHandler.class, handler.getClass());

    // invoke with the request configured for jcruser
    handler = controllerServlet.getHandler(request);
    assertNotNull(handler);
    assertEquals(JCRUserStorageHandler.class, handler.getClass());

    // invoke with the request configured for nomatch
    handler = controllerServlet.getHandler(request);
    assertNull(handler);

    
    
    controllerServlet.service(request, response);

    verify(config, request,response);

  }

}
