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

import org.sakaiproject.kernel.api.Registry;
import org.sakaiproject.kernel.api.RegistryService;
import org.sakaiproject.kernel.api.authz.SubjectPermissionService;
import org.sakaiproject.kernel.api.authz.SubjectPermissions;
import org.sakaiproject.kernel.api.authz.SubjectStatement;
import org.sakaiproject.kernel.api.authz.SubjectTokenProvider;
import org.sakaiproject.kernel.api.authz.UserSubjects;
import org.sakaiproject.kernel.api.userenv.UserEnvironment;

import java.util.List;

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
  private Registry<String,SubjectTokenProvider<String>> registry;
  private String locale;

  @Inject
  public UserEnvironmentBean(SubjectPermissionService subjectPermissionService,
      @Named(USER_ENV_TTL) int ttl, RegistryService registryService) {
    expiry = System.currentTimeMillis() + ttl;
    this.subjectPermissionService = subjectPermissionService;
    this.registry = registryService.getRegistry(SubjectStatement.PROVIDER_REGISTRY);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.userenv.UserEnvironment#hasExpired()
   */
  public boolean hasExpired() {
    return (System.currentTimeMillis() > expiry);
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.userenv.UserEnvironment#matches(org.sakaiproject.kernel.api.authz.SubjectStatement)
   */
  // TODO: No test coverage of this
  public boolean matches(SubjectStatement subject) {
    switch (subject.getSubjectType()) {
    case PROVIDED:
      List<SubjectTokenProvider<String>> providers = registry.getList();
      for (SubjectTokenProvider<String> provider : providers) {
        if (provider.matches(this, subject)) {
          return true;
        }
      }
      return false;
    case GROUP:
      String subjectToken = subject.getSubjectToken();
      loadSubjects();
      if (subjects != null && subjectsBean.hasSubject(subjectToken)) {
        subjectsBean.setSubjectPermissionService(subjectPermissionService);
        SubjectPermissions subjectPermissions = subjectsBean
            .getSubjectPermissions(subjectToken);
        return subjectPermissions.hasPermission(subject.getPermissionToken());
      }
      return false;
    case USERID:
      return userid.equals(subject.getSubjectToken());
    case AUTHENTICATED:
      return (userid != null && userid.trim().length() > 0);
    case ANON:
      return true;
    }
    return false;
  }

  /**
   * 
   */
  private void loadSubjects() {
    if (subjectsBean == null) {
      subjectsBean = new SubjectsBean(subjectPermissionService);
      for (String subject : subjects) {
        subjectsBean.put(subject, subject);
      }
    }
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.userenv.UserEnvironment#isSuperUser()
   */
  public boolean isSuperUser() {
    return superUser;
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.userenv.UserEnvironment#getSubjects()
   */
  public String[] getSubjects() {
    return subjects;
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.userenv.UserEnvironment#getUserid()
   */
  public String getUserid() {
    return userid;
  }

  /**
   * @param superUser
   *          the superUser to set
   */
  public void setSuperUser(boolean superUser) {
    if (sealed) {
      throw new RuntimeException(
          "Attempt to unseal a sealed UserEnvironmentBean ");
    }
    this.superUser = superUser;
  }

  /**
   * @param userid
   *          the userid to set
   */
  public void setUserid(String userid) {
    if (sealed) {
      throw new RuntimeException(
          "Attempt to unseal a sealed UserEnvironmentBean ");
    }
    this.userid = userid;
  }

  /**
   * @param subjects
   *          the subjects to set
   */
  public void setSubjects(String[] subjects) {
    if (sealed) {
      throw new RuntimeException(
          "Attempt to unseal a sealed UserEnvironmentBean ");
    }
    subjectsBean = null;
    this.subjects = subjects;
  }

  /**
   * @param sealed
   *          the sealed to set
   */
  public void seal() {
    this.sealed = true;
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.userenv.UserEnvironment#getUserSubjects()
   */
  public UserSubjects getUserSubjects() {
    loadSubjects();
    return subjectsBean;
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.userenv.UserEnvironment#getLocale()
   */
  public String getLocale() {
    return locale;
  }
  
    /**
     * @param locale the locale to set
     */
    public void setLocale(String locale) {
      if (sealed) {
        throw new RuntimeException(
            "Attempt to unseal a sealed UserEnvironmentBean ");
      }
      this.locale = locale;
    }
}
