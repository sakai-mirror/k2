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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sakaiproject.kernel.Activator;
import org.sakaiproject.kernel.KernelConstants;
import org.sakaiproject.kernel.api.ComponentActivatorException;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
import org.sakaiproject.kernel.api.rest.RestProvider;
import org.sakaiproject.kernel.api.serialization.BeanConverter;
import org.sakaiproject.kernel.rest.RestPatchProvider;
import org.sakaiproject.kernel.test.KernelIntegrationBase;
import org.sakaiproject.kernel.util.IOUtils;
import org.sakaiproject.kernel.util.StringUtils;
import org.sakaiproject.kernel.webapp.RestServiceFaultException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

/**
 * Unit tests for the RestSiteProvider
 */
public class RestPatchProviderKernelUnitT extends BaseRestUnitT {

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

  /**
   * Patch new file with data
   * 
   * @throws ServletException
   * @throws IOException
   * @throws JCRNodeFactoryServiceException
   * @throws RepositoryException
   */
  @Test
  public void testPatchSharedWithUser() throws ServletException, IOException,
      RepositoryException, JCRNodeFactoryServiceException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("user1", "SESSION-21312312", baos);

    expect(request.getMethod()).andReturn("POST").anyTimes();

    expect(request.getParameterValues("k")).andReturn(
        new String[] { "a", "b", "c", "d" });
    expect(request.getParameterValues("v")).andReturn(
        new String[] { "a1", "b2", "c3", "d4" });
    expect(request.getParameterValues("a")).andReturn(
        new String[] { "u", "r", "u", "r" });
    expect(request.getParameterValues("i")).andReturn(null);

    response.setContentType(RestProvider.CONTENT_TYPE);
    expectLastCall();

    replayMocks();

    String[] elements = new String[] { "patch", "f", "a", "test", "file" };

    RestPatchProvider rsp = new RestPatchProvider(registryService,
        jcrNodeFactoryService, injector.getInstance(Key.get(
            BeanConverter.class, Names
                .named(KernelConstants.REPOSITORY_BEANCONVETER))),
        PRIVATE_BASE_PATH);
    rsp.dispatch(elements, request, response);

    String op = baos.toString(StringUtils.UTF8);
    assertEquals("{\"response\":\"OK\"}", op);

    Node n = jcrNodeFactoryService.getNode("/a/test/file");
    assertNotNull(n);
    String result = IOUtils.readFully(jcrNodeFactoryService
        .getInputStream("/a/test/file"), StringUtils.UTF8);
    assertEquals("{\"a\":\"a1\",\"c\":\"c3\"}", result);

    verifyMocks();
  }

  /**
   * Patch old file with data
   * 
   * @throws ServletException
   * @throws IOException
   * @throws JCRNodeFactoryServiceException
   * @throws RepositoryException
   */
  @Test
  public void testPatchSharedWithUserExists() throws ServletException,
      IOException, RepositoryException, JCRNodeFactoryServiceException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("user1", "SESSION-21312312", baos);

    expect(request.getMethod()).andReturn("POST").anyTimes();

    expect(request.getParameterValues("k")).andReturn(
        new String[] { "a", "b", "c", "d" });
    expect(request.getParameterValues("v")).andReturn(
        new String[] { "a1", "b2", "c3", "d4" });
    expect(request.getParameterValues("a")).andReturn(
        new String[] { "u", "r", "u", "r" });
    expect(request.getParameterValues("i")).andReturn(null);

    response.setContentType(RestProvider.CONTENT_TYPE);
    expectLastCall();

    replayMocks();

    ByteArrayInputStream in = new ByteArrayInputStream(
        "{\"a\":\"a1\",\"b\":\"b1\",\"c\":\"c3\",\"d\":\"d1\"}"
            .getBytes(StringUtils.UTF8));
    Node n = jcrNodeFactoryService.setInputStream("/a/test/file2", in, RestProvider.CONTENT_TYPE);
    n.save();

    String[] elements = new String[] { "patch", "f", "a", "test", "file2" };

    RestPatchProvider rsp = new RestPatchProvider(registryService,
        jcrNodeFactoryService, injector.getInstance(Key.get(
            BeanConverter.class, Names
                .named(KernelConstants.REPOSITORY_BEANCONVETER))),
        PRIVATE_BASE_PATH);
    rsp.dispatch(elements, request, response);

    String op = baos.toString(StringUtils.UTF8);
    assertEquals("{\"response\":\"OK\"}", op);

    n = jcrNodeFactoryService.getNode("/a/test/file2");
    assertNotNull(n);
    String result = IOUtils.readFully(jcrNodeFactoryService
        .getInputStream("/a/test/file"), StringUtils.UTF8);
    assertEquals("{\"a\":\"a1\",\"c\":\"c3\"}", result);

    verifyMocks();
  }

  /**
   * Patch new file with data
   * 
   * @throws ServletException
   * @throws IOException
   * @throws JCRNodeFactoryServiceException
   * @throws RepositoryException
   */
  @Test
  public void testPatchSharedNoUser() throws ServletException, IOException,
      RepositoryException, JCRNodeFactoryServiceException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes(null, "SESSION-21312312-nouser1", baos);

    expect(request.getMethod()).andReturn("POST").anyTimes();

    expect(request.getParameterValues("k")).andReturn(
        new String[] { "a", "b", "c", "d" });
    expect(request.getParameterValues("v")).andReturn(
        new String[] { "a1", "b2", "c3", "d4" });
    expect(request.getParameterValues("a")).andReturn(
        new String[] { "u", "r", "u", "r" });
    expect(request.getParameterValues("i")).andReturn(null);

    response.setContentType(RestProvider.CONTENT_TYPE);
    expectLastCall();

    replayMocks();

    String[] elements = new String[] { "patch", "f", "a", "test", "file" };

    RestPatchProvider rsp = new RestPatchProvider(registryService,
        jcrNodeFactoryService, injector.getInstance(Key.get(
            BeanConverter.class, Names
                .named(KernelConstants.REPOSITORY_BEANCONVETER))),
        PRIVATE_BASE_PATH);
    rsp.dispatch(elements, request, response);

    String op = baos.toString(StringUtils.UTF8);
    assertEquals("{\"response\":\"OK\"}", op);

    Node n = jcrNodeFactoryService.getNode("/a/test/file");
    assertNotNull(n);
    String result = IOUtils.readFully(jcrNodeFactoryService
        .getInputStream("/a/test/file"), StringUtils.UTF8);
    assertEquals("{\"a\":\"a1\",\"c\":\"c3\"}", result);

    verifyMocks();
  }

  /**
   * Patch old file with data
   * 
   * @throws ServletException
   * @throws IOException
   * @throws JCRNodeFactoryServiceException
   * @throws RepositoryException
   */
  @Test
  public void testPatchSharedNoUserExists() throws ServletException,
      IOException, RepositoryException, JCRNodeFactoryServiceException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes(null, "SESSION-21312312-nouser2", baos);

    expect(request.getMethod()).andReturn("POST").anyTimes();

    expect(request.getParameterValues("k")).andReturn(
        new String[] { "a", "b", "c", "d" });
    expect(request.getParameterValues("v")).andReturn(
        new String[] { "a1", "b2", "c3", "d4" });
    expect(request.getParameterValues("a")).andReturn(
        new String[] { "u", "r", "u", "r" });
    expect(request.getParameterValues("i")).andReturn(null);

    response.setContentType(RestProvider.CONTENT_TYPE);
    expectLastCall();

    replayMocks();

    ByteArrayInputStream in = new ByteArrayInputStream(
        "{\"a\":\"a1\",\"b\":\"b1\",\"c\":\"c3\",\"d\":\"d1\"}"
            .getBytes(StringUtils.UTF8));
    Node n = jcrNodeFactoryService.setInputStream("/a/test/file2", in, RestProvider.CONTENT_TYPE);
    n.save();

    String[] elements = new String[] { "patch", "f", "a", "test", "file2" };

    RestPatchProvider rsp = new RestPatchProvider(registryService,
        jcrNodeFactoryService, injector.getInstance(Key.get(
            BeanConverter.class, Names
                .named(KernelConstants.REPOSITORY_BEANCONVETER))),
        PRIVATE_BASE_PATH);
    rsp.dispatch(elements, request, response);

    String op = baos.toString(StringUtils.UTF8);
    assertEquals("{\"response\":\"OK\"}", op);

    n = jcrNodeFactoryService.getNode("/a/test/file2");
    assertNotNull(n);
    String result = IOUtils.readFully(jcrNodeFactoryService
        .getInputStream("/a/test/file"), StringUtils.UTF8);
    assertEquals("{\"a\":\"a1\",\"c\":\"c3\"}", result);

    verifyMocks();
  }

  /**
   * Patch old file with data
   * 
   * @throws ServletException
   * @throws IOException
   * @throws JCRNodeFactoryServiceException
   * @throws RepositoryException
   */
  @Test
  public void testPatchSharedNoUserBadMethod() throws ServletException,
      IOException, RepositoryException, JCRNodeFactoryServiceException {
    setupServices();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes(null, "SESSION-21312312-nouser2", baos);

    expect(request.getMethod()).andReturn("POST").anyTimes();

    expect(request.getParameterValues("k")).andReturn(
        new String[] { "a", "b", "c" }); // not long enough
    expect(request.getParameterValues("v")).andReturn(
        new String[] { "a1", "b2", "c3", "d4" });
    expect(request.getParameterValues("a")).andReturn(
        new String[] { "u", "r", "u", "r" });
    expect(request.getParameterValues("i")).andReturn(null);

    expectLastCall();

    replayMocks();

    String[] elements = new String[] { "patch", "f", "a", "test", "file2" };

    RestPatchProvider rsp = new RestPatchProvider(registryService,
        jcrNodeFactoryService, injector.getInstance(Key.get(
            BeanConverter.class, Names
                .named(KernelConstants.REPOSITORY_BEANCONVETER))),
        PRIVATE_BASE_PATH);
    try {
      rsp.dispatch(elements, request, response);
      fail();
    } catch (RestServiceFaultException ex) {
      assertEquals(HttpServletResponse.SC_BAD_REQUEST, ex.getStatusCode());
    }

    verifyMocks();
  }

}
