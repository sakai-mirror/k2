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

package org.sakaiproject.kernel.messaging.email;

import org.sakaiproject.kernel.api.email.CharacterSet;
import org.sakaiproject.kernel.api.email.ContentType;
import org.sakaiproject.kernel.api.email.EmailAddress;
import org.sakaiproject.kernel.api.email.EmailMessage;
import org.sakaiproject.kernel.api.email.RecipientType;
import org.sakaiproject.kernel.api.messaging.MessagingException;
import org.sakaiproject.kernel.api.messaging.MessagingService;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.mail.internet.MimeMessage;

/**
 * Value object for sending emails. Mimics javax.mail.internet.MimeMessage
 * without having a dependency on javax.mail<br>
 *
 * <p>
 * Sending a message can be done by specifying recipients and/or <em>actual</em>
 * recipients. If only recipients (to, cc, bcc) are specified, those are the
 * people that will recieve the message and will see each other listed in the
 * to, cc and bcc fields. If actual recipients are specified, any other
 * recipients will be ignored but will be added to the email headers
 * appropriately. This allows for mailing to lists and hiding recipients
 * (recipients: mylist@somedomain.edu, actualRecipients: [long list of
 * students].
 * </p>
 *
 * <p>
 * The default content type for a message is {@link ContentType#TEXT_PLAIN}. The
 * content type only applies to the message body.
 * </p>
 * <p>
 * The default character set for a message is UTF-8.
 * </p>
 *
 * @see javax.mail.Transport#send(MimeMessage)
 * @see javax.mail.Transport#send(MimeMessage, javax.mail.Address[])
 * @see javax.mail.internet.InternetAddress
 * @see ContentType
 * @see CharacterSet
 */
public class JmsEmailMessage implements EmailMessage, Serializable {
  /**
   * Default serial version ID
   */
  private static final long serialVersionUID = 1L;

  /**
   * Who this message is from
   */
  private EmailAddress from;

  /**
   * Addressee(s) for replies
   */
  private List<EmailAddress> replyTo;

  /**
   * Recipients of message
   */
  private Map<RecipientType, List<EmailAddress>> recipients;

  /**
   * Subject of message
   */
  private String subject;

  /**
   * Body content of message
   */
  private String body;

  /**
   * Attachments to consider for message
   */
  private List<File> attachments;

  /**
   * Arbitrary headers for message
   */
  private Map<String, String> headers;

  /**
   * Mime type of message. Defaults to text/plain.
   *
   * @see org.sakaiproject.kernel.api.ContentType
   */
  private String contentType = ContentType.TEXT_PLAIN;

  /**
   * Character set of text in message
   *
   * @see org.sakaiproject.kernel.api.CharacterSet
   */
  private String characterSet = CharacterSet.UTF_8;

  /**
   * Format of this message if in plain text.
   *
   * @see org.sakaiproject.kernel.api.email.PlainTextFormat
   */
  private String format;

  /**
   * Reference to the messaging service. This is used to change message to Jms
   * Message and not intended to be used after serialization.
   */
  private transient MessagingService messagingService;

  /**
   * Default constructor.
   */
  public JmsEmailMessage(MessagingService messagingService) {
    this.messagingService = messagingService;
    recipients = new HashMap<RecipientType, List<EmailAddress>>();
    headers = new HashMap<String, String>();
    attachments = new ArrayList<File>();
    replyTo = new ArrayList<EmailAddress>();
  }

  /**
   * Get the sender of this message.
   *
   * @return The sender of this message.
   */
  public EmailAddress getFrom() {
    return from;
  }

  /**
   * Set the sender of this message.
   *
   * @param email
   *          Email address of sender.
   */
  public void setFrom(String email) {
    this.from = new EmailAddress(email);
  }

  /**
   * Set the sender of this message.
   *
   * @param emailAddress
   *          {@link EmailAddress} of message sender.
   */
  public void setFrom(EmailAddress emailAddress) {
    this.from = emailAddress;
  }

  /**
   * Get recipient for replies.
   *
   * @return {@link EmailAddress} of reply to recipient.
   */
  public List<EmailAddress> getReplyTo() {
    return replyTo;
  }

  /**
   * Set recipient for replies.
   *
   * @param email
   *          EmailAddress of reply to recipient.
   */
  public void addReplyTo(EmailAddress emailAddress) {
    replyTo.add(emailAddress);
  }

  /**
   * Set recipient for replies.
   *
   * @param email
   *          Email string of reply to recipient.
   */
  public void addReplyTo(String email) {
    replyTo.add(new EmailAddress(email));
  }

  /**
   * Set recipient for replies.
   *
   * @param email
   *          {@link EmailAddress} of reply to recipient.
   */
  public void addReplyTo(List<EmailAddress> replyTo) {
    this.replyTo = replyTo;
  }

  /**
   * Get recipients of this message that are associated to a certain type
   *
   * @param type
   * @return
   * @see Type
   */
  public List<EmailAddress> getRecipients(RecipientType type) {
    List<EmailAddress> retval = null;
    if (recipients != null) {
      retval = recipients.get(type);
    }
    return retval;
  }

  /**
   * Add a recipient to this message.
   *
   * @param type
   *          How to address the recipient.
   * @param email
   *          Email to send to.
   */
  public void addRecipient(RecipientType type, String email) {
    List<EmailAddress> addresses = recipients.get(type);
    if (addresses == null) {
      addresses = new ArrayList<EmailAddress>();
    }
    addresses.add(new EmailAddress(email));
    recipients.put(type, addresses);
  }

  /**
   * Add a recipient to this message.
   *
   * @param type
   *          How to address the recipient.
   * @param name
   *          Name of recipient.
   * @param email
   *          Email to send to.
   */
  public void addRecipient(RecipientType type, EmailAddress address) {
    List<EmailAddress> addresses = recipients.get(type);
    if (addresses == null) {
      addresses = new ArrayList<EmailAddress>();
    }
    addresses.add(address);
    recipients.put(type, addresses);
  }

  /**
   * Add multiple recipients to this message.
   *
   * @param type
   *          How to address the recipients.
   * @param addresses
   *          List of {@link EmailAddress} to add to this message.
   */
  public void addRecipients(RecipientType type, List<EmailAddress> addresses) {
    List<EmailAddress> currentAddresses = recipients.get(type);
    if (currentAddresses == null) {
      recipients.put(type, addresses);
    } else {
      currentAddresses.addAll(addresses);
    }
  }

  /**
   * Get all recipients as a flattened list. This is intended to be used for
   * determining the recipients for an SMTP route.
   *
   * @return list of recipient addresses associated to this message
   */
  public List<EmailAddress> getAllRecipients() {
    List<EmailAddress> rcpts = new ArrayList<EmailAddress>();

    if (recipients.containsKey(RecipientType.TO)) {
      rcpts.addAll(recipients.get(RecipientType.TO));
    }

    if (recipients.containsKey(RecipientType.CC)) {
      rcpts.addAll(recipients.get(RecipientType.CC));
    }

    if (recipients.containsKey(RecipientType.BCC)) {
      rcpts.addAll(recipients.get(RecipientType.BCC));
    }

    if (recipients.containsKey(RecipientType.ACTUAL)) {
      rcpts.addAll(recipients.get(RecipientType.ACTUAL));
    }

    return rcpts;
  }

  /**
   * Get the subject of this message.
   *
   * @return The subject of this message. May be empty or null value.
   */
  public String getSubject() {
    return subject;
  }

  /**
   * Set the subject of this message.
   *
   * @param subject
   *          Subject for this message. Empty and null values allowed.
   */
  public void setSubject(String subject) {
    this.subject = subject;
  }

  /**
   * Get the body content of this message.
   *
   * @return The body content of this message.
   */
  public String getBody() {
    return body;
  }

  /**
   * Set the body content of this message.
   *
   * @param body
   *          The content of this message.
   */
  public void setBody(String body) {
    this.body = body;
  }

  /**
   * Get the attachments on this message
   *
   * @return List of {@link java.io.File} attached to this message.
   */
  public List<File> getAttachments() {
    return attachments;
  }

  /**
   * Add an attachment to this message.
   *
   * @param attachment
   *          File to attach to this message.
   */
  public void addAttachment(File attachment) {
    attachments.add(attachment);
  }

  /**
   * Set the attachments of this message. Will replace any existing attachments.
   *
   * @param attachments
   *          The attachments to set on this message.
   */
  public void addAttachments(List<File> attachments) {
    this.attachments.addAll(attachments);
  }

  /**
   * Get the headers of this message.
   *
   * @return {@link java.util.Map} of headers set on this message.
   */
  public Map<String, String> getHeaders() {
    return headers;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.email.EmailMessage#getHeader(java.lang.String)
   */
  public String getHeader(String key) {
    String retval = headers.get(key);
    return retval;
  }

  /**
   * Flattens the headers down to "key: value" strings.
   *
   * @return List of properly formatted headers. List will be 0 length if no
   *         headers found. Does not return null
   */
  public List<String> extractHeaders() {
    List<String> retval = new ArrayList<String>();

    if (headers != null) {
      for (String key : headers.keySet()) {
        String value = headers.get(key);
        if (key != null && value != null) {
          retval.add(key + ": " + value);
        }
      }
    }
    return retval;
  }

  /**
   * Remove a header from this message. Does nothing if header is not found.
   *
   * @param key
   */
  public void removeHeader(String key) {
    if (!headers.isEmpty() && headers.containsKey(key)) {
      headers.remove(key);
    }
  }

  /**
   * Add a header to this message. If the key is found in the headers of this
   * message, the value is appended to the previous value found and separated by
   * a space. A key of null will not be added. If value is null, previous
   * entries of the matching key will be removed.
   *
   * @param key
   *          The key of the header.
   * @param value
   *          The value of the header.
   */
  public void addHeader(String key, String value) {
    if (headers.get(key) == null) {
      setHeader(key, value);
    } else if (key != null && value != null) {
      String prevVal = headers.get(key);
      prevVal += " " + value;
      headers.put(key, prevVal);
    } else if (value == null) {
      removeHeader(key);
    }
  }

  /**
   * Sets a header to this message. Any previous value for this key will be
   * replaced. If value is null, previous entries of the matching key will be
   * removed.
   *
   * @param key
   *          The key of the header.
   * @param value
   *          The value of the header.
   */
  public void setHeader(String key, String value) {
    if (key != null && value != null) {
      headers.put(key, value);
    }
  }

  /**
   * Get the mime type of this message.
   *
   * @return {@link org.sakaiproject.email.api.ContentType} of this message.
   */
  public String getContentType() {
    return contentType;
  }

  /**
   * Set the mime type of this message.
   *
   * @param mimeType
   *          The mime type to use for this message.
   * @see org.sakaiproject.email.api.ContentType
   */
  public void setContentType(String mimeType) {
    this.contentType = mimeType;
  }

  /**
   * Get the character set for text in this message. Used for the subject and
   * body.
   *
   * @return The character set used for this message.
   */
  public String getCharacterSet() {
    return characterSet;
  }

  /**
   * Set the character set for text in this message.
   *
   * @param characterSet
   *          The character set used to render text in this message.
   * @see org.sakaproject.email.api.CharacterSet
   */
  public void setCharacterSet(String characterSet) {
    this.characterSet = characterSet;
  }

  /**
   * Gets the format of this message.
   *
   * @return
   */
  public String getFormat() {
    return format;
  }

  /**
   * Set the format of this message if content type is text/plain
   *
   * @param format
   * @see org.sakaiproject.email.api.PlainTextFormat
   * @see org.sakaiproject.email.api.ContentType
   */
  public void setFormat(String format) {
    this.format = format;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.email.EmailMessage#send()
   */
  public void send() throws MessagingException {
    try {
      messagingService.send(this.toJmsMessage());
    } catch (JMSException e) {
      throw new MessagingException(e.getMessage(), e);
    }
  }

  /**
   * Creates a {@link javax.jms.Message} from the data in this EmailMessage.
   *
   * @return
   * @throws JMSException
   */
  protected Message toJmsMessage() throws JMSException {
    ObjectMessage msg = messagingService.createObjectMessage();
    msg.setObject(this);
    return msg;
  }
}