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

import org.apache.shindig.social.opensocial.model.db.DbAccount;
import org.apache.shindig.social.opensocial.sql.model.AccountDb;
import org.apache.shindig.social.opensocial.sql.support.DbPackage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 */
public class PersonAccountDao extends BaseDao<AccountDb> {

  /* (non-Javadoc)
   * @see org.apache.shindig.social.opensocial.sql.dao.BaseDao#getModule()
   */
  @Override
  protected String getModule() {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see org.apache.shindig.social.opensocial.sql.dao.BaseDao#getObjectName()
   */
  @Override
  protected String getObjectName() {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see org.apache.shindig.social.opensocial.sql.dao.BaseDao#getPackage()
   */
  @Override
  protected DbPackage getPackage() {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see org.apache.shindig.social.opensocial.sql.dao.BaseDao#load(java.sql.ResultSet)
   */
  @Override
  protected AccountDb load(ResultSet rs) throws SQLException {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see org.apache.shindig.social.opensocial.sql.dao.BaseDao#save(org.apache.shindig.social.opensocial.sql.model.DbObject, java.sql.PreparedStatement, boolean)
   */
  @Override
  protected void save(AccountDb p, PreparedStatement ps, boolean b) throws SQLException {
    // TODO Auto-generated method stub
    
  }


}
