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
package org.sakaiproject.kernel.util.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sakaiproject.kernel.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 *
 */
public class StringUtilsTest {
  @Test
  public void splitEmpty() {
    String[] split = StringUtils.split(null, '/');
    assertEquals(0, split.length);

    split = StringUtils.split("", '/');
    assertEquals(0, split.length);

    split = StringUtils.split("/", '/');
    assertEquals(0, split.length);
  }

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
    e = StringUtils.split("", '.');
    assertEquals(0, e.length);
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
    assertEquals(13, e.length);
    assertEquals("that", e[6]);
    e = StringUtils.split(".......that..is.....", '.');
    assertEquals(13, e.length);
    assertEquals("that", e[6]);
    assertEquals("", e[7]);
    assertEquals("is", e[8]);
  }

  public void testSplitLimit() {
    String[] e = StringUtils.split("a.test.of.something.that.should.be.ok",
        '.', 100);
    assertEquals(8, e.length);
    assertEquals("a", e[0]);
    assertEquals("test", e[1]);
    assertEquals("of", e[2]);
    assertEquals("something", e[3]);
    assertEquals("that", e[4]);
    assertEquals("should", e[5]);
    assertEquals("be", e[6]);
    assertEquals("ok", e[7]);
    e = StringUtils.split(".a.test.of.something.that.should.be.ok", '.', 4);
    assertEquals(4, e.length);
    assertEquals("a", e[0]);
    assertEquals("test", e[1]);
    assertEquals("of", e[2]);
    assertEquals("something", e[3]);
    e = StringUtils.split(".a.test.of.something.that.should.be.ok.", '.', 2);
    assertEquals(2, e.length);
    assertEquals("a", e[0]);
    assertEquals("test", e[1]);
    e = StringUtils.split("a.test.of.something.that.should.be.ok.", '.', 8);
    assertEquals(8, e.length);
    assertEquals("a", e[0]);
    assertEquals("test", e[1]);
    assertEquals("of", e[2]);
    assertEquals("something", e[3]);
    assertEquals("that", e[4]);
    assertEquals("should", e[5]);
    assertEquals("be", e[6]);
    assertEquals("ok", e[7]);
    e = StringUtils.split(".......that.......", '.', 5);
    assertEquals(1, e.length);
    assertEquals("that", e[0]);
    e = StringUtils.split(".......that..is.....", '.', 2);
    assertEquals(2, e.length);
    assertEquals("that", e[0]);
    assertEquals("", e[1]);
    e = StringUtils.split("..............", '.', 2);
    assertEquals(0, e.length);

    // StringUtils.byteToHex(base);
    // StringUtils.sha1Hash(tohash);
  }

  @Test
  public void testByteToHex() {
    for (int i = 0; i < 256; i++) {

      byte[] b = new byte[10];
      for (int j = 0; j < b.length; j++) {
        b[j] = (byte) (i - 128);
      }
      StringUtils.byteToHex(b);
    }
  }

  @Test
  public void testSha1Hash() throws UnsupportedEncodingException,
      NoSuchAlgorithmException {
    assertEquals("db2ae1644939bfbf8602a58bec78b39bfe660f58", StringUtils
        .sha1Hash("password"));
    assertEquals("db2ae1644939bfbf8602a58bec78b39bfe660f58", StringUtils
        .sha1Hash("password"));
    assertEquals("db2ae1644939bfbf8602a58bec78b39bfe660f58", StringUtils
        .sha1Hash("password"));
    assertEquals("db2ae1644939bfbf8602a58bec78b39bfe660f58", StringUtils
        .sha1Hash("password"));
    assertEquals("db2ae1644939bfbf8602a58bec78b39bfe660f58", StringUtils
        .sha1Hash("password"));
    assertEquals("db2ae1644939bfbf8602a58bec78b39bfe660f58", StringUtils
        .sha1Hash("password"));
    assertTrue(!"db2ae1644939bfbf8602a58bec78b39bfe660f58".equals(StringUtils
        .sha1Hash("password22")));
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
}
