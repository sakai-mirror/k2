package org.sakaiproject.kernel.osgi.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * A Kernel bundle activator brings up a bundle configured as a kernel.
 *
 */
public class CommonBundleActivator implements BundleActivator {
  private static final Log LOG = LogFactory.getLog(CommonBundleActivator.class);

  /**
   * The running bundle context.
   */
  private BundleContext bundleContext;

  private CommonObjectLifecycle commonObjectLifeCycle;

  public CommonBundleActivator() {
    LOG.info("Created CommonBundleActivator ++++++++++++++++++++++++++++");
  }



  // ---------- BundleActivator ----------------------------------------------

  public void start(final BundleContext newBundleContext) throws Exception {
    this.bundleContext = newBundleContext;
    commonObjectLifeCycle = new CommonObjectLifecycle();
    commonObjectLifeCycle.start();
    
  }

  public void stop(final BundleContext newBundleContext) {
    // execute optional bundle stop tasks of an extension
    try {
      commonObjectLifeCycle.stop();
    } catch (Exception e) {
      LOG.error("Unexpected exception caught", e);
    }

    // drop bundle context reference
    this.bundleContext = null;
  }

  /* (non-Javadoc)
   * @see org.sakaiproject.kernel.api.Kernel#getContext()
   */
  public BundleContext getContext() {
    return bundleContext;
  }

}
