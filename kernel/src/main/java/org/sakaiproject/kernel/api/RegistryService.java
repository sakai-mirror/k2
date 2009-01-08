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
package org.sakaiproject.kernel.api;

import com.google.inject.ImplementedBy;
import com.google.inject.Singleton;

import org.sakaiproject.kernel.registry.RegistryServiceImpl;

/**
 * The registry service manages registries that are used to register providers
 */
@ImplementedBy(RegistryServiceImpl.class)
@Singleton
public interface RegistryService {
  /**
   * @param type
   *          the name of the registry to retrieve, normally taken from the
   *          service in question.
   * @return the registry of providers used for that service.
   */
  <T extends Provider> Registry<T> getRegistry(String type);
}