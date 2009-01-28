/*
 * Licensed to the Sakai Foundation (SF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The SF licenses this file
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.webapp.WebAppClassLoader;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.resource.Resource;
import org.sakaiproject.kernel.loader.common.CommonObjectConfigurationException;
import org.sakaiproject.kernel.loader.common.CommonObjectManager;
import org.sakaiproject.kernel.loader.server.SwitchedClassLoader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 */
public class SakaiWebAppContext extends WebAppContext {
  private static final Log LOG = LogFactory.getLog(SakaiWebAppContext.class);
  private ClassLoader parentClassloader = null;
  private WebAppClassLoader webappClassLoader = null;
  private Object lock = new Object();
  private ClassLoader containerClassLoader;

  /**
   * @throws CommonObjectConfigurationException
   * @throws IOException
   * 
   */
  public SakaiWebAppContext(ClassLoader containerClassLoader) throws CommonObjectConfigurationException, IOException {
    this.containerClassLoader = containerClassLoader;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mortbay.jetty.handler.ContextHandler#getClassLoader()
   */
  @Override
  public ClassLoader getClassLoader() {
    if (webappClassLoader == null) {
      synchronized (lock) {
        if (webappClassLoader == null) {
          try {

            CommonObjectManager com = new CommonObjectManager("sharedclassloader");
            ClassLoader cl = com.getManagedObject();
            parentClassloader = new SwitchedClassLoader(new URL[]{}, cl, containerClassLoader);
            
            LOG.info("Got Classloader fromm JMX as " + parentClassloader + "("
                + parentClassloader.getClass() + ")");
            if (LOG.isDebugEnabled()) {
              LOG.debug("Thread Context class loader is: " + parentClassloader);
              ClassLoader loader = parentClassloader.getParent();
              while (loader != null) {
                LOG.debug("Parent class loader is: " + loader);
                loader = loader.getParent();
              }
            }

            webappClassLoader = new WebAppClassLoader(parentClassloader, this);
            super.setClassLoader(webappClassLoader);
          } catch (CommonObjectConfigurationException e) {
            LOG.error(e);
          } catch (IOException e) {
            LOG.error(e);
          }
        }
      }
    }
    return webappClassLoader;
  }
  
  /* (non-Javadoc)
   * @see org.mortbay.jetty.webapp.WebAppContext#setWar(java.lang.String)
   */
  @Override
  public void setWar(String war) {
    try {
      Resource r = Resource.newResource(war);
      LOG.info("Resource "+r);
    } catch (MalformedURLException e) {
      LOG.info("Resource Error "+e.getMessage(),e);
    } catch (IOException e) {
      LOG.info("Resource Error "+e.getMessage(),e);
    }
    super.setWar(war);
  }
}
