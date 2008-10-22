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
package org.sakaiproject.kernel;

import com.google.inject.AbstractModule;
import com.google.inject.CreationException;
import com.google.inject.name.Names;
import com.google.inject.spi.Message;

import org.sakaiproject.kernel.api.ComponentManager;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.ServiceManager;
import org.sakaiproject.kernel.component.ResourceLoader;
import org.sakaiproject.kernel.component.core.ShutdownService;
import org.sakaiproject.kernel.component.core.guice.ServiceProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

/**
 * A Guice module used to create the kernel component.
 */
public class KernelModule extends AbstractModule  {

    /**
     * Location of the kernel properties.
     */
    private final static String DEFAULT_PROPERTIES = "res://kernel.properties";

    /**
     * The properties for the kernel
     */
    private final Properties properties;

    /**
     * The kernel which the bootstrap component exists within.
     */
    private Kernel kernel;

    /**
     * Create a Guice module for the kernel bootstrap.
     * 
     * @param kernel
     *          the kernel performing the bootstrap.
     */
    public KernelModule(Kernel kernel) {
      this.kernel = kernel;
      InputStream is = null;
      try {
        is = ResourceLoader.openResource(DEFAULT_PROPERTIES);
        properties = new Properties();
        properties.load(is);
      } catch (IOException e) {
        throw new CreationException(Arrays.asList(new Message(
            "Unable to load properties: " + DEFAULT_PROPERTIES)));
      } finally {
        try {
          if (is != null) {
            is.close();
          }
        } catch (IOException e) {
          // dont care about this.
        }
      }
    }

    /**
     * Create the bootstrap module with a kernel and supplied properties.
     * 
     * @param kernel
     * @param properties
     */
    public KernelModule(Kernel kernel, Properties properties) {
      this.properties = properties;
      this.kernel = kernel;
    }

    /**
     * Configure the guice bindings.
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
      Names.bindProperties(this.binder(), properties);
      bind(Kernel.class).toInstance(kernel);
      ServiceManager serviceManager = kernel.getServiceManager();
      bind(ServiceManager.class).toInstance(serviceManager);
      bind(ComponentManager.class).toInstance(kernel.getComponentManager());
      bind(ShutdownService.class).toProvider(new ServiceProvider<ShutdownService>(serviceManager,ShutdownService.class));
      
    }
}
