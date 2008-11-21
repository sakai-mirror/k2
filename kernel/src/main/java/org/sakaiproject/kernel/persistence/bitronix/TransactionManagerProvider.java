package org.sakaiproject.kernel.persistence.bitronix;

import bitronix.tm.TransactionManagerServices;

import com.google.inject.Provider;

import javax.transaction.TransactionManager;

public class TransactionManagerProvider implements Provider<TransactionManager> {
  private TransactionManager transactionManager;
  
  /**
   * 
   */
  public TransactionManagerProvider() {
   transactionManager = TransactionManagerServices.getTransactionManager();
  }

  /**
   * {@inheritDoc}
   * @see com.google.inject.Provider#get()
   */
  public TransactionManager get() {
    return transactionManager;
  }

}
