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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;

/**
 *
 */
/*
 * This object connects to a single Address, and to a single organization, 
 * defining the organizations relationship with the address
 */
@Entity
@SecondaryTable(name="organizational_address", pkJoinColumns=@PrimaryKeyJoinColumn(name="address_id", referencedColumnName="oid"))
public class OrganizationAddressDb extends AddressDb {
  @Basic
  @Column(name="primary", table="organizational_address")
  private Boolean org_primary;
  
  @ManyToOne
  @JoinColumn(name="organization_id", referencedColumnName="oid")
  private Organization organization;
  
  @Basic
  @Column(name="type", length=255, table="organizational_address")
  private String org_type;


  public OrganizationAddressDb() {
    // TODO Auto-generated constructor stub
  }


  public String getType() {
    return org_type;
  }

  public void setType(String type) {
    this.org_type = type;
  }


  public Boolean getPrimary() {
    return org_primary;
  }

  public void setPrimary(Boolean primary) {
    this.org_primary = primary;
  }


}
