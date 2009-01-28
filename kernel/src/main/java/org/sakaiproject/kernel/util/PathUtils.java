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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Generate a path prefix based on the user id
 * 
 */
public class PathUtils {

  private static final Log log = LogFactory.getLog(PathUtils.class);

  /**
   * Generate a path using a SHA-1 hash split into path parts to generate a
   * unique path to the user information, that will not result in too many
   * objects in each folder.
   * 
   * @param user
   * @return
   */
  public static String getUserPrefix(String user) {
    MessageDigest md;
    if (user != null) {
      try {
        md = MessageDigest.getInstance("SHA-1");
        byte[] userHash = md.digest(user.getBytes("UTF-8"));

        if (user.length() == 0) {
          user = "anon";
        }

        char[] chars = new char[8 + user.length()];
        byte current = userHash[0];
        int hi = (current & 0xF0) >> 4;
        int lo = current & 0x0F;
        chars[0] = '/';
        chars[1] = (char) (hi < 10 ? ('0' + hi) : ('A' + hi - 10));
        chars[2] = (char) (lo < 10 ? ('0' + lo) : ('A' + lo - 10));
        current = userHash[1];
        hi = (current & 0xF0) >> 4;
        lo = current & 0x0F;
        chars[3] = '/';
        chars[4] = (char) (hi < 10 ? ('0' + hi) : ('A' + hi - 10));
        chars[5] = (char) (lo < 10 ? ('0' + lo) : ('A' + lo - 10));
        chars[6] = '/';
        for (int i = 0; i < user.length(); i++) {
          char c = user.charAt(i);
          if (!Character.isLetterOrDigit(c)) {
            c = '_';
          }
          chars[i + 7] = c;
        }
        chars[7 + user.length()] = '/';
        return new String(chars);
      } catch (NoSuchAlgorithmException e) {
        log.error(e);
      } catch (UnsupportedEncodingException e) {
        log.error(e);
      }
      if (user.length() == 0) {
        user = "anon";
      }

      char[] chars = new char[2 + user.length()];
      chars[0] = '/';
      for (int i = 0; i < user.length(); i++) {
        char c = user.charAt(i);
        if (!Character.isLetterOrDigit(c)) {
          c = '_';
        }
        chars[i + 1] = c;
      }
      chars[1 + user.length()] = '/';
      return new String(chars);
    }
    return null;
  }

  /**
   * @param resourceReference
   * @return
   */
  public static String getParentReference(String resourceReference) {
    char[] ref = resourceReference.toCharArray();
    int i = ref.length-1;
    while ( i >= 0 && ref[i] == '/'  ) i--;
    while ( i >= 0 && ref[i] != '/' ) i--;
    while ( i >= 0 && ref[i] == '/'  ) i--;
    if ( i == -1 ) {
      return "/";
    }
    return new String(ref,0,i+1);
  }
}
