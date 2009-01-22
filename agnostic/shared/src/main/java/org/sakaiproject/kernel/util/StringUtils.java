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
package org.sakaiproject.kernel.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 */
public class StringUtils {

  private static final char[] TOHEX = "0123456789abcdef".toCharArray();

  /**
   * @param packageName
   * @param c
   * @return
   */
  public static String[] split(String st, char sep) {

    if (st == null) {
      return new String[0];
    }
    char[] pn = st.toCharArray();
    if (pn.length == 0) {
      return new String[0];
    }
    int n = 1;
    int start = 0;
    int end = pn.length;
    while (start < end && sep == pn[start])
      start++;
    while (start < end && sep == pn[end - 1])
      end--;
    for (int i = start; i < end; i++) {
      if (sep == pn[i]) {
        n++;
      }
    }
    String[] e = new String[n];
    int s = start;
    int j = 0;
    for (int i = start; i < end; i++) {
      if (pn[i] == sep) {
        e[j++] = new String(pn, s, i - s);
        s = i + 1;
      }
    }
    if (s < end) {
      e[j++] = new String(pn, s, end - s);
    }
    return e;
  }

  /**
   * @param resourceReference
   * @param c
   * @param i
   * @return
   */
  public static String[] split(String st, char sep, int maxElements) {
    char[] pn = st.toCharArray();
    int n = 1;
    int start = 0;
    int end = pn.length;
    while (start < end && sep == pn[start])
      start++;
    while (start < end && sep == pn[end - 1])
      end--;
    for (int i = start; i < end; i++) {
      if (sep == pn[i]) {
        n++;
      }
    }
    String[] e = new String[Math.min(maxElements, n)];
    int s = start;
    int j = 0;
    for (int i = start; i < end && j < e.length; i++) {
      if (pn[i] == sep) {
        e[j++] = new String(pn, s, i - s);
        s = i + 1;
      }
    }
    if (s < end && j < e.length) {
      e[j++] = new String(pn, s, end - s);
    }
    return e;
  }

  public static String sha1Hash(String tohash) throws UnsupportedEncodingException, NoSuchAlgorithmException {
    byte[] b = tohash.getBytes("UTF-8");
    MessageDigest sha1 = MessageDigest.getInstance("SHA");
    b = sha1.digest(b);
    return byteToHex(b);
  }

  public static String byteToHex(byte[] base) {
    char[] c = new char[base.length*2];
    int i = 0;
    
    for (byte b : base) {
      int j = b;
      j = j+128;
      c[i++] = TOHEX[j/0x10];      
      c[i++] = TOHEX[j%0x10];
    }
    return new String(c);
  }

}
