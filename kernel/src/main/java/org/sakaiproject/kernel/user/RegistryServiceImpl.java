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

import org.sakaiproject.kernel.api.user.Provider;
import org.sakaiproject.kernel.api.user.Registry;
import org.sakaiproject.kernel.api.user.RegistryService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 */
public class RegistryServiceImpl implements RegistryService {

  Map<String, Registry<? extends Provider>> providerMap = new ConcurrentHashMap<String, Registry<? extends Provider>>();


  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.user.ProviderRegistryService#getProviderRegistry(org.sakaiproject.kernel.api.user.ProviderRegistryType)
   */
  @SuppressWarnings("unchecked")
  public <T extends Provider> Registry<T> getRegistry(
      String type) {
    Registry<? extends Provider> providerRegistry = providerMap.get(type);
    if ( providerRegistry == null ) {
      providerRegistry = new RegistryImpl<T>();
      providerMap.put(type, providerRegistry);
    }
    return (Registry<T>) providerRegistry;
  }



}
