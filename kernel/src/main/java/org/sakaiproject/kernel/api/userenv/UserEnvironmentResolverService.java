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
package org.sakaiproject.kernel.api.userenv;

import com.google.inject.ImplementedBy;

import org.sakaiproject.kernel.api.session.Session;
import org.sakaiproject.kernel.authz.simple.SimpleJcrUserEnvironmentResolverService;

/**
 * The UserEnvironmentResolverService resolves {@link UserEnvironment} based on
 * {@link Session} objects.
 */
@ImplementedBy(SimpleJcrUserEnvironmentResolverService.class)
public interface UserEnvironmentResolverService {

  /**
   * Setting: The time to live of User Env objects the local cache, this should
   * be set in the kernel properties file.
   */
  public static final String TTL = "userenv.ttl";

  /**
   * Get a {@link UserEnvironment} objects based on the supplied session.
   * 
   * @param currentSession
   *          the supplied session.
   * @return the UserEnvironment object.
   */
  UserEnvironment resolve(Session currentSession);


}
