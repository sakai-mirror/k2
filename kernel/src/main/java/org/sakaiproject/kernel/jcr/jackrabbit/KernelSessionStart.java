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

import org.sakaiproject.kernel.internal.api.InitializationAction;
import org.sakaiproject.kernel.internal.api.KernelInitializtionException;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;

/**
 * 
 */
public class KernelSessionStart implements InitializationAction {

  private JCRServiceImpl jcrService;

  /**
   * 
   */
  @Inject
  public KernelSessionStart(JCRServiceImpl jcrService) {
    this.jcrService = jcrService;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.internal.api.InitializationAction#init()
   */
  public void init() throws KernelInitializtionException {
    try {
      jcrService.loginSystem();
    } catch (LoginException e) {
      throw new KernelInitializtionException(e.getMessage(), e);
    } catch (RepositoryException e) {
      throw new KernelInitializtionException(e.getMessage(), e);
    }
  }

}
