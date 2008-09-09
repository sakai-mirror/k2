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

import org.apache.shindig.social.opensocial.sql.support.DbPackage;

import com.google.inject.Inject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 */
public class PersonPropertiesDao extends BaseDao<DbMap> {
  private String module = "SocialNode";
  private DbPackage dbpackage;

  /**
   * 
   */
  @Inject
  public PersonPropertiesDao(DbPackage dbpackage) {
    this.dbpackage = dbpackage;
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
   * @see org.apache.shindig.social.opensocial.sql.dao.BaseDao#getObjectName()
   */
  @Override
  protected String getObjectName() {
    return "person_properties";
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.shindig.social.opensocial.sql.dao.BaseDao#getPackage()
   */
  @Override
  protected DbPackage getPackage() {
    return dbpackage;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.shindig.social.opensocial.sql.dao.BaseDao#load(java.sql.ResultSet)
   */
  @Override
  protected DbMap load(ResultSet rs) throws SQLException {
    DbMap dbMap = new DbMap();
    boolean more = true;
    while (more) {
      DbListField dbListItem = new DbListField();
      dbListItem.setObjectId(rs.getInt(1));
      dbListItem.setFkId(rs.getInt(2));
      dbListItem.setType(rs.getString(3));
      dbListItem.setPrimary(rs.getBoolean(4));
      dbListItem.setValue(rs.getString(5));
      String name = rs.getString(6);
      DbList list = dbMap.get(name);
      if (list == null) {
        list = new DbList();
        dbMap.put(name, list);
      }
      list.add(dbListItem);
      more = rs.next();
    }
    return dbMap;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.shindig.social.opensocial.sql.dao.BaseDao#save(org.apache.shindig.social.opensocial.sql.model.DbObject,
   *      java.sql.PreparedStatement, boolean)
   */
  @Override
  protected void save(DbMap p, PreparedStatement ps, boolean b) throws SQLException {
    // TODO Auto-generated method stub

  }
}
