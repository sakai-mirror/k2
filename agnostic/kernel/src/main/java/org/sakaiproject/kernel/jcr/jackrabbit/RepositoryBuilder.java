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
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import com.thoughtworks.xstream.XStream;

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
import java.io.InputStream;
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

@Singleton
public class RepositoryBuilder {

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
  /*
   * public static final String DEFAULT_DBDIALECT_PROP =
   * "vendor@org.sakaiproject.db.api.SqlService";
   * 
   * public static final String DEFAULT_DBUSER_PROP =
   * "username@javax.sql.BaseDataSource";
   * 
   * public static final String DEFAULT_DBPASS_PROP =
   * "password@javax.sql.BaseDataSource";
   * 
   * public static final String DEFAULT_DBDRIVER_PROP =
   * "driverClassName@javax.sql.BaseDataSource";
   * 
   * public static final String DEFAULT_DBURL_PROP =
   * "url@javax.sql.BaseDataSource";
   */
  public static final String DEFAULT_DSPERSISTMNGR_PROP = "dataSourcePersistanceManager@org.sakaiproject.kernel.api.jcr.JCRService.repositoryBuilder";

  private static final String BASE_NAME = "@org.sakaiproject.kernel.jcr.jackrabbit.RepositoryBuilder";

  public static final String NAME_DB_DIALECT = "dbDialect" + BASE_NAME;

  public static final String NAME_DB_USER = "dbUser" + BASE_NAME;

  public static final String NAME_DB_PASS = "dbPassword" + BASE_NAME;

  public static final String NAME_DB_DRIVER = "dbDriver" + BASE_NAME;

  public static final String NAME_DB_URL = "dbUrl" + BASE_NAME;

  public static final String NAME_DB_CONTENTONFILESYSTEM = "contentOnFilesystem"
      + BASE_NAME;

  public static final String NAME_DB_USESHARED_FS_BLOB = "useSharedFSBlob"
      + BASE_NAME;

  public static final String NAME_DB_SHARED_FS_BLOB_LOCATION = "sharedFSBlobLocation"
      + BASE_NAME;

  public static final String NAME_SERVER_ID = "serverId";

  public static final String NAME_SERVER_LOCATION = "sharedLocation"
      + BASE_NAME;

  public static final String NAME_NAMESPACES_MAP = "namespaces" + BASE_NAME;

  public static final String NAME_REPOSITORY_HOME = "repositoryHome"
      + BASE_NAME;

  public static final String NAME_REPOSITORY_CONFIG_LOCATION = "repositoryConfigTemplate"
      + BASE_NAME;

  public static final String NAME_NODE_TYPE_CONFIGURATION = "nodeTypeConfiguration"
      + BASE_NAME;

  public static final String NAME_STARTUP_ACTIONS = "startupActions"
      + BASE_NAME;

  private static final ThreadLocal<Injector> injectorHolder = new ThreadLocal<Injector>();

  private RepositoryImpl repository;

  // private String repositoryConfig;

  // private String repositoryHome;

  // private String nodeTypeConfiguration;

  // private boolean dataSourcePersistanceManager = true;

  // private List<StartupAction> startupActions;

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
  public RepositoryBuilder(@Named(NAME_DB_DIALECT) String dbDialect,
      @Named(NAME_DB_USER) String dbUser, @Named(NAME_DB_PASS) String dbPass,
      @Named(NAME_DB_DRIVER) String dbDriver, @Named(NAME_DB_URL) String dbURL,
      @Named(NAME_DB_CONTENTONFILESYSTEM) String contentOnFilesystem,
      @Named(NAME_DB_USESHARED_FS_BLOB) String useSharedFSBlobStore,
      @Named(NAME_DB_SHARED_FS_BLOB_LOCATION) String sharedFSBlobLocation,
      @Named(NAME_SERVER_ID) String serverId,
      @Named(NAME_SERVER_LOCATION) String journalLocation,
      @Named(NAME_REPOSITORY_HOME) String repositoryHome,
      @Named(NAME_REPOSITORY_CONFIG_LOCATION) String repositoryConfigTemplate,
      @Named(NAME_NODE_TYPE_CONFIGURATION) String nodeTypeConfiguration,
      @Named(NAME_NAMESPACES_MAP) String namespacesConfiguration,
      @Named(NAME_STARTUP_ACTIONS) List<StartupAction> startupActions,
      Injector injector)
      throws IOException, RepositoryException {

    dbURL = dbURL.replaceAll("&", "&amp;");

    String persistanceManagerClass = persistanceManagers.get(dbDialect);
    log.info(MessageFormat.format("\nJCR Repository Config is \n"
        + "\trepositoryConfig = {0} \n" + "\tdbURL = {1}\n"
        + "\tdbUser = {2} \n" + "\tdbDriver = {4} \n" + "\tdbDialect = {5} \n"
        + "\trepository Home = {6}\n" + "\tcontentOnFilesystem = {7}\n"
        + "\tpersistanceManageerClass= {8}\n", new Object[] {
        repositoryConfigTemplate, dbURL, dbUser, dbPass, dbDriver, dbDialect,
        repositoryHome, contentOnFilesystem, persistanceManagerClass }));

    String contentStr = ResourceLoader.readResource(repositoryConfigTemplate,
        this.getClass().getClassLoader());

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

    ByteArrayInputStream bais = new ByteArrayInputStream(contentStr.getBytes());
    try {

      injectorHolder.set(injector);
      RepositoryConfig rc = RepositoryConfig.create(bais, repositoryHome);
      repository = RepositoryImpl.create(rc);
      
      Runtime.getRuntime().addShutdownHook(new Thread(){
        /**
         * {@inheritDoc}
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
          RepositoryBuilder.this.stop();
        }
      });
      setup(namespacesConfiguration, nodeTypeConfiguration, startupActions);

    } finally {
      injectorHolder.set(null);
      bais.close();
    }

    log.info("Repository Builder passed init ");
  }

  public void stop() {
    if (repository != null) {
      try {
        
        repository.shutdown();
        log.info("An A No current connection exception from the version manager is normal, if the version manager hasnt been used");
      } catch (Exception ex) {
        log.warn("Repository Shutdown failed, this may be normal "
            + ex.getMessage());
      }
      repository = null;
    }

  }

  @SuppressWarnings("unchecked")
  private void setup(String namespacesConfiguration,
      String nodeTypeConfiguration, List<StartupAction> startupActions)
      throws RepositoryException, IOException {
    SakaiJCRCredentials ssp = new SakaiJCRCredentials();
    Session s = repository.login(ssp);
    try {
      Workspace w = s.getWorkspace();
      NamespaceRegistry reg = w.getNamespaceRegistry();

      XStream parser = new XStream();
      parser.addDefaultImplementation(HashMap.class, Map.class);
      InputStream in = null;
      Map<String, String> namespaces = new HashMap<String, String>();
      try {
        in = ResourceLoader.openResource(namespacesConfiguration, this
            .getClass().getClassLoader());
        namespaces = (Map<String, String>) parser.fromXML(in, namespaces);
      } finally {
        try {
          in.close();
        } catch (Exception ex) {

        }
      }
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
        in = ResourceLoader.openResource(nodeTypeConfiguration, this.getClass()
            .getClassLoader());
        NodeTypeManagerImpl ntm = (NodeTypeManagerImpl) w.getNodeTypeManager();
        ntm.registerNodeTypes(in, "text/xml");
      } catch (Exception ex) {
        log
            .info("Exception Loading Types, this is expected for all loads after the first one: "
                + ex.getMessage()
                + "(this message is here because Jackrabbit does not give us a good way to detect that the node types are already added)");
      } finally {
        try {
          in.close();
        } catch (Exception ex) {

        }
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

  /**
   * @return
   */
  public static Injector getStartupInjector() {
    return injectorHolder.get();
  }


}
