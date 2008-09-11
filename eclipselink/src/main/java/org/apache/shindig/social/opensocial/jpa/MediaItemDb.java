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
package org.apache.shindig.social.opensocial.jpa;

import static javax.persistence.GenerationType.IDENTITY;

import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.MediaItem;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import java.util.List;

/**
 * see
 * http://code.google.com/apis/opensocial/docs/0.7/reference/opensocial.Activity.MediaItem.Field.html
 *
 */
@Entity
public class MediaItemDb implements MediaItem, DbObject {
  @Id
  @GeneratedValue(strategy=IDENTITY)
  @Column(name="oid")
  private long objectId;
  /*
   * The mapping for this is in ActivityDb. 
   */
  @ManyToMany(targetEntity=ActivityDb.class, mappedBy = "mediaItems")
  protected List<Activity> activities;
  @Basic
  @Column(name="mime_type", length=255)
  private String mimeType;
  @Basic
  @Enumerated
  @Column(name="media_type")
  private Type type;
  @Basic
  @Column(name="url", length=255)
  private String url;

  public MediaItemDb() {
  }

  public MediaItemDb(String mimeType, Type type, String url) {
    this.mimeType = mimeType;
    this.type = type;
    this.url = url;
  }

  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * @return the objectId
   */
  public long getObjectId() {
    return objectId;
  }

  /**
   * @param objectId the objectId to set
   */
  public void setObjectId(long objectId) {
    this.objectId = objectId;
  }
}
