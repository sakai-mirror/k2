package org.sakaiproject.kernel.api.persistence;

import javax.sql.DataSource;

/**
 * Service to provide data sources.
 */
public interface DataSourceService {

  public static final String JDBC_DRIVER_NAME = "jdbc.driver";
  public static final String JDBC_URL = "jdbc.url";
  public static final String JDBC_USERNAME = "jdbc.username";
  public static final String JDBC_PASSWORD = "jdbc.password";
  public static final String JDBC_VALIDATION_QUERY = "jdbc.validation";
  public static final String JDBC_DEFAULT_READ_ONLY = "jdbc.defaultReadOnly";
  public static final String JDBC_DEFAULT_AUTO_COMMIT = "jdbc.defaultAutoCommit";
  public static final String JDBC_DEFAULT_PREPARED_STATEMENTS = "jdbc.defaultPreparedStatement";
  /**
   * Standard JPA JTA DataSource name.
   */
  public static final String JTA_DATASOURCE = "javax.persistence.jtaDataSource";

  /**
   * Standard JPA non-JTA DataSource name.
   */
  public static final String NON_JTA_DATASOURCE = "javax.persistence.nonJtaDataSource";

  /**
   * @return
   */
  DataSource getDataSource();

  /**
   * @return
   */
  String getType();
}
