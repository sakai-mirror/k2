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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Base implementation for messages
 */
public class MessageImpl implements Message {

  private static final long serialVersionUID = 1L;

  private final MessagingService messagingService;
  private final HashMap<String, Serializable> data;

  public MessageImpl(MessagingService messagingService) {
    this.messagingService = messagingService;
    data = new HashMap<String, Serializable>();
  }

  /**
   * {@inheritDoc}
   *
   * @param key
   * @param value
   * @see Message#setHeader(java.lang.String, java.lang.String)
   */
  @SuppressWarnings("unchecked")
  public void setHeader(String key, String value) {
    HashMap<String, String> headers = (HashMap<String, String>) getField(
        Message.Field.HEADERS);
    if (headers == null) {
      headers = new HashMap<String, String>();
    }
    headers.put(key, value);
  }

  /**
   * {@inheritDoc}
   *
   * @param key
   * @see Message#removeField(java.lang.String)
   */
  @SuppressWarnings("unchecked")
  public void removeHeader(String key) {
    HashMap<String, String> headers = (HashMap<String, String>) getField(
        Message.Field.HEADERS);
    if (headers != null) {
      headers.remove(key);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#getBody()
   */
  public Serializable getBody() {
    return getField(Message.Field.BODY.toString());
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#getField(java.lang.String)
   */
  public Serializable getField(String key) {
    return data.get(key);
  }

  /**
   * {@inheritDoc}
   * @param <T>
   * @param key
   * @return
   * @see Message#getField(java.lang.Enum)
   */
  public Serializable getField(Enum<?> key) {
    return getField(key.toString());
  }

  public Map<String, Serializable> getFields() {
    return data;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#getTitle()
   */
  public String getTitle() {
    return (String) getField(Message.Field.TITLE.toString());
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#getType()
   */
  public String getType() {
    return (String) getField(Message.Field.TYPE.toString());
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#removeField(Enum)
   */
  public void removeField(Enum<?> key) {
    removeField(key.toString());
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#removeField(String)
   */
  public void removeField(String key) {
    data.remove(key);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#setBody(java.lang.String)
   */
  public void setBody(Serializable newBody) {
    setField(Message.Field.BODY.toString(), newBody);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#setField(java.lang.String,
   *      java.lang.Object)
   */
  public void setField(String key, Serializable value) {
    data.put(key, value);
  }

  /**
   * {@inheritDoc}
   *
   * @param key
   * @param value
   * @see Message#setField(java.lang.Enum, java.io.Serializable)
   */
  public void setField(Enum<?> key, Serializable value) {
    setField(key.toString(), value);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#setTitle(java.lang.String)
   */
  public void setTitle(String newTitle) {
    setField(Message.Field.TITLE.toString(), newTitle);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#setType(org.sakaiproject.kernel.api.messaging.Message.Type)
   */
  public void setType(String newType) {
    setField(Message.Field.TYPE.toString(), newType);
  }

  /**
   * {@inheritDoc}
   *
   * @see Message#send()
   */
  public void send() {
    messagingService.send(this);
  }
}
