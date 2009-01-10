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
package org.sakaiproject.kernel.webapp;

import org.sakaiproject.kernel.api.KernelManager;
import org.sakaiproject.kernel.api.Registry;
import org.sakaiproject.kernel.api.RegistryService;
import org.sakaiproject.kernel.api.rest.RestProvider;
import org.sakaiproject.kernel.util.StringUtils;
import org.sakaiproject.kernel.util.rest.RestDescription;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 */
public class RestServlet extends HttpServlet {

  /**
   * 
   */
  private static final long serialVersionUID = -172232497404083238L;
  private Registry<String, RestProvider> registry;

  /**
   * {@inheritDoc}
   * 
   * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
   */
  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    KernelManager km = new KernelManager();
    RegistryService registryService = km.getService(RegistryService.class);
    registry = registryService.getRegistry(RestProvider.REST_REGISTRY);
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  protected void service(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {

    String requestPath = request.getPathInfo();
    if ( requestPath == null ) {
      requestPath = "";
    }
    String[] elements = StringUtils.split(requestPath, '/');
    String locator = "default";
    if (elements != null && elements.length > 0) {
      locator = elements[0];
    }
    Map<String, RestProvider> restProviders = registry.getMap();
    System.err.println("Got Providers map "+restProviders);
    if ( locator == null ) {
      locator = "default";
    }
    if ( "__describe__".equals(locator) ) {
      locator = "default";
    }
    RestProvider restProvider = restProviders.get(locator);
    System.err.println("Got Provider for "+locator+" as "+restProviders);
       if (restProvider == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
    } else {
      try {
        if ( requestPath.endsWith("__describe__") ) {
          RestDescription description = restProvider.getDescription();
          String format = request.getParameter("fmt");
          if ( "xml".equals(format) ) {
            response.setContentType("text/xml");
            response.getWriter().print(description.toXml());
          } else if ( "json".equals(format) ) {
            response.setContentType(RestProvider.CONTENT_TYPE);
            response.getWriter().print(description.toJson());
          } else {
            response.setContentType("text/html");
            response.getWriter().print(description.toHtml());            
          }
        } else {
          restProvider.dispatch(elements, request, response);
        }
      } catch (SecurityException ex) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, ex.getMessage());
      }
    }
  }
}
