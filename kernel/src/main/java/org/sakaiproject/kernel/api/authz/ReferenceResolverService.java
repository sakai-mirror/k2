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
package org.sakaiproject.kernel.api.authz;

import com.google.inject.ImplementedBy;

import org.sakaiproject.kernel.authz.simple.SimpleReferenceResolverService;

/**
 * The Reference resolver server resolves reference URI's into ReferenceObjects.
 */
@ImplementedBy(SimpleReferenceResolverService.class)
public interface ReferenceResolverService {

  /**
   * Resolve the ReferenceObject.
   * 
   * @param resourceReference
   *          the reference URI. If this is native, it will contain no domain.
   * @return the ReferenceObject after resolution, null if no reference object
   *         is present.
   */
  ReferencedObject resolve(String resourceReference);

}