/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/trunk/sdata/tool/sakai-sdata-impl/src/main/java/org/sakaiproject/sdata/tool/functions/CHSPropertiesFunction.java $
 * $Id: CHSPropertiesFunction.java 49164 2008-05-17 20:21:09Z ian@caret.cam.ac.uk $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2007 The Sakai Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.sdata.tool.functions;

import com.google.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
import org.sakaiproject.sdata.tool.api.Handler;
import org.sakaiproject.sdata.tool.api.ResourceDefinition;
import org.sakaiproject.sdata.tool.api.SDataException;
import org.sakaiproject.sdata.tool.model.JCRNodeMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * Get the list of tags on a site context.
 * </p>
 * <p>
 * <b>f=t</b>: Path specifies the context.
 * </p>
 * <p>
 * <b>n</b>: The Name of the property
 * 
 * @author ieb
 */
public class JCRTaggingFunction extends JCRSDataFunction {

  private static final String PROPERTY_NAME = "n";
  private static final String PROPERTY_QUERY = "a";
  private static final String PROPERTY_QUERY_VALUE = "q";
  private static final String PROPERTY_NRESULTS = "c";
  private static final String PROPERTY_START = "s";
  private static final String ALL_TAGS = "a";
  private static final String LIST_TAGS = "l";
  private static final Log LOG = LogFactory.getLog(JCRTaggingFunction.class);

  private JCRTagging jcrTagging;
  private JCRNodeFactoryService jcrNodeFactoryService;

  @Inject
  public JCRTaggingFunction(JCRTagging jcrTagging,
      JCRNodeFactoryService jcrNodeFactoryService) {
    this.jcrTagging = jcrTagging;
    this.jcrNodeFactoryService = jcrNodeFactoryService;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.sakaiproject.sdata.tool.api.SDataFunction#call(org.sakaiproject.sdata
   * .tool.api.Handler, javax.servlet.http.HttpServletRequest,
   * javax.servlet.http.HttpServletResponse, java.lang.Object,
   * org.sakaiproject.sdata.tool.api.ResourceDefinition)
   */
  public void call(Handler handler, HttpServletRequest request,
      HttpServletResponse response, Object target, ResourceDefinition rp)
      throws SDataException {
    SDataFunctionUtil.checkMethod(request.getMethod(), "GET");

    String propertyName = request.getParameter(PROPERTY_NAME);
    String query = request.getParameter(PROPERTY_QUERY);
    String nresultsParam = request.getParameter(PROPERTY_NRESULTS);
    String startParam = request.getParameter(PROPERTY_START);
    if (query == null || query.trim().length() == 0) {
      query = ALL_TAGS;
    }
    int start = 0;
    int nresults = 10;
    if (startParam != null && startParam.length() > 0) {
      try {
        start = Integer.parseInt(startParam);
      } catch (Exception ex) {
      }
    }
    if (nresultsParam != null && nresultsParam.length() > 0) {
      try {
        nresults = Integer.parseInt(nresultsParam);
      } catch (Exception ex) {
      }
    }

    String path = rp.getRepositoryPath();
    if (!path.endsWith("/")) {
      path = path + "/";
    }
    String[] parts = path.split("/");
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("name", propertyName);
    result.put("path", path);
    if (parts.length > 2) {
      String context = parts[2];

      result.put("context", context);
      if (ALL_TAGS.equals(query)) {
        Map<String, Integer> distribution = jcrTagging.getPropertyVector(
            context, propertyName);

        result.put("distribution", distribution);
      } else if (LIST_TAGS.equals(query)) {
        String queryValue = request.getParameter(PROPERTY_QUERY_VALUE);
        String[] values = queryValue.split(",");

        List<String> hits = jcrTagging.getPropertyMatches(context,
            propertyName, values, start, nresults);
        List<Map<String, Object>> hitResults = new ArrayList<Map<String, Object>>();

        for (String hit : hits) {
          try {
            Node n = jcrNodeFactoryService.getNode(hit);
            hitResults.add(new JCRNodeMap(n, 0, rp));
          } catch (RepositoryException e) {
            LOG.info("Failed to get node", e);
          } catch (JCRNodeFactoryServiceException e) {
            LOG.info("Failed to get node", e);
          }
        }
        result.put("hits", hitResults);
      }
    } else {
      result
          .put(
              "error",
              "The Path does not contain enough elements to represent a context, and so no tags can be found ");

    }
    try {
      handler.sendMap(request, response, result);
    } catch (IOException e) {
      throw new SDataException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          "IO Error " + e.getMessage());
    }

  }
}
