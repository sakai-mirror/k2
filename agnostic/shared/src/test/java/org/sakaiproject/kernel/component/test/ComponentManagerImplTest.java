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
package org.sakaiproject.kernel.component.test;

import org.junit.Before;
import org.junit.Test;
import org.sakaiproject.kernel.api.ComponentManager;
import org.sakaiproject.kernel.api.ComponentSpecification;
import org.sakaiproject.kernel.component.ComponentManagerImpl;
import org.sakaiproject.kernel.component.KernelImpl;
import org.sakaiproject.kernel.component.ServiceManagerImpl;
import org.sakaiproject.kernel.component.test.mock.MockComponentSpecificationImpl;

/**
 *
 */
public class ComponentManagerImplTest {

  private KernelImpl kernel;

  @Before
  public void before() {
    kernel = new KernelImpl();
    kernel.start();
    ServiceManagerImpl serviceManager = new ServiceManagerImpl(kernel);
    serviceManager.start();
  }
  /**
   * Test method for {@link org.sakaiproject.kernel.component.ComponentManagerImpl#ComponentManagerImpl(org.sakaiproject.kernel.component.KernelImpl)}.
   */
  @Test
  public void testComponentManagerImpl() {
    ComponentManager cm = new ComponentManagerImpl(kernel);
  }

  /**
   * Test method for {@link org.sakaiproject.kernel.component.ComponentManagerImpl#start()}.
   */
  @Test
  public void testStart() throws Exception {
    ComponentManagerImpl cm = new ComponentManagerImpl(kernel);
    cm.start();
    cm.stop();
  }

  /**
   * Test method for {@link org.sakaiproject.kernel.component.ComponentManagerImpl#stop()}.
   */
  @Test
  public void testStop() throws Exception {
    ComponentManagerImpl cm = new ComponentManagerImpl(kernel);
    cm.start();
    cm.stop();
  }

  /**
   * Test method for {@link org.sakaiproject.kernel.component.ComponentManagerImpl#startComponent(org.sakaiproject.kernel.api.ComponentSpecification)}.
   */
  @Test
  public void testStartComponent() throws Exception {
    ComponentManagerImpl cm = new ComponentManagerImpl(kernel);
    cm.start();
    cm.startComponent(new MockComponentSpecificationImpl());
    cm.stop();
  }


  /**
   * Test method for {@link org.sakaiproject.kernel.component.ComponentManagerImpl#stopComponent(org.sakaiproject.kernel.api.ComponentSpecification)}.
   */
  @Test
  public void testStopComponent() throws Exception {
    ComponentManagerImpl cm = new ComponentManagerImpl(kernel);
    cm.start();
    ComponentSpecification spec = new MockComponentSpecificationImpl();
    cm.startComponent(spec);
    cm.stopComponent(spec);
    cm.stop();
  }

}
