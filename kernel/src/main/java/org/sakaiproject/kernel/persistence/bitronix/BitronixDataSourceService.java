package org.sakaiproject.kernel.persistence.bitronix;

import bitronix.tm.resource.jdbc.PoolingDataSource;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.sakaiproject.kernel.api.persistence.DataSourceService;

import javax.sql.DataSource;

/**
 * <p>
 * Service to provide a data source for database connections.
 * </p>
 */
@Singleton
public class BitronixDataSourceService implements DataSourceService {
  
  /**
   * The Datasource 
   */
  private PoolingDataSource dataSource;

  
  /**
   * Construct a Bitronix Datasource Provider
   * @param driverClassName
   * @param url
   * @param username
   * @param password
   * @param validationQuery
   * @param defaultReadOnly
   * @param defaultAutoCommit
   * @param poolPreparedStatements
   */
  @Inject
  public BitronixDataSourceService(
      @Named(JDBC_DRIVER_NAME) String driverClassName, 
      @Named(JDBC_URL) String url,
      @Named(JDBC_USERNAME) String username,
      @Named(JDBC_PASSWORD) String password,
      @Named(JDBC_VALIDATION_QUERY) String validationQuery,
      @Named(JDBC_DEFAULT_READ_ONLY) boolean defaultReadOnly,
      @Named(JDBC_DEFAULT_AUTO_COMMIT) boolean defaultAutoCommit,
      @Named(JDBC_DEFAULT_PREPARED_STATEMENTS) boolean poolPreparedStatements) {

    dataSource = new PoolingDataSource();
    dataSource.setClassName(driverClassName);
    dataSource.setUniqueName("sakai");
    dataSource.setMaxPoolSize(3);
    dataSource.getDriverProperties().setProperty("user", username);
    dataSource.getDriverProperties().setProperty("password", password);
    dataSource.getDriverProperties().setProperty("URL", url);
    dataSource.init();

  }


  /**
   * {@inheritDoc}
   * @see com.google.inject.Provider#get()
   */
  public DataSource get() {
    return dataSource;
  }


  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.persistence.DataSourceService#getDataSource()
   */
  public DataSource getDataSource() {
    return dataSource;
  }


  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.persistence.DataSourceService#getType()
   */
  public String getType() {
    // TODO Auto-generated method stub
    return DataSourceService.JTA_DATASOURCE;
  }

}
