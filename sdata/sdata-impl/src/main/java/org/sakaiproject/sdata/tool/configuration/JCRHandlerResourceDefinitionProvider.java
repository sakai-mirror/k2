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
import com.google.inject.Provider;
import com.google.inject.name.Named;

import org.sakaiproject.sdata.tool.JCRHandler;
import org.sakaiproject.sdata.tool.api.ResourceDefinitionFactory;
import org.sakaiproject.sdata.tool.api.SecurityAssertion;
import org.sakaiproject.sdata.tool.util.ResourceDefinitionFactoryImpl;

/**
 * 
 */
public class JCRHandlerResourceDefinitionProvider implements
    Provider<ResourceDefinitionFactory> {

  private ResourceDefinitionFactory resourceDefinitionFactoryImpl;

  /**
   * @param securityAssertion
   * 
   */
  @Inject
  public JCRHandlerResourceDefinitionProvider(
      ResourceDefinitionFactoryImpl resourceDefinitionFactoryImpl,
      @Named(JCRHandler.BASE_PATH) String basePath,
      @Named(JCRHandler.BASE_URL) String baseUrl,
      @Named(JCRHandler.SECURITY_ASSERTION) SecurityAssertion securityAssertion) {
    this.resourceDefinitionFactoryImpl = resourceDefinitionFactoryImpl;
    resourceDefinitionFactoryImpl.setBasePath(basePath);
    resourceDefinitionFactoryImpl.setBaseUrl(baseUrl);
    resourceDefinitionFactoryImpl.setSecurityAssertion(securityAssertion);
  }

  /**
   * {@inheritDoc}
   * 
   * @see com.google.inject.Provider#get()
   */
  public ResourceDefinitionFactory get() {
    return resourceDefinitionFactoryImpl;
  }

}
