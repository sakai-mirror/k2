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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easymock.EasyMock;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sakaiproject.kernel.Activator;
import org.sakaiproject.kernel.api.ComponentActivatorException;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.KernelManager;
import org.sakaiproject.kernel.api.RequiresStop;
import org.sakaiproject.kernel.api.ServiceSpec;
import org.sakaiproject.kernel.api.ShutdownService;
import org.sakaiproject.kernel.component.KernelLifecycle;
import org.sakaiproject.kernel.util.FileUtil;
import org.sakaiproject.kernel.webapp.filter.SakaiRequestFilter;

import java.io.File;
import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 */
public class KernelIntegrationTest {
  private static final Log LOG = LogFactory.getLog(KernelIntegrationTest.class);
  private static KernelLifecycle kernelLifecycle;

  @BeforeClass
  public static void beforeClass() throws ComponentActivatorException {
    // If there are problems with startup and shutdown, these will prevent the
    // problem
    FileUtil.deleteAll(new File("target/jcr"));
    FileUtil.deleteAll(new File("target/testdb"));
    kernelLifecycle = new KernelLifecycle();
    kernelLifecycle.start();

    KernelManager kernelManager = new KernelManager();
    Kernel kernel = kernelManager.getKernel();
    Activator activator = new Activator();
    activator.activate(kernel);
    for (Class<?> c : Activator.SERVICE_CLASSES) {

      ShutdownService ss = kernel.getServiceManager().getService(
          new ServiceSpec(ShutdownService.class));
      Object s = kernel.getServiceManager().getService(new ServiceSpec(c));
      if (s instanceof RequiresStop) {
        ss.register((RequiresStop) s);
      }
    }
  }

  @AfterClass
  public static void afterClass() {
    try {
      kernelLifecycle.stop();
    } catch (Exception ex) {
      LOG.info("Failed to stop kernel ", ex);
    }
  }
  
  
  
  /**
   * Test the request Filter
   * @throws ServletException
   * @throws IOException
   */
  @Test
  public void testSakaiRequestFilter() throws ServletException, IOException {
    FilterConfig filterConfig = EasyMock.createMock(FilterConfig.class);
    HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
    HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
    FilterChain chain = EasyMock.createMock(FilterChain.class);
  
    EasyMock.expect(filterConfig.getInitParameter("cookie-name")).andReturn("JSESSIONID");
    EasyMock.expect(filterConfig.getInitParameter("time-requests")).andReturn("true");
    EasyMock.expect(request.getMethod()).andReturn("GET");
    EasyMock.expect(request.getPathInfo()).andReturn("/sdata/f");
    chain.doFilter((ServletRequest)EasyMock.anyObject(), (ServletResponse)EasyMock.anyObject());
    EasyMock.replay(filterConfig,request,response,chain);
    
    SakaiRequestFilter requestFilter = new SakaiRequestFilter();
    requestFilter.init(filterConfig);
    requestFilter.doFilter(request, response, chain);
    
    EasyMock.verify(filterConfig,request,response,chain);
  }
  /**
   * Test the request fileter with no settings
   * @throws ServletException
   * @throws IOException
   */
  @Test
  public void testSakaiRequestFilterSettings() throws ServletException, IOException {
    FilterConfig filterConfig = EasyMock.createMock(FilterConfig.class);
    HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
    HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
    FilterChain chain = EasyMock.createMock(FilterChain.class);
  
    EasyMock.expect(filterConfig.getInitParameter("cookie-name")).andReturn(null);
    EasyMock.expect(filterConfig.getInitParameter("time-requests")).andReturn(null);
    chain.doFilter((ServletRequest)EasyMock.anyObject(), (ServletResponse)EasyMock.anyObject());
    EasyMock.replay(filterConfig,request,response,chain);
    
    SakaiRequestFilter requestFilter = new SakaiRequestFilter();
    requestFilter.init(filterConfig);
    requestFilter.doFilter(request, response, chain);
    
    EasyMock.verify(filterConfig,request,response,chain);
  }
  
  
}
