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

import org.sakaiproject.sdata.tool.api.SDataFunction;
import org.sakaiproject.sdata.tool.functions.JCRCreateFolder;
import org.sakaiproject.sdata.tool.functions.JCRHideReleaseFunction;
import org.sakaiproject.sdata.tool.functions.JCRMoveFunction;
import org.sakaiproject.sdata.tool.functions.JCRNodeMetadata;
import org.sakaiproject.sdata.tool.functions.JCRPermissionsFunction;
import org.sakaiproject.sdata.tool.functions.JCRPropertiesFunction;
import org.sakaiproject.sdata.tool.functions.JCRTaggingFunction;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class JCRHandlerFunctionProvider implements
    Provider<Map<String, SDataFunction>> {

  private Map<String, SDataFunction> functionMap = new HashMap<String, SDataFunction>();

  /**
   * 
   */
  @Inject
  public JCRHandlerFunctionProvider(JCRCreateFolder createFolder,
      JCRHideReleaseFunction hideRelease, JCRMoveFunction move,
      JCRNodeMetadata node, JCRPermissionsFunction permission,
      JCRPropertiesFunction properties, JCRTaggingFunction tagging,
      @Named("jcrhandler.function.createfolder") String createFolderKey,
      @Named("jcrhandler.function.hide") String hideReleaseKey,
      @Named("jcrhandler.function.move") String moveKey,
      @Named("jcrhandler.function.node") String nodeKey,
      @Named("jcrhandler.function.permission") String permssionKey,
      @Named("jcrhandler.function.properties") String propertiesKey,
      @Named("jcrhandler.function.tag") String taggingKey) {

    functionMap.put(createFolderKey, createFolder);
    functionMap.put(hideReleaseKey, hideRelease);
    functionMap.put(moveKey, move);
    functionMap.put(nodeKey, node);
    functionMap.put(permssionKey, permission);
    functionMap.put(propertiesKey, properties);
    functionMap.put(taggingKey, tagging);
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
