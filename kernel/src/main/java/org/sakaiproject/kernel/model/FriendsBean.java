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
package org.sakaiproject.kernel.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
import org.sakaiproject.kernel.api.rest.RestProvider;
import org.sakaiproject.kernel.api.serialization.BeanConverter;
import org.sakaiproject.kernel.api.social.FriendsResolverService;
import org.sakaiproject.kernel.user.UserFactoryService;
import org.sakaiproject.kernel.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * 
 */
public class FriendsBean {
  private static final String PRIVATE_PATH_BASE = "jcrprivate.base";

  private String uuid;
  private Map<String, FriendBean> friends;
  private JCRNodeFactoryService jcrNodeFactoryService;
  private BeanConverter beanConverter;
  private UserFactoryService userFactoryService;
  private String privatePathBase;


  @Inject
  public FriendsBean(JCRNodeFactoryService jcrNodeFactoryService,
      UserFactoryService userFactoryService,
      @Named(BeanConverter.REPOSITORY_BEANCONVETER) BeanConverter beanConverter,
      @Named(PRIVATE_PATH_BASE) String privatePathBase) {
    friends = Maps.newLinkedHashMap();
    this.jcrNodeFactoryService = jcrNodeFactoryService;
    this.beanConverter = beanConverter;
    this.userFactoryService = userFactoryService;
    this.privatePathBase = privatePathBase;
  }


  /**
   * @return the uuid
   */
  public String getUuid() {
    return uuid;
  }

  /**
   * @param uuid
   *          the uuid to set
   */
  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  /**
   * @return the friends
   */
  public List<FriendBean> getFriends() {
    return Lists.newArrayList(friends.values());
  }

  /**
   * @param friends
   *          the friends to set
   */
  public void setFriends(List<FriendBean> friends) {
    Map<String, FriendBean> newFriends = Maps.newLinkedHashMap();
    for ( FriendBean fb : friends ) {
      newFriends.put(fb.getFriendUuid(), fb);
    }
    this.friends = newFriends;
  }

  /**
   * @param friendBean
   */
  public void addFriend(FriendBean friendBean) {
    friends.put(friendBean.getFriendUuid(),friendBean);
  }

  /**
   * @param friendUuid
   */
  public void removeFriend(String friendUuid) {
    friends.remove(friendUuid);
  }

  /**
   * @param friendUuid
   * @return
   */
  public boolean hasFriend(String friendUuid) {
    return friends.containsKey(friendUuid);
  }

  /**
   * @param friendUuid
   * @return
   */
  public FriendBean getFriend(String friendUuid) {
    return friends.get(friendUuid);
  }

  /**
   * 
   */
  public Map<String, FriendBean> friendsMap() {
    return Maps.newLinkedHashMap(friends);
  }
  
  public void save() throws JCRNodeFactoryServiceException, RepositoryException, UnsupportedEncodingException {
    String userPath = userFactoryService.getUserEnvPath(uuid);
    userPath = privatePathBase + userPath + FriendsResolverService.FRIENDS_FILE;

    String json = beanConverter.convertToString(this);
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

}