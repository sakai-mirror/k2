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

  void addAttachment(String mimeType, Serializable attachment);

  void addPart(Message message);
}
