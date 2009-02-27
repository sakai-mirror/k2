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

import javax.jms.ConnectionFactory;
import javax.jms.ObjectMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.sakaiproject.kernel.api.messaging.EmailMessage;
import org.sakaiproject.kernel.api.messaging.Message;
import org.sakaiproject.kernel.api.messaging.MessagingService;
import org.sakaiproject.kernel.api.messaging.MultipartMessage;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Named;

/**
 *
 */
public class JmsMessagingService implements MessagingService {
  public static final String PROP_ACTIVEMQ_BROKER_URL = "jms.brokerurl";

  protected ActiveMQConnectionFactory connectionFactory = null;

  private Injector injector = null;

  @Inject
  public JmsMessagingService(@Named(PROP_ACTIVEMQ_BROKER_URL) String brokerUrl, Injector injector) {
    connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
    this.injector = injector;
  }

  public JmsMessagingService(ActiveMQConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
  }

  public JmsMessagingService(ConnectionFactory connectionFactory) {
    this.connectionFactory = (ActiveMQConnectionFactory) connectionFactory;
  }

  public ConnectionFactory getConnectionFactory() {
    return (ConnectionFactory) connectionFactory;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.messaging.MessagingService#send(javax.jms.Message)
   */
  public void send(Message msg) {
  }

  public ObjectMessage createObjectMessage() {
    // TODO Auto-generated method stub
    return null;
  }

  public EmailMessage createEmailMessage() {
    EmailMessage em = injector.getInstance(Key.get(EmailMessage.class));
    return em;
  }

  public Message createMessage() {
    Message m = injector.getInstance(Key.get(MessageImpl.class));
    return m;
  }

  public MultipartMessage createMultipartMessage() {
    MultipartMessage mm = injector.getInstance(Key.get(MultipartMessage.class));
    return mm;
  }

}
