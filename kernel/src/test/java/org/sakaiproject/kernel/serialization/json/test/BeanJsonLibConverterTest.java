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
package org.sakaiproject.kernel.serialization.json.test;


import static org.junit.Assert.*;
import com.google.inject.Guice;
import com.google.inject.Injector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sakaiproject.kernel.model.test.ModelModule;
import org.sakaiproject.kernel.serialization.json.BeanJsonLibConverter;

import java.util.List;
import java.util.Map;

/**
 * 
 */
public class BeanJsonLibConverterTest {

  private BeanJsonLibConverter converter;
  private Injector injector;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    injector = Guice.createInjector(new ModelModule());
    converter = injector
        .getInstance(BeanJsonLibConverter.class);
    converter.setDebugMode(true);

  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void beanConvertEmpty() {
    Object object = converter.convertToObject("", Object.class);
    assertNotNull(object);
  }
  @Test
  public void beanConvertArray() {
    String[] stringArray = converter.convertToObject("[ \"test\", \"test2\" ]", String[].class);
    assertNotNull(stringArray);
    assertEquals(2, stringArray.length);
    assertEquals("test", stringArray[0]);
    assertEquals("test2", stringArray[1]);
  }
  @Test
  public void beanConvertList() {
    List<String> stringList = converter.convertToObject("[ \"test\", \"test2\" ]", String.class);
    assertNotNull(stringList);
    assertEquals(2, stringList.size());
    assertEquals("test", stringList.get(0));
    assertEquals("test2", stringList.get(1));
  }

  @Test
  public void beanConvertMap() {
    Map<?, ?> map = converter.convertToObject("{ \"test1\" : \"value1\", \"test2\" : \"value2\" }", Map.class);
    assertNotNull(map);
    assertEquals(2, map.size());
    assertTrue(map.containsKey("test1"));
    assertTrue(map.containsKey("test2"));
    assertEquals("value1", map.get("test1"));
    assertEquals("value2", map.get("test2"));
  }

  @Test
  public void testContentType() {
    assertEquals("application/json",converter.getContentType());
  }

  
}
