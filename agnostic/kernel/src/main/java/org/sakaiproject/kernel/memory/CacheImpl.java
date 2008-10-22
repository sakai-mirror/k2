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

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.sakaiproject.kernel.api.memory.Cache;

/**
 * 
 */
public class CacheImpl<V> implements Cache<V> {

  private String cacheName;
  private net.sf.ehcache.Cache cache;

  /**
   * @param cacheManager
   * @param name
   */
  public CacheImpl(CacheManager cacheManager, String name) {
    if (cacheName == null) {
      cacheName = "default";
    }
    synchronized (cacheManager) {
      cache = cacheManager.getCache(cacheName);
      if (cache == null) {
        cacheManager.addCache(cacheName);
        cache = cacheManager.getCache(cacheName);
        if (cache == null) {
          throw new RuntimeException("Failed to create Cache with name "
              + cacheName);
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.memory.Cache#clear()
   */
  public void clear() {
    cache.removeAll();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.memory.Cache#containsKey(java.lang.String)
   */
  public boolean containsKey(String key) {
    return cache.isKeyInCache(key);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.memory.Cache#get(java.lang.String)
   */
  @SuppressWarnings("unchecked")
  public V get(String key) {
    Element e = cache.get(key);
    return (V) e.getObjectValue();
  }

  /**
   * {@inherit-doc}
   * 
   * @see org.sakaiproject.kernel.api.memory.Cache#put(java.lang.String,
   *      java.lang.Object)
   */
  public void put(String key, V payload) {
    cache.put(new Element(key, payload));
  }

  /**
   * {@inherit-doc}
   * 
   * @see org.sakaiproject.kernel.api.memory.Cache#remove(java.lang.String)
   */
  public void remove(String key) {
    cache.remove(key);
  }

}
