/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2007, 2008 Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

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
 * @author ieb
 */
public class SecureSakaiAccessManager implements AccessManager {
  private static final Log log = LogFactory
      .getLog(SecureSakaiAccessManager.class);

  private static final SimplePermissionQuery[] PERMISSION_QUERIES = new SimplePermissionQuery[READ&WRITE&REMOVE];
  
  static {
    for ( int i = 0; i < PERMISSION_QUERIES.length; i++ ) {
      PERMISSION_QUERIES[i] = new SimplePermissionQuery(actionToString(i));
      if ( (i&READ) == READ ) {
        PERMISSION_QUERIES[i].addQueryStatement(new SimpleQueryStatement(PermissionQuery.READ));
      }
      if ( (i&WRITE) == WRITE ) {
        PERMISSION_QUERIES[i].addQueryStatement(new SimpleQueryStatement(PermissionQuery.WRITE));
      }
      if ( (i&REMOVE) == REMOVE ) {
        PERMISSION_QUERIES[i].addQueryStatement(new SimpleQueryStatement(PermissionQuery.REMOVE));
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
  private Cache<Boolean> cache;

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

  @Inject
  public SecureSakaiAccessManager(JCRService jcrService, AuthzResolverService authzResolverService, CacheManagerService cacheManagerService) throws ComponentActivatorException,
      RepositoryException {
    this.authzResolverService = authzResolverService;
    session = jcrService.getSession();
    cache = cacheManagerService.getCache("jcr-accessmanager",
        CacheScope.REQUEST);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.apache.jackrabbit.core.security.AccessManager#init(org.apache.jackrabbit
   * .core.security.AMContext)
   */
  public void init(AMContext context) throws AccessDeniedException, Exception {
    if (initialized) {
      throw new IllegalStateException("already initialized");
    }
        

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
    if ( !initialized ) {
      throw new RepositoryException("Access Manager is not initialized ");
    }
    if (!isGranted(item, permission)) {
      throw new AccessDeniedException("Permission deined to "
          + PERMISSION_QUERIES[permission] + "on" + item);
    }
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
    if ( !initialized ) {
      throw new RepositoryException("Access Manager is not initialized ");
    }
    // this is the system just grant
    if (sakaisystem) {
      return true;
    }
    if (!checking.get()) {
      try {
        // in checking so dont recurse.
        checking.set(true);
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
        while (!JCRConstants.NT_FILE.equals(nodeType)
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
        if (cache.containsKey(queryKey)) {
          return cache.get(queryKey);
        }

        // the node is now located on a File or Folder, this the path we are
        // intereted in working on.

        try {
          // potentially expensive call.
          authzResolverService.check(resourceReference, spq);
          cache.put(queryKey, true);
          return true;
        } catch (PermissionDeniedException denied) {
          cache.put(queryKey, false);
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
    // TODO look up the workspace in
    return true;
  }

  /**
   * @param permission
   * @return
   */
  private static String actionToString(int permission) {
    if ( permission == 0 ) {
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
