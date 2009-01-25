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
package org.sakaiproject.kernel.jcr.jackrabbit.sakai;

import com.google.inject.Injector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.core.HierarchyManager;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.XASessionImpl;
import org.apache.jackrabbit.core.config.WorkspaceConfig;
import org.apache.jackrabbit.core.security.AMContext;
import org.apache.jackrabbit.core.security.AccessManager;
import org.apache.jackrabbit.core.security.AuthContext;

import java.io.File;

import javax.jcr.AccessDeniedException;
import javax.jcr.RepositoryException;
import javax.security.auth.Subject;

/**
 * This JCR Session impl overrides the XASession impl to allow us to create an
 * injected access manager from Guice. This will allow the Access Manager to
 * call on whatever core services it requires.
 */
public class SakaiXASessionImpl extends XASessionImpl {

  private static final Log LOG = LogFactory.getLog(SakaiXASessionImpl.class);
  private Injector injector;
  private static ThreadLocal<Injector> injectorHolder = new ThreadLocal<Injector>() {
    protected Injector initialValue() {
      return null;
    }
  };

  /**
   * @param rep
   * @param injector
   * @param loginContext
   * @param wspConfig
   * @throws AccessDeniedException
   * @throws RepositoryException
   */
  public SakaiXASessionImpl(SakaiRepositoryImpl rep, Injector injector,
      AuthContext loginContext, WorkspaceConfig wspConfig)
      throws AccessDeniedException, RepositoryException {
    super(prepareInjector(rep, injector), loginContext, wspConfig);
    setInjector(injector);
  }

  /**
   * Finish construction and set the injector
   * 
   * @param injector2
   */
  private void setInjector(Injector injector) {
    this.injector = injector;
    injectorHolder.set(null);
  }

  /**
   * Prepare the injector for construction
   * 
   * @param rep
   * @param injector2
   * @return
   */
  private static RepositoryImpl prepareInjector(SakaiRepositoryImpl rep,
      Injector injector) {
    injectorHolder.set(injector);
    return rep;
  }

  /**
   * Get the injector, either from the thread local or from the field. During
   * construction the thread local will be set and used.
   * 
   * @return
   */
  private Injector getInjector() {
    if (injector == null) {
      return injectorHolder.get();
    }
    return injector;
  }

  /**
   * @param rep
   * @param subject
   * @param wspConfig
   * @throws AccessDeniedException
   * @throws RepositoryException
   */
  public SakaiXASessionImpl(SakaiRepositoryImpl rep, Injector injector,
      Subject subject, WorkspaceConfig wspConfig) throws AccessDeniedException,
      RepositoryException {
    super(prepareInjector(rep, injector), subject, wspConfig);
    setInjector(injector);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.apache.jackrabbit.core.SessionImpl#createAccessManager(javax.security.auth.Subject,
   *      org.apache.jackrabbit.core.HierarchyManager)
   */
  @Override
  protected AccessManager createAccessManager(Subject subject,
      HierarchyManager hierMgr) throws AccessDeniedException,
      RepositoryException {
    // AccessManagerConfig amConfig = rep.getConfig().getAccessManagerConfig();
    try {

      AMContext ctx = new AMContext(new File(rep.getConfig().getHomeDir()),
          ((SakaiRepositoryImpl) rep).getFileSystem(), subject, hierMgr,
          ((SakaiRepositoryImpl) rep).getNamespaceRegistry(), wsp.getName());
      // inject the access manager so its part of Guice.

      AccessManager accessMgr = getInjector().getInstance(AccessManager.class);
      accessMgr.init(ctx);
      return accessMgr;
    } catch (AccessDeniedException ade) {
      // re-throw
      throw ade;
    } catch (Exception e) {
      // wrap in RepositoryException
      String msg = "failed to instantiate AccessManager implementation using the Guice Injector ";
      LOG.error(msg, e);
      throw new RepositoryException(msg, e);
    }
  }

}
