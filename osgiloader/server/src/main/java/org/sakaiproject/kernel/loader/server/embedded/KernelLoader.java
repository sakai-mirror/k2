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

package org.sakaiproject.kernel.loader.server.embedded;

import org.sakaiproject.kernel.loader.common.CommonLifecycle;
import org.sakaiproject.kernel.loader.server.LoaderEnvironment;

/**
 * A static loader that will bring the kernel up once per jvm with the thread classloader. This
 * loader can be used in containers that have no lifecycle and the only option is to use a static
 * startup mechanism
 */
public class KernelLoader {

  private static final Object lock = new Object();

  private static CommonLifecycle kernelManager;

  private static boolean failed = false;

  private static boolean running = false;

  private static boolean started = false;

  private static boolean starting = false;

  private boolean stopped = false;

  private boolean stopping = false;

  /**
   * Start the kernel for the JVM
   */
  public static void start() throws Exception {
    synchronized (lock) {
      if (starting || running || started) {
        return;
      }
      starting = true;

      ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();

      try {

        Class<CommonLifecycle> clazz = LoaderEnvironment.getManagerClass(currentClassLoader);
        kernelManager = clazz.newInstance();
        kernelManager.start();
        failed = false;
        running = true;
        started = true;

      } finally {
        starting = false;
      }

    }
  }

  /**
   * Stop the kernel
   */
  public void stop() throws Exception {
    synchronized (lock) {
      if (stopping || !running || !started || starting) {
        return;
      }
      stopping = true;
      System.err.println("Stopping Component Manger");
      try {
        kernelManager.stop();
        failed = false;
        running = false;
        started = false;
      } finally {
        stopping = false;
      }
    }
  }

  /**
   * @return true if the kernel failed to start.
   */
  public boolean isFailed() {
    return failed;
  }

  /**
   * @return true if the kernel is up and running.
   */
  public boolean isRunning() {
    return running;
  }

  /**
   * @return true if the kernel is stated.
   */
  public boolean isStarted() {
    return started;
  }

  /**
   * @return true if the kernel is starting.
   */
  public boolean isStarting() {
    return starting;
  }

  /**
   * @return true if the kernel is stopped.
   */
  public boolean isStopped() {
    return stopped;
  }

  /**
   * @return true if the kernel is stopping.
   */
  public boolean isStopping() {
    return stopping;
  }

}
