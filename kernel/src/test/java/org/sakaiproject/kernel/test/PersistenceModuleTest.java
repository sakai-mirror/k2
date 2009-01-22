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
package org.sakaiproject.kernel.test;

import static org.junit.Assert.assertNotNull;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sakaiproject.kernel.KernelModule;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.KernelManager;
import org.sakaiproject.kernel.component.KernelLifecycle;
import org.sakaiproject.kernel.persistence.PersistenceModule;
import org.sakaiproject.kernel.util.KernelComponentProperties;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;

/**
 *
 */
public class PersistenceModuleTest {
  @BeforeClass
  public static void beforeClass() {
    KernelIntegrationBase.disableKernelStartup();
  }

  @AfterClass
  public static void afterClass() {
    KernelIntegrationBase.enableKernelStartup();
  }

  @Test
  public void loadModule() throws Exception {
    Properties props = new KernelComponentProperties().getProperties();
    KernelLifecycle kernelLifecycle = new KernelLifecycle();
    kernelLifecycle.start();

    KernelManager kernelManager = new KernelManager();
    Kernel kernel = kernelManager.getKernel();

    Injector injector = Guice.createInjector(new KernelModule(kernel, props),
        new PersistenceModule(kernel));
    DataSource ds = injector.getInstance(DataSource.class);
    EntityManager em = injector.getInstance(EntityManager.class);
    TransactionManager tm = injector.getInstance(TransactionManager.class);
    assertNotNull(ds);
    assertNotNull(em);
    assertNotNull(tm);
    em.close();
  }
}
