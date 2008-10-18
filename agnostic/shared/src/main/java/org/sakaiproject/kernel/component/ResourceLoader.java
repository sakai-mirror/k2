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
package org.sakaiproject.kernel.component;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 */
public class ResourceLoader {

  /**
   * @param defaultProperties
   * @return
   * @throws IOException
   */
  public static InputStream openResource(String resource) throws IOException {
    if (resource.startsWith("res://")) {
      ClassLoader cl = ResourceLoader.class.getClassLoader();
      return cl.getResourceAsStream(resource.substring(6));
    } else if (resource.startsWith("inline://")) {
      return new ByteArrayInputStream(resource.substring(9).getBytes("UTF-8"));
    } else {
      return new FileInputStream(resource);
    }
  }

  /**
   * @param d
   * @return
   * @throws IOException
   */
  public static String readResource(String resource) throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(
        openResource(resource)));
    StringBuilder sb = new StringBuilder();
    try {
      for (String line = in.readLine(); line != null; line = in.readLine()) {
        sb.append(line);
      }
    } finally {
      in.close();
    }
    return sb.toString();
  }

}
