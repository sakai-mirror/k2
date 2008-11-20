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

import java.util.Collection;

/**
 * A reference object contains the metadata associated with authz for an object.
 */
public interface ReferencedObject {

  /**
   * @return the full access control list for the object.
   */
  Collection<? extends AccessControlStatement> getAccessControlList();

  /**
   * @return the parent object, null if there is no reasonable parent.
   */
  ReferencedObject getParent();

  /**
   * @return does this object have no possible parent objects from and authz
   *         perspective. True if there are no parents.
   */
  boolean isRoot();

  /**
   * @return a collection of Access controls that may be inherited.
   */
  Collection<? extends AccessControlStatement> getInheritableAccessControlList();

  /**
   * @return a unique key for referencing object in caches and maps.
   */
  String getKey();

}
