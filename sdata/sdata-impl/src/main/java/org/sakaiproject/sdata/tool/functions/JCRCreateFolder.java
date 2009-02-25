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

import com.google.inject.Inject;

import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
import org.sakaiproject.sdata.tool.api.Handler;
import org.sakaiproject.sdata.tool.api.ResourceDefinition;
import org.sakaiproject.sdata.tool.api.SDataException;
import org.sakaiproject.sdata.tool.api.SDataFunction;
import org.sakaiproject.sdata.tool.model.JCRNodeMap;

import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ieb
 */
public class JCRCreateFolder implements SDataFunction {

  private static final String KEY = "c";
  private JCRNodeFactoryService jcrNodeFactory;

  @Inject
  public JCRCreateFolder(JCRNodeFactoryService jcrNodeFactoryService) {
    this.jcrNodeFactory = jcrNodeFactoryService;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.sakaiproject.sdata.tool.api.SDataFunction#call(org.sakaiproject.sdata
   * .tool.api.Handler, javax.servlet.http.HttpServletRequest,
   * javax.servlet.http.HttpServletResponse, java.lang.Object)
   */
  public void call(Handler handler, HttpServletRequest request,
      HttpServletResponse response, Object target, ResourceDefinition rp)
      throws SDataException {

    SDataFunctionUtil.checkMethod(request.getMethod(), "POST");
    try {
      Node n = jcrNodeFactory.createFolder(rp.getRepositoryPath());

      JCRNodeMap outputMap = new JCRNodeMap(n, rp.getDepth(), rp);
      handler.sendMap(request, response, outputMap);

    } catch (JCRNodeFactoryServiceException e) {
      throw new SDataException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e
          .getMessage());
    } catch (RepositoryException e) {
      throw new SDataException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e
          .getMessage());
    } catch (IOException e) {
      throw new SDataException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e
          .getMessage());
    }

  }

  public void destroy() {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.sdata.tool.api.SDataFunction#getKey()
   */
  public String getKey() {
    return KEY;
  }

}