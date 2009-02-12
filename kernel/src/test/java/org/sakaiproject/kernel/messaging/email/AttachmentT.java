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
package org.sakaiproject.kernel.messaging.email;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sakaiproject.kernel.api.email.Attachment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class AttachmentT {
  @Test
  public void createWithFilename() throws Exception {
    File f = File.createTempFile("test", null);
    String filename = f.getPath();
    Attachment a = new Attachment(filename);
    File fa = a.getFile();
    assertEquals(f, fa);
  }

  @Test
  public void createWithFile() throws Exception {
    File f = File.createTempFile("test", null);
    Attachment a = new Attachment(f);
    File fa = a.getFile();
    assertEquals(f, fa);
  }

  @Test
  public void convertList() throws Exception {
    ArrayList<File> files = new ArrayList<File>();
    files.add(File.createTempFile("test1", null));
    files.add(File.createTempFile("test2", null));
    files.add(File.createTempFile("test3", null));
    files.add(File.createTempFile("test4", null));
    files.add(File.createTempFile("test5", null));
    List<Attachment> attachments = Attachment.toAttachment(files);

    assertEquals(files.size(), attachments.size());

    // lists should be parallel
    for (int i = 0; i < files.size(); i++) {
      assertEquals(files.get(i), attachments.get(i).getFile());
    }
  }
}
