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

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.fail;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easymock.EasyMock;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sakaiproject.kernel.api.ComponentActivatorException;
import org.sakaiproject.kernel.api.KernelManager;
import org.sakaiproject.kernel.api.UpdateFailedException;
import org.sakaiproject.kernel.api.authz.AccessControlStatement;
import org.sakaiproject.kernel.api.authz.AuthzResolverService;
import org.sakaiproject.kernel.api.authz.PermissionDeniedException;
import org.sakaiproject.kernel.api.authz.PermissionQuery;
import org.sakaiproject.kernel.api.authz.PermissionQueryService;
import org.sakaiproject.kernel.api.authz.ReferenceResolverService;
import org.sakaiproject.kernel.api.authz.ReferencedObject;
import org.sakaiproject.kernel.api.authz.SubjectStatement;
import org.sakaiproject.kernel.api.authz.SubjectStatement.SubjectType;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
import org.sakaiproject.kernel.authz.simple.JcrAccessControlStatementImpl;
import org.sakaiproject.kernel.authz.simple.JcrSubjectStatement;
import org.sakaiproject.kernel.authz.simple.SimplePermissionQuery;

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
import javax.servlet.http.HttpSession;

/**
 * 
 */
public class AuthZServiceTest extends KernelIntegrationBase {

  private static final Log LOG = LogFactory.getLog(AuthZServiceTest.class);

  @BeforeClass
  public static void beforeClass() throws ComponentActivatorException {
    KernelIntegrationBase.beforeClass();
  }
  
  @AfterClass
  public static void afterClass() {
    KernelIntegrationBase.afterClass();
  }
  
  @Test
  public void testCheck() throws JCRNodeFactoryServiceException, UpdateFailedException, AccessDeniedException, ItemExistsException, ConstraintViolationException, InvalidItemStateException, ReferentialIntegrityException, VersionException, LockException, NoSuchNodeTypeException, RepositoryException {
    LOG
        .info("Starting Test ====================================================");
    KernelManager km = new KernelManager();
    AuthzResolverService authzResolver = km
        .getService(AuthzResolverService.class);
    ReferenceResolverService referenceResolverService = km.getService(ReferenceResolverService.class);
    PermissionQueryService pqs = km.getService(PermissionQueryService.class);
    PermissionQuery pq = pqs.getPermission("GET");

    HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
    HttpServletResponse response = EasyMock
        .createMock(HttpServletResponse.class);
    HttpSession session = EasyMock.createMock(HttpSession.class);
    
    setupRequest(request,response,session,"ib236");
    replay(request, response, session);
    startRequest(request, response, "JSESSION");

    try {
      authzResolver.check("/x/y/z", pq);
      // no AuthZ exists, so assume it should generate a permission denied
      // exception.
      fail();
    } catch (PermissionDeniedException e) {
    }

    endRequest();
    verify(request, response, session);

    /*
    reset(request, response, session);

    
    
    
    setupRequest(request,response,session,"admin");
    replay(request, response, session);
    startRequest(request, response, "JSESSION");
    
    JCRNodeFactoryService jcrNodeFactory = km.getService(JCRNodeFactoryService.class);
    Node n = jcrNodeFactory.createFile("/test/a/b/c/d.txt");
    n.save();
    n.getSession().save();
    
    ReferencedObject ro = referenceResolverService.resolve("/test/a/b/c/d.txt");
    ReferencedObject parent = ro.getParent();
    parent = parent.getParent();
    
    // create an ACL at the parent that will allow those read permission in group1:maintain to perform httpget, make it apply ot all subnodes
    SubjectStatement subjectStatement = new JcrSubjectStatement(SubjectType.GROUP,"group1:maintain","read");
    AccessControlStatement grantReadToHttpGetInheritable = new JcrAccessControlStatementImpl(subjectStatement,"httpget",true,true); 
    parent.addAccessControlStatement(grantReadToHttpGetInheritable);
        
    SimplePermissionQuery permissionQuery = new SimplePermissionQuery("checkhttpget");
    permissionQuery.addQueryStatement(new SimpleQueryStatement("httpget"));    
    authzResolver.check("/test/a/b/c/d.txt", permissionQuery);
    
    endRequest();
    verify(request, response, session);
*/
    LOG
        .info("Completed Test ====================================================");
  }

  /**
   * @param request
   * @param response
   * @param session
   */
  private void setupRequest(HttpServletRequest request,
      HttpServletResponse response, HttpSession session, String userName) {

    expect(request.getSession()).andReturn(session).anyTimes();
    expect(request.getSession(true)).andReturn(session).anyTimes();
    expect(request.getSession(false)).andReturn(session).anyTimes();
    expect(session.getId()).andReturn("123").anyTimes();
    expect(session.getAttribute("_u")).andReturn(userName).anyTimes();
  }

}
