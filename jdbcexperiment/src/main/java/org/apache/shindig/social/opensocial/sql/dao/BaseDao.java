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

import org.apache.shindig.social.opensocial.sql.model.DbObject;
import org.apache.shindig.social.opensocial.sql.support.DbPackage;
import org.apache.shindig.social.opensocial.sql.support.QueryCallback;
import org.apache.shindig.social.opensocial.sql.support.UpdateCallback;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 */
public abstract class BaseDao<T extends DbObject> {
  public Integer getObjectId(final String string) throws SQLException {
    return getPackage().getSqlStatements().executeQuery(getModule(), getObjectName()+".getid",
        new QueryCallback<Integer>() {

          public Integer executeQuery(PreparedStatement ps) throws SQLException {
            ps.clearParameters();
            ps.setString(1, string);
            ResultSet rs = null;
            try {
              rs = ps.executeQuery();
              if (rs.next()) {
                return new Integer(rs.getInt(1));
              }
              return null;
            } finally {
              rs.close();
            }
          }

        });
  }


  public T get(final int id) throws SQLException {
    return getPackage().getSqlStatements().executeQuery(getModule(), getObjectName()+".get",
        new QueryCallback<T>() {

          public T executeQuery(PreparedStatement ps) throws SQLException {
            ps.clearParameters();
            ps.setInt(1, id);
            ResultSet rs = null;
            try {
              rs = ps.executeQuery();
              if (rs.next()) {
                return load(rs);
              }
              return null;
            } finally {
              rs.close();
            }

          }

        });
  }

  public void update(final T p) throws SQLException {
    if (p.getObjectId() == 0) {
      if (getPackage().getSqlStatements().executeUpdate(getModule(), getObjectName()+".insert",
          new UpdateCallback() {
            public int executeUpdate(PreparedStatement ps) throws SQLException {
              ps.clearParameters();
              save(p, ps, false);
              return ps.executeUpdate();
            }
          }) == 1) {
        setObjectId((DbObject) p);
      }

    } else {
      getPackage().getSqlStatements().executeUpdate(getModule(), getObjectName()+".update", new UpdateCallback() {
        public int executeUpdate(PreparedStatement ps) throws SQLException {
          ps.clearParameters();
          save(p, ps, true);
          return ps.executeUpdate();
        }
      });
    }
  }

  /**
   * @return
   */
  protected abstract String getObjectName();

  /**
   * @param p
   * @param ps
   * @param b
   * @throws SQLException 
   */
  protected abstract void save(T p, PreparedStatement ps, boolean b) throws SQLException;

  /**
   * @param rs
   * @return
   * @throws SQLException 
   */
  protected abstract T load(ResultSet rs) throws SQLException;
  
  
  /**
   * @param p
   * @param objectId
   * @throws SQLException 
   */
  protected void setObjectId(DbObject p) throws SQLException {
    p.setObjectId(getObjectId(p.getId()));
  }
  /**
   * @return
   */
  protected abstract String getModule();

  /**
   * @return
   */
  protected abstract DbPackage getPackage();

}
