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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.ComponentActivator;
import org.sakaiproject.kernel.api.ComponentActivatorException;
import org.sakaiproject.kernel.api.ComponentDependency;
import org.sakaiproject.kernel.api.ComponentManager;
import org.sakaiproject.kernel.api.ComponentSpecification;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.KernelConfigurationException;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class ComponentManagerImpl implements ComponentManager {

  private static final Log LOG = LogFactory.getLog(ComponentManagerImpl.class);
  private static final String DEFAULT_COMPONENTS_PROPERTIES = "res://kernel.properties";
  private static final String DEFAULT_COMPONENTS = "components";
  private Kernel kernel;
  private Map<ComponentSpecification, ComponentActivator> components = new ConcurrentHashMap<ComponentSpecification, ComponentActivator>();
  private Map<String, ComponentSpecification> componentsByName = new ConcurrentHashMap<String, ComponentSpecification>();

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
    LOG.info("Starting Component Manager");
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
    URL[] classPathURLs = spec.getClassPathURLs();
    ClassLoader currentClassloader = Thread.currentThread()
        .getContextClassLoader();
    ClassLoader componentClassloader = currentClassloader;
    if (classPathURLs != null) {
      componentClassloader = new URLClassLoader(spec.getClassPathURLs(), cl);
      Thread.currentThread().setContextClassLoader(componentClassloader);
    }
    try {
      LOG.info("Activating " + spec + " with Class "
          + spec.getComponentActivatorClassName());
      Class<ComponentActivator> clazz = (Class<ComponentActivator>) componentClassloader
          .loadClass(spec.getComponentActivatorClassName());

      for (ComponentDependency dependant : spec.getDependencies()) {
        if (dependant.isManaged()) {
          startComponent(componentsByName.get(dependant.getComponentName()));
        }
      }

      ComponentActivator activator = clazz.newInstance();

      activator.activate(kernel);

      components.put(spec, activator);
      componentsByName.put(spec.getName(),spec);
      return true;
    } catch (ClassNotFoundException e) {
      throw new KernelConfigurationException("Unable to start component "
          + spec + " cause:" + e.getMessage(), e);
    } catch (InstantiationException e) {
      throw new KernelConfigurationException("Unable to start component "
          + spec + " cause:" + e.getMessage(), e);
    } catch (IllegalAccessException e) {
      throw new KernelConfigurationException("Unable to start component "
          + spec + " cause:" + e.getMessage(), e);
    } catch (ComponentActivatorException e) {
      throw new KernelConfigurationException("Unable to start component "
          + spec + " cause:" + e.getMessage(), e);
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
      InputStream in = ResourceLoader
          .openResource(DEFAULT_COMPONENTS_PROPERTIES);
      try {
        if (in != null) {
          p.load(in);
          in.close();
        }
      } finally {
        if (in != null) {
          in.close();
        }
      }
      String dc = p.getProperty(DEFAULT_COMPONENTS);
      if (dc != null) {
        String[] defaultComponents = dc.split(";");
        for (String d : defaultComponents) {
          d = d.trim();
          if (d.length() > 0) {
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
    components.clear();
    componentsByName.clear();
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
        stopComponent(componentsByName.get(dependant.getComponentName()));
      }
    }
    ComponentActivator activator = components.get(spec);
    if (activator != null) {
      activator.deactivate();
    }
    components.remove(spec);
    componentsByName.remove(spec.getName());
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.sakaiproject.kernel.api.ComponentManager#getComponents()
   */
  public ComponentSpecification[] getComponents() {
    return components.keySet().toArray(new ComponentSpecification[0]);
  }


}
