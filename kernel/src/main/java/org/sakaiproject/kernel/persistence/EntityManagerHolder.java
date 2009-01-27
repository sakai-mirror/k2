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
package org.sakaiproject.kernel.persistence;

import org.sakaiproject.kernel.api.memory.ThreadBound;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 * A Thread bound entity manager that closes when its unbound.
 */
public class EntityManagerHolder implements ThreadBound {

  private EntityManager entityManager;

  /**
   * 
   */
  public EntityManagerHolder(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.memory.ThreadBound#unbind()
   */
  public void unbind() {
    if (entityManager.isOpen()) {
      EntityTransaction transaction = entityManager.getTransaction();
      if (transaction.isActive()) {
        transaction.commit();
      }
      entityManager.close();
    }
  }

  /**
   * @return the entityManager
   */
  public EntityManager getEntityManager() {
    return entityManager;
  }
}
