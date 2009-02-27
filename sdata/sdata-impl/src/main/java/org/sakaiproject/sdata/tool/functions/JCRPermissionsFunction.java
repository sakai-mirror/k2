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

package org.sakaiproject.sdata.tool.functions;

import org.sakaiproject.kernel.util.rest.RestDescription;
import org.sakaiproject.sdata.tool.api.Handler;
import org.sakaiproject.sdata.tool.api.ResourceDefinition;
import org.sakaiproject.sdata.tool.api.SDataException;

import javax.jcr.Node;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This has not been implemented as yet.
 * 
 * @author ieb
 */
public class JCRPermissionsFunction extends JCRSDataFunction {
  private static final String KEY = "pe";
  private static final RestDescription DESCRIPTION = new RestDescription();
  static {
    DESCRIPTION.setTitle("Permissions Function");
    DESCRIPTION.setBackUrl("?doc=1");
    DESCRIPTION
        .setShortDescription("Manages permissions on a node , mapped to function  " + KEY);
    DESCRIPTION
        .addSection(
            2,
            "Introduction",
            "This function is not implemented at the moment.");
    DESCRIPTION.addResponse(String
        .valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR),
        "on any other error");

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
      HttpServletResponse response, Node target, ResourceDefinition rp)
      throws SDataException {
    SDataFunctionUtil.checkMethod(request.getMethod(), "POST|GET");
    
    
    
    
    
    // TODO To Be implemented
    throw new SDataException(HttpServletResponse.SC_NOT_IMPLEMENTED,
        " Permissions is not implemented in JCR at the moment ");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.sdata.tool.api.SDataFunction#getKey()
   */
  public String getKey() {
    return KEY;
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.sdata.tool.api.SDataFunction#getDescription()
   */
  public RestDescription getDescription() {
    return DESCRIPTION;
  }

}
