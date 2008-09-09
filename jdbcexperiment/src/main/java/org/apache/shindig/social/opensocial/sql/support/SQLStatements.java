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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class SQLStatements {

  /**
   * 
   */
  private static final long serialVersionUID = -3021070070547417444L;
  private Map<String, SQLLoader> loaders;
  private DataSource datasource;

  /**
   * @throws SQLException
   * @throws IOException
   * 
   */
  @Inject
  public SQLStatements(DataSource ch, DbModules modules) throws SQLException, IOException {
    this.datasource = ch;
    loaders = new HashMap<String, SQLLoader>();
    for (DbModule module : modules) {
      SQLLoader l = new SQLLoader(module);
      loaders.put(module.getName(), l);
    }
  }

  public String get(String module, String name, Object[] replacements) {
    return loaders.get(module).get(name, replacements);
  }

  public int executeUpdate(String module, String name, UpdateCallback cb) throws SQLException {
    return executeUpdate(module, name, cb);
  }

  public int executeUpdate(String module, String name, Object[] replacements, UpdateCallback cb)
      throws SQLException {
    PreparedStatement ps = null;
    try {
      if ( datasource instanceof MasterSlaveCapable ) {
        MasterSlaveCapable ms = (MasterSlaveCapable) datasource;
        ms.startWriting();
      }
      Connection c = datasource.getConnection();
      ps = c.prepareStatement(get(module, name, replacements));
      return cb.executeUpdate(ps);
      
    } finally {
      ps.close();
    }
  }

  public <T> T executeQuery(String module, String name, QueryCallback<T> cb) throws SQLException {
    return executeQuery(module, name, null, cb);
  }

  public <T> T executeQuery(String module, String name, Object[] replacements, QueryCallback<T> cb)
      throws SQLException {
    PreparedStatement ps = null;
    try {
      Connection c = datasource.getConnection();
      ps = c.prepareStatement(get(module, name, replacements));
      return cb.executeQuery(ps);
    } finally {
      ps.close();
    }

  }


}
