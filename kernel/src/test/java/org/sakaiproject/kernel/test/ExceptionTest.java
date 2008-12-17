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
package org.sakaiproject.kernel.test;

import org.junit.Test;
import org.sakaiproject.kernel.api.UpdateFailedException;
import org.sakaiproject.kernel.api.authz.PermissionDeniedException;
import org.sakaiproject.kernel.api.authz.UnauthorizedException;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
import org.sakaiproject.kernel.api.user.AuthenticationException;
import org.sakaiproject.kernel.api.user.UserNotDefinedException;
import org.sakaiproject.kernel.internal.api.KernelInitializtionException;
import org.sakaiproject.kernel.jcr.api.internal.RepositoryStartupException;


/**
 * This class binds and tests exceptions, mainly to ensure that they can be constructed.
 */
public class ExceptionTest {

  @Test
  public void testRepositroyStartupException() {
    RepositoryStartupException e = new RepositoryStartupException();
    @SuppressWarnings("unused")
    RepositoryStartupException e1 = new RepositoryStartupException("Startup Test");
    @SuppressWarnings("unused")
    RepositoryStartupException e2 = new RepositoryStartupException(e);
    @SuppressWarnings("unused")
    RepositoryStartupException e3 = new RepositoryStartupException("Startup Test",e);
  }
  @Test
  public void testKernelInitializationException() {
    KernelInitializtionException e = new KernelInitializtionException();
    @SuppressWarnings("unused")
    KernelInitializtionException e1 = new KernelInitializtionException("Startup Test");
    @SuppressWarnings("unused")
    KernelInitializtionException e2 = new KernelInitializtionException(e);
    @SuppressWarnings("unused")
    KernelInitializtionException e3 = new KernelInitializtionException("Startup Test",e);
  }
  @Test
  public void testAuthenticationException() {
    AuthenticationException e = new AuthenticationException();
    @SuppressWarnings("unused")
    AuthenticationException e1 = new AuthenticationException("Startup Test");
    @SuppressWarnings("unused")
    AuthenticationException e2 = new AuthenticationException(e);
    @SuppressWarnings("unused")
    AuthenticationException e3 = new AuthenticationException("Startup Test",e);
  }
  @Test
  public void testUserNotDefinedException() {
    UserNotDefinedException e = new UserNotDefinedException();
    @SuppressWarnings("unused")
    UserNotDefinedException e1 = new UserNotDefinedException("Startup Test");
    @SuppressWarnings("unused")
    UserNotDefinedException e2 = new UserNotDefinedException(e);
    @SuppressWarnings("unused")
    UserNotDefinedException e3 = new UserNotDefinedException("Startup Test",e);
  }
  @Test
  public void testJCRNodeFactoryServiceException() {
    JCRNodeFactoryServiceException e = new JCRNodeFactoryServiceException();
    @SuppressWarnings("unused")
    JCRNodeFactoryServiceException e1 = new JCRNodeFactoryServiceException("Startup Test");
    @SuppressWarnings("unused")
    JCRNodeFactoryServiceException e2 = new JCRNodeFactoryServiceException(e);
    @SuppressWarnings("unused")
    JCRNodeFactoryServiceException e3 = new JCRNodeFactoryServiceException("Startup Test",e);
  }
  @Test
  public void testPermissionDeniedException() {
    PermissionDeniedException e = new PermissionDeniedException();
    @SuppressWarnings("unused")
    PermissionDeniedException e1 = new PermissionDeniedException("Startup Test");
    @SuppressWarnings("unused")
    PermissionDeniedException e2 = new PermissionDeniedException(e);
    @SuppressWarnings("unused")
    PermissionDeniedException e3 = new PermissionDeniedException("Startup Test",e);
  }
  @Test
  public void testUnauthorizedException() {
    UnauthorizedException e = new UnauthorizedException();
    @SuppressWarnings("unused")
    UnauthorizedException e1 = new UnauthorizedException("Startup Test");
    @SuppressWarnings("unused")
    UnauthorizedException e2 = new UnauthorizedException(e);
    @SuppressWarnings("unused")
    UnauthorizedException e3 = new UnauthorizedException("Startup Test",e);
  }
  @Test
  public void testUpdateFailedException() {
    UpdateFailedException e = new UpdateFailedException();
    @SuppressWarnings("unused")
    UpdateFailedException e1 = new UpdateFailedException("Startup Test");
    @SuppressWarnings("unused")
    UpdateFailedException e2 = new UpdateFailedException(e);
    @SuppressWarnings("unused")
    UpdateFailedException e3 = new UpdateFailedException("Startup Test",e);
  }
}
