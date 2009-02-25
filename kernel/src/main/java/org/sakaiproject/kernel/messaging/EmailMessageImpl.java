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

import org.sakaiproject.kernel.api.messaging.EmailAttachment;
import org.sakaiproject.kernel.api.messaging.EmailMessage;
import org.sakaiproject.kernel.api.messaging.MessagingException;
import org.sakaiproject.kernel.api.messaging.MessagingService;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of Email messages.
 */
public class EmailMessageImpl extends MessageImpl implements EmailMessage {
  private final MessagingService messagingService;
  private final ArrayList<EmailAttachment> attachments;

  public EmailMessageImpl(MessagingService messagingService) {
    this.messagingService = messagingService;
    attachments = new ArrayList<EmailAttachment>();
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.EmailMessage#addAttachment(org.sakaiproject.kernel.api.messaging.EmailAttachment)
   *      )
   */
  public void addAttachment(EmailAttachment attachment) {
    attachments.add(attachment);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.EmailMessage#addReplyTo(java.lang.String)
   */
  public void addReplyTo(String email) {
    List<String> replyTos = getField(EmailMessage.Field.REPLY_TO);
    if (replyTos == null) {
      replyTos = new ArrayList<String>();
    }
    if (!replyTos.contains(email)) {
      replyTos.add(email);
    }
    setField(EmailMessage.Field.REPLY_TO, replyTos);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.EmailMessage#addTo(java.lang.String)
   */
  public void addTo(String email) {
    List<String> replyTos = getField(EmailMessage.Field.TO);
    if (replyTos == null) {
      replyTos = new ArrayList<String>();
    }
    if (!replyTos.contains(email)) {
      replyTos.add(email);
    }
    setField(EmailMessage.Field.TO, replyTos);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.EmailMessage#getAttachments()
   */
  public List<EmailAttachment> getAttachments() {
    return attachments;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.EmailMessage#removeHeader(java.lang.String)
   */
  public void removeHeader(String key) {
    removeField(key);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.EmailMessage#send()
   */
  public void send() throws MessagingException {
    messagingService.send(this);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.EmailMessage#setContentType(java.lang.String)
   */
  public void setContentType(String mimeType) {
    setField(EmailMessage.Field.CONTENT_TYPE, mimeType);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.EmailMessage#setFrom(java.lang.String)
   */
  public void setFrom(String address) {
    setField(EmailMessage.Field.FROM, address);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.EmailMessage#setHeader(java.lang.String,
   *      java.lang.String)
   */
  public void setHeader(String key, String value) {
    setField(key, value);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.EmailMessage#setSubject(java.lang.String)
   */
  public void setSubject(String subject) {
    setField(EmailMessage.Field.SUBJECT, subject);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.EmailMessage#getContentType()
   */
  public String getContentType() {
    return getField(EmailMessage.Field.CONTENT_TYPE);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.EmailMessage#getFrom()
   */
  public String getFrom() {
    return getField(EmailMessage.Field.FROM);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.EmailMessage#getHeader(java.lang.String)
   */
  public String getHeader(String key) {
    return getField(key);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.EmailMessage#getReplyTo()
   */
  public List<String> getReplyTo() {
    return getField(EmailMessage.Field.REPLY_TO);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.EmailMessage#getSubject()
   */
  public String getSubject() {
    return getField(EmailMessage.Field.SUBJECT);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.EmailMessage#getTo()
   */
  public List<String> getTo() {
    return getField(EmailMessage.Field.TO);
  }
}
