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

import org.sakaiproject.kernel.api.UpdateFailedException;
import org.sakaiproject.kernel.api.authz.AccessControlStatement;
import org.sakaiproject.kernel.api.authz.ReferencedObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

/**
 * 
 */
public class JcrReferenceObject implements ReferencedObject {

  private static final String ACL_PROPERTY = "jcr:acl";
  private List<AccessControlStatement> acl;
  private List<AccessControlStatement> inheritableAcl;
  private String path;
  private boolean rootReference = false;
  private JcrReferenceObject parentReference;
  private Node node;

  /**
   * @param n
   * @throws RepositoryException
   */
  public JcrReferenceObject(Node node) throws RepositoryException {
    this.node = node;
    path = node.getPath();
    acl = new ArrayList<AccessControlStatement>();
    inheritableAcl = new ArrayList<AccessControlStatement>();
    Property property = node.getProperty(ACL_PROPERTY);
    for (Value aclSpec : property.getValues()) {
      AccessControlStatement acs = new JcrAccessControlStatementImpl(aclSpec.getString());
      if (acs.isPropagating()) {
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
    if (parent != null) {
      parentReference = new JcrReferenceObject(parent);
      if (parentReference.getInheritableAccessControlList().size() == 0) {
        rootReference = true;
      }
    } else {
      rootReference = true;
    }

  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.authz.ReferencedObject#getAccessControlList()
   */
  public Collection<? extends AccessControlStatement> getAccessControlList() {
    return acl;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.authz.ReferencedObject#getInheritableAccessControlList()
   */
  public Collection<? extends AccessControlStatement> getInheritableAccessControlList() {
    return inheritableAcl;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.authz.ReferencedObject#getKey()
   */
  public String getKey() {
    return path;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.authz.ReferencedObject#getParent()
   */
  public ReferencedObject getParent() {
    return parentReference;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.authz.ReferencedObject#isRoot()
   */
  public boolean isRoot() {
    return rootReference;
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.authz.ReferencedObject#addAccessControlStatement(org.sakaiproject.kernel.api.authz.AccessControlStatement)
   */
  public void addAccessControlStatement(AccessControlStatement newAcs)
      throws UpdateFailedException {
    try {
      for (AccessControlStatement acs : acl) {
        if (newAcs.equals(acs)) {
          return;
        }
      }
      String[] values = new String[acl.size()+1];
      int i = 0;
      for ( AccessControlStatement acs : acl ) {
        values[i++] = acs.toString();
      }
      values[i] = newAcs.toString();
      node.setProperty(ACL_PROPERTY, values);
      node.save();
      
      acl.add(newAcs);
      if (newAcs.isPropagating()) {
        inheritableAcl.add(newAcs);
      }
      
    } catch (NumberFormatException e) {
      throw new UpdateFailedException("Unable to update ACL in node " + path
          + " :" + e.getMessage());
    } catch (RepositoryException e) {
      throw new UpdateFailedException("Unable to update ACL in node " + path
          + " :" + e.getMessage());
    }
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.authz.ReferencedObject#removeAccessControlStatement(org.sakaiproject.kernel.api.authz.AccessControlStatement)
   */
  public void removeAccessControlStatement(AccessControlStatement removeAcs)
      throws UpdateFailedException {
    try {
      List<AccessControlStatement> toRemove = new ArrayList<AccessControlStatement>();
      List<String> newValues = new ArrayList<String>();
      for (AccessControlStatement acs : acl) {
        if (removeAcs.equals(acs)) {
          toRemove.add(acs);
        } else {
          newValues.add(acs.toString());
        }
      }
      String[] values = newValues.toArray(new String[0]);
      node.setProperty(ACL_PROPERTY, values);
      node.save();
      
      acl.removeAll(toRemove);
      inheritableAcl.removeAll(toRemove);
    } catch (RepositoryException e) {
      throw new UpdateFailedException("Unable to update ACL in node " + path
          + " :" + e.getMessage());
    }
  }
}