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

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 
 */
public class DbModules extends ArrayList<DbModule> {

  /**
   * 
   */
  private static final long serialVersionUID = -7974854399949406337L;
  private static final Log LOG = LogFactory.getLog(DbModules.class);

  /**
   * @throws IOException 
   * 
   */
  @Inject
  public DbModules(@Named("db.modules.path")
  String modules, @Named("db.type") String dbType ) throws IOException {

    String[] moduleList = modules.split(";");
    for (String module : moduleList) {
      URL configurl = this.getClass().getClassLoader().getResource(module);
      if (configurl == null) {
        LOG.error("Failed to load module from " + module);
      } else {
        LOG.info("Loading Module List from " + configurl.toString());
        DbModule config = new DbModule(dbType); 
        config.load(configurl.openStream());
        add(config);
      }
    }
  }
}
