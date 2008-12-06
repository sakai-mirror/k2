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
import org.sakaiproject.kernel.api.authz.ReferenceResolverService;
import org.sakaiproject.kernel.api.authz.ReferencedObject;
import org.sakaiproject.kernel.util.PathUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An Empty reference object is an object that does not exist or is not visible
 * to the current user. This ReferenceObject has no acl and is root.
 */
public class EmptyReferenceObject implements ReferencedObject {

  private List<AccessControlStatement> acl;
  private Exception cause;
  private String resourceReference;
  private ReferenceResolverService resolverService;
  private String parentResourceReference;
  private boolean rootReference;

  /**
   * @param resourceReference
   * @param e
   */
  public EmptyReferenceObject(String resourceReference, Exception e,
      ReferenceResolverService resolverService) {
    acl = new ArrayList<AccessControlStatement>();
    cause = e;
    this.resourceReference = resourceReference;
    this.resolverService = resolverService;
    rootReference = resourceReference == null
        || resourceReference.trim().length() == 0
        || resourceReference.trim().equals("/");
    if (!rootReference) {
      parentResourceReference = PathUtils.getParentReference(resourceReference);
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
    return acl;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.authz.ReferencedObject#getKey()
   */
  public String getKey() {
    return resourceReference;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.authz.ReferencedObject#getParent()
   */
  public ReferencedObject getParent() {
    return resolverService.resolve(parentResourceReference);
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
   * 
   * @see org.sakaiproject.kernel.api.authz.ReferencedObject#addAccessControlStatement(org.sakaiproject.kernel.api.authz.AccessControlStatement)
   */
  public void addAccessControlStatement(AccessControlStatement newAcs)
      throws UpdateFailedException {
    throw new UpdateFailedException(
        "Reference Object is an empty object and as such has no acl");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.authz.ReferencedObject#removeAccessControlStatement(org.sakaiproject.kernel.api.authz.AccessControlStatement)
   */
  public void removeAccessControlStatement(AccessControlStatement removeAcs)
      throws UpdateFailedException {
    throw new UpdateFailedException(
        "Reference Object is an empty object and as such has no acl");
  }

  /**
   * @return the cause
   */
  public Exception getCause() {
    return cause;
  }

}
