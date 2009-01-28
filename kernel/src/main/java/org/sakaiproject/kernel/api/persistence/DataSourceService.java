/*
 * Licensed to the Sakai Foundation (SF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The SF licenses this file
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

package org.sakaiproject.kernel.api.persistence;

import javax.sql.DataSource;

/**
 * Service to provide data sources.
 */
public interface DataSourceService {

  public static final String JDBC_DRIVER_NAME = "jdbc.driver";
  public static final String JDBC_URL = "jdbc.url";
  public static final String JDBC_USERNAME = "jdbc.username";
  public static final String JDBC_PASSWORD = "jdbc.password";
  public static final String JDBC_VALIDATION_QUERY = "jdbc.validation";
  public static final String JDBC_DEFAULT_READ_ONLY = "jdbc.defaultReadOnly";
  public static final String JDBC_DEFAULT_AUTO_COMMIT = "jdbc.defaultAutoCommit";
  public static final String JDBC_DEFAULT_PREPARED_STATEMENTS = "jdbc.defaultPreparedStatement";
  /**
   * Standard JPA JTA DataSource name.
   */
  public static final String JTA_DATASOURCE = "javax.persistence.jtaDataSource";

  /**
   * Standard JPA non-JTA DataSource name.
   */
  public static final String NON_JTA_DATASOURCE = "javax.persistence.nonJtaDataSource";

  /**
   * @return
   */
  DataSource getDataSource();

  /**
   * @return
   */
  String getType();
}
