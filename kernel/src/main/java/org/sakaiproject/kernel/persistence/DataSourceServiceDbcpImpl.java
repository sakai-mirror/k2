package org.sakaiproject.kernel.persistence;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.apache.commons.pool.impl.GenericKeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.sakaiproject.kernel.api.persistence.DataSourceService;
import org.sakaiproject.kernel.internal.api.KernelInitializtionException;

import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * <p>
 * Service to provide a data source for database connections.
 * </p>
 * The implementation is based on the javadoc from DBCP which reads like the
 * following:<br/>
 * The {@link org.apache.commons.dbcp.PoolingDataSource} uses an underlying
 * {@link org.apache.commons.pool.ObjectPool} to create and store its
 * java.sql.Connection.<br/>
 * To create a {@link org.apache.commons.pool.ObjectPool}, you'll need a
 * {@link org.apache.commons.pool.PoolableObjectFactory} that creates the actual
 * {@link java.sql.Connection}s. That's what
 * {@link org.apache.commons.dbcp.PoolableConnectionFactory} is for.<br/>
 * To create the {@link org.apache.commons.dbcp.PoolableConnectionFactory} ,
 * you'll need at least two things:
 * <ol>
 * <li>A {@link org.apache.commons.dbcp.ConnectionFactory} from which the actual
 * database {@link java.sql.Connection}s will be obtained.</li>
 * <li>An empty and factory-less {@link org.apache.commons.pool.ObjectPool} in
 * which the {@link java.sql.Connection}s will be stored.</li>
 * </ol>
 * When you pass an {@link org.apache.commons.pool.ObjectPool} into the
 * {@link org.apache.commons.dbcp.PoolableConnectionFactory} , it will
 * automatically register itself as the
 * {@link org.apache.commons.pool.PoolableObjectFactory} for that pool.<br/>
 * You can optionally provide a
 * {@link org.apache.commons.pool.KeyedObjectPoolFactory} that will be used to
 * create {@link org.apache.commons.pool.KeyedObjectPool}s for pooling
 * {@link java.sql.PreparedStatement}s for each {@link java.sql.Connection}.
 */
@Singleton
public class DataSourceServiceDbcpImpl implements DataSourceService,
    Provider<DataSource> {
  private Log log = LogFactory.getLog(DataSourceService.class);

  private DataSource dataSource;

  public void init() throws KernelInitializtionException {
    String driverClassName = "jdbc.driver.name";
    String url = "jdbc:some:connect:string";
    String username = "";
    String password = "";
    String validationQuery = "select 1 from dual";
    boolean defaultReadOnly = false;
    boolean defaultAutoCommit = true;
    boolean poolPreparedStatements = false;

    // Load the JDBC driver class
    if (driverClassName != null) {
      try {
        Class.forName(driverClassName);
      } catch (ClassNotFoundException e) {
        String message = "Cannot load JDBC driver class '" + driverClassName
            + "': " + e.getMessage();
        log.error(message, e);
        throw new KernelInitializtionException(message, e);
      }
    }

    // Create a JDBC driver instance
    Driver driver = null;
    try {
      driver = DriverManager.getDriver(url);
    } catch (SQLException e) {
      String message = "Cannot create JDBC driver of class '" + driverClassName
          + "' for connect URL '" + url + "': " + e.getMessage();
      log.error(message, e);
      throw new KernelInitializtionException(message, e);
    }

    Properties props = new Properties();
    props.put("user", username);
    props.put("password", password);

    GenericObjectPool connectionPool = new GenericObjectPool(null);
    ConnectionFactory connectionFactory = new DriverConnectionFactory(driver,
        url, props);

    // Set up statement pool, if desired
    GenericKeyedObjectPoolFactory statementPoolFactory = null;
    if (poolPreparedStatements) {
      int maxActive = -1;
      byte whenExhaustedAction = GenericKeyedObjectPool.WHEN_EXHAUSTED_FAIL;
      long maxWait = 0L;
      int maxIdlePerKey = 1;
      int maxOpenPreparedStatements = 0;
      statementPoolFactory = new GenericKeyedObjectPoolFactory(null, maxActive,
          whenExhaustedAction, maxWait, maxIdlePerKey,
          maxOpenPreparedStatements);
    }

    PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(
        connectionFactory, connectionPool, statementPoolFactory,
        validationQuery, defaultReadOnly, defaultAutoCommit);
    dataSource = new PoolingDataSource(connectionPool);
  }

  public DataSource dataSource() {
    if (dataSource == null) {
      try {
        init();
      } catch (KernelInitializtionException e) {
        log.error(e.getMessage(), e);
      }
    }
    return dataSource;
  }

  public Connection connection() throws SQLException {
    return dataSource().getConnection();
  }

  public DataSource get() {
    return dataSource();
  }
}
