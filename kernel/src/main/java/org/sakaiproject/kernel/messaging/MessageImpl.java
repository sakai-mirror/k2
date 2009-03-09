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

import org.sakaiproject.kernel.api.messaging.Message;
import org.sakaiproject.kernel.api.messaging.MessagingService;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Base implementation for messages
 */
public class MessageImpl implements Message {

  public static final String TEXT_PLAIN = "text/plain";
  public static final String TEXT_HTML = "text/html";

  private static final long serialVersionUID = 1L;

  private final MessagingService messagingService;
  private final HashMap<String, String> data;

  private URL bodyUrl;
  private String bodyText;
  private String from;

  public MessageImpl(MessagingService messagingService, String type) {
    this.messagingService = messagingService;
    setType(type);
    data = new HashMap<String, String>();
  }

  public MessageImpl(MessagingService messagingService) {
    this(messagingService, Message.Type.INTERNAL.toString());
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#getTo()
   */
  public String getTo() {
    return getHeader(Message.Field.TO);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#addTo()
   */
  public void addTo(String to) {
    String msgTo = getHeader(Message.Field.TO);
    if (msgTo != null) {
      msgTo += ", " + to;
    } else {
      msgTo = to;
    }
    setHeader(Message.Field.TO, msgTo);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#getBody()
   */
  public URL getBody() {
    return bodyUrl;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#getHeader(java.lang.String)
   */
  public String getHeader(String key) {
    return data.get(key);
  }

  /**
   * {@inheritDoc}
   * @param <T>
   * @param key
   * @return
   * @see Message#getHeader(java.lang.Enum)
   */
  public String getHeader(Enum<?> key) {
    return getHeader(key.toString());
  }

  public Map<String, String> getHeaders() {
    return data;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#getSubject()
   */
  public String getSubject() {
    return getHeader(Message.Field.TITLE.toString());
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#getType()
   */
  public String getType() {
    return getHeader(Message.Field.TYPE.toString());
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#removeHeader(Enum)
   */
  public void removeHeader(Enum<?> key) {
    removeHeader(key.toString());
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#removeHeader(String)
   */
  public void removeHeader(String key) {
    data.remove(key);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#setBody(java.lang.String)
   */
  public void setBody(String newBody) {
    setHeader(Message.Field.BODY.toString(), newBody);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#setHeader(java.lang.String,
   *      java.lang.Object)
   */
  public void setHeader(String key, String value) {
    data.put(key, value);
  }

  /**
   * {@inheritDoc}
   *
   * @param key
   * @param value
   * @see Message#setHeader(java.lang.Enum, java.io.Serializable)
   */
  public void setHeader(Enum<?> key, String value) {
    setHeader(key.toString(), value);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#setSubject(java.lang.String)
   */
  public void setSubject(String newTitle) {
    setHeader(Message.Field.TITLE.toString(), newTitle);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#setType(org.sakaiproject.kernel.api.messaging.Message.Type)
   */
  public void setType(String newType) {
    setHeader(Message.Field.TYPE.toString(), newType);
  }

  /**
   * {@inheritDoc}
   *
   * @see Message#send()
   */
  public void send() {
    messagingService.send(this);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#isBodyText()
   */
  public boolean isBodyText() {
    return bodyText != null;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#getFrom()
   */
  public String getFrom() {
    return from;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#getText()
   */
  public String getText() {
    return bodyText;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#setBody(java.net.URL)
   */
  public void setBody(URL bodyUrl) {
    this.bodyUrl = bodyUrl;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#setFrom()
   */
  public void setFrom(String from) {
    this.from = from;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#setText(java.lang.String)
   */
  public void setText(String text) {
    this.bodyText = text;

    if (getMimeType() != null) {
      setHeader(Message.Field.MIME_TYPE, TEXT_PLAIN);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#getMimeType()
   */
  public String getMimeType() {
    return getHeader(Message.Field.MIME_TYPE);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#setMimeType(java.lang.String)
   */
  public void setMimeType(String mimeType) {
    setHeader(Message.Field.MIME_TYPE, mimeType);
  }
}
