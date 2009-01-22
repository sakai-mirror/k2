/*******************************************************************************
 * Copyright 2008 Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.sakaiproject.kernel.util;

/**
 *
 */
public class KernelProperties extends PropertiesLoader {
  /**
   * Location of the kernel properties.
   */
  public static final String DEFAULT_PROPERTIES = "res://kernel.properties";

  /**
   * the environment variable that contains overrides to kernel properties
   */
  public static final String LOCAL_PROPERTIES = "SAKAI_KERNEL_PROPERTIES";

  /**
   * The System property name that contains overrides to the kernel properties
   * resource
   */
  public static final String SYS_LOCAL_PROPERTIES = "sakai.kernel.properties";

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.util.PropertiesLoader#getDefaultProperties()
   */
  @Override
  protected String getDefaultProperties() {
    return DEFAULT_PROPERTIES;
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.util.PropertiesLoader#getLocalProperties()
   */
  @Override
  protected String getLocalProperties() {
    return LOCAL_PROPERTIES;
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.util.PropertiesLoader#getSysLocalProperties()
   */
  @Override
  protected String getSysLocalProperties() {
    return SYS_LOCAL_PROPERTIES;
  }

}
