/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sakaiproject.kernel.messaging;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Map;
import org.sakaiproject.kernel.api.messaging.Message;
import org.sakaiproject.kernel.api.messaging.MessagingService;
import org.sakaiproject.kernel.api.messaging.MultipartMessage;

/**
 *
 * @author chall39
 */
public class MultipartMessageImpl extends MessageImpl implements MultipartMessage {

  public MultipartMessageImpl(MessagingService messagingService) {
    super(messagingService);
  }

  public void addAttachment(String mimeType, Serializable attachment) {
    MessageImpl msg = new MessageImpl(null);
    msg.setType(mimeType);
    msg.setBody(attachment);
    addPart(msg);
  }

  public void addPart(Message message) {
    ArrayList<Message> parts = (ArrayList<Message>) getField(MultipartMessage.Field.PARTS);
    if (parts == null || parts.isEmpty()) {
      parts = new ArrayList<Message>();
    }
    parts.add(message);
    setField(MultipartMessage.Field.PARTS, parts);
  }
}
