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

package org.sakaiproject.kernel.jcr.jackrabbit.sakai;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.core.WorkspaceImpl;
import org.sakaiproject.kernel.jcr.api.internal.StartupAction;
import org.sakaiproject.kernel.jcr.jackrabbit.JCRServiceImpl;

import java.util.Arrays;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Performs basic repository startup.
 * 
 */
public class SakaiRepositoryStartup implements StartupAction {
  private static final Log log = LogFactory
      .getLog(SakaiRepositoryStartup.class);

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.kernel.jcr.api.internal.StartupAction#startup(javax.jcr.Session)
   */
  public void startup(Session s) {

    try {
      WorkspaceImpl workspace = (WorkspaceImpl) s.getWorkspace();
      List<String> existingWorkspaces = Arrays.asList(workspace
          .getAccessibleWorkspaceNames());
      if (!existingWorkspaces.contains(JCRServiceImpl.DEFAULT_WORKSPACE)) {
        if (log.isInfoEnabled())
          log.info("Creating Workspace Sakai ");
        workspace.createWorkspace(JCRServiceImpl.DEFAULT_WORKSPACE);
        log.info("Created default Sakai Jackrabbit Workspace: "
            + JCRServiceImpl.DEFAULT_WORKSPACE);
      }
      if (!s.getRootNode().hasNode("testdata")) {
        if (log.isInfoEnabled()) {
          log.info("Creating Test Data ");
        }
        s.getRootNode().addNode("testdata", "nt:unstructured");
        if (log.isInfoEnabled()) {
          log.info("Added Test Data Node Under Root");
        }
      } else {
        if (log.isInfoEnabled()) {
          log.info("Added Test Data Node Under Already present");
        }
      }

    } catch (RepositoryException ex) {
      log.info("Failed to startup repo", ex);
      throw new IllegalStateException(
          "Failed to add Sakai Jackrabbit JCR root node, JCR workspace/repository failure",
          ex);
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }
}
