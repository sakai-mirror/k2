/**
 * 
 */
package org.sakaiproject.kernel.component;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.felix.framework.Logger;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.loader.common.CommonLifecycle;
import org.sakaiproject.kernel.loader.common.CommonLifecycleEvent;
import org.sakaiproject.kernel.loader.common.CommonLifecycleListener;

import javax.management.Descriptor;
import javax.management.MBeanException;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.RuntimeOperationsException;
import javax.management.modelmbean.DescriptorSupport;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanInfo;
import javax.management.modelmbean.ModelMBeanInfoSupport;
import javax.management.modelmbean.ModelMBeanOperationInfo;
import javax.management.modelmbean.RequiredModelMBean;
import javax.management.openmbean.CompositeData;

import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author ieb
 */
public class KernelManager implements CommonLifecycle {

  /**
   * @throws MBeanException
   * @throws RuntimeOperationsException
   */
  public KernelManager() throws MBeanException, RuntimeOperationsException {
    super();
  }

  private static final Log log = LogFactory.getLog(KernelManager.class);

  private CopyOnWriteArraySet<CommonLifecycleListener> listeners = new CopyOnWriteArraySet<CommonLifecycleListener>();

  private Date lastLoadDate;

  private long loadTime;

  private KernelBundleActivator kernelBundleActivator;

  public void start() {
    log
        .info("Component Manager is starting =========================================================================");
    try {
      long start = System.currentTimeMillis();
      lifecycleEvent(CommonLifecycleEvent.BEFORE_START);
      lastLoadDate = new Date();
      

      MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

      RequiredModelMBean model = new RequiredModelMBean(createMBeanInfo());
      model.setManagedResource(this, "objectReference");
      ObjectName componentManager = new ObjectName(Kernel.MBEAN_KERNEL_BUNDLE);
      mbs.registerMBean(model, componentManager);

      Logger logger = new KernelLogger();
      kernelBundleActivator = new KernelBundleActivator(logger);

      try {
        System.runFinalization();
        Runtime.getRuntime().gc();
        CompositeData permGen = null;
        try {
          permGen = (CompositeData) mbs.getAttribute(new ObjectName(
              "java.lang:type=MemoryPool,name=Perm Gen"), "Usage");
        } catch (Exception ex) {
          permGen = (CompositeData) mbs.getAttribute(new ObjectName(
              "java.lang:type=MemoryPool,name=CMS Perm Gen"), "Usage");
        }
        CompositeData tenuredGen;
        try {
          tenuredGen = (CompositeData) mbs.getAttribute(new ObjectName(
              "java.lang:type=MemoryPool,name=Tenured Gen"), "Usage");
        } catch (Exception ex) {
          tenuredGen = (CompositeData) mbs.getAttribute(new ObjectName(
              "java.lang:type=MemoryPool,name=CMS Old Gen"), "Usage");
        }
        CompositeData codeCache = (CompositeData) mbs.getAttribute(new ObjectName(
            "java.lang:type=MemoryPool,name=Code Cache"), "Usage");
        CompositeData edenSpace = null;
        try {
          edenSpace = (CompositeData) mbs.getAttribute(new ObjectName(
              "java.lang:type=MemoryPool,name=Eden Space"), "Usage");
        } catch (Exception ex) {
          edenSpace = (CompositeData) mbs.getAttribute(new ObjectName(
              "java.lang:type=MemoryPool,name=Par Eden Space"), "Usage");

        }
        CompositeData survivorSpace = null;
        try {
          survivorSpace = (CompositeData) mbs.getAttribute(new ObjectName(
              "java.lang:type=MemoryPool,name=Survivor Space"), "Usage");
        } catch (Exception ex) {
          survivorSpace = (CompositeData) mbs.getAttribute(new ObjectName(
              "java.lang:type=MemoryPool,name=Par Survivor Space"), "Usage");
        }
        long permGenUsed = Long.parseLong(String.valueOf(permGen.get("used")));
        long codeCacheUsed = Long.parseLong(String.valueOf(codeCache.get("used")));
        long edenSpaceUsed = Long.parseLong(String.valueOf(edenSpace.get("used")));
        long tenuredGenUsed = Long.parseLong(String.valueOf(tenuredGen.get("used")));
        long survivorSpaceUsed = Long.parseLong(String.valueOf(survivorSpace.get("used")));

        log.info("           Permgen Used " + permGenUsed / (1024 * 1024) + " MB");
        log.info("           Code Cache Used " + codeCacheUsed / (1024 * 1024) + " MB");
        log.info("           Eden Used " + edenSpaceUsed / (1024 * 1024) + " MB");
        log.info("           Tenured Used " + tenuredGenUsed / (1024 * 1024) + " MB");
        log.info("           Survivour Used " + survivorSpaceUsed / (1024 * 1024) + " MB");
      } catch (Exception ex2) {
        log.info("Startup Memory Stats Not available");
      }
      lifecycleEvent(CommonLifecycleEvent.START);
      lifecycleEvent(CommonLifecycleEvent.AFTER_START);
      loadTime = System.currentTimeMillis() - start;

    } catch (Throwable ex) {
      log.error("Failed to start ComponentManager ", ex);
      System.exit(10);
    }
    log
        .info("Kernel Manager Start Complete =========================================================================");

  }

  /**
   * @return
   */
  private ModelMBeanInfo createMBeanInfo() {
    Descriptor lastLoadDate = new DescriptorSupport(new String[] { "name=LastLoadDate",
        "descriptorType=attribute", "default=0", "displayName=Last Load Date",
        "getMethod=getLastLoadDate" });
    Descriptor lastLoadTime = new DescriptorSupport(new String[] { "name=LastLoadTime",
        "descriptorType=attribute", "default=0", "displayName=Last Load Time",
        "getMethod=getLoadTime" });

    ModelMBeanAttributeInfo[] mmbai = new ModelMBeanAttributeInfo[2];
    mmbai[0] = new ModelMBeanAttributeInfo("LastLoadDate", "java.util.Date", "Last Load Date",
        true, false, false, lastLoadDate);

    mmbai[1] = new ModelMBeanAttributeInfo("LastLoadTime", "java.lang.Long", "Last Load Time",
        true, false, false, lastLoadTime);

    ModelMBeanOperationInfo[] mmboi = new ModelMBeanOperationInfo[7];

    mmboi[0] = new ModelMBeanOperationInfo("start", "Start the Kernel", null, "void",
        ModelMBeanOperationInfo.ACTION);
    mmboi[1] = new ModelMBeanOperationInfo("stop", "Stop the Kernel", null, "void",
        ModelMBeanOperationInfo.ACTION);
    mmboi[2] = new ModelMBeanOperationInfo("getKernel",
        "Get the Current Component Manager", null, Kernel.class.getName(),
        ModelMBeanOperationInfo.INFO);

    mmboi[3] = new ModelMBeanOperationInfo("addKernelLifecycleListener",
        "Add a listener to the kernel lifecycle",
        new MBeanParameterInfo[] { new MBeanParameterInfo("Lifecycle Listener",
            CommonLifecycleListener.class.getName(), "The Lifecycle Listener to be added") },
        "void", ModelMBeanOperationInfo.ACTION);
    mmboi[4] = new ModelMBeanOperationInfo("removeKernelLifecycleListener",
        "Remove a listener to the kernel lifecycle",
        new MBeanParameterInfo[] { new MBeanParameterInfo("Lifecycle Listener",
            CommonLifecycleListener.class.getName(), "The Lifecycle Listener to be removed") },
        "void", ModelMBeanOperationInfo.ACTION);
    mmboi[5] = new ModelMBeanOperationInfo("getLastLoadDate",
        "The date the kernel was last loaded", null, "java.util.Date",
        ModelMBeanOperationInfo.INFO);
    mmboi[6] = new ModelMBeanOperationInfo("getLoadTime",
        "The time it took to load the kernel", null, "long",
        ModelMBeanOperationInfo.INFO);

    /*
     * mmboi[1] = new ModelMBeanOperationInfo("decPanelValue", "decrement the meter value", null,
     * "void", ModelMBeanOperationInfo.ACTION ); mmboi[2] = new
     * ModelMBeanOperationInfo("getPanelValue", "getter for PanelValue", null,"Integer",
     * ModelMBeanOperationInfo.INFO); MBeanParameterInfo [] mbpi = new MBeanParameterInfo[1];
     * mbpi[0] = new MBeanParameterInfo("inVal", "java.lang.Integer", "value to set"); mmboi[3] =
     * new ModelMBeanOperationInfo("setPanelValue", "setter for PanelValue", mbpi, "void",
     * ModelMBeanOperationInfo.ACTION); ModelMBeanConstructorInfo [] mmbci = new
     * ModelMBeanConstructorInfo[1]; mmbci[0] = new ModelMBeanConstructorInfo("ClickMeterMod",
     * "constructor for Model Bean Sample", null);
     */

    return new ModelMBeanInfoSupport(this.getClass().getName(), "Sakai Kernel", mmbai,
        null, mmboi, null);
  }

  public void stop() {
    log.info("Component Manager is stopping");
    try {
      lifecycleEvent(CommonLifecycleEvent.BEFORE_STOP);
      lifecycleEvent(CommonLifecycleEvent.STOP);
      kernelBundleActivator.doStopBundle();
      lifecycleEvent(CommonLifecycleEvent.AFTER_START);
    } catch (Throwable ex) {
      log.error("Failed to stop ComponentManager ", ex);
    }

  }

  public void destroy() {
    log.info("Component Manager is stopping");
    try {
      lifecycleEvent(CommonLifecycleEvent.DESTROY);
      listeners.clear();
    } catch (Throwable ex) {
      log.error("Failed to stop ComponentManager ", ex);
    }

  }

  public Object getLifecycleObject() {
    return kernelBundleActivator;
  }

  public Kernel getComponentManager() {
    return kernelBundleActivator;
  }

  /**
   * Fire the lifecycle events
   * 
   * @param event
   */
  protected void lifecycleEvent(CommonLifecycleEvent event) {
    for (CommonLifecycleListener l : listeners) {
      l.lifecycleEvent(event);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.sakaiproject.component.loader.shared.SharedComponentManagerMBean#addComponentManagerLifecycleListener(org.sakaiproject.component.loader.common.CommonLifecycleListener)
   */
  public void addKernelLifecycleListener(CommonLifecycleListener listener) {
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.sakaiproject.component.loader.shared.SharedComponentManagerMBean#removeComponentManagerLifecycleListener(org.sakaiproject.component.loader.common.CommonLifecycleListener)
   */
  public void removeKernelLifecycleListener(CommonLifecycleListener listener) {
    listeners.remove(listener);
  }

  public Date getLastLoadDate() {
    return lastLoadDate;
  }

  public long getLoadTime() {
    return loadTime;
  }

}
