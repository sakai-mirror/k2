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
package org.sakaiproject.kernel.rest.site;

import com.google.inject.Inject;

import org.sakaiproject.kernel.api.Registry;
import org.sakaiproject.kernel.api.RegistryService;
import org.sakaiproject.kernel.api.rest.Documentable;
import org.sakaiproject.kernel.api.rest.JaxRsSingletonProvider;
import org.sakaiproject.kernel.api.session.Session;
import org.sakaiproject.kernel.api.session.SessionManagerService;
import org.sakaiproject.kernel.api.site.SiteException;
import org.sakaiproject.kernel.api.site.SiteService;
import org.sakaiproject.kernel.api.user.User;
import org.sakaiproject.kernel.api.userenv.UserEnvironmentResolverService;
import org.sakaiproject.kernel.model.SiteBean;
import org.sakaiproject.kernel.rest.RestSiteProvider.Params;
import org.sakaiproject.kernel.util.rest.RestDescription;
import org.sakaiproject.kernel.util.user.AnonUser;
import org.sakaiproject.kernel.webapp.Initialisable;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

/**
 * 
 */
@Path("/site")
public class SiteProvider implements Documentable, JaxRsSingletonProvider, Initialisable {

  private static final String OK = "{\"response\", \"OK\"}";
  private SessionManagerService sessionManagerService;
  private Registry<String, JaxRsSingletonProvider> jaxRsSingletonRegistry;
  private SiteService siteService;
  private UserEnvironmentResolverService userEnvironmentResolverService;

  private static final RestDescription DESC = new RestDescription();
  private static final String SITE_PATH_PARAM = "sitePath";
  private static final String SITE_TYPE_PARAM = "siteType";
  private static final String OWNER_PARAM = "owner";
  private static final String USER_PARAM = "uuserid";
  private static final String MEMBERSHIP_PARAM = "membertoken";
  private static final String NAME_PARAM = "name";
  private static final String DESCRIPTION_PARAM = "description";
  static {
    DESC.setTitle("Site Service");
    DESC.setShortDescription("The rest service to support site management");
    DESC.setTitle("Site");
    DESC.setShortDescription("Creates a site and adds the current user as the owner.");
    DESC.addSection(1, "Introduction", "");
    DESC.addSection(2, "Check ID", "Checks to see if the site ID exists, if it does a "
        + HttpServletResponse.SC_OK
        + " is returned and the site object as json, if it does not exist a "
        + HttpServletResponse.SC_NOT_FOUND + " is returned ");
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
                + ","
                + Params.TYPE + "," + Params.OWNER + " the Site ID must not exist");
    DESC.addURLTemplate("/_rest/site/create",
        "Accepts POST to create a site, see the section on Create for details");
    DESC.addURLTemplate("/_rest/site/get/<siteId>",
        "Accepts GET to check if a site exists, see the secion on Check ID");
    DESC
        .addURLTemplate(
            "/_rest/site/owner/add/<siteId>",
            "Accepts POST to add the specified user id (in the owner paramerer) as an owner to the site id, "
                + "the current user must be a owner of the site.");
    DESC
        .addURLTemplate(
            "/_rest/site/owner/remove/<siteId>",
            "Accepts POST to remove the specified user id (in the owner parameter) as an owner to the site id, "
                + "the current user must be a owner of the site, and there must be at least 1 owner after the specified user"
                + "is removed from the list of owners.");
    DESC.addParameter(Params.ID, "The Site ID");
    DESC.addParameter(Params.NAME, "The Site Name");
    DESC.addParameter(Params.DESCRIPTION, "The Site Description");
    DESC.addParameter(Params.TYPE, "The Site Type");
    DESC.addParameter(Params.OWNER, "The Site Owner, only available to super users");
    DESC.addResponse(String.valueOf(HttpServletResponse.SC_OK),
        "If the action completed Ok, or if the site exits");
    DESC.addResponse(String.valueOf(HttpServletResponse.SC_CONFLICT),
        "If a site exists when trying to create a site");
    DESC.addResponse(String.valueOf(HttpServletResponse.SC_FORBIDDEN),
        "If permission to create the site is denied");
    DESC.addResponse(String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR),
        " Any other error");

  }

  /**
   * 
   */
  @Inject
  public SiteProvider(RegistryService registryService, SiteService siteService,
      UserEnvironmentResolverService userEnvironmentResolverService) {
    jaxRsSingletonRegistry = registryService
        .getRegistry(JaxRsSingletonProvider.JAXRS_SINGLETON_REGISTRY);

    jaxRsSingletonRegistry.add(this);
    this.siteService = siteService;
    this.userEnvironmentResolverService = userEnvironmentResolverService;

  }

  /**
   * @param path
   * @param type
   * @return
   */
  @POST
  @Path("/create/{" + SITE_PATH_PARAM + "}")
  @Produces(MediaType.TEXT_PLAIN)
  public String createSite(@PathParam(SITE_PATH_PARAM) String path,
      @FormParam(SITE_TYPE_PARAM) String type, @FormParam(NAME_PARAM) String name,
      @FormParam(DESCRIPTION_PARAM) String description) {
    try {
      User u = getAuthenticatedUser();
      SiteBean siteBean = siteService.createSite(path, type);
      siteBean.addOwner(u.getUuid());
      siteBean.setDescription(description);
      siteBean.setName(name);
      userEnvironmentResolverService
          .addMembership(u.getUuid(), siteBean.getId(), "owner");
      siteBean.save();
    } catch (SiteException e) {
      throw new WebApplicationException(e, Status.CONFLICT);
    }
    return OK;
  }

  /**
   * @param path
   * @return
   */
  @GET
  @Path("/get/{" + SITE_PATH_PARAM + "}")
  @Produces(MediaType.TEXT_PLAIN)
  public String getSite(@PathParam(SITE_PATH_PARAM) String path) {
    if (siteService.siteExists(path)) {
      throw new WebApplicationException(Status.OK);
    }
    throw new WebApplicationException(Status.NOT_FOUND);
  }

  /**
   * @param path
   * @param ownerId
   * @return
   */
  @POST
  @Path("/owner/add/{" + SITE_PATH_PARAM + "}")
  @Produces(MediaType.TEXT_PLAIN)
  public String addOwner(@PathParam(SITE_PATH_PARAM) String path,
      @FormParam(OWNER_PARAM) String ownerId) {
    if (siteService.siteExists(path)) {
      try {
        SiteBean siteBean = siteService.getSite(path);
        checkIsOwner(siteBean);
        siteBean.addOwner(ownerId);
        userEnvironmentResolverService.addMembership(ownerId, siteBean.getId(), "owner");
        siteBean.save();
        return OK;
      } catch (SiteException e) {
        throw new WebApplicationException(e, Status.CONFLICT);
      }
    }
    throw new WebApplicationException(Status.NOT_FOUND);
  }

  /**
   * @param path
   * @param ownerId
   * @return
   */
  @POST
  @Path("/owner/remove/{" + SITE_PATH_PARAM + "}")
  @Produces(MediaType.TEXT_PLAIN)
  public String removeOwner(@PathParam(SITE_PATH_PARAM) String path,
      @FormParam(OWNER_PARAM) String ownerId) {
    if (siteService.siteExists(path)) {
      try {
        SiteBean siteBean = siteService.getSite(path);
        checkIsOwner(siteBean);
        if (siteBean.getOwners().length == 1) {
          throw new WebApplicationException(new RuntimeException(
              "Cant remove the last owner, transfer ownership first"), Status.CONFLICT);
        }
        siteBean.removeOwner(ownerId);
        userEnvironmentResolverService.removeMembership(ownerId, siteBean.getId(),
            "owner");
        siteBean.save();
        return OK;
      } catch (SiteException e) {
        throw new WebApplicationException(e, Status.CONFLICT);
      }
    }
    throw new WebApplicationException(Status.NOT_FOUND);
  }

  @POST
  @Path("/members/add/{" + SITE_PATH_PARAM + "}")
  @Produces(MediaType.TEXT_PLAIN)
  public String addMember(@PathParam(SITE_PATH_PARAM) String path,
      @FormParam(USER_PARAM) String[] userIds,
      @FormParam(MEMBERSHIP_PARAM) String[] membershipType) {
    if (siteService.siteExists(path)) {
      if (userIds == null || membershipType == null) {
        throw new WebApplicationException(new RuntimeException("Bad Parameters"),
            Status.BAD_REQUEST);
      }
      if (userIds.length != membershipType.length) {
        throw new WebApplicationException(new RuntimeException(
            "UserIDs and Membership Token arrays must be the same length"),
            Status.BAD_REQUEST);
      }
      SiteBean siteBean = siteService.getSite(path);
      for (int i = 0; i < userIds.length; i++) {
        userEnvironmentResolverService.addMembership(userIds[i], siteBean.getId(),
            membershipType[i]);
      }

    }
    throw new WebApplicationException(Status.NOT_FOUND);
  }

  @POST
  @Path("/members/remove/{" + SITE_PATH_PARAM + "}")
  @Produces(MediaType.TEXT_PLAIN)
  public String removeMember(@PathParam(SITE_PATH_PARAM) String path,
      @FormParam(USER_PARAM) String[] userIds,
      @FormParam(MEMBERSHIP_PARAM) String[] membershipType) {
    if (siteService.siteExists(path)) {
      if (userIds == null || membershipType == null) {
        throw new WebApplicationException(new RuntimeException("Bad Parameters"),
            Status.BAD_REQUEST);
      }
      if (userIds.length != membershipType.length) {
        throw new WebApplicationException(new RuntimeException(
            "UserIDs and Membership Token arrays must be the same length"),
            Status.BAD_REQUEST);
      }
      SiteBean siteBean = siteService.getSite(path);
      for (int i = 0; i < userIds.length; i++) {
        userEnvironmentResolverService.removeMembership(userIds[i], siteBean.getId(),
            membershipType[i]);
      }

    }
    throw new WebApplicationException(Status.NOT_FOUND);
  }

  /**
   * @param siteBean
   */
  private void checkIsOwner(SiteBean siteBean) {
    User u = getAuthenticatedUser();
    String userId = u.getUuid();
    boolean isOwner = false;
    for (String owner : siteBean.getOwners()) {
      if (userId.equals(owner)) {
        isOwner = true;
      }
    }
    if (isOwner) {
      throw new WebApplicationException(new RuntimeException("Not a site owner"),
          Status.FORBIDDEN);
    }

  }

  /**
   * @param request
   * @param response
   * @return
   * @throws IOException
   */
  private User getAuthenticatedUser() {
    Session session = sessionManagerService.getCurrentSession();
    System.err.println("Session is " + session);
    if (session == null) {
      throw new WebApplicationException(Status.UNAUTHORIZED);
    }
    User user = session.getUser();
    if (user == null || user instanceof AnonUser || user.getUuid() == null) {
      throw new WebApplicationException(Status.UNAUTHORIZED);
    }
    return user;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.rest.Documentable#getRestDocumentation()
   */
  public RestDescription getRestDocumentation() {
    return DESC;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.rest.JaxRsSingletonProvider#getJaxRsSingleton()
   */
  public Documentable getJaxRsSingleton() {
    return this;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.Provider#getKey()
   */
  public String getKey() {
    return "presence";
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.Provider#getPriority()
   */
  public int getPriority() {
    return 0;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.webapp.Initialisable#destroy()
   */
  public void destroy() {
    jaxRsSingletonRegistry.remove(this);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.webapp.Initialisable#init()
   */
  public void init() {
  }
}
