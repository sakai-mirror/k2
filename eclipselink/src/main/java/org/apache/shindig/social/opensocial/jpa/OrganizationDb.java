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

import org.apache.shindig.social.opensocial.model.Address;
import org.apache.shindig.social.opensocial.model.Organization;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.util.Date;

@Entity
@Table(name="organization")
public class OrganizationDb implements Organization, DbObject {
  @Id
  @GeneratedValue(strategy=IDENTITY)
  @Column(name="oid")
  private long objectId;
  @OneToMany(targetEntity=OrganizationAddressDb.class, mappedBy="organization")
  private Address address;
  @Basic
  @Column(name="description", length=255)
  private String description;
  @Basic
  @Column(name="endDate")
  @Temporal(TemporalType.DATE)
  private Date endDate;
  @Basic
  @Column(name="field", length=255)
  private String field;
  @Basic
  @Column(name="name", length=255)
  private String name;
  @Basic
  @Column(name="salary", length=255)
  private String salary;
  @Basic
  @Column(name="start_date")
  @Temporal(TemporalType.DATE)
  private Date startDate;
  @Basic
  @Column(name="sub_field", length=255)
  private String subField;
  @Basic
  @Column(name="title", length=255)
  private String title;
  @Basic
  @Column(name="webpage", length=255)
  private String webpage;
  @Basic
  @Column(name="type", length=255)
  private String type;
  @Basic
  @Column(name="primary")
  private Boolean primary;

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Date getEndDate() {
    if (endDate == null) {
      return null;
    }
    return new Date(endDate.getTime());
  }

  public void setEndDate(Date endDate) {
    if (endDate == null) {
      this.endDate = null;
    } else {
      this.endDate = new Date(endDate.getTime());
    }
  }

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSalary() {
    return salary;
  }

  public void setSalary(String salary) {
    this.salary = salary;
  }

  public Date getStartDate() {
    if (startDate == null) {
      return null;
    }
    return new Date(startDate.getTime());
  }

  public void setStartDate(Date startDate) {
    if (startDate == null) {
      this.startDate = null;
    } else {
      this.startDate = new Date(startDate.getTime());
    }
  }

  public String getSubField() {
    return subField;
  }

  public void setSubField(String subField) {
    this.subField = subField;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getWebpage() {
    return webpage;
  }

  public void setWebpage(String webpage) {
    this.webpage = webpage;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Boolean getPrimary() {
    return primary;
  }

  public void setPrimary(Boolean primary) {
    this.primary = primary;
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
