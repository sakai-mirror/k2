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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.sakaiproject.kernel.api.messaging.Message;
import org.sakaiproject.kernel.api.messaging.MessageConverter;
import org.sakaiproject.kernel.api.messaging.MessagingService;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

/**
 *
 */
public class JsonMessageConverter implements MessageConverter {

  private static final String BODY_TEXT = "body-text";
  private static final String BODY_URL = "body-url";
  private static final String PARTS = "parts";

  /**
   *
   */
  public JsonMessageConverter() {
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.MessageConverter#serialize(org.sakaiproject.kernel.api.messaging.Message)
   */
  public String serialize(Message msg) {
    // create the string writer and initial builder
    JSONObject base = new JSONObject();

    // accumulate headers from message
    base.accumulateAll(msg.getHeaders());

    // add the body
    if (msg.isBodyText()) {
      base.element(BODY_TEXT, msg.getText());
    } else {
      base.element(BODY_URL, msg.getBody().toExternalForm());
    }

    // add attachments
    for (Message part : msg.getParts()) {
      base.accumulate(PARTS, serialize(part));
    }

    return base.toString();
  }

  public Message deserialize(String json, MessagingService messagingService)
      throws MalformedURLException {
    JSONObject jsonObj = (JSONObject) JSONSerializer.toJSON(json);
    return deserialize(jsonObj, messagingService);
  }

  protected Message deserialize(JSONObject jsonObj,
      MessagingService messagingService) throws MalformedURLException {
    Message msg = messagingService.createMessage();

    // add headers
    Set entrySet = jsonObj.entrySet();
    Iterator entries = entrySet.iterator();
    while (entries.hasNext()) {
      Entry entry = (Entry) entries.next();
      if (BODY_TEXT.equals(entry.getKey())) {
        msg.setText((String) entry.getValue());
      }
      if (BODY_URL.equals(entry.getKey())) {
        msg.setBody(new URL((String) entry.getValue()));
      }
      if (PARTS.equals(entry.getKey())) {
        JSONArray array = (JSONArray) entry.getValue();
        for (Object o : array) {
          msg.addPart(deserialize((JSONObject) o, messagingService));
        }
      }
    }
    return msg;
  }
}
