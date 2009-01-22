/*******************************************************************************
 * Copyright 2008 Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.sakaiproject.kernel.persistence;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.persistence.DataSourceService;
import org.sakaiproject.kernel.component.core.SharedClassLoader;
import org.sakaiproject.kernel.component.core.SharedClassLoaderContainer;
import org.sakaiproject.kernel.persistence.dbcp.DataSourceServiceImpl;
import org.sakaiproject.kernel.persistence.eclipselink.EntityManagerProvider;
import org.sakaiproject.kernel.persistence.geronimo.TransactionManagerProvider;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;

/**
 * Configuration module for persistence bindings.
 */
public class PersistenceModule extends AbstractModule {
  private static final Log LOG = LogFactory.getLog(PersistenceModule.class);

  private final Kernel kernel;
  private Properties properties;

  public PersistenceModule(Kernel kernel) {
    // the KernelModule and PersistenceModule classes were performing the same
    // properties read up. The activator now loads both of these modules into
    // the same injector so only one of them needs to read it up and
    // KernelModule does it first.
    this.kernel = kernel;
  }

  public PersistenceModule(Kernel kernel, Properties properties) {
    this.kernel = kernel;
    this.properties = properties;
  }

  /**
   * {@inheritDoc}
   *
   * @see com.google.inject.AbstractModule#configure()
   */
  @Override
  protected void configure() {
    if (properties != null) {
      LOG.info("Loading supplied properties");
      Names.bindProperties(this.binder(), properties);
    }

    SharedClassLoaderContainer container =
      kernel.getService(SharedClassLoaderContainer.class);

    bind(SharedClassLoader.class).toInstance(
        (SharedClassLoader) container.getManagedObject());
    bind(EntityManager.class).toProvider(EntityManagerProvider.class).in(
        Scopes.SINGLETON);
    bind(DataSource.class).toProvider(DataSourceServiceImpl.class).in(
        Scopes.SINGLETON);
    bind(TransactionManager.class).toProvider(TransactionManagerProvider.class)
        .in(Scopes.SINGLETON);

    // bind the services
    bind(DataSourceService.class).to(DataSourceServiceImpl.class).in(
        Scopes.SINGLETON);
  }
}
