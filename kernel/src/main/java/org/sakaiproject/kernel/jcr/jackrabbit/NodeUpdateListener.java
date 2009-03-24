/*
 * Licensed to the Sakai Foundation (SF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The SF licenses this file
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
import org.apache.jackrabbit.core.observation.EventImpl;
import org.sakaiproject.kernel.api.jcr.EventRegistration;
import org.sakaiproject.kernel.api.jcr.JCRConstants;
import org.sakaiproject.kernel.api.jcr.JCRService;
import org.sakaiproject.kernel.api.locking.Lock;
import org.sakaiproject.kernel.api.locking.LockTimeoutException;
import org.sakaiproject.kernel.api.memory.CacheManagerService;
import org.sakaiproject.kernel.api.memory.CacheScope;
import org.sakaiproject.kernel.util.PathUtils;

import java.util.GregorianCalendar;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;

/**
 * 
 */
public class NodeUpdateListener implements EventListener, EventRegistration {

  private static final Log LOG = LogFactory.getLog(NodeUpdateListener.class);
  private JCRService jcrService;
  private CacheManagerService cacheManagerService;

  /**
   * @param listeners
   * @throws RepositoryException
   * 
   */
  @Inject
  public NodeUpdateListener(JCRService jcrService,
      CacheManagerService cacheManagerService)
      throws RepositoryException {
    this.jcrService = jcrService;
    this.cacheManagerService = cacheManagerService;
  }

  /**
   * {@inheritDoc}
   * 
   * @throws RepositoryException
   * 
   * @see org.sakaiproject.kernel.api.jcr.EventRegistration#register(javax.jcr.observation.ObservationManager)
   */
  public void register(ObservationManager observationManager) throws RepositoryException {
    observationManager.addEventListener(this, Event.PROPERTY_ADDED
        | Event.PROPERTY_CHANGED | Event.NODE_ADDED, "/", true, null, new String[] {
        JCRConstants.NT_FILE, JCRConstants.NT_FOLDER}, false);
    LOG.info("Registered " + this.getClass().getName());
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.jcr.observation.EventListener#onEvent(javax.jcr.observation.EventIterator)
   */
  public void onEvent(EventIterator events) {
    try {
      Session s = jcrService.loginSystem();

      for (; events.hasNext();) {

        EventImpl e = (EventImpl) events.nextEvent();
        if (e.isExternal()) {
          continue;
        }
        try {
          String nodePath = e.getPath();
          switch (e.getType()) {
          case Event.PROPERTY_ADDED:
          case Event.PROPERTY_CHANGED:
            nodePath = PathUtils.getParentReference(nodePath);
          }
          if (nodePath.endsWith(JCRConstants.JCR_CONTENT)) {
            nodePath = PathUtils.getParentReference(nodePath);
          }
          Session session = jcrService.getSession();
          Node n = (Node) session.getItem(nodePath);

          if (!n.hasProperty(JCRConstants.JCR_CREATEDBY)) {
            Lock lock = jcrService.lock(n);
            try {
              n.setProperty(JCRConstants.JCR_CREATEDBY, e.getUserID());
              LOG.info("Node created by " + e.getUserID());
              if (!n.hasProperty(JCRConstants.JCR_CREATED)) {
                GregorianCalendar now = new GregorianCalendar();
                n.setProperty(JCRConstants.JCR_CREATED, now);
              }
              n.save();
            } finally {
              lock.unlock();
            }
          } else {
            if (!n.hasProperty(JCRConstants.JCR_MODIFIEDBY)) {
              Lock lock = jcrService.lock(n);
              try {
                n.setProperty(JCRConstants.JCR_MODIFIEDBY, e.getUserID());
                LOG.info("Node modified by " + e.getUserID());
                n.save();
              } finally {
                lock.unlock();
              }

            } else {
              String userId = n.getProperty(JCRConstants.JCR_MODIFIEDBY).getString();
              if (!s.getUserID().equals(e.getUserID()) && !e.getUserID().equals(userId) ) {
                Lock lock = jcrService.lock(n);
                try {
                  n.setProperty(JCRConstants.JCR_MODIFIEDBY, e.getUserID());
                  LOG.info("Node modified by " + e.getUserID() + " previously " + userId);
                  n.save();
                } finally {
                  lock.unlock();
                }
              }
            }
          }
        } catch (LockTimeoutException t) {
          LOG.error("Failed to get lock on node "+t.getMessage());
        } catch (RepositoryException ex) {
          LOG.error("Failed to update node ", ex);
        }
      }
      s.save();
    } catch (LoginException e) {
      LOG.warn("Cant Login to JCR " + e.getMessage(), e);
    } catch (RepositoryException e) {
      LOG.warn("Cant Login to JCR " + e.getMessage(), e);
    } finally {
      try {
        jcrService.logout();
      } catch (LoginException e) {
        LOG.warn("Cant Logout of JCR " + e.getMessage(), e);
      } catch (RepositoryException e) {
        LOG.warn("Cant Logout of JCR " + e.getMessage(), e);
      }
      try {
        cacheManagerService.unbind(CacheScope.REQUEST);
      } catch (Exception ex) {
        LOG.warn("Exception unbinding cache manager from request.");
      }
      try {
        cacheManagerService.unbind(CacheScope.THREAD);
      } catch (Exception ex) {
        LOG.warn("Exception unbinding cache manager from thread.");
      }
    }
  }

}
