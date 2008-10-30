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

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.sakaiproject.kernel.component.core.ComponentClassLoader;
import org.sakaiproject.kernel.component.core.PackageRegistryServiceImpl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 
 */
public class ComponentClassloaderTest {


  private static final Log LOG = LogFactory.getLog(ComponentClassloaderTest.class);


  @Test
  public void testExportedPackages() throws MalformedURLException, IOException {
    PackageRegistryServiceImpl prs = new PackageRegistryServiceImpl(); 
    ClassLoader exportClassloader = this.getClass().getClassLoader();
    prs.addExport("org.sakaiproject.kernel.component.test", exportClassloader);
    URL[] urls = new URL[1];
    File f = new File("../server/target/classes/");
    // classpaths to directories must end in / ... bad! / is a separator
    urls[0] = new URL("file://"+f.getCanonicalPath()+"/");
    
    ComponentClassLoader cc = new ComponentClassLoader(prs,urls,exportClassloader);
    LOG.info("Classloader Structure is "+cc.toString());
    // test for a non found, look at code coverage to check that the export was checked. 
    try {
      cc.loadClass("org.sakaiproject.kernel.component.test.NonExistantClass");
      fail();
    } catch (ClassNotFoundException e) {
    }
    
    // load something from the exported classloader
    try {
      Class<?> c = cc.loadClass("org.sakaiproject.kernel.component.test.KernelLifecycleTest");
      assertSame(exportClassloader, c.getClassLoader());
    } catch (ClassNotFoundException e) {
      fail();
    }
    // load something thats only in the ComponentClassloader
    try {
      Class<?> c = cc.loadClass("org.sakaiproject.kernel.loader.server.SwitchedClassLoader");
      assertSame(cc, c.getClassLoader());
    } catch (ClassNotFoundException e) {
      LOG.error("Failed ",e);
      fail();
    }
    
  }
}