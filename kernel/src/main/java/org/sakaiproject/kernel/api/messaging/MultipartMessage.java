/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.sakaiproject.kernel.api.messaging;

import java.io.Serializable;

/**
 *
 * @author chall39
 */
public interface MultipartMessage extends Message {
  static enum Field {
    PARTS("Parts");

    public final String niceName;

    Field(String niceName) {
      this.niceName = niceName;
    }
  }

  /**
   * Add an attachment to the message.  Convenience method for adding a part to
   * the message.  This constructs a new message and adds it to the message
   * being called.
   * 
   * @param mimeType
   * @param attachment
   */
  void addAttachment(String mimeType, Serializable attachment);

  /**
   * Add a message as a part of the calling message.
   *
   * @param message
   */
  void addPart(Message message);
}
