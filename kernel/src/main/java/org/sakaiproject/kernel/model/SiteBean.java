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

package org.sakaiproject.kernel.model;

import edu.emory.mathcs.backport.java.util.Arrays;

import org.sakaiproject.kernel.api.site.SiteException;
import org.sakaiproject.kernel.api.site.SiteService;
import org.sakaiproject.kernel.site.SiteServiceImpl;

/**
 * Bean for holding information about a Site.
 */
public class SiteBean extends GroupBean {

  private String id;
  private String type;
  private transient String sitePath;
  private transient SiteService siteService;

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
  
  /**
   * {@inheritDoc}
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getName()+":"+getId()+":"+getDescription()+":"+getType()+":"+Arrays.toString(getSubjectTokens());
  }

  /**
   * @param sitePath
   */
  public String location(String sitePath) {
    this.sitePath = sitePath;
    return sitePath;    
  }

  /**
   * @param siteServiceImpl
   */
  public void service(SiteService siteService) {
    this.siteService = siteService;
  }

  /**
   * @return
   */
  public String location() {
    return sitePath;
  }
  
  public void save() throws SiteException {
   siteService.save(this);
  }
}
