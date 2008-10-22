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
package org.sakaiproject.kernel.memory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.management.ManagementService;

import org.sakaiproject.kernel.api.RequiresStop;
import org.sakaiproject.kernel.api.memory.Cache;
import org.sakaiproject.kernel.api.memory.CacheManagerService;

import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServer;

/**
 * 
 */
public class CacheManagerServiceImpl implements CacheManagerService, RequiresStop {


  private CacheManager cacheManager;
  private Map<String, Cache<?>> caches = new HashMap<String, Cache<?>>();

  @Inject
  public CacheManagerServiceImpl(@Named("cache.config")
  String configPath, @Named("cache.jmx.stats")
  String withCacheStatistics) {
    create(configPath, withCacheStatistics);
  }

  public void create(String configPath, String withCacheStatistics) {
    URL url = getClass().getResource(configPath);
    cacheManager = new CacheManager(url);

    /*
     * Add in a shutdown hook, for safety
     */
    Runtime.getRuntime().addShutdownHook(new Thread() {
      /*
       * (non-Javadoc)
       *
       * @see java.lang.Thread#run()
       */
      @Override
      public void run() {
        try {
          CacheManagerServiceImpl.this.stop();
        } catch (Throwable t) {

          // I really do want to swallow this, and make the shutdown clean for
          // others
        }
      }
    });

    // register the cache manager with JMX
    MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
    ManagementService.registerMBeans(cacheManager, mBeanServer, true, true, true, Boolean.valueOf(
        withCacheStatistics).booleanValue());

  }

  /**
   * perform a shutdown
   */
  public void stop() {
    cacheManager.shutdown();
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.memory.CacheManagerService#getCache(java.lang.String)
   */
  @SuppressWarnings("unchecked")
  public <V> Cache<V> getCache(String name) {
    if (name == null) {
      return new CacheImpl<V>(cacheManager, name);
    } else {
      Cache<V> c = (Cache<V>) caches.get(name);
      if (c == null) {
        c = new CacheImpl<V>(cacheManager, name);
        caches.put(name, c);
      }
      return c;
    }
  }

}
