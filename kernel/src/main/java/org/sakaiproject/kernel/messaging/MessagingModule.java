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

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

import org.sakaiproject.kernel.api.messaging.MessagingService;
import org.sakaiproject.kernel.messaging.email.MailSessionProvider;

import java.util.Properties;

/**
 *
 */
public class MessagingModule extends AbstractModule {
  private Properties props;

  /**
   *
   */
  public MessagingModule() {
  }

  public MessagingModule(Properties props) {
    this.props = props;
  }

  /**
   * {@inheritDoc}
   *
   * @see com.google.inject.AbstractModule#configure()
   */
  @Override
  protected void configure() {
    if (props != null) {
      Names.bindProperties(this.binder(), props);
    }

    // create session for messaging
    bind(javax.jms.Session.class).toProvider(JmsSessionProvider.class).in(
        Scopes.SINGLETON);

    // create session for mail
    bind(javax.mail.Session.class).toProvider(MailSessionProvider.class).in(
        Scopes.SINGLETON);

    // create messaging service
    bind(MessagingService.class).to(JmsMessagingService.class).in(
        Scopes.SINGLETON);
  }
}
