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

package org.sakaiproject.kernel.loader.server.tomcat5;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.startup.ContextConfig;
import org.sakaiproject.kernel.loader.common.stats.MemoryStats;
import org.sakaiproject.kernel.loader.common.stats.NewMemoryStats;
import org.sakaiproject.kernel.loader.common.stats.OldMemoryStats;

/**
 * This class needs to be attached to the Host container inside tomcat, so that it can control the
 * lifecycle of the webapps
 * 
 * Needs to be deployed to server
 * 
 * <pre>
 *  &lt;Host name=&quot;localhost&quot; appBase=&quot;webapps&quot;
 *      unpackWARs=&quot;true&quot; autoDeploy=&quot;true&quot;
 *      xmlValidation=&quot;false&quot; xmlNamespaceAware=&quot;false&quot;
 *      configClass=&quot;org.sakaiproject.kernel.loader.server.tomcat5.SakaiContextConfig&quot;
 *      &gt;
 * </pre>
 * 
 * @author ieb
 */
public class SakaiContextConfig extends ContextConfig {

  private static MemoryStats oldMemoryStats = new OldMemoryStats();
  private static MemoryStats newMemoryStats = new NewMemoryStats();

  static {
    oldMemoryStats.baseLine();
    newMemoryStats.baseLine();
  }

  /**
   * 
   */
  public SakaiContextConfig() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.catalina.LifecycleListener#lifecycleEvent(org.apache.catalina.LifecycleEvent)
   */
  public void lifecycleEvent(LifecycleEvent event) {
    String type = event.getType();
    super.lifecycleEvent(event);

    if (Lifecycle.AFTER_START_EVENT.equals(type)) {
      log.info(event.getSource() + oldMemoryStats.measure() + newMemoryStats.measure());
    }
  }

}
