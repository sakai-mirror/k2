package org.sakaiproject.kernel.api.persistence;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;

public interface PersistenceService {
  DataSource dataSource();
  TransactionManager transactionManager();
  EntityManager entityManager();
}
