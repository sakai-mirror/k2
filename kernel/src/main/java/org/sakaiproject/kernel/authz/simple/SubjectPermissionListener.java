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
package org.sakaiproject.kernel.authz.simple;

import com.google.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.jcr.EventRegistration;
import org.sakaiproject.kernel.api.jcr.JCRConstants;

import javax.jcr.RepositoryException;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.EventListenerIterator;
import javax.jcr.observation.ObservationManager;

/**
 * 
 */
public class SubjectPermissionListener implements EventListener,
    EventRegistration {

  private static final Log LOG = LogFactory
      .getLog(SubjectPermissionListener.class);

  /**
   * @throws RepositoryException
   * 
   */
  @Inject
  public SubjectPermissionListener() throws RepositoryException {
  }

  /**
   * {@inheritDoc}
   * 
   * @throws RepositoryException
   * 
   * @see org.sakaiproject.kernel.api.jcr.EventRegistration#register(javax.jcr.observation.ObservationManager)
   */
  public void register(ObservationManager observationManager)
      throws RepositoryException {
    observationManager.addEventListener(this, Event.PROPERTY_ADDED
        | Event.PROPERTY_CHANGED | Event.PROPERTY_REMOVED, "/", true, null,
        new String[] { JCRConstants.NT_RESOURCE }, false);
    for (EventListenerIterator iterator = observationManager
        .getRegisteredEventListeners(); iterator.hasNext();) {
      LOG.info("Registered Event Listener " + iterator.nextEventListener());
    }

    LOG.info("Registerd SubjectPermissionListener with "+observationManager);
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.jcr.observation.EventListener#onEvent(javax.jcr.observation.EventIterator)
   */
  public void onEvent(EventIterator events) {
    System.err.println("Got JCR Event " + events);
  }
}
