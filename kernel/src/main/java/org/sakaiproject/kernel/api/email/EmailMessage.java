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
package org.sakaiproject.kernel.api.email;

import org.sakaiproject.kernel.api.email.EmailAddress.RcptType;
import org.sakaiproject.kernel.api.messaging.MessagingException;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 *
 */
public interface EmailMessage {
  /**
   * Get the sender of this message.
   *
   * @return The sender of this message.
   */
  EmailAddress getFrom();

  /**
   * Set the sender of this message.
   *
   * @param email
   *          Email address of sender.
   */
  void setFrom(String email);

  /**
   * Set the sender of this message.
   *
   * @param emailAddress
   *          {@link EmailAddress} of message sender.
   */
  void setFrom(EmailAddress emailAddress);

  /**
   * Get recipient for replies.
   *
   * @return {@link EmailAddress} of reply to recipient.
   */
  List<EmailAddress> getReplyTo();

  /**
   * Set recipient for replies.
   *
   * @param email
   *          Email string of reply to recipient.
   */
  void addReplyTo(EmailAddress emailAddress);

  /**
   * Set recipient for replies.
   *
   * @param email
   *          {@link EmailAddress} of reply to recipient.
   */
  void addReplyTo(List<EmailAddress> replyTo);

  /**
   * Get intended recipients of this message.
   *
   * @return List of {@link EmailAddress} that will receive this message
   */
  Map<RcptType, List<EmailAddress>> getRecipients();

  /**
   * Get recipients of this message that are associated to a certain type
   *
   * @param type
   * @return
   * @see Type
   */
  List<EmailAddress> getRecipients(RcptType type);

  /**
   * Add a recipient to this message.
   *
   * @param type
   *          How to address the recipient.
   * @param email
   *          Email to send to.
   */
  void addRecipient(RcptType type, String email);

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
  void addRecipient(RcptType type, EmailAddress address);

  /**
   * Add multiple recipients to this message.
   *
   * @param type
   *          How to address the recipients.
   * @param addresses
   *          List of {@link EmailAddress} to add to this message.
   */
  void addRecipients(RcptType type, List<EmailAddress> addresses);

  /**
   * Get all recipients as a flattened list. This is intended to be used for
   * determining the recipients for an SMTP route.
   *
   * @return list of recipient addresses associated to this message
   */
  List<EmailAddress> getAllRecipients();

  /**
   * Get the subject of this message.
   *
   * @return The subject of this message. May be empty or null value.
   */
  String getSubject();

  /**
   * Set the subject of this message.
   *
   * @param subject
   *          Subject for this message. Empty and null values allowed.
   */
  void setSubject(String subject);

  /**
   * Get the body content of this message.
   *
   * @return The body content of this message.
   */
  String getBody();

  /**
   * Set the body content of this message.
   *
   * @param body
   *          The content of this message.
   */
  void setBody(String body);

  /**
   * Get the attachments on this message
   *
   * @return List of {@link Attachment} attached to this message.
   */
  List<Attachment> getAttachments();

  /**
   * Add an attachment to this message.
   *
   * @param attachment
   *          File to attach to this message.
   */
  void addAttachment(Attachment attachment);

  /**
   * Add an attachment to this message. Same as addAttachment(new
   * Attachment(file)).
   *
   * @param file
   */
  void addAttachment(File file);

  /**
   * Set the attachments of this message. Will replace any existing attachments.
   *
   * @param attachments
   *          The attachments to set on this message.
   */
  void addAttachments(List<Attachment> attachments);

  /**
   * Get the headers of this message.
   *
   * @return {@link java.util.Map} of headers set on this message.
   */
  Map<String, String> getHeaders();

  /**
   * Flattens the headers down to "key: value" strings.
   *
   * @return List of properly formatted headers. List will be 0 length if no
   *         headers found. Does not return null
   */
  List<String> extractHeaders();

  /**
   * Remove a header from this message. Does nothing if header is not found.
   *
   * @param key
   */
  void removeHeader(String key);

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
  void addHeader(String key, String value);

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
  void setHeader(String key, String value);

  /**
   * Set the headers of this message. Will replace any existing headers.
   *
   * @param headers
   *          The headers to use on this message.
   */
  void setHeaders(Map<String, String> headers);

  /**
   * Sets headers on this message. The expected format of each header is
   * "key:value".
   *
   * @param headers
   */
  void setHeaders(List<String> headers);

  /**
   * Get the mime type of this message.
   *
   * @return {@link org.sakaiproject.email.api.ContentType} of this message.
   */
  String getContentType();

  /**
   * Set the mime type of this message.
   *
   * @param mimeType
   *          The mime type to use for this message.
   * @see org.sakaiproject.email.api.ContentType
   */
  void setContentType(String mimeType);

  /**
   * Get the character set for text in this message. Used for the subject and
   * body.
   *
   * @return The character set used for this message.
   */
  String getCharacterSet();

  /**
   * Set the character set for text in this message.
   *
   * @param characterSet
   *          The character set used to render text in this message.
   * @see org.sakaproject.email.api.CharacterSet
   */
  void setCharacterSet(String characterSet);

  /**
   * Gets the format of this message.
   *
   * @return
   */
  String getFormat();

  /**
   * Set the format of this message if content type is text/plain
   *
   * @param format
   * @see org.sakaiproject.email.api.PlainTextFormat
   * @see org.sakaiproject.email.api.ContentType
   */
  void setFormat(String format);

  /**
   * Send this message. The implementation by which this is sent depends on how
   * the class was created. The implementation shouldn't be a concern.
   *
   * @throws MessagingException
   */
  void send() throws MessagingException;
}