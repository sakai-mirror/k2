package org.sakaiproject.kernel.persistence;

import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.sakaiproject.kernel.api.persistence.TransactionService;

import bitronix.tm.TransactionManagerServices;

import com.google.inject.Provider;

public class TransactionServiceImpl implements TransactionService,
    Provider<Transaction> {
  private TransactionManager tm;

  public TransactionManager getTransactionManager() {
    if (tm == null) {
      tm = TransactionManagerServices.getTransactionManager();
    }
    return tm;
  }

  public Transaction getTransaction() {
    Transaction t = null;
    try {
      t = getTransactionManager().getTransaction();
    }
    catch (SystemException e) {
      // TODO need to do something with this.
    }
    return t;
  }

  public Transaction get() {
    return getTransaction();
  }
}
