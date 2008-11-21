package org.sakaiproject.kernel.api.persistence;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

public interface TransactionService {
  public TransactionManager getTransactionManager();

  public Transaction getTransaction();
}
