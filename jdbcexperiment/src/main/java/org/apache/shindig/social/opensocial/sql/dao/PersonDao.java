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
package org.apache.shindig.social.opensocial.sql.dao;

import org.apache.shindig.social.core.model.EnumImpl;
import org.apache.shindig.social.opensocial.model.Enum.Drinker;
import org.apache.shindig.social.opensocial.model.Enum.NetworkPresence;
import org.apache.shindig.social.opensocial.model.Person.Gender;
import org.apache.shindig.social.opensocial.sql.model.PersonDb;
import org.apache.shindig.social.opensocial.sql.support.DbPackage;

import com.google.inject.Inject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 */
public class PersonDao extends BaseDao<PersonDb> {
  private String module = "SocialNode";
  private DbPackage dbpackage;
  private PersonPropertiesDao personPropertiesDao;

  @Inject
  public PersonDao(DbPackage dbpackage, PersonPropertiesDao personPropertiesDao) {
    this.dbpackage = dbpackage;
    this.personPropertiesDao = personPropertiesDao;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.shindig.social.opensocial.sql.dao.BaseDao#getModule()
   */
  @Override
  protected String getModule() {
    return module;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.shindig.social.opensocial.sql.dao.BaseDao#getSocialPackage()
   */
  @Override
  protected DbPackage getPackage() {
    return dbpackage;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.shindig.social.opensocial.sql.dao.BaseDao#getObjectName()
   */
  @Override
  protected String getObjectName() {
    return "person";
  }

  /**
   * @param rs
   * @return
   * @throws SQLException 
   */
  protected PersonDb load(ResultSet rs) throws SQLException {
    PersonDb person = new PersonDb(dbpackage,personPropertiesDao);
    
    int i = 1;
    person.setObjectId(rs.getInt(i++));
    person.setId(rs.getString(i++));
    
    
    person.setAboutMe(rs.getString(i++));
    person.setAge(rs.getInt(i++));
    person.setBirthday(rs.getDate(i++));
    person.setChildren(rs.getString(i++));
    person.setDrinker(new EnumImpl<Drinker>(Drinker.valueOf(rs.getString(i++))));
    person.setEthnicity(rs.getString(i++));
    person.setFashion(rs.getString(i++));
    person.setGender(Gender.valueOf(rs.getString(i++)));
    person.setHappiestWhen(rs.getString(i++));
    person.setHumor(rs.getString(i++));
    person.setJobInterests(rs.getString(i++));
    person.setLivingArrangement(rs.getString(i++));
    person.setNetworkPresence(new EnumImpl<NetworkPresence>(NetworkPresence.valueOf(rs.getString(i++))));
    person.setNickname(rs.getString(i++));
    person.setPets(rs.getString(i++));
    person.setPoliticalViews(rs.getString(i++));
    person.setProfileUrl(rs.getString(i++));
    person.setRelationshipStatus(rs.getString(i++));
    person.setReligion(rs.getString(i++));
    person.setRomance(rs.getString(i++));
    person.setScaredOf(rs.getString(i++));
    person.setSexualOrientation(rs.getString(i++));
    person.setStatus(rs.getString(i++));
    person.setThumbnailUrl(rs.getString(i++));
    person.setUpdated(rs.getTimestamp(i++));
    person.setUtcOffset(rs.getLong(i++));
    
    
    
    
    return person;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.shindig.social.opensocial.sql.dao.BaseDao#save(org.apache.shindig.social.opensocial.sql.model.DbObject,
   *      java.sql.PreparedStatement, boolean)
   */
  @Override
  protected void save(PersonDb p, PreparedStatement ps, boolean b) {

  }
}
