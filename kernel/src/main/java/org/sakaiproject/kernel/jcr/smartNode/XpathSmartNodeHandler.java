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
package org.sakaiproject.kernel.jcr.smartNode;

import com.google.inject.Inject;

import org.sakaiproject.kernel.api.Registry;
import org.sakaiproject.kernel.api.RegistryService;
import org.sakaiproject.kernel.jcr.api.SmartNodeHandler;

import javax.jcr.query.Query;

/**
 * XPath handler for smart folder actions.
 */
public class XpathSmartNodeHandler extends JcrSmartNodeHandler {
  private static final String KEY = Query.XPATH;

  /**
   *
   */
  @Inject
  public XpathSmartNodeHandler(RegistryService registryService) {
    Registry<String, SmartNodeHandler> registry = registryService
        .getRegistry(SmartNodeHandler.REGISTRY);
    registry.add(this);
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.Provider#getKey()
   */
  public String getKey() {
    return KEY;
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.Provider#getPriority()
   */
  public int getPriority() {
    return 0;
  }

}
