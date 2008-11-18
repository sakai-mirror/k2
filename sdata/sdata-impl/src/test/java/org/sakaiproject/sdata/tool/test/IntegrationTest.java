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

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyLong;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.KernelManager;
import org.sakaiproject.kernel.api.ServiceSpec;
import org.sakaiproject.kernel.api.jcr.JCRService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
import org.sakaiproject.kernel.component.KernelLifecycle;
import org.sakaiproject.kernel.component.core.KernelBootstrapModule;
import org.sakaiproject.kernel.util.ResourceLoader;
import org.sakaiproject.sdata.tool.ControllerServlet;
import org.sakaiproject.sdata.tool.JCRDumper;
import org.sakaiproject.sdata.tool.JCRHandler;
import org.sakaiproject.sdata.tool.JCRUserStorageHandler;
import org.sakaiproject.sdata.tool.api.Handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 */
public class IntegrationTest {

  private static final Log LOG = LogFactory.getLog(IntegrationTest.class);
  private static KernelLifecycle kl;
  private static Kernel kernel;

  @BeforeClass
  public static void beforeClass() throws IOException {
    assertNotNull(IntegrationTest.class.getClassLoader()
        .getResourceAsStream("kernel-component.properties"));

    LOG.info("Got kernel-component.properties using "
        + ControllerServlet.class.getClassLoader());
    assertNotNull(IntegrationTest.class.getClassLoader()
        .getResourceAsStream("integration-kernel.properties"));
    LOG.info("Got integration-kernel.properties using "
        + ControllerServlet.class.getClassLoader());

    @SuppressWarnings("unused")
    String s = ResourceLoader.readResource("res://kernel-component.properties",
        IntegrationTest.class.getClassLoader());
    LOG.info("Got res://kernel-component.properties from ResourceLoader using "
        + ControllerServlet.class.getClassLoader());
    s = ResourceLoader.readResource("res://integration-kernel.properties",
        IntegrationTest.class.getClassLoader());
    LOG
        .info("Got res://integration-kernel.properties from ResourceLoader using "
            + ControllerServlet.class.getClassLoader());
    System.setProperty(KernelBootstrapModule.SYS_LOCAL_PROPERTIES,
        "res://integration-kernel.properties");
    kl = new KernelLifecycle();
    kl.start();

    KernelManager km = new KernelManager();
    kernel = km.getKernel();

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

    replay(config, request, response);
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

    verify(config, request, response);
  }

  @Test
  public void testServletRequest1() throws ServletException, IOException {

    ServletConfig config = createMock(ServletConfig.class);
    HttpServletRequest request = createMock(HttpServletRequest.class);
    HttpServletResponse response = createMock(HttpServletResponse.class);

    // service call 1

    // now the sevice call.
    // this is fragile and at the moment, we just get a 403, which doesnt sound
    // that good !
    // call 1
    expect(request.getMethod()).andReturn("GET").anyTimes();
    expect(request.getPathInfo()).andReturn("/f/test34a/sas/info.txt")
        .anyTimes();
    expect(request.getRemoteUser()).andReturn("ieb").anyTimes();
    expect(request.getParameter("snoop")).andReturn("0").anyTimes();
    expect(request.getParameter("v")).andReturn(null).anyTimes();
    expect(request.getParameter("f")).andReturn(null).anyTimes();
    expect(request.getParameter("d")).andReturn(null).anyTimes();
    expect(request.getRequestURL()).andAnswer(new IAnswer<StringBuffer>() {

      public StringBuffer answer() throws Throwable {

        return new StringBuffer(
            "http://localhost:8080/sdata/f/test34a/sas/info.txt");
      }

    }).anyTimes();

    response.setHeader("x-sdata-handler",
        "org.sakaiproject.sdata.tool.JCRHandler");

    response.setHeader("x-sdata-url", "/f/test34a/sas/info.txt");
    response.reset();
    response.sendError(404);
    replay(config, request, response);

    ControllerServlet controllerServlet = new ControllerServlet();
    controllerServlet.init(config);
    controllerServlet.service(request, response);
    verify(config, request, response);

  }

  @Test
  public void testServletRequest2() throws ServletException, IOException {

    ServletConfig config = createMock(ServletConfig.class);
    HttpServletRequest request = createMock(HttpServletRequest.class);
    HttpServletResponse response = createMock(HttpServletResponse.class);

    // service call 2
    // call 2 no path
    expect(request.getMethod()).andReturn("GET").anyTimes();
    expect(request.getPathInfo()).andReturn(null).anyTimes();
    expect(request.getRequestURL()).andAnswer(new IAnswer<StringBuffer>() {

      public StringBuffer answer() throws Throwable {

        return new StringBuffer("http://localhost:8080/sdata");
      }

    }).anyTimes();

    response.reset();
    response.sendError(404, "No Handler Found");

    replay(config, request, response);
    ControllerServlet controllerServlet = new ControllerServlet();
    controllerServlet.init(config);
    controllerServlet.service(request, response);
    verify(config, request, response);

  }

  @Test
  public void testServletRequest3() throws ServletException, IOException {

    ServletConfig config = createMock(ServletConfig.class);
    HttpServletRequest request = createMock(HttpServletRequest.class);
    HttpServletResponse response = createMock(HttpServletResponse.class);

    // service call 3

    // call 3 empty path
    expect(request.getMethod()).andReturn("GET").anyTimes();
    expect(request.getPathInfo()).andReturn("").anyTimes();
    expect(request.getRequestURL()).andAnswer(new IAnswer<StringBuffer>() {

      public StringBuffer answer() throws Throwable {

        return new StringBuffer("http://localhost:8080/sdata");
      }

    }).anyTimes();

    response.reset();
    response.sendError(404, "No Handler Found");

    replay(config, request, response);
    ControllerServlet controllerServlet = new ControllerServlet();
    controllerServlet.init(config);
    controllerServlet.service(request, response);
    verify(config, request, response);

  }

  @Test
  public void testServletRequest4() throws ServletException, IOException {

    ServletConfig config = createMock(ServletConfig.class);
    HttpServletRequest request = createMock(HttpServletRequest.class);
    HttpServletResponse response = createMock(HttpServletResponse.class);

    // service call 4
    // exercise the check
    // call 4
    expect(request.getMethod()).andReturn("GET").anyTimes();
    expect(request.getPathInfo()).andReturn("/checkRunning").anyTimes();
    expect(request.getHeader("x-testdata-size")).andReturn("10").anyTimes();
    expect(response.getOutputStream()).andAnswer(
        new IAnswer<ServletOutputStream>() {

          public ServletOutputStream answer() throws Throwable {
            return new ServletOutputStream() {

              @Override
              public void write(int arg0) throws IOException {
              }

            };
          }

        }).anyTimes();
    expect(request.getRequestURL()).andAnswer(new IAnswer<StringBuffer>() {

      public StringBuffer answer() throws Throwable {

        return new StringBuffer("http://localhost:8080/sdata");
      }

    }).anyTimes();

    response.setHeader("x-sdata-handler",
        "org.sakaiproject.sdata.tool.ControllerServlet$1");
    response.setHeader("x-sdata-url", "/checkRunning");
    response.setContentType("application/octet-stream");
    response.setContentLength(10);
    response.setStatus(200);

    replay(config, request, response);
    ControllerServlet controllerServlet = new ControllerServlet();
    controllerServlet.init(config);
    controllerServlet.service(request, response);
    verify(config, request, response);

  }

  @Test
  public void testGet() throws ServletException,
      JCRNodeFactoryServiceException, AccessDeniedException,
      ItemExistsException, ConstraintViolationException,
      InvalidItemStateException, ReferentialIntegrityException,
      VersionException, LockException, NoSuchNodeTypeException,
      RepositoryException, IOException {
    ServletConfig config = createMock(ServletConfig.class);
    HttpServletRequest request = createMock(HttpServletRequest.class);
    HttpServletResponse response = createMock(HttpServletResponse.class);

    expect(request.getMethod()).andReturn("GET").atLeastOnce();
    expect(request.getPathInfo()).andReturn("/f/test/testfile.txt")
        .atLeastOnce();
    expect(request.getRemoteUser()).andReturn("ieb").anyTimes();
    expect(request.getParameter("v")).andReturn(null).anyTimes();
    expect(request.getParameter("f")).andReturn(null).anyTimes();
    expect(request.getParameter("d")).andReturn(null).anyTimes();
    expect(request.getParameter("snoop")).andReturn("0").anyTimes();
    expect(request.getDateHeader("if-unmodified-since")).andReturn(0L)
        .atLeastOnce();
    expect(request.getDateHeader("if-modified-since")).andReturn(0L)
        .atLeastOnce();
    expect(request.getHeader("if-match")).andReturn(null).atLeastOnce();
    expect(request.getHeader("if-none-match")).andReturn(null).atLeastOnce();
    expect(request.getHeader("range")).andReturn(null).atLeastOnce();
    expect(request.getDateHeader("if-range")).andReturn(0L).atLeastOnce();
    expect(request.getHeader("if-range")).andReturn(null).atLeastOnce();

    response.setHeader("x-sdata-handler",
        "org.sakaiproject.sdata.tool.JCRHandler");
    response.setHeader("x-sdata-url", "/f/test/testfile.txt");
    response.setContentType("application/octet-stream");
    response.setDateHeader((String) anyObject(), anyLong());
    expectLastCall().anyTimes();
    response.addHeader((String) anyObject(), (String) anyObject());
    expectLastCall().anyTimes();
    response.setHeader((String) anyObject(), (String) anyObject());
    expectLastCall().anyTimes();
    response.setStatus(200);
    expectLastCall().atLeastOnce();
    response.setContentLength(12);
    expectLastCall().atLeastOnce();

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final ServletOutputStream out = new ServletOutputStream() {

      @Override
      public void write(int c) throws IOException {
        baos.write(c);
      }

    };
    expect(response.getOutputStream()).andAnswer(
        new IAnswer<ServletOutputStream>() {

          public ServletOutputStream answer() throws Throwable {
            return out;
          }

        }).anyTimes();
    expect(request.getRequestURL()).andAnswer(new IAnswer<StringBuffer>() {

      public StringBuffer answer() throws Throwable {

        return new StringBuffer("http://localhost:8080/sdata");
      }

    }).anyTimes();

    replay(config, request, response);

    ControllerServlet controllerServlet = new ControllerServlet();
    controllerServlet.init(config);

    // create a node and populate it with some content
    JCRNodeFactoryService jcrNodeFactoryService = kernel.getServiceManager()
        .getService(new ServiceSpec(JCRNodeFactoryService.class));
    jcrNodeFactoryService.createFile("/test/testfile.txt");
    String content = "some content";
    ByteArrayInputStream bais = new ByteArrayInputStream(content
        .getBytes("UTF-8"));
    jcrNodeFactoryService.setInputStream("/test/testfile.txt", bais).save();

    // get the node back
    controllerServlet.service(request, response);

    String result = new String(baos.toByteArray(), "UTF-8");
    assertEquals(content, result);

  }
  
  @Test
  public void testGetMetaData() throws ServletException,
      JCRNodeFactoryServiceException, AccessDeniedException,
      ItemExistsException, ConstraintViolationException,
      InvalidItemStateException, ReferentialIntegrityException,
      VersionException, LockException, NoSuchNodeTypeException,
      RepositoryException, IOException {
    ServletConfig config = createMock(ServletConfig.class);
    HttpServletRequest request = createMock(HttpServletRequest.class);
    HttpServletResponse response = createMock(HttpServletResponse.class);

    expect(request.getMethod()).andReturn("GET").atLeastOnce();
    expect(request.getPathInfo()).andReturn("/f/test")
        .atLeastOnce();
    expect(request.getRemoteUser()).andReturn("ieb").anyTimes();
    expect(request.getParameter("v")).andReturn(null).anyTimes();
    expect(request.getParameter("f")).andReturn("m").anyTimes();
    expect(request.getParameter("d")).andReturn("2").anyTimes();
    expect(request.getParameter("snoop")).andReturn("0").anyTimes();
    expect(request.getDateHeader("if-unmodified-since")).andReturn(0L)
        .atLeastOnce();
    expect(request.getDateHeader("if-modified-since")).andReturn(0L)
        .atLeastOnce();
    expect(request.getHeader("if-match")).andReturn(null).atLeastOnce();
    expect(request.getHeader("if-none-match")).andReturn(null).atLeastOnce();
    expect(request.getHeader("range")).andReturn(null).atLeastOnce();
    expect(request.getDateHeader("if-range")).andReturn(0L).atLeastOnce();
    expect(request.getHeader("if-range")).andReturn(null).atLeastOnce();

    response.setHeader("x-sdata-handler",
        "org.sakaiproject.sdata.tool.JCRHandler");
    response.setHeader("x-sdata-url", "/f/test/testfile.txt");
    response.setContentType("text/plain;charset=UTF-8");
    expectLastCall().anyTimes();
    response.setDateHeader((String) anyObject(), anyLong());
    expectLastCall().anyTimes();
    response.addHeader((String) anyObject(), (String) anyObject());
    expectLastCall().anyTimes();
    response.setHeader((String) anyObject(), (String) anyObject());
    expectLastCall().anyTimes();
    response.setStatus(200);
    expectLastCall().atLeastOnce();
    response.setContentLength(anyInt());
    expectLastCall().atLeastOnce();

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final ServletOutputStream out = new ServletOutputStream() {

      @Override
      public void write(int c) throws IOException {
        baos.write(c);
      }

    };
    expect(response.getOutputStream()).andAnswer(
        new IAnswer<ServletOutputStream>() {

          public ServletOutputStream answer() throws Throwable {
            return out;
          }

        }).anyTimes();
    expect(request.getRequestURL()).andAnswer(new IAnswer<StringBuffer>() {

      public StringBuffer answer() throws Throwable {

        return new StringBuffer("http://localhost:8080/sdata");
      }

    }).anyTimes();

    replay(config, request, response);

    ControllerServlet controllerServlet = new ControllerServlet();
    controllerServlet.init(config);

    // create a node and populate it with some content
    JCRNodeFactoryService jcrNodeFactoryService = kernel.getServiceManager()
        .getService(new ServiceSpec(JCRNodeFactoryService.class));
    jcrNodeFactoryService.createFile("/test/testfile.txt");
    String content = "some content";
    ByteArrayInputStream bais = new ByteArrayInputStream(content
        .getBytes("UTF-8"));
    jcrNodeFactoryService.setInputStream("/test/testfile.txt", bais).save();

    // get the node back
    controllerServlet.service(request, response);

    String result = new String(baos.toByteArray(), "UTF-8");
    assertNotNull(result);
    
  }

  
  @Test
  public void testDumper() throws ServletException, IOException, JCRNodeFactoryServiceException, RepositoryException, ItemExistsException, ConstraintViolationException, InvalidItemStateException, ReferentialIntegrityException, VersionException, LockException, NoSuchNodeTypeException, RepositoryException {

    // create a node and populate it with some content
    JCRNodeFactoryService jcrNodeFactoryService = kernel.getServiceManager()
        .getService(new ServiceSpec(JCRNodeFactoryService.class));
    jcrNodeFactoryService.createFile("/test/testfile.txt");
    String content = "some content";
    ByteArrayInputStream bais = new ByteArrayInputStream(content
        .getBytes("UTF-8"));
    jcrNodeFactoryService.setInputStream("/test/testfile.txt", bais).save();
    
    JCRService jcrService = kernel.getServiceManager().getService(new ServiceSpec(JCRService.class));
    
    HttpServletRequest request = createMock(HttpServletRequest.class);
    HttpServletResponse response = createMock(HttpServletResponse.class);

    
    JCRDumper dumper = new JCRDumper(jcrService);
    reset(request,response);
    replay(request,response);
    dumper.doDelete(request, response);
    verify(request,response);
    generateDumperCallSequence("GET",request,response);
    dumper.doGet(request, response);
    verify(request,response);
    reset(request,response);
    replay(request,response);
    dumper.doHead(request, response);
    verify(request,response);
    reset(request,response);
    replay(request,response);
    dumper.doPost(request, response);
    verify(request,response);
    reset(request,response);
    replay(request,response);
    dumper.doPut(request, response);   
    verify(request,response);

  }

  /**
   * @param string
   * @param request
   * @param response
   * @throws IOException 
   */
  private void generateDumperCallSequence(String method,
      HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    reset(request,response);
    expect(request.getPathInfo()).andReturn("/test/testfile.txt").anyTimes();
    expect(request.getMethod()).andReturn(method).anyTimes();
    
    response.setContentType("text/xml");
    expect(response.getOutputStream()).andAnswer(
        new IAnswer<ServletOutputStream>() {

          public ServletOutputStream answer() throws Throwable {
            return new ServletOutputStream() {

              @Override
              public void write(int b) throws IOException {
              }
              
            };
          }

        }).anyTimes();

     
    replay(request,response);
    
  }

}
