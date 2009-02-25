/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sakaiproject.kernel.messaging;

import org.sakaiproject.kernel.api.messaging.Message;
import org.sakaiproject.kernel.api.messaging.MessagingService;
import org.sakaiproject.kernel.api.messaging.MultipartMessage;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 */
public class MultipartMessageImpl extends MessageImpl implements
    MultipartMessage {

  private static final long serialVersionUID = 1L;

  public MultipartMessageImpl(MessagingService messagingService) {
    super(messagingService);
  }

  /**
   * {@inheritDoc}
   *
   * @param mimeType
   * @param attachment
   * @see MultipartMessage#addAttachment(java.lang.String, java.io.Serializable)
   */
  public void addAttachment(String mimeType, Serializable attachment) {
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
  @SuppressWarnings("unchecked")
  public void addPart(Message message) {
    ArrayList<Message> parts = (ArrayList<Message>) getField(
        MultipartMessage.Field.PARTS);
    if (parts == null || parts.isEmpty()) {
      parts = new ArrayList<Message>();
    }
    parts.add(message);
    setField(MultipartMessage.Field.PARTS, parts);
  }
}
