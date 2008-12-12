package org.sakaiproject.kernel2.test;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.sakaiproject.kernel.api.KernelManager;
import org.sakaiproject.kernel.api.UpdateFailedException;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
import org.sakaiproject.kernel.test.KernelIntegrationBase;

public class OrmProjectLoaderTest extends KernelIntegrationBase {
  private static final Log LOG = LogFactory.getLog(OrmProjectLoaderTest.class);

  @Test
  public void testCheck() throws JCRNodeFactoryServiceException, UpdateFailedException, AccessDeniedException, ItemExistsException, ConstraintViolationException, InvalidItemStateException, ReferentialIntegrityException, VersionException, LockException, NoSuchNodeTypeException, RepositoryException {
    if ( true ) {
      return; // fixing test WIP
    }
    LOG
        .info("Starting Test ====================================================");
    KernelManager km = new KernelManager();
  }
}
