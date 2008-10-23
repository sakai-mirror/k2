/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2007, 2008 Sakai Foundation
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

package org.sakaiproject.kernel.jcr.jackrabbit.sakai;

import org.apache.jackrabbit.core.security.CredentialsCallback;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.KernelConfigurationException;
import org.sakaiproject.kernel.api.KernelManager;
import org.sakaiproject.kernel.api.ServiceManager;
import org.sakaiproject.kernel.api.ServiceSpec;
import org.sakaiproject.kernel.api.user.Authentication;
import org.sakaiproject.kernel.api.user.AuthenticationException;
import org.sakaiproject.kernel.api.user.AuthenticationManager;
import org.sakaiproject.kernel.api.user.User;
import org.sakaiproject.kernel.api.user.UserDirectoryService;
import org.sakaiproject.kernel.api.user.UserNotDefinedException;
import org.sakaiproject.kernel.jcr.jackrabbit.JCRAnonymousPrincipal;
import org.sakaiproject.kernel.jcr.jackrabbit.JCRSystemPrincipal;

import java.security.Principal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

public class SakaiLoginModule implements LoginModule {
  private static final String SAKAI_SYSTEM_USER = "sakaisystem";

  private static final String SAKAI_ANON_USER = ".anon";

  private Subject subject;

  private CallbackHandler callbackHandler;

  private final Set<Principal> principals = new HashSet<Principal>();

  private UserDirectoryService userDirectoryService;

  private AuthenticationManager authenticationManager;

  @SuppressWarnings("unused")
  private Map<String, ?> sharedState;

  @SuppressWarnings("unused")
  private Map<String, ?> options;

  /**
   * Constructor
   * 
   * @throws KernelConfigurationException
   */
  public SakaiLoginModule() throws KernelConfigurationException {
    KernelManager km = new KernelManager();
    Kernel k = km.getKernel();
    ServiceManager sm = k.getServiceManager();
    this.userDirectoryService = sm.getService(new ServiceSpec(
        UserDirectoryService.class));
    this.authenticationManager = sm.getService(new ServiceSpec(
        AuthenticationManager.class));
  }

  /**
   * {@inheritDoc}
   */
  public void initialize(Subject subject, CallbackHandler callbackHandler,
      Map<String, ?> sharedState, Map<String, ?> options) {
    this.subject = subject;
    this.callbackHandler = callbackHandler;
    this.sharedState = sharedState;
    this.options = options;
  }

  /**
   * {@inheritDoc}
   */
  public boolean login() throws LoginException {
    // prompt for a user name and password
    if (callbackHandler == null) {
      throw new LoginException("no CallbackHandler available");
    }

    boolean authenticated = false;
    principals.clear();
    try {

      // Get credentials using a JAAS callback
      CredentialsCallback ccb = new CredentialsCallback();
      callbackHandler.handle(new Callback[] { ccb });
      Credentials creds = ccb.getCredentials();
      // Use the credentials to set up principals
      if (creds != null) {
        if (creds instanceof SimpleCredentials) {
          SimpleCredentials sc = (SimpleCredentials) creds;
          // authenticate

          User u = null;
          try {
            Authentication auth = authenticationManager
                .authenticate(new JCRIdPwEvidence(sc.getUserID(), new String(sc
                    .getPassword())));
            u = userDirectoryService.getUser(auth.getUid());
          } catch (NullPointerException e) {
            u = null;
          } catch (AuthenticationException e) {
            u = null;
          } catch (UserNotDefinedException e) {
            u = null;
          }
          // old way used UDS directly, no caching, new way above gets cached
          // -AZ
          // User u = userDirectoryService.authenticate(sc.getUserID(),
          // new String(sc.getPassword()));
          if (u == null) {
            principals.add(new JCRAnonymousPrincipal(SAKAI_ANON_USER));
          } else {
            principals.add(new SakaiUserPrincipalImpl(u));
          }

          authenticated = true;
        } else if (creds instanceof SakaiJCRCredentials) {
          principals.add(new JCRSystemPrincipal(SAKAI_SYSTEM_USER));
          authenticated = true;
        }
      } else {
        // authenticated via Session or Sakai Wrapper
        User u = userDirectoryService.getCurrentUser();
        if (u == null) {
          principals.add(new JCRAnonymousPrincipal(SAKAI_ANON_USER));
        } else {
          principals.add(new SakaiUserPrincipalImpl(u));
        }
        authenticated = true;
      }
    } catch (java.io.IOException ioe) {
      throw new LoginException(ioe.toString());
    } catch (UnsupportedCallbackException uce) {
      throw new LoginException(uce.getCallback().toString() + " not available");
    }

    if (authenticated) {
      return !principals.isEmpty();
    } else {
      principals.clear();
      throw new FailedLoginException();
    }
  }

  /**
   * {@inheritDoc}
   */
  public boolean commit() throws LoginException {
    if (principals.isEmpty()) {
      return false;
    } else {
      // add a principals (authenticated identities) to the Subject
      subject.getPrincipals().addAll(principals);
      return true;
    }
  }

  /**
   * {@inheritDoc}
   */
  public boolean abort() throws LoginException {
    if (principals.isEmpty()) {
      return false;
    } else {
      logout();
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  public boolean logout() throws LoginException {
    subject.getPrincipals().removeAll(principals);
    principals.clear();
    return true;
  }
}
