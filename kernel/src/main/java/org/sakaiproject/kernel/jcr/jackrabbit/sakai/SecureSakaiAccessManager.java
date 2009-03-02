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

package org.sakaiproject.kernel.jcr.jackrabbit.sakai;

import com.google.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.core.HierarchyManager;
import org.apache.jackrabbit.core.ItemId;
import org.apache.jackrabbit.core.NodeId;
import org.apache.jackrabbit.core.PropertyId;
import org.apache.jackrabbit.core.security.AMContext;
import org.apache.jackrabbit.core.security.AccessManager;
import org.apache.jackrabbit.spi.commons.conversion.DefaultNamePathResolver;
import org.apache.jackrabbit.spi.commons.namespace.NamespaceResolver;
import org.sakaiproject.kernel.api.ComponentActivatorException;
import org.sakaiproject.kernel.api.authz.AuthzResolverService;
import org.sakaiproject.kernel.api.authz.PermissionDeniedException;
import org.sakaiproject.kernel.api.authz.PermissionQuery;
import org.sakaiproject.kernel.api.jcr.JCRConstants;
import org.sakaiproject.kernel.api.jcr.JCRService;
import org.sakaiproject.kernel.api.memory.Cache;
import org.sakaiproject.kernel.api.memory.CacheManagerService;
import org.sakaiproject.kernel.api.memory.CacheScope;
import org.sakaiproject.kernel.authz.simple.SimplePermissionQuery;
import org.sakaiproject.kernel.authz.simple.SimpleQueryStatement;
import org.sakaiproject.kernel.jcr.api.internal.SakaiUserPrincipal;
import org.sakaiproject.kernel.jcr.jackrabbit.JCRAnonymousPrincipal;
import org.sakaiproject.kernel.jcr.jackrabbit.JCRSystemPrincipal;

import java.security.Principal;
import java.util.Set;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.security.auth.Subject;

/**
 */
public class SecureSakaiAccessManager implements AccessManager {
  private static final Log log = LogFactory
      .getLog(SecureSakaiAccessManager.class);

  private static final SimplePermissionQuery[] PERMISSION_QUERIES = new SimplePermissionQuery[READ
      | WRITE | REMOVE];

  private static final boolean debug = log.isDebugEnabled();

  private static final long TTL = 60000L; // cache for a minute

  static {
    for (int i = 0; i < PERMISSION_QUERIES.length; i++) {
      PERMISSION_QUERIES[i] = new SimplePermissionQuery(actionToString(i));
      if ((i & READ) == READ) {
        PERMISSION_QUERIES[i].addQueryStatement(new SimpleQueryStatement(
            PermissionQuery.READ));
      }
      if ((i & WRITE) == WRITE) {
        PERMISSION_QUERIES[i].addQueryStatement(new SimpleQueryStatement(
            PermissionQuery.WRITE));
      }
      if ((i & REMOVE) == REMOVE) {
        PERMISSION_QUERIES[i].addQueryStatement(new SimpleQueryStatement(
            PermissionQuery.REMOVE));
      }
    }

  }

  /**
   * The Subject represents the User or Actor that this access manager belongs
   * to, it is not related to the SubjectToken in the Sakai K2 AuthZ system.
   */
  private Subject subject;

  @SuppressWarnings("unused")
  private HierarchyManager hierMgr;

  /**
   * If the User or Actor using this access manager is an anonymous user, this
   * ill be true.
   */
  protected boolean anonymous = false;

  /**
   * Set to true once the Access Manager is initialized
   */
  private boolean initialized = false;

  /**
   * If the Actor bound to this access manager is the system, then this is try,
   * otherwise it its false.
   */
  protected boolean sakaisystem = false;

  private NamespaceResolver resolver;

  /**
   * The sakai User Id, or null if there is none.
   */
  protected String sakaiUserId = null;

  @SuppressWarnings("unused")
  private DefaultNamePathResolver pathResolver;

  /**
   * The AuthZ Resolver
   */
  private AuthzResolverService authzResolverService;

  /**
   * A Request Scope Cache containing already resolved permissions, we dont
   * support inverting a permissions resolution half way though a request cycle.
   */
  private Cache<ExpiringGrant<Boolean>> cache;

  /**
   * The session that we are bound to.
   */
  private Session session;

  private ThreadLocal<Boolean> checking = new ThreadLocal<Boolean>() {
    /**
     * {@inheritDoc}
     * 
     * @see java.lang.ThreadLocal#initialValue()
     */
    @Override
    protected Boolean initialValue() {
      return false;
    }
  };

  private JCRService jcrService;

  private CacheManagerService cacheManagerService;

  private Object sessionLock = new Object();

  private static int ncalls;

  private static int nfullresolvegdeny;

  private static int nfullresolvegrant;

  private static int ncachedresolve;

  private static int nresolve;

  private static int nrequestgrant;

  private static int nanondnied;

  private static int nadmin;

  private static long dumptime = System.currentTimeMillis();

  @Inject
  public SecureSakaiAccessManager(JCRService jcrService,
      AuthzResolverService authzResolverService,
      CacheManagerService cacheManagerService)
      throws ComponentActivatorException, RepositoryException {
    this.authzResolverService = authzResolverService;
    this.jcrService = jcrService;
    this.cacheManagerService = cacheManagerService;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.apache.jackrabbit.core.security.AccessManager#init(org.apache.jackrabbit
   * .core.security.AMContext)
   */
  @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = { "BC_VACUOUS_INSTANCEOF" }, justification = "The type safety in only at compile time.")
  public void init(AMContext context) throws AccessDeniedException, Exception {
    if (initialized) {
      throw new IllegalStateException("already initialized");
    }

    cache = cacheManagerService.getCache("jcr-accessmanager",
        CacheScope.INSTANCE);
    

    subject = context.getSubject();
    hierMgr = context.getHierarchyManager();
    resolver = context.getNamespaceResolver();
    pathResolver = new DefaultNamePathResolver(resolver, true);

    anonymous = !subject.getPrincipals(JCRAnonymousPrincipal.class).isEmpty();
    if (!anonymous) {
      sakaisystem = !subject.getPrincipals(JCRSystemPrincipal.class).isEmpty();

      Set<SakaiUserPrincipal> principals = subject
          .getPrincipals(SakaiUserPrincipal.class);
      if (principals.size() == 0) {
        if (log.isDebugEnabled()) {
          log.debug("No SakaiUserPrincipal found for context: " + context);
        }
      } else {
        for (Principal p : principals) {
          if (p instanceof SakaiUserPrincipal) {
            SakaiUserPrincipal sp = (SakaiUserPrincipal) p;
            sakaiUserId = sp.getName();
          }
        }
      }
    }

    initialized = true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.jackrabbit.core.security.AccessManager#close()
   */
  public synchronized void close() throws Exception {
    if (!initialized) {
      throw new IllegalStateException("not initialized");
    }

    initialized = false;

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.apache.jackrabbit.core.security.AccessManager#checkPermission(org.apache
   * .jackrabbit.core.ItemId, int)
   */
  public void checkPermission(ItemId item, int permission)
      throws AccessDeniedException, ItemNotFoundException, RepositoryException {
    if (!initialized) {
      throw new RepositoryException("Access Manager is not initialized ");
    }
    if (!isGranted(item, permission)) {
      if (debug) {
        log.debug("Denied " + permission + " on " + item);
      }
      throw new AccessDeniedException("Permission deined to " + sakaiUserId
          + " to " + PERMISSION_QUERIES[permission] + "on" + item);
    }
    log.info("Granted " + permission + " on " + item);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.apache.jackrabbit.core.security.AccessManager#isGranted(org.apache.
   * jackrabbit.core.ItemId, int)
   */
  public boolean isGranted(ItemId item, int permission)
      throws ItemNotFoundException, RepositoryException {
    // not initialized, or closed
    if (!initialized) {
      throw new RepositoryException("Access Manager is not initialized ");
    }
    try {
      ncalls++;
      // this is the system just grant
      if (sakaisystem) {
        nadmin++;
        return true;
      }
      // anon is never allowed to write
      if (anonymous
          && ((permission & AccessManager.WRITE) == AccessManager.WRITE || (permission & AccessManager.REMOVE) == AccessManager.REMOVE)) {
        nanondnied++;
        return false;
      }
      // check for a security manager bypass
      String requestGrant = authzResolverService.getRequestGrant();
      if (requestGrant != null) {
        nrequestgrant++;
        return true;
      }
      if (session == null) {
        synchronized (sessionLock) {
          session = jcrService.getSession();
        }
      }
      if (!checking.get()) {
        try {
          // in checking so dont recurse.
          checking.set(true);
          nresolve++;
          Node node = null;
          if (item.denotesNode()) {
            NodeId nid = (NodeId) item;
            node = session.getNodeByUUID(nid.getUUID().toString());
          } else {
            PropertyId propertyId = (PropertyId) item;
            node = session.getNodeByUUID(propertyId.getParentId().toString());
          }
          // find the first NT_FILE or NT_FOLDER parent
          String nodeType = node.getPrimaryNodeType().getName();
          Node rootNode = session.getRootNode();
          while (node != rootNode && !JCRConstants.NT_FILE.equals(nodeType)
              && !JCRConstants.NT_FOLDER.equals(nodeType)) {
            Node parent = node.getParent();
            if (parent == null) {
              break; // this should never happen unless we are dealing with some
              // wierd structure.
            }
            node = parent;
            nodeType = node.getPrimaryNodeType().getName();
          }
          PermissionQuery spq = PERMISSION_QUERIES[permission];
          String resourceReference = node.getPath();
          String queryKey = spq.getQueryToken(resourceReference);
          if (cache.containsKey(sakaiUserId + ":" + queryKey)) {
            ExpiringGrant<Boolean> cg = cache.get(sakaiUserId + ":" + queryKey);
            if (cg != null && !cg.hasExpired()) {
              ncachedresolve++;
              return cg.getPayload();
            }
          }

          // the node is now located on a File or Folder, this the path we are
          // intereted in working on.

          try {
            // potentially expensive call.
            authzResolverService.check(resourceReference, spq);
            cache.put(sakaiUserId + ":" + queryKey, new ExpiringGrant<Boolean>(true,TTL));
            nfullresolvegrant++;
            return true;
          } catch (PermissionDeniedException denied) {
            nfullresolvegdeny++;
            cache.put(sakaiUserId + ":" + queryKey, new ExpiringGrant<Boolean>(false,TTL));
            if (debug) {
              log.debug("Permission Denied for " + sakaiUserId + " on "
                  + resourceReference + ":" + denied.getMessage());
            }
            return false;
          }

        } finally {
          // out of checking
          checking.set(false);
        }
      } else {
        // internal just exit with granted
        return true;
      }
    } finally {
      if (System.currentTimeMillis() > dumptime) {
        dumptime = System.currentTimeMillis() + 30000L;
        int ncachemiss = 0;
        int panondnied = 0;
        int prequestgrant = 0;
        int presolve = 0;
        int padmin = 0;
        if (ncalls > 0) {
          padmin = nadmin * 100 / ncalls;
          presolve = nresolve * 100 / ncalls;
          prequestgrant = nrequestgrant * 100 / ncalls;
          panondnied = nanondnied * 100 / ncalls;
        }
        int pcachemiss = 0;
        int pfullresolvegdeny = 0;
        int pfullresolvegrant = 0;
        int pcachedresolve = 0;
        if (nresolve > 0) {
          ncachemiss = nfullresolvegdeny + nfullresolvegrant;
          pcachemiss = ncachemiss * 100 / nresolve;
          pfullresolvegdeny = nfullresolvegdeny * 100 / nresolve;
          pfullresolvegrant = nfullresolvegrant * 100 / nresolve;
          pcachedresolve = ncachedresolve * 100 / nresolve;
        }
        log.info("Calls:" + ncalls + "," + " Resolved:" + nresolve + "(" + presolve
            + "%)," + " Cache(Hit:"
            + ncachedresolve + "(" + pcachedresolve + "%)," +
            " Miss:" + ncachemiss + "(" + pcachemiss + "%))," + " Resolved(Grant:"
            + nfullresolvegrant + "(" + pfullresolvegrant + "%)," + " Deny:"
            + nfullresolvegdeny + "(" + pfullresolvegdeny + "%)),"  + "Misc(super user:" + nadmin
            + "(" + padmin + "%)," + " anon deny:" + nanondnied + "(" + panondnied
            + "%)," + " request granted:" + nrequestgrant + "(" + prequestgrant + "%))");
        ncalls = 0;
        nadmin = 0;
        nanondnied = 0;
        ncachedresolve = 0;
        nfullresolvegdeny = 0;
        nfullresolvegrant = 0;
        nresolve = 0;
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.apache.jackrabbit.core.security.AccessManager#canAccess(java.lang.String
   * )
   */
  public boolean canAccess(String workspace) throws NoSuchWorkspaceException,
      RepositoryException {
    return true;
  }

  /**
   * @param permission
   * @return
   */
  private static String actionToString(int permission) {
    if (permission == 0) {
      return "none";
    }
    StringBuilder sb = new StringBuilder();
    if ((permission & AccessManager.READ) == AccessManager.READ) {
      sb.append("read ");
    }
    if ((permission & AccessManager.REMOVE) == AccessManager.REMOVE) {
      sb.append("remove ");
    }
    if ((permission & AccessManager.WRITE) == AccessManager.WRITE) {
      sb.append("write ");
    }
    return sb.toString();
  }

}
