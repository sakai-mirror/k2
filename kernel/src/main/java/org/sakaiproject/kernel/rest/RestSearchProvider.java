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
package org.sakaiproject.kernel.rest;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.sakaiproject.kernel.api.Registry;
import org.sakaiproject.kernel.api.RegistryService;
import org.sakaiproject.kernel.api.jcr.JCRConstants;
import org.sakaiproject.kernel.api.jcr.JCRService;
import org.sakaiproject.kernel.api.rest.RestProvider;
import org.sakaiproject.kernel.api.serialization.BeanConverter;
import org.sakaiproject.kernel.util.IOUtils;
import org.sakaiproject.kernel.util.JCRNodeMap;
import org.sakaiproject.kernel.util.StringUtils;
import org.sakaiproject.kernel.util.rest.RestDescription;
import org.sakaiproject.kernel.webapp.RestServiceFaultException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 */
public class RestSearchProvider implements RestProvider {

  private static final RestDescription DESC = new RestDescription();
  private static final String KEY = "search";
  private static final String QUERY = "q";
  private static final String NRESUTS_PER_PAGE = "n";
  private static final String PAGE = "p";

  static {
    DESC.setTitle("Search");
    DESC.setShortDescription("Provides search functionality into the JCR.");
    DESC.addSection(1, "Introduction",
        "This service allows search using the JCR search implemetation.");
    DESC
        .addSection(
            2,
            "Search ",
            "Performs a search operation, and returns  "
                + HttpServletResponse.SC_OK
                + " on sucess, the content of the response is of the form.... TDOD DOC ");
    DESC.addURLTemplate("/rest/" + KEY, "Accepts GET to perform the search");
    DESC.addParameter(QUERY, "The query string");
    DESC.addParameter(NRESUTS_PER_PAGE, "the number of results per page");
    DESC.addParameter(PAGE, "the page, 0 = the first page ");
    DESC.addResponse(String.valueOf(HttpServletResponse.SC_OK),
        "The search was completed OK and the result set is returned ");
    DESC.addResponse(String.valueOf(HttpServletResponse.SC_FORBIDDEN),
        "If permission to search was denied ");
    DESC.addResponse(String
        .valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR),
        " Any other error");

  }

  private JCRService jcrService;
  private BeanConverter beanConverter;

  /**
   * 
   */
  @Inject
  public RestSearchProvider(RegistryService registryService,
      JCRService jcrService,
      @Named(BeanConverter.REPOSITORY_BEANCONVETER) BeanConverter beanConverter) {
    Registry<String, RestProvider> registry = registryService
        .getRegistry(RestProvider.REST_REGISTRY);
    registry.add(this);
    System.err
        .println("ADDED "
            + this
            + " to registry ===================================================================================================================================================================================================================================================");
    this.jcrService = jcrService;
    this.beanConverter = beanConverter;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.rest.RestProvider#dispatch(java.lang.String[],
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  public void dispatch(String[] elements, HttpServletRequest request,
      HttpServletResponse response) {
    try {
      Map<String, Object> map = doSearch(request, response);
      if (map != null) {
        String responseBody = beanConverter.convertToString(map);
        response.setContentType(RestProvider.CONTENT_TYPE);
        response.getOutputStream().print(responseBody);
      }
    } catch (SecurityException ex) {
      throw ex;
    } catch (RestServiceFaultException ex) {
      throw ex;
    } catch (Exception ex) {
      ex.printStackTrace();
      throw new RestServiceFaultException(ex.getMessage(), ex);
    }
  }

  /**
   * @param request
   * @param response
   * @return
   * @throws RepositoryException
   * @throws LoginException
   * @throws IOException 
   * @throws UnsupportedEncodingException 
   */
  private Map<String, Object> doSearch(HttpServletRequest request,
      HttpServletResponse response) throws RestServiceFaultException,
      LoginException, RepositoryException, UnsupportedEncodingException, IOException {
    Session session = jcrService.getSession();
    String query = request.getParameter(QUERY);
    String nresults = request.getParameter(NRESUTS_PER_PAGE);
    String page = request.getParameter(PAGE);

    if (StringUtils.isEmpty(query)) {
      throw new RestServiceFaultException(HttpServletResponse.SC_BAD_REQUEST,
          "No Query");
    }
    String nodeType = "nt:base";
    int nr = 50;
    if (!StringUtils.isEmpty(nresults)) {
      nr = Integer.parseInt(nresults);
    }
    int np = 0;
    if (!StringUtils.isEmpty(page)) {
      np = Integer.parseInt(page);
    }

    int start = np * nr;
    int end = (np + 1) * nr;

    // escape the query
    QueryManager queryManager = session.getWorkspace().getQueryManager();

    Query q = null;
    String escapedQuery = StringUtils.escapeJCRSQL(query);
    String sqlQuery = "SELECT * FROM " + nodeType + " WHERE CONTAINS(.,'"
        + escapedQuery + "' )";
    try {
      q = queryManager.createQuery(sqlQuery, Query.SQL);
    } catch (InvalidQueryException ex) {
      throw new RestServiceFaultException(HttpServletResponse.SC_BAD_REQUEST,
          "Invalid query presented to content system: " + sqlQuery + " "
              + ex.getMessage(), ex);
    }
    long startMs = System.currentTimeMillis();
    QueryResult result = q.execute();
    NodeIterator ni = result.getNodes();
    long endMs = System.currentTimeMillis();
    System.err.println("Executed " + sqlQuery + " in " + (endMs - startMs)
        + " ms " + ni.getSize() + " hits");
    Map<String, Object> results = new HashMap<String, Object>();
    List<Map<String, Object>> resultList = Lists.newArrayList();
    long size = ni.getSize();
    long startPos = 0;
    long endPos = 0;
    try {
      System.err.println("Skipping " + start);
      ni.skip(start);
      startPos = ni.getPosition();
      endPos = startPos;

      for (int i = start; (i < end) && (ni.hasNext());) {
        Node n = ni.nextNode();
        Node parentNode = n.getParent();
        Map<String, Object> itemResponse = new HashMap<String, Object>();
        itemResponse.put("nodeproperties", new JCRNodeMap(parentNode, 1));
        if (JCRConstants.NT_FILE.equals(parentNode.getPrimaryNodeType()
            .getName())) {
          String mimeType = "application/octet-stream";
          String encoding = "UTF-8";
          Property mimeTypeProperty = n.getProperty(JCRConstants.JCR_MIMETYPE);
          if (mimeTypeProperty != null) {
            mimeType = mimeTypeProperty.getString();
          }
          Property contentEncoding = n.getProperty(JCRConstants.JCR_ENCODING);
          if (contentEncoding != null) {
            encoding = contentEncoding.getString();
          }
          if (mimeType != null && mimeType.startsWith("text")) {
            Property p = n.getProperty(JCRConstants.JCR_DATA);
            if (p.getLength() < 10240) {
              InputStream in = null;
              try {
                in = p.getStream();
                itemResponse.put("content", IOUtils.readFully(in, encoding));
              } finally {
                try {
                  in.close();
                } catch (Exception ex) {
                }
              }
            }
          }
        }
        resultList.add(itemResponse);
        endPos = ni.getPosition();
      }

    } catch (NoSuchElementException ex) {
      // went over the end.
    }
    results.put("page", np);
    results.put("pageSize", nr);
    results.put("size", size);
    results.put("start", startPos);
    results.put("end", endPos);
    results.put("results", resultList);
    return results;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.rest.RestProvider#getDescription()
   */
  public RestDescription getDescription() {
    return DESC;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.Provider#getKey()
   */
  public String getKey() {
    return KEY;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.Provider#getPriority()
   */
  public int getPriority() {
    return 0;
  }

}