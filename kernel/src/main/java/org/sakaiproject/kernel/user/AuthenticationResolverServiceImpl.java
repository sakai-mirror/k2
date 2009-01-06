/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.kernel.user;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.sakaiproject.kernel.api.user.Authentication;
import org.sakaiproject.kernel.api.user.AuthenticationResolverService;
import org.sakaiproject.kernel.api.user.ExternalTrustedPrincipal;
import org.sakaiproject.kernel.api.user.IdPrincipal;
import org.sakaiproject.kernel.api.user.User;
import org.sakaiproject.kernel.api.user.UserResolverService;

import java.security.Principal;

/**
 * <p>
 * An Authentication component working with the UserDirectoryService.
 * </p>
 */
@Singleton
public class AuthenticationResolverServiceImpl implements
    AuthenticationResolverService {
  public static final String RESOLVER_CHAIN_HEAD = "authentication.resolver.head";
  private AuthenticationCache authenticationCache;
  private AuthenticationResolverService nextInChain;
  private UserResolverService userResolverService;

  /**
   * 
   */
  @Inject
  public AuthenticationResolverServiceImpl(
      @Named(RESOLVER_CHAIN_HEAD) AuthenticationResolverService nextInChain,
      AuthenticationCache authenticationCache) {
    this.authenticationCache = authenticationCache;
    this.nextInChain = nextInChain;
  }

  /**********************************************************************************************************************************************************************************************************************************************************
   * Work interface methods: AuthenticationManager
   *********************************************************************************************************************************************************************************************************************************************************/

  /**
   * @inheritDoc
   */
  public Authentication authenticate(Principal principal)
      throws SecurityException {

    Authentication rv = null;
    if (principal instanceof IdPrincipal) {
      IdPrincipal idPrincipal = (IdPrincipal) principal;

      // Check the cache. If repeat authentication failures are being throttled,
      // an immediate AuthenticationException might be thrown here.
      rv = authenticationCache.getAuthentication(idPrincipal);
      if (rv == null) {
        rv = nextInChain.authenticate(principal);
        if (rv == null) {
          authenticationCache.putAuthenticationFailure(idPrincipal);
          throw new SecurityException(
              "Invalid Login: Either user not found or password incorrect.");
        }
      }

      // Cache the authentication.
      authenticationCache.putAuthentication(idPrincipal, rv);
    } else if (principal instanceof ExternalTrustedPrincipal) {
      ExternalTrustedPrincipal externalTrustedPrincipal = (ExternalTrustedPrincipal) principal;

      User user = userResolverService.resolve(externalTrustedPrincipal
          .getIdentifier());
      if (user != null) {
        rv = new AuthenticationImpl(user);
      } else {
        throw new SecurityException(
            "Invalid Login: User not found in directory.");
      }
    } else {
      rv = nextInChain.authenticate(principal);
    }
    if (rv == null) {
      throw new SecurityException("Failed to authenticate " + principal);
    }
    return rv;
  }
}
