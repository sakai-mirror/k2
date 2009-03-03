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

import com.google.inject.Provider;

import org.sakaiproject.sdata.tool.smartFolder.JcrSmartFolderHandler;
import org.sakaiproject.sdata.tool.smartFolder.JpaSmartFolderHandler;
import org.sakaiproject.sdata.tool.smartFolder.SmartFolderHandler;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class SmartFolderHandlerListProvider implements
    Provider<List<SmartFolderHandler>> {

  private ArrayList<SmartFolderHandler> handlers;
  /**
   *
   */
  public SmartFolderHandlerListProvider(JcrSmartFolderHandler jcrHandler,
      JpaSmartFolderHandler jpaHandler) {
    handlers = new ArrayList<SmartFolderHandler>();
    handlers.add(jcrHandler);
    handlers.add(jpaHandler);
  }

  /**
   * {@inheritDoc}
   *
   * @see com.google.inject.Provider#get()
   */
  public List<SmartFolderHandler> get() {
    return handlers;
  }

}
