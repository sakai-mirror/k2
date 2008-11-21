package org.sakaiproject.kernel.api.persistence;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * Service to provide data sources.
 */
public interface DataSourceService {
  DataSource dataSource();
  Connection connection() throws SQLException;
}
