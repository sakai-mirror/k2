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

import org.mortbay.jetty.Server;
import org.sakaiproject.kernel.loader.server.jetty.KernelLoader;

/**
 * A skeleton Jetty Server.
 */
public class JettyServer {

  /**
   * The port to run the server on.
   */
  private static final int JETTY_PORT = 9003;

  /**
   * the jetty server.
   */
  private final Server server;


  /**
   * Create a new Jetty Server.
   * @throws Exception if the jetty server wont create.
   */
  public JettyServer() throws Exception {
    server = createServer(JETTY_PORT);
  }

  /**
   * Start the jetty server up.
   * @throws Exception if the jetty server wont start.
   */
  public final void start() throws Exception {
    server.start();
  }

  /**
   * Stop the server.
   * @throws Exception if the server fails to stop.
   */
  public final void stop() throws Exception {
    server.stop();
  }

  /**
   * Starts the server for end-to-end tests.
   * @param port the port to open the server on.
   * @return the server to return.
   * @throws Exception if the server can't be created.
   */
  private Server createServer(int port) throws Exception {
    Server newServer = new Server(port);
    KernelLoader kl = new KernelLoader();
    newServer.addLifeCycle(kl);
    return newServer;
  }
}
