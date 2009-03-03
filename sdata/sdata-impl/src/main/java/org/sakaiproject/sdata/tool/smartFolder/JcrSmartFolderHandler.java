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
package org.sakaiproject.sdata.tool.smartFolder;

import com.google.inject.Inject;

import org.sakaiproject.kernel.api.Registry;
import org.sakaiproject.kernel.api.RegistryService;

import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

/**
 *
 */
public class JcrSmartFolderHandler implements SmartFolderHandler {
  private static final String KEY_0 = Query.XPATH;
  private static final String KEY_1 = Query.SQL;

  private final QueryManager queryMgr;

  /**
   *
   */
  @Inject
  public JcrSmartFolderHandler(RegistryService registryService,
      QueryManager queryMgr) {
    Registry<String, SmartFolderHandler> registry = registryService
        .getRegistry(SmartFolderHandler.SMARTFOLDER_REGISTRY);
    registry.add(KEY_0, this);
    registry.add(KEY_1, this);
    this.queryMgr = queryMgr;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.sdata.tool.smartFolder.SmartFolderHandler#handle(javax.jcr.Node)
   */
  public Map<String, Object> handle(Query query) throws RepositoryException {
    QueryResult results = query.execute();
    return null;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.Provider#getKey()
   */
  public String getKey() {
    return KEY_0 + ";" + KEY_1;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.Provider#getPriority()
   */
  public int getPriority() {
    return 0;
  }

}
