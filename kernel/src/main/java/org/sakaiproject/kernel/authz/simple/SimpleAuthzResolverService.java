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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 */
public class SimpleAuthzResolverService implements AuthzResolverService {

  private static final Log LOG = LogFactory
      .getLog(SimpleAuthzResolverService.class);
  private SessionManagerService sessionManager;
  private ReferenceResolverService referenceResolverService;
  private UserEnvironmentResolverService userEnvironmentResolverService;
  private Cache<Map<String, List<AccessControlStatement>>> cachedAcl;
  private CacheManagerService cacheManagerService;

  private long secureKey = System.currentTimeMillis();

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

    System.err.println("Check on " + resourceReference + " For ["
        + permissionQuery.getQueryToken(resourceReference) + "]");
    Cache<Boolean> grants = cacheManagerService.getCache("authz",
        CacheScope.REQUEST);
    if (grants.containsKey("request-granted" + secureKey)) {
      System.err.println("Bypassed Security ");
      return;
    }

    String permissionQueryToken = permissionQuery
        .getQueryToken(resourceReference);

    if (grants.containsKey(permissionQueryToken)) {
      if (grants.get(permissionQueryToken)) {
        System.err.println("Security Cached granted  " + permissionQueryToken);
        return;
      } else {
        throw new PermissionDeniedException("No grant found on "
            + resourceReference + " by " + permissionQuery
            + " (request cached) ");

      }
    }

    UserEnvironment userEnvironment = userEnvironmentResolverService
        .resolve(sessionManager.getCurrentSession());
    System.err.println(" User Env " + userEnvironment);
    System.err.println(" Locating Referenced object " + resourceReference);
    ReferencedObject referencedObject = referenceResolverService
        .resolve(resourceReference);
    System.err.println(" Got Referenced Object " + referencedObject);
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
      System.err.println("Acl is null, creating it");
      // not in the cache create, and populate
      acl = new HashMap<String, List<AccessControlStatement>>();
      Collection<? extends AccessControlStatement> aclList = referencedObject
          .getAccessControlList();
      if (aclList.size() == 0) {
        System.err.println("ACL For node is empty ");
      } else {

        for (AccessControlStatement ac : referencedObject
            .getAccessControlList()) {
          // if there was an acl this marks the position back up the hierarchy
          // that is the first node with acl statements.
          if (controllingObject == null) {
            controllingObject = referencedObject;
          }
          // populate the permissions into the map, appending to lists,
          // each key represents an access control item, the list contains
          // varieties of acl to be consulted
          String key = ac.getStatementKey();
          List<AccessControlStatement> plist = acl.get(key);
          if (plist == null) {
            System.err.println("Creating new key " + key);
            plist = new ArrayList<AccessControlStatement>();
            acl.put(key, plist);
          }
          System.err.println("Adding to " + key + " acs " + ac);
          plist.add(ac);
        }
      }

      ReferencedObject parent = referencedObject.getParent();

      while (parent != null ) {
        Map<String, List<AccessControlStatement>> parentAcl = cachedAcl
            .get(parent.getKey());
        if (parentAcl != null) {
          System.err.println("appending cache parent acl for "
              + parent.getKey());
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

          Collection<? extends AccessControlStatement> pAcl = parent
              .getAccessControlList();
          if (pAcl.size() == 0) {
            System.err.println("ACL For Parent node is empty "
                + parent.getKey());
          } else {
            // nothing in the cache, sop
            for (AccessControlStatement ac : pAcl) {
              if (ac.isPropagating()) {
                // the ac is propagaing meaning it will propagate to child
                // nodes.
                if (controllingObject == null) {
                  controllingObject = parent;
                }
                String key = ac.getStatementKey();
                List<AccessControlStatement> plist = acl.get(key);
                if (plist == null) {
                  System.err.println("Creating new parent key " + key);
                  plist = new ArrayList<AccessControlStatement>();
                  acl.put(key, plist);
                }
                System.err.println("Adding to  " + key + " " + ac);
                plist.add(ac);
              }
            }
          }
          // if this was the root element, stop resolution
          if ( parent.isRoot() ) {
            break;
          }
          parent = parent.getParent();
        }
        System.err.println("Next parent is " + parent);
        if (parent != null) {
          System.err.println(" Parent Key " + parent.getKey());
        }
      }
      if (controllingObject != null) {
        cachedAcl.put(controllingObject.getKey(), acl);
      }
    } else {
      System.err.println(" Using Cached ACL");
    }

    if (acl.size() == 0) {
      System.err.println("WARNING ------------------Empty ACL" );
    } else {
      for (String k : acl.keySet()) {
        System.err.println("Loaded ACL for " + k);
      }
    }
    // now we have the acl derived, we can now go through the permissionQuery,
    // extract the query statements to
    // see if any are satisfied or denied in order.

    System.err.println(" Checking against "+userEnvironment);
    for (QueryStatement qs : permissionQuery.statements()) {
      System.err.println("Evaluating " + qs.getStatementKey());
      List<AccessControlStatement> kacl = acl.get(qs.getStatementKey());
      if (kacl != null) {
        System.err.println("Found ACL set " + kacl + " for key set "
            + qs.getStatementKey());
        for (AccessControlStatement ac : kacl) {
          if (userEnvironment.matches(ac.getSubject())) {
            if (ac.isGranted()) {
              System.err.println("Granted Permission " + ac);
              // cache the response in the request scope cache.
              grants.put(permissionQueryToken, true);
              return;
            } else {
              // cache the response in the request scope cache.
              grants.put(permissionQueryToken, false);
              System.err.println("Denied Permission " + ac);
              throw new PermissionDeniedException(
                  "Permission Explicitly deinied on " + resourceReference
                      + " by " + ac + " for " + qs + " user environment "
                      + userEnvironment);
            }
          } else {
            System.err.println("User does not have subject matching "+ac.getSubject());
          }
        }
      } else {
        System.err.println("No ACL found for " + qs.getStatementKey());
      }
    }

    // cache the response in the request scope cache.
    grants.put(permissionQueryToken, false);
    System.err.println("Denied Permission, no match "
        + sessionManager.getCurrentSession().getUserId());
    throw new PermissionDeniedException("No grant found on "
        + resourceReference + " by " + permissionQuery + " for "
        + userEnvironment);
  }

  public void setRequestGrant() {
    Cache<Boolean> grants = cacheManagerService.getCache("authz",
        CacheScope.REQUEST);
    grants.put("request-granted" + secureKey, true);
    LOG.warn("Request Fully Granted ");
  }

  public void clearRequestGrant() {
    Cache<Boolean> grants = cacheManagerService.getCache("authz",
        CacheScope.REQUEST);
    grants.remove("request-granted" + secureKey);
    LOG.warn("Request Granted Removed ");
  }
}
