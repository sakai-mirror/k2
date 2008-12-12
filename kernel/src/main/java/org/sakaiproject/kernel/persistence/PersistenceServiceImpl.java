package org.sakaiproject.kernel.persistence;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;

import org.sakaiproject.kernel.api.persistence.PersistenceService;

import com.google.inject.Inject;

public class PersistenceServiceImpl implements PersistenceService {
  private DataSource dataSource;
  private TransactionManager transactionManager;
  private EntityManager entityManager;

  @Inject
  public PersistenceServiceImpl(DataSource dataSource,
      TransactionManager transactionManager, EntityManager entityManager) {
    super();
    this.dataSource = dataSource;
    this.transactionManager = transactionManager;
    this.entityManager = entityManager;
  }

  public DataSource dataSource() {
    return dataSource;
  }

  public EntityManager entityManager() {
    return entityManager;
  }

  public TransactionManager transactionManager() {
    return transactionManager;
  }

}
