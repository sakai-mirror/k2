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

import com.google.inject.Inject;

import org.sakaiproject.kernel.api.Registry;
import org.sakaiproject.kernel.api.RegistryService;
import org.sakaiproject.kernel.api.user.Authentication;
import org.sakaiproject.kernel.api.user.AuthenticationResolverProvider;
import org.sakaiproject.kernel.api.user.AuthenticationResolverService;

import java.security.Principal;
import java.util.List;

/**
 * This class acts as a container for authentication mechanisms that are
 * registered from elsewhere
 */
public class ProviderAuthenticationResolverService implements
    AuthenticationResolverService {

  private NullAuthenticationResolverServiceImpl nullService;
  private Registry<String,AuthenticationResolverProvider<String>> registry;

  /**
   * 
   */
  @Inject
  public ProviderAuthenticationResolverService(
      NullAuthenticationResolverServiceImpl nullService,
      RegistryService providerService) {
    this.nullService = nullService;
    this.registry = providerService
        .getRegistry(PROVIDER_REGISTRY);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.user.AuthenticationResolverService#authenticate(java.security.Principal)
   */
  public Authentication authenticate(Principal principal)
      throws SecurityException {
    List<AuthenticationResolverProvider<String>> providers = registry.getList();
    if (providers.size() == 0) {
      return nullService.authenticate(principal);
    }
    StringBuilder messages = new StringBuilder();
    for (AuthenticationResolverProvider<String> authN : providers) {
      try {
        return authN.authenticate(principal);
      } catch (SecurityException se) {
        if (messages.length() == 0) {
          messages.append("Authentication Failed:\n");
        }
        messages.append("\t").append(authN).append(" said ").append(
            se.getMessage()).append("\n");
      }
    }
    throw new SecurityException(messages.toString());
  }

}
