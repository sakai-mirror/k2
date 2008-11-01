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
package org.sakaiproject.kernel.component.core.test;

import org.sakaiproject.kernel.api.ClassExporter;

import java.io.InputStream;

/**
 * 
 */
public class MockClassExport implements ClassExporter {

  private ClassLoader classLoader;

  /**
   * @param classLoader
   */
  public MockClassExport(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.ClassExporter#getExportedResourceAsStream(java.lang.String)
   */
  public InputStream getExportedResourceAsStream(String name) {
    return classLoader.getResourceAsStream(name);
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.api.ClassExporter#loadExportedClass(java.lang.String)
   */
  public Class<?> loadExportedClass(String name) throws ClassNotFoundException {
    return classLoader.loadClass(name);
  }

}
