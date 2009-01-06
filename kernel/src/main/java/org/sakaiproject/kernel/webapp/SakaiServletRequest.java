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
import org.sakaiproject.kernel.session.SessionImpl;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

/**
 * 
 */
public class SakaiServletRequest extends HttpServletRequestWrapper {

  private Session session;

  /**
   * @param request
   * @param sessionManagerService
   */
  public SakaiServletRequest(ServletRequest request) {
    super((HttpServletRequest) request);
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.servlet.http.HttpServletRequestWrapper#getSession()
   */
  @Override
  public HttpSession getSession() {
    if (session == null) {
      HttpSession rsession = super.getSession();
      if (rsession != null) {
        session = new SessionImpl(rsession);
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
    if (session == null) {
      HttpSession rsession = super.getSession(create);
      if (rsession != null) {
        session = new SessionImpl(rsession);
      }
    }
    return session;
  }

  /**
   * @return
   */
  public Session getSakaiSession() {
    if (session == null) {
      HttpSession rsession = super.getSession(true);
      if (rsession != null) {
        session = new SessionImpl(rsession);
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
      if ( session != null ) {
        remoteUser = session.getUser().getUuid();
      } else {
        remoteUser = null;
      }
    }
    return remoteUser;
  }

}
