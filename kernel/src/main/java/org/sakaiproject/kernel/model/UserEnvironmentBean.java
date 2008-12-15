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
package org.sakaiproject.kernel.model;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.sakaiproject.kernel.api.authz.SubjectPermissionService;
import org.sakaiproject.kernel.api.authz.SubjectPermissions;
import org.sakaiproject.kernel.api.authz.SubjectStatement;
import org.sakaiproject.kernel.api.userenv.UserEnvironment;

/**
 * 
 */
public class UserEnvironmentBean implements UserEnvironment {

  private static final String USER_ENV_TTL = "userenvironment.ttl";
  private transient long expiry;
  private transient SubjectsBean subjectsBean;
  private boolean superUser = false;
  private String[] subjects = new String[0];
  private String userid;
  private SubjectPermissionService subjectPermissionService;
  private boolean sealed = false;

  @Inject
  public UserEnvironmentBean(SubjectPermissionService subjectPermissionService, @Named(USER_ENV_TTL) int ttl) {
    expiry = System.currentTimeMillis() + ttl;
    this.subjectPermissionService = subjectPermissionService;
  }
    
  
  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.userenv.UserEnvironment#hasExpired()
   */
  public boolean hasExpired() {
    return ( System.currentTimeMillis() > expiry);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.userenv.UserEnvironment#matches(org.sakaiproject.kernel.api.authz.SubjectStatement)
   */
  public boolean matches(SubjectStatement subject) {
    switch (subject.getSubjectType()) {
    case GROUP:
      String subjectToken = subject.getSubjectToken();
      loadSubjects();
      if ( subjects != null && subjectsBean.hasSubject(subjectToken)) {
        subjectsBean.setSubjectPermissionService(subjectPermissionService);
        SubjectPermissions subjectPermissions = subjectsBean.getSubjectPermissions(subjectToken);
        return subjectPermissions.hasPermission(subject.getPermissionToken());
      }
      return false;
    case USERID:
      return userid.equals(subject.getSubjectToken());
    case AUTHENTICATED:
      return ( userid != null && userid.trim().length() > 0 );
    case ANON:
      return true;
    }
    return false;
  }
  
  /**
   * 
   */
  private void loadSubjects() {
    if ( subjectsBean  == null ) {
      subjectsBean = new SubjectsBean();
      for ( String subject : subjects ) {
        subjectsBean.put(subject,subject);
      }
    }
  }


  /**
   * @return the superUser
   */
  public boolean isSuperUser() {
    return superUser;
  }
  
  /**
   * @return the subjects
   */
  public String[] getSubjects() {
    return subjects;
  }
  
  /**
   * @return the userid
   */
  public String getUserid() {
    return userid;
  }
  
  /**
   * @param superUser the superUser to set
   */
  public void setSuperUser(boolean superUser) {
    if ( sealed ) {
      throw new RuntimeException("Attempt to unseal a sealed UserEnvironmentBean ");
    }
    this.superUser = superUser;
  }
   /**
   * @param userid the userid to set
   */
  public void setUserid(String userid) {
    if ( sealed ) {
      throw new RuntimeException("Attempt to unseal a sealed UserEnvironmentBean ");
    }
    this.userid = userid;
  }
  
  /**
   * @param subjects the subjects to set
   */
  public void setSubjects(String[] subjects) {
    if ( sealed ) {
      throw new RuntimeException("Attempt to unseal a sealed UserEnvironmentBean ");
    }
    subjectsBean = null;
    System.err.println("Setting subjects to "+subjects);
    this.subjects = subjects;
  }

  /**
   * @param sealed the sealed to set
   */
  public void seal() {
     this.sealed = true;
  }

}
