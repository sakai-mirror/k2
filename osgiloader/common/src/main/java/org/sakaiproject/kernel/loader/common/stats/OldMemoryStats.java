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

/**
 * @author ieb
 * 
 */
public class OldMemoryStats extends AbstractStats implements MemoryStats {

  private static final String[] NAMES = new String[] { "java.lang:type=MemoryPool,name=Perm Gen",
      "java.lang:type=MemoryPool,name=Tenured Gen", "java.lang:type=MemoryPool,name=Code Cache",
      "java.lang:type=MemoryPool,name=Eden Space", "java.lang:type=MemoryPool,name=Survivor Space" };

  private static final String[] LABELS = { "        Permgen Used ", "        Tenured Used ",
      "     Code Cache Used ", "           Eden Used ", "       Survivor Used " };

  /**
   * @return
   */
  protected String[] getLables() {
    return LABELS;
  }

  /**
   * @return
   */
  protected String[] getNames() {
    return NAMES;
  }

}
