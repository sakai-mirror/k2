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
package org.sakaiproject.sdata.tool.configuration;

import com.google.inject.Inject;
import com.google.inject.Provider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.sdata.tool.api.SDataFunction;
import org.sakaiproject.sdata.tool.functions.JCRCheckInFunction;
import org.sakaiproject.sdata.tool.functions.JCRCopyFunction;
import org.sakaiproject.sdata.tool.functions.JCRHideReleaseFunction;
import org.sakaiproject.sdata.tool.functions.JCRMoveFunction;
import org.sakaiproject.sdata.tool.functions.JCRNodeMetadata;
import org.sakaiproject.sdata.tool.functions.JCRPermissionsFunction;
import org.sakaiproject.sdata.tool.functions.JCRPropertiesFunction;
import org.sakaiproject.sdata.tool.functions.JCRRevertFunction;
import org.sakaiproject.sdata.tool.functions.JCRTaggingFunction;
import org.sakaiproject.sdata.tool.functions.JCRVersionHistoryFunction;

import java.util.HashMap;
import java.util.Map;

/**
 * Registers function handlers.
 */
public class JCRHandlerFunctionProvider implements Provider<Map<String, SDataFunction>> {

  private static final Log LOG = LogFactory.getLog(JCRHandlerFunctionProvider.class);
  private Map<String, SDataFunction> functionMap = new HashMap<String, SDataFunction>();

  /**
   * 
   */
  @Inject
  public JCRHandlerFunctionProvider(JCRHideReleaseFunction hideRelease,
      JCRMoveFunction move, JCRNodeMetadata node, JCRPermissionsFunction permission,
      JCRPropertiesFunction properties, JCRTaggingFunction tagging,
      JCRVersionHistoryFunction jcrVersionHistoryFunction,
      JCRCopyFunction jcrCopyFunction, JCRCheckInFunction jcrCheckInFunction, JCRRevertFunction jcrRevertFunction) {
    add(hideRelease);
    add(move);
    add(node);
    add(permission);
    add(properties);
    add(tagging);
    add(jcrVersionHistoryFunction);
    add(jcrCopyFunction);
    add(jcrCheckInFunction);
    add(jcrRevertFunction);
  }

  /**
   * @param hideRelease
   */
  private void add(SDataFunction f) {
    if ( functionMap.containsKey(f.getKey())) {
      throw new RuntimeException("Function "+f.getKey()+" Overwritten existing:"+functionMap.get(f.getKey())+"  new:"+f);
    }
    LOG.debug("Added function "+f.getKey()+" as "+f);
    functionMap.put(f.getKey(),f);
  }

  /**
   * {@inheritDoc}
   * 
   * @see com.google.inject.Provider#get()
   */
  public Map<String, SDataFunction> get() {
    return functionMap;
  }

}
