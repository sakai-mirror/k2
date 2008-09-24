package org.sakaiproject.kernel.component;

import org.apache.felix.framework.Felix;
import org.apache.felix.framework.Logger;
import org.apache.felix.framework.cache.BundleCache;
import org.apache.felix.framework.util.FelixConstants;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.osgi.service.url.URLConstants;
import org.osgi.service.url.URLStreamHandlerService;
import org.sakaiproject.kernel.api.Kernel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A Kernel bundle activator brings up a bundle configured as a kernel.
 *
 */
public class KernelBundleActivator implements BundleActivator, Kernel {
  /**
   * The bundle logger.
   */
  protected Logger logger;
  /**
   * The running bundle context.
   */
  private BundleContext bundleContext;
  /**
   * The Felix OSGi container being used for this bundle.
   */
  private Felix felix;

  /**
   * Creates the bundle activator and starts it, with a logger.
   * @param newLogger the logger to use with this bundle.
   * @throws IOException if the properties that configure the bundle cant be opened.
   */
  public KernelBundleActivator(final Logger newLogger) throws IOException {
    this.logger = newLogger;

    this.logger.log(Logger.LOG_INFO, "Starting POC");

    Map<String, String> m = new ConcurrentHashMap<String, String>();

    InputStream in = this.getClass().getResourceAsStream("config.properties");
    Properties p = new Properties();
    p.load(in);
    for (Entry<?, ?> e : p.entrySet()) {
      m.put(String.valueOf(e.getKey()), String.valueOf(e.getValue()));
    }
    m.put(BundleCache.CACHE_PROFILE_DIR_PROP, "cache");
    // make sure Felix does not exit the VM when terminating ...
    m.put(FelixConstants.EMBEDDED_EXECUTION_PROP, "true");


    // check for auto-start bundles
    this.setInstallBundles(m);

    // ensure execution environment
    this.setExecutionEnvironment(m);


    // the custom activator list just contains this servlet
    List<BundleActivator> activators = new ArrayList<BundleActivator>();
    activators.add(this);
    // activators.add(new BootstrapInstaller(logger, resourceProvider));

    // create the framework and start it
    Felix tmpFelix = new Felix(logger, m, activators);
    try {
      tmpFelix.start();
    } catch (BundleException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // only assign field if start succeeds
    this.felix = tmpFelix;

    // log sucess message
    this.logger.log(Logger.LOG_INFO, "Sakai OSGi Activator started");
  }

  /**
   * Ensures sensible Execution Environment setting. If the
   * <code>org.osgi.framework.executionenvironment</code> property is set in the configured
   * properties or the system properties, we ensure that older settings for J2SE-1.2, J2SE-1.3 and
   * J2SE-1.4 are included. If the property is neither set in the configuration properties nor in
   * the system properties, the property is not set.
   *
   * @param props The configuration properties to check and optionally amend.
   */
  private void setExecutionEnvironment(final Map<String, String> props) {
    // get the current Execution Environment setting
    String ee = props.get(Constants.FRAMEWORK_EXECUTIONENVIRONMENT);
    if (ee == null) {
      ee = System.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT);
    }

    // if there is a setting, ensure J2SE-1.2/3/4/5 is included in the list
    if (ee != null) {
      int javaMinor;
      try {
        String specVString = System.getProperty("java.specification.version");
        javaMinor = Version.parseVersion(specVString).getMinor();
      } catch (IllegalArgumentException iae) {
        // don't care, assume minimal sling version (1.5)
        javaMinor = 5;
      }

      for (int i = 2; i <= javaMinor; i++) {
        String exEnv = "J2SE-1." + i;
        if (ee.indexOf(exEnv) < 0) {
          ee += "," + exEnv;
        }
      }

      this.logger.log(Logger.LOG_INFO, "Using Execution Environment setting: " + ee);
      props.put(Constants.FRAMEWORK_EXECUTIONENVIRONMENT, ee);
    } else {
      this.logger.log(Logger.LOG_INFO, "Not using Execution Environment setting");
    }
  }

  /**
   * Install auto start bundles based on a pattern.
   * Searches for all sakai.install.* keys, and collect them into a single
   * sakai.install.bundles key for installation into the map.
   * @param props the existing properties.
   */
  private void setInstallBundles(final Map<String, String> props) {
    String prefix = "sakai.install.";
    Set<String> levels = new TreeSet<String>();
    for (String key : props.keySet()) {
      if (key.startsWith(prefix)) {
        levels.add(key.substring(prefix.length()));
      }
    }

    StringBuffer buf = new StringBuffer();
    for (String level : levels) {
      if (buf.length() > 0) {
        buf.append(',');
      }
      buf.append(level);
    }

    props.put(prefix + "bundles", buf.toString());

  }


  // ---------- BundleActivator ----------------------------------------------

  /**
   * Called when the OSGi framework is being started. This implementation registers as a service
   * listener for the <code>javax.servlet.Servlet</code> class and calls the
   * {@link #doStartBundle()} method for implementations to execute more startup tasks. Additionally
   * the <code>context</code> URL protocol handler is registered.
   *
   * @param newBundleContext The <code>BundleContext</code> of the system bundle of the OSGi
   *                framework.
   * @throws Exception May be thrown if the {@link #doStartBundle()} throws.
   */
  public void start(final BundleContext newBundleContext) throws Exception {
    this.bundleContext = newBundleContext;

    // register the context URL handler
    Hashtable<String, Object> props = new Hashtable<String, Object>();
    props.put(URLConstants.URL_HANDLER_PROTOCOL, new String[] {"context"});

    StreamHandlerService streamHandler = new StreamHandlerService();

    bundleContext.registerService(URLStreamHandlerService.class.getName(), streamHandler, props);

    // execute optional bundle startup tasks of an extension
    // this.doStartBundle();
  }

  /**
   * Called when the OSGi framework is being shut down. This implementation first calls the
   * {@link #doStopBundle()} method method before unregistering as a service listener and ungetting
   * an servlet delegatee if one has been acquired.
   *
   * @param newBundleContext The <code>BundleContext</code> of the system bundle of the OSGi
   *                framework.
   */
  public void stop(final BundleContext newBundleContext) {
    // execute optional bundle stop tasks of an extension
    try {
      //this.doStopBundle();
    } catch (Exception e) {
      this.logger.log(Logger.LOG_ERROR, "Unexpected exception caught", e);
    }

    // drop bundle context reference
    this.bundleContext = null;
  }

  /**
   * Executes additional startup tasks and is called by the {@link #start(BundleContext)} method.
   * <p>
   * This implementation does nothing and may be overwritten by extensions requiring additional
   * startup tasks.
   *
   * @throws Exception May be thrown in case of problems.
   */
  protected void doStartBundle() throws Exception {
  }

  /**
   * Executes additional shutdown tasks and is called by the {@link #stop(BundleContext)} method.
   * <p>
   * This implementation does nothing and may be overwritten by extensions requiring additional
   * shutdown tasks.
   * <p>
   * When overwriting this method, it must be made sure, that no exception may be thrown, otherwise
   * unexpected behaviour may result.
   */
  protected void doStopBundle() {
  }

  /**
   * @return the current bundle context
   */
  public BundleContext getBundleContext() {
    return bundleContext;
  }

  /**
   * Destroys this servlet by shutting down the OSGi framework and hence the delegatee servlet if
   * one is set at all.
   */
  public void destroy() {
    // shutdown the Felix container
    if (felix != null) {
      logger.log(Logger.LOG_INFO, "Shutting down Sling");
      felix.stopAndWait();
      logger.log(Logger.LOG_INFO, "Sling stopped");
      felix = null;
    }
  }

}
