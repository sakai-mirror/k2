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
import com.google.inject.Key;
import com.google.inject.name.Named;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
import org.sakaiproject.kernel.api.messaging.EmailMessage;
import org.sakaiproject.kernel.api.messaging.Message;
import org.sakaiproject.kernel.api.messaging.MessagingService;
import org.sakaiproject.kernel.api.messaging.MultipartMessage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jms.ConnectionFactory;
import javax.jms.ObjectMessage;

/**
 *
 */
public class JmsMessagingService implements MessagingService {
  public static final String PROP_ACTIVEMQ_BROKER_URL = "jms.brokerurl";

  protected ActiveMQConnectionFactory connectionFactory = null;

  private Injector injector = null;

  @Inject
  public JmsMessagingService(@Named(PROP_ACTIVEMQ_BROKER_URL) String brokerUrl,
      Injector injector) {
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
    return connectionFactory;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.messaging.MessagingService#send(javax.jms.Message)
   */
  public void send(final Message msg) {
    JCRNodeFactoryService jcrNodeFactoryService = injector
        .getInstance(JCRNodeFactoryService.class);
    // FIXME get the real path for the outbox
    String path = "REPLACETHIS/userenv/messages/outbox";
    try {
      PipedInputStream pis = new PipedInputStream();
      PipedOutputStream pos = new PipedOutputStream(pis);
      final ObjectOutputStream oos = new ObjectOutputStream(pos);
      // Write to the output stream in another thread so it doesn't all have to buffer the entire thing into memory
      // see http://ostermiller.org/convert_java_outputstream_inputstream.html
      new Thread(
        new Runnable() {
          public void run(){
            try {
              oos.writeObject(msg);
            } catch (IOException e) {
              //FIXME do something here
            }
          }
        }
      ).start();

      Node n = jcrNodeFactoryService.setInputStream(path, pis,
          "application/x-java-serialized-object");
      oos.flush();
      pos.close();
      oos.close();
      n.save();
    } catch (JCRNodeFactoryServiceException e) {
      // FIXME do something here
    } catch (RepositoryException e) {
      // FIXME do something here
    } catch (IOException e) {
      // FIXME do something here
    }
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
