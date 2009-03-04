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
package org.sakaiproject.kernel.jcr.jackrabbit.journal;

import org.apache.jackrabbit.core.journal.AppendRecord;
import org.apache.jackrabbit.core.journal.DatabaseJournal;
import org.apache.jackrabbit.core.journal.JournalException;
import org.apache.jackrabbit.core.journal.RecordProducer;

import java.io.InputStream;

/**
 * 
 */
public class SakaiJournal extends DatabaseJournal {

  /**
   * {@inheritDoc}
   * 
   * @see org.apache.jackrabbit.core.journal.AbstractJournal#createProducer(java.lang.String)
   */
  @Override
  protected RecordProducer createProducer(String identifier) {
    return new SakaiRecordProducer(this, identifier);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.apache.jackrabbit.core.journal.DatabaseJournal#append(org.apache.jackrabbit.core.journal.AppendRecord,
   *      java.io.InputStream, int)
   */
  @Override
  protected void append(AppendRecord record, InputStream in, int length)
      throws JournalException {
    if (record instanceof SakaiAppendRecord) {
      SakaiAppendRecord sakaiAppendRecord = (SakaiAppendRecord) record;
      if (!sakaiAppendRecord.hasData()) {
        record.cancelUpdate();
        return;
      }
    }
    super.append(record, in, length);
  }
}
