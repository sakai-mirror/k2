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

import org.sakaiproject.kernel.api.authz.AccessControlStatement;
import org.sakaiproject.kernel.api.authz.AuthzResolverService;
import org.sakaiproject.kernel.api.authz.PermissionDeniedException;
import org.sakaiproject.kernel.api.authz.PermissionQuery;
import org.sakaiproject.kernel.api.authz.QueryStatement;
import org.sakaiproject.kernel.api.authz.ReferenceResolverService;
import org.sakaiproject.kernel.api.authz.ReferencedObject;
import org.sakaiproject.kernel.api.memory.Cache;
import org.sakaiproject.kernel.api.memory.CacheManagerService;
import org.sakaiproject.kernel.api.memory.CacheScope;
import org.sakaiproject.kernel.api.session.SessionManagerService;
import org.sakaiproject.kernel.api.userenv.UserEnvironment;
import org.sakaiproject.kernel.api.userenv.UserEnvironmentResolverService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 */
public class SimpleAuthzResolverService implements AuthzResolverService {

  private SessionManagerService sessionManager;
  private ReferenceResolverService referenceResolverService;
  private UserEnvironmentResolverService userEnvironmentResolverService;
  private Cache<Map<String, List<AccessControlStatement>>> cachedAcl;
  private CacheManagerService cacheManagerService;

  /**
   * 
   */
  @Inject
  public SimpleAuthzResolverService(SessionManagerService sessionManager,
      ReferenceResolverService referenceResolverService,
      UserEnvironmentResolverService userEnvironmentResolverService,
      CacheManagerService cacheManagerService) {
    this.sessionManager = sessionManager;
    this.referenceResolverService = referenceResolverService;
    this.userEnvironmentResolverService = userEnvironmentResolverService;
    this.cachedAcl = cacheManagerService.getCache("acl_cache",
        CacheScope.CLUSTERINVALIDATED);
    this.cacheManagerService = cacheManagerService;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.authz.AuthzResolverService#check(java.lang.String,
   *      org.sakaiproject.kernel.api.authz.PermissionQuery)
   */
  public void check(String resourceReference, PermissionQuery permissionQuery)
      throws PermissionDeniedException {

    Cache<Boolean> grants = cacheManagerService.getCache("authz",
        CacheScope.REQUEST);

    String localPermissoinKey = permissionQuery.getKey(resourceReference);

    if (grants.containsKey(localPermissoinKey)) {
      if (grants.get(localPermissoinKey)) {
        return;
      } else {
        throw new PermissionDeniedException("No grant found on "
            + resourceReference + " by " + permissionQuery + " (request cached) ");

      }
    }

    UserEnvironment userEnvironment = userEnvironmentResolverService
        .resolve(sessionManager.getCurrentSession());
    ReferencedObject referencedObject = referenceResolverService
        .resolve(resourceReference);
    /*
     * build a hash of permission lists keyed by access control key, the access
     * control is populates in the permission list so that the access control
     * statements from higher up the tree are appended to the list. This means
     * that access control statements are order in closeness to the node, so
     * that close access control statements have more influence over the
     * security check
     */

    /*
     * the controlling object is the first object looking back up the tree that
     * has an ACL statement.
     */
    ReferencedObject controllingObject = null;

    /*
     * Check that the ACL isn't in the cache
     */
    Map<String, List<AccessControlStatement>> acl = cachedAcl
        .get(referencedObject.getKey());
    if (acl == null) {
      // not in the cache create, and populate
      acl = new HashMap<String, List<AccessControlStatement>>();

      for (AccessControlStatement ac : referencedObject.getAccessControlList()) {
        // if there was an acl this marks the position back up the hierarchy
        // that is the first node with acl statements.
        if (controllingObject == null) {
          controllingObject = referencedObject;
        }
        // populate the permissions into the map, appending to lists,
        // each key represents an access control item, the list contains
        // varieties of acl to be consulted
        String key = ac.getKey();
        List<AccessControlStatement> plist = acl.get(key);
        if (plist == null) {
          plist = new ArrayList<AccessControlStatement>();
          acl.put(key, plist);
        }
        plist.add(ac);
      }

      ReferencedObject parent = referencedObject.getParent();

      while (parent != null && !parent.isRoot()) {
        Map<String, List<AccessControlStatement>> parentAcl = cachedAcl
            .get(parent.getKey());
        if (parentAcl != null) {
          // copy the acl, appending found statements to the end of the current
          // node
          if (acl.size() > 0) {
            for (Entry<String, List<AccessControlStatement>> e : parentAcl
                .entrySet()) {
              List<AccessControlStatement> plist = acl.get(e.getKey());
              if (plist == null) {
                plist = new ArrayList<AccessControlStatement>();
                acl.put(e.getKey(), plist);
              }
              plist.addAll(e.getValue());
            }
          } else {
            // the acl was empty so we can just use this one.
            acl = parentAcl;
          }
          break;
        } else {
          // nothing in the cache, sop
          for (AccessControlStatement ac : parent.getAccessControlList()) {
            if (ac.isPropagating()) {
              // the ac is propagaing meaning it will propagate to child nodes.
              if (controllingObject == null) {
                controllingObject = parent;
              }
              String key = ac.getKey();
              List<AccessControlStatement> plist = acl.get(key);
              if (plist == null) {
                plist = new ArrayList<AccessControlStatement>();
                acl.put(key, plist);
              }
              plist.add(ac);
            }
          }
          parent = parent.getParent();
        }
      }
      cachedAcl.put(controllingObject.getKey(), acl);
    }

    // now we have the acl derived, we can now go through the permissionQuery,
    // extract the query statements to
    // see if any are satisfied or denied in order.

    for (QueryStatement qs : permissionQuery.statements()) {
      List<AccessControlStatement> kacl = acl.get(qs.getKey());
      for (AccessControlStatement ac : kacl) {
        if (userEnvironment.matches(ac.getSubject())) {
          if (ac.isGranted()) {
            // cache the response in the request scope cache.
            grants.put(localPermissoinKey, true);
            return;
          } else {
            // cache the response in the request scope cache.
            grants.put(localPermissoinKey, false);
            throw new PermissionDeniedException(
                "Permission Explicitly deinied on " + resourceReference
                    + " by " + ac + " for " + qs + " user environment "
                    + userEnvironment);
          }
        }
      }
    }

    // cache the response in the request scope cache.
    grants.put(localPermissoinKey, false);
    throw new PermissionDeniedException("No grant found on "
        + resourceReference + " by " + permissionQuery + " for "
        + userEnvironment);
  }
}
