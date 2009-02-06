/*
 * Licensed to the Sakai Foundation (SF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The SF licenses this file
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
package org.sakaiproject.kernel.test;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.sakaiproject.kernel.util.PathUtils;
import org.sakaiproject.kernel.util.StringUtils;

/**
 * 
 */
public class PathUtilsUT {
  private static final String[] USER_PATH_TEST = new String[] { null, "",
      "~test", "ieb236" };
  private static final String[] REFERENCE_PARENT_TEST = new String[] {
      "/user/home/ieb:/user/home", "/user/home/ieb///:/user/home",
      "/user/home////ieb:/user/home", "/user////home////ieb:/user////home",
      "/user/:/", "/////:/" };
  private static final Log LOG = LogFactory.getLog(PathUtilsUT.class);

  /**
   * Test method for
   * {@link org.sakaiproject.kernel.util.PathUtils#getUserPrefix(java.lang.String)}
   * .
   */
  @Test
  public void testGetUserPrefix() {

    for (String user : USER_PATH_TEST) {
      String userPath = PathUtils.getUserPrefix(user);
      if (user != null) {
        assertNotNull(userPath);
        assertTrue(userPath.length() > 2);
      }
      LOG.debug("User:" + user + ":" + userPath);
    }
  }

  /**
   * Test method for
   * {@link org.sakaiproject.kernel.util.PathUtils#getParentReference(java.lang.String)}
   * .
   */
  @Test
  public void testGetParentReference() {
    for (String testPair : REFERENCE_PARENT_TEST) {
      String[] p = StringUtils.split(testPair, ':');
      String parent = PathUtils.getParentReference(p[0]);
      LOG.debug("Checking "+testPair+" gave  "+parent);
      assertEquals(p[1], parent);
    }
  }

}
