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
package org.sakaiproject.kernel.api.site;

import org.sakaiproject.kernel.model.SiteBean;

/**
 * Provides functionality for working Sites.
 */
public interface SiteService {
  /**
   * Path where site information is stored in the user environment.
   */
  String PATH_MYSITES = "MySites/";

  /**
   * Name of the group definition file.
   */
  String FILE_GROUPDEF = "groupdef.json";

  /**
   * Validates if a given id is unique.
   *
   * @param id
   * @return
   */
  boolean siteExists(String id);

  /**
   * Retrieves a requested site by id.
   *
   * @param id
   * @return
   */
  SiteBean getSite(String id);

  /**
   * Creates a site using the supplied bean.
   *
   * @param site
   */
  void createSite(SiteBean site);
}
