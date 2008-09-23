/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/component/branches/SAK-12134/component-loader/tomcat5/component-loader-server/impl/src/java/org/sakaiproject/component/loader/tomcat5/server/SakaiLoader.java $
 * $Id: SakaiLoader.java 38801 2007-11-27 17:33:27Z ian@caret.cam.ac.uk $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2007 The Sakai Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.kernel.loader.server.jetty;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.component.LifeCycle;
import org.sakaiproject.kernel.loader.common.CommonLifecycle;

/**
 * 
 * @author ieb
 * 
 */
public class KernelLoader implements LifeCycle {

  private static final Log log = LogFactory.getLog(KernelLoader.class);

  // private static final String MBEAN_SHARED_CLASSLOADER =
  // "Catalina:type=ServerClassLoader,name=shared";

  private static final String MBEAN_CONTAINER = "Catalina:type=Host,host=localhost";

  private static final String COMPONENT_MANAGER_CLASS = "org.sakaiproject.kernel.component.KernelManager";

  private CommonLifecycle kernelManager;
  
  private ClassLoader sharedClassloader;

  private boolean failed = false;

  private boolean running = false;

  private boolean started = false;

  private boolean starting = false;

  private boolean stopped = false;

  private boolean stopping = false;



  public void start() throws Exception {
    if ( starting || running || started ) {
      return;
    }
    starting = true;
    
    ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
    sharedClassloader = oldClassLoader;
    Thread.currentThread().setContextClassLoader(sharedClassloader);
    
    try {
      log.info("Loading " + COMPONENT_MANAGER_CLASS + " using " + sharedClassloader);
      
      
      Class<CommonLifecycle> clazz = (Class<CommonLifecycle>) sharedClassloader.loadClass(COMPONENT_MANAGER_CLASS);
      log.info("Loaded Ok ");
      kernelManager = clazz.newInstance();
      log.info("Starting Component Manager " + clazz.getName());
      kernelManager.start();
      failed = false;
      running  = true;
      started = true;
      
    } finally {
//      Thread.currentThread().setContextClassLoader(oldClassLoader);
      starting = false;
    }
  }

  public void stop() throws Exception {
    if ( stopping ) {
      return;
    }
    stopping = true;
    System.err.println("Stopping Component Manger");
//    ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
//    Thread.currentThread().setContextClassLoader(sharedClassloader);
    try {
      kernelManager.stop();
      failed = false;
      running  = false;
      started = false;
    } finally {
//      Thread.currentThread().setContextClassLoader(oldClassLoader);
      stopping = false;
    }
  }

  /* (non-Javadoc)
   * @see org.mortbay.component.LifeCycle#isFailed()
   */
  public boolean isFailed() {
    return failed;
  }

  /* (non-Javadoc)
   * @see org.mortbay.component.LifeCycle#isRunning()
   */
  public boolean isRunning() {
    return running;
  }

  /* (non-Javadoc)
   * @see org.mortbay.component.LifeCycle#isStarted()
   */
  public boolean isStarted() {
   return started;
  }

  /* (non-Javadoc)
   * @see org.mortbay.component.LifeCycle#isStarting()
   */
  public boolean isStarting() {
    return starting;
  }

  /* (non-Javadoc)
   * @see org.mortbay.component.LifeCycle#isStopped()
   */
  public boolean isStopped() {
    return stopped;
  }

  /* (non-Javadoc)
   * @see org.mortbay.component.LifeCycle#isStopping()
   */
  public boolean isStopping() {
    return stopping;
  }

}
