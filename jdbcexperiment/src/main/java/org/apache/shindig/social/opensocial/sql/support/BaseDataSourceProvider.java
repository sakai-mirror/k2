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
package org.apache.shindig.social.opensocial.sql.support;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import org.apache.commons.dbcp.BasicDataSource;

import javax.sql.DataSource;

/**
 *
 */
public class BaseDataSourceProvider implements Provider<DataSource>{

  private String driverClassName;
  private String password;
  private String url;
  private String username;
  private int maxActive;
  private int maxIdle;

  /* (non-Javadoc)
   * @see com.google.inject.Provider#get()
   */
  public DataSource get() {
    BasicDataSource basicDataSource = new BasicDataSource();
    basicDataSource.setDriverClassName(driverClassName);
    basicDataSource.setPassword(password);
    basicDataSource.setUrl(url);
    basicDataSource.setUsername(username);
    basicDataSource.setMaxActive(maxActive);
    basicDataSource.setMaxIdle(maxIdle);
    return basicDataSource;
  }

  /**
   * @return the driverClassName
   */
  public String getDriverClassName() {
    return driverClassName;
  }

  /**
   * @param driverClassName the driverClassName to set
   */
  public void setDriverClassName(String driverClassName) {
    this.driverClassName = driverClassName;
  }

  /**
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * @param password the password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * @return the url
   */
  public String getUrl() {
    return url;
  }

  /**
   * @param url the url to set
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * @param username the username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * @return the maxActive
   */
  public int getMaxActive() {
    return maxActive;
  }

  /**
   * @param maxActive the maxActive to set
   */
  public void setMaxActive(int maxActive) {
    this.maxActive = maxActive;
  }

  /**
   * @return the maxIdle
   */
  public int getMaxIdle() {
    return maxIdle;
  }

  /**
   * @param maxIdle the maxIdle to set
   */
  public void setMaxIdle(int maxIdle) {
    this.maxIdle = maxIdle;
  }

}
