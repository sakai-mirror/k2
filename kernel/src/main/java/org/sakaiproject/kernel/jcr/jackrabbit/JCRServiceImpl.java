/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2007, 2008 Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.kernel.jcr.jackrabbit;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.RequiresStop;
import org.sakaiproject.kernel.api.jcr.JCRService;
import org.sakaiproject.kernel.api.memory.Cache;
import org.sakaiproject.kernel.api.memory.CacheManagerService;
import org.sakaiproject.kernel.api.memory.CacheScope;

import javax.jcr.Credentials;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

@Singleton
public class JCRServiceImpl implements JCRService, RequiresStop {
  private static final Log LOG = LogFactory.getLog(JCRServiceImpl.class);

  public static final String DEFAULT_WORKSPACE = "sakai";

  private static final String JCR_REQUEST_CACHE = "jcr.rc";

  private static final String JCR_SESSION_HOLDER = "sh";


  /**
   * The injected 170 repository
   */
  private RepositoryBuilder repositoryBuilder = null;

  private Credentials repositoryCredentials;

  private boolean requestScope = true;

  private CacheManagerService cacheManager;

  /**
   * 
   */
  @Inject
  public JCRServiceImpl(RepositoryBuilder repositoryBuilder,
      @Named(JCRService.NAME_CREDENTIALS) Credentials repositoryCredentials,
      CacheManagerService cacheManager,
      @Named(JCRService.NAME_REQUEST_SCOPE) boolean requestScope) {
    this.repositoryBuilder = repositoryBuilder;
    this.repositoryCredentials = repositoryCredentials;
    this.cacheManager = cacheManager;
    this.requestScope = requestScope;

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.sakaiproject.kernel.api.RequiresStop#stop()
   */
  public void stop() {
    repositoryBuilder.stop();
    LOG.info("Repository has been stopped");
  }

  public Session getSession() throws LoginException, RepositoryException {
    return login();
  }

  public Session login() throws LoginException, RepositoryException {
    Session session = null;
    SessionHolder sh = getSessionHolder();
    if (sh == null) {
      long t1 = System.currentTimeMillis();
      sh = new SessionHolder(repositoryBuilder, repositoryCredentials,
          DEFAULT_WORKSPACE);
      setSesssionHolder(sh);
      if (LOG.isDebugEnabled())
        LOG.debug("Session Start took " + (System.currentTimeMillis() - t1)
            + "ms");
    }
    session = sh.getSession();
    return session;
  }

  /**
   * @return
   */
  private Cache<Object> getRequestCache() {
    if (requestScope) {
      return cacheManager.getCache(JCR_REQUEST_CACHE, CacheScope.REQUEST);
    } else {
      return cacheManager.getCache(JCR_REQUEST_CACHE, CacheScope.THREAD);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.sakaiproject.kernel.api.jcr.JCRService#logout()
   */
  public void logout() throws LoginException, RepositoryException {
    clearSessionHolder();
  }

  /**
   * @param jcrSessionHolder
   * @return
   */
  private SessionHolder getSessionHolder() {
    return (SessionHolder) getRequestCache().get(JCR_SESSION_HOLDER);
  }

  /**
   * @param sh
   */
  private void setSesssionHolder(SessionHolder sh) {
    getRequestCache().put(JCR_SESSION_HOLDER, sh);
  }

  /**
   * 
   */
  private void clearSessionHolder() {
    getRequestCache().remove(JCR_SESSION_HOLDER);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.sakaiproject.kernel.api.jcr.JCRService#getRepository()
   */
  public Repository getRepository() {
    return repositoryBuilder.getInstance();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.sakaiproject.kernel.api.jcr.JCRService#setCurrentSession(javax.jcr.
   * Session)
   */
  public Session setSession(Session session) {
    Session currentSession = null;
    SessionHolder sh = getSessionHolder();
    if (sh != null) {
      currentSession = sh.getSession();
      sh.keepLoggedIn();
    }
    if (session == null) {
      clearSessionHolder();
    } else {
      sh = new SessionHolder(session);
      setSesssionHolder(sh);
    }
    return currentSession;
  }

  public boolean needsMixin(Node node, String mixin) throws RepositoryException {
    return true;
    // ! node.getSession().getWorkspace().getNodeTypeManager().getNodeType(node.
    // getPrimaryNodeType().getName()).isNodeType(mixin);
  }

  public boolean hasActiveSession() {
    Session session = null;
    SessionHolder sh = getSessionHolder();
    if (sh != null) {
      session = sh.getSession();
    }
    return (session != null);
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.jcr.JCRService#getDefaultWorkspace()
   */
  public String getDefaultWorkspace() {
    return DEFAULT_WORKSPACE;
  }

}
