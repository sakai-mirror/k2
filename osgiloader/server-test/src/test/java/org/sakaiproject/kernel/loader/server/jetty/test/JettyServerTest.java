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
package org.sakaiproject.kernel.loader.server.jetty.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test the startup.
 */
public class JettyServerTest {

  /**
   * holds the jetty server.
   */
  private static JettyServer server;

  /**
   * create the jetty server and prepare it for use.
   * @throws Exception when the server starts up.
   */
  @BeforeClass
  public static void setUpOnce() throws Exception {
    server = new JettyServer();
    server.start();
  }

  /**
   * tear down the server.
   * @throws Exception when the server failed to shutdown
   */
  @AfterClass
  public static void tearDownOnce() throws Exception {
    server.stop();
  }

  /**
   * Test the startup and teardown.
   */
  @Test
  public void testStartup() {

  }

}
