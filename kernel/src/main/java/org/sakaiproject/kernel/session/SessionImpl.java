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
package org.sakaiproject.kernel.session;

import org.sakaiproject.kernel.api.session.Session;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/**
 * 
 */
@SuppressWarnings("deprecation")
public class SessionImpl implements Session {

  private static final String USER_ID = "_u";
  private HttpSession baseSession;

  /**
   * @param httpRequest
   * @param rsession
   */
  public SessionImpl(HttpSession baseSession) {
    this.baseSession = baseSession;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.session.Session#getUserId()
   */
  public String getUserId() {
    return (String) getAttribute(USER_ID);
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.servlet.http.HttpSession#getAttribute(java.lang.String)
   */
  public Object getAttribute(String name) {
    return baseSession.getAttribute(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.servlet.http.HttpSession#getAttributeNames()
   */
  @SuppressWarnings("unchecked")
  public Enumeration getAttributeNames() {
    return baseSession.getAttributeNames();
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.servlet.http.HttpSession#getCreationTime()
   */
  public long getCreationTime() {
    return baseSession.getCreationTime();
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.servlet.http.HttpSession#getId()
   */
  public String getId() {
    return baseSession.getId();
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.servlet.http.HttpSession#getLastAccessedTime()
   */
  public long getLastAccessedTime() {
    return baseSession.getLastAccessedTime();
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.servlet.http.HttpSession#getMaxInactiveInterval()
   */
  public int getMaxInactiveInterval() {
    return baseSession.getMaxInactiveInterval();
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.servlet.http.HttpSession#getServletContext()
   */
  public ServletContext getServletContext() {
    return baseSession.getServletContext();
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.servlet.http.HttpSession#getSessionContext()
   */
  public HttpSessionContext getSessionContext() {
    return baseSession.getSessionContext();
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.servlet.http.HttpSession#getValue(java.lang.String)
   */
  public Object getValue(String name) {
    return baseSession.getValue(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.servlet.http.HttpSession#getValueNames()
   */
  public String[] getValueNames() {
    return baseSession.getValueNames();
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.servlet.http.HttpSession#invalidate()
   */
  public void invalidate() {
    baseSession.invalidate();

  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.servlet.http.HttpSession#isNew()
   */
  public boolean isNew() {
    return baseSession.isNew();
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.servlet.http.HttpSession#putValue(java.lang.String,
   *      java.lang.Object)
   */
  public void putValue(String name, Object value) {
    baseSession.putValue(name, value);
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.servlet.http.HttpSession#removeAttribute(java.lang.String)
   */
  public void removeAttribute(String name) {
    baseSession.removeAttribute(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.servlet.http.HttpSession#removeValue(java.lang.String)
   */
  public void removeValue(String name) {
    baseSession.removeValue(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.servlet.http.HttpSession#setAttribute(java.lang.String,
   *      java.lang.Object)
   */
  public void setAttribute(String name, Object value) {
    baseSession.setAttribute(name, value);
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.servlet.http.HttpSession#setMaxInactiveInterval(int)
   */
  public void setMaxInactiveInterval(int interval) {
    baseSession.setMaxInactiveInterval(interval);
  }

}
