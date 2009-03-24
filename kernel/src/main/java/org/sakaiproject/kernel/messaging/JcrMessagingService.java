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
import com.google.inject.Injector;

import org.sakaiproject.kernel.api.jcr.JCRConstants;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
import org.sakaiproject.kernel.api.messaging.EmailMessage;
import org.sakaiproject.kernel.api.messaging.Message;
import org.sakaiproject.kernel.api.messaging.MessagingService;
import org.sakaiproject.kernel.api.serialization.BeanConverter;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 *
 */
public class JcrMessagingService implements MessagingService {
  public static final String PROP_ACTIVEMQ_BROKER_URL = "jms.brokerurl";

  private Injector injector;
  private JCRNodeFactoryService jcrNodeFactory;
  private BeanConverter beanConverter;

  @Inject
  public JcrMessagingService(JCRNodeFactoryService jcrNodeFactory,
      BeanConverter beanConverter,
      Injector injector) {
    this.jcrNodeFactory = jcrNodeFactory;
    this.injector = injector;
    this.beanConverter = beanConverter;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.MessagingService#send(javax.jms.Message)
   */
  public void send(Message msg) {
    // FIXME get the real path for the outbox
    String path = "REPLACETHIS/userenv/messages/outbox";
    try {
      String json = beanConverter.convertToString(msg);
      ByteArrayInputStream bais = new ByteArrayInputStream(json
          .getBytes("UTF-8"));
      Node n = jcrNodeFactory.setInputStream(path, bais, "application/json");
      n.setProperty(JCRConstants.JCR_MESSAGE_TYPE, msg.getType());
      n.setProperty(JCRConstants.JCR_MESSAGE_RCPTS, msg.getTo());
    } catch (JCRNodeFactoryServiceException e) {
      // FIXME do something here
    } catch (RepositoryException e) {
      // FIXME do something here
    } catch (IOException e) {
      // FIXME do something here
    }
  }

  public EmailMessage createEmailMessage() {
    EmailMessage em = injector.getInstance(EmailMessage.class);
    return em;
  }

  public Message createMessage() {
    Message m = injector.getInstance(Message.class);
    return m;
  }
}
