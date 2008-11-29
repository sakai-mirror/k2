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

import org.sakaiproject.kernel.api.authz.AccessControlStatement;
import org.sakaiproject.kernel.api.authz.ReferencedObject;

import java.util.ArrayList;
import java.util.Collection;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;

/**
 * 
 */
public class JcrReferenceObject implements ReferencedObject {

  
  private static final String ACL_PROPERTY = null;
  private ArrayList<AccessControlStatement> acl;
  private ArrayList<AccessControlStatement> inheritableAcl;
  private String path;
  private boolean rootReference = false;
  private JcrReferenceObject parentReference;

  /**
   * @param n
   * @throws RepositoryException 
   */
  public JcrReferenceObject(Node node) throws RepositoryException {
    path = node.getPath();
    acl = new ArrayList<AccessControlStatement>();
    inheritableAcl = new ArrayList<AccessControlStatement>();
    for ( PropertyIterator pi = node.getProperties(ACL_PROPERTY); pi.hasNext(); ) {
      Property p = pi.nextProperty();
      AccessControlStatement acs = new JcrAccessControlStatementImpl(p);
      if ( acs.isPropagating() ) {
        inheritableAcl.add(acs);
      }
      acl.add(acs);
    }
    Node parent = null;
    try {
      parent = node.getParent();
    } catch (ItemNotFoundException e) {
    } catch (AccessDeniedException e) {
    } catch (RepositoryException e) {
    }
    if ( parent != null ) {
      parentReference = new JcrReferenceObject(parent);
      if ( parentReference.getInheritableAccessControlList().size() == 0 ) {
        rootReference = true;
      }
    } else {
      rootReference = true;
    }
    
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.authz.ReferencedObject#getAccessControlList()
   */
  public Collection<? extends AccessControlStatement> getAccessControlList() {
    return acl;
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.authz.ReferencedObject#getInheritableAccessControlList()
   */
  public Collection<? extends AccessControlStatement> getInheritableAccessControlList() {
    return inheritableAcl;
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.authz.ReferencedObject#getKey()
   */
  public String getKey() {
    return path;
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.authz.ReferencedObject#getParent()
   */
  public ReferencedObject getParent() {
    return parentReference;
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.authz.ReferencedObject#isRoot()
   */
  public boolean isRoot() {
    return rootReference;
  }

}
