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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sakaiproject.kernel.Activator;
import org.sakaiproject.kernel.api.ComponentActivatorException;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.KernelManager;
import org.sakaiproject.kernel.api.RequiresStop;
import org.sakaiproject.kernel.api.ServiceSpec;
import org.sakaiproject.kernel.api.ShutdownService;
import org.sakaiproject.kernel.api.memory.Cache;
import org.sakaiproject.kernel.api.memory.CacheManagerService;
import org.sakaiproject.kernel.api.memory.CacheScope;
import org.sakaiproject.kernel.component.KernelLifecycle;
import org.sakaiproject.kernel.util.FileUtil;

import java.io.File;

public class CacheTest {
  private static final Log LOG = LogFactory.getLog(ActivatorTest.class);
  private static KernelLifecycle kernelLifecycle;
  private static KernelManager kernelManager;
  private Cache<String> default_cache;
  private Cache<String> named_cache;
  private CacheManagerService cacheManagerService;
  
  @BeforeClass
  public static void startClass()throws ComponentActivatorException {
    KernelIntegrationBase.disableKernelStartup();
 // If there are problems with startup and shutdown, these will prevent the
    // problem
    File jcrBase = new File("target/jcr");
    File dbBase = new File("target/testdb");
    System.err.println("==========================================================================");
    System.err.println("Removing all previous JCR and DB traces from "+jcrBase.getAbsolutePath()+" "+dbBase.getAbsolutePath());
    
    FileUtil.deleteAll(jcrBase);
    FileUtil.deleteAll(dbBase);
    System.err.println("==========================================================================");

    kernelLifecycle = new KernelLifecycle();
    kernelLifecycle.start();

    kernelManager = new KernelManager();
    Kernel kernel = kernelManager.getKernel();
    Activator activator = new Activator();
    activator.activate(kernel);
    for (Class<?> c : Activator.SERVICE_CLASSES) {

      ShutdownService ss = kernel.getServiceManager().getService(
          new ServiceSpec(ShutdownService.class));
      Object s = kernel.getServiceManager().getService(new ServiceSpec(c));
      if (s instanceof RequiresStop) {
        ss.register((RequiresStop) s);
      }
    }
  }
  
  @AfterClass
  public static void afterClass() {
    try {
      kernelLifecycle.stop();
    } catch (Exception ex) {
      LOG.info("Failed to stop kernel ", ex);
    }
    KernelIntegrationBase.enableKernelStartup();
  }
  
  @Before
  public void before() {
    cacheManagerService = kernelManager.getService(CacheManagerService.class);
    default_cache = cacheManagerService.getCache(null, CacheScope.INSTANCE);
    named_cache = cacheManagerService.getCache("namedCache", CacheScope.INSTANCE);
    
  }
  
  
  @After
  public void after() {
    default_cache.clear();
    named_cache.clear();
  }

  @Test
  public void testClear() {
    assertFalse(default_cache.containsKey("test"));
    assertFalse(default_cache.containsKey("test2"));
    default_cache.put("test", "123");
    default_cache.put("test2", "321");
    assertTrue(default_cache.containsKey("test"));
    assertTrue(default_cache.containsKey("test2"));
    default_cache.clear();
    //Make sure all entries were removed
    assertFalse(default_cache.containsKey("test"));
    assertFalse(default_cache.containsKey("test2"));
  }

  @Test
  public void testThreadCacheClear() {
    Cache<String> thread_cache = cacheManagerService.getCache(null, CacheScope.THREAD);
    assertFalse(thread_cache.containsKey("test"));
    assertFalse(thread_cache.containsKey("test2"));
    thread_cache.put("test", "123");
    thread_cache.put("test2", "321");
    assertTrue(thread_cache.containsKey("test"));
    assertTrue(thread_cache.containsKey("test2"));
    thread_cache.clear();
    //Make sure all entries were removed
    assertFalse(thread_cache.containsKey("test"));
    assertFalse(thread_cache.containsKey("test2"));
  }
  
  @Test
  public void testRemove() {
    default_cache.put("test", "123");
    assertTrue(default_cache.containsKey("test"));
    default_cache.put("test2", "321");
    assertTrue(default_cache.containsKey("test2"));
    default_cache.remove("test");
    //Make sure we only removed the requested entry
    assertFalse(default_cache.containsKey("test"));
    assertTrue(default_cache.containsKey("test2"));
  }
  
  @Test
  public void testThreadCacheRemove(){
    Cache<String> thread_cache = cacheManagerService.getCache(null, CacheScope.THREAD);
    thread_cache.put("test", "123");
    assertTrue(thread_cache.containsKey("test"));
    thread_cache.put("test2", "1234");
    assertTrue(thread_cache.containsKey("test2"));
    thread_cache.remove("test");
    //Make sure we only removed the requested entry
    assertFalse(thread_cache.containsKey("test"));
    assertTrue(thread_cache.containsKey("test2"));
  }
  
  @Test
  public void testOverwrite() {
    default_cache.clear();
    default_cache.put("test", "123");
    default_cache.put("test", "321");
    assertTrue(default_cache.get("test").equals("321"));
  }
  
  @Test
  public void testCacheIsolation() {
    default_cache.clear();
    named_cache.clear();
    default_cache.put("test", "123");
    named_cache.put("test2", "1234");
    // Insure that we can't read values stored in a different cache
    assertFalse(default_cache.containsKey("test2"));
    assertTrue(default_cache.containsKey("test"));
    assertFalse(named_cache.containsKey("test"));
    assertTrue(named_cache.containsKey("test2"));
  }
}
