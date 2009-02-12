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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.sakaiproject.kernel.api.email.EmailAddress;
import org.sakaiproject.kernel.api.email.RecipientType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class JmsEmailMessageT {
  private JmsEmailMessage msg;

  @Before
  public void setUp() {
    msg = new JmsEmailMessage(null);
  }

  @Test
  public void setFrom() {
    String email = "test1@example.com";
    EmailAddress ea = new EmailAddress(email);
    msg.setFrom("test1@example.com");
    assertEquals(email, msg.getFrom().getAddress());

    msg.setFrom(ea);
    assertEquals(ea, msg.getFrom());
  }

  @Test
  public void setSubject() {
    msg.setSubject("test subject");
    assertEquals("test subject", msg.getSubject());
  }

  @Test
  public void setReplyTo() {
    String e1 = "test1@example.com";
    String e2 = "test2@example.com";

    msg.addReplyTo(e1);
    List<EmailAddress> ea = msg.getReplyTo();
    assertTrue(ea.get(0).equals(e1));

    msg.addReplyTo(e2);
    assertTrue(ea.get(0).equals(e1));
    assertTrue(ea.get(1).equals(e2));
  }

  @Test
  public void setReplyToAddresses() {
    String e1 = "test1@example.com";
    String e2 = "test2@example.com";
    EmailAddress a1 = new EmailAddress(e1);
    EmailAddress a2 = new EmailAddress(e2);
    msg.addReplyTo(a1);
    List<EmailAddress> ea = msg.getReplyTo();
    assertEquals(1, ea.size());
    assertTrue(ea.get(0).equals(a1));

    msg.addReplyTo(a2);
    assertEquals(2, ea.size());
    assertTrue(ea.get(0).equals(a1));
    assertTrue(ea.get(1).equals(a2));
  }

  @Test
  public void setReplyToList() {
    String e1 = "test1@example.com";
    String e2 = "test2@example.com";
    EmailAddress a1 = new EmailAddress(e1);
    EmailAddress a2 = new EmailAddress(e2);
    List<EmailAddress> replyTos = new ArrayList<EmailAddress>();
    replyTos.add(a1);
    replyTos.add(a2);

    msg.addReplyTo(replyTos);
    List<EmailAddress> ea = msg.getReplyTo();
    assertEquals(replyTos.size(), ea.size());
    assertTrue(ea.get(0).equals(a1));
    assertTrue(ea.get(1).equals(a2));
  }

  @Test
  public void addRecipients() {
    String add1 = "test1@example.com";
    String add2 = "test2@example.com";
    String add3 = "test3@example.com";
    String add4 = "test4@example.com";
    String add5 = "test5@example.com";
    String add6 = "test6@example.com";
    String add7 = "test7@example.com";
    String add8 = "test8@example.com";

    msg.addRecipient(RecipientType.TO, add1);
    msg.addRecipient(RecipientType.TO, add2);
    msg.addRecipient(RecipientType.CC, add3);
    msg.addRecipient(RecipientType.CC, add4);
    msg.addRecipient(RecipientType.BCC, add5);
    msg.addRecipient(RecipientType.ACTUAL, add6);
    msg.addRecipient(RecipientType.ACTUAL, add7);
    msg.addRecipient(RecipientType.ACTUAL, add8);

    List<EmailAddress> rcpts = msg.getRecipients(RecipientType.TO);
    assertEquals(2, rcpts.size());
    assertTrue(rcpts.get(0).equals(add1));
    assertTrue(rcpts.get(1).equals(add2));

    rcpts = msg.getRecipients(RecipientType.CC);
    assertEquals(2, rcpts.size());
    assertTrue(rcpts.get(0).equals(add3));
    assertTrue(rcpts.get(1).equals(add4));

    rcpts = msg.getRecipients(RecipientType.BCC);
    assertEquals(1, rcpts.size());
    assertTrue(rcpts.get(0).equals(add5));

    rcpts = msg.getRecipients(RecipientType.ACTUAL);
    assertEquals(3, rcpts.size());
    assertTrue(rcpts.get(0).equals(add6));
    assertTrue(rcpts.get(1).equals(add7));
    assertTrue(rcpts.get(2).equals(add8));
  }

  @Test
  public void addRecipientAddresses() {
    EmailAddress add1 = new EmailAddress("test1@example.com");
    EmailAddress add2 = new EmailAddress("test2@example.com");
    EmailAddress add3 = new EmailAddress("test3@example.com");
    EmailAddress add4 = new EmailAddress("test4@example.com");
    EmailAddress add5 = new EmailAddress("test5@example.com");
    EmailAddress add6 = new EmailAddress("test6@example.com");
    EmailAddress add7 = new EmailAddress("test7@example.com");
    EmailAddress add8 = new EmailAddress("test8@example.com");

    msg.addRecipient(RecipientType.TO, add1);
    msg.addRecipient(RecipientType.TO, add2);
    msg.addRecipient(RecipientType.CC, add3);
    msg.addRecipient(RecipientType.CC, add4);
    msg.addRecipient(RecipientType.BCC, add5);
    msg.addRecipient(RecipientType.ACTUAL, add6);
    msg.addRecipient(RecipientType.ACTUAL, add7);
    msg.addRecipient(RecipientType.ACTUAL, add8);

    List<EmailAddress> rcpts = msg.getRecipients(RecipientType.TO);
    assertEquals(2, rcpts.size());
    assertTrue(rcpts.get(0).equals(add1));
    assertTrue(rcpts.get(1).equals(add2));

    rcpts = msg.getRecipients(RecipientType.CC);
    assertEquals(2, rcpts.size());
    assertTrue(rcpts.get(0).equals(add3));
    assertTrue(rcpts.get(1).equals(add4));

    rcpts = msg.getRecipients(RecipientType.BCC);
    assertEquals(1, rcpts.size());
    assertTrue(rcpts.get(0).equals(add5));

    rcpts = msg.getRecipients(RecipientType.ACTUAL);
    assertEquals(3, rcpts.size());
    assertTrue(rcpts.get(0).equals(add6));
    assertTrue(rcpts.get(1).equals(add7));
    assertTrue(rcpts.get(2).equals(add8));
  }

  @Test
  public void addRecipientList() {
    EmailAddress add1 = new EmailAddress("test1@example.com");
    EmailAddress add2 = new EmailAddress("test2@example.com");
    EmailAddress add3 = new EmailAddress("test3@example.com");
    EmailAddress add4 = new EmailAddress("test4@example.com");
    EmailAddress add5 = new EmailAddress("test5@example.com");
    ArrayList<EmailAddress> list1 = new ArrayList<EmailAddress>();
    list1.add(add1);
    list1.add(add2);
    list1.add(add3);
    msg.addRecipients(RecipientType.TO, list1);

    List<EmailAddress> rcpts = msg.getRecipients(RecipientType.TO);
    assertEquals(3, rcpts.size());
    assertEquals(add1, rcpts.get(0));
    assertEquals(add2, rcpts.get(1));
    assertEquals(add3, rcpts.get(2));

    ArrayList<EmailAddress> list2 = new ArrayList<EmailAddress>();
    list1.add(add4);
    list1.add(add5);
    msg.addRecipients(RecipientType.TO, list2);

    assertEquals(5, rcpts.size());
    assertEquals(add4, rcpts.get(3));
    assertEquals(add5, rcpts.get(4));
  }

  @Test
  public void getRecipients() {
    EmailAddress add1 = new EmailAddress("test1@example.com");
    EmailAddress add2 = new EmailAddress("test2@example.com");
    EmailAddress add3 = new EmailAddress("test3@example.com");
    EmailAddress add4 = new EmailAddress("test4@example.com");
    EmailAddress add5 = new EmailAddress("test5@example.com");
    EmailAddress add6 = new EmailAddress("test6@example.com");
    EmailAddress add7 = new EmailAddress("test7@example.com");
    EmailAddress add8 = new EmailAddress("test8@example.com");

    msg.addRecipient(RecipientType.TO, add1);
    msg.addRecipient(RecipientType.TO, add2);
    msg.addRecipient(RecipientType.CC, add3);
    msg.addRecipient(RecipientType.CC, add4);
    msg.addRecipient(RecipientType.BCC, add5);
    msg.addRecipient(RecipientType.ACTUAL, add6);
    msg.addRecipient(RecipientType.ACTUAL, add7);
    msg.addRecipient(RecipientType.ACTUAL, add8);

    List<EmailAddress> rcpts = msg.getAllRecipients();
    assertEquals(8, rcpts.size());
    assertEquals(add1, rcpts.get(0));
    assertEquals(add2, rcpts.get(1));
    assertEquals(add3, rcpts.get(2));
    assertEquals(add4, rcpts.get(3));
    assertEquals(add5, rcpts.get(4));
    assertEquals(add6, rcpts.get(5));
    assertEquals(add7, rcpts.get(6));
    assertEquals(add8, rcpts.get(7));
  }

  @Test
  public void addAttachments() {
    File f1 = new File("test1.tmp");
    File f2 = new File("test2.tmp");
    msg.addAttachment(f1);
    msg.addAttachment(f2);

    assertEquals(2, msg.getAttachments().size());
    assertEquals(f1, msg.getAttachments().get(0));
    assertEquals(f2, msg.getAttachments().get(1));
  }

  @Test
  public void addAttachmentList() {
    List<File> files = new ArrayList<File>();
    File f1 = new File("test1.tmp");
    File f2 = new File("test2.tmp");
    files.add(f1);
    files.add(f2);
    msg.addAttachments(files);
    assertEquals(2, msg.getAttachments().size());
    assertEquals(f1, msg.getAttachments().get(0));
    assertEquals(f2, msg.getAttachments().get(1));
  }

  @Test
  public void addSimpleHeaders() {
    msg.addHeader("random-header-1", "random-value-1");
    msg.addHeader("random-header-2", "random-value-2");
    assertEquals("random-value-1", msg.getHeader("random-header-1"));
    assertEquals("random-value-2", msg.getHeader("random-header-2"));
  }

  @Test
  public void addCompoundHeaders() {
    msg.addHeader("random-header-1", "random-value-1");
    msg.addHeader("random-header-1", "random-value-2");
    assertEquals("random-value-1 random-value-2", msg
        .getHeader("random-header-1"));
  }

  @Test
  public void removeHeader() {
    JmsEmailMessage msg = new JmsEmailMessage(null);

    msg.addHeader("random-header-1", "random-value-1");
    msg.addHeader("random-header-1", null);
    assertEquals(null, msg.getHeader("random-header-1"));
  }

  @Test
  public void extractHeaders() {
    msg.addHeader("random-header-1", "random-value-1");
    msg.addHeader("random-header-2", "random-value-2");
    msg.addHeader("random-header-2", "random-value-3");
    List<String> headers = msg.extractHeaders();
    assertEquals("random-header-1: random-value-1", headers.get(0));
    assertEquals("random-header-2: random-value-2 random-value-3", headers
        .get(1));
  }
}