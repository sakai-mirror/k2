/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2008 Timefields Ltd
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.kernel.webapp.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.KernelManager;
import org.sakaiproject.kernel.api.memory.CacheManagerService;
import org.sakaiproject.kernel.api.memory.CacheScope;
import org.sakaiproject.kernel.api.session.SessionManagerService;
import org.sakaiproject.kernel.webapp.SakaiServletRequest;
import org.sakaiproject.kernel.webapp.SakaiServletResponse;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 
 */
public class SakaiRequestFilter implements Filter {
  private static final Log LOG = LogFactory.getLog(SakaiRequestFilter.class);

  private static final String TIME_REQUEST = "time-requests";

  private static final String COOKIE_NAME = "cookie-name";

  private static final String DEFAULT_COOKIE_NAME = "JSESSIONID";

  private boolean timeOn = false;

  private SessionManagerService sessionManagerService;

  private CacheManagerService cacheManagerService;

  private String cookieName;

  /**
   * {@inheritDoc}
   * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
   */
  public void init(FilterConfig config) throws ServletException {
    cookieName  = config.getInitParameter(COOKIE_NAME);
    if ( cookieName == null || cookieName.trim().length() == 0) {
      cookieName = DEFAULT_COOKIE_NAME;
    }
    timeOn = "true".equals(config.getInitParameter(TIME_REQUEST));
    KernelManager kernelManager = new KernelManager();
    sessionManagerService = kernelManager
        .getService(SessionManagerService.class);
    cacheManagerService = kernelManager.getService(CacheManagerService.class);
    LOG.info(" SessionManagerService "+sessionManagerService);
    LOG.info(" Cache Manager Service "+cacheManagerService);
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.servlet.Filter#destroy()
   */
  public void destroy() {

  }

  /**
   * {@inheritDoc}
   * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
   */
  public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain chain) throws IOException, ServletException {
    SakaiServletRequest wrequest = new SakaiServletRequest(request);
    SakaiServletResponse wresponse = new SakaiServletResponse(response,cookieName);
    sessionManagerService.bindRequest(wrequest);
    try {

      if (timeOn) {
        long start = System.currentTimeMillis();
        try {
          chain.doFilter(wrequest, wresponse);
        } finally {
          long end = System.currentTimeMillis();
          HttpServletRequest hrequest = (HttpServletRequest) request;
          LOG.info("Request took " + hrequest.getMethod() + " "
              + hrequest.getPathInfo() + " " + (end - start) + " ms");
        }
      } else {
        chain.doFilter(wrequest, wresponse);
      }
    } finally {
      cacheManagerService.unbind(CacheScope.REQUEST);
    }
  }

}
