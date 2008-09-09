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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

/**
 * 
 */
public class DDLLoader {

  private static final Log LOG = LogFactory.getLog(DDLLoader.class);
  private static final String COMMENT = "--";
  private DataSource datasource;
  private DbModules modules;

  @Inject
  public DDLLoader(DataSource datasource, DbModules modules) {
    this.datasource = datasource;
    this.modules = modules;
  }

  @SuppressWarnings("unchecked")
  public void init() throws SQLException, IOException {
    String script = null;

    int nmodules = 0;
    for (DbModule module : modules) {

      if ( datasource instanceof MasterSlaveCapable ) {
        MasterSlaveCapable ms = (MasterSlaveCapable) datasource;
        ms.startWriting();
      }
      Connection c = datasource.getConnection();
      Statement s = null;
      BufferedReader reader = null;
      try {
        s = c.createStatement();
        script = module.getDdlScript();
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(script);
        if (in == null) {
          throw new IOException("Cant find DDL at " + script);
        }
        LOG.info("Loading Script " + script);
        reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder statement = new StringBuilder();
        int failed = 0;
        int total = 0;
        while (true) {
          String line = reader.readLine();
          if (line == null) {
            break;
          }
          line = line.trim();
          if (!line.startsWith(COMMENT)) {
            if (line.endsWith(";")) {
              statement.append(line.substring(0, line.length() - 1));
              boolean ok = safeExecute(c, s, statement.toString());
              if (!ok && total == 0) {
                LOG.info("Ignoring Script " + script
                    + " as first statement failed, indicating script has been run already");
                break;
              }
              if (!ok) {
                failed++;
              }
              total++;
              statement.delete(0, statement.length());
            } else {
              statement.append(line);
            }
          }
        }
        LOG.info("Script commplete, processed " + total + " of which " + failed + " failed");
      } catch (IOException ioex) {
        LOG.error("Failed to execute create script " + script, ioex);
      } catch (SQLException sqlex) {
        LOG.error("Failed to execute create script " + script, sqlex);

      } finally {
        try {
          s.close();
        } catch (Exception ex2) {

        }
        try {
          c.close();
        } catch (Exception ex2) {

        }
        try {
          reader.close();
        } catch (Exception ex2) {

        }
      }
    }
    if (nmodules == 0) {
      LOG.warn("No DDL was executed for " + modules);
    } else {
      LOG.info("Loaded " + nmodules + " modules");
    }

  }

  private boolean safeExecute(Connection c, Statement s, String sql) {

    try {
      c.clearWarnings();
      LOG.info("Executing " + sql);
      s.execute(sql);
      return true;
    } catch (SQLException sqlex) {

      LOG.info("Failed to execute " + sql + " cause :" + sqlex.getMessage());
      try {
        SQLWarning warning = c.getWarnings();
        if (warning != null) {
          LOG.info("SQL Warning: " + warning.getMessage());
        }
      } catch (Exception e) {
        LOG.warn("Failed to get Warning " + e.getMessage());
      }
      return false;
    }
  }

}
