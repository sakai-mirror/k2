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
package org.sakaiproject.kernel.component.core.test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.sakaiproject.kernel.component.core.PackageRegistryServiceImpl;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * 
 */
public class PackageRegistryServiceTest {



  /**
   * Test method for {@link org.sakaiproject.kernel.component.core.PackageRegistryServiceImpl#addExport(java.lang.String, java.lang.ClassLoader)}.
   */
  @Test
  public void testAddExport() {
    
    ClassLoader apiLoader = new URLClassLoader(new URL[0]);
    ClassLoader special = new URLClassLoader(new URL[0]);
    ClassLoader specialsomewhere = new URLClassLoader(new URL[0]);
    PackageRegistryServiceImpl registry = new PackageRegistryServiceImpl();
    registry.addExport("org.sakaiproject.kernel.api", apiLoader);
    registry.addExport("org.sakaiproject.kernel.api.something.special", special);
    registry.addExport("org.sakaiproject.kernel.api.something.special2.somewhere", specialsomewhere);
    registry.addExport("org.sakaiproject.kernel.api.something.special.somewhere.else", specialsomewhere);
    
    assertNull(registry.findClassloader("com.ibm"));
    assertNull(registry.findClassloader("org.sakaiproject.kernel"));
    assertSame(apiLoader, registry.findClassloader("org.sakaiproject.kernel.api"));
    assertSame(apiLoader, registry.findClassloader("org.sakaiproject.kernel.api.user.util.other.Test123"));
    assertSame(apiLoader, registry.findClassloader("org.sakaiproject.kernel.api.something"));
    assertSame(apiLoader, registry.findClassloader("org.sakaiproject.kernel.api.something.special2"));
    assertSame(special, registry.findClassloader("org.sakaiproject.kernel.api.something.special"));
    assertSame(special, registry.findClassloader("org.sakaiproject.kernel.api.something.special.Test12345"));
    assertSame(specialsomewhere, registry.findClassloader("org.sakaiproject.kernel.api.something.special2.somewhere"));
    assertSame(specialsomewhere, registry.findClassloader("org.sakaiproject.kernel.api.something.special2.somewhere.xsye.ses.Test321"));
    assertSame(special, registry.findClassloader("org.sakaiproject.kernel.api.something.special.somewhere.Test213"));
    assertSame(specialsomewhere, registry.findClassloader("org.sakaiproject.kernel.api.something.special.somewhere.else.Test213"));
    
    registry.removeExport("org.sakaiproject.kernel.api.something.special");
    assertSame(apiLoader, registry.findClassloader("org.sakaiproject.kernel.api.something.special"));
    assertSame(apiLoader, registry.findClassloader("org.sakaiproject.kernel.api.something.special.Test12345"));
    assertSame(specialsomewhere, registry.findClassloader("org.sakaiproject.kernel.api.something.special2.somewhere"));
    assertSame(specialsomewhere, registry.findClassloader("org.sakaiproject.kernel.api.something.special2.somewhere.xsye.ses.Test321"));
    assertSame(apiLoader, registry.findClassloader("org.sakaiproject.kernel.api.something.special.somewhere.Test213"));
    assertSame(specialsomewhere, registry.findClassloader("org.sakaiproject.kernel.api.something.special.somewhere.else.Test213"));
  }


}
