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

import com.google.common.collect.Maps;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sakaiproject.kernel.Activator;
import org.sakaiproject.kernel.api.ComponentActivatorException;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
import org.sakaiproject.kernel.api.rest.RestProvider;
import org.sakaiproject.kernel.api.serialization.BeanConverter;
import org.sakaiproject.kernel.rest.RestSearchProvider;
import org.sakaiproject.kernel.test.KernelIntegrationBase;
import org.sakaiproject.kernel.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.servlet.ServletException;

/**
 * Unit tests for the RestSiteProvider
 */
public class RestSearchProviderKernelUnitT extends BaseRestUnitT {

  /**
   * 
   */
  public static class QueryPattern {

    private Map<String, String> params;
    private String response;

    /**
     * @param of
     * @param string
     */
    public QueryPattern(String[] params, String response) {
      
      this.params = Maps.newHashMap();
      for ( int i = 0; i < params.length; i+= 2) {
        this.params.put(params[i], params[i+1]);
      }
      this.response = response;
    }

    /**
     * @param string
     * @return
     */
    public String getParameter(String key) {
      return params.get(key);
    }

    /**
     * @return
     */
    public String getResponse() {
      return response;
    }

  }

  private static final QueryPattern[] TESTPATTERN = new QueryPattern[] { 
    new QueryPattern(new String[] {"q", "somethingthatwillnerverexist", "n", null, "p", null},
      "{\"start\":0,\"page\":0,\"end\":0,\"pageSize\":0,\"size\":0}"), 
    new QueryPattern(new String[] {"q", "admin", "n", null, "p", null},
    "{\"start\":0,\"page\":0,\"end\":0,\"pageSize\":0,\"size\":0}") 
  
  };

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
   * @throws InterruptedException 
   */
  @Test
  public void testSearch() throws ServletException, IOException,
      RepositoryException, JCRNodeFactoryServiceException, InterruptedException {
    setupServices();
    
    Thread.sleep(10000);
    for (QueryPattern testQuery : TESTPATTERN) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      setupAnyTimes("user1", "SESSION-21312312", baos);
      expect(request.getMethod()).andReturn("POST").anyTimes();

      expect(request.getParameter("q")).andReturn(testQuery.getParameter("q"));
      expect(request.getParameter("n")).andReturn(testQuery.getParameter("n"));
      expect(request.getParameter("p")).andReturn(testQuery.getParameter("p"));

      response.setContentType(RestProvider.CONTENT_TYPE);
      expectLastCall();

      replayMocks();

      String[] elements = new String[] { "search" };

      RestSearchProvider rsp = new RestSearchProvider(registryService,
          jcrService, injector.getInstance(Key.get(BeanConverter.class, Names
              .named(BeanConverter.REPOSITORY_BEANCONVETER))));
      rsp.dispatch(elements, request, response);

      String op = baos.toString(StringUtils.UTF8);
      assertEquals(testQuery.getResponse(), op);

      verifyMocks();
      resetMocks();
    }
  }

}
