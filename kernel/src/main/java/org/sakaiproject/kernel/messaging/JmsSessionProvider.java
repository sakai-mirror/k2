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
import com.google.inject.Provider;
import com.google.inject.name.Named;

import javax.jms.ConnectionFactory;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * Provide a configured JMS Session for Guice.
 */
public class JmsSessionProvider implements Provider<Session> {

  private ConnectionFactory connectionFactory;

  @Inject
  public JmsSessionProvider(
      @Named(JmsMessagingService.PROP_ACTIVEMQ_BROKER_URL) String url) {
    connectionFactory = new ActiveMQConnectionFactory(url);
  }

  public JmsSessionProvider() {
    // TODO Auto-generated constructor stub
  }

  public Session get() {
    return null;
  }
}
