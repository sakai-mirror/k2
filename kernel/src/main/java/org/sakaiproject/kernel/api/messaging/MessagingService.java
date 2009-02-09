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
package org.sakaiproject.kernel.api.messaging;

import org.sakaiproject.kernel.api.email.EmailMessage;

import java.io.Serializable;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

/**
 *
 */
public interface MessagingService {
  /**
   * Send a given message.
   *
   * @param msg
   */
  void send(Message msg);

  /**
   * Creates a {@link javax.jms.BytesMessage} object.
   *
   * @return
   * @throws JMSException
   */
  BytesMessage createBytesMessage() throws JMSException;

  /**
   * Creates a {@link javax.jms.MapMessage} object.
   *
   * @return
   * @throws JMSException
   */
  MapMessage createMapMessage() throws JMSException;

  /**
   * Creates a {@link javax.jms.Message} object.
   *
   * @return
   * @throws JMSException
   */
  Message createMessage() throws JMSException;

  /**
   * Creates a {@link javax.jms.ObjectMessage} object.
   *
   * @return
   * @throws JMSException
   */
  ObjectMessage createObjectMessage() throws JMSException;

  /**
   * Creates a initialized {@link javax.jms.ObjectMessage} object.
   *
   * @return
   * @throws JMSException
   */
  ObjectMessage createObjectMessage(Serializable init)
      throws JMSException;

  /**
   * Creates a {@link javax.jms.StreamMessage} object.
   *
   * @return
   * @throws JMSException
   */
  StreamMessage createStreamMessage() throws JMSException;

  /**
   * Creates a {@link javax.jms.TextMessage} object.
   *
   * @return
   * @throws JMSException
   */
  TextMessage createTextMessage() throws JMSException;

  /**
   * Creates a initialized {@link javax.jms.TextMessage} object.
   *
   * @return
   * @throws JMSException
   */
  TextMessage createTextMessage(String text) throws JMSException;

  /**
   * Creates a {@link org.sakaiproject.kernel.api.email.EmailMessage} object.
   *
   * @return
   * @throws JMSException
   */
  EmailMessage createEmailMessage() throws JMSException;
}
