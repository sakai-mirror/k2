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
package org.sakaiproject.kernel.authz;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sakaiproject.kernel.api.ComponentActivatorException;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.KernelManager;
import org.sakaiproject.kernel.api.authz.AccessControlStatement;
import org.sakaiproject.kernel.api.authz.ReferenceResolverService;
import org.sakaiproject.kernel.api.authz.ReferencedObject;
import org.sakaiproject.kernel.api.authz.SubjectStatement;
import org.sakaiproject.kernel.api.authz.SubjectStatement.SubjectType;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.authz.simple.JcrAccessControlStatementImpl;
import org.sakaiproject.kernel.authz.simple.JcrSubjectStatement;
import org.sakaiproject.kernel.model.AclIndexBean;
import org.sakaiproject.kernel.test.KernelIntegrationBase;
import org.sakaiproject.kernel.util.PathUtils;

import java.util.List;

import javax.jcr.Node;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 */
public class AclListenerTest extends KernelIntegrationBase {

  private static Kernel kernel;
  private static JCRNodeFactoryService jcrService;
  private static EntityManager entityManager;
  private static ReferenceResolverService referenceResolverService;
  private final String TEST_FILE = "testFile.txt";

  @BeforeClass
  public static void beforeClass() throws ComponentActivatorException {
    KernelIntegrationBase.beforeClass();

    KernelManager manager = new KernelManager();
    kernel = manager.getKernel();

    jcrService = kernel.getService(JCRNodeFactoryService.class);
    entityManager = kernel.getService(EntityManager.class);
    referenceResolverService = kernel
        .getService(ReferenceResolverService.class);
  }

  @AfterClass
  public static void afterClass() {
    KernelIntegrationBase.afterClass();
  }

  @Test
  public void createAcl() throws Exception {
    String path = PathUtils.getUserPrefix("testUser1") + TEST_FILE;
    Node node = jcrService.createFile(path);
    node.save();
    node.getSession().save();

    Query query = entityManager
        .createNamedQuery(AclIndexBean.Queries.FINDBY_PATH);
    query.setParameter(AclIndexBean.QueryParams.FINDBY_PATH_PATH, path);
    List<AclIndexBean> results = query.getResultList();
    assertTrue(results.size() == 0);

    ReferencedObject ro = referenceResolverService.resolve(path);
    ReferencedObject parent = ro.getParent();
    parent = parent.getParent();

    SubjectStatement subjectStatement = new JcrSubjectStatement(
        SubjectType.GROUP, "group1:maintain", "read");
    AccessControlStatement grantReadToHttpGetInheritable = new JcrAccessControlStatementImpl(
        subjectStatement, "httpget", true, true);
    parent.addAccessControlStatement(grantReadToHttpGetInheritable);

    results = query.getResultList();
    node.getSession().save();

    assertEquals(1, results.size());
  }
}
