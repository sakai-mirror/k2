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
package org.sakaiproject.kernel.messaging.email;

import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sakaiproject.kernel.api.email.EmailAddress;
import org.sakaiproject.kernel.api.email.EmailAddress.RcptType;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;

/**
 * Unit test for email message listener
 */
public class EmailMessageListenerTest {
  static Session session;
  static EmailMessageListener listener;

  private JmsEmailMessage email;

  @BeforeClass
  public static void beforeClass() {
    Properties props = new Properties();
    props.put("mail.smtp.host", "localhost");
    props.put("mail.smtp.port", "8025");
    props.put("mail.smtp.from", "postmaster@emailMessageListener.test");
    props.put("mail.smtp.sendpartial", "true");
    session = Session.getDefaultInstance(props);

    listener = new EmailMessageListener(session);
    listener.setAllowTransport(false);
  }

  @AfterClass
  public static void afterClass() {
    session = null;
  }

  @Before
  public void setUp() {
    email = new JmsEmailMessage(null);
  }

  @Test
  public void send() throws Exception {
    // pass a null service because we're starting after when the service is used
    email.setFrom(new EmailAddress("test@example.com", "Some Dude"));
    email.addRecipient(RcptType.TO, new EmailAddress("random@example.com",
        "Random Chick"));
    email.setBody("This is some test text.");

    listener.handleMessage(email);
  }

  @Test
  public void invalidFrom() throws Exception {
    // pass a null service because we're starting after when the service is used
    email.addRecipient(RcptType.TO, new EmailAddress("random@example.com",
        "Random Chick"));
    email.setBody("This is some test text.");

    try {
      listener.handleMessage(email);
      fail("Should fail with no 'from' address");
    } catch (MessagingException e) {
      // expected
    }
  }

  @Test
  public void noRcpts() throws Exception {
    // pass a null service because we're starting after when the service is used
    email.setFrom(new EmailAddress("test@example.com", "Some Dude"));
    email.setBody("This is some test text.");

    try {
      listener.handleMessage(email);
      fail("Should fail with no recipient addresses");
    } catch (MessagingException e) {
      // expected
    }
  }
}
