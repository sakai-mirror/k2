/**
 * $Id$
 * $URL$
 **************************************************************************
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
 */

package org.sakaiproject.kernel.user;

import com.google.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.memory.Cache;
import org.sakaiproject.kernel.api.memory.CacheManagerService;
import org.sakaiproject.kernel.api.memory.CacheScope;
import org.sakaiproject.kernel.api.user.Authentication;
import org.sakaiproject.kernel.api.user.AuthenticationException;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Because DAV clients do not understand the concept of secure sessions, a DAV
 * user will end up asking Sakai to re-authenticate them for every action.
 * To ease the overhead, this class checks a size-limited timing-out cache
 * of one-way encrypted successful authentication IDs and passwords.
 * <p>
 * There's nothing DAV-specific about this class, and it's also independent of
 * any Sakai classes other than the "Authentication" user ID and EID holder.
 *
 * @author Aaron Zeckoski (aaron@caret.cam.ac.uk)
 */
public class AuthenticationCache {
	private static final Log LOG = LogFactory.getLog(AuthenticationCache.class);

	private Cache<AuthenticationRecord> authCache;

	/**
   * 
   */
	
	@Inject
  public AuthenticationCache(CacheManagerService cacheManager) {
    this.authCache = cacheManager.getCache(AuthenticationCache.class.getName(), CacheScope.INSTANCE);
  }

	public Authentication getAuthentication(String authenticationId, String password)
			throws AuthenticationException {
		Authentication auth = null;
		try {
			AuthenticationRecord record = authCache.get(authenticationId);
			if (MessageDigest.isEqual(record.encodedPassword, getEncrypted(password))) {
				if (record.authentication == null) {
					if (LOG.isDebugEnabled()) LOG.debug("getAuthentication: replaying authentication failure for authenticationId=" + authenticationId);
					throw new AuthenticationException("repeated invalid login");
				} else {
					if (LOG.isDebugEnabled()) LOG.debug("getAuthentication: returning record for authenticationId=" + authenticationId);
					auth = record.authentication;
				}
			} else {
				// Since the passwords didn't match, we're no longer getting repeats,
				// and so the record should be removed.
				if (LOG.isDebugEnabled()) LOG.debug("getAuthentication: record for authenticationId=" + authenticationId + " failed password check");
				authCache.remove(authenticationId);
			}
		} catch (NullPointerException e) {
			// this is ok and generally expected to indicate the value is not in the cache
			auth = null;
		}
		return auth;
	}

	public void putAuthentication(String authenticationId, String password, Authentication authentication) {
		putAuthenticationRecord(authenticationId, password, authentication);
	}

	public void putAuthenticationFailure(String authenticationId, String password) {
		putAuthenticationRecord(authenticationId, password, null);
	}

	protected void putAuthenticationRecord(String authenticationId, String password,
			Authentication authentication) {
		if (authCache.containsKey(authenticationId)) {
			// Don't indefinitely renew the cached record -- we want to force
			// real authentication after the timeout.
		} else {
			authCache.put( authenticationId,
					new AuthenticationRecord(getEncrypted(password), authentication, System.currentTimeMillis()) );
		}
	}

	private byte[] getEncrypted(String plaintext) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA");
			messageDigest.update(plaintext.getBytes("UTF-8"));
			return messageDigest.digest();
		} catch (NoSuchAlgorithmException e) {
			// This seems highly unlikely.
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}


	static class AuthenticationRecord {
		byte[] encodedPassword;
		Authentication authentication;	// Null for failed authentication
		long createTimeInMs;

		public AuthenticationRecord(byte[] encodedPassword, Authentication authentication, long createTimeInMs) {
			this.encodedPassword = encodedPassword;
			this.authentication = authentication;
			this.createTimeInMs = createTimeInMs;
		}
	}

}
