/*
 * Licensed to the Sakai Foundation (SF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership. The SF licenses this file to you
 * under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the
 * License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under the License.
 */
package org.sakaiproject.kernel.util;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.KernelManager;
import org.sakaiproject.kernel.api.jcr.JCRConstants;
import org.sakaiproject.kernel.api.jcr.JCRService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.jcr.jackrabbit.sakai.SakaiJCRCredentials;
import org.sakaiproject.kernel.test.KernelIntegrationBase;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Session;
import javax.jcr.Value;

/**
 *
 */
public class JcrUtilsT {
  private static JCRNodeFactoryService nodeFactory;
  private static boolean shutdown;
  private static Session session;

  private final String randomFile1 = "/userenv/test/random1.file";

  private Node node;

  @BeforeClass
  public static void beforeThisClass() throws Exception {
    shutdown = KernelIntegrationBase.beforeClass();

    KernelManager km = new KernelManager();
    Kernel kernel = km.getKernel();
    nodeFactory = kernel.getService(JCRNodeFactoryService.class);

    JCRService jcrService = kernel.getService(JCRService.class);

    // login to the repo with super admin
    SakaiJCRCredentials credentials = new SakaiJCRCredentials();
    session = jcrService.getRepository().login(credentials);
    jcrService.setSession(session);
  }

  @Before
  public void setUp() throws Exception {
    node = nodeFactory.createFile(randomFile1, "text/plain");
  }

  @After
  public void tearDown() throws Exception {
    node.remove();
    session.save();
  }

  @AfterClass
  public static void afterClass() {
    KernelIntegrationBase.afterClass(shutdown);
  }

  @Test
  public void addLabel() throws Exception {
    JcrUtils.addNodeLabel(node, "test label");
    session.save();

    assertTrue(node.hasProperty(JCRConstants.JCR_LABELS));
    Property prop = node.getProperty(JCRConstants.JCR_LABELS);
    Value[] values = prop.getValues();
    assertEquals(1, values.length);
    assertEquals("test label", values[0].getString());
  }

  @Test
  public void addMultipleLabels() throws Exception {
    JcrUtils.addNodeLabel(node, "test label");
    JcrUtils.addNodeLabel(node, "another test label");
    session.save();

    assertTrue(node.hasProperty(JCRConstants.JCR_LABELS));
    Property prop = node.getProperty(JCRConstants.JCR_LABELS);
    Value[] values = prop.getValues();
    assertEquals(2, values.length);
    assertEquals("test label", values[0].getString());
    assertEquals("another test label", values[1].getString());
  }

  @Test
  public void removeLabel() throws Exception {
    JcrUtils.addNodeLabel(node, "test label");
    JcrUtils.addNodeLabel(node, "another test label");
    session.save();

    assertTrue(node.hasProperty(JCRConstants.JCR_LABELS));

    JcrUtils.removeNodeLabel(node, "test label");
    session.save();

    assertTrue(node.hasProperty(JCRConstants.JCR_LABELS));
    Property prop = node.getProperty(JCRConstants.JCR_LABELS);
    Value[] values = prop.getValues();
    assertEquals(1, values.length);
    assertEquals("another test label", values[0].getString());
  }

  @Test
  public void removeLabels() throws Exception {
    JcrUtils.addNodeLabel(node, "test label");
    JcrUtils.addNodeLabel(node, "another test label");
    JcrUtils.addNodeLabel(node, "yet another test label");
    session.save();

    assertTrue(node.hasProperty(JCRConstants.JCR_LABELS));

    JcrUtils.removeNodeLabel(node, "test label");
    session.save();

    assertTrue(node.hasProperty(JCRConstants.JCR_LABELS));
    Property prop = node.getProperty(JCRConstants.JCR_LABELS);
    Value[] values = prop.getValues();
    assertEquals(2, values.length);
    assertEquals("another test label", values[0].getString());
    assertEquals("yet another test label", values[1].getString());

    JcrUtils.removeNodeLabel(node, "yet another test label");
    session.save();

    assertTrue(node.hasProperty(JCRConstants.JCR_LABELS));
    prop = node.getProperty(JCRConstants.JCR_LABELS);
    values = prop.getValues();
    assertEquals(1, values.length);
    assertEquals("another test label", values[0].getString());
  }

  @Test
  public void removeLastLabel() throws Exception {
    JcrUtils.addNodeLabel(node, "test label");
    session.save();

    assertTrue(node.hasProperty(JCRConstants.JCR_LABELS));

    JcrUtils.removeNodeLabel(node, "test label");
    session.save();

    assertTrue(node.hasProperty(JCRConstants.JCR_LABELS));
    Property prop = node.getProperty(JCRConstants.JCR_LABELS);
    Value[] values = prop.getValues();
    assertEquals(0, values.length);
  }
}
