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
package org.sakaiproject.kernel.rest;

import com.google.inject.Inject;

import org.sakaiproject.kernel.api.Registry;
import org.sakaiproject.kernel.api.RegistryService;
import org.sakaiproject.kernel.api.rest.RestProvider;
import org.sakaiproject.kernel.api.site.SiteService;
import org.sakaiproject.kernel.model.RoleBean;
import org.sakaiproject.kernel.model.SiteBean;
import org.sakaiproject.kernel.util.rest.RestDescription;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Rest provider for site functions.
 */
public class RestSiteProvider implements RestProvider {
  public static interface Params {
    String ID = "id";
    String NAME = "name";
    String DESCRIPTION = "description";
    String TYPE = "type";
  }

  private static final String KEY = "site";
  private static final RestDescription DESC;

  private static final String CREATE = "create";
  private static final String CHECK_ID = "checkId";
  private static final String[] PERM_FULL = { "read", "write", "delete" };

  private final SiteService siteService;

  static {
    DESC = new RestDescription();
    DESC.setTitle("Site");
    DESC
        .setShortDescription("Creates a site and adds the current user as the owner.");
    DESC.addSection(1, "Introduction", "");
    DESC.addSection(2, "Check ID",
        "Checks to see if the site ID exists, if it does a "
            + HttpServletResponse.SC_CONFLICT
            + " is returned if it does not exist a "
            + HttpServletResponse.SC_OK + " is returned ");
    DESC
        .addSection(
            3,
            "Create",
            "Create a Site, returns {response: 'OK'} if the creation worked with a response code of 200. "
                + "If permission is dentied a 403 will be returned, if there was any other sort of failure a 500 will be returned. The call expects the following parameters"
                + Params.ID
                + ","
                + Params.NAME
                + ","
                + Params.DESCRIPTION
                + "," + Params.TYPE + " the Site ID must not exist");
    DESC.addURLTemplate("/rest/"+KEY+"/"+CREATE, "Accepts POST to create a site, see the section on Create for details");
    DESC.addURLTemplate("/rest/"+KEY+"/"+CHECK_ID, "Accepts GET to check if a site exists, see the secion on Check ID");
    DESC.addSection(4, "GET", "");
    DESC.addParameter(Params.ID, "The Site ID");
    DESC.addParameter(Params.NAME, "The Site Name");
    DESC.addParameter(Params.DESCRIPTION, "The Site Description");
    DESC.addParameter(Params.TYPE, "The Site Type");
    DESC
        .addResponse(
            String.valueOf(HttpServletResponse.SC_OK),
            "If the action completed Ok, or if the site does not exist on Check ID (ieb: that sounds wrong!)");
    DESC.addResponse(String.valueOf(HttpServletResponse.SC_CONFLICT),
        "If a site exists on Check ID");
    DESC.addResponse(String.valueOf(HttpServletResponse.SC_FORBIDDEN),
        "If permission to create the site is denied");
    DESC.addResponse(String
        .valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR),
        " Any other error");
  }

  @Inject
  public RestSiteProvider(RegistryService registryService,
      SiteService siteService) {
    this.siteService = siteService;
    Registry<String, RestProvider> restRegistry = registryService
        .getRegistry(RestProvider.REST_REGISTRY);
    restRegistry.add(this);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.rest.RestProvider#dispatch(java.lang.String[],
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  public void dispatch(String[] elements, HttpServletRequest req,
      HttpServletResponse resp) throws ServletException, IOException {
    if (elements.length >= 1) {
      if (CREATE.equals(elements[1])) {
        doCreate(req, resp);
      } else if (CHECK_ID.equals(elements[1])) {
        doCheckId(req, resp);
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.rest.RestProvider#getDescription()
   */
  public RestDescription getDescription() {
    return DESC;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.Provider#getKey()
   */
  public String getKey() {
    return KEY;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.Provider#getPriority()
   */
  public int getPriority() {
    return 0;
  }

  private void doCheckId(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    String id = req.getParameter(Params.ID);
    if (siteService.siteExists(id)) {
      resp.setStatus(HttpServletResponse.SC_CONFLICT);
      resp.getOutputStream()
          .print("{\"response\": \"Site ID [" + id + "] exists.\"}");
    } else {
      resp.setStatus(HttpServletResponse.SC_OK);
      resp.getOutputStream().print(
          "{\"response\": \"Site ID [" + id + "] is unique.\"}");
    }
  }

  private void doCreate(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    // grab the site id and build the site node path.
    String id = req.getParameter(Params.ID);

    // check for an existing site
    if (siteService.siteExists(id)) {
      resp.setStatus(HttpServletResponse.SC_CONFLICT);
      resp.getOutputStream()
          .print("{\"response\": \"Site ID [" + id + "] exists.\"}");
    } else {
      // get the rest of the site info
      String name = req.getParameter(Params.NAME);
      String description = req.getParameter(Params.DESCRIPTION);
      String type = req.getParameter(Params.TYPE);

      // create the site
      SiteBean site = new SiteBean();
      site.setId(id);
      site.setName(name);
      site.setDescription(description);
      site.setType(type);

      // add the admin role
      RoleBean roles[] = new RoleBean[1];
      roles[0] = new RoleBean();
      roles[0].setName("admin");
      roles[0].setPermissions(PERM_FULL);

      site.setRoles(roles);

      siteService.createSite(site);

      // if all goes well
      resp.setStatus(HttpServletResponse.SC_OK);
      resp.getOutputStream().print("{\"response\": \"OK\"}");
    }
  }
}
