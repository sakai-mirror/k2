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
package org.sakaiproject.kernel.component;

import org.sakaiproject.kernel.api.ComponentActivator;
import org.sakaiproject.kernel.api.ComponentDependency;
import org.sakaiproject.kernel.api.ComponentManager;
import org.sakaiproject.kernel.api.ComponentSpecification;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.KernelConfigurationException;

import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class ComponentManagerImpl implements ComponentManager {

  private static final String DEFAULT_COMPONENTS_PROPERTIES = "kernel.properties";
  private static final String DEFAULT_COMPONENTS = "components";
  private Kernel kernel;
  private Map<ComponentSpecification, ComponentActivator> components = new ConcurrentHashMap<ComponentSpecification, ComponentActivator>();

  /**
   * @param kernel
   */
  public ComponentManagerImpl(KernelImpl kernel) {
    this.kernel = kernel;
    kernel.setComponentManager(this);

  }

  /**
   * @throws KernelConfigurationException
   * 
   */
  public void start() throws KernelConfigurationException {
    startDefaultComponents();
  }

  /**
   * 
   */
  public void stop() {
    stopComponents();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.sakaiproject.kernel.api.ComponentManager#startComponent(org.sakaiproject
   * .kernel.api.ComponentSpecification)
   */
  @SuppressWarnings("unchecked")
  public boolean startComponent(ComponentSpecification spec)
      throws KernelConfigurationException {
    // create a new component classloader
    // load the component spec.
    ClassLoader cl = kernel.getParentComponentClassLoader();
    URLClassLoader componentClassloader = new URLClassLoader(spec
        .getClassPathURLs(), cl);
    ClassLoader currentClassloader = Thread.currentThread()
        .getContextClassLoader();
    Thread.currentThread().setContextClassLoader(componentClassloader);
    try {
      Class<ComponentActivator> clazz = (Class<ComponentActivator>) componentClassloader
          .loadClass(spec.getComponentActivatorClassName());

      for (ComponentDependency dependant : spec.getDependencies()) {
        if (dependant.isManaged()) {
          startComponent(dependant.getComponentSpec());
        }
      }

      ComponentActivator activator = clazz.newInstance();

      activator.activate(kernel);

      components.put(spec, activator);
      return true;
    } catch (ClassNotFoundException e) {
      throw new KernelConfigurationException("Unable to start component "
          + spec + " cause:" + e.getMessage());
    } catch (InstantiationException e) {
      throw new KernelConfigurationException("Unable to start component "
          + spec + " cause:" + e.getMessage());
    } catch (IllegalAccessException e) {
      throw new KernelConfigurationException("Unable to start component "
          + spec + " cause:" + e.getMessage());
    } finally {
      Thread.currentThread().setContextClassLoader(currentClassloader);
    }
  }

  /**
   * Start a default set of components, how the default set is specified is an
   * implementation detail.
   * 
   * @return true if the default set start was sucessfull.
   * @throws KernelConfigurationException
   */
  @SuppressWarnings("unchecked")
  protected boolean startDefaultComponents()
      throws KernelConfigurationException {
    try {
      // load a list of components urls from a properties file.
      Properties p = new Properties();
      InputStream in = this.getClass().getResourceAsStream(
          DEFAULT_COMPONENTS_PROPERTIES);
      if (in != null) {
        p.load(in);
        in.close();
      }
      String dc = p.getProperty(DEFAULT_COMPONENTS);
      if (dc != null) {
        String[] defaultComponents = dc.split(";");
        for (String d : defaultComponents) {
          ComponentSpecification spec = null;
          if (d.startsWith("class:")) {
            String activatorName = d.substring("class:".length());
            Class<ComponentSpecification> aclazz = (Class<ComponentSpecification>) this
                .getClass().getClassLoader().loadClass(activatorName);
            spec = aclazz.newInstance();
          } else {
            spec = new URLComponentSpecificationImpl(d);
          }
          startComponent(spec);
        }
      }
      return true;
    } catch (Exception ex) {
      throw new KernelConfigurationException("Unable To start components "
          + ex.getMessage(), ex);
    }
  }

  /**
   * Stop all components.
   * 
   * @return
   */
  protected boolean stopComponents() {
    for (ComponentSpecification spec : components.keySet()) {
      stopComponent(spec);
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.sakaiproject.kernel.api.ComponentManager#stopComponent(org.sakaiproject
   * .kernel.api.ComponentSpecification)
   */
  public boolean stopComponent(ComponentSpecification spec) {
    for (ComponentDependency dependant : spec.getDependencies()) {
      if (dependant.isManaged()) {
        stopComponent(dependant.getComponentSpec());
      }
    }
    ComponentActivator activator = components.get(spec);
    if (activator != null) {
      activator.deactivate();
    }
    return false;
  }

}
