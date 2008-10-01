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
package org.sakaiproject.kernel.loader.common;

/**
 * 
 */
public interface CommonObject {
  /**
   * The name of the Mbean used for kernel.
   */
  String MBEAN_COMMON = "Sakai:type=CommonObject";

  /**
   * Gives access to the OSGi Classloader, the implementation of this object should be in a bundle
   * and this method should give the calling class the classloader of the bundle that loaded the
   * CommonObjectImpl.
   * 
   * Calling objects should register to be informed of if the CommonObjectManager reloads
   * 
   * @return
   */
  ClassLoader getOSGiClassLoader();

}
