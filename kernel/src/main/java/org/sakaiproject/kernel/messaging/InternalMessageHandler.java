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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.Registry;
import org.sakaiproject.kernel.api.RegistryService;
import org.sakaiproject.kernel.api.jcr.JCRConstants;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
import org.sakaiproject.kernel.api.messaging.Message;
import org.sakaiproject.kernel.api.messaging.OutboxNodeHandler;
import org.sakaiproject.kernel.api.user.UserFactoryService;

import java.io.InputStream;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

public class InternalMessageHandler implements OutboxNodeHandler {

  private static final Log log = LogFactory
      .getLog(InternalMessageHandler.class);
  private static final String key = Message.Type.INTERNAL.toString();
  private static final int priority = 0;

  private final UserFactoryService userFactory;
  private final JCRNodeFactoryService nodeFactory;

  @Inject
  public InternalMessageHandler(RegistryService registryService,
      UserFactoryService userFactory, JCRNodeFactoryService nodeFactory) {
    Registry<String, OutboxNodeHandler> registry = registryService
        .getRegistry(OutboxNodeHandler.REGISTRY);
    registry.add(this);

    this.userFactory = userFactory;
    this.nodeFactory = nodeFactory;
  }

  public void handle(String userID, String filePath, String fileName, Node node) {
    try {
      Property prop = node.getProperty(JCRConstants.JCR_MESSAGE_RCPTS);
      String rcptsVal = prop.getString();
      String[] rcpts = StringUtils.split(rcptsVal, ",");

      for (String rcpt : rcpts) {
        /** FIXME set message path for the user. */
        String msgPath = userFactory.getNewMessagePath(rcpt);
        InputStream in = nodeFactory.getInputStream(node.getPath());
        nodeFactory.setInputStream(msgPath, in, "UTF-8");
        /** TODO remove any properties that are associated to the sender */
      }
    } catch (RepositoryException e) {
      log.error(e.getMessage(), e);
    } catch (JCRNodeFactoryServiceException e) {
      log.error(e.getMessage(), e);
    }
  }

  public String getKey() {
    return key;
  }

  public int getPriority() {
    return priority;
  }

}
