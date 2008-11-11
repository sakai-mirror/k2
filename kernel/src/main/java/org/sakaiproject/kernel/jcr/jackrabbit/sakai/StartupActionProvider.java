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

import com.google.inject.Inject;
import com.google.inject.Provider;

import org.sakaiproject.kernel.jcr.api.internal.StartupAction;

import java.util.ArrayList;
import java.util.List;

/**
 * A provider for a list of JCR Startup actions.
 */
public class StartupActionProvider implements Provider<List<StartupAction>> {

  /**
   * The list of actions.
   */
  private List<StartupAction> startupActions;

  /**
   * Create a list of startup actions for the JCR repository.
   */
  @Inject
  public StartupActionProvider(SakaiRepositoryStartup repositoryStartup,
      PopulateBaseRepository populateBaseRepository) {
    startupActions = new ArrayList<StartupAction>();
    startupActions.add(repositoryStartup);
    startupActions.add(populateBaseRepository);
  }

  /**
   * {@inheritDoc}
   * 
   * @see com.google.inject.Provider#get()
   */
  public List<StartupAction> get() {
    return startupActions;
  }

}