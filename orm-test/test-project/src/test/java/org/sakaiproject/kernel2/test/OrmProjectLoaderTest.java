package org.sakaiproject.kernel2.test;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sakaiproject.kernel.Activator;
import org.sakaiproject.kernel.api.ComponentActivator;
import org.sakaiproject.kernel.api.ComponentActivatorException;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.KernelManager;
import org.sakaiproject.kernel.api.RequiresStop;
import org.sakaiproject.kernel.api.ServiceSpec;
import org.sakaiproject.kernel.api.ShutdownService;
import org.sakaiproject.kernel.api.memory.CacheManagerService;
import org.sakaiproject.kernel.api.memory.CacheScope;
import org.sakaiproject.kernel.api.persistence.PersistenceService;
import org.sakaiproject.kernel.component.KernelLifecycle;
import org.sakaiproject.kernel.util.FileUtil;

public class OrmProjectLoaderTest {
  private static final Log LOG = LogFactory.getLog(OrmProjectLoaderTest.class);

  private static KernelLifecycle kernelLifecycle;
  private static KernelManager kernelManager;
  private static PersistenceService persistence;

  @BeforeClass
  public static void beforeClass() throws ComponentActivatorException {
    // If there are problems with startup and shutdown, these will prevent the
    // problem
    File jcrBase = new File("target/jcr");
    File dbBase = new File("target/testdb");
    System.err
        .println("==========================================================================");
    System.err.println("Removing all previous JCR and DB traces from "
        + jcrBase.getAbsolutePath() + " " + dbBase.getAbsolutePath());

    FileUtil.deleteAll(jcrBase);
    FileUtil.deleteAll(dbBase);
    System.err
        .println("==========================================================================");

    kernelLifecycle = new KernelLifecycle();
    kernelLifecycle.start();

    kernelManager = new KernelManager();
    Kernel kernel = kernelManager.getKernel();

    // activate kernel core stuff
    ComponentActivator activator = new Activator();
    activator.activate(kernel);
    ShutdownService ss = kernel.getServiceManager().getService(
        new ServiceSpec(ShutdownService.class));
    for (Class<?> c : Activator.SERVICE_CLASSES) {
      Object s = kernel.getServiceManager().getService(new ServiceSpec(c));
      if (s instanceof RequiresStop) {
        ss.register((RequiresStop) s);
      }
    }

    // activate model project 1
    activator = new org.sakaiproject.kernel2.mp1.Activator();
    activator.activate(kernel);

    // activate model project 2
    activator = new org.sakaiproject.kernel2.mp2.Activator();
    activator.activate(kernel);

    persistence = kernel.getService(PersistenceService.class);
    System.out.println("DataSource: " + persistence.dataSource());
    System.out.println("EntityManager: " + persistence.entityManager());
  }

  @AfterClass
  public static void afterClass() {
    try {
      kernelLifecycle.stop();
    } catch (Exception ex) {
      LOG.info("Failed to stop kernel ", ex);
    }
  }

  @Test
  public void doSomething() throws Exception {
    int count = 0;
    for (Enumeration<URL> orms = this.getClass().getClassLoader().getResources(
    "META-INF/orm.xml"); orms.hasMoreElements();) {
      URL orm = orms.nextElement();
      System.out.println("** orm:" + count + ": " + orm);
      count++;
    }
    System.out.println("*** Found " + count + " orm.xml files on the classpath.");
  }

  /**
   * 
   */
  protected void endRequest() {
    CacheManagerService cacheManagerService = kernelManager
        .getService(CacheManagerService.class);
    cacheManagerService.unbind(CacheScope.REQUEST);
  }

  /**
   * @param request
   * @param response
   * @param cookieName
   * @return
   */
  // protected HttpServletResponse startRequest(HttpServletRequest request,
  // HttpServletResponse response, String cookieName) {
  // SakaiServletRequest wrequest = new SakaiServletRequest(request);
  // SakaiServletResponse wresponse = new SakaiServletResponse(response,
  // cookieName);
  // SessionManagerService sessionManagerService = kernelManager
  // .getService(SessionManagerService.class);
  // sessionManagerService.bindRequest(wrequest);
  // return wresponse;
  //
  // }
}
