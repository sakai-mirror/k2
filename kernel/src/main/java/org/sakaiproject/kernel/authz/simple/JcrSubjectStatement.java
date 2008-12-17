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

import org.sakaiproject.kernel.api.authz.SubjectStatement;
import org.sakaiproject.kernel.util.StringUtils;

/**
 * 
 */
public class JcrSubjectStatement implements SubjectStatement {

  public static final SubjectStatement UNKNOWN = new JcrSubjectStatement();
  private SubjectType subjectType;
  private String subjectToken;
  private String permissionToken;

  /**
   * @param substring
   */
  public JcrSubjectStatement(String subjectStatement) {
    String[] parts = StringUtils.split(subjectStatement, ':');
    try {
      subjectType = SubjectType.valueOf(parts[0]);
    } catch ( IllegalArgumentException e ) {
      subjectType = SubjectType.UNDEFINED;
    }
    subjectToken = parts[1];
    permissionToken = parts[2];
  }

  public JcrSubjectStatement(SubjectType subjectType, String subjectToken,
      String permissionToken) {
    this.subjectType = subjectType;
    this.subjectToken = subjectToken;
    this.permissionToken = permissionToken;
  }

  /**
   * 
   */
  private JcrSubjectStatement() {
    subjectType = SubjectType.UNDEFINED;
    subjectToken = "none";
    permissionToken = "none";
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.authz.SubjectStatement#getPermissionToken()
   */
  public String getPermissionToken() {
    return permissionToken;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.authz.SubjectStatement#getSubjectToken()
   */
  public String getSubjectToken() {
    return subjectToken;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.authz.SubjectStatement#getSubjectType()
   */
  public SubjectType getSubjectType() {
    return subjectType;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return subjectType.hashCode() + subjectToken.hashCode()
        + permissionToken.hashCode();
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof JcrSubjectStatement) {
      JcrSubjectStatement jcrss = (JcrSubjectStatement) obj;
      return subjectType.equals(jcrss.subjectType)
          && subjectToken.equals(jcrss.subjectToken)
          && permissionToken.equals(jcrss.permissionToken);
    }
    return false;
  }
  
  
  /**
   * {@inheritDoc}
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return subjectType+":"+subjectToken+":"+permissionToken;
  }

}
