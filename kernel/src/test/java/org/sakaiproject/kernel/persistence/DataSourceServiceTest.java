/*******************************************************************************
 * Copyright 2008 Sakai Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.sakaiproject.kernel.persistence;

import com.google.inject.Guice;
import com.google.inject.Injector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.sakaiproject.kernel.api.persistence.DataSourceService;
import org.sakaiproject.kernel.persistence.dbcp.DataSourceServiceImpl;
import org.sakaiproject.kernel.util.FileUtil;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

public class DataSourceServiceTest {
  @Before
  public void beforeClass() throws Exception {
    File dbBase = new File("target/unittestdb");
    FileUtil.deleteAll(dbBase);
    dbBase = new File("target/testdb");
    FileUtil.deleteAll(dbBase);
  }

  @AfterClass
  public static void afterClass() {
  }

  @Test
  public void createConnection() throws Exception {
    // setup parameters
    String driverClassName = "org.apache.derby.jdbc.EmbeddedDriver";
    String url = "jdbc:derby:target/unittestdb;create=true";
    String username = "sa";
    String password = "";
    String validationQuery = "values(1)";
    boolean defaultReadOnly = false;
    boolean defaultAutoCommit = false;
    boolean poolPreparedStatements = false;

    // create connection
    DataSourceService dataSourceService = new DataSourceServiceImpl(
        driverClassName, url, username, password, validationQuery,
        defaultReadOnly, defaultAutoCommit, poolPreparedStatements);
    DataSource dataSource = dataSourceService.getDataSource();
    Connection conn = dataSource.getConnection();

    validateConnection(conn);
    testCRUD(conn);

    conn.rollback();
    conn.close();
  }

  @Test
  public void injectConnection() throws Exception {
    Injector injector = Guice.createInjector(new DataSourceModule());
    DataSource dataSource = injector.getInstance(DataSource.class);
    Connection conn = dataSource.getConnection();

    validateConnection(conn);
    testCRUD(conn);

    conn.rollback();
    conn.close();
  }

  private void validateConnection(Connection conn) throws Exception {
    assertNull(conn.getWarnings());
    PreparedStatement ps = conn.prepareStatement("values(1)");
    ResultSet rs = ps.executeQuery();
    assertTrue(rs.next());
    assertEquals(1, rs.getInt(1));
  }

  private void testCRUD(Connection conn) throws Exception {
    // create table
    Statement stmt = conn.createStatement();
    stmt
        .executeUpdate("create table Employees (Employee_ID INTEGER, Name VARCHAR(30))");
    stmt.close();

    // insert data
    stmt = conn.createStatement();
    stmt
        .executeUpdate("insert into Employees (Employee_ID, Name) values (1, 'Bob')");
    stmt.close();

    // look up that data
    stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("select count(*) from Employees");
    assertTrue(rs.next());
    assertEquals(1, rs.getInt(1));
    stmt.close();

    // delete the data
    stmt = conn.createStatement();
    stmt.executeUpdate("delete from Employees");
    stmt.close();
  }
}
