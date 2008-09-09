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
import com.google.inject.name.Named;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 
 */
public class  MasterSlaveDataSource implements DataSource {

  public static final String SLAVE_DATASOURCE = "slave-datasource";
  public static final String MASTER_DATASOURCE = "master-datasource";
  private DataSource master;
  private DataSource slave;
  ThreadLocal<Connection> connection = new ThreadLocal<Connection>();

  @Inject
  public MasterSlaveDataSource(@Named(MASTER_DATASOURCE) DataSource master, @Named(SLAVE_DATASOURCE) DataSource slave) {
    this.master = master;
    this.slave = slave;
  }

  /* (non-Javadoc)
   * @see org.apache.commons.dbcp.BasicDataSource#getConnection()
   */
  public Connection getConnection() throws SQLException {
    Connection c = connection.get();
    if ( c == null ) {
      c = new MasterSlaveConnection(master,slave,this);
      connection.set(c);
    }
    return c;
  }

  /* (non-Javadoc)
   * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
   */
  public Connection getConnection(String username, String password) throws SQLException {
    return null;
  }

  /* (non-Javadoc)
   * @see javax.sql.DataSource#getLogWriter()
   */
  public PrintWriter getLogWriter() throws SQLException {
    return master.getLogWriter();
  }

  /* (non-Javadoc)
   * @see javax.sql.DataSource#getLoginTimeout()
   */
  public int getLoginTimeout() throws SQLException {
    // TODO Auto-generated method stub
    return master.getLoginTimeout();
  }

  /* (non-Javadoc)
   * @see javax.sql.DataSource#setLogWriter(java.io.PrintWriter)
   */
  public void setLogWriter(PrintWriter out) throws SQLException {
    master.setLogWriter(out);
    slave.setLogWriter(out);
  }

  /* (non-Javadoc)
   * @see javax.sql.DataSource#setLoginTimeout(int)
   */
  public void setLoginTimeout(int seconds) throws SQLException {
    master.setLoginTimeout(seconds);
    slave.setLoginTimeout(seconds);
  }
  

}
