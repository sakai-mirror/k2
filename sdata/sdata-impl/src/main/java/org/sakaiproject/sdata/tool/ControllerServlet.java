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

package org.sakaiproject.sdata.tool;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.sdata.tool.api.Handler;
import org.sakaiproject.sdata.tool.configuration.SDataModule;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * Obviously the former is more compact.
 * </p>
 * <p>
 * When the servlet inits, it will create instances of the classes names in the
 * classname property and then register those against the baseurl property. When
 * processing a request, the path info will be examined and the first element of
 * the path will be used to match a selected handler on baseurl. The handler
 * will then be invoked for the method in question. If no handler is found then
 * a 404 will be sent back to the user.
 * </p>
 * <p>
 * There is an additional url /checkRunning that will respond with some sample
 * random data. This is used for unit testing. The size of the block can be set
 * with a x-testdata-size header in the request. This is limited to 4K maximum.
 * </p>
 * 
 * @author ieb
 */
public class ControllerServlet extends HttpServlet {

  /**
	 * 
	 */
  private static final long serialVersionUID = -7098194528761855627L;

  private static final Log log = LogFactory.getLog(ControllerServlet.class);

  /**
   * Dummy handler used for all those requests that cant be matched.
   */
  private Handler nullHandler = new Handler() {

    /**
     * 
     */
    private static final long serialVersionUID = -225447966882182992L;
    private Random r = new Random(System.currentTimeMillis());

    /*
     * (non-Javadoc)
     * 
     * @seeorg.sakaiproject.sdata.tool.api.Handler#doDelete(javax.servlet.http.
     * HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doDelete(HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException {
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.sakaiproject.sdata.tool.api.Handler#doGet(javax.servlet.http.
     * HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
      int size = 1024;
      try {
        size = Integer.parseInt(request.getHeader("x-testdata-size"));
      } catch (Exception ex) {

      }
      size = Math.min(4096, size);
      byte[] b = new byte[size];
      r.nextBytes(b);
      response.setContentType("application/octet-stream");
      response.setContentLength(b.length);
      response.setStatus(HttpServletResponse.SC_OK);
      response.getOutputStream().write(b);
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.sakaiproject.sdata.tool.api.Handler#doHead(javax.servlet.http.
     * HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doHead(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.sakaiproject.sdata.tool.api.Handler#doPost(javax.servlet.http.
     * HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.sakaiproject.sdata.tool.api.Handler#doPut(javax.servlet.http.
     * HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doPut(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.sakaiproject.sdata.tool.api.Handler#setHandlerHeaders(javax.servlet
     * .http.HttpServletResponse)
     */
    public void setHandlerHeaders(HttpServletRequest request,
        HttpServletResponse response) {
      response.setHeader("x-sdata-url", request.getPathInfo());
      response.setHeader("x-sdata-handler", this.getClass().getName());
    }

    public void sendError(HttpServletRequest request,
        HttpServletResponse response, Throwable ex) throws IOException {

    }

    public void sendMap(HttpServletRequest request,
        HttpServletResponse response, Map<String, Object> contetMap)
        throws IOException {

    }

    public String getKey() {
      return null;
    }

  };

  private SDataConfiguration configuration;

  /**
   * Construct a Controller servlet
   */
  public ControllerServlet() {

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
   */
  @Override
  public void init(ServletConfig config) throws ServletException {

    Injector injector = Guice.createInjector(new SDataModule());
    configuration = injector.getInstance(SDataConfiguration.class);
  }


  /*
   * (non-Javadoc)
   * 
   * @see
   * javax.servlet.http.HttpServlet#doDelete(javax.servlet.http.HttpServletRequest
   * , javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doDelete(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    Handler h = getHandler(request);
    if (h != null) {
      h.setHandlerHeaders(request, response);
      h.doDelete(request, response);
    } else {
      response.reset();
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "No Handler Found");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
   * javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    Handler h = getHandler(request);
    if (h != null) {
      h.setHandlerHeaders(request, response);
      h.doGet(request, response);
    } else {
      response.reset();
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "No Handler Found");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * javax.servlet.http.HttpServlet#doHead(javax.servlet.http.HttpServletRequest
   * , javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doHead(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    Handler h = getHandler(request);
    if (h != null) {
      h.setHandlerHeaders(request, response);
      h.doHead(request, response);
    } else {
      response.reset();
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "No Handler Found");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
   * , javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    Handler h = getHandler(request);
    if (h != null) {
      h.setHandlerHeaders(request, response);
      h.doPost(request, response);
    } else {
      response.reset();
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "No Handler Found");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest,
   * javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doPut(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    Handler h = getHandler(request);
    if (h != null) {
      h.setHandlerHeaders(request, response);
      h.doPut(request, response);
    } else {
      response.reset();
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "No Handler Found");
    }
  }

  /**
   * Get the handler mapped to a request path.
   * 
   * @param request
   * @return
   */
  public Handler getHandler(HttpServletRequest request) {
    String pathInfo = request.getPathInfo();
    if (log.isDebugEnabled()) {
      log.debug("Path is " + pathInfo);
    }
    if ("/checkRunning".equals(pathInfo)) {
      return nullHandler;
    }
    if (pathInfo == null)
      return null;

    char[] path = pathInfo.trim().toCharArray();
    if (path.length < 1)
      return null;
    int start = 0;
    if (path[0] == '/') {
      start = 1;
    }
    int end = start;
    for (; end < path.length && path[end] != '/'; end++)
      ;
    String key = new String(path, start, end - start);
    
    Handler h = configuration.getHandlerRegister().get(key);
    System.err.println("Key "+key+" matched "+h);
    return h;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest
   * , javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    long start = System.currentTimeMillis();
    super.service(req, resp);
    log.info((System.currentTimeMillis() - start) + " ms " + req.getMethod()
        + ":" + req.getRequestURL());
  }

  
  /**
   * @return the nullHandler
   */
  public Handler getNullHandler() {
    return nullHandler;
  }
}
