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
package org.sakaiproject.kernel.rest;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.KernelManager;
import org.sakaiproject.kernel.api.Registry;
import org.sakaiproject.kernel.api.RegistryService;
import org.sakaiproject.kernel.api.authz.SubjectPermissions;
import org.sakaiproject.kernel.api.authz.SubjectStatement;
import org.sakaiproject.kernel.api.authz.UserSubjects;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
import org.sakaiproject.kernel.api.rest.RestProvider;
import org.sakaiproject.kernel.api.serialization.BeanConverter;
import org.sakaiproject.kernel.api.session.Session;
import org.sakaiproject.kernel.api.session.SessionManagerService;
import org.sakaiproject.kernel.api.site.SiteService;
import org.sakaiproject.kernel.api.user.User;
import org.sakaiproject.kernel.api.userenv.UserEnvironment;
import org.sakaiproject.kernel.authz.simple.SimpleJcrUserEnvironmentResolverService;
import org.sakaiproject.kernel.model.GroupMembershipBean;
import org.sakaiproject.kernel.model.SiteBean;
import org.sakaiproject.kernel.model.SiteIndexBean;
import org.sakaiproject.kernel.model.SubjectPermissionBean;
import org.sakaiproject.kernel.util.IOUtils;
import org.sakaiproject.kernel.util.PathUtils;
import org.sakaiproject.kernel.util.rest.RestDescription;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Implements the MySites service.
 * 
 * This is coded according to OpenSocial API v.8.1
 * http://www.opensocial.org/Technical-Resources/opensocial-spec-v081/restful-protocol#TOC-2.1-Responses
 * 
 */
public class RestMySitesProvider implements RestProvider {

  public static String SITES_ELEMENT = "sites";
  private static RestDescription DESCRIPTION = new RestDescription();
  private static final Log LOG = LogFactory.getLog(RestMySitesProvider.class);
  private SessionManagerService sessionManagerService;
  private BeanConverter beanConverter;
  private String userEnvironmentBase;

  public static final String JCR_USERENV_BASE = "jcruserenv.base"; // / add to
  // shared api
  public static final String USERENV = "userenv"; // /add to shared api

  public static final String OUTPUT_PARAM_NAME_STARTINDEX = "startIndex";
  public static final String OUTPUT_PARAM_NAME_ITEMSPERPAGE = "startIndex";

  public static final String INPUT_PARAM_NAME_STARTINDEX = "itemsPerPage";
  public static final String INPUT_PARAM_NAME_COUNT = "count";
  private static final String OUTPUT_PARAM_NAME_TOTALRESULTS = "totalResults";
  private static final String OUTPUT_SITES_KEY = "entry";

  private SimpleJcrUserEnvironmentResolverService simpleJcrUserEnvironmentResolverService;
  private EntityManager entityManager;
  private SiteService siteService;

  @Inject
  public RestMySitesProvider(
      RegistryService registryService,
      SessionManagerService sessionManagerService,
      EntityManager entityManager,
      SimpleJcrUserEnvironmentResolverService simpleJcrUserEnvironmentResolverService,
      @Named(BeanConverter.REPOSITORY_BEANCONVETER) BeanConverter beanConverter,
      @Named(JCR_USERENV_BASE) String userEnvironmentBase,
      SiteService siteService) {
    Registry<String, RestProvider> registry = registryService
        .getRegistry(RestProvider.REST_REGISTRY);
    registry.add(this);
    this.sessionManagerService = sessionManagerService;
    this.beanConverter = beanConverter;
    this.userEnvironmentBase = userEnvironmentBase;
    this.simpleJcrUserEnvironmentResolverService = simpleJcrUserEnvironmentResolverService;
    this.entityManager = entityManager;
    this.siteService = siteService;
  }

  static {
    DESCRIPTION.setTitle("MySites Service");
    DESCRIPTION
        .setShortDescription("The MySites service provides information about "
            + "sites associated with the current user");
    DESCRIPTION
        .addSection(
            1,
            "Introduction",
            "The MySites Service, when queried will respond with a json specific "
                + "to the logged in user. If no logged in user is present, then an "
                + "anonymous json response will be sent. In addition some headers "
                + "will be modified to reflect the locale preferences of the user.");
    DESCRIPTION.addSection(2, "Response: Anon User",
        "Where the user is an anon user the response will contain a list of sites "
            + "that are accessible anonymously.");
    DESCRIPTION.addSection(2, "Response: Authenticated User",
        "Where the user is an authenticaated user the response will contain a list of "
            + "sites associated wit the user including the role(s) in each.");
    DESCRIPTION.addParameter("none", "The service accepts no parameters ");
    DESCRIPTION
        .addHeader("none",
            "The service neither looks for headers nor sets any non standatd headers");
    DESCRIPTION
        .addURLTemplate(
            "sites",
            "The service is selected by /rest/sites. If there is any training path the request will be ignored by this provider");
    DESCRIPTION
        .addResponse(
            "200",
            "The service returns a JSON body with a list of N 'items' structures. eg "
                + " {\"items\":[{\"type\":\"course\",\"title\":\"Test 1\",\"owner\":\"Nicolaas Matthijs (no email)\""
                + ",\"members\":1,\"description\":\"My Workspace Site\",\"creationDate\":\"06-10-2008\""
                + ",\"id\":\"178c5241-3c4d-4fb7-b501-fe47ce831934\"}\""
                + " ,{\"items\":[{\"type\":\"course\",\"title\":\"Test 2\",\"owner\":\"Nicolaas Matthijs (no email)\""
                + ",\"members\":325,\"description\":\"Another test\",\"creationDate\":\"06-10-2008\""
                + ",\"id\":\"21546c21-b501-b74f-3c4d-fe48319347ce\"}}");

  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.rest.RestProvider#dispatch(java.lang.String[],
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  public void dispatch(String[] elements, HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    if (elements.length == 1 && SITES_ELEMENT.equals(elements[0])) {

      KernelManager kernelManager = new KernelManager();
      Kernel kernel = kernelManager.getKernel();

      if (null == sessionManagerService) {
        sessionManagerService = kernel.getService(SessionManagerService.class);
      }

      Session session = sessionManagerService.getCurrentSession();
      User user = session.getUser();

      String uuid = null;

      if (user == null || user.getUuid() == null) {
        uuid = "anon";
      } else {
        uuid = user.getUuid();
      }
      UserEnvironment env = simpleJcrUserEnvironmentResolverService
          .resolve(user);

      LOG.info("getting subjects....");
      String[] subjects = env.getSubjects();
      if (LOG.isInfoEnabled()) {
        LOG.info("list of subjects for user (" + uuid + ", " + subjects.length
            + " total):");
        for (int i = 0; i < subjects.length; ++i) {
          LOG.info("--> " + subjects[i]);
        }
      }

      LOG.info("Parsing for startindex param from request ....." + request);

      /*
       * parse and handle the paging This is coded according to OpenSocial API
       * v.8.1
       * http://www.opensocial.org/Technical-Resources/opensocial-spec-v081
       * /restful -protocol#TOC-2.1-Responses
       */
      Map<String, Object> pagingEnvelope = new HashMap<String, Object>();

      // check for startIndex param
      try {
        String param = request.getParameter(INPUT_PARAM_NAME_STARTINDEX);
        int startIndex = Integer.parseInt(param);
        pagingEnvelope.put(OUTPUT_PARAM_NAME_STARTINDEX, startIndex);
      } catch (NumberFormatException e) {// just skip it
      } catch (Exception e) {
        LOG.error("General Exception thrown parsing request for startIndex");
      }

      /// check for 'count' param
      try {
        // /set to 'count' until we know how many values we have left to display
        int itemsPerPage = Integer.parseInt(request
            .getParameter(INPUT_PARAM_NAME_COUNT));
        pagingEnvelope.put(OUTPUT_PARAM_NAME_ITEMSPERPAGE, itemsPerPage);
      } catch (NumberFormatException e) {
        // just skip it
      } catch (Exception e) {
        LOG.error("General Exception thrown parsing request for itemsPerPage");
      }

      if (LOG.isDebugEnabled()) {
        LOG.debug("getting subjects as sites....");
      }

      String id = null;
      SiteBean memSite = null;
      Set<SiteBean> sites = new HashSet<SiteBean>();

      for (String s : subjects) {
        if (s != null) {
          String[] parts = s.split(":");
          if (parts.length == 2) {
            memSite = siteService.getSite(parts[0]);

            if (null == memSite) {
              LOG
                  .warn("group id not found as a site id... subject token: "
                      + s);
            } else {
              if (LOG.isDebugEnabled()) {
                LOG.debug("Site found: " + memSite.getName());
              }
              if (!sites.contains(memSite)) {
                sites.add(memSite);
              }
            }
          } else {
            LOG.error("malformed subject in userenvronment (user: " + uuid
                + ")");
          }
        } else {
          LOG.error("null subject found in userenvironment (user: " + uuid
              + ")");
        }
      }
      pagingEnvelope.put(OUTPUT_PARAM_NAME_TOTALRESULTS, sites.size());
      pagingEnvelope.put(OUTPUT_SITES_KEY, sites.toArray());
      sendOutput(response, pagingEnvelope);
    }

  }

  /**
   * @param response
   * @param itemMap
   * @throws IOException
   */
  private void sendOutput(HttpServletResponse response, Map itemMap)
      throws IOException {
    response.setContentType(RestProvider.CONTENT_TYPE);
    ServletOutputStream outputStream = response.getOutputStream();

    outputStream.print(beanConverter.convertToString(itemMap));

  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.rest.RestProvider#getDescription()
   */
  public RestDescription getDescription() {
    return DESCRIPTION;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.Provider#getKey()
   */
  public String getKey() {
    return "mysites";
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.Provider#getPriority()
   */
  public int getPriority() {
    return 0;
  }

}