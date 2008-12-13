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
import org.sakaiproject.kernel.api.jcr.EventRegistration;
import org.sakaiproject.kernel.jcr.api.internal.RepositoryStartupException;
import org.sakaiproject.kernel.jcr.api.internal.StartupAction;

import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.ObservationManager;

/**
 * A startup action that registers events, should probably be used after the
 * repo is started so all the types exist.
 */
public class RegisterEventListeners implements StartupAction {

  private static final Log LOG = LogFactory.getLog(RegisterEventListeners.class);
  private List<EventRegistration> eventRegistrations;

  /**
   * 
   */
  @Inject
  public RegisterEventListeners(List<EventRegistration> eventRegistrations) {
    this.eventRegistrations = eventRegistrations;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.jcr.api.internal.StartupAction#startup(javax.jcr.Session)
   */
  public void startup(Session s) throws RepositoryStartupException {
    LOG.info("Registering Repository Event Listeners ");
    try {
      ObservationManager observationManager = s.getWorkspace()
          .getObservationManager();
      for (EventRegistration eventRegistration : eventRegistrations) {
        eventRegistration.register(observationManager);
      }
    } catch (RepositoryException re) {
      throw new RepositoryStartupException("Failed to register Listeners ", re);
    }

  }

}
