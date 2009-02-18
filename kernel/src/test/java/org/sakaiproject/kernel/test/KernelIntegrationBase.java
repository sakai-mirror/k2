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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.KernelConstants;
import org.sakaiproject.kernel.api.ComponentActivatorException;
import org.sakaiproject.kernel.api.KernelManager;
import org.sakaiproject.kernel.api.jcr.JCRService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
import org.sakaiproject.kernel.api.memory.CacheManagerService;
import org.sakaiproject.kernel.api.memory.CacheScope;
import org.sakaiproject.kernel.api.rest.RestProvider;
import org.sakaiproject.kernel.api.session.SessionManagerService;
import org.sakaiproject.kernel.api.site.SiteService;
import org.sakaiproject.kernel.api.user.UserResolverService;
import org.sakaiproject.kernel.api.userenv.UserEnvironmentResolverService;
import org.sakaiproject.kernel.component.KernelLifecycle;
import org.sakaiproject.kernel.user.jcr.JcrAuthenticationResolverProvider;
import org.sakaiproject.kernel.util.FileUtil;
import org.sakaiproject.kernel.util.PathUtils;
import org.sakaiproject.kernel.util.ResourceLoader;
import org.sakaiproject.kernel.util.StringUtils;
import org.sakaiproject.kernel.webapp.SakaiServletRequest;
import org.sakaiproject.kernel.webapp.SakaiServletResponse;
import org.sakaiproject.kernel.webapp.filter.SakaiAuthenticationFilter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

import javax.jcr.AccessDeniedException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A base class for integration tests the extender must invoke beforeClass and
 * afterClass as part of its lifecycle. It cannot be performed here as it will
 * not get invoked correctly.
 */
public class KernelIntegrationBase {
  private static final Log LOG = LogFactory.getLog(KernelIntegrationBase.class);
  private static KernelLifecycle kernelLifecycle;
  private static KernelManager kernelManager;
  private static UserEnvironmentResolverService userEnvironmentResolverService;
  private static final String USERBASE = "res://org/sakaiproject/kernel/test/sampleuserenv/";
  private static final String[] USERS = new String[] { "admin", "ib236", "ieb" };
  private static final String SITEBASE = "res://org/sakaiproject/kernel/test/samplesite/";
  private static final String[] SITES = new String[] { "site1", "site2" };

  public static synchronized boolean beforeClass()
      throws ComponentActivatorException {
    if (kernelManager == null) {
      System.err.println("no kernel has been started ");
      // If there are problems with startup and shutdown, these will prevent the
      // problem
      File jcrBase = new File("target/jcr");
      File dbBase = new File("target/testdb");
      System.err
          .println("==========================================================================");
      System.err.println("Removing all previous JCR and DB traces from "
          + jcrBase.getAbsolutePath() + " " + dbBase.getAbsolutePath());

      FileUtil.deleteAll(jcrBase);
      FileUtil.deleteAll(dbBase);
      System.err
          .println("==========================================================================");

      KernelManager.setTestMode();
      System
          .setProperty("sakai.kernel.properties",
              "inline://core.component.locations=\ncomponent.locations=classpath:;\n");

      kernelLifecycle = new KernelLifecycle();
      kernelLifecycle.start();

      kernelManager = new KernelManager();
      userEnvironmentResolverService = kernelManager
          .getService(UserEnvironmentResolverService.class);
      return true;
    } else {
      System.err.println("Reusing the kernel ");
      return false;
    }
  }

  public static void afterClass(boolean shutdown) {

    if (false) {
      try {
        kernelLifecycle.stop();
        KernelManager.clearTestMode();
      } catch (Exception ex) {
        LOG.info("Failed to stop kernel ", ex);
      }
      kernelManager = null;
      kernelLifecycle = null;
      KernelIntegrationBase.enableKernelStartup();
    } else {
      System.err.println("Keeping kernel alive ");
    }
  }

  /**
   * 
   */
  protected void endRequest() {
    CacheManagerService cacheManagerService = kernelManager
        .getService(CacheManagerService.class);
    cacheManagerService.unbind(CacheScope.REQUEST);
  }

  /**
   * @param request
   * @param response
   * @param cookieName
   * @return
   */
  protected HttpServletResponse startRequest(HttpServletRequest request,
      HttpServletResponse response, String cookieName,
      UserResolverService userResolverService) {
    SessionManagerService sessionManagerService = kernelManager
        .getService(SessionManagerService.class);
    SakaiServletRequest wrequest = new SakaiServletRequest(request, response,
        userResolverService, sessionManagerService);
    SakaiServletResponse wresponse = new SakaiServletResponse(response);
    sessionManagerService.bindRequest(wrequest);
    return wresponse;

  }

  /**
   * @throws IOException
   * @throws AccessDeniedException
   * @throws RepositoryException
   * @throws JCRNodeFactoryServiceException
   * @throws InterruptedException
   * @throws NoSuchAlgorithmException
   */
  public static void loadTestUsers() throws IOException, AccessDeniedException,
      RepositoryException, JCRNodeFactoryServiceException,
      InterruptedException, NoSuchAlgorithmException {
    KernelManager km = new KernelManager();
    JCRNodeFactoryService jcrNodeFactoryService = km
        .getService(JCRNodeFactoryService.class);
    JCRService jcrService = km.getService(JCRService.class);
    for (String user : USERS) {
      InputStream in = ResourceLoader.openResource(USERBASE + user + ".json",
          SakaiAuthenticationFilter.class.getClassLoader());
      Node n = jcrNodeFactoryService.setInputStream(getUserEnvPath(user), in,
          RestProvider.CONTENT_TYPE);

      n.setProperty(JcrAuthenticationResolverProvider.JCRPASSWORDHASH,
          StringUtils.sha1Hash("password"));
      n.save();
      in.close();
    }
    jcrService.getSession().save();
    Thread.yield();
    Thread.sleep(1000);

  }

  public static void loadTestSites() throws IOException,
      JCRNodeFactoryServiceException, RepositoryException,
      NoSuchAlgorithmException, InterruptedException {
    KernelManager km = new KernelManager();
    JCRNodeFactoryService jcrNodeFactoryService = km
        .getService(JCRNodeFactoryService.class);
    JCRService jcrService = km.getService(JCRService.class);
    for (String siteName : SITES) {
      InputStream in = ResourceLoader.openResource(SITEBASE + siteName
          + "/groupdef.json", KernelIntegrationBase.class.getClassLoader());
      Node n = jcrNodeFactoryService.setInputStream(KernelIntegrationBase
          .buildUsersOwnedSitesFilePath("ib236", siteName), in,
          RestProvider.CONTENT_TYPE);

      n.save();
      in.close();
      LOG.info("Test site saved: "
          + KernelIntegrationBase.buildUsersOwnedSitesFilePath("ib236",
              siteName));
    }
    jcrService.getSession().save();
    Thread.yield();
    Thread.sleep(1000);
    LOG.info("test sites loaded.");
  }

  protected static String buildUsersOwnedSitesFilePath(String userId,
      String siteIndexId) {
    String userPath = userEnvironmentResolverService
        .getUserEnvironmentBasePath(userId);
    String siteNode = userPath + SiteService.PATH_MYSITES
        + PathUtils.getUserPrefix(siteIndexId) + SiteService.FILE_GROUPDEF;
    return siteNode;
  }

  /**
   * @return
   */
  public static String getUserEnvPath(String userId) {
    String prefix = PathUtils.getUserPrefix(userId);
    return "/userenv" + prefix + KernelConstants.USERENV;
  }

  /**
   * Stops the default kernel from starting up when the lifecycle is started,
   * used when there is a test that wants to test an aspect of the kernel
   * startup.
   */
  public static void disableKernelStartup() {
    System.setProperty("sakai.kernel.properties",
        "inline://core.component.locations=\n");
  }

  /**
   * Re-enable kernel startup
   */
  public static void enableKernelStartup() {
    System.clearProperty("sakai.kernel.properties");
  }

}
