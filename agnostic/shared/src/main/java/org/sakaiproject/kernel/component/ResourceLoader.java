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
 * Abstration of resource loading, that understands file refernece as well as
 * classpath resources.
 */
public class ResourceLoader {

  /**
   * Get an input stream for a resource.
   * 
   * @param resource
   *          a URI pointing to the resource, URI's starting res:// mean
   *          resources from the classpath and inline:// means the rest of the
   *          uri is the resource. Anything else is resolved as a file.
   * @return the input stream for the resource. It is the callers responsibility
   *         to close this stream.
   * @throws IOException
   *           if the resource could not be opened.
   */
  public static InputStream openResource(String resource) throws IOException {
    return openResource(resource,ResourceLoader.class.getClassLoader());
  }
  
  public static InputStream openResource(String resource, ClassLoader classLoader) throws IOException {
    if (resource.startsWith("res://")) {
      InputStream in = classLoader.getResourceAsStream(resource.substring(6));
      if ( in == null ) {
        throw new IOException("Unable to find resource "+resource+" using "+classLoader);
      }
      return in;
    } else if (resource.startsWith("inline://")) {
      return new ByteArrayInputStream(resource.substring(9).getBytes("UTF-8"));
    } else {
      return new FileInputStream(resource);
    }
  }
  

  /**
   * Read a resource into a string.
   * 
   * @param d
   *          the URI for the resource, @see openResource(String resource)
   * @return the contents of the resource.
   * @throws IOException
   *           if there was a problem opening the resource.
   */
  public static String readResource(String resource) throws IOException {
    return readResource(resource,ResourceLoader.class.getClassLoader());
  }

  /**
   * @param repositoryConfigTemplate
   * @param classLoader
   * @return
   * @throws IOException 
   */
  public static String readResource(String resource,
      ClassLoader classLoader) throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(
        openResource(resource,classLoader)));
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
