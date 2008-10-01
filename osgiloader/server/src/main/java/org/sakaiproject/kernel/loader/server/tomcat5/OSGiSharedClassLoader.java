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
package org.sakaiproject.kernel.loader.server.tomcat5;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.loader.WebappClassLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.loader.common.CommonLifecycleEvent;
import org.sakaiproject.kernel.loader.common.CommonLifecycleListener;
import org.sakaiproject.kernel.loader.common.CommonObject;
import org.sakaiproject.kernel.loader.common.CommonObjectManager;

/**
 * 
 */
public class OSGiSharedClassLoader extends WebappClassLoader implements LifecycleListener,
    CommonLifecycleListener {
  private static final Log LOG = LogFactory.getLog(OSGiSharedClassLoader.class);
  private CommonObjectManager commonObjectManager;
  private CommonObject commonObject;
  private ClassLoader parentClassLoader;

  /**
   * 
   */
  public OSGiSharedClassLoader() {
    super();
    super.addLifecycleListener(this);
  }

  public OSGiSharedClassLoader(ClassLoader parent) {
    super(parent);
    super.addLifecycleListener(this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.catalina.LifecycleListener#lifecycleEvent(org.apache.catalina.LifecycleEvent)
   */
  public void lifecycleEvent(LifecycleEvent event) {
    try {
      String type = event.getType();
      LOG.debug("At " + type);
      if (Lifecycle.INIT_EVENT.equals(type)) {
        commonObjectManager = new CommonObjectManager();
        commonObjectManager.addListener(this);
        commonObject = commonObjectManager.getCommonObject();
        parentClassLoader = commonObject.getOSGiClassLoader();
        this.setParentClassLoader(parentClassLoader);
        LOG.info("Parent Classloader has been set to " + parentClassLoader);

        LOG.debug("INIT");
      } else if (Lifecycle.BEFORE_START_EVENT.equals(type)) {
        LOG.debug("Before Start");
        start();
      } else if (Lifecycle.START_EVENT.equals(type)) {
        LOG.debug("Start");
      } else if (Lifecycle.AFTER_START_EVENT.equals(type)) {
        LOG.debug("After Start");
      } else if (Lifecycle.PERIODIC_EVENT.equals(type)) {
        LOG.debug("Periodic");
      } else if (Lifecycle.BEFORE_STOP_EVENT.equals(type)) {
        LOG.debug("Before Stop");
      } else if (Lifecycle.STOP_EVENT.equals(type)) {
        LOG.debug("Stop");
      } else if (Lifecycle.AFTER_STOP_EVENT.equals(type)) {
        LOG.debug("After Stop");
        stop();
      } else if (Lifecycle.DESTROY_EVENT.equals(type)) {
        LOG.debug("Destroy ");
        commonObject = null;
        commonObjectManager.removeListener(this);
        commonObjectManager = null;

      } else {
        LOG.warn("Unrecognised Container Lifecycle Event ");
      }
    } catch (Exception ex) {
      LOG.error("Failed to start Component Context ", ex);
    }
  }

  public void lifecycleEvent(CommonLifecycleEvent event) {
    // TODO Should pull the webapp down on stop, but in reality the shared bundle never gets
    // unloaded

  }

}
