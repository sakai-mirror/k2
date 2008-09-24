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

package org.sakaiproject.kernel.loader.server.jetty;

import org.mortbay.component.LifeCycle;
import org.sakaiproject.kernel.loader.common.CommonLifecycle;
import org.sakaiproject.kernel.loader.server.LoaderEnvironment;

/**
 * A Jetty Loader, that uses a single classloader and operates as a top level Jetty Component.
 * @author ieb
 * 
 */
public class KernelLoader implements LifeCycle {

  private CommonLifecycle kernelManager;
  
  private boolean failed = false;

  private boolean running = false;

  private boolean started = false;

  private boolean starting = false;

  private boolean stopped = false;

  private boolean stopping = false;



  /**
   * Start the kernel
   * {@inheritDoc} 
   */
  public void start() throws Exception {
    if ( starting || running || started ) {
      return;
    }
    starting = true;
    
    ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();    
    try {
      
      Class<CommonLifecycle> clazz = LoaderEnvironment.getManagerClass(currentClassLoader);
      kernelManager = clazz.newInstance();
      kernelManager.start();
      failed = false;
      running  = true;
      started = true;
      
    } finally {
      starting = false;
    }
  }

  /**
   * Stop the kernel
   * {@inheritDoc} 
   */
  public void stop() throws Exception {
    if ( stopping ) {
      return;
    }
    stopping = true;
    try {
      kernelManager.stop();
      failed = false;
      running  = false;
      started = false;
    } finally {
      stopping = false;
    }
  }

  /**
   * {@inheritDoc} 
   */
  public boolean isFailed() {
    return failed;
  }

  /**
   * {@inheritDoc} 
   */
  public boolean isRunning() {
    return running;
  }

  /**
   * {@inheritDoc} 
   */
  public boolean isStarted() {
   return started;
  }

  /**
   * {@inheritDoc} 
   */
  public boolean isStarting() {
    return starting;
  }

  /**
   * {@inheritDoc} 
   */
  public boolean isStopped() {
    return stopped;
  }

  /**
   * {@inheritDoc} 
   */
  public boolean isStopping() {
    return stopping;
  }

}
