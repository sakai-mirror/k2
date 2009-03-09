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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sakaiproject.kernel.messaging;

import org.sakaiproject.kernel.api.messaging.Message;
import org.sakaiproject.kernel.api.messaging.MessagingService;
import org.sakaiproject.kernel.api.messaging.MultipartMessage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MultipartMessageImpl extends MessageImpl implements
    MultipartMessage {

  private static final long serialVersionUID = 1L;

  private final ArrayList<Message> parts;

  public MultipartMessageImpl(MessagingService messagingService) {
    this(messagingService, Message.Type.INTERNAL.toString());
  }

  public MultipartMessageImpl(MessagingService messagingService, String type) {
    super(messagingService, type);
    parts = new ArrayList<Message>();
  }

  /**
   * {@inheritDoc}
   *
   * @param mimeType
   * @param attachment
   * @see MultipartMessage#addAttachment(java.lang.String, java.io.Serializable)
   */
  public void addAttachment(String mimeType, URL attachment) {
    MessageImpl msg = new MessageImpl(null);
    msg.setType(mimeType);
    msg.setBody(attachment);
    addPart(msg);
  }

  /**
   * {@inheritDoc}
   *
   * @param message
   * @see MultipartMessage#addPart(org.sakaiproject.kernel.api.messaging.Message)
   */
  public void addPart(Message message) {
    parts.add(message);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.MultipartMessage#getParts()
   */
  public List<Message> getParts() {
    return parts;
  }


}
