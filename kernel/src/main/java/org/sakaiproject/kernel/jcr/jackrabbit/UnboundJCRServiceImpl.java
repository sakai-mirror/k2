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
package org.sakaiproject.kernel.jcr.jackrabbit;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.core.observation.EventImpl;
import org.sakaiproject.kernel.api.jcr.JCRService;
import org.sakaiproject.kernel.jcr.jackrabbit.sakai.SakaiJCRCredentials;

import javax.jcr.Credentials;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.ObservationManager;

/* This is mostly the same as the JCRServiceImpl, except you are never bound to a 
 * particular thread.  You will need to keep track of your session, and use the logout
 * methods available on JSR-170. 
 */
public class UnboundJCRServiceImpl implements JCRService {

  private static final Log log = LogFactory.getLog(UnboundJCRServiceImpl.class);

  public static final String DEFAULT_WORKSPACE = "sakai";

  /**
   * The injected 170 repository
   */
  private RepositoryBuilder repositoryBuilder = null;

  private Credentials repositoryCredentials;

  @Inject
  public UnboundJCRServiceImpl(RepositoryBuilder repositoryBuilder,
      @Named(JCRService.NAME_CREDENTIALS) Credentials repositoryCredentials, 
      @Named(JCRService.NAME_REQUEST_SCOPE) boolean requestScope) {

    boolean error = false;
    try {
      if (repositoryBuilder == null) {
        log.error("Repository has not been set ");
        error = true;
      }
      if (repositoryCredentials == null) {
        log.error("Credentials has not been set ");
        error = true;
      }
    } catch (Throwable t) {
      log.error("Failed init(): ", t);
      error = true;
    } finally {
      if (error) {
        throw new RuntimeException(
            "Fatal error initialising JCRService... (see previous logged ERROR for details)");
        // System.exit is not a good idea to use, it causes everything
        // to die instead of shutting down -AZ
        // System.exit(-1);
      }
    }
    log.info("JCR Service initialised...");
  }

  /**
   * Returns to uninitialized state.
   */
  public void destroy() {
    // repositoryBuilder.destroy();
    log.info("destroy()");
  }

  public Session getSession() throws LoginException, RepositoryException {
    return login();
  }

  public Session login() throws LoginException, RepositoryException {
    Repository repository = repositoryBuilder.getInstance();
    return repository.login();
  }
  
  public Session loginSystem() throws LoginException, RepositoryException {
    Repository repository = repositoryBuilder.getInstance();
    return repository.login(repositoryCredentials);
  }

  /*
   * See JSR-170 for how to use session.logout()
   */
  public void logout() throws LoginException, RepositoryException {
  }

  public Repository getRepository() {
    return repositoryBuilder.getInstance();
  }

  public Session setSession(Session session) {
    return session;
  }

  public boolean needsMixin(Node node, String mixin) throws RepositoryException {
    return true;
  }

  public boolean hasActiveSession() {
    return false;
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.jcr.JCRService#getDefaultWorkspace()
   */
  public String getDefaultWorkspace() {
    return DEFAULT_WORKSPACE;
  }
  
  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.jcr.JCRService#getObservationManager()
   */
  public ObservationManager getObservationManager() {
    SakaiJCRCredentials ssp = new SakaiJCRCredentials();
    Session s = null;
    ObservationManager observationManager = null;
    try {
      s = getRepository().login(ssp);
      observationManager = s.getWorkspace().getObservationManager();
    } catch (RepositoryException e) {
      log.error("Failed to get ObservationManager from workspace");
      e.printStackTrace();
    } finally {
      try {
        s.logout();
      } catch (Exception ex) {
      }
      ;
    }
    return observationManager;
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.jcr.JCRService#isExternalEvent(javax.jcr.observation.Event)
   */
  public boolean isExternalEvent(Event event) {
    EventImpl eventImpl = (EventImpl) event;
    return eventImpl.isExternal();
  }

}
