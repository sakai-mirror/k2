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
package org.sakaiproject.kernel.component.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sakaiproject.kernel.component.FileUtil;
import org.sakaiproject.kernel.component.ResourceLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 
 */
public class ResourceLoaderTest {

  private File baseFile;

  @Before
  public void before() throws IOException {
    baseFile = new File("target/resourceloadertest");
    touchFile(new File(baseFile, "testfile1.txt"));
  }

  @After
  public void after() {
    FileUtil.deleteAll(baseFile);
  }

  /**
   * @param f
   * @throws IOException
   */
  private void touchFile(File f) throws IOException {
    f.getParentFile().mkdirs();
    FileWriter fw = new FileWriter(f);
    fw.write(String.valueOf(System.currentTimeMillis()));
    fw.close();
  }

  /**
   * Test method for
   * {@link org.sakaiproject.kernel.component.ResourceLoader#openResource(java.lang.String)}
   * .
   * 
   * @throws IOException
   */
  @Test
  public void testOpenResourceString() throws IOException {

    InputStream in = ResourceLoader
        .openResource("res://org/sakaiproject/kernel/component/test/complexcomponent.xml");
    assertNotNull(in);
    in.close();
    try {
      in = ResourceLoader.openResource("res://sdfkjsdlkfjsdlfkjsd lkfjsdkl ");
      fail();
    } catch (IOException e) {
    }
    in.close();
    in = ResourceLoader.openResource("inline://sdfkjsdlkfjsdlfkjsd lkfjsdkl ");
    assertNotNull(in);
    in.close();

    in = ResourceLoader.openResource("http://www.sakaiproject.org");
    assertNotNull(in);
    in.close();

    File f = new File(baseFile, "testfile1.txt");
    String path = f.getAbsolutePath();
    in = ResourceLoader.openResource(path);
    assertNotNull(in);
    in.close();

    in = ResourceLoader.openResource("file:/" + path);
    assertNotNull(in);
    in.close();

  }

  /**
   * Test method for
   * {@link org.sakaiproject.kernel.component.ResourceLoader#openResource(java.lang.String, java.lang.ClassLoader)}
   * .
   * 
   * @throws IOException
   */
  @Test
  public void testOpenResourceStringClassLoader() throws IOException {
    InputStream in = ResourceLoader.openResource(
        "res://org/sakaiproject/kernel/component/test/complexcomponent.xml",
        this.getClass().getClassLoader());
    assertNotNull(in);
    in.close();
  }

  /**
   * Test method for
   * {@link org.sakaiproject.kernel.component.ResourceLoader#readResource(java.lang.String)}
   * .
   * @throws IOException 
   */
  @Test
  public void testReadResourceString() throws IOException {
    String in = ResourceLoader.readResource(
        "res://org/sakaiproject/kernel/component/test/complexcomponent.xml");
    assertNotNull(in);
    assertTrue(in.length()> 0);
  }

  /**
   * Test method for
   * {@link org.sakaiproject.kernel.component.ResourceLoader#readResource(java.lang.String, java.lang.ClassLoader)}
   * .
   * @throws IOException 
   */
  @Test
  public void testReadResourceStringClassLoader() throws IOException {
    String in = ResourceLoader.readResource(
        "res://org/sakaiproject/kernel/component/test/complexcomponent.xml",
        this.getClass().getClassLoader());
    assertNotNull(in);
    assertTrue(in.length()> 0);
  }

  /**
   * Test method for
   * {@link org.sakaiproject.kernel.component.ResourceLoader#readResource(java.net.URL)}
   * .
   * @throws IOException 
   * @throws MalformedURLException 
   */
  @Test
  public void testReadResourceURL() throws MalformedURLException, IOException {
    String in = ResourceLoader.readResource(
        new URL("http://www.google.com"));
    assertNotNull(in);
    assertTrue(in.length()> 0);
  }

}
