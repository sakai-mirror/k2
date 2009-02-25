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

import org.sakaiproject.kernel.api.messaging.Message;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class MessageImpl implements Message {
  private final Map<String, Object> data;

  public MessageImpl() {
    data = new HashMap<String, Object>();
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#getBody()
   */
  public String getBody() {
    return getField(Message.Field.BODY.toString());
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#getField(java.lang.String)
   */
  @SuppressWarnings("unchecked")
  public <T> T getField(String key) {
    return (T) data.get(key);
  }

  public <T> T getField(Enum key) {
    return getField(key.toString());
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
   * @see org.sakaiproject.kernel.api.messaging.Message#setBody(java.lang.String)
   */
  public void setBody(String newBody) {
    setField(Message.Field.BODY.toString(), newBody);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Message#setField(java.lang.String,
   *      java.lang.Object)
   */
  public <T> void setField(String key, T value) {
    data.put(key, value);
  }

  public <T> void setField(Enum key, T value) {
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

}
