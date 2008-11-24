package org.sakaiproject.kernel.persistence.dbcp;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.apache.commons.pool.impl.GenericKeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.sakaiproject.kernel.api.persistence.DataSourceService;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

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
public class DataSourceServiceImpl implements DataSourceService, Provider<DataSource> {

  private DataSource dataSource;

  /**
   * Construct a DBCP data source service.
   * 
   * @param driverClassName
   * @param url
   * @param username
   * @param password
   * @param validationQuery
   * @param defaultReadOnly
   * @param defaultAutoCommit
   * @param poolPreparedStatements
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  @Inject
  public DataSourceServiceImpl(@Named(JDBC_DRIVER_NAME) String driverClassName,
      @Named(JDBC_URL) String url, @Named(JDBC_USERNAME) String username,
      @Named(JDBC_PASSWORD) String password,
      @Named(JDBC_VALIDATION_QUERY) String validationQuery,
      @Named(JDBC_DEFAULT_READ_ONLY) boolean defaultReadOnly,
      @Named(JDBC_DEFAULT_AUTO_COMMIT) boolean defaultAutoCommit,
      @Named(JDBC_DEFAULT_PREPARED_STATEMENTS) boolean poolPreparedStatements)
      throws ClassNotFoundException, SQLException {

    Class.forName(driverClassName);
    Driver driver = DriverManager.getDriver(url);

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

    @SuppressWarnings("unused")
    PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(
        connectionFactory, connectionPool, statementPoolFactory,
        validationQuery, defaultReadOnly, defaultAutoCommit);
    dataSource = new PoolingDataSource(connectionPool);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.persistence.DataSourceService#getDataSource()
   */
  public DataSource getDataSource() {
    return dataSource;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.persistence.DataSourceService#getType()
   */
  public String getType() {
    return DataSourceService.NON_JTA_DATASOURCE;
  }

  /**
   * {@inheritDoc}
   * 
   * @see com.google.inject.Provider#get()
   */
  public DataSource get() {
    return dataSource;
  }

}
