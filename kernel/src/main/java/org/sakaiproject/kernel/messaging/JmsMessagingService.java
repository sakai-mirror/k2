/*******************************************************************************
 * Copyright 2008 Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.sakaiproject.kernel.messaging;

import com.google.inject.Inject;

import org.sakaiproject.kernel.api.email.EmailMessage;
import org.sakaiproject.kernel.api.messaging.MessagingService;
import org.sakaiproject.kernel.messaging.email.JmsEmailMessage;

import java.io.Serializable;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

/**
 *
 */
public class JmsMessagingService implements MessagingService {
  private final Session session;

  @Inject
  public JmsMessagingService(Session session) {
    this.session = session;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.MessagingService#createBytesMessage()
   */
  public BytesMessage createBytesMessage() throws JMSException {
    return session.createBytesMessage();
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.MessagingService#createEmailMessage()
   */
  public EmailMessage createEmailMessage() throws JMSException {
    return new JmsEmailMessage(this);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.MessagingService#createMapMessage()
   */
  public MapMessage createMapMessage() throws JMSException {
    return session.createMapMessage();
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.MessagingService#createMessage()
   */
  public Message createMessage() throws JMSException {
    return session.createMessage();
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.MessagingService#createObjectMessage()
   */
  public ObjectMessage createObjectMessage() throws JMSException {
    return session.createObjectMessage();
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.MessagingService#createObjectMessage(java.io.Serializable)
   */
  public ObjectMessage createObjectMessage(Serializable init)
      throws JMSException {
    return session.createObjectMessage(init);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.MessagingService#createStreamMessage()
   */
  public StreamMessage createStreamMessage() throws JMSException {
    return session.createStreamMessage();
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.MessagingService#createTextMessage()
   */
  public TextMessage createTextMessage() throws JMSException {
    return session.createTextMessage();
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.MessagingService#createTextMessage(java.lang.String)
   */
  public TextMessage createTextMessage(String text) throws JMSException {
    return session.createTextMessage(text);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.MessagingService#send(javax.jms.Message)
   */
  public void send(Message msg) {
  }

}
