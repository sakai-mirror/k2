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

import org.sakaiproject.kernel.api.user.Authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 */
public class AuthenticationServlet extends HttpServlet {

  /**
   * 
   */
  private static final long serialVersionUID = -2118658526409944277L;

  /**
   * {@inheritDoc}
   * 
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doCheckLogin(request, response);
  }

  /**
   * @throws IOException
   * 
   */
  private void doCheckLogin(HttpServletRequest request,
      HttpServletResponse response) throws IOException {
    String login = request.getParameter("l");
    if ("1".equals(login)) {
      Object o = request.getAttribute(Authentication.REQUESTTOKEN);
      if (o == null) {
        // login didnt happen, so it must be a 401
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }
    }
    // pull the user to get it in the session and send a 200
    request.getRemoteUser();
    response.getWriter().write("OK");
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doCheckLogin(request, response);
  }
}
