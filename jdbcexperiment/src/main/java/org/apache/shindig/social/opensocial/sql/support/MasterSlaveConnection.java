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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Map;

/**
 *
 */
public class MasterSlaveConnection implements Connection, MasterSlaveCapable {

  private static final Log LOG = LogFactory.getLog(MasterSlaveConnection.class);
  private DataSource slave;
  private DataSource master;
  private boolean onslave;

  /**
   * @param master
   * @param slave
   * @param masterSlaveDataSource
   */
  public MasterSlaveConnection(DataSource master, DataSource slave,
      MasterSlaveDataSource masterSlaveDataSource) {
    this.master = master;
    this.slave = slave;
    this.onslave = true;
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#clearWarnings()
   */
  public void clearWarnings() throws SQLException {
    getConnection().clearWarnings();
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#close()
   */
  public void close() throws SQLException {
    getConnection().close();
  }

  /**
   * @return
   * @throws SQLException 
   */
  private Connection getConnection() throws SQLException {
    if ( onslave ) {
      return slave.getConnection();
    } else {
      return master.getConnection();
    }
  }
  
  /* (non-Javadoc)
   * @see org.apache.shindig.social.opensocial.sql.MasterSlaveCapable#startWriting()
   */
  public void startWriting() {
    if ( onslave ) {
      try {
        slave.getConnection().close();
      } catch (SQLException e) {
        LOG.error("Failed to close datasource "+e.getMessage());
      }
    }
    onslave = false;
  }

  


  /* (non-Javadoc)
   * @see java.sql.Connection#commit()
   */
  public void commit() throws SQLException {
    getConnection().commit();
    onslave = true;
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#createStatement()
   */
  public Statement createStatement() throws SQLException {
    return getConnection().createStatement();
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#createStatement(int, int)
   */
  public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
    return getConnection().createStatement(resultSetType,resultSetConcurrency);
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#createStatement(int, int, int)
   */
  public Statement createStatement(int resultSetType, int resultSetConcurrency,
      int resultSetHoldability) throws SQLException {
    return getConnection().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#getAutoCommit()
   */
  public boolean getAutoCommit() throws SQLException {
    return getConnection().getAutoCommit();
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#getCatalog()
   */
  public String getCatalog() throws SQLException {
    return getConnection().getCatalog();
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#getHoldability()
   */
  public int getHoldability() throws SQLException {
    return getConnection().getHoldability();
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#getMetaData()
   */
  public DatabaseMetaData getMetaData() throws SQLException {
    return getConnection().getMetaData();
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#getTransactionIsolation()
   */
  public int getTransactionIsolation() throws SQLException {
    return getConnection().getTransactionIsolation();
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#getTypeMap()
   */
  public Map<String, Class<?>> getTypeMap() throws SQLException {
    return getConnection().getTypeMap();
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#getWarnings()
   */
  public SQLWarning getWarnings() throws SQLException {
    return getConnection().getWarnings();
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#isClosed()
   */
  public boolean isClosed() throws SQLException {
    return getConnection().isClosed();
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#isReadOnly()
   */
  public boolean isReadOnly() throws SQLException {
    return getConnection().isClosed();
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#nativeSQL(java.lang.String)
   */
  public String nativeSQL(String sql) throws SQLException {
    return getConnection().nativeSQL(sql);
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#prepareCall(java.lang.String)
   */
  public CallableStatement prepareCall(String sql) throws SQLException {
    return getConnection().prepareCall(sql);
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#prepareCall(java.lang.String, int, int)
   */
  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
      throws SQLException {
    return getConnection().prepareCall(sql, resultSetType, resultSetConcurrency);
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#prepareCall(java.lang.String, int, int, int)
   */
  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
      int resultSetHoldability) throws SQLException {
    return getConnection().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#prepareStatement(java.lang.String)
   */
  public PreparedStatement prepareStatement(String sql) throws SQLException {
    return getConnection().prepareStatement(sql);
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#prepareStatement(java.lang.String, int)
   */
  public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
    return getConnection().prepareStatement(sql, autoGeneratedKeys);
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#prepareStatement(java.lang.String, int[])
   */
  public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
    return getConnection().prepareStatement(sql, columnIndexes);
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#prepareStatement(java.lang.String, java.lang.String[])
   */
  public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
    return getConnection().prepareStatement(sql, columnNames);
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#prepareStatement(java.lang.String, int, int)
   */
  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
      throws SQLException {
    return getConnection().prepareStatement(sql, resultSetType, resultSetConcurrency);
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#prepareStatement(java.lang.String, int, int, int)
   */
  public PreparedStatement prepareStatement(String sql, int resultSetType,
      int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    return getConnection().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#releaseSavepoint(java.sql.Savepoint)
   */
  public void releaseSavepoint(Savepoint savepoint) throws SQLException {
    getConnection().releaseSavepoint(savepoint);
   }

  /* (non-Javadoc)
   * @see java.sql.Connection#rollback()
   */
  public void rollback() throws SQLException {
    getConnection().rollback();
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#rollback(java.sql.Savepoint)
   */
  public void rollback(Savepoint savepoint) throws SQLException {
    getConnection().rollback(savepoint);
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#setAutoCommit(boolean)
   */
  public void setAutoCommit(boolean autoCommit) throws SQLException {
    getConnection().setAutoCommit(autoCommit);
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#setCatalog(java.lang.String)
   */
  public void setCatalog(String catalog) throws SQLException {
    getConnection().setCatalog(catalog);
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#setHoldability(int)
   */
  public void setHoldability(int holdability) throws SQLException {
    getConnection().setHoldability(holdability);
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#setReadOnly(boolean)
   */
  public void setReadOnly(boolean readOnly) throws SQLException {
    getConnection().setReadOnly(readOnly);
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#setSavepoint()
   */
  public Savepoint setSavepoint() throws SQLException {
    return getConnection().setSavepoint();
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#setSavepoint(java.lang.String)
   */
  public Savepoint setSavepoint(String name) throws SQLException {
    return getConnection().setSavepoint(name);
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#setTransactionIsolation(int)
   */
  public void setTransactionIsolation(int level) throws SQLException {
     getConnection().setTransactionIsolation(level);
  }

  /* (non-Javadoc)
   * @see java.sql.Connection#setTypeMap(java.util.Map)
   */
  public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
    getConnection().setTypeMap(map);
  }


}
