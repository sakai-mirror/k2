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
import com.google.inject.name.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
import org.sakaiproject.kernel.jcr.api.internal.RepositoryStartupException;
import org.sakaiproject.kernel.jcr.api.internal.StartupAction;
import org.sakaiproject.kernel.user.UserFactoryService;
import org.sakaiproject.kernel.user.jcr.JcrUserFactoryService;
import org.sakaiproject.kernel.util.PathUtils;
import org.sakaiproject.kernel.util.ResourceLoader;
import org.sakaiproject.kernel.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.Map.Entry;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Populate the repository with some basic files, by reading a properties file.
 */
public class PopulateBaseRepository implements StartupAction {

  private static final Log LOG = LogFactory
      .getLog(PopulateBaseRepository.class);
  private static final String RESOURCES_TO_LOAD = "res://org/sakaiproject/kernel/jcr/jackrabbit/populate_repository.properties";
  private JCRNodeFactoryService jcrNodeFactoryService;
  private String userEnvironmentBase;

  /**
   * Construct the populate action, injecting the {@link JCRNodeFactoryService}
   */
  @Inject
  public PopulateBaseRepository(
      JCRNodeFactoryService jFactoryService,
      @Named(JcrUserFactoryService.JCR_USERENV_BASE) String userEnvironmentBase) {
    this.jcrNodeFactoryService = jFactoryService;
    this.userEnvironmentBase = userEnvironmentBase;
  }

  /**
   * {@inheritDoc}
   * 
   * Processes a set of items to add to the repository, taken from
   * propulate_repository.properties. These are structured as follows, the key
   * specifies the location and the value specifies the content. The key may
   * optionally be split by a @ character, and if it is, after the @ defines a
   * function to be applied to the key before the @ to generate the path.
   * Currently there is only a user environment function that will convert the
   * key into a path inside the user environment space.
   * 
   * The content is split onto content specifications separated by ';'. Each
   * content spec may be delimited by =, the first element defining the type of
   * content. There are 3 types, 'body' where the second element of the content
   * spec is a resource reference resolvable by the ResourceLoader. If the first
   * element is property, the second element is the name of the property and the
   * third the value of the property. If the first element is property-sha1,
   * then the value of the property is encoded with a SHA1 message digest.
   * 
   * @throws RepositoryStartupException
   * 
   * @see org.sakaiproject.kernel.jcr.api.internal.StartupAction#startup(javax.jcr.Session)
   */
  public boolean startup(Session s) throws RepositoryStartupException {
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
        String[] pathSpec = StringUtils.split((String) r.getKey(), '@');
        String path = null;
        if (pathSpec.length > 1) {
          if ("userenv".equals(pathSpec[1])) {
            path = getUserEnvPath(pathSpec[0]);
          } else {
            path = pathSpec[0];
          }
        } else {
          path = pathSpec[0];
        }

        Node n = jcrNodeFactoryService.getNode(path);
        if (n == null) {
          String[] content = StringUtils.split((String) r.getValue(), ';');
          for (String c : content) {
            String[] b = StringUtils.split(c, '=');
            if ("body".equals(b[0])) {
              LOG.info("Creating startup repository node " + path);
              n = jcrNodeFactoryService.createFile(path);
              InputStream resoruceStream = ResourceLoader.openResource(b[1],
                  this.getClass().getClassLoader());
              try {
                jcrNodeFactoryService.setInputStream(path, resoruceStream);
                n.save();
              } finally {
                resoruceStream.close();
              }
            } else if ("property".equals(b[0])) {
              n = jcrNodeFactoryService.createFile(path);
              n.setProperty(b[1], b[2]);
              n.save();
            } else if ("property-sha1".equals(b[0])) {
              n = jcrNodeFactoryService.createFile(path);
              n.setProperty(b[1], StringUtils.sha1Hash(b[2]));
              n.save();
            }
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
    } catch (NoSuchAlgorithmException e) {
      throw new RepositoryStartupException("Failed to populate Repository ", e);
    }
    return false;
  }

  /**
   * @return generate the user environment path given an internal ID for the
   *         user.
   */
  public String getUserEnvPath(String userId) {
    String prefix = PathUtils.getUserPrefix(userId);
    return userEnvironmentBase + prefix
        + UserFactoryService.USERENV;
  }

}
