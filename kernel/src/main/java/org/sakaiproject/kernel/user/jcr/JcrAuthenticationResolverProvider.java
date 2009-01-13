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
package org.sakaiproject.kernel.user.jcr;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.sakaiproject.kernel.api.Registry;
import org.sakaiproject.kernel.api.RegistryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.api.user.Authentication;
import org.sakaiproject.kernel.api.user.AuthenticationManagerProvider;
import org.sakaiproject.kernel.api.user.AuthenticationResolverProvider;
import org.sakaiproject.kernel.api.user.IdPwPrincipal;
import org.sakaiproject.kernel.api.user.User;
import org.sakaiproject.kernel.api.user.UserResolverService;
import org.sakaiproject.kernel.authz.simple.SimpleJcrUserEnvironmentResolverService;
import org.sakaiproject.kernel.user.AuthenticationImpl;
import org.sakaiproject.kernel.user.AuthenticationResolverServiceImpl;
import org.sakaiproject.kernel.util.PathUtils;
import org.sakaiproject.kernel.util.StringUtils;

import java.security.Principal;

import javax.jcr.Node;
import javax.jcr.Property;

/**
 * Performs authentication against a property on the user env file containing a
 * SHA1 hash of the password
 */
public class JcrAuthenticationResolverProvider implements
    AuthenticationResolverProvider, AuthenticationManagerProvider {

  public static final String JCRPASSWORDHASH = "sakai:sha1-password-hash";
  private String userEnvironmentBase;
  private JCRNodeFactoryService jcrNodeFactoryService;
  private UserResolverService userResolverService;

  /**
   * @param userResolverService
   * 
   */
  @Inject
  public JcrAuthenticationResolverProvider(
      JCRNodeFactoryService jcrNodeFactoryService,
      @Named(SimpleJcrUserEnvironmentResolverService.JCR_USERENV_BASE) String userEnvironmentBase,
      UserResolverService userResolverService, RegistryService registryService) {
    this.jcrNodeFactoryService = jcrNodeFactoryService;
    this.userEnvironmentBase = userEnvironmentBase;
    this.userResolverService = userResolverService;

    // register as a resolver and a manager
    Registry<String, AuthenticationResolverProvider> authResolverRegistry = registryService
        .getRegistry(AuthenticationResolverServiceImpl.PROVIDER_REGISTRY);
    authResolverRegistry.add(this);
    Registry<String, AuthenticationManagerProvider> authManagerRegistry = registryService
        .getRegistry(AuthenticationResolverServiceImpl.MANAGER_PROVIDER_REGISTRY);
    authManagerRegistry.add(this);

  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.user.AuthenticationResolverProvider#authenticate(java.security.Principal)
   */
  public Authentication authenticate(Principal principal)
      throws SecurityException {
    if (principal instanceof IdPwPrincipal) {
      // resolve the location of the users security file, which is the Userenv
      // file
      IdPwPrincipal idPwPrincipal = (IdPwPrincipal) principal;
      User user = userResolverService.resolve(idPwPrincipal.getIdentifier());
      if (user != null) {
        try {
          String userEnvPath = getUserEnvPath(user.getUuid());
          Node n = jcrNodeFactoryService.getNode(userEnvPath);
          if (n != null) {
            Property p = n.getProperty(JCRPASSWORDHASH);
            String hash = p.getString();
            String nonce = StringUtils.sha1Hash(idPwPrincipal.getPassword());
            if (nonce.equals(hash)) {
              return new AuthenticationImpl(user);
            }
          }
        } catch (Exception ex) {
          throw new SecurityException("Authentication Failed for user "
              + idPwPrincipal.getIdentifier(), ex);
        }
      }
      throw new SecurityException("Authentication Failed for user "
          + idPwPrincipal.getIdentifier()+": not known to the system ");
    }
    throw new SecurityException("Authentication Principal " + principal
        + " not suitable for " + this.getClass().getName());
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.user.AuthenticationManagerProvider#setAuthentication(java.security.Principal,
   *      java.security.Principal)
   */
  public void setAuthentication(Principal oldPrincipal, Principal newPrincipal)
      throws SecurityException {
    if (oldPrincipal instanceof IdPwPrincipal
        && newPrincipal instanceof IdPwPrincipal) {
      IdPwPrincipal oldIdPwPrincipal = (IdPwPrincipal) oldPrincipal;
      IdPwPrincipal newIdPwPrincipal = (IdPwPrincipal) newPrincipal;
      if (oldIdPwPrincipal.getIdentifier().equals(
          newIdPwPrincipal.getIdentifier())) {
        User user = userResolverService.resolve(oldIdPwPrincipal
            .getIdentifier());
        String userEnvPath = getUserEnvPath(user.getUuid());
        try {
          Node n = jcrNodeFactoryService.getNode(userEnvPath);
          if (n == null) {
            throw new SecurityException(
                "User does not exist, cant set password");
          } else {
            Property p = n.getProperty(JCRPASSWORDHASH);
            String hash = p.getString();
            String nonce = StringUtils.sha1Hash(oldIdPwPrincipal.getPassword());
            if (nonce.equals(hash)) {
              nonce = StringUtils.sha1Hash(newIdPwPrincipal.getPassword());
              n.setProperty(JCRPASSWORDHASH, nonce);
              n.save();
              return; // success
            } else {
              throw new SecurityException(
                  "Old Passwords do not match, password was not changed ");
            }
          }
        } catch (Exception ex) {
          throw new SecurityException("Failed to set password :"
              + ex.getMessage(), ex);
        }
      } else {
        throw new SecurityException(
            "Princiapls do not reference the same user, password not changed ");
      }
    }
    throw new SecurityException("Principals not of correct type for "
        + JcrAuthenticationResolverProvider.class.getName());
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.Provider#getKey()
   */
  public String getKey() {
    return "jcr-authn-provider-sha1-hash";
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.Provider#getPriority()
   */
  public int getPriority() {
    return 0;
  }

  /**
   * @return
   */
  public String getUserEnvPath(String userId) {
    String prefix = PathUtils.getUserPrefix(userId);
    return userEnvironmentBase + prefix
        + SimpleJcrUserEnvironmentResolverService.USERENV;
  }
}
