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

import org.sakaiproject.kernel.api.messaging.EmailMessage;
import org.sakaiproject.kernel.api.messaging.MessagingService;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of Email messages.
 */
public class EmailMessageImpl extends MultipartMessageImpl implements
    EmailMessage {

  private static final long serialVersionUID = 1L;

  public EmailMessageImpl(MessagingService messagingService) {
    super(messagingService);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.EmailMessage#addReplyTo(java.lang.String)
   */
  @SuppressWarnings("unchecked")
  public void addReplyTo(String email) {
    ArrayList<String> replyTos = (ArrayList<String>) getField(
        EmailMessage.Field.REPLY_TO);
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
  @SuppressWarnings("unchecked")
  public void addTo(String email) {
    ArrayList<String> replyTos = (ArrayList<String>) getField(
        EmailMessage.Field.TO);
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
    return (String) getField(EmailMessage.Field.CONTENT_TYPE);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.EmailMessage#getFrom()
   */
  public String getFrom() {
    return (String) getField(EmailMessage.Field.FROM);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.EmailMessage#getHeader(java.lang.String)
   */
  public String getHeader(String key) {
    return (String) getField(key);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.EmailMessage#getReplyTo()
   */
  @SuppressWarnings("unchecked")
  public List<String> getReplyTo() {
    return (List<String>) getField(EmailMessage.Field.REPLY_TO);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.EmailMessage#getSubject()
   */
  @SuppressWarnings("unchecked")
  public String getSubject() {
    return (String) getField(EmailMessage.Field.SUBJECT);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.EmailMessage#getTo()
   */
  @SuppressWarnings("unchecked")
  public List<String> getTo() {
    return (List<String>) getField(EmailMessage.Field.TO);
  }
}
