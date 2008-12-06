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
import org.sakaiproject.kernel.api.userenv.UserEnvironment;
import org.sakaiproject.kernel.api.userenv.UserEnvironmentResolverService;
import org.sakaiproject.kernel.util.IOUtils;
import org.sakaiproject.kernel.util.PathUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.jcr.RepositoryException;

/**
 * 
 */
public class SimpleJcrUserEnvironmentResolverService implements
    UserEnvironmentResolverService {

  private static final String JCR_USERENV_BASE = "jcruserenv.base";
  private static final String USERENV = "/userenv";
  private static final Log LOG = LogFactory.getLog(SimpleJcrUserEnvironmentResolverService.class);
  private JCRNodeFactoryService jcrNodeFactoryService;
  private String userEnvironmentBase;
  private CacheManagerService cacheManagerService;
  private BeanConverter beanConverter;
  private UserEnvironment nullUserEnv;

  /**
 * 
 */
  @Inject
  public SimpleJcrUserEnvironmentResolverService(
      JCRNodeFactoryService jcrNodeFactoryService,
      @Named(JCR_USERENV_BASE) String userEnvironmentBase,
      CacheManagerService cacheManagerService,
      @Named(BeanConverter.REPOSITORY_BEANCONVETER) BeanConverter beanConverter,
      @Named(UserEnvironment.NULLUSERENV) UserEnvironment nullUserEnv) {
    this.jcrNodeFactoryService = jcrNodeFactoryService;
    this.userEnvironmentBase = userEnvironmentBase;
    this.cacheManagerService = cacheManagerService;
    this.nullUserEnv = nullUserEnv;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.userenv.UserEnvironmentResolverService#resolve(org.sakaiproject.kernel.api.session.Session)
   */
  public UserEnvironment resolve(Session currentSession) {
    Cache<UserEnvironment> cache = cacheManagerService.getCache("userenv",
        CacheScope.INSTANCE);
    if (cache.containsKey(currentSession.getId())) {
      UserEnvironment ue = cache.get(currentSession.getId());
      if (ue != null && !ue.hasExpired()) {
        return ue;
      }
    }

    try {
      String prefix = PathUtils.getUserPrefix(currentSession.getUserId());
      String userEnv = userEnvironmentBase + prefix + USERENV;
      String userEnvBody = IOUtils.readFully(jcrNodeFactoryService
          .getOutputStream(userEnv), "UTF-8");
      // convert to a bean, the 
      UserEnvironment ue = beanConverter.convertToObject(userEnvBody, UserEnvironment.class);
      cache.put(currentSession.getId(), ue);
      return ue;
    } catch (UnsupportedEncodingException e) {
      LOG.error(e);
    } catch (IOException e) {
      LOG.warn("Failed to read userenv for "+currentSession.getUserId()+" cause :"+e.getMessage());
      LOG.debug(e);
    } catch (RepositoryException e) {
      LOG.warn("Failed to read userenv for "+currentSession.getUserId()+" cause :"+e.getMessage());
      LOG.debug(e);
    } catch (JCRNodeFactoryServiceException e) {
      LOG.warn("Failed to read userenv for "+currentSession.getUserId()+" cause :"+e.getMessage());
      LOG.debug(e);
    }
    return nullUserEnv;
  }
}
