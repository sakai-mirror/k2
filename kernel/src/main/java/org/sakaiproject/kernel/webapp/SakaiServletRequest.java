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

import org.sakaiproject.kernel.api.session.Session;
import org.sakaiproject.kernel.api.user.Authentication;
import org.sakaiproject.kernel.api.user.User;
import org.sakaiproject.kernel.api.user.UserResolverService;
import org.sakaiproject.kernel.session.SessionImpl;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 
 */
public class SakaiServletRequest extends HttpServletRequestWrapper {

  public static final String NO_SESSION_USE = "_no_session";
  private Session session;
  private UserResolverService userResolverService;
  private HttpServletResponse response;
  private String cookieName;

  /**
   * @param request
   * @param sessionManagerService
   */
  public SakaiServletRequest(ServletRequest request, ServletResponse response, String cookieName,
      UserResolverService userResolverService) {
    super((HttpServletRequest) request);
    this.userResolverService = userResolverService;
    this.response = (HttpServletResponse) response;
    this.cookieName = cookieName;
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.servlet.http.HttpServletRequestWrapper#getSession()
   */
  @Override
  public HttpSession getSession() {
    if (!"true".equals(super.getAttribute(NO_SESSION_USE))) {
      if (session == null) {
        HttpSession rsession = super.getSession();
        if (rsession != null) {
          Cookie c = new Cookie(cookieName,rsession.getId());
          c.setPath("/");
          c.setMaxAge(-1);
          response.addCookie(c);
          System.err.println("New Session created and cookie set to"+c);
          session = new SessionImpl(rsession, (Authentication) super
              .getAttribute(Authentication.REQUESTTOKEN), userResolverService);
        }
      }
    }
    return session;
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.servlet.http.HttpServletRequestWrapper#getSession(boolean)
   */
  @Override
  public HttpSession getSession(boolean create) {
    if (!"true".equals(super.getAttribute(NO_SESSION_USE))) {
      if (session == null) {
        HttpSession rsession = super.getSession(create);
        if (rsession != null) {
          session = new SessionImpl(rsession, (Authentication) super
              .getAttribute(Authentication.REQUESTTOKEN), userResolverService);
        }
      }
    }
    return session;
  }

  /**
   * @return
   */
  public Session getSakaiSession() {
    if (!"true".equals(super.getAttribute(NO_SESSION_USE))) {
      if (session == null) {
        HttpSession rsession = super.getSession(true);
        if (rsession != null) {
          session = new SessionImpl(rsession, (Authentication) super
              .getAttribute(Authentication.REQUESTTOKEN), userResolverService);
        }
      }
    }
    return session;
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.servlet.http.HttpServletRequestWrapper#getRemoteUser()
   */
  @Override
  public String getRemoteUser() {
    String remoteUser = super.getRemoteUser();
    if (remoteUser == null || remoteUser.trim().length() == 0) {
      getSession(false);
      if (session != null) {
        User u = session.getUser();
        if (u != null) {
          remoteUser = u.getUuid();
        }
      }
    }
    if (remoteUser == null || remoteUser.trim().length() == 0) {
      Authentication a = (Authentication) super
          .getAttribute(Authentication.REQUESTTOKEN);
      if (a != null) {
        remoteUser = a.getUid();
      }
    }
    return remoteUser;
  }

}
