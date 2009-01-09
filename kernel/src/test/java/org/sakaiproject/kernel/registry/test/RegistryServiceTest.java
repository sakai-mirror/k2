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
package org.sakaiproject.kernel.registry.test;


import static org.junit.Assert.*;
import org.junit.Test;
import org.sakaiproject.kernel.api.Registry;
import org.sakaiproject.kernel.registry.RegistryServiceImpl;

import java.util.List;

/**
 * 
 */
public class RegistryServiceTest {

  @Test
  public void testRegistryServiceAddForward() {
    RegistryServiceImpl registryServiceImpl = new RegistryServiceImpl();
    Registry<String,TProvider<String>> r = registryServiceImpl.getRegistry("testRegistry");
    for ( int i = 0; i < 100; i++ ) {
      r.add(new TProvider<String>(i,String.valueOf(i)));
    }
    List<TProvider<String>> p = r.getList();
    for ( int i = 0; i < 100; i++ ) {
      assertEquals(i, p.get(i).getPriority());
    }
  }
  @Test
  public void testRegistryServiceAddReverse() {
    RegistryServiceImpl registryServiceImpl = new RegistryServiceImpl();
    Registry<String,TProvider<String>> r = registryServiceImpl.getRegistry("testRegistry");
    for ( int i = 99; i >= 0; i-- ) {
      r.add(new TProvider<String>(i,String.valueOf(i)));
    }
    List<TProvider<String>> p = r.getList();
    for ( int i = 0; i < 100; i++ ) {
      assertEquals(i, p.get(i).getPriority());
    }
  }
  
  @Test
  public void testRegistryServiceRemove() {
    RegistryServiceImpl registryServiceImpl = new RegistryServiceImpl();
    Registry<String,TProvider<String>> r = registryServiceImpl.getRegistry("testRegistry");
    TProvider<String> tp = new TProvider<String>(-2,String.valueOf(-2));
    r.add(tp);
    for ( int i = 99; i >= 0; i-- ) {
      r.add(new TProvider<String>(i,String.valueOf(i)));
    }
    r.remove(tp);
    List<TProvider<String>> p = r.getList();
    assertEquals(100, p.size());
    for ( int i = 0; i < 100; i++ ) {
      assertEquals(i, p.get(i).getPriority());
    }
  }

}
