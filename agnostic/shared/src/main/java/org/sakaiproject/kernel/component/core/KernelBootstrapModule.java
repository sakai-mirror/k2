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

import com.google.inject.AbstractModule;
import com.google.inject.CreationException;
import com.google.inject.name.Names;
import com.google.inject.spi.Message;

import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.component.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

/**
 * 
 */
public class KernelBootstrapModule extends AbstractModule {

  private final static String DEFAULT_PROPERTIES = "res://kernel.properties";
  
  private final Properties properties;

  private Kernel kernel;
  
  public KernelBootstrapModule(Kernel kernel) {
    this.kernel = kernel;
    InputStream is = null;
    try {
      is = ResourceLoader.openResource(DEFAULT_PROPERTIES);
      properties = new Properties();
      properties.load(is);
    } catch (IOException e) {
      throw new CreationException(Arrays.asList(
          new Message("Unable to load properties: " + DEFAULT_PROPERTIES)));
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
  
  public KernelBootstrapModule(Kernel kernel, Properties properties) {
    this.properties = properties;
    this.kernel = kernel;
  }

  /* (non-Javadoc)
   * @see com.google.inject.AbstractModule#configure()
   */
  @Override
  protected void configure() {
    Names.bindProperties(this.binder(), properties);
    bind(Kernel.class).toInstance(kernel);
    bind(KernelInjectorService.class).asEagerSingleton();
    bind(ShutdownService.class).asEagerSingleton();
    bind(SharedClassLoaderContainer.class).asEagerSingleton();
    
  }

}
