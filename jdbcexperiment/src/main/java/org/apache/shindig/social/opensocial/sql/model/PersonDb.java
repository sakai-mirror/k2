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
package org.apache.shindig.social.opensocial.sql.model;

import org.apache.shindig.social.core.model.PersonImpl;
import org.apache.shindig.social.opensocial.model.Account;
import org.apache.shindig.social.opensocial.sql.dao.PersonAccountDao;
import org.apache.shindig.social.opensocial.sql.dao.PersonPropertiesDao;
import org.apache.shindig.social.opensocial.sql.support.DbPackage;

import java.sql.SQLException;
import java.util.List;

/**
 *
 */
public class PersonDb extends PersonImpl implements DbObject {

  private int objectId;
  private DbPackage socialPackage;
  private PersonPropertiesDao personPropertiesDao;
  private List<Account> accountList;
  private PersonAccountDao accountsDao;

  /**
   * @param socialPackage
   */
  public PersonDb(DbPackage socialPackage, PersonPropertiesDao personPropertiesDao) {
    this.socialPackage = socialPackage;
    this.personPropertiesDao = personPropertiesDao;
  }

  /**
   * @param objectId
   */
  public void setObjectId(int objectId) {
     this.objectId = objectId;
    
  }

  /**
   * @return the objectId
   */
  public int getObjectId() {
    return objectId;
  }
  
  /* (non-Javadoc)
   * @see org.apache.shindig.social.core.model.PersonImpl#getAccounts()
   */
  @Override
  public List<Account> getAccounts() {
    if ( accountList == null ) {
      try {
        accountList = (List<Account>) accountsDao.get(getObjectId());
      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return accountList;
  }
  
  /* (non-Javadoc)
   * @see org.apache.shindig.social.core.model.PersonImpl#setAccounts(java.util.List)
   */
  @Override
  public void setAccounts(List<Account> accounts) {
    // TODO Auto-generated method stub
    super.setAccounts(accounts);
  }

  
  /*
  DbMap props = personPropertiesDao.get(person.getObjectId());
  
  
  person.setAccounts(accounts);
  person.setAddresses(addresses);
  person.setActivities(props.get("activity").toValueList());
  person.setBodyType(bodyType);
  person.setBooks(props.get("book").toValueList());
  person.setCurrentLocation(currentLocation);
  person.setCars(props.get("car").toValueList());
  person.setFood(props.get("food").toValueList());
  person.setHeroes(props.get("hero").toValueList());
  person.setEmails(props.get("email"));
  person.setLanguagesSpoken(props.get("languagesSpoken").toValueList());
  person.setIms(props.get("im"));
  person.setInterests(props.get("interests").toValueList());
  person.setMovies(props.get("movie").toValueList());
  person.setMusic(props.get("music").toValueList());
  person.setPhoneNumbers(props.get("phonenumber"));
  person.setPhotos(props.get("photos"));
  person.setQuotes(props.get("quotes").toValueList());
  person.setLookingFor(getLookingForList(props.get("lookingfor")));
  person.setName(name);
  person.setOrganizations(organizations);
  person.setProfileSong(profileSong);
  person.setProfileVideo(profileVideo);
  person.setSmoker(newSmoker);
  person.setSports(sports);
  person.setTags(tags);
  person.setTurnOffs(turnOffs);
  person.setTurnOns(turnOns);
  person.setTvShows(tvShows);
  person.setUrls(urls);
*/
}
