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

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

import org.apache.shindig.social.opensocial.model.Account;
import org.apache.shindig.social.opensocial.model.Address;
import org.apache.shindig.social.opensocial.model.BodyType;
import org.apache.shindig.social.opensocial.model.Enum;
import org.apache.shindig.social.opensocial.model.ListField;
import org.apache.shindig.social.opensocial.model.Name;
import org.apache.shindig.social.opensocial.model.Organization;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.model.Url;

import com.google.common.collect.Lists;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import java.util.Date;
import java.util.List;

/**
 * Default Implementation of the Person object in the org.apache.shindig.social.opensocial.jpa.
 */
@Entity
public class PersonDb implements Person, DbObject {
  @Id
  @GeneratedValue(strategy=IDENTITY)
  @Column(name="oid")
  private long objectId;
  protected long oid;
  protected String aboutMe;
  protected List<Account> accounts;
  protected List<String> activities;
  protected List<Address> addresses;
  protected Integer age;
  protected BodyType bodyType;
  protected List<String> books;
  protected List<String> cars;
  protected String children;
  protected Address currentLocation;
  protected Date birthday;
  protected Enum<Enum.Drinker> drinker;
  protected List<ListField> emails;
  protected String ethnicity;
  protected String fashion;
  protected List<String> food;
  protected Gender gender;
  protected String happiestWhen;
  protected Boolean hasApp;
  protected List<String> heroes;
  protected String humor;
  protected String id;
  protected List<ListField> ims;
  protected List<String> interests;
  protected String jobInterests;
  protected List<String> languagesSpoken;
  protected Date updated;
  protected String livingArrangement;
  protected List<Enum<Enum.LookingFor>> lookingFor;
  protected List<String> movies;
  protected List<String> music;
  protected Name name;
  protected Enum<Enum.NetworkPresence> networkPresence;
  protected String nickname;
  protected List<Organization> organizations;
  protected String pets;
  protected List<ListField> phoneNumbers;
  protected List<ListField> photos;
  protected String politicalViews;
  protected Url profileSong;
  protected Url profileVideo;
  protected List<String> quotes;
  protected String relationshipStatus;
  protected String religion;
  protected String romance;
  protected String scaredOf;
  protected String sexualOrientation;
  protected Enum<Enum.Smoker> smoker;
  protected List<String> sports;
  protected String status;
  protected List<String> tags;
  protected Long utcOffset;
  protected List<String> turnOffs;
  protected List<String> turnOns;
  protected List<String> tvShows;
  protected List<Url> urls;

  // Note: Not in the opensocial js person object directly
  private boolean isOwner = false;
  private boolean isViewer = false;

  public PersonDb() {
  }

  public PersonDb(String id, Name name) {
    this.id = id;
    this.name = name;
  }

  public String getAboutMe() {
    return aboutMe;
  }

  public void setAboutMe(String aboutMe) {
    this.aboutMe = aboutMe;
  }

  public List<Account> getAccounts() {
    return accounts;
  }

  public void setAccounts(List<Account> accounts) {
    this.accounts = accounts;
  }

  public List<String> getActivities() {
    return activities;
  }

  public void setActivities(List<String> activities) {
    this.activities = activities;
  }

  public List<Address> getAddresses() {
    return addresses;
  }

  public void setAddresses(List<Address> addresses) {
    this.addresses = addresses;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  public BodyType getBodyType() {
    return bodyType;
  }

  public void setBodyType(BodyType bodyType) {
    this.bodyType = bodyType;
  }

  public List<String> getBooks() {
    return books;
  }

  public void setBooks(List<String> books) {
    this.books = books;
  }

  public List<String> getCars() {
    return cars;
  }

  public void setCars(List<String> cars) {
    this.cars = cars;
  }

  public String getChildren() {
    return children;
  }

  public void setChildren(String children) {
    this.children = children;
  }

  public Address getCurrentLocation() {
    return currentLocation;
  }

  public void setCurrentLocation(Address currentLocation) {
    this.currentLocation = currentLocation;
  }

  public Date getBirthday() {
    if (birthday == null) {
      return null;
    }
    return new Date(birthday.getTime());
  }

  public void setBirthday(Date birthday) {
    if (birthday == null) {
      this.birthday = null;
    } else {
      this.birthday = new Date(birthday.getTime());
    }
  }

  public Enum<Enum.Drinker> getDrinker() {
    return this.drinker;
  }

  public void setDrinker(Enum<Enum.Drinker> newDrinker) {
    this.drinker = newDrinker;
  }

  public List<ListField> getEmails() {
    return emails;
  }

  public void setEmails(List<ListField> emails) {
    this.emails = emails;
  }

  public String getEthnicity() {
    return ethnicity;
  }

  public void setEthnicity(String ethnicity) {
    this.ethnicity = ethnicity;
  }

  public String getFashion() {
    return fashion;
  }

  public void setFashion(String fashion) {
    this.fashion = fashion;
  }

  public List<String> getFood() {
    return food;
  }

  public void setFood(List<String> food) {
    this.food = food;
  }

  public Gender getGender() {
    return gender;
  }

  public void setGender(Gender newGender) {
    this.gender = newGender;
  }

  public String getHappiestWhen() {
    return happiestWhen;
  }

  public void setHappiestWhen(String happiestWhen) {
    this.happiestWhen = happiestWhen;
  }

  public Boolean getHasApp() {
    return hasApp;
  }

  public void setHasApp(Boolean hasApp) {
    this.hasApp = hasApp;
  }

  public List<String> getHeroes() {
    return heroes;
  }

  public void setHeroes(List<String> heroes) {
    this.heroes = heroes;
  }

  public String getHumor() {
    return humor;
  }

  public void setHumor(String humor) {
    this.humor = humor;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<ListField> getIms() {
    return ims;
  }

  public void setIms(List<ListField> ims) {
    this.ims = ims;
  }

  public List<String> getInterests() {
    return interests;
  }

  public void setInterests(List<String> interests) {
    this.interests = interests;
  }

  public String getJobInterests() {
    return jobInterests;
  }

  public void setJobInterests(String jobInterests) {
    this.jobInterests = jobInterests;
  }

  public List<String> getLanguagesSpoken() {
    return languagesSpoken;
  }

  public void setLanguagesSpoken(List<String> languagesSpoken) {
    this.languagesSpoken = languagesSpoken;
  }

  public Date getUpdated() {
    if (updated == null) {
      return null;
    }
    return new Date(updated.getTime());
  }

  public void setUpdated(Date updated) {
    if (updated == null) {
      this.updated = null;
    } else {
      this.updated = new Date(updated.getTime());
    }
  }

  public String getLivingArrangement() {
    return livingArrangement;
  }

  public void setLivingArrangement(String livingArrangement) {
    this.livingArrangement = livingArrangement;
  }

  public List<Enum<Enum.LookingFor>> getLookingFor() {
    return lookingFor;
  }

  public void setLookingFor(List<Enum<Enum.LookingFor>> lookingFor) {
    this.lookingFor = lookingFor;
  }

  public List<String> getMovies() {
    return movies;
  }

  public void setMovies(List<String> movies) {
    this.movies = movies;
  }

  public List<String> getMusic() {
    return music;
  }

  public void setMusic(List<String> music) {
    this.music = music;
  }

  public Name getName() {
    return name;
  }

  public void setName(Name name) {
    this.name = name;
  }

  public Enum<Enum.NetworkPresence> getNetworkPresence() {
    return networkPresence;
  }

  public void setNetworkPresence(Enum<Enum.NetworkPresence> networkPresence) {
    this.networkPresence = networkPresence;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public List<Organization> getOrganizations() {
    return organizations;
  }

  public void setOrganizations(List<Organization> organizations) {
    this.organizations = organizations;
  }

  public String getPets() {
    return pets;
  }

  public void setPets(String pets) {
    this.pets = pets;
  }

  public List<ListField> getPhoneNumbers() {
    return phoneNumbers;
  }

  public void setPhoneNumbers(List<ListField> phoneNumbers) {
    this.phoneNumbers = phoneNumbers;
  }

  public List<ListField> getPhotos() {
    return photos;
  }

  public void setPhotos(List<ListField> photos) {
    this.photos = photos;
  }

  public String getPoliticalViews() {
    return politicalViews;
  }

  public void setPoliticalViews(String politicalViews) {
    this.politicalViews = politicalViews;
  }

  public Url getProfileSong() {
    return profileSong;
  }

  public void setProfileSong(Url profileSong) {
    this.profileSong = profileSong;
  }

  public Url getProfileVideo() {
    return profileVideo;
  }

  public void setProfileVideo(Url profileVideo) {
    this.profileVideo = profileVideo;
  }

  public List<String> getQuotes() {
    return quotes;
  }

  public void setQuotes(List<String> quotes) {
    this.quotes = quotes;
  }

  public String getRelationshipStatus() {
    return relationshipStatus;
  }

  public void setRelationshipStatus(String relationshipStatus) {
    this.relationshipStatus = relationshipStatus;
  }

  public String getReligion() {
    return religion;
  }

  public void setReligion(String religion) {
    this.religion = religion;
  }

  public String getRomance() {
    return romance;
  }

  public void setRomance(String romance) {
    this.romance = romance;
  }

  public String getScaredOf() {
    return scaredOf;
  }

  public void setScaredOf(String scaredOf) {
    this.scaredOf = scaredOf;
  }

  public String getSexualOrientation() {
    return sexualOrientation;
  }

  public void setSexualOrientation(String sexualOrientation) {
    this.sexualOrientation = sexualOrientation;
  }

  public Enum<Enum.Smoker> getSmoker() {
    return this.smoker;
  }

  public void setSmoker(Enum<Enum.Smoker> newSmoker) {
    this.smoker = newSmoker;
  }

  public List<String> getSports() {
    return sports;
  }

  public void setSports(List<String> sports) {
    this.sports = sports;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public Long getUtcOffset() {
    return utcOffset;
  }

  public void setUtcOffset(Long utcOffset) {
    this.utcOffset = utcOffset;
  }

  public List<String> getTurnOffs() {
    return turnOffs;
  }

  public void setTurnOffs(List<String> turnOffs) {
    this.turnOffs = turnOffs;
  }

  public List<String> getTurnOns() {
    return turnOns;
  }

  public void setTurnOns(List<String> turnOns) {
    this.turnOns = turnOns;
  }

  public List<String> getTvShows() {
    return tvShows;
  }

  public void setTvShows(List<String> tvShows) {
    this.tvShows = tvShows;
  }

  public List<Url> getUrls() {
    return urls;
  }

  public void setUrls(List<Url> urls) {
    this.urls = urls;
  }

  public boolean getIsOwner() {
    return isOwner;
  }

  public void setIsOwner(boolean isOwner) {
    this.isOwner = isOwner;
  }

  public boolean getIsViewer() {
    return isViewer;
  }

  public void setIsViewer(boolean isViewer) {
    this.isViewer = isViewer;
  }

  // Proxied fields

  public String getProfileUrl() {
    Url url = getListFieldWithType(PROFILE_URL_TYPE, getUrls());
    return url == null ? null : url.getValue();
  }

  public void setProfileUrl(String profileUrl) {
    Url url = getListFieldWithType(PROFILE_URL_TYPE, getUrls());
    if (url != null) {
      url.setValue(profileUrl);
    } else {
      setUrls(addListField(new UrlDb(profileUrl, null, PROFILE_URL_TYPE), getUrls()));
    }
  }

  public String getThumbnailUrl() {
    ListField photo = getListFieldWithType(THUMBNAIL_PHOTO_TYPE, getPhotos());
    return photo == null ? null : photo.getValue();
  }

  public void setThumbnailUrl(String thumbnailUrl) {
    ListField photo = getListFieldWithType(THUMBNAIL_PHOTO_TYPE, getPhotos());
    if (photo != null) {
      photo.setValue(thumbnailUrl);
    } else {
      setPhotos(addListField(new ListFieldDb(THUMBNAIL_PHOTO_TYPE, thumbnailUrl), getPhotos()));
    }
  }

  private <T extends ListField> T getListFieldWithType(String type, List<T> list) {
    if (list != null) {
      for (T url : list) {
        if (type.equalsIgnoreCase(url.getType())) {
          return url;
        }
      }
    }

    return null;
  }

  private <T extends ListField> List<T> addListField(T field, List<T> list) {
    if (list == null) {
      list = Lists.newArrayList();
    }
    list.add(field);
    return list;
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
