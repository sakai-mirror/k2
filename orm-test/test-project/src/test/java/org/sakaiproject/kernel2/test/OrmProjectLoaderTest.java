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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.sakaiproject.kernel.api.ComponentActivator;
import org.sakaiproject.kernel.api.ComponentActivatorException;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.KernelManager;
import org.sakaiproject.kernel.api.RequiresStop;
import org.sakaiproject.kernel.api.ServiceSpec;
import org.sakaiproject.kernel.api.ShutdownService;
import org.sakaiproject.kernel.component.KernelLifecycle;
import org.sakaiproject.kernel.jpa.Employee;
import org.sakaiproject.kernel.util.FileUtil;
import org.sakaiproject.kernel2.mp2.Room;

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

  private static EntityManager em;

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

    // activate kernel core stuff
    ComponentActivator activator = new org.sakaiproject.kernel.Activator();
    activator.activate(kernel);

    // register with the shutdown service
    ShutdownService ss = kernel.getServiceManager().getService(
        new ServiceSpec(ShutdownService.class));
    for (Class<?> c : org.sakaiproject.kernel.Activator.SERVICE_CLASSES) {
      Object s = kernel.getServiceManager().getService(new ServiceSpec(c));
      if (s instanceof RequiresStop) {
        ss.register((RequiresStop) s);
      }
    }

    // // activate this project
    // activator = new org.sakaiproject.kernel.jpa.Activator();
    // activator.activate(kernel);
    //
    // // activate model project 2
    // activator = new org.sakaiproject.kernel2.mp2.Activator();
    // activator.activate(kernel);

    em = kernel.getService(EntityManager.class);
  }

  @AfterClass
  public static void afterClass() {
    if (em != null) {
      em.close();
    }
    try {
      if (kernelLifecycle != null) {
        kernelLifecycle.stop();
        kernelLifecycle.destroy();
      }
    } catch (Exception ex) {
      LOG.info("Failed to stop kernel ", ex);
    }
  }

  // ignore this because it is not relavent any more.
  @Ignore
  public void countPersistence() throws Exception {
    // count the number of persistence files found on the classpath
    int count = 0;
    for (Enumeration<URL> orms = this.getClass().getClassLoader().getResources(
        "META-INF/persistence.xml"); orms.hasMoreElements();) {
      URL orm = orms.nextElement();
      System.out.println("** pers:" + count + ": " + orm);
      count++;
    }
    System.out.println("*** Found " + count
        + " persistence.xml files on the classpath.");
    assertTrue(count > 1);
  }

  // ignore this because it is not relavent any more.
  @Ignore
  public void countOrm() throws Exception {
    // count the number of ORMs found on the classpath
    int count = 0;
    for (Enumeration<URL> orms = this.getClass().getClassLoader().getResources(
        "META-INF/orm.xml"); orms.hasMoreElements();) {
      URL orm = orms.nextElement();
      System.out.println("** orm:" + count + ": " + orm);
      count++;
    }
    System.out.println("*** Found " + count
        + " orm.xml files on the classpath.");
    assertTrue(count > 1);
  }

  @Test
  public void accessRemoteModel() throws Exception {
    em.getTransaction().begin();

    // look for model from kernel
    Query selectSubject = em
        .createQuery("select s from SubjectPermissionBean s");
    assertEquals(0, selectSubject.getResultList().size());

    // look for model from model-project-2
    Query selectProject = em.createQuery("select r from Room r");
    assertEquals(0, selectProject.getResultList().size());

    // add something
    Room r = new Room();
    r.id = 1;
    r.number = 1;
    em.persist(r);

    em.flush();

    // single first name entry
    Query selectByNumber = em
        .createQuery("select r from Room r where r.number = ?1");
    selectByNumber.setParameter(1, 1);
    List<Room> employees = selectByNumber.getResultList();
    assertNotNull(employees);
    assertEquals(1, employees.size());

    em.getTransaction().rollback();
  }

  /**
   * Ignore this test because there is a problem in it.
   * 
   * @throws Exception
   */
  @Ignore
  public void accesLocalModel() throws Exception {
    em.getTransaction().begin();

    Query select = em.createQuery("select e from Employee e");
    assertEquals(0, select.getResultList().size());

    Employee e = new Employee();
    e.setFirstName("Carl");
    em.persist(e);
    em.flush();

    assertEquals(1, select.getResultList().size());

    em.getTransaction().rollback();
  }
}
