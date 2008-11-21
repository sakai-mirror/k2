package org.sakaiproject.kernel.persistence;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.persistence.DataSourceService;
import org.sakaiproject.kernel.internal.api.KernelInitializtionException;

import bitronix.tm.resource.jdbc.PoolingDataSource;

import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * <p>
 * Service to provide a data source for database connections.
 * </p>
 */
@Singleton
public class DataSourceServiceBitronixImpl implements DataSourceService,
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

    PoolingDataSource dataSource = new PoolingDataSource();
    dataSource.setClassName(driverClassName);
    dataSource.setUniqueName("sakai");
    dataSource.setMaxPoolSize(3);
    dataSource.getDriverProperties().setProperty("user", username);
    dataSource.getDriverProperties().setProperty("password", password);
    dataSource.getDriverProperties().setProperty("URL", url);
    dataSource.init();

    this.dataSource = dataSource;
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
