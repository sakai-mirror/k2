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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.sakaiproject.kernel.api.ComponentSpecificationException;
import org.sakaiproject.kernel.component.core.Maven2DependencyResolver;
import org.sakaiproject.kernel.component.model.ClasspathDependencyImpl;

import java.net.URL;

/**
 * 
 */
public class Maven2DependencyResolverTest {

  /**
   * Test method for {@link org.sakaiproject.kernel.component.core.Maven2DependencyResolver#resolve(java.net.URL[], org.sakaiproject.kernel.api.ClasspathDependency)}.
   * @throws ComponentSpecificationException 
   */
  @Test
  public void testResolve() throws ComponentSpecificationException {
    Maven2DependencyResolver m2resolver = new Maven2DependencyResolver();
    ClasspathDependencyImpl dep = new ClasspathDependencyImpl();
    dep.setGroupId("com.google.code.guice");
    dep.setArtifactId("guice");
    dep.setVersion("1.0");
    URL[] urls = new URL[1];
    urls[0] = m2resolver.resolve(null, dep);
    assertNotNull(urls[0]);
    assertNull(m2resolver.resolve(urls, dep));
    
  }

}
