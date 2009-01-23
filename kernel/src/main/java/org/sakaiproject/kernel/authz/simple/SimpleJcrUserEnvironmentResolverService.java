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

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
import org.sakaiproject.kernel.api.memory.Cache;
import org.sakaiproject.kernel.api.memory.CacheManagerService;
import org.sakaiproject.kernel.api.memory.CacheScope;
import org.sakaiproject.kernel.api.serialization.BeanConverter;
import org.sakaiproject.kernel.api.session.Session;
import org.sakaiproject.kernel.api.user.User;
import org.sakaiproject.kernel.api.userenv.UserEnvironment;
import org.sakaiproject.kernel.api.userenv.UserEnvironmentResolverService;
import org.sakaiproject.kernel.user.UserFactoryService;
import org.sakaiproject.kernel.util.IOUtils;
import org.sakaiproject.kernel.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import javax.jcr.RepositoryException;

/**
 * 
 */
public class SimpleJcrUserEnvironmentResolverService implements
    UserEnvironmentResolverService {

  protected String LOCALE_SESSION_KEY = "sakai.locale.";

  private static final Log LOG = LogFactory
      .getLog(SimpleJcrUserEnvironmentResolverService.class);
  private JCRNodeFactoryService jcrNodeFactoryService;
  private BeanConverter beanConverter;
  private UserEnvironment nullUserEnv;
  private Cache<UserEnvironment> cache;
  private UserFactoryService userFactoryService;

  /**
 * 
 */
  @Inject
  public SimpleJcrUserEnvironmentResolverService(
      JCRNodeFactoryService jcrNodeFactoryService,
      CacheManagerService cacheManagerService,
      @Named(BeanConverter.REPOSITORY_BEANCONVETER) BeanConverter beanConverter,
      @Named(UserEnvironment.NULLUSERENV) UserEnvironment nullUserEnv,
      UserFactoryService userFactoryService) {
    this.jcrNodeFactoryService = jcrNodeFactoryService;
    this.nullUserEnv = nullUserEnv;
    this.beanConverter = beanConverter;
    this.userFactoryService = userFactoryService;
    cache = cacheManagerService.getCache("userenv",
        CacheScope.CLUSTERINVALIDATED);
    cache.put("test", null);
    cache.remove("test");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.userenv.UserEnvironmentResolverService#resolve(org.sakaiproject.kernel.api.user.User)
   */
  public UserEnvironment resolve(User user) {
    if (user != null) {
      if (cache.containsKey(user.getUuid())) {
        UserEnvironment ue = cache.get(user.getUuid());
        if (ue != null && !ue.hasExpired()) {
          return ue;
        }
      }

      String userEnv = userFactoryService.getUserEnvPath(user.getUuid());
      UserEnvironment ue = loadUserEnvironmentBean(userEnv);
      if (ue != null) {
        cache.put(user.getUuid(), ue);
        return ue;
      }
    }
    return nullUserEnv;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.userenv.UserEnvironmentResolverService#resolve(org.sakaiproject.kernel.api.session.Session)
   */
  public UserEnvironment resolve(Session currentSession) {
    return resolve(currentSession.getUser());
  }

  public void expire(String userId) {
    cache.remove(userId);
  }

  /**
   * @param userEnv2
   * @return
   * @throws JCRNodeFactoryServiceException
   * @throws RepositoryException
   * @throws IOException
   * @throws UnsupportedEncodingException
   */
  private UserEnvironment loadUserEnvironmentBean(String userEnvPath) {
    try {
      String userEnvBody = IOUtils.readFully(jcrNodeFactoryService
          .getInputStream(userEnvPath), "UTF-8");
      // convert to a bean, the
      UserEnvironment ue = beanConverter.convertToObject(userEnvBody,
          UserEnvironment.class);
      // seal the bean to prevent modification.
      ue.seal();
      return ue;
    } catch (UnsupportedEncodingException e) {
      LOG.error(e);
    } catch (IOException e) {
      LOG.warn("Failed to read userenv " + userEnvPath + " cause :"
          + e.getMessage());
      LOG.debug(e);
    } catch (RepositoryException e) {
      LOG.warn("Failed to read userenv for " + userEnvPath + " cause :"
          + e.getMessage());
      LOG.debug(e);
    } catch (JCRNodeFactoryServiceException e) {
      LOG.warn("Failed to read userenv for " + userEnvPath + " cause :"
          + e.getMessage());
      LOG.debug(e);
    }
    return null;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.userenv.UserEnvironmentResolverService#getUserEnvironmentBasePath(java.lang.String)
   */
  public String getUserEnvironmentBasePath(String userId) {
    return userFactoryService.getUserEnvironmentBasePath(userId);
  }

  /**
   * * Return user's prefered locale * First: return locale from Sakai user
   * preferences, if available * Second: return locale from user session, if
   * available * Last: return system default locale
   * 
   * @param locale * *
   * @return user's Locale object
   */
  public Locale getUserLocale(Locale browserLocale, Session session) {
    Locale loc = null;

    User user = session.getUser();
    UserEnvironment userEnvironment = null;
    if (user != null && user.getUuid() != null) {
      userEnvironment = resolve(user);
    }
    String localeKey = (String) session.getAttribute(LOCALE_SESSION_KEY);
    if (userEnvironment != null && localeKey == null) {
      localeKey = userEnvironment.getLocale();
    }
    String[] locValues = StringUtils.split(localeKey, '_');
    if (locValues.length > 1) {
      loc = new Locale(locValues[0], locValues[1]);
    } else if (locValues.length == 1) {
      loc = new Locale(locValues[0]);
    } else if (browserLocale != null) {
      loc = browserLocale;
    } else {
      loc = Locale.getDefault();
    }
    return loc;
  }

}
