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

import java.io.Serializable;
import org.sakaiproject.kernel.api.messaging.Message;

import java.util.HashMap;
import java.util.Map;
import org.sakaiproject.kernel.api.messaging.MessagingService;

/**
 * Base implementation for messages
 */
public class MessageImpl implements Message {
  private final MessagingService messagingService;
  private final HashMap<String, Serializable> data;

  public MessageImpl(MessagingService messagingService) {
    this.messagingService = messagingService;
    data = new HashMap<String, Serializable>();
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#getBody()
   */
  public <T> T getBody() {
    return getField(Message.Field.BODY.toString());
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#getField(java.lang.String)
   */
  public <T> T getField(String key) {
    return (T) data.get(key);
  }

  public <T> T getField(Enum<?> key) {
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
    return getField(Message.Field.TITLE.toString());
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#getType()
   */
  public String getType() {
    return getField(Message.Field.TYPE.toString());
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

  public void send() {
    messagingService.send(this);
  }
}
