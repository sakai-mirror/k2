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

import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.Registry;
import org.sakaiproject.kernel.api.RegistryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
import org.sakaiproject.kernel.api.rest.RestProvider;
import org.sakaiproject.kernel.api.serialization.BeanConverter;
import org.sakaiproject.kernel.api.session.Session;
import org.sakaiproject.kernel.api.session.SessionManagerService;
import org.sakaiproject.kernel.api.user.User;
import org.sakaiproject.kernel.api.user.UserResolverService;
import org.sakaiproject.kernel.api.userenv.UserEnvironment;
import org.sakaiproject.kernel.api.userenv.UserEnvironmentResolverService;
import org.sakaiproject.kernel.component.core.KernelBootstrapModule;
import org.sakaiproject.kernel.user.UserFactoryService;
import org.sakaiproject.kernel.user.jcr.JcrAuthenticationResolverProvider;
import org.sakaiproject.kernel.util.IOUtils;
import org.sakaiproject.kernel.util.StringUtils;
import org.sakaiproject.kernel.util.rest.RestDescription;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.jcr.Node;
import javax.jcr.Property;
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
  private static final String PASSWORD_OLD_PARAM = "oldPassword";
  private static final String EXISTS = "exists";
  public static final String PROP_ANON_ACCOUNTING = "rest.user.anonymous.account.creation";
  private BeanConverter beanConverter;
  private UserResolverService userResolverService;
  private JCRNodeFactoryService jcrNodeFactoryService;
  private UserFactoryService userFactoryService;
  private UserEnvironmentResolverService userEnvironmentResolverService;
  private SessionManagerService sessionManagerService;
  private boolean anonymousAccounting;

  

  /**
   * @param sessionManager
   * 
   */
  @Inject
  public RestUserProvider(
      RegistryService registryService,
      @Named(BeanConverter.REPOSITORY_BEANCONVETER) BeanConverter beanConverter,
      UserResolverService userResolverService,
      UserEnvironmentResolverService userEnvironmentResolverService,
      JCRNodeFactoryService jcrNodeFactoryService,
      UserFactoryService userFactoryService,
      SessionManagerService sessionManagerService,
      @Named(PROP_ANON_ACCOUNTING) String anonymousAccounting) {
    Registry<String, RestProvider> restRegistry = registryService
        .getRegistry(RestProvider.REST_REGISTRY);
    restRegistry.add(this);
    this.beanConverter = beanConverter;
    this.userResolverService = userResolverService;
    this.jcrNodeFactoryService = jcrNodeFactoryService;
    this.userFactoryService = userFactoryService;
    this.userEnvironmentResolverService = userEnvironmentResolverService;
    this.sessionManagerService = sessionManagerService;
   
    
    this.anonymousAccounting = "true".equals(anonymousAccounting);
    
System.err.println("@#@######@########@######### anonymous: " + anonymousAccounting);
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
      if ("POST".equals(request.getMethod())
          || "1".equals(request.getParameter("forcePost"))) {
        // Security is managed by the JCR
        Map<String, Object> map = null;
        if ("new".equals(elements[1])) {
          map = createUser(request, response);
        } else if ("changepassword".equals(elements[1])) {
          map = changePassword(request, response,
              elements.length > 2 ? elements[2] : null);
        }

        if (map != null) {
          String responseBody = beanConverter.convertToString(map);
          response.setContentType(RestProvider.CONTENT_TYPE);
          response.getOutputStream().print(responseBody);
        }

      } else {
        if("GET".equals(request.getMethod()) && elements.length == 3 && getKey().equals(elements[0]) 
            && EXISTS.equals(elements[2]) && elements[1].trim().length() > 0) {
          handleUserExists(elements[1].trim(), response);
        } else {
          response.reset();
          response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
      }
    } catch (SecurityException ex) {
      response.reset();
      response.sendError(HttpServletResponse.SC_FORBIDDEN);
    } catch (Exception ex) {
      throw new ServletException(ex);
    }
  }

  private void handleUserExists(String eid, HttpServletResponse response) throws ServletException, IOException {
    
    
    Session session = sessionManagerService.getCurrentSession();
    User user = session.getUser();



    if ((user == null || user.getUuid() == null) && !anonymousAccounting) {
      response.reset();
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    } else {
      
      UserEnvironment env = userEnvironmentResolverService
      .resolve(user);

      if (!anonymousAccounting && (null == env || !env.isSuperUser())) {
        response.reset();
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
      } else {
        if(userResolverService.resolve(eid) != null) {
          response.reset();
          Map<String, String> body = new HashMap<String, String>();
          body.put("response", "OK");
          body.put("eid", eid);
          body.put("exists", "true");
          String json = beanConverter.convertToString(body);
          response.setContentType(RestProvider.CONTENT_TYPE);
          response.getOutputStream().print(json);
          response.getOutputStream().flush();
          response.getOutputStream().close();
        } else {
          response.reset();
          response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
      }
    }
    
  }

  /**
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws UnsupportedEncodingException
   * @throws JCRNodeFactoryServiceException
   * @throws RepositoryException
   */
  private Map<String, Object> changePassword(HttpServletRequest request,
      HttpServletResponse response, String externalId)
      throws UnsupportedEncodingException, NoSuchAlgorithmException,
      IOException, RepositoryException, JCRNodeFactoryServiceException {
    Session session = sessionManagerService.getCurrentSession();
    if (session == null) {
      response.reset();
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return null;
    }
    UserEnvironment ue = userEnvironmentResolverService.resolve(session);
    if (ue == null) {
      response.reset();
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return null;
    }
    User thisUser = ue.getUser();
    User user = thisUser;

    boolean superUser = false;
    if (externalId != null) {
      if (ue.isSuperUser()) {
        user = userResolverService.resolve(externalId);
        if (user == null) {
          throw new SecurityException("Specified user cant be found ");
        }
        superUser = true;
      } else {
        throw new SecurityException(
            "User does not have permission to change others passwords");
      }
    }
    if ( thisUser.getUuid().equals(user.getUuid()) ) {
      superUser = false;
    }
    String password = request.getParameter(PASSWORD_PARAM);
    String passwordOld = request.getParameter(PASSWORD_OLD_PARAM);

    if (password == null || password.trim().length() < 5) {
      response.reset();
      response.sendError(HttpServletResponse.SC_BAD_REQUEST,
          "Passwords are too short, minimum 5 characters");
      return null;
    }

    String userEnvironmentPath = userFactoryService.getUserEnvPath(user
        .getUuid());
    Node userEnvNode = jcrNodeFactoryService.getNode(userEnvironmentPath);
    if (!superUser) {
      // set the password
      Property storedPassword = userEnvNode
          .getProperty(JcrAuthenticationResolverProvider.JCRPASSWORDHASH);
      if (storedPassword != null) {
        String storedPasswordString = storedPassword.getString();
        if (storedPasswordString != null) {
          if (!StringUtils.sha1Hash(passwordOld).equals(storedPassword)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                "Old Password does not match ");
            return null;
          }
        }
      }
    }

    userEnvNode.setProperty(JcrAuthenticationResolverProvider.JCRPASSWORDHASH,
        StringUtils.sha1Hash(password));

    Map<String, Object> r = new HashMap<String, Object>();
    r.put("response", "OK");
    r.put("uuid", user.getUuid());
    return r;

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
      System.err.println("New User at " + userEnvironmentPath + " Is "
          + userEnv);
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
    DESCRIPTION
        .setShortDescription("The User service creates users, and sets the users password ");
    DESCRIPTION.addSection(1, "Create User",
        "Create a new user by POST ing to the /rest/user/new url with the "
            + " following parameters " + FIRST_NAME_PARAM + ","
            + LAST_NAME_PARAM + "," + EMAIL_PARAM + "," + EXTERNAL_USERID_PARAM
            + "," + PASSWORD_PARAM + "," + USER_TYPE_PARAM
            + " as described below");
    DESCRIPTION
        .addURLTemplate("/user/new",
            "POST to create a new user, firstname, lastname, email, userid and password).");
    DESCRIPTION
        .addURLTemplate(
            "/user/changepassword/<user eid>",
            "POST to change the users password, and optionally specify which user. If the user is not specified, the action"
                + " is applied to the current user, if the user is specified on the path as an EID, and the current user has super "
                + "user privalages the password will be changed. If an attempt is made to change the current users password, super "
                + "user or not, the old password must also be supplied.).");
    DESCRIPTION.addParameter(FIRST_NAME_PARAM, "The first name of the User");
    DESCRIPTION.addParameter(LAST_NAME_PARAM, "The last name of the User");
    DESCRIPTION.addParameter(EMAIL_PARAM, "The email for the user User");
    DESCRIPTION.addParameter(EXTERNAL_USERID_PARAM,
        "The external user ID for the user User");
    DESCRIPTION.addParameter(PASSWORD_PARAM,
        "The initial or replacement password for the User");
    DESCRIPTION.addParameter(PASSWORD_OLD_PARAM,
        "the old password for the User");
    DESCRIPTION.addParameter(USER_TYPE_PARAM, "The type of the user User");
    DESCRIPTION.addURLTemplate("/user/<user eid>/exists", "GET to test for existence of a user");

    DESCRIPTION
        .addResponse(
            String.valueOf(HttpServletResponse.SC_OK),
            "On New { \"response\" : \"OK\", \"uuid\" : \"AAAA\" }, where AAAA is the new user UUID ");
    DESCRIPTION.addResponse(String.valueOf(HttpServletResponse.SC_FORBIDDEN),
        "If the user does not have permission to perform the action");
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
