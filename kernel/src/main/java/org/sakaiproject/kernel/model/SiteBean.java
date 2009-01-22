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
package org.sakaiproject.kernel.model;

/**
 * Bean for holding information about a Site.
 */
public class SiteBean extends GroupBean {

  private String id;
  private String type;

  /**
   * Get the ID of this site.
   *
   * @return
   */
  public String getId() {
    return id;
  }

  /**
   * Set the ID for this site.
   *
   * @param id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Get the type of this site.
   *
   * @return
   */
  public String getType() {
    return type;
  }

  /**
   * Set the type for this site.
   *
   * @param type
   */
  public void setType(String type) {
    this.type = type;
  }
}