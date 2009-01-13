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
package org.sakaiproject.kernel.test;

import com.google.inject.Injector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.Activator;
import org.sakaiproject.kernel.api.ComponentActivatorException;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.KernelManager;
import org.sakaiproject.kernel.api.RequiresStop;
import org.sakaiproject.kernel.api.ServiceSpec;
import org.sakaiproject.kernel.api.ShutdownService;
import org.sakaiproject.kernel.api.jcr.JCRService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
import org.sakaiproject.kernel.api.memory.CacheManagerService;
import org.sakaiproject.kernel.api.memory.CacheScope;
import org.sakaiproject.kernel.api.session.SessionManagerService;
import org.sakaiproject.kernel.api.user.UserResolverService;
import org.sakaiproject.kernel.authz.simple.SimpleJcrUserEnvironmentResolverService;
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
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;
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
  private static Injector injector;
  private static final String USERBASE = "res://org/sakaiproject/kernel/test/sampleuserenv/";
  private static final String[] USERS = new String[] {"admin","ib236","ieb" };


  public static void beforeClass() throws ComponentActivatorException {
    // If there are problems with startup and shutdown, these will prevent the
    // problem
    File jcrBase = new File("target/jcr");
    File dbBase = new File("target/testdb");
    System.err.println("==========================================================================");
    System.err.println("Removing all previous JCR and DB traces from "+jcrBase.getAbsolutePath()+" "+dbBase.getAbsolutePath());
    
    FileUtil.deleteAll(jcrBase);
    FileUtil.deleteAll(dbBase);
    System.err.println("==========================================================================");

    kernelLifecycle = new KernelLifecycle();
    kernelLifecycle.start();

    kernelManager = new KernelManager();
    Kernel kernel = kernelManager.getKernel();
    Activator activator = new Activator();
    activator.activate(kernel);
    for (Class<?> c : Activator.SERVICE_CLASSES) {

      ShutdownService ss = kernel.getServiceManager().getService(
          new ServiceSpec(ShutdownService.class));
      Object s = kernel.getServiceManager().getService(new ServiceSpec(c));
      if (s instanceof RequiresStop) {
        ss.register((RequiresStop) s);
      }
    }
    injector = activator.getInjector();
  }

  public static void afterClass() {
    try {
      kernelLifecycle.stop();
    } catch (Exception ex) {
      LOG.info("Failed to stop kernel ", ex);
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
      HttpServletResponse response, String cookieName, UserResolverService userResolverService) {
    SakaiServletRequest wrequest = new SakaiServletRequest(request,userResolverService);
    SakaiServletResponse wresponse = new SakaiServletResponse(response,
        cookieName);
    SessionManagerService sessionManagerService = kernelManager
        .getService(SessionManagerService.class);
    sessionManagerService.bindRequest(wrequest);
    return wresponse;

  }
  
  /**
   * @return the injector
   */
  public static Injector getInjector() {
    return injector;
  }

  /**
   * @throws IOException
   * @throws AccessDeniedException
   * @throws RepositoryException
   * @throws JCRNodeFactoryServiceException
   * @throws InterruptedException 
   * @throws NoSuchAlgorithmException 
   */
  public static void loadTestUsers() throws IOException, AccessDeniedException,  RepositoryException, JCRNodeFactoryServiceException, InterruptedException, NoSuchAlgorithmException {
    KernelManager km = new KernelManager();
    JCRNodeFactoryService jcrNodeFactoryService = km.getService(JCRNodeFactoryService.class);
    JCRService jcrService = km.getService(JCRService.class);
    for ( String user : USERS ) {
      InputStream in = ResourceLoader.openResource(USERBASE+user+".json", SakaiAuthenticationFilter.class.getClassLoader());
      Node n = jcrNodeFactoryService.setInputStream(getUserEnvPath("ieb"), in);
      n.setProperty(JcrAuthenticationResolverProvider.JCRPASSWORDHASH, StringUtils.sha1Hash("password"));
      in.close();
    }
    jcrService.getSession().save();
    Thread.yield();
    Thread.sleep(1000);
    
    
    
    
  }
  /**
   * @return
   */
  public static String getUserEnvPath(String userId) {
    String prefix = PathUtils.getUserPrefix(userId);
    return "/userenv" + prefix + SimpleJcrUserEnvironmentResolverService.USERENV;
  }

}
