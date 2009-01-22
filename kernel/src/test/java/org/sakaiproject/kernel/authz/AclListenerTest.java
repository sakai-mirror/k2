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
package org.sakaiproject.kernel.authz;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.sakaiproject.kernel.api.ComponentActivatorException;
import org.sakaiproject.kernel.test.KernelIntegrationBase;

/**
 *
 */
public class AclListenerTest extends KernelIntegrationBase {
  @BeforeClass
  public static void beforeClass() throws ComponentActivatorException {
    KernelIntegrationBase.beforeClass();
  }

  @AfterClass
  public static void afterClass() {
    KernelIntegrationBase.afterClass();
  }
}
