package org.sakaiproject.kernel.persistence.geronimo;

import javax.transaction.TransactionManager;
import javax.transaction.xa.XAException;

import org.apache.geronimo.transaction.manager.GeronimoTransactionManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class TransactionManagerProvider implements Provider<TransactionManager> {
  public static final String TRANSACTION_TIMEOUT_SECONDS = "transaction.timeoutSeconds";
  private GeronimoTransactionManager transMgr;

  @Inject
  public TransactionManagerProvider(
      @Named(TRANSACTION_TIMEOUT_SECONDS) int defaultTransactionTimeoutSeconds)
      throws XAException {
    transMgr = new GeronimoTransactionManager(defaultTransactionTimeoutSeconds);
  }

  public TransactionManager get() {
    return transMgr;
  }

}
