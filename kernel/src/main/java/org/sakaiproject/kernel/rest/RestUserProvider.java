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

import org.sakaiproject.kernel.api.Registry;
import org.sakaiproject.kernel.api.RegistryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
import org.sakaiproject.kernel.api.rest.RestProvider;
import org.sakaiproject.kernel.api.serialization.BeanConverter;
import org.sakaiproject.kernel.api.user.User;
import org.sakaiproject.kernel.api.user.UserResolverService;
import org.sakaiproject.kernel.user.UserFactoryService;
import org.sakaiproject.kernel.user.jcr.JcrAuthenticationResolverProvider;
import org.sakaiproject.kernel.util.IOUtils;
import org.sakaiproject.kernel.util.StringUtils;
import org.sakaiproject.kernel.util.rest.RestDescription;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 */
public class RestUserProvider implements RestProvider {

  private static final RestDescription DESCRIPTION = new RestDescription();
  private static final String FIRST_NAME_PARAM = "firstName";
  private static final String LAST_NAME_PARAM = "lastName";
  private static final String EMAIL_PARAM = "email";
  private static final String EXTERNAL_USERID_PARAM = "eid";
  private static final String PASSWORD_PARAM = "password";
  private static final String USER_TYPE_PARAM = "userType";
  private BeanConverter beanConverter;
  private UserResolverService userResolverService;
  private JCRNodeFactoryService jcrNodeFactoryService;
  private UserFactoryService userFactoryService;

  /**
   * 
   */
  @Inject
  public RestUserProvider(
      RegistryService registryService,
      @Named(BeanConverter.REPOSITORY_BEANCONVETER) BeanConverter beanConverter,
      UserResolverService userResolverService,
      JCRNodeFactoryService jcrNodeFactoryService,
      UserFactoryService userFactoryService) {
    Registry<String, RestProvider> restRegistry = registryService
        .getRegistry(RestProvider.REST_REGISTRY);
    restRegistry.add(this);
    this.beanConverter = beanConverter;
    this.userResolverService = userResolverService;
    this.jcrNodeFactoryService = jcrNodeFactoryService;
    this.userFactoryService = userFactoryService;

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
    try {
      if ("POST".equals(request.getMethod())) {
        // Security is managed by the JCR
        Map<String, Object> map = null;
        if ("new".equals(elements[1])) {
          map = createUser(request, response);
        }

        if (map != null) {
          String responseBody = beanConverter.convertToString(map);
          response.setContentType(RestProvider.CONTENT_TYPE);
          response.getOutputStream().print(responseBody);
        }

      } else {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
      }
    } catch (SecurityException ex) {
      response.reset();
      response.sendError(HttpServletResponse.SC_FORBIDDEN);
    } catch (Exception ex) {
      throw new ServletException(ex);
    }
  }

  /**
   * @param request
   * @return
   * @throws JCRNodeFactoryServiceException
   * @throws RepositoryException
   * @throws IOException
   * @throws NoSuchAlgorithmException
   */
  private Map<String, Object> createUser(HttpServletRequest request,
      HttpServletResponse response) throws RepositoryException,
      JCRNodeFactoryServiceException, IOException, NoSuchAlgorithmException {
    ByteArrayInputStream bais = null;
    InputStream templateInputStream = null;
    try {
      String firstName = request.getParameter(FIRST_NAME_PARAM);
      String lastName = request.getParameter(LAST_NAME_PARAM);
      String email = request.getParameter(EMAIL_PARAM);
      String externalId = request.getParameter(EXTERNAL_USERID_PARAM);
      String password = request.getParameter(PASSWORD_PARAM);
      String userType = request.getParameter(USER_TYPE_PARAM);

      User u = userResolverService.resolve(externalId);
      if (u != null) {
        response.reset();
        response.sendError(HttpServletResponse.SC_CONFLICT);
        return null;
      }

      u = userFactoryService.createNewUser(externalId);

      String userEnvironmentPath = userFactoryService.getUserEnvPath(u
          .getUuid());

      String userEnvironmentTemplate = userFactoryService
          .getUserEnvTemplate(userType);

      // load the template
      templateInputStream = jcrNodeFactoryService
          .getInputStream(userEnvironmentTemplate);
      String template = IOUtils.readFully(templateInputStream, "UTF-8");
      Map<String, Object> templateMap = beanConverter.convertToObject(template,
          Map.class);

      // make the template this user
      templateMap.put("uuid", u.getUuid());
      templateMap.put("firstName", firstName);
      templateMap.put("lastName", lastName);
      templateMap.put("email", email);
      templateMap.put("userType", userType);

      // save the template
      String userEnv = beanConverter.convertToString(templateMap);
      bais = new ByteArrayInputStream(userEnv.getBytes("UTF-8"));
      Node userEnvNode = jcrNodeFactoryService.setInputStream(
          userEnvironmentPath, bais);

      // set the password
      userEnvNode.setProperty(
          JcrAuthenticationResolverProvider.JCRPASSWORDHASH, StringUtils
              .sha1Hash(password));

      userEnvNode.save();

      Map<String, Object> r = new HashMap<String, Object>();
      r.put("response", "OK");
      r.put("uuid", u.getUuid());
      return r;
    } finally {
      try {
        bais.close();
      } catch (Exception ex) {
      }
      try {
        templateInputStream.close();
      } catch (Exception ex) {
      }
    }

  }

  static {
    DESCRIPTION.setTitle("User Rest Service");
    DESCRIPTION.setShortDescription("The Logout service logs the user out ");
    DESCRIPTION.addSection(1, "Create User",
        "Create a new user by POST ing to the /rest/user/new url with the "
            + " following parameters " + FIRST_NAME_PARAM + ","
            + LAST_NAME_PARAM + "," + EMAIL_PARAM + "," + EXTERNAL_USERID_PARAM
            + "," + PASSWORD_PARAM + "," + USER_TYPE_PARAM
            + " as described below");
    DESCRIPTION
        .addURLTemplate("new",
            "POST to create a new user, firstname, lastname, email, userid and password).");
    DESCRIPTION.addParameter(FIRST_NAME_PARAM, "The first name of the User");
    DESCRIPTION.addParameter(LAST_NAME_PARAM, "The last name of the User");
    DESCRIPTION.addParameter(EMAIL_PARAM, "The email for the user User");
    DESCRIPTION.addParameter(EXTERNAL_USERID_PARAM,
        "The external user ID for the user User");
    DESCRIPTION.addParameter(PASSWORD_PARAM,
        "The initial password for the User");
    DESCRIPTION.addParameter(USER_TYPE_PARAM, "The type of the user User");

    DESCRIPTION
        .addResponse(
            String.valueOf(HttpServletResponse.SC_OK),
            "On New { \"response\" : \"OK\", \"uuid\" : \"AAAA\" }, where AAAA is the new user UUID ");
    DESCRIPTION.addResponse(String.valueOf(HttpServletResponse.SC_FORBIDDEN),
        "If the user does not have permission to create users");
    DESCRIPTION.addResponse(String
        .valueOf(HttpServletResponse.SC_METHOD_NOT_ALLOWED),
        "If the method is used");

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
    return "user";
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
