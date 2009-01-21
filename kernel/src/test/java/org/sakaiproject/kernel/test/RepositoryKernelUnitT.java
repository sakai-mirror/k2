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
package org.sakaiproject.kernel.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sakaiproject.kernel.api.ComponentActivatorException;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.KernelManager;
import org.sakaiproject.kernel.api.jcr.JCRService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
import org.sakaiproject.kernel.util.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * 
 */
public class RepositoryKernelUnitT extends KernelIntegrationBase {

  private static boolean shutdown;

  @BeforeClass
  public static void beforeThisClass() throws ComponentActivatorException {
    shutdown = KernelIntegrationBase.beforeClass();
  }

  @AfterClass
  public static void afterClass() {
    KernelIntegrationBase.afterClass(shutdown);
  }

  @Test
  public void testJCRNodeFactory() throws JCRNodeFactoryServiceException,
      LoginException, RepositoryException, IOException {
    KernelManager km = new KernelManager();
    Kernel kernel = km.getKernel();
    JCRNodeFactoryService jcrNodeFactoryService = kernel
        .getService(JCRNodeFactoryService.class);
    JCRService jcrService = kernel.getService(JCRService.class);
    Session session = jcrService.login();
    jcrNodeFactoryService.createFile("/test/test.txt");
    session.save();
    jcrNodeFactoryService.createFolder("/test/newfolder");
    session.save();
    ByteArrayInputStream bais = new ByteArrayInputStream("testvalue".getBytes("UTF-8"));
    jcrNodeFactoryService.setInputStream("/test/test.txt", bais);
    session.save();
    String result = IOUtils.readFully(jcrNodeFactoryService.getInputStream("/test/test.txt"),"UTF-8");
    assertEquals("testvalue", result);
    Node n = jcrNodeFactoryService.getNode("/test/test.txt");
    assertNotNull(n);
    jcrService.logout();
  }

}
