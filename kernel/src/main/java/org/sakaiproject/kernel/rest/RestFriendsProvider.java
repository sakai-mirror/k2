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

package org.sakaiproject.kernel.rest;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
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
import org.sakaiproject.kernel.api.userenv.UserEnvironment;
import org.sakaiproject.kernel.api.userenv.UserEnvironmentResolverService;
import org.sakaiproject.kernel.model.FriendBean;
import org.sakaiproject.kernel.model.FriendStatus;
import org.sakaiproject.kernel.model.FriendsBean;
import org.sakaiproject.kernel.user.UserFactoryService;
import org.sakaiproject.kernel.util.IOUtils;
import org.sakaiproject.kernel.util.StringUtils;
import org.sakaiproject.kernel.util.rest.RestDescription;
import org.sakaiproject.kernel.webapp.RestServiceFaultException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The rest friends provider provides management of friends lists. These are
 * stored in a known place with metadata, as a json file. The service works on
 * the basis of adding and removing friends records from a json file.
 */
public class RestFriendsProvider implements RestProvider {

  /**
   * The path elements that might be expected in a request.
   */
  public enum PathElement {

    /**
     * major path element, always requires friendUuid
     */
    connect(3, new String[] { FRIENDUUID }), request(3,
        new String[] { MESSAGE }), accept(3, new String[] {}), cancel(3,
        new String[] {}), UNDEFINED(0, new String[] {}), reject(3,
        new String[] {}), ignore(3, new String[] {}), status(2, new String[] {}), remove(
        3, new String[] {});

    protected String[] required;
    protected int nelements;

    /**
     * 
     */
    private PathElement(int nelements, String[] required) {
      this.nelements = nelements;
      this.required = required;
    }
  }

  /**
   * A parameter parser and validator.
   */
  public class MapParams {
    protected String friendUuid;
    protected String message;
    protected String uuid;
    private PathElement major;
    private PathElement minor;

    /**
     * @param request
     */
    public MapParams(String[] elements, HttpServletRequest request) {
      if (elements.length < 2) {
        throw new RestServiceFaultException(HttpServletResponse.SC_BAD_REQUEST,
            "The request is invalid");
      }
      try {
        major = PathElement.valueOf(elements[1]);
      } catch (IllegalArgumentException ex) {
        throw new RestServiceFaultException(HttpServletResponse.SC_BAD_REQUEST,
            "The is invalid " + StringUtils.join(elements, 0, '/'));
      }
      if (elements.length < major.nelements) {
        throw new RestServiceFaultException(HttpServletResponse.SC_BAD_REQUEST,
            "The request is invalid");
      }
      if (major.nelements > 2) {
        try {
          minor = PathElement.valueOf(elements[2]);
        } catch (IllegalArgumentException ex) {
          throw new RestServiceFaultException(
              HttpServletResponse.SC_BAD_REQUEST, "The is invalid "
                  + StringUtils.join(elements, 0, '/'));
        }
      } else {
        minor = PathElement.UNDEFINED;
      }
      if (elements.length < minor.nelements) {
        throw new RestServiceFaultException(HttpServletResponse.SC_BAD_REQUEST,
            "The request is invalid");
      }
      for (String p : major.required) {
        if (StringUtils.isEmpty(p)) {
          throw new RestServiceFaultException(
              HttpServletResponse.SC_BAD_REQUEST, p + " must be specified");

        }
      }
      for (String p : minor.required) {
        if (StringUtils.isEmpty(p)) {
          throw new RestServiceFaultException(
              HttpServletResponse.SC_BAD_REQUEST, p + " must be specified");

        }
      }
      uuid = request.getRemoteUser();
      if (elements.length > 3) {
        Session session = sessionManagerService.getCurrentSession();
        UserEnvironment userEnvironment = userEnvironmentResolverService
            .resolve(session);
        if (userEnvironment.isSuperUser()) {
          uuid = elements[3];
        } else {
          throw new RestServiceFaultException(HttpServletResponse.SC_FORBIDDEN,
              "Only a super user is allowed to perform friends operations on other users.");
        }
      }

      friendUuid = request.getParameter(FRIENDUUID);
      message = request.getParameter(MESSAGE);

    }
  }

  private static final RestDescription DESC = new RestDescription();

  private static final String KEY = "friend";

  private static final String PRIVATE_PATH_BASE = "jcrprivate.base";

  private static final String FRIENDUUID = "friendUuid";

  private static final String MESSAGE = "message";

  private static final String FRIENDS_FILE = "fiends.json";

  static {
    DESC.setTitle("Friends");
    DESC.setShortDescription("Managed Friend Pairs");
    DESC.addSection(1, "Introduction",
        "This service allows the management of a friends list.");
    DESC
        .addSection(
            2,
            "Connect ",
            "A client may request a connection to a friend, with with the connect/request service. This starts a simple workflow"
                + "between the users. Once requesting, the requesting user may cancel the request. The invited friend may accept, reject or ignore"
                + "the request. For all these actions the response status code is "
                + HttpServletResponse.SC_OK
                + ", if the operation is denied "
                + "as a result of a conflicting record the response status code is  "
                + HttpServletResponse.SC_CONFLICT);
    DESC
        .addSection(
            2,
            "Status ",
            "Responds with the current users friends records. Super admins may request other users friends records.  ");
   
    DESC
        .addURLTemplate(
            "/rest/" + KEY + "/" + PathElement.status + "/" + "/<userid>",
            "Accepts GET to remove get the friend list for a user. A super user may specify the user who is performing the "
                + "accept, otherwise its the current user. ");
    DESC
        .addURLTemplate(
            "/rest/" + KEY + "/" + PathElement.connect + "/"
                + PathElement.request + "/<userid>",
            "Accepts POST to invite a friend to this user id. A super user may specify the user who is performing the "
                + "invite, otherwise its the current user. The post must be accompanied by a text message and a friend to invite.");
    DESC
        .addURLTemplate(
            "/rest/" + KEY + "/" + PathElement.connect + "/"
                + PathElement.accept + "/<userid>",
            "Accepts POST to accept an earlier invitation. A super user may specify the user who is performing the "
                + "accept, otherwise its the current user. The post must be accompanied by friend to accept.");
    DESC
        .addURLTemplate(
            "/rest/" + KEY + "/" + PathElement.connect + "/"
                + PathElement.reject + "/<userid>",
            "Accepts POST to reject an earlier invitation. A super user may specify the user who is performing the "
                + "reject, otherwise its the current user. The post must be accompanied by friend to reject, the target user will be notified.");
    DESC
        .addURLTemplate(
            "/rest/" + KEY + "/" + PathElement.connect + "/"
                + PathElement.ignore + "/<userid>",
            "Accepts POST to ignore an earlier invitation. A super user may specify the user who is performing the "
                + "ignore, otherwise its the current user. The post must be accompanied by friend to reject, the target user will not be notified.");
    DESC
        .addURLTemplate(
            "/rest/" + KEY + "/" + PathElement.connect + "/"
                + PathElement.cancel + "/<userid>",
            "Accepts POST to cancel an earlier invitation. A super user may specify the user who is performing the "
                + "accept, otherwise its the current user. The post must be accompanied by friend to cancel.");
    DESC
        .addURLTemplate(
            "/rest/" + KEY + "/" + PathElement.connect + "/"
                + PathElement.remove + "/<userid>",
            "Accepts POST to remove an earlier connection. A super user may specify the user who is performing the "
                + "accept, otherwise its the current user. The post must be accompanied by friend to cancel.");
    DESC.addSection(2, "POST", "");
    DESC.addParameter(FRIENDUUID, "the UUID of the friend");
    DESC.addParameter(MESSAGE,
        "the message associated with the request, required for requests");
    DESC.addResponse(String.valueOf(HttpServletResponse.SC_OK),
        "If the action completed Ok");
    DESC.addResponse(String.valueOf(HttpServletResponse.SC_CONFLICT),
        "If the request could not be compelted at this time");
    DESC.addResponse(String.valueOf(HttpServletResponse.SC_FORBIDDEN),
        "If permission to manage the connection is denied");
    DESC.addResponse(String
        .valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR),
        " Any other error");
  }

  private JCRNodeFactoryService jcrNodeFactoryService;

  private BeanConverter beanConverter;

  private SessionManagerService sessionManagerService;

  private UserEnvironmentResolverService userEnvironmentResolverService;

  private String privatePathBase;

  private Map<String, Object> OK = ImmutableMap.of("response", (Object) "OK");

  private UserFactoryService userFactoryService;

  @Inject
  public RestFriendsProvider(
      RegistryService registryService,
      JCRNodeFactoryService jcrNodeFactoryService,
      SessionManagerService sessionManagerService,
      UserEnvironmentResolverService userEnvironmentResolverService,
      UserFactoryService userFactoryService,
      @Named(BeanConverter.REPOSITORY_BEANCONVETER) BeanConverter beanConverter,
      @Named(PRIVATE_PATH_BASE) String privatePathBase) {
    Registry<String, RestProvider> registry = registryService
        .getRegistry(RestProvider.REST_REGISTRY);
    registry.add(this);
    this.jcrNodeFactoryService = jcrNodeFactoryService;
    this.beanConverter = beanConverter;
    this.privatePathBase = privatePathBase;
    this.sessionManagerService = sessionManagerService;
    this.userEnvironmentResolverService = userEnvironmentResolverService;
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
      HttpServletResponse response) {
    try {
      MapParams params = new MapParams(elements, request);
      Map<String, Object> map = Maps.newHashMap();
      switch (params.major) {
      case connect:
        if (!"POST".equals(request.getMethod())) {
          throw new RestServiceFaultException(
              HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
        switch (params.minor) {
        case request:
          map = doRequestConnect(params, request, response);
          break;
        case accept:
          map = doAcceptConnect(params, request, response);
          break;
        case cancel:
          map = doCancelConnect(params, request, response);
          break;
        case reject:
          map = doRejectConnect(params, request, response);
          break;
        case ignore:
          map = doIgnoreConnect(params, request, response);
          break;
        case remove:
          map = doRemoveConnect(params, request, response);
          break;
        default:
          doRequestError();
          break;
        }
        break;
      case status:
        if (!"GET".equals(request.getMethod())) {
          throw new RestServiceFaultException(
              HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
        map = doStatus(params, request, response);
        break;
      default:
        doRequestError();
        break;
      }

      if (map != null) {
        String responseBody = beanConverter.convertToString(map);
        response.setContentType(RestProvider.CONTENT_TYPE);
        response.getOutputStream().print(responseBody);
      }
    } catch (SecurityException ex) {
      throw ex;
    } catch (RestServiceFaultException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new RestServiceFaultException(ex.getMessage(), ex);
    }
  }

  /**
   * @param params
   * @param request
   * @param response
   * @return
   * @throws RepositoryException
   * @throws JCRNodeFactoryServiceException
   * @throws IOException
   */
  private Map<String, Object> doRemoveConnect(MapParams params,
      HttpServletRequest request, HttpServletResponse response)
      throws JCRNodeFactoryServiceException, RepositoryException, IOException {
    FriendsBean myFriends = loadFriends(params.uuid);
    FriendsBean friendFriends = loadFriends(params.friendUuid);
    if (!myFriends.hasFriend(params.friendUuid)
        || !friendFriends.hasFriend(params.uuid)) {
      throw new RestServiceFaultException(HttpServletResponse.SC_NOT_FOUND,
          " The friend connection is missing ");
    }
    myFriends.removeFriend(params.friendUuid);
    friendFriends.removeFriend(params.uuid);
    saveFriends(myFriends);
    saveFriends(friendFriends);
    return OK;
  }

  /**
   * @param params
   * @param request
   * @param response
   * @return
   * @throws RepositoryException
   * @throws JCRNodeFactoryServiceException
   * @throws IOException
   */
  private Map<String, Object> doRequestConnect(MapParams params,
      HttpServletRequest request, HttpServletResponse response)
      throws JCRNodeFactoryServiceException, RepositoryException, IOException {
    FriendsBean myFriends = loadFriends(params.uuid);
    FriendsBean friendFriends = loadFriends(params.friendUuid);
    if (myFriends.hasFriend(params.friendUuid)
        || friendFriends.hasFriend(params.uuid)) {
      throw new RestServiceFaultException(HttpServletResponse.SC_CONFLICT,
          "There is already a connection invited, pending or accepted ");
    }
    myFriends.addFriend(new FriendBean(params.uuid, params.friendUuid,
        FriendStatus.PENDING));
    friendFriends.addFriend(new FriendBean(params.friendUuid, params.uuid,
        FriendStatus.INVITED));
    saveFriends(myFriends);
    saveFriends(friendFriends);
    return OK;
  }

  /**
   * @param params
   * @param request
   * @param response
   * @return
   * @throws RepositoryException
   * @throws JCRNodeFactoryServiceException
   * @throws IOException
   */
  private Map<String, Object> doAcceptConnect(MapParams params,
      HttpServletRequest request, HttpServletResponse response)
      throws JCRNodeFactoryServiceException, RepositoryException, IOException {
    FriendsBean myFriends = loadFriends(params.uuid);
    FriendsBean friendFriends = loadFriends(params.friendUuid);
    if (!myFriends.hasFriend(params.friendUuid)
        || !friendFriends.hasFriend(params.uuid)) {
      throw new RestServiceFaultException(HttpServletResponse.SC_NOT_FOUND,
          " The friend connection is missing ");
    }
    FriendBean myFriendBean = myFriends.getFriend(params.friendUuid);
    FriendBean friendFriendBean = friendFriends.getFriend(params.uuid);
    if (!myFriendBean.isInState(FriendStatus.INVITED)
        || !friendFriendBean.isInState(FriendStatus.PENDING)) {
      throw new RestServiceFaultException(HttpServletResponse.SC_CONFLICT,
          "The invitation to connect is not current");
    }

    myFriendBean.updateStatus(FriendStatus.ACCEPTED);
    friendFriendBean.updateStatus(FriendStatus.ACCEPTED);
    saveFriends(myFriends);
    saveFriends(friendFriends);
    return OK;
  }

  /**
   * @param params
   * @param request
   * @param response
   * @return
   * @throws RepositoryException
   * @throws JCRNodeFactoryServiceException
   * @throws IOException
   */
  private Map<String, Object> doCancelConnect(MapParams params,
      HttpServletRequest request, HttpServletResponse response)
      throws JCRNodeFactoryServiceException, RepositoryException, IOException {
    FriendsBean myFriends = loadFriends(params.uuid);
    FriendsBean friendFriends = loadFriends(params.friendUuid);
    if (!myFriends.hasFriend(params.friendUuid)
        || !friendFriends.hasFriend(params.uuid)) {
      throw new RestServiceFaultException(HttpServletResponse.SC_NOT_FOUND,
          " The friend connection is missing ");
    }
    FriendBean myFriendBean = myFriends.getFriend(params.friendUuid);
    FriendBean friendFriendBean = friendFriends.getFriend(params.uuid);
    if (!myFriendBean.isInState(FriendStatus.PENDING)
        || !friendFriendBean.isInState(FriendStatus.INVITED)) {
      throw new RestServiceFaultException(HttpServletResponse.SC_CONFLICT,
          "The invitation to connect is not current");
    }

    myFriends.removeFriend(params.friendUuid);
    friendFriends.removeFriend(params.uuid);
    saveFriends(myFriends);
    saveFriends(friendFriends);
    return OK;
  }

  /**
   * @param params
   * @param request
   * @param response
   * @return
   * @throws RepositoryException
   * @throws JCRNodeFactoryServiceException
   * @throws IOException
   */
  private Map<String, Object> doRejectConnect(MapParams params,
      HttpServletRequest request, HttpServletResponse response)
      throws JCRNodeFactoryServiceException, RepositoryException, IOException {
    FriendsBean myFriends = loadFriends(params.uuid);
    FriendsBean friendFriends = loadFriends(params.friendUuid);
    if (!myFriends.hasFriend(params.friendUuid)
        || !friendFriends.hasFriend(params.uuid)) {
      throw new RestServiceFaultException(HttpServletResponse.SC_NOT_FOUND,
          " The friend connection is missing ");
    }
    FriendBean myFriendBean = myFriends.getFriend(params.friendUuid);
    FriendBean friendFriendBean = friendFriends.getFriend(params.uuid);
    if (!myFriendBean.isInState(FriendStatus.INVITED)
        || !friendFriendBean.isInState(FriendStatus.PENDING)) {
      throw new RestServiceFaultException(HttpServletResponse.SC_CONFLICT,
          "The invitation to connect is not current");
    }

    myFriends.removeFriend(params.friendUuid);
    friendFriends.removeFriend(params.uuid);
    saveFriends(myFriends);
    saveFriends(friendFriends);
    return OK;
  }

  /**
   * @param params
   * @param request
   * @param response
   * @return
   * @throws RepositoryException
   * @throws JCRNodeFactoryServiceException
   * @throws IOException
   */
  private Map<String, Object> doIgnoreConnect(MapParams params,
      HttpServletRequest request, HttpServletResponse response)
      throws JCRNodeFactoryServiceException, RepositoryException, IOException {
    FriendsBean myFriends = loadFriends(params.uuid);
    FriendsBean friendFriends = loadFriends(params.friendUuid);
    if (!myFriends.hasFriend(params.friendUuid)
        || !friendFriends.hasFriend(params.uuid)) {
      throw new RestServiceFaultException(HttpServletResponse.SC_NOT_FOUND,
          " The friend connection is missing ");
    }
    FriendBean myFriendBean = myFriends.getFriend(params.friendUuid);
    FriendBean friendFriendBean = friendFriends.getFriend(params.uuid);
    if (!myFriendBean.isInState(FriendStatus.INVITED)
        || !friendFriendBean.isInState(FriendStatus.PENDING)) {
      throw new RestServiceFaultException(HttpServletResponse.SC_CONFLICT,
          "The invitation to connect is not current");
    }

    myFriends.removeFriend(params.friendUuid);
    friendFriends.removeFriend(params.uuid);
    saveFriends(myFriends);
    saveFriends(friendFriends);
    return OK;
  }

  /**
   * @param params
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws RepositoryException
   * @throws UnsupportedEncodingException
   */
  private Map<String, Object> doStatus(MapParams params,
      HttpServletRequest request, HttpServletResponse response)
      throws UnsupportedEncodingException, RepositoryException, IOException {
    FriendsBean myFriends = loadFriends(params.uuid);
    return ImmutableMap.of("response", "OK", "status", myFriends);
  }

  /**
   * 
   */
  private void doRequestError() {
    throw new RestServiceFaultException(HttpServletResponse.SC_BAD_REQUEST);
  }

  /**
   * @param uuid
   * @return
   * @throws RepositoryException
   * @throws IOException
   * @throws UnsupportedEncodingException
   */
  private FriendsBean loadFriends(String uuid) throws RepositoryException,
      UnsupportedEncodingException, IOException {
    String userPath = userFactoryService.getUserEnvPath(uuid);
    userPath = privatePathBase + userPath + FRIENDS_FILE;
    InputStream in = null;
    try {
      in = jcrNodeFactoryService.getInputStream(userPath);
      String json = IOUtils.readFully(in, StringUtils.UTF8);
      FriendsBean fb = beanConverter.convertToObject(json, FriendsBean.class);
      return fb;
    } catch (JCRNodeFactoryServiceException ex) {
      return new FriendsBean(uuid);
    } finally {
      try {
        in.close();
      } catch (Exception ex) {
      }
    }
  }

  /**
   * @param friendFriends
   * @throws RepositoryException
   * @throws JCRNodeFactoryServiceException
   * @throws UnsupportedEncodingException
   */
  private void saveFriends(FriendsBean friendsBean)
      throws JCRNodeFactoryServiceException, RepositoryException,
      UnsupportedEncodingException {
    String userPath = userFactoryService.getUserEnvPath(friendsBean.getUuid());
    userPath = privatePathBase + userPath + FRIENDS_FILE;
    String json = beanConverter.convertToString(friendsBean);
    InputStream in = new ByteArrayInputStream(json.getBytes(StringUtils.UTF8));
    try {
      Node n = jcrNodeFactoryService.setInputStream(userPath, in,
          RestProvider.CONTENT_TYPE);
      n.save();
    } finally {
      try {
        in.close();
      } catch (Exception ex) {
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

}
