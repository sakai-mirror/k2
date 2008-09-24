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

import org.apache.catalina.Engine;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Server;
import org.apache.catalina.ServerFactory;
import org.apache.catalina.Service;
import org.apache.catalina.core.StandardService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.loader.common.CommonLifecycle;
import org.sakaiproject.kernel.loader.server.LoaderEnvironment;

import javax.management.ObjectName;

/**
 * This is a loader for Tomcat5 that is deployed as a lifecycle listener inside tomcat. This needs to
 * be deployed into Server as its loaded from server. <Listener
 * className="org.sakaiproject.kernel.loader.server.tomcat5.KernelLoader"/>
 * 
 * 
 */
public class KernelLoader implements LifecycleListener {

  private static final Log log = LogFactory.getLog(KernelLoader.class);

  /**
   * The name of the container Mbean where the shared classloader comes from
   */
  private static final String MBEAN_CONTAINER = "Catalina:type=Host,host=localhost";

  /**
   * The kernel manager that implements a common lifecycle API
   */
  private CommonLifecycle kernelManager;

  /**
   * The classloade to use to load the kernel, in tomcat 5 this is the shared classloader
   */
  private ClassLoader sharedClassloader;

  /**
   * The parent tomcat Engine that represents this tomcat instance
   */
  private Engine engine;

  /**
   * {@inheritDoc} Loads the kernel when the Container start event is emitted.
   */
  public void lifecycleEvent(LifecycleEvent event) {
    try {
      String type = event.getType();
      log.debug("At " + type);
      if (Lifecycle.INIT_EVENT.equals(type)) {
        log.debug("INIT");
      } else if (Lifecycle.BEFORE_START_EVENT.equals(type)) {
        log.debug("Before Start");
        start();
      } else if (Lifecycle.START_EVENT.equals(type)) {
        log.debug("Start");
      } else if (Lifecycle.AFTER_START_EVENT.equals(type)) {
        log.debug("After Start");
      } else if (Lifecycle.PERIODIC_EVENT.equals(type)) {
        log.debug("Periodic");
      } else if (Lifecycle.BEFORE_STOP_EVENT.equals(type)) {
        log.debug("Before Stop");
      } else if (Lifecycle.STOP_EVENT.equals(type)) {
        log.debug("Stop");
      } else if (Lifecycle.AFTER_STOP_EVENT.equals(type)) {
        log.debug("After Stop");
        stop();
      } else if (Lifecycle.DESTROY_EVENT.equals(type)) {
        log.debug("Destroy ");
      } else {
        log.warn("Unrecognised Container Lifecycle Event ");
      }
    } catch (Exception ex) {
      log.error("Failed to start Component Context ", ex);
    }
  }

  /**
   * Perform the start operation, by constructing the shared classloader and then instancing the
   * kernel manager in that classloader and starting the kernel.
   * 
   * @throws Exception
   */
  private void start() throws Exception {
    ObjectName pname = new ObjectName(MBEAN_CONTAINER);
    Service service = getService(pname);
    log.warn("Got service as " + service);
    engine = (Engine) service.getContainer();
    log.warn("Got engine as " + engine + " with classloader " + engine.getClass().getClassLoader()
        + " and with parent classloader " + engine.getParentClassLoader());
    sharedClassloader = engine.getParentClassLoader();
    ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(sharedClassloader);
    try {
      Class<CommonLifecycle> clazz = LoaderEnvironment.getManagerClass(sharedClassloader);
      kernelManager = clazz.newInstance();
      log.info("Starting Component Manager " + clazz.getName());
      kernelManager.start();

    } finally {
      Thread.currentThread().setContextClassLoader(oldClassLoader);
    }
  }

  /**
   * Stop the configured kernel manager
   * @throws Exception
   */
  private void stop() throws Exception {
    System.err.println("Stopping Component Manger");
    ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(sharedClassloader);
    try {
      kernelManager.stop();
    } finally {
      Thread.currentThread().setContextClassLoader(oldClassLoader);
    }
  }

  /**
   * Get hold of the parent service
   * @param oname
   * @return
   * @throws Exception
   */
  private Service getService(ObjectName oname) throws Exception {

    String domain = oname.getDomain();
    Server server = ServerFactory.getServer();
    Service[] services = server.findServices();
    StandardService service = null;
    for (int i = 0; i < services.length; i++) {
      service = (StandardService) services[i];
      if (domain.equals(service.getObjectName().getDomain())) {
        break;
      }
    }
    if (!service.getObjectName().getDomain().equals(domain)) {
      throw new Exception("Service with the domain is not found");
    }
    return service;

  }

}
