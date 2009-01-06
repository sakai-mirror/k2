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
package org.sakaiproject.kernel.authz.simple;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.authz.SubjectPermissionService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
import org.sakaiproject.kernel.api.serialization.BeanConverter;
import org.sakaiproject.kernel.api.session.SessionManagerService;
import org.sakaiproject.kernel.jcr.api.JcrContentListener;
import org.sakaiproject.kernel.model.GroupBean;
import org.sakaiproject.kernel.model.RoleBean;
import org.sakaiproject.kernel.model.SubjectPermissionBean;
import org.sakaiproject.kernel.util.IOUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

/**
 * 
 */
public class SubjectPermissionListener implements JcrContentListener {

  private static final Log LOG = LogFactory
      .getLog(SubjectPermissionListener.class);
  private static final String GROUP_FILE_NAME = "groupdef.json";
  private BeanConverter beanConverter;
  private JCRNodeFactoryService jcrNodeFactoryService;
  private EntityManager entityManager;
  private SubjectPermissionService subjectPermissionService;

  /**
   * @param entityManager
   * 
   */
  @Inject
  public SubjectPermissionListener(
      JCRNodeFactoryService jcrNodeFactoryService,
      @Named(SimpleJcrUserEnvironmentResolverService.JCR_USERENV_BASE) String userEnvironmentBase,
      @Named(BeanConverter.REPOSITORY_BEANCONVETER) BeanConverter beanConverter,
      SessionManagerService sessionManagerService,
      SubjectPermissionService subjectPermissionService,
      EntityManager entityManager) {
    this.jcrNodeFactoryService = jcrNodeFactoryService;
    this.beanConverter = beanConverter;
    this.subjectPermissionService = subjectPermissionService;
    this.entityManager = entityManager;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.jcr.api.JcrContentListener#onEvent(int,
   *      java.lang.String, java.lang.String, java.lang.String)
   */
  public void onEvent(int type, String userID, String filePath, String fileName) {
    if (fileName.equals(GROUP_FILE_NAME)) {
      try {
        String groupBody = IOUtils.readFully(jcrNodeFactoryService
            .getInputStream(filePath), "UTF-8");
        GroupBean groupBean = beanConverter.convertToObject(groupBody,
            GroupBean.class);

        // expire all permission sets associated with this
        for (String subjectToken : groupBean.getSubjectTokens()) {
          subjectPermissionService.expire(subjectToken);
        }
        
        

        // the user environment bean contains a list of subjects, which the
        // users membership of groups
        Query query = entityManager
            .createNamedQuery(SubjectPermissionBean.FINDBY_GROUP);
        query.setParameter(SubjectPermissionBean.PARAM_GROUP, groupBean.getName());
        List<?> subjectPermissionList = query.getResultList();
        List<SubjectPermissionBean> toAdd = new ArrayList<SubjectPermissionBean>();
        List<SubjectPermissionBean> toRemove = new ArrayList<SubjectPermissionBean>();

        for (Object o : subjectPermissionList) {
          SubjectPermissionBean subjectPermissionBean = (SubjectPermissionBean) o;
          String subjectToken = subjectPermissionBean.getSubjectToken();
          String permission = subjectPermissionBean.getPermissionToken();
          boolean found = false;
          for (RoleBean role : groupBean.getRoles()) {
            String subject = role.getSubjectToken(groupBean.getName());          
            if (subjectToken.equals(subject)) {
              for ( String rolePermission : role.getPermissions() ) {
                if ( permission.equals(rolePermission) ) {
                  found = true;
                  break;
                }
              }
              if ( found ) {
                break;
              }
            }
          }
          if (!found) {
            toRemove.add(subjectPermissionBean);
          }
        }

        for (RoleBean roleBean : groupBean.getRoles()) {
          String subject = roleBean.getSubjectToken(groupBean.getName());
          for ( String permission : roleBean.getPermissions() ) {
            boolean found = false;
            for (Object o : subjectPermissionList) {
              SubjectPermissionBean subjectPermissionBean = (SubjectPermissionBean) o;
              if (subject.equals(subjectPermissionBean.getSubjectToken()) && permission.equals(subjectPermissionBean.getPermissionToken()) ) {
                found = true;
                break;
              }
            }
            if (!found) {
              toAdd.add(new SubjectPermissionBean(groupBean.getName(), roleBean.getName(), subject, permission));
            }
            
          }
        }

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        for (SubjectPermissionBean spb : toRemove) {
          entityManager.remove(spb);
        }
        for (SubjectPermissionBean spb : toAdd) {
          entityManager.persist(spb);
        }
        transaction.commit();

      } catch (UnsupportedEncodingException e) {
        LOG.error(e);
      } catch (IOException e) {
        LOG.warn("Failed to read userenv " + filePath + " cause :"
            + e.getMessage());
        LOG.debug(e);
      } catch (RepositoryException e) {
        LOG.warn("Failed to read userenv for " + filePath + " cause :"
            + e.getMessage());
        LOG.debug(e);
      } catch (JCRNodeFactoryServiceException e) {
        LOG.warn("Failed to read userenv for " + filePath + " cause :"
            + e.getMessage());
        LOG.debug(e);
      }
    }

  }
}