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
import org.sakaiproject.kernel.api.ComponentSpecificationException;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.KernelConfigurationException;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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
  /**
   * A map of components that have been started, indexed by spec.
   */
  private Map<ComponentSpecification, ComponentActivator> components = new ConcurrentHashMap<ComponentSpecification, ComponentActivator>();
  /**
   * A map of known components indexed by name
   */
  private Map<String, ComponentSpecification> componentsByName = new ConcurrentHashMap<String, ComponentSpecification>();
  /**
   * A map of started components indexed by name
   */
  private Map<String, ComponentSpecification> startedComponents = new ConcurrentHashMap<String, ComponentSpecification>();

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

      for (ComponentDependency dependant : spec.getDependencies()) {
        if (dependant.isManaged()) {
          startComponent(componentsByName.get(dependant.getComponentName()));
        }
      }

      LOG.info("Activating " + spec + " with Class "
          + spec.getComponentActivatorClassName());
      Class<ComponentActivator> clazz = (Class<ComponentActivator>) componentClassloader
          .loadClass(spec.getComponentActivatorClassName());

      ComponentActivator activator = clazz.newInstance();

      activator.activate(kernel);

      components.put(spec, activator);
      startedComponents.put(spec.getName(), spec);
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
      List<ComponentSpecification> toStart = new ArrayList<ComponentSpecification>();
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
            componentsByName.put(spec.getName(), spec);
            toStart.add(spec);
          }
        }
      }
      for (ComponentSpecification spec : getStartOrder(toStart)) {
        startComponent(spec);
      }
      return true;
    } catch (Exception ex) {
      throw new KernelConfigurationException("Unable To start components "
          + ex.getMessage(), ex);
    }
  }

  /**
   * Work out the start order of all
   * 
   * @param toStart
   * @return
   * @throws ComponentSpecificationException
   */
  private List<ComponentSpecification> getStartOrder(
      List<ComponentSpecification> toStart)
      throws ComponentSpecificationException {
    final Map<ComponentSpecification, Integer> speclevel = new HashMap<ComponentSpecification, Integer>();
    List<ComponentSpecification> errors = new ArrayList<ComponentSpecification>();
    List<ComponentSpecification> unstable = new ArrayList<ComponentSpecification>();
    // Analyse the list, pulling in additional dependencies, and assigning each
    // dependency a level.
    // Convergence will happen in at worst the size of the populated speclevel
    // list.

    unstable.add(toStart.get(0));
    speclevel.put(toStart.get(0), 0);
    for (int i = 0; i < speclevel.size() + 1 && errors.size() == 0
        && unstable.size() > 0; i++) {
      unstable.clear();
      for (ComponentSpecification spec : toStart) {
        Integer plevel = speclevel.get(spec);
        if (plevel == null) {
          plevel = 0;
          speclevel.put(spec, plevel);
          unstable.add(spec);
        }
        for (ComponentDependency d : spec.getDependencies()) {
          ComponentSpecification cs = componentsByName
              .get(d.getComponentName());
          if (cs == null) {
            errors.add(spec);
          } else {
            Integer dlevel = speclevel.get(cs);
            if (dlevel == null || dlevel <= plevel) {
              dlevel = plevel + 1;
              speclevel.put(cs, dlevel);
              unstable.add(cs);
            }
          }
        }
      }
    }
    // look for instability or missing dependencies
    StringBuilder message = new StringBuilder();
    if (unstable.size() > 0) {
      message
          .append("\n\tERROR:There is a cyclic dependency between components, that must be removed\n");
      for (ComponentSpecification cs : unstable) {
        message.append("\t\tUnstable Component ").append(
            cs.getDependencyDescription()).append("\n");
      }
    }
    if (errors.size() > 0) {
      message
          .append("\n\tERROR:The component dependency graph has unsatisfield dependencies\n");
      for (ComponentSpecification spec : errors) {
        for (ComponentDependency d : spec.getDependencies()) {
          if (!componentsByName.containsKey(d.getComponentName())) {
            message.append("\t\tComponent ").append(spec.getName()).append(
                " depends on unsatisfied depedency ").append(
                d.getComponentName()).append("\n");
          }
        }
      }
    }
    if (message.length() > 0) {
      throw new ComponentSpecificationException(
          "Unable to start the component tree dur to the following errors "
              + message.toString());
    }
    // we now have a level sorted list, extract the levels in order leaving out
    // the components that are already started
    List<ComponentSpecification> notStarted = new ArrayList<ComponentSpecification>();
    for (ComponentSpecification spec : toStart) {
      if (!startedComponents.containsKey(spec)) {
        notStarted.add(spec);
      }
    }
    // sort according to the level
    Collections.sort(notStarted, new Comparator<ComponentSpecification>() {

      public int compare(ComponentSpecification o1, ComponentSpecification o2) {
        return speclevel.get(o1)-speclevel.get(o2);
      }

    });
    
    return notStarted;

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
    startedComponents.clear();
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
        stopComponent(startedComponents.get(dependant.getComponentName()));
      }
    }
    ComponentActivator activator = components.get(spec);
    if (activator != null) {
      activator.deactivate();
    }
    components.remove(spec);
    startedComponents.remove(spec.getName());
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
