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
import org.sakaiproject.kernel.api.user.UserResolverService;

/**
 * A Null User Resolver Service that just sets eid == uid. Not to be used in
 * production.
 */
public class NullUserResolverServiceImpl implements UserResolverService {

  private static final Log LOG = LogFactory
      .getLog(NullUserResolverServiceImpl.class);

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.user.UserResolverService#resolve(java.lang.String)
   */
  public User resolve(final String identifier) {
    LOG.warn("NON PRODUCTION CORE: Null resolution of user " + identifier);
    return new User() {

      /**
       * 
       */
      private static final long serialVersionUID = 3786435035308647068L;

      public String getEid() {
        return identifier;
      }

      public String getUuid() {
        return identifier;
      }

    };
  }

}
