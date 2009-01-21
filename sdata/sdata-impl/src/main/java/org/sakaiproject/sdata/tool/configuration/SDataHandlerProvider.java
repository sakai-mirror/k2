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
import org.sakaiproject.sdata.tool.JCRUserStorageHandler;
import org.sakaiproject.sdata.tool.SnoopHandler;
import org.sakaiproject.sdata.tool.api.Handler;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class SDataHandlerProvider implements Provider<Map<String, Handler>> {

  private Map<String, Handler> handlerMap;

  /**
   * 
   */
  @Inject
  public SDataHandlerProvider(JCRHandler jcrHandler,
      JCRUserStorageHandler jcrUserStorageHandler,
      SnoopHandler snoopHandler,
      @Named(JCRHandler.HANDLER_KEY) String jcrKey,
      @Named(JCRUserStorageHandler.HANDLER_KEY) String jcrUserKey) {
    handlerMap = new HashMap<String, Handler>();
    handlerMap.put(jcrKey, jcrHandler);
    handlerMap.put(jcrUserKey, jcrUserStorageHandler);
    handlerMap.put("snoop", snoopHandler);
  }

  /**
   * {@inheritDoc}
   * 
   * @see com.google.inject.Provider#get()
   */
  public Map<String, Handler> get() {
    return handlerMap;
  }

}
