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

import javax.sql.DataSource;

import java.io.IOException;
import java.sql.SQLException;

/**
 *
 */
public class DbPackage {

  private DataSource connectionHolder;
  private SQLStatements sqlstatements;
  

  @Inject
  public DbPackage(DataSource connectionHolder, DDLLoader loader, SQLStatements sqlstatements) throws SQLException, IOException {
    this.connectionHolder = connectionHolder;
    this.sqlstatements = sqlstatements;
    loader.init();
  }




  /**
   * @return the sqlstatements
   */
  public SQLStatements getSqlStatements() {
    return sqlstatements;
  }


  /**
   * @param sqlstatements the sqlstatements to set
   */
  public void setSqlstatements(SQLStatements sqlstatements) {
    this.sqlstatements = sqlstatements;
  }
}
