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
package org.sakaiproject.kernel.api.rest;

import org.sakaiproject.kernel.api.Provider;
import org.sakaiproject.kernel.util.rest.RestDescription;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 */
public interface RestProvider extends Provider<String> {

  public static final String REST_REGISTRY = "rest.registry";
  public static final String CONTENT_TYPE = "text/json";

  /**
   * Produces the output for the rest request, as json 
   * @param elements
   *          the path elements of the request
   * @param request
   *          the request
   * @param response
   *          the response
   * @throws IOException 
   * @throws ServletException 
   */
  void dispatch(String[] elements, HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException;

  /**
   * @return get the description of the service.
   */
  RestDescription getDescription();


}
