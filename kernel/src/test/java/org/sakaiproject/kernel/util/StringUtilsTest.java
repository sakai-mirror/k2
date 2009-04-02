/*
 * Licensed to the Sakai Foundation (SF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The SF licenses this file
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
package org.sakaiproject.kernel.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for StringUtils class.
 */
public class StringUtilsTest {
  @Test
  public void addString() {
    String[] arr = { "one", "two" };
    arr = StringUtils.addString(arr, "three");
    assertEquals(3, arr.length);
    assertEquals("one", arr[0]);
    assertEquals("two", arr[1]);
    assertEquals("three", arr[2]);
  }

  @Test
  public void addSameString() {
    String[] arr = { "one", "two" };
    arr = StringUtils.addString(arr, "one");
    assertEquals(2, arr.length);
    assertEquals("one", arr[0]);
    assertEquals("two", arr[1]);
  }

  @Test
  public void removeStringFromMiddle() {
    String[] arr = { "one", "two", "three" };
    arr = StringUtils.removeString(arr, "two");
    assertEquals(2, arr.length);
    assertEquals("one", arr[0]);
    assertEquals("three", arr[1]);
  }

  @Test
  public void removeStringFromBeginning() {
    String[] arr = { "one", "two", "three" };
    arr = StringUtils.removeString(arr, "one");
    assertEquals(2, arr.length);
    assertEquals("two", arr[0]);
    assertEquals("three", arr[1]);
  }

  @Test
  public void removeStringFromEnd() {
    String[] arr = { "one", "two", "three" };
    arr = StringUtils.removeString(arr, "three");
    assertEquals(2, arr.length);
    assertEquals("one", arr[0]);
    assertEquals("two", arr[1]);
  }

  @Test
  public void removeMissingString() {
    String[] arr = { "one", "two", "three" };
    arr = StringUtils.removeString(arr, "four");
    assertEquals(3, arr.length);
    assertEquals("one", arr[0]);
    assertEquals("two", arr[1]);
    assertEquals("three", arr[2]);
  }

  @Test
  public void isEmpty() {
    assertTrue(StringUtils.isEmpty(""));
    assertTrue(StringUtils.isEmpty("  "));
    assertTrue(StringUtils.isEmpty(null));
    assertFalse(StringUtils.isEmpty("something"));
    assertFalse(StringUtils.isEmpty(" something "));
  }

  @Test
  public void stripBlanks() {
    assertEquals("thisisatest", StringUtils.stripBlanks("this is a test"));
    assertEquals("thisisatest", StringUtils.stripBlanks(" this isa test "));
  }

  @Test
  public void splitEmpty() {
    String[] split = StringUtils.split(null, '/');
    assertEquals(0, split.length);

    split = StringUtils.split("", '/');
    assertEquals(0, split.length);

    split = StringUtils.split("/", '/');
    assertEquals(0, split.length);
  }

  @Test
  public void split() {
    String[] split = StringUtils.split("/ss/t/f/u", '/');
    assertEquals(4, split.length);

    split = StringUtils.split("/ss/t/f/u/", '/');
    assertEquals(4, split.length);
  }

  @Test
  public void splitRepeatedSep() {
    String[] split = StringUtils.split("//", '/');
    assertEquals(1, split.length);

    split = StringUtils.split("/ss/t/f//u", '/');
    assertEquals(5, split.length);

    split = StringUtils.split("/ss/t//f//u", '/');
    assertEquals(6, split.length);

    split = StringUtils.split("/ss//t//f//u", '/');
    assertEquals(7, split.length);

    split = StringUtils.split("//ss//t//f//u", '/');
    assertEquals(8, split.length);

    split = StringUtils.split("/ss/t/f/u//", '/');
    assertEquals(5, split.length);
  }

  @Test
  public void splitMax() {
    String[] split = StringUtils.split("/ss/", '/', 3);
    assertEquals(1, split.length);

    split = StringUtils.split("/ss/t/f/u", '/', 3);
    assertEquals(3, split.length);

    split = StringUtils.split("/ss/t/f//u", '/', 3);
    assertEquals(3, split.length);
  }

  @Test
  public void join() {
    String str = " one two three";
    String join = StringUtils.join(new String[] { "one", "two", "three" }, 0,
        ' ');
    assertEquals(str, join);

    join = StringUtils.join(StringUtils.split(str, ' '), 0, ' ');
    assertEquals(str, join);

    join = StringUtils.join(StringUtils.split(str, ' '), 1, ' ');
    assertEquals(" two three", join);
  }
}
