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

package org.sakaiproject.kernel.api.messaging;

import java.net.URL;
import java.util.List;

/**
 * Message that allows parts to be added on as attachments.
 */
public interface MultipartMessage extends Message {

  public static final String TYPE = "MultipartMessage";

  /**
   * Add an attachment to the message. Convenience method for adding a part to
   * the message. This constructs a new message and adds it to the message being
   * called.
   *
   * @param mimeType
   * @param attachment
   */
  void addAttachment(String mimeType, URL attachment);

  /**
   * Add a message as a part of the calling message.
   *
   * @param message
   */
  void addPart(Message message);

  /**
   * Get all parts added to the message.
   *
   * @return all parts added to the message. Empty non-null list if no parts
   *         found.
   */
  List<Message> getParts();
}
