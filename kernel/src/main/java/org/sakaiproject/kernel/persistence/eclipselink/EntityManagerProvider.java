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
package org.sakaiproject.kernel.persistence.eclipselink;

import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_LEVEL;
import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_SESSION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_THREAD;
import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_TIMESTAMP;
import static org.eclipse.persistence.config.PersistenceUnitProperties.TARGET_SERVER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.TRANSACTION_TYPE;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.TargetServer;
import org.sakaiproject.kernel.api.persistence.DataSourceService;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.spi.PersistenceUnitTransactionType;

/**
 * Guice provider for {@link javax.persistence.EntityManager} as implemented by
 * Eclipselink.
 */
public class EntityManagerProvider implements Provider<EntityManager> {
  private static final Log LOG = LogFactory.getLog(Boolean.class);

  private static final String DB_MIN_WRITE = "eclipselink.write.min";
  private static final String DB_MIN_NUM_READ = "eclipselink.read.min";
  private static final String DB_UNITNAME = "jpa.unitname";
  private EntityManager entityManager;

  /**
   * Construct an EclipseLink entity manager provider.
   * 
   * @param minRead
   * @param minWrite
   * @param dataSourceService
   * @param unitName
   */
  @Inject
  public EntityManagerProvider(@Named(DB_MIN_NUM_READ) String minRead,
      @Named(DB_MIN_WRITE) String minWrite,
      DataSourceService dataSourceService, @Named(DB_UNITNAME) String unitName) {

    Map<String, Object> properties = new HashMap<String, Object>();

    // Ensure RESOURCE_LOCAL transactions is used.
    properties.put(TRANSACTION_TYPE,
        PersistenceUnitTransactionType.RESOURCE_LOCAL.name());

    // Configure the internal EclipseLink connection pool
    // properties.put(JDBC_DRIVER, dbDriver);
    // properties.put(JDBC_URL, dbUrl);
    // properties.put(JDBC_USER, dbUser);
    // properties.put(JDBC_PASSWORD, dbPassword);
    // properties.put(JDBC_READ_CONNECTIONS_MIN, minRead);
    // properties.put(JDBC_WRITE_CONNECTIONS_MIN, minWrite);

    // Configure logging. FINE ensures all SQL is shown
    properties.put(LOGGING_LEVEL, "FINE");
    properties.put(LOGGING_TIMESTAMP, "true");
    properties.put(LOGGING_THREAD, "false");
    properties.put(LOGGING_SESSION, "false");

    // Ensure that no server-platform is configured
    properties.put(TARGET_SERVER, TargetServer.None);

    properties.put(PersistenceUnitProperties.DDL_GENERATION,
        PersistenceUnitProperties.CREATE_ONLY);
    properties.put(PersistenceUnitProperties.DROP_JDBC_DDL_FILE, "drop.sql");
    properties
        .put(PersistenceUnitProperties.CREATE_JDBC_DDL_FILE, "create.sql");
    properties.put(PersistenceUnitProperties.DDL_GENERATION_MODE,
        PersistenceUnitProperties.DDL_BOTH_GENERATION);
    properties.put(dataSourceService.getType(), dataSourceService
        .getDataSource());

    // properties.put(PersistenceUnitProperties.SESSION_CUSTOMIZER,
    // EnableIntegrityChecker.class.getName());

    LOG.info("Starting connection manager with properties " + properties);

    EntityManagerFactory emFactory = Persistence.createEntityManagerFactory(
        unitName, properties);
    entityManager = emFactory.createEntityManager();
  }

  /**
   * {@inheritDoc}
   * 
   * @see com.google.inject.Provider#get()
   */
  public EntityManager get() {
    return entityManager;
  }

}