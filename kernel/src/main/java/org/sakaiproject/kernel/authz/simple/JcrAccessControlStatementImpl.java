/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
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
package org.sakaiproject.kernel.authz.simple;

import org.sakaiproject.kernel.api.authz.AccessControlStatement;
import org.sakaiproject.kernel.api.authz.SubjectStatement;

import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

/**
 * 
 */
public class JcrAccessControlStatementImpl implements AccessControlStatement {

  private static final String KEY_PREFIX = "k:";
  private static final String SUBJECT_PREFIX = "s:";
  private static final String GRANTED_PREFIX = "g:";
  private static final String PROPAGATING_PREFIX = "p:";
  private static final String GRANTED = "g:1";
  private static final String PROPAGATING = "p:1";
  private SubjectStatement subject;
  private boolean granted;
  private boolean propagating;
  private String key;

  /**
   * @param p
   * @throws RepositoryException
   * @throws ValueFormatException
   */
  public JcrAccessControlStatementImpl(Property p) throws ValueFormatException,
      RepositoryException {
    Value[] values = p.getValues();
    subject = JcrSubjectStatement.UNKNOWN;
    granted = false;
    propagating = false;
    for (Value v : values) {
      String val = v.toString();
      if (val.startsWith(SUBJECT_PREFIX)) {
        subject = new JcrSubjectStatement(val.substring(2));
      } else if (val.startsWith(KEY_PREFIX)) {
        key = val.substring(2);
      } else if (val.startsWith(GRANTED_PREFIX)) {
        granted = GRANTED.equals(val);
      } else if (val.startsWith(PROPAGATING_PREFIX)) {
        propagating = PROPAGATING.equals(val);
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.authz.AccessControlStatement#getKey()
   */
  public String getKey() {
    return key;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.authz.AccessControlStatement#getSubject()
   */
  public SubjectStatement getSubject() {
    return subject;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.authz.AccessControlStatement#isGranted()
   */
  public boolean isGranted() {
    return granted;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.authz.AccessControlStatement#isPropagating()
   */
  public boolean isPropagating() {
    return propagating;
  }

}
