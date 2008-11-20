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
package org.sakaiproject.kernel.authz.minimal;

import org.sakaiproject.kernel.api.authz.AuthzResolverService;
import org.sakaiproject.kernel.api.authz.PermissionDeniedException;
import org.sakaiproject.kernel.api.authz.PermissionQuery;

/**
 * A mimimal authz resolution service that says yes to eveything.
 */
public class MinimalAuthzResolverService implements AuthzResolverService {

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.authz.AuthzResolverService#check(java.lang.String, org.sakaiproject.kernel.api.authz.PermissionQuery)
   */
  public void check(String resourceReference, PermissionQuery permissionQuery)
      throws PermissionDeniedException {
    
  }

}
