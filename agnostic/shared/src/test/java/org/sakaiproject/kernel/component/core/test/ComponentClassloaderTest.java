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


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sakaiproject.kernel.api.DependencyResolverService;
import org.sakaiproject.kernel.api.PackageRegistryService;
import org.sakaiproject.kernel.component.URLComponentSpecificationImpl;
import org.sakaiproject.kernel.component.core.ComponentClassLoader;
import org.sakaiproject.kernel.component.core.Maven2DependencyResolver;
import org.sakaiproject.kernel.component.core.PackageRegistryServiceImpl;

/**
 * 
 */
public class ComponentClassloaderTest {


  @Test
  public void testExportedPackages() {
    PackageRegistryService packageRegistryService = new PackageRegistryServiceImpl();
    DependencyResolverService dependencyResolverService = new Maven2DependencyResolver();
    URLComponentSpecificationImpl uc = new URLComponentSpecificationImpl(null,TEST_RESOURCE);
   
    ComponentClassLoader ccl = new ComponentClassLoader(packageRegistryService,)
  }
}
