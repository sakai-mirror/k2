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

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sakaiproject.kernel.api.ClassExporter;
import org.sakaiproject.kernel.api.ComponentSpecificationException;
import org.sakaiproject.kernel.component.KernelImpl;
import org.sakaiproject.kernel.component.URLComponentSpecificationImpl;
import org.sakaiproject.kernel.component.core.ClassLoaderServiceImpl;
import org.sakaiproject.kernel.component.core.Maven2DependencyResolver;
import org.sakaiproject.kernel.component.core.PackageRegistryServiceImpl;
import org.sakaiproject.kernel.component.core.SharedClassLoader;

/**
 * 
 */
public class ClassLoaderServiceTest {
  private static final String COMPONENT1 = "res://org/sakaiproject/kernel/component/core/test/component1.xml";


  /**
   * Test method for {@link org.sakaiproject.kernel.component.core.ClassLoaderServiceImpl#getComponentClassLoader(org.sakaiproject.kernel.api.ComponentSpecification)}.
   * @throws ComponentSpecificationException 
   */
  @Test
  public void testGetComponentClassLoader() throws ComponentSpecificationException {
    PackageRegistryServiceImpl prs = new PackageRegistryServiceImpl(); 
    Maven2DependencyResolver dependencyResolver = new Maven2DependencyResolver();
    KernelImpl kernel = new KernelImpl();
    
    // add an export that wont be used
    ClassExporter exportClassloader = new MockClassExport(this.getClass().getClassLoader());
    prs.addExport("org.sakaiproject.kernel.component.test", exportClassloader);
    
    
    SharedClassLoader sharedClassLoader = new SharedClassLoader(prs,dependencyResolver,kernel);

    ClassLoaderServiceImpl cls = new ClassLoaderServiceImpl(sharedClassLoader,prs,dependencyResolver);
    
    URLComponentSpecificationImpl componentSpec = new URLComponentSpecificationImpl(null,COMPONENT1);
    
    ClassLoader cl = cls.getComponentClassLoader(componentSpec);
    
  }

}
