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

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * A JPA bean for the friends index.
 */
@Entity
@Table(name = "friends")
@NamedQueries(value = {
    @NamedQuery(name = FriendsIndexBean.FINDBY_FRIENDUUID, query = "select s from FriendsIndexBean s where s.friendUuid = :friendUuid"),
    @NamedQuery(name = FriendsIndexBean.FINDBY_UUID, query = "select s from FriendsIndexBean s where s.uuid = :uuid") })
public class FriendsIndexBean {

  public static final String PARAM_FRIENDUUID = "friendUuid";
  public static final String FINDBY_FRIENDUUID = "FriendsIndex.FindByFriendUuid";
  public static final String PARAM_UUID = "uuid";
  public static final String FINDBY_UUID = "SubjectPermission.FindByUuid";

  @SuppressWarnings("unused")
  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "oid")
  private long objectId;

  /**
   * The UUID of the owner of this friend connection
   */
  @Column(name = "uuid")
  private String uuid;
  /**
   * The UUID of the friend
   */
  @Column(name = "friendUuid")
  private String friendUuid;
  /**
   * The first name of the user.
   */
  @Column(name = "firstName")
  private String firstName;

  /**
   * The last name of the user.
   */
  @Column(name = "lastName")
  private String lastName;

  /**
   * 
   */
  public FriendsIndexBean() {
  }
  /**
   * 
   */
  public FriendsIndexBean(String uuid, String friendUuid, String firstName,
      String lastName) {
    this.uuid = uuid;
    this.friendUuid = friendUuid;
    this.firstName = firstName;
    this.lastName = lastName;
  }

  /**
   * @return the uuid
   */
  public String getUuid() {
    return uuid;
  }
  
  /**
   * @return the firstName
   */
  public String getFirstName() {
    return firstName;
  }
  /**
   * @return the lastName
   */
  public String getLastName() {
    return lastName;
  }
  /**
   * @return the friendUuid
   */
  public String getFriendUuid() {
    return friendUuid;
  }
}
