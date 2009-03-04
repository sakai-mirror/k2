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
package org.sakaiproject.kernel.messaging;

import com.google.inject.Inject;

import org.sakaiproject.kernel.KernelConstants;
import org.sakaiproject.kernel.api.Registry;
import org.sakaiproject.kernel.api.RegistryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
import org.sakaiproject.kernel.api.messaging.Message;
import org.sakaiproject.kernel.api.messaging.OutgoingMessageHandler;
import org.sakaiproject.kernel.jcr.api.JcrContentListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import javax.jcr.RepositoryException;

/**
 *
 */
public class OutboxListener implements JcrContentListener {

  private JCRNodeFactoryService jcrNodeFactory;
  private Registry<String, OutgoingMessageHandler> registry;

  @Inject
  public OutboxListener(JCRNodeFactoryService jcrNodeFactory,
      RegistryService registryService) {
    registry = registryService.getRegistry(OutgoingMessageHandler.REGISTRY);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.jcr.api.JcrContentListener#onEvent(int,
   *      java.lang.String, java.lang.String, java.lang.String)
   */
  public void onEvent(int type, String userID, String filePath, String fileName) {
    // make sure we deal only with outbox items
    if (filePath.endsWith(KernelConstants.OUTBOX)) {
      try {
        // get the input stream from jcr
        InputStream is = jcrNodeFactory.getInputStream(filePath + fileName);

        // convert to an object
        ObjectInputStream ois = new ObjectInputStream(is);
        Object obj = ois.readObject();

        // make sure we have message
        if (obj instanceof Message) {
          Message msg = (Message) obj;

          // call up the appropriate handler and pass off
          OutgoingMessageHandler handler = registry.getMap().get(msg.getType());
          if (handler != null) {
            handler.handle(userID, filePath, fileName, msg);
          }
        }
      } catch (IOException e) {
        //
      } catch (ClassNotFoundException e) {
        //
      } catch (JCRNodeFactoryServiceException e) {
        //
      } catch (RepositoryException e) {
        //
      }
    }
  }

}
