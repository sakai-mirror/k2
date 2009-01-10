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
package org.sakaiproject.kernel.registry;

import org.sakaiproject.kernel.api.Provider;
import org.sakaiproject.kernel.api.Registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 */
public class RegistryImpl<V, T extends Provider<V>> implements Registry<V, T> {

  List<T> providers = new ArrayList<T>();

  private Comparator<? super T> comparitor = new Comparator<T>() {
    public int compare(T o1, T o2) {
      return o1.getPriority() - o2.getPriority();
    }

  };


  private Map<V, T> mappedProviders = new ConcurrentHashMap<V, T>();

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.Registry#add(java.lang.Object)
   */
  public synchronized void add(T provider) {
    System.err.println("Adding "+provider+" to "+this);
    List<T> newList = new ArrayList<T>();
    newList.addAll(providers);
    newList.add(provider);
    Collections.sort(newList, comparitor);
    providers = newList;
    mappedProviders.put(provider.getKey(), provider);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.Registry#remove(java.lang.Object)
   */
  public synchronized void remove(T provider) {
    List<T> newList = new ArrayList<T>();
    newList.addAll(providers);
    newList.remove(provider);
    Collections.sort(newList, comparitor);
    providers = newList;
    mappedProviders.remove(provider);
  }

  /**
   * @return the providers
   */
  public List<T> getList() {
    System.err.println("Getting List from "+this);
    return providers;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.Registry#getMap()
   */
  public Map<V, T> getMap() {    
    System.err.println("Getting Map from "+this);
    return mappedProviders ;
  }

}
