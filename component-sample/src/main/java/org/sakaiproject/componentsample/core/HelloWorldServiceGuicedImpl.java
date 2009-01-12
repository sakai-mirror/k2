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
package org.sakaiproject.componentsample.core;

import com.google.inject.Inject;

import org.sakaiproject.componentsample.api.HelloWorldService;
import org.sakaiproject.componentsample.api.InternalDateService;
import org.sakaiproject.kernel.api.jcr.JCRService;
import org.sakaiproject.kernel.model.GroupMembershipBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Repository;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * This is the service implementation.
 */
public class HelloWorldServiceGuicedImpl implements HelloWorldService {

  /**
   * The internal service that will provide dates.
   */
  private InternalDateService internalDateService;
  private JCRService jcrService;
  private EntityManager entityManager;
  
  /**
   * A constructor that supports injection, so I know that when the class is
   * created, it is complete and ready for use.
   * 
   * @param internalDateService
   *          an instance of the InternalDateService that I want this to use.
   * @param jcrService
   *          an instance of the JCRService that I want this to use.
   * @param entityManager
   *          an instance of the EntityManager that I want this to use.
   */
  @Inject
  public HelloWorldServiceGuicedImpl(InternalDateService internalDateService, JCRService jcrService, EntityManager entityManager) {
    this.internalDateService = internalDateService;
    this.jcrService = jcrService;
    this.entityManager = entityManager;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.componentsample.api.HelloWorldService#getGreeting()
   */
  public String getGreeting() {
    return "Hi there, the time is " + internalDateService.getDate();
  }
  
  public Map<String, String> getJCRInfo() {
    // Get some info about the JCR Repository
    Repository repo = jcrService.getRepository();
    String[] jcrDesKeys = repo.getDescriptorKeys();
    Map<String, String> jcrInfo = new HashMap<String, String>();
    for (String key : jcrDesKeys) {
      jcrInfo.put(key, repo.getDescriptor(key));
    }
    return jcrInfo;
  }

  public Map<String, String> getJPAInfo() {
    //// You could write your own JPA query that would look like:
    //@SuppressWarnings("unchecked")
    //List<GroupMembershipBean> groupMemberships = entityManager.createQuery("select g from  GroupMembershipBean g").getResultList();
    
    // We're going to use an existing named query for the example though
    Query adminGroupQuery = entityManager.createNamedQuery(GroupMembershipBean.FINDBY_USER);
    adminGroupQuery.setParameter(GroupMembershipBean.USER_PARAM, "admin");
    @SuppressWarnings("unchecked")
    List<GroupMembershipBean> adminGroupList = adminGroupQuery.getResultList();
    
    // The rest of this is just parsing out the result for display purposes
    Map<String, String> adminGroups = new HashMap<String, String>();
    for (GroupMembershipBean gmb : adminGroupList){
      adminGroups.put(gmb.getGroupId(), gmb.getRoleId());
    }
    return adminGroups;
  }
}