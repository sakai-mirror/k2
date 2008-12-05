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

/**
 * 
 */
public class StringUtils {

  /**
   * @param packageName
   * @param c
   * @return
   */
  public static String[] split(String st, char sep) {
    char[] pn = st.toCharArray();
    int n = 1;
    int start = 0;
    int end = pn.length;
    while ( sep == pn[start] && start < end ) start++;
    while( sep == pn[end-1] && start < end ) end--;
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
        s = i+1;
      }
    }
    if ( s < end ) {
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
    while ( sep == pn[start] && start < end ) start++;
    while( sep == pn[end-1] && start < end ) end--;
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
        s = i+1;
      }
    }
    if ( s < end && j < e.length) {
      e[j++] = new String(pn, s, end - s);
    }
    return e;
  }

}
