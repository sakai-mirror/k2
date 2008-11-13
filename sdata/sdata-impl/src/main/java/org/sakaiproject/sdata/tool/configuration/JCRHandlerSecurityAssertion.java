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

import org.sakaiproject.kernel.util.StringUtils;
import org.sakaiproject.sdata.tool.api.SecurityAssertion;
import org.sakaiproject.sdata.tool.util.PathSecurityAssertion;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class JCRHandlerSecurityAssertion implements Provider<SecurityAssertion> {

  private SecurityAssertion securityAssertion;

  /**
   * @param baseLocation
   * @param baseReference
   * @param lockDefinition
   * 
   */
  @Inject
  public JCRHandlerSecurityAssertion(
      PathSecurityAssertion pathSecurityAssertion,
      @Named("jcrhandler.basePath") String baseLocation,
      @Named("jcrhandler.baseURL") String baseReference,
      @Named("jcrhandler.lockDefinition") String lockDefinition) {
    this.securityAssertion = pathSecurityAssertion;
    pathSecurityAssertion.setBaseLocation(baseLocation);
    pathSecurityAssertion.setBaseReference(baseReference);
    Map<String, String> locks = new HashMap<String, String>();
    for (String lockDef : StringUtils.split(lockDefinition, ';')) {
      String[] l = StringUtils.split(lockDef, ':');
      locks.put(l[0], l[1]);
    }
    pathSecurityAssertion.setLocks(locks);
  }

  /**
   * {@inheritDoc}
   * 
   * @see com.google.inject.Provider#get()
   */
  public SecurityAssertion get() {
    return securityAssertion;
  }

}
