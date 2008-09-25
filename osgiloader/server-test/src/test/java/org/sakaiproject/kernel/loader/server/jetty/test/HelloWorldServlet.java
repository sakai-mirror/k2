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
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.KernelConfigurationException;
import org.sakaiproject.kernel.api.KernelManager;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
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
    DEFAULT(""), KERNEL("k");

    private static Map<String, Function> table = new HashMap<String, Function>();
    static {
      for ( Function f : Function.values() ) {
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
      if ( value != null ) {
        f = table.get(value);
        if ( f == null ) {
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
