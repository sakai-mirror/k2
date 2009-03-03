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

import java.util.List;
import java.util.Map;

import javax.jcr.query.Query;
import javax.persistence.EntityManager;

/**
 * Handler for smart folder actions that use an ad-hoc JPA query.
 */
public class JpaSmartFolderHandler implements SmartFolderHandler {
  public static final String KEY = "jpa";

  private final EntityManager entityManager;
  /**
   *
   */
  @Inject
  public JpaSmartFolderHandler(RegistryService registryService,
      EntityManager entityManager) {
    Registry<String, SmartFolderHandler> registry = registryService
        .getRegistry(SmartFolderHandler.SMARTFOLDER_REGISTRY);
    registry.add(this);
    this.entityManager = entityManager;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.sdata.tool.smartFolder.SmartFolderHandler#handle(javax.jcr.Node)
   */
  public Map<String, Object> handle(Query query) {
    String stmt = query.getStatement();
    javax.persistence.Query jpaQuery = entityManager.createQuery(stmt);
    List results = jpaQuery.getResultList();
    return null;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.Provider#getKey()
   */
  public String getKey() {
    return KEY;
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
