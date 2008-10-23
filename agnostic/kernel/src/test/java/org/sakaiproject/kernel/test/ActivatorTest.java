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
package org.sakaiproject.kernel.test;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sakaiproject.kernel.Activator;
import org.sakaiproject.kernel.api.ComponentActivatorException;
import org.sakaiproject.kernel.api.KernelConfigurationException;
import org.sakaiproject.kernel.api.RequiresStop;
import org.sakaiproject.kernel.api.ServiceSpec;
import org.sakaiproject.kernel.api.util.FileUtil;
import org.sakaiproject.kernel.component.ComponentManagerImpl;
import org.sakaiproject.kernel.component.KernelImpl;
import org.sakaiproject.kernel.component.ServiceManagerImpl;
import org.sakaiproject.kernel.component.core.ShutdownService;

import java.io.File;

/**
 * 
 */
public class ActivatorTest {

  private static final Log LOG = LogFactory.getLog(ActivatorTest.class);
  private KernelImpl kernel;
  private ComponentManagerImpl componentManager;
  private ServiceManagerImpl serviceManager;

  @Before
  public void start() throws KernelConfigurationException {
    // If there are problems with startup and shutdown, these will prevent the problem
    //FileUtil.deleteAll(new File("target/jcr"));
    //FileUtil.deleteAll(new File("target/testdb"));
    kernel = new KernelImpl();
    kernel.start();
    serviceManager = new ServiceManagerImpl(kernel);
    serviceManager.start();
    componentManager = new ComponentManagerImpl(kernel);
    componentManager.start();
  }

  @After
  public void stop() {
    try {
      componentManager.stop();
      serviceManager.stop();
      kernel.stop();
    } catch (Exception ex) {
      LOG.info("Failed to stop kernel ", ex);
    }
  }

  /**
   * Test method for
   * {@link org.sakaiproject.kernel.Activator#activate(org.sakaiproject.kernel.api.Kernel)}
   * .
   * 
   * @throws ComponentActivatorException
   */
  @Test
  public void testActivate() throws ComponentActivatorException {
    Activator activator = new Activator();
    activator.activate(kernel);
    for (Class<?> c : Activator.SERVICE_CLASSES) {

      ShutdownService ss = kernel.getServiceManager().getService(
          new ServiceSpec(ShutdownService.class));
      Object s = kernel.getServiceManager().getService(new ServiceSpec(c));
      if (s instanceof RequiresStop) {
        ss.register((RequiresStop) s);
      }
    }
  }

}
