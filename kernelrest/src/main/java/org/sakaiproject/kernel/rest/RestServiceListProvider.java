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
package org.sakaiproject.kernel.rest;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provider;

import org.sakaiproject.kernel.rest.count.RestCountProvider;
import org.sakaiproject.kernel.rest.friends.RestFriendsProvider;
import org.sakaiproject.kernel.rest.me.RestMeProvider;
import org.sakaiproject.kernel.rest.presence.PresenceProvider;
import org.sakaiproject.kernel.rest.search.RestSearchProvider;
import org.sakaiproject.kernel.rest.site.SiteProvider;
import org.sakaiproject.kernel.webapp.Initialisable;

import java.util.List;

/**
 *
 */
public class RestServiceListProvider implements Provider<List<Initialisable>> {

  private List<Initialisable> list;

  /**
   *
   */
  @Inject
  public RestServiceListProvider(RestFriendsProvider restFriendsProvider,
      DefaultRestProvider defaultRestProvider, RestMeProvider restMeProvider,
      RestSnoopProvider restSnoopProvider, RestSearchProvider restSearchProvider,
      PresenceProvider presenceProvider,
      SiteProvider siteProvider, RestCountProvider restCountProvider) {
    list = ImmutableList.of((Initialisable) restFriendsProvider, defaultRestProvider,
        restMeProvider, restSnoopProvider,
        restSearchProvider, presenceProvider, siteProvider, restCountProvider);
  }

  /**
   * {@inheritDoc}
   *
   * @see com.google.inject.Provider#get()
   */
  public List<Initialisable> get() {
    return list;
  }
}
