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
package org.sakaiproject.kernel.loader.server.jetty.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * This test validates a jar file, sometimes if javax.activation is in a bundle it wont validate.
 */
public class ManifestTest {

  private static final Log LOG = LogFactory.getLog(ManifestTest.class);
  private static final String JARFILE = "../shared-bundle/target/shared-bundle-0.1-SNAPSHOT.jar";

  @Test
  public void manifestTest() throws IOException {
    File f = new File(JARFILE);
    if (f.exists()) {
      LOG.info("Opening File " + f.getCanonicalPath());
      JarFile jf = new JarFile(f);
      JarEntry ze = jf.getJarEntry("META-INF/MANIFEST.MF");
      LOG.info("Zip Entry " + ze.getName());
      InputStream is = jf.getInputStream(ze);
      Manifest m = new Manifest(is);
      is.close();
      LOG.info("Manifest is " + m + " " + m.getMainAttributes().size());
      Attributes a = m.getMainAttributes();
      for (Entry<Object, Object> e : a.entrySet()) {
        String value = String.valueOf(e.getValue());
        if ( value.length() > 75 ) {
          value = value.substring(0,75)+"...";
        }
        LOG.info("Attribute  " + e.getKey() + ":" + value);
      }
    } else {
      LOG.info("Test Disabled, target jar file does not exist");
    }
  }
}
