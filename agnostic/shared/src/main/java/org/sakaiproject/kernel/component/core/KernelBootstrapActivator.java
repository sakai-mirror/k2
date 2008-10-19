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
package org.sakaiproject.kernel.component.core;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.ComponentActivator;
import org.sakaiproject.kernel.api.ComponentActivatorException;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.KernelConfigurationException;
import org.sakaiproject.kernel.api.RequiresStop;
import org.sakaiproject.kernel.api.ServiceSpec;

import java.util.Collection;

/**
 *
 */
public class KernelBootstrapActivator implements ComponentActivator {

  private static final Log LOG = LogFactory
      .getLog(KernelBootstrapActivator.class);
  private Kernel kernel;

  /**
   * @throws KernelConfigurationException
   * @see org.sakaiproject.kernel.api.ComponentActivator#activate(org.sakaiproject.kernel.api.Kernel)
   */
  public void activate(Kernel kernel) throws ComponentActivatorException {
    LOG.info("Starting Shared Container");
    this.kernel = kernel;
    @SuppressWarnings("unused")
    Injector injector = Guice.createInjector(new KernelBootstrapModule(kernel));
  }

  /**
   * @see org.sakaiproject.kernel.api.ComponentActivator#deactivate()
   */
  public void deactivate() {
    Collection<RequiresStop> toStop = kernel.getServiceManager().getServices(
        new ServiceSpec(RequiresStop.class, true));
    
    for (RequiresStop s : toStop) {
      s.stop();
    }
  }

}
