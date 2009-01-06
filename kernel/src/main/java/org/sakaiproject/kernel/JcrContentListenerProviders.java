/*******************************************************************************
 * Copyright 2008 Sakai Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.sakaiproject.kernel;

import com.google.inject.Inject;
import com.google.inject.Provider;

import org.sakaiproject.kernel.authz.simple.SubjectPermissionListener;
import org.sakaiproject.kernel.authz.simple.UserEnvironmentListener;
import org.sakaiproject.kernel.jcr.api.JcrContentListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class JcrContentListenerProviders implements
    Provider<List<JcrContentListener>> {

  private List<JcrContentListener> list = new ArrayList<JcrContentListener>();

  /**
   * 
   */
  @Inject
  public JcrContentListenerProviders(
      UserEnvironmentListener userEnvironmentListener, SubjectPermissionListener subjectPermissionListener) {
    list.add(userEnvironmentListener);
    list.add(subjectPermissionListener);
  }

  /**
   * {@inheritDoc}
   * 
   * @see com.google.inject.Provider#get()
   */
  public List<JcrContentListener> get() {
    return list;
  }

}