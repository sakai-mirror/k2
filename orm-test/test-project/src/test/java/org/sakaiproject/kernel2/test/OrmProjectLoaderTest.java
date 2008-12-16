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
package org.sakaiproject.kernel2.test;

import static org.junit.Assert.assertNotNull;

import static org.junit.Assert.assertEquals;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sakaiproject.jpa.Employee;
import org.sakaiproject.kernel.Activator;
import org.sakaiproject.kernel.KernelModule;
import org.sakaiproject.kernel.api.ComponentActivator;
import org.sakaiproject.kernel.api.ComponentActivatorException;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.KernelManager;
import org.sakaiproject.kernel.api.RequiresStop;
import org.sakaiproject.kernel.api.ServiceSpec;
import org.sakaiproject.kernel.api.ShutdownService;
import org.sakaiproject.kernel.component.KernelLifecycle;
import org.sakaiproject.kernel.persistence.PersistenceModule;
import org.sakaiproject.kernel.util.FileUtil;
import org.sakaiproject.kernel2.mp2.Campus;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class OrmProjectLoaderTest {
  private static final Log LOG = LogFactory.getLog(OrmProjectLoaderTest.class);

  private static KernelLifecycle kernelLifecycle;
  private static KernelManager kernelManager;
  private static Injector injector;

  private EntityManager em;

  @BeforeClass
  public static void beforeClass() throws ComponentActivatorException {
    // If there are problems with startup and shutdown, these will prevent the
    // problem
    File jcrBase = new File("target/jcr");
    File dbBase = new File("target/testdb");
    System.err
        .println("==========================================================================");
    System.err.println("Removing all previous JCR and DB traces from "
        + jcrBase.getAbsolutePath() + " " + dbBase.getAbsolutePath());

    FileUtil.deleteAll(jcrBase);
    FileUtil.deleteAll(dbBase);
    System.err
        .println("==========================================================================");

    kernelLifecycle = new KernelLifecycle();
    kernelLifecycle.start();

    kernelManager = new KernelManager();
    Kernel kernel = kernelManager.getKernel();

    // the persistence module doesn't need the properties to be loaded/bound
    // unless the datasource is going to be used.
    injector = Guice.createInjector(new KernelModule(kernel),
        new PersistenceModule());

    // activate kernel core stuff
    ComponentActivator activator = new Activator();
    activator.activate(kernel);

    // register with the shutdown service
    ShutdownService ss = kernel.getServiceManager().getService(
        new ServiceSpec(ShutdownService.class));
    for (Class<?> c : Activator.SERVICE_CLASSES) {
      Object s = kernel.getServiceManager().getService(new ServiceSpec(c));
      if (s instanceof RequiresStop) {
        ss.register((RequiresStop) s);
      }
    }

    // activate model project 1
    activator = new org.sakaiproject.kernel2.mp1.Activator();
    activator.activate(kernel);

    // activate model project 2
    activator = new org.sakaiproject.kernel2.mp2.Activator();
    activator.activate(kernel);
  }

  @Before
  public void before() {
    em = injector.getInstance(EntityManager.class);
  }

  @After
  public void after() {
//    em.close();
  }

  @AfterClass
  public static void afterClass() {
    try {
      kernelLifecycle.stop();
    } catch (Exception ex) {
      LOG.info("Failed to stop kernel ", ex);
    }
  }

  @Test
  public void countOrms() throws Exception {
    // count the number of persistence files found on the classpath
    int count = 0;
    for (Enumeration<URL> orms = this.getClass().getClassLoader().getResources(
        "META-INF/persistence.xml"); orms.hasMoreElements();) {
      URL orm = orms.nextElement();
      System.out.println("** orm:" + count + ": " + orm);
      count++;
    }
    System.out.println("*** Found " + count
        + " orm.xml files on the classpath.");
    assertEquals(3, count);

    // count the number of ORMs found on the classpath
    count = 0;
    for (Enumeration<URL> orms = this.getClass().getClassLoader().getResources(
        "META-INF/orm.xml"); orms.hasMoreElements();) {
      URL orm = orms.nextElement();
      System.out.println("** orm:" + count + ": " + orm);
      count++;
    }
    System.out.println("*** Found " + count
        + " orm.xml files on the classpath.");
    assertEquals(3, count);
  }

  @Test
  public void accessLocalModel() throws Exception {
    em.getTransaction().begin();

    Campus c = new Campus();
    c.id = 1;
    c.name = "Main";
    em.persist(c);
    em.flush();

    Query q = em.createQuery("select c from Campus c where c.id = ?1");
    q.setParameter(1, 1);
    Campus campus = (Campus) q.getSingleResult();
    assertNotNull(campus);

    em.getTransaction().rollback();
  }

  public void accessRemoteModel() throws Exception {
    em.getTransaction().begin();

    // add something
    Employee e1 = new Employee();
    e1.setFirstName("Carl");
    e1.setLastName("Hall");
    em.persist(e1);
    em.flush();

    // single first name entry
    Query selectByFirstName = em
        .createQuery("select e from Employee e where e.firstName = ?1");
    selectByFirstName.setParameter(1, "Carl");
    List<Employee> employees = (List<Employee>) selectByFirstName
        .getResultList();
    assertNotNull(employees);
    assertEquals(1, employees.size());

    em.getTransaction().rollback();
  }
}
