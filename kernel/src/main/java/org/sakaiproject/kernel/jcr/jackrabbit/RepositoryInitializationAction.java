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
package org.sakaiproject.kernel.jcr.jackrabbit;

import com.google.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.jcr.JCRService;
import org.sakaiproject.kernel.internal.api.InitializationAction;
import org.sakaiproject.kernel.jcr.api.internal.RepositoryStartupException;
import org.sakaiproject.kernel.jcr.api.internal.StartupAction;
import org.sakaiproject.kernel.jcr.jackrabbit.sakai.SakaiJCRCredentials;

import java.util.Iterator;
import java.util.List;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * A Kernel initalization action to initialize the JCR repository. This class
 * performs initalization by invoking a list of JCR StartupActions. Those
 * actions are injected into the constructor.
 */
public class RepositoryInitializationAction implements InitializationAction {

  private static final Log LOG = LogFactory
      .getLog(RepositoryInitializationAction.class);
  private Repository repository;
  private List<StartupAction> startupActions;

  /**
   * Create the repository initialization action.
   */
  @Inject
  public RepositoryInitializationAction(JCRService jcrService,
      List<StartupAction> startupActions) {
    this.repository = jcrService.getRepository();
    this.startupActions = startupActions;

  }

  /**
   * {@inheritDoc}
   * 
   * @throws RepositoryStartupException
   * @see org.sakaiproject.kernel.internal.api.InitializationAction#init()
   */
  public void init() throws RepositoryStartupException {

    SakaiJCRCredentials ssp = new SakaiJCRCredentials();
    Session s = null;
    try {
      s = repository.login(ssp);
      LOG.info("Starting " + startupActions);
      if (startupActions != null) {
        for (Iterator<StartupAction> i = startupActions.iterator(); i.hasNext();) {
          i.next().startup(s);
        }
      }
      s.save();
    } catch (RepositoryException e) {
      throw new RepositoryStartupException(
          "Failed to initialization on respository ", e);
    } finally {
      s.logout();
    }

  }

}