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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.ComponentLoaderService;
import org.sakaiproject.kernel.api.ComponentManager;
import org.sakaiproject.kernel.api.ComponentSpecification;
import org.sakaiproject.kernel.api.ComponentSpecificationException;
import org.sakaiproject.kernel.api.KernelConfigurationException;
import org.sakaiproject.kernel.component.URLComponentSpecificationImpl;
import org.sakaiproject.kernel.util.FileUtil;
import org.sakaiproject.kernel.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Loads components
 */
public class ComponentLoaderServiceImpl implements ComponentLoaderService  {

  private static final String COMPONENT_SPEC_XML = "SAKAI-INF/component.xml";
  private static final Log LOG = LogFactory
      .getLog(ComponentLoaderServiceImpl.class);
  private ComponentManager componentManager;

  /**
   * @throws IOException
   * @throws ComponentSpecificationException
   * @throws KernelConfigurationException
   * 
   */
  @Inject
  public ComponentLoaderServiceImpl(ComponentManager componentManager) {
    this.componentManager = componentManager;
  }

  public void load(String componentLocations, boolean fromClassloader) throws IOException, ComponentSpecificationException,
      KernelConfigurationException {
    // convert the location set into a list of URLs
    List<URL> locations = new ArrayList<URL>();
    for (String location : StringUtils.split(componentLocations,';')) {
      if (location.endsWith(".jar")) {
        if (location.indexOf("://") < 0) {
          location = "file:/" + location;
        }
        locations.add(new URL(location));
      }
      for (File f : FileUtil.findAll(location, "*.jar")) {
        String path = f.getCanonicalPath();
        if (path.indexOf("://") < 0) {
          path = "file:/" + path;
        }
        locations.add(new URL(path));
      }
    }

    // bind to the parent classloader ?
    ClassLoader parent = null;
    if (fromClassloader) {
      parent = this.getClass().getClassLoader();
    }
    // find all the instances
    URLClassLoader uclassloader = new URLClassLoader(locations
        .toArray(new URL[0]), parent);

    List<ComponentSpecification> specs = new ArrayList<ComponentSpecification>();
    for (Enumeration<URL> components = uclassloader
        .getResources(COMPONENT_SPEC_XML); components.hasMoreElements();) {
      URL url = components.nextElement();
      String componentSpecXml = url.toString();
      String source = componentSpecXml;
      if (source.endsWith(COMPONENT_SPEC_XML)) {
        source = source
            .substring(source.length() - COMPONENT_SPEC_XML.length());
      }
      LOG.info("Adding Component " + url);
      specs.add(new URLComponentSpecificationImpl(source, componentSpecXml));
    }
    uclassloader = null;
    if (specs.size() > 0) {
      componentManager.startComponents(specs);
    }
  }

}
