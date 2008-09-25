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
package org.sakaiproject.kernel.loader.server.jetty.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.url.URLStreamHandlerService;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.KernelConfigurationException;
import org.sakaiproject.kernel.api.KernelManager;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class HelloWorldServlet extends HttpServlet {

  /**
   * 
   */
  public static enum Function {
    DEFAULT(""), KERNEL("k"), GETSERVICE("s"), TESTSOURCESERVICE("t");

    private static Map<String, Function> table = new HashMap<String, Function>();
    static {
      for (Function f : Function.values()) {
        table.put(f.toString(), f);
      }
    }
    private final String name;

    private Function(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }

    public static Function getValueOf(String value) {
      Function f = DEFAULT;
      if (value != null) {
        f = table.get(value);
        if (f == null) {
          f = DEFAULT;
        }
      }
      return f;
    }
  }

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private static final Log LOG = LogFactory.getLog(HelloWorldServlet.class);
  public static final String DEPLOYED_URL = "/hello";
  public static final String REQUEST_URL = JettyServer.SERVER_URL + DEPLOYED_URL;
  public static final String RESPONSE = "hello";
  private Kernel kernel;

  /**
   * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
   */
  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    KernelManager km = new KernelManager();
    try {
      kernel = km.getKernel();
      LOG.info("Got kernel as " + kernel);
    } catch (KernelConfigurationException e) {
      throw new ServletException(e);
    }
  }

  /**
   * Write a hello response.
   * 
   * @param req the request
   * @param resp the response
   * @throws ServletException if there is a servlet releted exception
   * @throws IOException if there is a problem writing
   */
  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
      throws ServletException, IOException {
    Function f = Function.getValueOf(req.getParameter("f"));
    switch (f) {
    case KERNEL: {
      resp.setContentType("text/plain");
      PrintWriter w = resp.getWriter();
      w.print(RESPONSE);
      LOG.info("Sending Response for Kernel ");
      break;
    }
    case GETSERVICE: {
      resp.setContentType("text/xml");
      BundleContext bc = kernel.getContext();
      PrintWriter w = resp.getWriter();
      w.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      w.println("<kernel>");
      w.println("  <bundles>");
      for (Bundle b : bc.getBundles()) {
        LOG.info("Got Bundle " + b.getSymbolicName());
        w.print("    <bundle>");
        w.print(b.getSymbolicName());
        w.println("</bundle>");
      }
      w.println("  </bundles>");
      w.println("  <services>");
      try {
        for (ServiceReference sr : bc.getAllServiceReferences(null, null)) {
          LOG.info("Got Service Reference " + sr.toString());
          String sx = sr.toString();
          w.print("    <service>");
          w.print(sx);
          w.println("</service>");
        }
      } catch (InvalidSyntaxException e) {
        LOG.error(e);
        w.print("    <error>");
        w.print(e.getMessage());
        w.println("</error>");

      }
      w.println("  </services>");
      ServiceReference sr = bc.getServiceReference("org.osgi.service.url.URLStreamHandlerService");
      URLStreamHandlerService o = (URLStreamHandlerService) bc.getService(sr);
      URLConnection u = o.openConnection(new URL(HelloWorldServlet.REQUEST_URL + "?f="
          + Function.TESTSOURCESERVICE));
      BufferedReader in  = new BufferedReader(new InputStreamReader(u.getInputStream()));
      StringBuilder sb = new StringBuilder();
      String line = in.readLine();
      while ( line != null ) {
        sb.append(line);
        line = in.readLine();
      }
      w.println("  <service-invoke>");
      w.println(sb.toString());
      w.println("  </service-invoke>");

      w.println("</kernel>");
      LOG.info("Sending Response for GETSERVICE ");
      break;
    }
    case TESTSOURCESERVICE: {
      resp.setContentType("text/plain");
      PrintWriter w = resp.getWriter();
      w.print("testsource");
      LOG.info("Sending Response for Kernel ");
      break;

    }
    default: {
      resp.setContentType("text/plain");
      PrintWriter w = resp.getWriter();
      w.print(RESPONSE);
      LOG.info("Sending Response");
      break;
    }
    }
  }
}
