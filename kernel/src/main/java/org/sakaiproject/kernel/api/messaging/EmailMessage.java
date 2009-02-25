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
package org.sakaiproject.kernel.api.messaging;

import java.util.List;

/**
 * Base interface for all email objects.
 */
public interface EmailMessage extends MultipartMessage {

  public static enum ContentType {

    /**
     * Plain message with no formatting
     */
    TEXT_PLAIN("text/plain"),
    /**
     * Html formatted message
     */
    TEXT_HTML("text/html"),
    /**
     * Rich text formatted message
     */
    TEXT_RICH("text/richtext");
    private final String rfcName;

    ContentType(String header) {
      this.rfcName = header;
    }

    @Override
    public String toString() {
      return rfcName;
    }
  }

  public static enum Field {

    FROM("From"), TO("To"), CC("Cc"), BCC("Bcc"), REPLY_TO("Reply-To"),
    SUBJECT("Subject"), DATE("Date"), CONTENT_TYPE("Content-Type");
    /**
     * the name of the header associated to the field.
     */
    private final String header;

    /**
     * Create a field based on a header name.
     *
     * @param jsonString
     *          the name of the field
     */
    private Field(String header) {
      this.header = header;
    }

    /**
     * @return a string representation of the enum.
     */
    @Override
    public String toString() {
      return this.header;
    }
  }

  /**
   * Sets the address the message should be sent to.
   *
   * @param address
   *          the address for the message.
   */
  void setFrom(String address);

  /**
   * Gets the address the message is sent from.
   *
   * @return the address for the message.
   */
  String getFrom();

  /**
   * Add recipient for replies.
   *
   * @param email
   *          Email string of reply to recipient.
   */
  void addReplyTo(String email);

  /**
   * Get reply recipients.
   *
   * @return {@link java.util.List} of {@link java.lang.String} of reply
   *         recipients.
   */
  List<String> getReplyTo();

  /**
   * Add a recipient to the message.
   *
   * @param email
   *          Email to send to.
   */
  void addTo(String email);

  /**
   * Get the recipients of the message.
   *
   * @return {@link java.util.List} of {@link java.lang.String}
   */
  List<String> getTo();

  /**
   * Set the subject of the message.
   *
   * @param subject
   *          Subject for the message. Empty and null values allowed.
   */
  void setSubject(String subject);

  /**
   * Get the subject of the message.
   *
   * @return the subject of the message.
   */
  String getSubject();

  /**
   * Remove a header from the message. Does nothing if header is not found.
   *
   * @param key
   */
  void removeHeader(String key);

  /**
   * Sets a header to the message. Any previous value for this key will be
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
   * Get a header from the message. null if key not found.
   *
   * @param key
   *          the value of header at key.
   */
  String getHeader(String key);

  /**
   * Set the mime type of the message.
   *
   * @param mimeType
   *          The mime type to use for the message.
   * @see org.sakaiproject.email.api.ContentType
   */
  void setContentType(String mimeType);

  /**
   * Get the mime type of the message.
   *
   * @return the mime type.
   *         <p>
   *         eg.
   *         <ul>
   *         <li>text/plain; charset=utf-8; format=flowed</li>
   *         <li>text/plain; charset=us-ascii</li>
   *         <li>text/html</li>
   *         </ul>
   *         </p>
   */
  String getContentType();

  /**
   * Send the message. The implementation by which this is sent depends on how
   * the class was created. The implementation shouldn't be a concern.
   *
   * @throws MessagingException
   */
  void send() throws MessagingException;
}
