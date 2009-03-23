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
package org.sakaiproject.kernel.api.user;

import org.sakaiproject.kernel.api.Provider;
import org.sakaiproject.kernel.api.userenv.UserEnvironment;

/**
 * Interface to be implemented by parties interested in participating in the
 * provisioning of users. Provisioning agents are called by order of priority at
 * the end of the provisioning process.
 */
public interface UserProvisionAgent extends Provider<String> {
  String REGISTRY = "userProvisioning";

  /**
   * Injection point provisioning a user.
   *
   * @param userEnv
   */
  void provision(UserEnvironment userEnv);
}
