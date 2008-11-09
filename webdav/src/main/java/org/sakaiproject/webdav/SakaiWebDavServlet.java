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
package org.sakaiproject.webdav;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.server.SessionProvider;
import org.apache.jackrabbit.webdav.DavLocatorFactory;
import org.apache.jackrabbit.webdav.simple.ResourceConfig;
import org.apache.jackrabbit.webdav.simple.SimpleWebdavServlet;
import org.sakaiproject.kernel.api.KernelManager;
import org.sakaiproject.kernel.api.jcr.JCRService;

import java.io.IOException;
import java.net.URL;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This code is based on Apache Sling code (and it should take all credit)
 */
public class SakaiWebDavServlet extends SimpleWebdavServlet {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private static final Log LOG = LogFactory.getLog(SakaiWebDavServlet.class);
  private Repository repository;
  private JCRService jcrService;

  @Override
  public Repository getRepository() {
    return repository;
  }

  @Override
  public void init() throws ServletException {
    super.init();

    KernelManager km = new KernelManager();
    jcrService = km.getService(JCRService.class);
    repository = jcrService.getRepository();

    // for now, the ResourceConfig is fixed
    final String configPath = "/webdav-resource-config.xml";
    final ResourceConfig rc = new ResourceConfig();
    final URL cfg = getClass().getResource(configPath);
    if (cfg == null) {
      throw new UnavailableException("ResourceConfig source not found:"
          + configPath);
    }

    rc.parse(cfg);
    setResourceConfig(rc);
  }

  @Override
  protected void service(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {

    final String pinfo = request.getPathInfo();

    if (pinfo == null) {
      String uri = request.getRequestURI();
      uri += "/";
      response.sendRedirect(uri);
    } else {
      super.service(request, response);
    }

  }

  private DavLocatorFactory locatorFactory;

  private SessionProvider sessionProvider;

  // ---------- SimpleWebdavServlet overwrites -------------------------------

  @Override
  public DavLocatorFactory getLocatorFactory() {
    if (locatorFactory == null) {

      String workspace = jcrService.getDefaultWorkspace();

      // no configuration, try to login and acquire the default name
      if (workspace == null || workspace.length() == 0) {
        Session tmp = null;
        try {
          tmp = jcrService.login();
          workspace = tmp.getWorkspace().getName();
        } catch (Throwable t) {
          LOG.info("Using Fallback workspace ");
          // TODO: log !!
          workspace = "default"; // fall back name
        } finally {
          if (tmp != null) {
            tmp.logout();
          }
        }
      }

      locatorFactory = new SakaiLocatorFactory(workspace);
    }
    return locatorFactory;
  }

  @Override
  public synchronized SessionProvider getSessionProvider() {
    if (sessionProvider == null) {
      sessionProvider = new SakaiSessionProvider(jcrService);
    }
    return sessionProvider;
  }

}
