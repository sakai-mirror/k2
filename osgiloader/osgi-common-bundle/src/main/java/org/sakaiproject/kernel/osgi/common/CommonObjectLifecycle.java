/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.sakaiproject.kernel.osgi.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.loader.common.CommonLifecycle;
import org.sakaiproject.kernel.loader.common.CommonLifecycleEvent;
import org.sakaiproject.kernel.loader.common.CommonLifecycleListener;
import org.sakaiproject.kernel.loader.common.CommonObject;

import javax.management.Descriptor;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.modelmbean.DescriptorSupport;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanInfo;
import javax.management.modelmbean.ModelMBeanInfoSupport;
import javax.management.modelmbean.ModelMBeanOperationInfo;
import javax.management.modelmbean.RequiredModelMBean;

import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * The CommonObjectLifecycle manages the lifecycle of the CommonObject.
 */
public class CommonObjectLifecycle implements CommonLifecycle<CommonObject> {

  /**
   * Create a CommonObjectLifecycle object.
   */
  public CommonObjectLifecycle() {
    super();
  }

  /**
   * a Logger.
   */
  private static final Log LOG = LogFactory.getLog(CommonObjectLifecycle.class);

  /**
   * Failure code that is used.
   */
  private static final int FAILURE_CODE = 10;

  /**
   * a concurrent store of listeners.
   */
  private CopyOnWriteArraySet<CommonLifecycleListener> listeners =
    new CopyOnWriteArraySet<CommonLifecycleListener>();

  /**
   * the date the kernel was last loaded.
   */
  private Date lastLoadDate;

  /**
   * how long it took to load.
   */
  private long loadTime;

  /**
   * The managed object which is a CommonObjectImpl, (implementing a CommonObject).
   */
  private CommonObjectImpl commonObject;

  /**
   * Execute the start phase of the lifecycle, creating the MBean and registering the newly started
   * CommonObject with JMX.
   *
   * @see org.sakaiproject.kernel.loader.common.CommonLifecycle#start()
   */
  public void start() {
    LOG.info("Component Lifecycle is starting ==============================================");
    try {
      long start = System.currentTimeMillis();
      lifecycleEvent(CommonLifecycleEvent.BEFORE_START);
      lastLoadDate = new Date();
      commonObject = new CommonObjectImpl();

      MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

      RequiredModelMBean model = new RequiredModelMBean(createMBeanInfo());
      model.setManagedResource(this, "objectReference");
      ObjectName commonObjectName = new ObjectName(CommonObject.MBEAN_COMMON);
      mbs.registerMBean(model, commonObjectName);

      

      loadTime = System.currentTimeMillis() - start;

    } catch (Throwable ex) {
      LOG.error("Failed to start Component Lifecycle ", ex);
      System.exit(FAILURE_CODE);
    }
    LOG.info("Common Lifecycle Start Complete ===========================================");

  }

  /**
   * Create the the MBean Info for the CommonObject so that the methods and properties are accessable via
   * JMX.
   *
   * @return a new MBeanInfo structure
   */
  private ModelMBeanInfo createMBeanInfo() {
    Descriptor lastLoadDateDesc = new DescriptorSupport(new String[] {"name=LastLoadDate",
        "descriptorType=attribute", "default=0", "displayName=Last Load Date",
        "getMethod=getLastLoadDate"});
    Descriptor lastLoadTimeDesc = new DescriptorSupport(new String[] {"name=LastLoadTime",
        "descriptorType=attribute", "default=0", "displayName=Last Load Time",
        "getMethod=getLoadTime" });

    ModelMBeanAttributeInfo[] mmbai = new ModelMBeanAttributeInfo[2];
    mmbai[0] = new ModelMBeanAttributeInfo("LastLoadDate", "java.util.Date", "Last Load Date",
        true, false, false, lastLoadDateDesc);

    mmbai[1] = new ModelMBeanAttributeInfo("LastLoadTime", "java.lang.Long", "Last Load Time",
        true, false, false, lastLoadTimeDesc);

    ModelMBeanOperationInfo[] mmboi = new ModelMBeanOperationInfo[5];

    mmboi[0] = new ModelMBeanOperationInfo("getManagedObject", "Get the Current Common Object", null,
        CommonObject.class.getName(), ModelMBeanOperationInfo.INFO);

    mmboi[1] = new ModelMBeanOperationInfo("addCommonObjectLifecycleListener",
        "Add a listener to the kernel lifecycle",
        new MBeanParameterInfo[] {new MBeanParameterInfo("Lifecycle Listener",
            CommonLifecycleListener.class.getName(), "The Lifecycle Listener to be added") },
        "void", ModelMBeanOperationInfo.ACTION);
    mmboi[2] = new ModelMBeanOperationInfo("removeCommonObjectLifecycleListener",
        "Remove a listener to the kernel lifecycle",
        new MBeanParameterInfo[] {new MBeanParameterInfo("Lifecycle Listener",
            CommonLifecycleListener.class.getName(), "The Lifecycle Listener to be removed") },
        "void", ModelMBeanOperationInfo.ACTION);
    mmboi[3] = new ModelMBeanOperationInfo("getLastLoadDate",
        "The date the kernel was last loaded", null, "java.util.Date",
        ModelMBeanOperationInfo.INFO);
    mmboi[4] = new ModelMBeanOperationInfo("getLoadTime", "The time it took to load the kernel",
        null, "long", ModelMBeanOperationInfo.INFO);

    return new ModelMBeanInfoSupport(this.getClass().getName(), "Sakai Kernel", mmbai, null,
        mmboi, null);
  }

  /**
   * Stop the Common Object and remove it from JMX.
   */
  public void stop() {
    LOG.info("Component Lifecyle is stopping");
    try {
      lifecycleEvent(CommonLifecycleEvent.BEFORE_STOP);
      lifecycleEvent(CommonLifecycleEvent.STOP);
      MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
      ObjectName kernel = new ObjectName(CommonObject.MBEAN_COMMON);
      mbs.unregisterMBean(kernel);
      lifecycleEvent(CommonLifecycleEvent.AFTER_STOP);

    } catch (Throwable ex) {
      LOG.error("Failed to stop Component Lifecycle ", ex);
    }

  }

  /**
   * Destroy the Common Object.
   */
  public void destroy() {
    LOG.info("Component Lifecycle is stopping");
    try {
      lifecycleEvent(CommonLifecycleEvent.DESTROY);
      listeners.clear();
    } catch (Throwable ex) {
      LOG.error("Failed to stop Component Lifecycle ", ex);
    }

  }

  /**
   * Get the kernel. (JMX method)
   * @return the kernel object
   */
  public CommonObject getManagedObject() {
    return commonObject;
  }

  /**
   * Fire the lifecycle events.
   *
   * @param event the event to be sent to listeners
   */
  protected void lifecycleEvent(final CommonLifecycleEvent event) {
    for (CommonLifecycleListener l : listeners) {
      l.lifecycleEvent(event);
    }
  }

  /**
   * Add a listener to the lifecycle.
   * @param listener the listener to add
   */
  public void addCommonObjectLifecycleListener(final CommonLifecycleListener listener) {
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  /**
   * @param listener the listener to add to a set of lifecycle listeners.
   */
  public void removeCommonObjectLifecycleListener(CommonLifecycleListener listener) {
    listeners.remove(listener);
  }

  /**
   * @return the date the kernel was last loaded.
   */
  public Date getLastLoadDate() {
    return lastLoadDate;
  }

  /**
   * @return the time taken to load last time.
   */
  public long getLoadTime() {
    return loadTime;
  }

}
