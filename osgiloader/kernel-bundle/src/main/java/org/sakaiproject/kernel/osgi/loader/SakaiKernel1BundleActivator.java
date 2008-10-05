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
package org.sakaiproject.kernel.osgi.loader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.url.URLConstants;
import org.sakaiproject.component.api.ComponentManager;

import java.util.Hashtable;

/**
 *
 */
public class SakaiKernel1BundleActivator implements BundleActivator {

  private static final Log LOG = LogFactory.getLog(SakaiKernel1BundleActivator.class);
  private ComponentManager componentManager;

  /*
   * (non-Javadoc)
   * 
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  public void start(BundleContext context) throws Exception {
    LOG.info("Starting Sakai 1 Kernel ++++++++++++++++++++++++++++++");
    System.setProperty("sakai.components.root","/Users/ieb/Caret/sakai22/sakaidev/kernel/kernel-component/src/main/");
    componentManager = org.sakaiproject.component.cover.ComponentManager.getInstance();
    // register the context URL handler
    Hashtable<String, Object> props = new Hashtable<String, Object>();
    props.put(URLConstants.URL_HANDLER_PROTOCOL, new String[] { "context" });
    context.registerService(ComponentManager.class.getName(), componentManager, props);
    LOG.info("+++++++++++++++++++++++++++++++ Sakai 1 Kernel Started");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  public void stop(BundleContext context) throws Exception {
    componentManager.close();
  }

}
