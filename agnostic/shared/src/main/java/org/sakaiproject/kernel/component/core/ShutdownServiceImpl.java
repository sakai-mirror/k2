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

import com.google.inject.Inject;

import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.RequiresStop;
import org.sakaiproject.kernel.api.ServiceManagerException;
import org.sakaiproject.kernel.api.ServiceSpec;
import org.sakaiproject.kernel.api.ShutdownService;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A shutdown service that stops all services registered with it.
 */
public class ShutdownServiceImpl implements RequiresStop, ShutdownService {

  /**
   * A list of services to stop on reload.
   */
  List<RequiresStop> tostop = new CopyOnWriteArrayList<RequiresStop>();

  /**
   * Create a shutdown service based on the kernel
   * 
   * @param kernel
   *          the kernel.
   * @throws ServiceManagerException
   */
  @Inject
  public ShutdownServiceImpl(Kernel kernel) throws ServiceManagerException {
    kernel.getServiceManager().registerService(
        new ServiceSpec(ShutdownService.class), this);
  }

  /**
   * Stop the Shutdown services.
   * 
   * @see org.sakaiproject.kernel.api.RequiresStop#stop()
   */
  public void stop() {
    for (RequiresStop s : tostop) {
      s.stop();
    }
  }

  /**
   * Register a new stop service.
   * 
   * @param toBeStopped
   */
  public void register(RequiresStop toBeStopped) {
    if (!tostop.contains(toBeStopped)) {
      tostop.add(toBeStopped);
    }
  }
}
