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
package org.sakaiproject.kernel.authz.simple;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.sakaiproject.kernel.api.authz.ReferenceResolverService;
import org.sakaiproject.kernel.api.authz.ReferencedObject;
import org.sakaiproject.kernel.util.StringUtils;

import java.util.Map;

/**
 * A Path Resolver looks at the first element of the path, and attempts to look
 * it up a resolver in a map. If there is no match, the default resolver is
 * used.
 */
public class PathReferenceResolverService implements ReferenceResolverService {

  public static final String DEFAULT_RESOLVER = "resolvers.default";
  private Map<String, ReferenceResolverService> resolvers;
  private ReferenceResolverService defaultResolver;

  /**
   * 
   */
  @Inject
  public PathReferenceResolverService(
      @Named(DEFAULT_RESOLVER) ReferenceResolverService defaultResolver,
      Map<String, ReferenceResolverService> resolvers) {
    this.resolvers = resolvers;
    this.defaultResolver = defaultResolver;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.authz.ReferenceResolverService#resolve(java.lang.String)
   */
  public ReferencedObject resolve(String resourceReference) {
    String[] locator = StringUtils.split(resourceReference, '/', 1);
    ReferenceResolverService resolver = resolvers.get(locator);
    if (resolver != null) {
      return resolver.resolve(resourceReference);
    }
    return defaultResolver.resolve(resourceReference);
  }

}