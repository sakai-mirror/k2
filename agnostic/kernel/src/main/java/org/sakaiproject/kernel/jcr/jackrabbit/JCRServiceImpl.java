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
import com.google.inject.name.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.RequiresStop;
import org.sakaiproject.kernel.api.jcr.JCRService;
import org.sakaiproject.kernel.api.thread.ThreadLocalManager;

import javax.jcr.Credentials;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class JCRServiceImpl implements JCRService, RequiresStop {
  private static final Log log = LogFactory.getLog(JCRServiceImpl.class);

  public static final String DEFAULT_WORKSPACE = "sakai";

  /**
   * The injected 170 repository
   */
  private RepositoryBuilder repositoryBuilder = null;

  private ThreadLocal<SessionHolder> sessionHolder = new ThreadLocal<SessionHolder>();

  private Credentials repositoryCredentials;

  private boolean requestScope = true;

  private ThreadLocalManager threadLocalManager;

  /**
   * 
   */
  @Inject
  public JCRServiceImpl(RepositoryBuilder repositoryBuilder,
      Credentials repositoryCredentials, ThreadLocalManager threadLocalManager,
      @Named(JCRService.REQUEST_SCOPE_NAME) boolean requestScope) {
    this.repositoryBuilder = repositoryBuilder;
    this.repositoryCredentials = repositoryCredentials;
    this.threadLocalManager = threadLocalManager;
    this.requestScope = requestScope;

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.sakaiproject.kernel.api.RequiresStop#stop()
   */
  public void stop() {
    repositoryBuilder.stop();
  }

  public Session getSession() throws LoginException, RepositoryException {
    return login();
  }

  public Session login() throws LoginException, RepositoryException {
    Session session = null;
    if (requestScope) {

      SessionHolder sh = (SessionHolder) threadLocalManager.get("jcrsession");
      if (sh == null) {
        long t1 = System.currentTimeMillis();
        sh = new SessionHolder(repositoryBuilder, repositoryCredentials,
            DEFAULT_WORKSPACE);
        threadLocalManager.set("jcrsession", sh);
        if (log.isDebugEnabled())
          log.debug("Session Start took " + (System.currentTimeMillis() - t1)
              + "ms");
      }
      session = sh.getSession();
    } else {
      SessionHolder sh = sessionHolder.get();
      if (sh == null) {
        sh = new SessionHolder(repositoryBuilder, repositoryCredentials,
            DEFAULT_WORKSPACE);
        sessionHolder.set(sh);
      }
      session = sh.getSession();
    }
    return session;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.sakaiproject.kernel.api.jcr.JCRService#logout()
   */
  public void logout() throws LoginException, RepositoryException {
    threadLocalManager.set("jcrsession", null);
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
    if (requestScope) {
      SessionHolder sh = (SessionHolder) threadLocalManager.get("jcrsession");
      if (sh != null) {

        currentSession = sh.getSession();
        sh.keepLoggedIn();
      }
      if (session == null) {
        threadLocalManager.set("jcrsession", null);
      } else {
        sh = new SessionHolder(session);
        threadLocalManager.set("jcrsession", sh);
      }
    } else {
      SessionHolder sh = sessionHolder.get();
      if (sh != null) {
        currentSession = sh.getSession();
      }
      if (session == null) {
        sessionHolder.set(null);
      } else {
        sh = new SessionHolder(session);
        sessionHolder.set(sh);
      }
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
    if (requestScope) {

      SessionHolder sh = (SessionHolder) threadLocalManager.get("jcrsession");
      if (sh != null) {
        session = sh.getSession();
      }
    } else {
      SessionHolder sh = sessionHolder.get();
      if (sh != null) {
        session = sh.getSession();
      }
    }
    return (session != null);
  }


}
