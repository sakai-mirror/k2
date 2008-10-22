/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2007, 2008 Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.kernel.jcr.jackrabbit;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.apache.jackrabbit.core.nodetype.NodeTypeManagerImpl;
import org.sakaiproject.kernel.component.ResourceLoader;
import org.sakaiproject.kernel.jcr.api.internal.StartupAction;
import org.sakaiproject.kernel.jcr.jackrabbit.persistance.BundleDbSharedPersistenceManager;
import org.sakaiproject.kernel.jcr.jackrabbit.persistance.DerbySharedPersistenceManager;
import org.sakaiproject.kernel.jcr.jackrabbit.persistance.MSSqlSharedPersistenceManager;
import org.sakaiproject.kernel.jcr.jackrabbit.persistance.MySqlSharedPersistenceManager;
import org.sakaiproject.kernel.jcr.jackrabbit.persistance.Oracle9SharedPersistenceManager;
import org.sakaiproject.kernel.jcr.jackrabbit.persistance.OracleSharedPersistenceManager;
import org.sakaiproject.kernel.jcr.jackrabbit.sakai.SakaiJCRCredentials;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jcr.NamespaceException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;

public class RepositoryBuilder  {

  private static final Log log = LogFactory.getLog(RepositoryBuilder.class);

  private static final String DB_URL = "\\$\\{db.url\\}";

  private static final String DB_USER = "\\$\\{db.user\\}";

  private static final String DB_PASS = "\\$\\{db.pass\\}";

  private static final String DB_DRIVER = "\\$\\{db.driver\\}";

  private static final String CONTENT_ID_DB = "\\$\\{content.filesystem\\}";

  private static final String USE_SHARED_FS_BLOB_STORE = "\\$\\{content.shared\\}";

  private static final String SHARED_CONTENT_BLOB_LOCATION = "\\$\\{content.shared.location\\}";

  private static final String DB_DIALECT = "\\$\\{db.dialect\\}";

  private static final String CLUSTER_NODE_ID = "\\$\\{sakai.cluster\\}";

  private static final String JOURNAL_LOCATION = "\\$\\{journal.location\\}";

  private static final String PERSISTANCE_MANAGER = "\\$\\{persistance.manager.class\\}";

  /*
   * These constants are the default Sakai Properties we will use if the values
   * are not custom injected.
   */
  public static final String DEFAULT_DBDIALECT_PROP = "vendor@org.sakaiproject.db.api.SqlService";

  public static final String DEFAULT_DBUSER_PROP = "username@javax.sql.BaseDataSource";

  public static final String DEFAULT_DBPASS_PROP = "password@javax.sql.BaseDataSource";

  public static final String DEFAULT_DBDRIVER_PROP = "driverClassName@javax.sql.BaseDataSource";

  public static final String DEFAULT_DBURL_PROP = "url@javax.sql.BaseDataSource";

  public static final String DEFAULT_DSPERSISTMNGR_PROP = "dataSourcePersistanceManager@org.sakaiproject.kernel.api.jcr.JCRService.repositoryBuilder";

  private static final String BASE_NAME = "@org.sakaiproject.kernel.jcr.jackrabbit.RepositoryBuilder";

  private static final String DB_DIALECT_NAME = "dbDialect" + BASE_NAME;

  private static final String DB_USER_NAME = "dbUser" + BASE_NAME;

  private static final String DB_PASS_NAME = "dbPassword" + BASE_NAME;

  private static final String DB_DRIVER_NAME = "dbDriver" + BASE_NAME;

  private static final String DB_URL_NAME = "dbUrl" + BASE_NAME;

  private static final String DB_CONTENTONFILESYSTEM_NAME = "contentOnFilesystem"
      + BASE_NAME;

  private static final String DB_USESHARED_FS_BLOB_NAME = "useSharedFSBlob"
      + BASE_NAME;

  private static final String DB_SHARED_FS_BLOB_LOCATION_NAME = "sharedFSBlobLocation"
      + BASE_NAME;

  private static final String SERVER_ID = "serverId";

  private static final String SERVER_LOCATION_NAME = "sharedLocation"
      + BASE_NAME;

  private static final String NAMESPACES_MAP_NAME = "namespaces" + BASE_NAME;

  private static final String REPOSITORY_HOME_NAME = "repositoryHome"
      + BASE_NAME;

  private static final String REPOSITORY_CONFIG_LOCATION = "repositoryConfigTemplate"
      + BASE_NAME;

  private static final String NODE_TYPE_CONFIGURATION_NAME = "nodeTypeConfiguration"
      + BASE_NAME;

  private static final String STARTUP_ACTIONS_NAME = "startupActions"+BASE_NAME;

  private RepositoryImpl repository;

  // private String repositoryConfig;

  // private String repositoryHome;

  // private String nodeTypeConfiguration;

  // private boolean dataSourcePersistanceManager = true;

  //private List<StartupAction> startupActions;

  // private String clusterNodeId;

  // private String journalLocation;

  // private String persistanceManagerClass;

  private static Map<String, String> vendors = new HashMap<String, String>();

  static {
    // TODO, could map to special Persistance managers to make use of the
    // Oracle Optimised version
    vendors.put("mysql", "mysql");
    vendors.put("oracle", "oracle");
    vendors.put("oracle9", "oracle9");
    vendors.put("mssql", "mssql");
    vendors.put("hsqldb", "default");
    vendors.put("derby", "derby");
  }

  private static Map<String, String> persistanceManagers = new HashMap<String, String>();
  static {
    persistanceManagers.put("mysql", MySqlSharedPersistenceManager.class
        .getName());
    persistanceManagers.put("oracle", OracleSharedPersistenceManager.class
        .getName());
    persistanceManagers.put("oracle9", Oracle9SharedPersistenceManager.class
        .getName());
    persistanceManagers.put("mssql", MSSqlSharedPersistenceManager.class
        .getName());
    persistanceManagers.put("derby", DerbySharedPersistenceManager.class
        .getName());
    persistanceManagers.put("default", BundleDbSharedPersistenceManager.class
        .getName());
  }

  public Repository getInstance() {
    return repository;
  }

  @Inject
  public RepositoryBuilder(@Named(DB_DIALECT_NAME) String dbDialect,
      @Named(DB_USER_NAME) String dbUser, @Named(DB_PASS_NAME) String dbPass,
      @Named(DB_DRIVER_NAME) String dbDriver, @Named(DB_URL_NAME) String dbURL,
      @Named(DB_CONTENTONFILESYSTEM_NAME) String contentOnFilesystem,
      @Named(DB_USESHARED_FS_BLOB_NAME) String useSharedFSBlobStore,
      @Named(DB_SHARED_FS_BLOB_LOCATION_NAME) String sharedFSBlobLocation,
      @Named(SERVER_ID) String serverId,
      @Named(SERVER_LOCATION_NAME) String journalLocation,
      @Named(NAMESPACES_MAP_NAME) Map<String, String> namespaces,
      @Named(REPOSITORY_HOME_NAME) String repositoryHome,
      @Named(REPOSITORY_CONFIG_LOCATION) String repositoryConfigTemplate,
      @Named(NODE_TYPE_CONFIGURATION_NAME) String nodeTypeConfiguration,
      @Named(STARTUP_ACTIONS_NAME) List<StartupAction> startupActions) {

    dbURL = dbURL.replaceAll("&", "&amp;");

    boolean error = false;
    try {
      String persistanceManagerClass = persistanceManagers.get(dbDialect);
      log.info(MessageFormat.format("\nJCR Repository Config is \n"
          + "\trepositoryConfig = {0} \n" + "\tdbURL = {1}\n"
          + "\tdbUser = {2} \n" + "\tdbDriver = {4} \n"
          + "\tdbDialect = {5} \n" + "\trepository Home = {6}\n"
          + "\tcontentOnFilesystem = {7}\n"
          + "\tpersistanceManageerClass= {8}\n", new Object[] {
          repositoryConfigTemplate, dbURL, dbUser, dbPass, dbDriver, dbDialect,
          repositoryHome, contentOnFilesystem, persistanceManagerClass }));

      String contentStr = ResourceLoader.readResource(repositoryConfigTemplate);

      contentStr = contentStr.toString().replaceAll(DB_URL, dbURL);
      contentStr = contentStr.replaceAll(DB_USER, dbUser);
      contentStr = contentStr.replaceAll(DB_PASS, dbPass);
      contentStr = contentStr.replaceAll(DB_DRIVER, dbDriver);
      contentStr = contentStr.replaceAll(DB_DIALECT, dbDialect);
      contentStr = contentStr.replaceAll(CONTENT_ID_DB, contentOnFilesystem);
      contentStr = contentStr.replaceAll(USE_SHARED_FS_BLOB_STORE,
          useSharedFSBlobStore);
      contentStr = contentStr.replaceAll(SHARED_CONTENT_BLOB_LOCATION,
          sharedFSBlobLocation);
      contentStr = contentStr.replaceAll(CLUSTER_NODE_ID, serverId);
      contentStr = contentStr.replaceAll(JOURNAL_LOCATION, journalLocation);
      contentStr = contentStr.replaceAll(PERSISTANCE_MANAGER,
          persistanceManagerClass);

      if (log.isDebugEnabled())
        log.debug("Repositroy Config is \n" + contentStr);

      ByteArrayInputStream bais = new ByteArrayInputStream(contentStr
          .getBytes());
      try {

        RepositoryConfig rc = RepositoryConfig.create(bais, repositoryHome);
        repository = RepositoryImpl.create(rc);
        setup(namespaces, nodeTypeConfiguration, startupActions);

      } finally {
        bais.close();
      }

    } catch (Throwable ex) {
      log.error("init() failure: " + ex);
      error = true;
    } finally {
      if (error) {
        throw new RuntimeException(
            "Fatal error initialising JCRService... (see previous logged ERROR for details)");

      }
    }
    log.info("Repository Builder passed init ");
  }

  public void stop() {
    if (repository != null) {
      log.info("Start repository shutdown  ");
      try {
        repository.shutdown();
      } catch (Exception ex) {
        log.warn("Repository Shutdown failed, this may be normal "
            + ex.getMessage());
      }
      log.info("Shutdown of repository complete  ");
      repository = null;
    }

  }

  private void setup(Map<String, String> namespaces,
      String nodeTypeConfiguration, List<StartupAction> startupActions) throws RepositoryException, IOException {
    SakaiJCRCredentials ssp = new SakaiJCRCredentials();
    Session s = repository.login(ssp);
    try {
      Workspace w = s.getWorkspace();
      NamespaceRegistry reg = w.getNamespaceRegistry();
      for (Entry<String, String> e : namespaces.entrySet()) {
        try {
          reg.getPrefix(e.getValue());
        } catch (NamespaceException nex) {
          try {
            log.info("Registering Namespage [" + e.getKey() + "] ["
                + e.getValue() + "]");
            reg.registerNamespace(e.getKey(), e.getValue());
          } catch (Exception ex) {
            throw new RuntimeException("Failed to register namespace prefix ("
                + e.getKey() + ") with uri (" + e.getValue()
                + ") in workspace: " + w.getName(), ex);
          }
        }
      }
      try {
        NodeTypeManagerImpl ntm = (NodeTypeManagerImpl) w.getNodeTypeManager();
        ntm.registerNodeTypes(this.getClass().getResourceAsStream(
            nodeTypeConfiguration), "text/xml");
      } catch (Exception ex) {
        log
            .info("Exception Loading Types, this is expected for all loads after the first one: "
                + ex.getMessage()
                + "(this message is here because Jackrabbit does not give us a good way to detect that the node types are already added)");
      }
      if (startupActions != null) {
        for (Iterator<StartupAction> i = startupActions.iterator(); i.hasNext();) {
          i.next().startup(s);
        }
      }

      s.save();
    } finally {
      s.logout();
    }

  }


}
