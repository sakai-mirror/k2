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

package org.sakaiproject.kernel.loader.common.stats;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;

import java.lang.management.ManagementFactory;

/**
 * @author ieb
 * 
 */
public abstract class AbstractStats implements MemoryStats {

  private static long permGenUsedStart;

  private static long codeCacheUsedStart;

  private static long edenSpaceUsedStart;

  private static long tenuredGenUsedStart;

  private static long survivorSpaceUsedStart;

  private static boolean active;

  private static long[] statsStart;

  /**
   * 
   */
  public String measure() {
    if (!active)
      return "";
    try {
      MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
      System.runFinalization();
      Runtime.getRuntime().gc();

      String[] names = getNames();
      String[] labels = getLables();
      long[] statsEnd = new long[names.length];

      for (int i = 0; i < names.length; i++) {
        CompositeData cd = (CompositeData) mbs.getAttribute(new ObjectName(names[i]), "Usage");
        statsEnd[i] = Long.parseLong(String.valueOf(cd.get("used")));
      }

      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < names.length; i++) {
        sb.append(labels[i]).append("\t").append((statsEnd[i] / (1024))).append("\t").append(
            (statsEnd[i] - statsStart[i]) / (1024)).append("\tKB\t");

      }
      statsStart = statsEnd;

      return sb.toString();

    } catch (Exception ex) {
      return "";
    }

  }

  /**
   * 
   */
  public void baseLine() {
    try {
      MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
      System.runFinalization();
      Runtime.getRuntime().gc();

      String[] names = getNames();
      String[] labels = getLables();
      statsStart = new long[names.length];

      for (int i = 0; i < names.length; i++) {
        CompositeData cd = (CompositeData) mbs.getAttribute(new ObjectName(names[i]), "Usage");
        statsStart[i] = Long.parseLong(String.valueOf(cd.get("used")));
      }
      active = true;
    } catch (Exception ex) {
      active = false;
    }

  }

  /**
   * @return
   */
  protected abstract String[] getNames();

  /**
   * @return
   */
  protected abstract String[] getLables();

}
