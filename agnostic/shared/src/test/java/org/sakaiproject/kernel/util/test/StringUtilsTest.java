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
package org.sakaiproject.kernel.util.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.sakaiproject.kernel.util.StringUtils;

/**
 * 
 */
public class StringUtilsTest {

  /**
   * Test method for
   * {@link org.sakaiproject.kernel.util.StringUtils#split(java.lang.String, char)}
   * .
   */
  @Test
  public void testSplit() {
    String[] e = StringUtils
        .split("a.test.of.something.that.should.be.ok", '.');
    assertEquals(8, e.length);
    assertEquals("a", e[0]);
    assertEquals("test", e[1]);
    assertEquals("of", e[2]);
    assertEquals("something", e[3]);
    assertEquals("that", e[4]);
    assertEquals("should", e[5]);
    assertEquals("be", e[6]);
    assertEquals("ok", e[7]);
    e = StringUtils.split(".a.test.of.something.that.should.be.ok", '.');
    assertEquals(8, e.length);
    assertEquals("a", e[0]);
    assertEquals("test", e[1]);
    assertEquals("of", e[2]);
    assertEquals("something", e[3]);
    assertEquals("that", e[4]);
    assertEquals("should", e[5]);
    assertEquals("be", e[6]);
    assertEquals("ok", e[7]);
    e = StringUtils.split(".a.test.of.something.that.should.be.ok.", '.');
    assertEquals(8, e.length);
    assertEquals("a", e[0]);
    assertEquals("test", e[1]);
    assertEquals("of", e[2]);
    assertEquals("something", e[3]);
    assertEquals("that", e[4]);
    assertEquals("should", e[5]);
    assertEquals("be", e[6]);
    assertEquals("ok", e[7]);
    e = StringUtils.split("a.test.of.something.that.should.be.ok.", '.');
    assertEquals(8, e.length);
    assertEquals("a", e[0]);
    assertEquals("test", e[1]);
    assertEquals("of", e[2]);
    assertEquals("something", e[3]);
    assertEquals("that", e[4]);
    assertEquals("should", e[5]);
    assertEquals("be", e[6]);
    assertEquals("ok", e[7]);
    e = StringUtils.split(".......that.......", '.');
    assertEquals(1, e.length);
    assertEquals("that", e[0]);
    e = StringUtils.split(".......that..is.....", '.');
    assertEquals(3, e.length);
    assertEquals("that", e[0]);
    assertEquals("", e[1]);
    assertEquals("is", e[2]);
  }

}
