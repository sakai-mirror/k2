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
package org.sakaiproject.kernel.initialization;

import com.google.inject.Inject;

import org.sakaiproject.kernel.internal.api.InitializationAction;
import org.sakaiproject.kernel.internal.api.KernelInitialization;
import org.sakaiproject.kernel.internal.api.KernelInitializtionException;

import java.util.List;

/**
 * Perform kernel initialization, based on list of actions.
 */
public class KernelInitializationImpl implements KernelInitialization {

  /**
   * The list of actions necessary to initialize the kernel.
   */
  private List<InitializationAction> actions;

  /**
   * 
   */
  @Inject
  public KernelInitializationImpl(List<InitializationAction> actions) {
    this.actions = actions;
  }

  /**
   * {@inheritDoc}
   * 
   * @throws KernelInitializtionException
   * @see org.sakaiproject.kernel.internal.api.KernelInitialization#init()
   */
  public void initKernel() throws KernelInitializtionException {
    for (InitializationAction action : actions) {
      action.init();
    }
  }

}