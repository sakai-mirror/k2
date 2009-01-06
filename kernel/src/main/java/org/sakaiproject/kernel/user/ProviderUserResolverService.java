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
package org.sakaiproject.kernel.user;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.user.User;
import org.sakaiproject.kernel.api.user.UserResolverProvider;
import org.sakaiproject.kernel.api.user.UserResolverService;

import java.util.List;

/**
 * This class acts as a container for authentication mechanisms that are
 * registered from elsewhere
 */
public class ProviderUserResolverService extends
    AbstractProviderRegistry<UserResolverProvider> implements
    UserResolverService {

  private static final Log LOG = LogFactory.getLog(ProviderUserResolverService.class);
  private List<UserResolverProvider> userResolverProviders;


  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.user.AbstractProviderRegistry#getProviders()
   */
  @Override
  protected List<UserResolverProvider> getProviders() {
    return userResolverProviders;
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.user.AbstractProviderRegistry#setProviders(java.util.List)
   */
  @Override
  protected void setProviders(List<UserResolverProvider> providers) {
    userResolverProviders = providers;
    
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.user.UserResolverService#resolve(java.lang.String)
   */
  public User resolve(String eid) {
    StringBuilder messages = new StringBuilder();
    for (UserResolverProvider userResolver : userResolverProviders) {
      try {
        User u =  userResolver.resolve(eid);
        if ( u != null ) {
          return u;
        }
      } catch (Exception se) {
        if (messages.length() == 0) {
          messages.append("User Resolution Failed:\n");
        }
        messages.append("\t").append(userResolver).append(" said ").append(
            se.getMessage()).append("\n");
      }
    }
    LOG.info("User Resolution failed "+messages.toString());
    return null;
  }

}
