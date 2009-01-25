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

import org.apache.jackrabbit.core.NamespaceRegistryImpl;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.apache.jackrabbit.core.config.WorkspaceConfig;
import org.apache.jackrabbit.core.fs.FileSystem;
import org.apache.jackrabbit.core.security.AuthContext;

import javax.jcr.AccessDeniedException;
import javax.jcr.RepositoryException;

/**
 * Extends the standard repository impl to allow the Guice injector to be passed
 * through.
 */
public class SakaiRepositoryImpl extends RepositoryImpl {

  private Injector injector;

  /**
   * @param repConfig
   * @throws RepositoryException
   */
  public SakaiRepositoryImpl(RepositoryConfig repConfig, Injector injector)
      throws RepositoryException {
    super(repConfig);
    this.injector = injector;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.apache.jackrabbit.core.RepositoryImpl#createSessionInstance(org.apache.jackrabbit.core.security.AuthContext,
   *      org.apache.jackrabbit.core.config.WorkspaceConfig)
   */
  @Override
  protected SessionImpl createSessionInstance(AuthContext loginContext,
      WorkspaceConfig wspConfig) throws AccessDeniedException,
      RepositoryException {
    return new SakaiXASessionImpl(this, injector, loginContext, wspConfig);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.apache.jackrabbit.core.RepositoryImpl#getFileSystem()
   */
  @Override
  protected FileSystem getFileSystem() {
    return super.getFileSystem();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.apache.jackrabbit.core.RepositoryImpl#getNamespaceRegistry()
   */
  @Override
  protected NamespaceRegistryImpl getNamespaceRegistry() {
    return super.getNamespaceRegistry();
  }

}
