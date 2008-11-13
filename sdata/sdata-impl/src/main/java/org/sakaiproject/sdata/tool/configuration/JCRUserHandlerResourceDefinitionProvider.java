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
package org.sakaiproject.sdata.tool.configuration;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.sakaiproject.sdata.tool.JCRUserStorageHandler;
import org.sakaiproject.sdata.tool.api.SecurityAssertion;
import org.sakaiproject.sdata.tool.util.ResourceDefinitionFactoryImpl;

/**
 * 
 */
public class JCRUserHandlerResourceDefinitionProvider extends JCRHandlerResourceDefinitionProvider{

  /**
   * @param resourceDefinitionFactoryImpl
   * @param basePath
   * @param baseUrl
   * @param securityAssertion
   */
  @Inject
  public JCRUserHandlerResourceDefinitionProvider(
      ResourceDefinitionFactoryImpl resourceDefinitionFactoryImpl,
      @Named(JCRUserStorageHandler.BASE_PATH) String basePath,
      @Named(JCRUserStorageHandler.BASE_URL) String baseUrl,
      @Named(JCRUserStorageHandler.SECURITY_ASSERTION) SecurityAssertion securityAssertion) {
    super(resourceDefinitionFactoryImpl, basePath, baseUrl, securityAssertion);
  }


}
