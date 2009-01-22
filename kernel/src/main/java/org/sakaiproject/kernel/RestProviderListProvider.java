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
package org.sakaiproject.kernel;

import com.google.inject.Inject;
import com.google.inject.Provider;

import org.sakaiproject.kernel.api.rest.RestProvider;
import org.sakaiproject.kernel.rest.DefaultRestProvider;
import org.sakaiproject.kernel.rest.RestAuthenticationProvider;
import org.sakaiproject.kernel.rest.RestMeProvider;
import org.sakaiproject.kernel.rest.RestSiteProvider;
import org.sakaiproject.kernel.rest.RestSnoopProvider;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class RestProviderListProvider implements Provider<List<RestProvider>> {

  private final List<RestProvider> list = new ArrayList<RestProvider>();
  /**
   *
   */
  @Inject
  public RestProviderListProvider(DefaultRestProvider defaultRestProvider,
      RestAuthenticationProvider restAuthenticationProvider,
      RestMeProvider restMeProvider, RestSiteProvider siteProvider,
      RestSnoopProvider restSnoopProvider) {
    list.add(restAuthenticationProvider);
    list.add(defaultRestProvider);
    list.add(restMeProvider);
    list.add(restSnoopProvider);
  }
  /**
   * {@inheritDoc}
   * @see com.google.inject.Provider#get()
   */
  public List<RestProvider> get() {
    return list;
  }

}
