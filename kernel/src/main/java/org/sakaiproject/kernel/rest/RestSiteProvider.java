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
import com.google.inject.name.Named;

import org.sakaiproject.kernel.api.Registry;
import org.sakaiproject.kernel.api.RegistryService;
import org.sakaiproject.kernel.api.authz.SubjectPermissionService;
import org.sakaiproject.kernel.api.rest.RestProvider;
import org.sakaiproject.kernel.api.serialization.BeanConverter;
import org.sakaiproject.kernel.api.session.Session;
import org.sakaiproject.kernel.api.session.SessionManagerService;
import org.sakaiproject.kernel.api.site.SiteService;
import org.sakaiproject.kernel.api.user.User;
import org.sakaiproject.kernel.api.userenv.UserEnvironmentResolverService;
import org.sakaiproject.kernel.model.RoleBean;
import org.sakaiproject.kernel.model.SiteBean;
import org.sakaiproject.kernel.model.UserEnvironmentBean;
import org.sakaiproject.kernel.user.AnonUser;
import org.sakaiproject.kernel.util.StringUtils;
import org.sakaiproject.kernel.util.rest.RestDescription;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
  private static final String GET = "get";
  private static final String ADD_OWNER = "addOwner";
  private static final String REMOVE_OWNER = "removeOwner";
  private static final String[] PERM_FULL = { "read", "write", "delete" };

  private final SiteService siteService;
  private BeanConverter beanConverter;
  private UserEnvironmentResolverService userEnvironmentResolverService;
  private SessionManagerService sessionManagerService;
  private SubjectPermissionService subjectPermissionService;
  private RegistryService registryService;

  static {
    DESC = new RestDescription();
    DESC.setTitle("Site");
    DESC
        .setShortDescription("Creates a site and adds the current user as the owner.");
    DESC.addSection(1, "Introduction", "");
    DESC
        .addSection(
            2,
            "Check ID",
            "Checks to see if the site ID exists, if it does a "
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
                + "," + Params.TYPE + " the Site ID must not exist");
    DESC.addURLTemplate("/rest/" + KEY + "/" + CREATE,
        "Accepts POST to create a site, see the section on Create for details");
    DESC.addURLTemplate("/rest/" + KEY + "/" + GET + "/<siteId>",
        "Accepts GET to check if a site exists, see the secion on Check ID");
    DESC.addURLTemplate("/rest/" + KEY + "/" + ADD_OWNER + "/<siteId>/<userID>",
    "Accepts POST to add the specified user id as an owner to the site id, " +
    "the current user must be a owner of the site.");
    DESC.addURLTemplate("/rest/" + KEY + "/" + REMOVE_OWNER + "/<siteId>/<userID>",
        "Accepts POST to remove the specified user id as an owner to the site id, " +
        "the current user must be a owner of the site, and there must be at least 1 owner after the specified user" +
        "is removed from the list of owners.");
    DESC.addSection(4, "GET", "");
    DESC.addParameter(Params.ID, "The Site ID");
    DESC.addParameter(Params.NAME, "The Site Name");
    DESC.addParameter(Params.DESCRIPTION, "The Site Description");
    DESC.addParameter(Params.TYPE, "The Site Type");
    DESC.addResponse(String.valueOf(HttpServletResponse.SC_OK),
        "If the action completed Ok, or if the site exits");
    DESC.addResponse(String.valueOf(HttpServletResponse.SC_CONFLICT),
        "If a site exists when trying to create a site");
    DESC.addResponse(String.valueOf(HttpServletResponse.SC_FORBIDDEN),
        "If permission to create the site is denied");
    DESC.addResponse(String
        .valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR),
        " Any other error");
  }

  @Inject
  public RestSiteProvider(RegistryService registryService,
      SiteService siteService,
      @Named(BeanConverter.REPOSITORY_BEANCONVETER) BeanConverter beanConverter,
      UserEnvironmentResolverService userEnvironmentResolverService,
      SessionManagerService sessionManagerService,
      SubjectPermissionService subjectPermissionService) {
    this.siteService = siteService;
    this.registryService = registryService;
    Registry<String, RestProvider> restRegistry = registryService
        .getRegistry(RestProvider.REST_REGISTRY);
    restRegistry.add(this);
    this.beanConverter = beanConverter;
    this.userEnvironmentResolverService = userEnvironmentResolverService;
    this.sessionManagerService = sessionManagerService;
    this.subjectPermissionService = subjectPermissionService;
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
    try {
      if (elements.length >= 1) {
        Map<String, Object> map = null;
        if (CREATE.equals(elements[1]) && "POST".equals(req.getMethod())) {
          map = doCreate(req, resp);
        } else if (GET.equals(elements[1])) {
          map = doGet(req, resp, elements.length > 2 ? elements[2] : null);
        } else if (ADD_OWNER.equals(elements[1])) {
          map = doAddOwner(req, resp, elements.length > 2 ? elements[2] : null,elements.length > 3 ? elements[3] : null);
        } else if (REMOVE_OWNER.equals(elements[1])) {
          map = doRemoveOwner(req, resp, elements.length > 2 ? elements[2] : null,elements.length > 3 ? elements[3] : null);
        } else {
          resp.reset();
          resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
        if (map != null) {
          String responseBody = beanConverter.convertToString(map);
          resp.setContentType(RestProvider.CONTENT_TYPE);
          resp.getOutputStream().print(responseBody);
        }
      }
    } catch (SecurityException ex) {
      resp.reset();
      resp.sendError(HttpServletResponse.SC_FORBIDDEN);
    } catch (Exception ex) {
      throw new ServletException(ex.getMessage(),ex);
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

  private Map<String, Object> doGet(HttpServletRequest req,
      HttpServletResponse resp, String siteId) throws IOException {
    if (siteId == null || siteId.trim().length() == 0) {
      resp.reset();
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
          " No Site ID specified ");
      return null;
    }
    SiteBean siteBean = siteService.getSite(siteId);
    if (siteBean != null) {
      resp.getOutputStream().print(beanConverter.convertToString(siteBean));
    } else {
      resp.reset();
      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
    return null;
  }

  private Map<String, Object> doCreate(HttpServletRequest req,
      HttpServletResponse resp) throws IOException {
    // grab the site id and build the site node path.
    String id = req.getParameter(Params.ID);

    // check for an existing site
    if (siteService.siteExists(id)) {
      resp.reset();
      resp.sendError(HttpServletResponse.SC_CONFLICT);
      return null;
    } else {
      
      Session session = sessionManagerService.getCurrentSession();
      if ( session == null )  {
        resp.reset();
        resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        return null;
      }
      User user = session.getUser();
      if ( user == null || user instanceof AnonUser )  {
        resp.reset();
        resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        return null;
      }
      UserEnvironmentBean userEnv = (UserEnvironmentBean) userEnvironmentResolverService.resolve(session);
      UserEnvironmentBean newUserEnvironment = new UserEnvironmentBean(subjectPermissionService,0,registryService);
      newUserEnvironment.copyFrom(userEnv);
      
      String newSubject = id+":owner";
      String[] subjects = newUserEnvironment.getSubjects();
      subjects = StringUtils.addString(subjects, newSubject);
      newUserEnvironment.setSubjects(subjects);
      
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
      site.addOwner(userEnv.getUser().getUuid());
      
      // add the admin role
      RoleBean roles[] = new RoleBean[1];
      roles[0] = new RoleBean();
      roles[0].setName("admin");
      roles[0].setPermissions(PERM_FULL);

      site.setRoles(roles);

      siteService.createSite(site);
      userEnvironmentResolverService.save(newUserEnvironment);
      

      Map<String , Object> responseMap = new HashMap<String, Object>();
      responseMap.put("response","OK");
      return responseMap;
    }
  }
  
  /**
   * @param req
   * @param resp
   * @param string
   * @param string2
   * @return
   * @throws IOException 
   */
  private Map<String, Object> doRemoveOwner(HttpServletRequest request,
      HttpServletResponse response, String siteId, String userId) throws IOException {
    Session session = sessionManagerService.getCurrentSession();
    if ( session == null )  {
      response.reset();
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return null;
    }
    User user = session.getUser();
    if ( user == null || user instanceof AnonUser )  {
      response.reset();
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return null;
    }
    if ( siteId == null || siteId.trim().length() == 0 ) {
      response.reset();
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Site ID Must be specified");
      return null;
    }
    if ( userId == null || userId.trim().length() == 0 ) {
      response.reset();
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "User ID Must be specified");
      return null;
    }
    
    SiteBean siteBean = siteService.getSite(siteId);
    if ( siteBean == null ) {
      response.reset();
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "Site "+siteId+" not found");
      return null;
    }
    
    String[] owners = siteBean.getOwners();
    String userUuid = user.getUuid();
    boolean isOwner = false;
    int i = 0;
    for ( String owner : owners ) {
      if ( userUuid.equals(owner) ) {
        isOwner = true;
      }
      if ( !userId.equals(owner) ) {
        i++;
      }
    }
    if ( !isOwner ) {
      throw new SecurityException("Not an owner of this site "+siteId);
    }
    if ( i < 1 ) {
      throw new SecurityException("A site must have at least one owner ");
    }
    if ( i == owners.length ) {
      response.reset();
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "User "+userId+" is not an owner of "+siteId);
      return null;
    }
    owners = StringUtils.removeString(owners, userId);
    siteBean.setOwners(owners);
    
    siteService.saveSite(siteBean);
    
    
    Map<String , Object> responseMap = new HashMap<String, Object>();
    responseMap.put("response","OK");
    return responseMap;
  }

  /**
   * @param req
   * @param resp
   * @param string
   * @param string2
   * @return
   * @throws IOException 
   */
  private Map<String, Object> doAddOwner(HttpServletRequest request,
      HttpServletResponse response, String siteId, String userId) throws IOException {
    Session session = sessionManagerService.getCurrentSession();
    if ( session == null )  {
      response.reset();
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return null;
    }
    User user = session.getUser();
    if ( user == null || user instanceof AnonUser )  {
      response.reset();
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return null;
    }
    if ( siteId == null || siteId.trim().length() == 0 ) {
      response.reset();
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Site ID Must be specified");
      return null;
    }
    if ( userId == null || userId.trim().length() == 0 ) {
      response.reset();
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "User ID Must be specified");
      return null;
    }
    
    SiteBean siteBean = siteService.getSite(siteId);
    if ( siteBean == null ) {
      response.reset();
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "Site "+siteId+" not found");
      return null;
    }
    
    String[] owners = siteBean.getOwners();
    String userUuid = user.getUuid();
    boolean isOwner = false;
    int i = 0;
    for ( String owner : owners ) {
      if ( userUuid.equals(owner) ) {
        isOwner = true;
      }
      if (!userId.equals(owner) ) {
        i++;
      }
    }
    if ( !isOwner ) {
      throw new SecurityException("Not an owner of this site "+siteId);
    }
    if ( i < 1 ) {
      throw new SecurityException("A site must have at least one owner ");
    }
    if ( i != owners.length ) {
      response.reset();
      response.sendError(HttpServletResponse.SC_CONFLICT, "User "+userId+" is already an owner of "+siteId);
      return null;
    }
    owners = StringUtils.addString(owners, userId);
    siteBean.setOwners(owners);
    
    // need to add the site to the userenv object for the user in question
    
    siteService.saveSite(siteBean);
    
    
    Map<String , Object> responseMap = new HashMap<String, Object>();
    responseMap.put("response","OK");
    return responseMap;
  }

  
}
