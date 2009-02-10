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

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class FriendsBean {

  private String uuid;
  private List<FriendBean> friends;

  /**
   * 
   */
  public FriendsBean() {
    friends = new ArrayList<FriendBean>();
  }

  /**
   * @param string
   */
  public FriendsBean(String uuid) {
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
    return Lists.newArrayList(friends);
  }

  /**
   * @param friends
   *          the friends to set
   */
  public void setFriends(List<FriendBean> friends) {
    this.friends = Lists.newArrayList(friends);
  }

  /**
   * @param friendBean
   */
  public void addFriend(FriendBean friendBean) {
    if (friends == null) {
      friends = Lists.newArrayList();
    }
    friends.add(friendBean);
  }
}
