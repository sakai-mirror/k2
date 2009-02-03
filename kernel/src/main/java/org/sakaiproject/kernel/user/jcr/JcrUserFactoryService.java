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
package org.sakaiproject.kernel.user.jcr;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.sakaiproject.kernel.api.user.User;
import org.sakaiproject.kernel.model.UserBean;
import org.sakaiproject.kernel.user.UserFactoryService;
import org.sakaiproject.kernel.util.PathUtils;
import org.sakaiproject.kernel.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

/**
 * Creates User information for the JCR style ot user storage.
 */
@Singleton
public class JcrUserFactoryService implements UserFactoryService {

  public static final String JCR_USERENV_BASE = "jcruserenv.base";
  public static final String JCR_USERENV_TEMPLATES = "jcruserenv.templates";
  private static final String JCR_DEFAULT_TEMPLATE = "jcruserenv.templates.default";
  private static final String JCR_PROFILE_TEMPLATES = "jcrprofile.templates";
  private static final String JCR_PROFILE_DEFAUT_TEMPLATES = "jcrprofile.templates.default";
  private static final String PROFILE_JSON = "profile.json";
  private static final String PRIVATE_PATH_BASE = "jcrprivateshared.base";

  private EntityManager entityManager;
  private String userEnvironmentBase;
  private Map<String, String> userTemplateMap;
  private String defaultTemplate;
  long entropy = System.currentTimeMillis();
  private String sharedPrivatePathBase;
  private String defaultProfileTemplate;
  private HashMap<String, String> profileTemplateMap;

  /**
   * 
   */
  @Inject
  public JcrUserFactoryService(EntityManager entityManager,
      @Named(JCR_USERENV_BASE) String userEnvironmentBase,
      @Named(JCR_USERENV_TEMPLATES) String userTemplates,
      @Named(JCR_DEFAULT_TEMPLATE) String defaultTemplate,
      @Named(PRIVATE_PATH_BASE) String sharedPrivatePathBase,
      @Named(JCR_PROFILE_TEMPLATES) String profileTemplates,
      @Named(JCR_PROFILE_DEFAUT_TEMPLATES) String defaultProfileTemplate

      ) {
    this.entityManager = entityManager;
    this.defaultTemplate = defaultTemplate;
    this.defaultProfileTemplate = defaultProfileTemplate;
    this.userEnvironmentBase = userEnvironmentBase;
    userTemplateMap = Maps.newHashMap();
    String[] templates = StringUtils.split(userTemplates, ';');
    for (String template : templates) {
      String[] nv = StringUtils.split(template, '=', 2);
      userTemplateMap.put(nv[0].trim(), nv[1].trim());
    }
    profileTemplateMap = Maps.newHashMap();
    templates = StringUtils.split(profileTemplates, ';');
    for (String template : templates) {
      String[] nv = StringUtils.split(template, '=', 2);
      profileTemplateMap.put(nv[0].trim(), nv[1].trim());
    }
    this.sharedPrivatePathBase =sharedPrivatePathBase;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.user.UserFactoryService#createNewUser(java.lang.String)
   */
  public User createNewUser(String externalId) {
    try {
      String uid = StringUtils.sha1Hash(externalId + entropy);
      UserBean ub = new UserBean(uid, externalId);
      entityManager.persist(ub);
      return ub;
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("Failed to generate new user ", e);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Failed to generate new user ", e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.user.UserFactoryService#getUserEnvPath(java.lang.String)
   */
  public String getUserEnvPath(String uuid) {
    return getUserEnvironmentBasePath(uuid) + USERENV;
  }

  /**
   * @param uuid
   * @return
   */
  public String getUserEnvironmentBasePath(String uuid) {
    String prefix = PathUtils.getUserPrefix(uuid);
    return userEnvironmentBase + prefix;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.user.UserFactoryService#getUserEnvTemplate(java.lang.String)
   */
  public String getUserEnvTemplate(String userType) {
    if (userType == null) {
      return defaultTemplate;
    }
    String template = userTemplateMap.get(userType);
    if (template == null) {
      return defaultTemplate;
    }
    return template;
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.user.UserFactoryService#getUserPathPrefix(java.lang.String)
   */
  public String getUserPathPrefix(String uuid) {
    return PathUtils.getUserPrefix(uuid);
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.user.UserFactoryService#getUserProfilePath(java.lang.String)
   */
  public String getUserProfilePath(String uuid) {
    
    return sharedPrivatePathBase + PathUtils.getUserPrefix(uuid) + PROFILE_JSON;
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.user.UserFactoryService#getUserProfileTempate(java.lang.String)
   */
  public String getUserProfileTempate(String userType) {
    if (userType == null) {
      return defaultProfileTemplate;
    }
    String template = profileTemplateMap.get(userType);
    if (template == null) {
      return defaultProfileTemplate;
    }
    return template;
  }

}
