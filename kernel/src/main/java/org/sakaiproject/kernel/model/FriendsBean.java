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

import java.util.List;
import java.util.Map;

/**
 * 
 */
public class FriendsBean {

  private String uuid;
  private Map<String, FriendBean> friends;

  /**
   * 
   */
  public FriendsBean() {
    friends = Maps.newLinkedHashMap();
  }

  /**
   * @param string
   */
  public FriendsBean(String uuid) {
    friends = Maps.newLinkedHashMap();
    this.uuid = uuid;
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
}
