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
package org.sakaiproject.kernel.osgi.common.test;

import org.junit.Assert;
import org.junit.Test;
import org.sakaiproject.kernel.loader.common.CommonObject;
import org.sakaiproject.kernel.loader.common.CommonObjectConfigurationException;
import org.sakaiproject.kernel.loader.common.CommonObjectManager;
import org.sakaiproject.kernel.osgi.common.CommonObjectLifecycle;


/**
 *
 */
public class CommonObjectLifecyleTest {

  @Test
  public void testCreate() throws CommonObjectConfigurationException {
    
    CommonObjectLifecycle col = new CommonObjectLifecycle();
    col.start();
    
    CommonObjectManager com = new CommonObjectManager();
    CommonObject co = com.getCommonObject();
    ClassLoader cl = co.getOSGiClassLoader();
    Assert.assertEquals(col.getClass().getClassLoader(), cl);
  }
}