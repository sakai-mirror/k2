/**********************************************************************************
 * Copyright 2008 Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/
package org.sakaiproject.kernel.api.email;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds an attachment for an email message. The attachment will be included
 * with the message.
 */
public class Attachment implements Serializable {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  /**
   * files to associated to this attachment
   */
  private final File file;

  public Attachment(File file) {
    this.file = file;
  }

  public Attachment(String filename) {
    this.file = new File(filename);
  }

  /**
   * Get the file associated to this attachment
   *
   * @return
   */
  public File getFile() {
    return file;
  }

  public static List<Attachment> toAttachment(List<? extends File> files) {
    ArrayList<Attachment> attachments = null;
    if (files != null) {
      attachments = new ArrayList<Attachment>();
      for (File f : files) {
        attachments.add(new Attachment(f));
      }
    }
    return attachments;
  }
}