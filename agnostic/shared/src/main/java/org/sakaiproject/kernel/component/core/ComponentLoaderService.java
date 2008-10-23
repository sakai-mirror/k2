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

import org.mortbay.log.Log;
import org.sakaiproject.kernel.api.ComponentManager;
import org.sakaiproject.kernel.api.ComponentSpecification;
import org.sakaiproject.kernel.api.ComponentSpecificationException;
import org.sakaiproject.kernel.api.KernelConfigurationException;
import org.sakaiproject.kernel.component.ResourceLoader;
import org.sakaiproject.kernel.component.URLComponentSpecificationImpl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Loads components
 */
public class ComponentLoaderService {

  /**
   * @throws IOException 
   * @throws ComponentSpecificationException 
   * @throws KernelConfigurationException 
   * 
   */
  @Inject
  public ComponentLoaderService(ComponentManager componentManager) throws ComponentSpecificationException, IOException, KernelConfigurationException {
    List<ComponentSpecification> specs = new ArrayList<ComponentSpecification>();
    for ( Enumeration<URL> components = this.getClass().getClassLoader().getResources("SAKAI-INF/component.xml"); components.hasMoreElements(); ) {
      URL url = components.nextElement();
      Log.info("Adding Component "+url);
      specs.add(new URLComponentSpecificationImpl(ResourceLoader.INLINE+ResourceLoader.readResource(url)));
    }
    componentManager.startComponents(specs);
  }
  
}
