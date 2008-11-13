/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2008 Timefields Ltd
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

package org.sakaiproject.sdata.tool.util;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.sakaiproject.sdata.tool.JCRHandler;
import org.sakaiproject.sdata.tool.api.ResourceDefinition;
import org.sakaiproject.sdata.tool.api.ResourceDefinitionFactory;
import org.sakaiproject.sdata.tool.api.SDataException;
import org.sakaiproject.sdata.tool.api.SecurityAssertion;

import javax.servlet.http.HttpServletRequest;

/**
 * Base Class for a resource definition factory
 * 
 * @author ieb
 */
public class ResourceDefinitionFactoryImpl implements ResourceDefinitionFactory {

  private String basePath;

  private String baseUrl;

  private SecurityAssertion securityAssertion;

  @Inject
  public ResourceDefinitionFactoryImpl() {
  }
  /**
   * @param basePath the basePath to set
   */
  public void setBasePath(String basePath) {
    this.basePath = basePath;
  }
  /**
   * @param baseUrl the baseUrl to set
   */
  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }
  /**
   * @param securityAssertion the securityAssertion to set
   */
  public void setSecurityAssertion(SecurityAssertion securityAssertion) {
    this.securityAssertion = securityAssertion;
  }

  /**
   * /** Get the ResourceDefinition bean based on the request
   * 
   * @param path
   * @return
   * @throws SDataException
   */
  public ResourceDefinition getSpec(final HttpServletRequest request)
      throws SDataException {

    String path = request.getPathInfo();
    path = path.substring(baseUrl.length());

    if (path.endsWith("/")) {
      path = path.substring(0, path.length() - 1);
    }

    String v = request.getParameter("v"); // version
    int version = -1;
    if (v != null && v.trim().length() > 0) {
      version = Integer.parseInt(v);
    }
    String f = request.getParameter("f"); // function
    String d = request.getParameter("d"); // function
    int depth = 1;
    if (d != null && d.trim().length() > 0) {
      depth = Integer.parseInt(d);
    }
    return new ResourceDefinitionImpl(request.getMethod(), f, depth, basePath,
        path, version, securityAssertion);
  }

}
