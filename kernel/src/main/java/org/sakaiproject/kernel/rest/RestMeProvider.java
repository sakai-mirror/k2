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
import org.sakaiproject.kernel.api.session.Session;
import org.sakaiproject.kernel.api.session.SessionManagerService;
import org.sakaiproject.kernel.api.user.User;
import org.sakaiproject.kernel.api.userenv.UserEnvironment;
import org.sakaiproject.kernel.api.userenv.UserEnvironmentResolverService;
import org.sakaiproject.kernel.authz.simple.NullUserEnvironment;
import org.sakaiproject.kernel.util.IOUtils;
import org.sakaiproject.kernel.util.rest.RestDescription;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Implements the Me service.
 */
public class RestMeProvider implements RestProvider {

  private static final String ANON_UE_FILE = "/configuration/defaults/anonue.json";
  private static RestDescription DESCRIPTION = new RestDescription();
  private JCRNodeFactoryService jcrNodeFactoryService;
  private SessionManagerService sessionManagerService;
  private UserLocale userLocale;
  private BeanConverter beanConverter;
  private UserEnvironmentResolverService userEnvironmentResolverService;

  @Inject
  public RestMeProvider(
      RegistryService registryService,
      SessionManagerService sessionManagerService,
      JCRNodeFactoryService jcrNodeFactoryService,
      UserLocale userLocale,
      @Named(BeanConverter.REPOSITORY_BEANCONVETER) BeanConverter beanConverter,
      UserEnvironmentResolverService userEnvironmentResolverService) {
    Registry<String, RestProvider> registry = registryService
        .getRegistry(RestProvider.REST_REGISTRY);
    registry.add(this);
    this.sessionManagerService = sessionManagerService;
    this.jcrNodeFactoryService = jcrNodeFactoryService;
    this.userLocale = userLocale;
    this.beanConverter = beanConverter;
    this.userEnvironmentResolverService = userEnvironmentResolverService;
  }

  static {
    DESCRIPTION.setTitle("Me Service");
    DESCRIPTION
        .setShortDescription("The Me service provides information about "
            + "the current user");
    DESCRIPTION
        .addSection(
            1,
            "Introduction",
            "The Me Service, when queried will respond with a json specific "
                + "to the logged in user. If no logged in user is present, then an "
                + "anonymouse json response will be sent. In addition some headers "
                + "will be modified to reflect the locale preferences of the user.");
    DESCRIPTION
        .addSection(
            2,
            "Response: Anon User",
            "Where the user is an anon user the response will contain 2 parts, "
                + "a description of the locale and a default anon user environment. The "
                + "locale is derived from the locale specified in the request and the "
                + "locale of the server. ");
    DESCRIPTION
        .addSection(
            2,
            "Response: Authenticated User",
            "Where the user is an authenticaated user the response will contain 2 parts, "
                + "a description of the locale and a authenticated user environment, of if none "
                + "is found a default one for the user. The "
                + "locale is derived from the locale specified in the request, any prefereces "
                + "expressed by the user and the " + "locale of the server. ");
    DESCRIPTION.addParameter("none", "The service accepts no parameters ");
    DESCRIPTION
        .addHeader("none",
            "The service neither looks for headers nor sets any non standatd headers");
    DESCRIPTION
        .addURLTemplate("me*",
            "The service is selected by /rest/me any training path will be ignored");
    DESCRIPTION
        .addResponse(
            "200",
            "The service returns a JSON body with 2 structures locale, and preferences. eg "
                + " { locale :{\"country\":\"US\",\"variant\":\"\",\"displayCountry\":\"United States\","
                + "\"ISO3Country\":\"USA\",\"displayVariant\":\"\",\"language\":\"en\",\"displayLanguage\":\"English\","
                + "\"ISO3Language\":\"eng\",\"displayName\":\"English (United States)\"}, "
                + "preferences :{ userid : \"ib236\",  superUser: false,  subjects : [\"group1:maintain\" ,\"group2:maintain\" ,"
                + "\"group2:access\" ,\".engineering:student\"]}}");

  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.rest.RestProvider#dispatch(java.lang.String[],
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse) /x/y/z?searchOrder=1231231
   */
  public void dispatch(String[] elements, HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    try {
      Session session = sessionManagerService.getCurrentSession();
      User user = session.getUser();

      System.err.println("Got user as " + user);

      Locale locale = userEnvironmentResolverService.getUserLocale(request
          .getLocale(), session);
      if (user == null || user.getUuid() == null
          || "anon".equals(user.getUuid())) {
        sendOutput(response, locale, ANON_UE_FILE);
      } else {
        UserEnvironment userEnvironment = userEnvironmentResolverService
            .resolve(user);
        if (userEnvironment == null
            || userEnvironment instanceof NullUserEnvironment) {
          sendDefaultUserOutput(response, locale, user.getUuid());
        } else {
          sendOutput(response, locale, userEnvironment);
        }
      }
    } catch (RepositoryException re) {
      throw new ServletException(re.getMessage(), re);
    } catch (JCRNodeFactoryServiceException e) {
      throw new ServletException(e.getMessage(), e);
    }
  }

  /**
   * @param response
   * @param anonMeFile
   * @throws JCRNodeFactoryServiceException
   * @throws RepositoryException
   * @throws IOException
   */
  private void sendOutput(HttpServletResponse response, Locale locale,
      UserEnvironment userEnvironment) throws RepositoryException,
      JCRNodeFactoryServiceException, IOException {
    response.setContentType(RestProvider.CONTENT_TYPE);
    ServletOutputStream outputStream = response.getOutputStream();
    outputStream.print("{ \"locale\" :");
    outputStream.print(beanConverter.convertToString(userLocale
        .localeToMap(locale)));
    outputStream.print(", \"preferences\" :");
    userEnvironment.setProtected(true);
    String json = beanConverter.convertToString(userEnvironment);
    userEnvironment.setProtected(false);
    outputStream.print(json);
    outputStream.print("}");
  }

  /**
   * @param response
   * @param anonMeFile
   * @throws JCRNodeFactoryServiceException
   * @throws RepositoryException
   * @throws IOException
   */
  private void sendOutput(HttpServletResponse response, Locale locale,
      String path) throws RepositoryException, JCRNodeFactoryServiceException,
      IOException {
    response.setContentType(RestProvider.CONTENT_TYPE);
    ServletOutputStream outputStream = response.getOutputStream();
    outputStream.print("{ \"locale\" :");
    outputStream.print(beanConverter.convertToString(userLocale
        .localeToMap(locale)));
    outputStream.print(", \"preferences\" :");

    InputStream in = null;
    try {
      in = jcrNodeFactoryService.getInputStream(path);
      IOUtils.stream(in, outputStream);
    } finally {
      try {
        in.close();
      } catch (Exception ex) {
      }
    }
    outputStream.print("}");
  }

  /**
   * @param response
   * @param anonMeFile
   * @throws JCRNodeFactoryServiceException
   * @throws RepositoryException
   * @throws IOException
   */
  private void sendDefaultUserOutput(HttpServletResponse response,
      Locale locale, String userUuid) throws RepositoryException,
      JCRNodeFactoryServiceException, IOException {
    response.setContentType(RestProvider.CONTENT_TYPE);
    ServletOutputStream outputStream = response.getOutputStream();
    outputStream.print("{ locale :");
    outputStream.print(beanConverter.convertToString(userLocale
        .localeToMap(locale)));
    outputStream.print(", preferences :");
    Map<String, Object> m = new HashMap<String, Object>();
    m.put("uuid", userUuid);
    m.put("superUser", false);
    m.put("subjects", new String[0]);
    outputStream.print(beanConverter.convertToString(m));
    outputStream.print("}");
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
    return "me";
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
