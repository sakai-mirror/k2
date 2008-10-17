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
package org.sakaiproject.kernel.component.core;

import org.sakaiproject.kernel.loader.common.CommonObject;

import java.lang.management.ManagementFactory;

import javax.management.Descriptor;
import javax.management.JMException;
import javax.management.JMRuntimeException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.modelmbean.DescriptorSupport;
import javax.management.modelmbean.InvalidTargetObjectTypeException;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanInfo;
import javax.management.modelmbean.ModelMBeanInfoSupport;
import javax.management.modelmbean.ModelMBeanOperationInfo;
import javax.management.modelmbean.RequiredModelMBean;

/**
 *
 */
public class SharedClassLoaderContainer  {

  public SharedClassLoaderContainer() throws JMRuntimeException, JMException, InvalidTargetObjectTypeException {
      MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
      RequiredModelMBean model = new RequiredModelMBean(createMBeanInfo());
      model.setManagedResource(this, "objectReference");
      ObjectName common = new ObjectName(CommonObject.MBEAN_COMMON+".sharedclassloader");
      mbs.registerMBean(model, common);
  }
  
  public void stop() throws JMException {
    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
    ObjectName common = new ObjectName(CommonObject.MBEAN_COMMON+".sharedclassloader");
    mbs.unregisterMBean(common);
  }


  /**
   * Create the the MBean Info for the Shared ClassLoader so that the methods
   * and properties are accessible via JMX.
   * 
   * @return a new MBeanInfo structure
   */
  private ModelMBeanInfo createMBeanInfo() {
    Descriptor sharedClassLoader = new DescriptorSupport(new String[] {
        "name=SharedClassLoader", "descriptorType=attribute", "default=null",
        "displayName=Shared Class Loader", "getMethod=getManagedObject" });

    ModelMBeanAttributeInfo[] mmbai = new ModelMBeanAttributeInfo[1];
    mmbai[0] = new ModelMBeanAttributeInfo("SharedClassLoader",
        ClassLoader.class.getName(), "Shared Class Loader", true, false, false,
        sharedClassLoader);

    ModelMBeanOperationInfo[] mmboi = new ModelMBeanOperationInfo[1];

    mmboi[0] = new ModelMBeanOperationInfo("getManagedObject",
        "Get the Shared Class Loader", null, ClassLoader.class.getName(),
        ModelMBeanOperationInfo.INFO);

    return new ModelMBeanInfoSupport(this.getClass().getName(),
        "Sakai Shared Classloader", mmbai, null, mmboi, null);
  }

  /* (non-Javadoc)
   * @see org.sakaiproject.kernel.loader.common.CommonObject#getManagedObject()
   */
  @SuppressWarnings("unchecked")
  public <T> T getManagedObject() {
    return (T) this.getClass().getClassLoader();
  }

}
