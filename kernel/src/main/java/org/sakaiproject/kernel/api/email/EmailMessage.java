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

package org.sakaiproject.kernel.api.email;

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
  String getFrom();

  /**
   * Set the sender of this message.
   *
   * @param email
   *          Email address of sender.
   */
  void setFrom(String email);

  /**
   * Get recipient for replies.
   *
   * @return {@link EmailAddress} of reply to recipient.
   */
  List<String> getReplyTo();

  /**
   * Add recipient for replies.
   *
   * @param email
   *          Email string of reply to recipient.
   */
  void addReplyTo(String email);

  /**
   * Get recipients of this message that are associated to a certain type
   *
   * @param type
   * @return
   * @see Type
   */
  List<EmailAddress> getRecipients(RecipientType type);

  /**
   * Add a recipient to this message.
   *
   * @param type
   *          How to address the recipient.
   * @param email
   *          Email to send to.
   */
  void addRecipient(RecipientType type, String email);

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
   * @return List of {@link File} attached to this message.
   */
  List<File> getAttachments();

  /**
   * Add an attachment to this message.
   *
   * @param attachment
   *          File to attach to this message.
   */
  void addAttachment(File attachment);

  /**
   * Set the attachments of this message. Will replace any existing attachments.
   *
   * @param attachments
   *          The attachments to set on this message.
   */
  void addAttachments(List<File> attachments);

  /**
   * Get the headers of this message.
   *
   * @return {@link java.util.Map} of headers set on this message.
   */
  Map<String, String> getHeaders();

  /**
   * Get a specific header from this message.
   *
   * @param key
   * @return The header value stored. null if not found.
   */
  String getHeader(String key);

  /**
   * Retrieves headers in a flattened pattern of "key: value".
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