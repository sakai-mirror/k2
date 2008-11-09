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
package org.sakaiproject.kernel.jcr.jackrabbit.sakai;

import com.google.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
import org.sakaiproject.kernel.jcr.api.internal.RepositoryStartupException;
import org.sakaiproject.kernel.jcr.api.internal.StartupAction;
import org.sakaiproject.kernel.util.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Map.Entry;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Populate the repository with some basic files.
 */
public class PopulateBaseRepository implements StartupAction {

  private static final Log LOG = LogFactory
      .getLog(PopulateBaseRepository.class);
  private static final String RESOURCES_TO_LOAD = "res://org/sakaiproject/kernel/jcr/jackrabbit/populate_repository.properties";
  private JCRNodeFactoryService jcrNodeFactoryService;

  /**
   * Construct the populate action, injecting the {@link JCRNodeFactoryService}
   */
  @Inject
  public PopulateBaseRepository(JCRNodeFactoryService jFactoryService) {
    this.jcrNodeFactoryService = jFactoryService;
  }

  /**
   * {@inheritDoc}
   * 
   * @throws RepositoryStartupException
   * 
   * @see org.sakaiproject.kernel.jcr.api.internal.StartupAction#startup(javax.jcr.Session)
   */
  public void startup(Session s) throws RepositoryStartupException {
    try {
      InputStream in = ResourceLoader.openResource(RESOURCES_TO_LOAD, this
          .getClass().getClassLoader());
      Properties p = new Properties();
      try {
        p.load(in);
      } finally {
        in.close();
      }
      for (Entry<?, ?> r : p.entrySet()) {
        String path = (String) r.getKey();
        String content = (String) r.getValue();
        Node n = jcrNodeFactoryService.getNode(path);
        LOG.info("Got start node " + n);
        if (n == null) {
          LOG.info("Creating startup repository node " + path);
          n = jcrNodeFactoryService.createFile(path);
          InputStream resoruceStream = ResourceLoader.openResource(content,
              this.getClass().getClassLoader());
          try {
            jcrNodeFactoryService.setInputStream(path, resoruceStream);
            n.save();
          } finally {
            resoruceStream.close();
          }
        } else {
          LOG.info("Starting Node already exists " + path);
        }
      }
    } catch (IOException e) {
      throw new RepositoryStartupException("Failed to populate Repository ", e);
    } catch (RepositoryException e) {
      throw new RepositoryStartupException("Failed to populate Repository ", e);
    } catch (JCRNodeFactoryServiceException e) {
      throw new RepositoryStartupException("Failed to populate Repository ", e);
    }
  }
}
