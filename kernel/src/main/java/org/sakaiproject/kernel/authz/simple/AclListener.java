/*******************************************************************************
 * Copyright 2008 Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.sakaiproject.kernel.authz.simple;

import com.google.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.authz.AccessControlStatement;
import org.sakaiproject.kernel.api.jcr.JCRConstants;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
import org.sakaiproject.kernel.jcr.api.JcrContentListener;
import org.sakaiproject.kernel.model.AclIndexBean;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.observation.Event;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

/**
 *
 */
public class AclListener implements JcrContentListener {

  private static final Log LOG = LogFactory.getLog(AclListener.class);
  private final JCRNodeFactoryService jcrNodeFactoryService;
  private final EntityManager entityManager;

  @Inject
  public AclListener(JCRNodeFactoryService jcrNodeFactoryService,
      EntityManager entityManager) {
    this.jcrNodeFactoryService = jcrNodeFactoryService;
    this.entityManager = entityManager;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.jcr.api.JcrContentListener#onEvent(int,
   *      java.lang.String, java.lang.String, java.lang.String)
   */
  public void onEvent(int type, String userID, String filePath, String fileName) {
    if (type == Event.PROPERTY_ADDED || type == Event.PROPERTY_CHANGED
        || type == Event.PROPERTY_REMOVED) {

      ArrayList<AclIndexBean> toCreate = new ArrayList<AclIndexBean>();
      ArrayList<AclIndexBean> toUpdate = new ArrayList<AclIndexBean>();
      ArrayList<AclIndexBean> toDelete = new ArrayList<AclIndexBean>();

      Query query = entityManager
          .createNamedQuery(AclIndexBean.Queries.FINDBY_PATH);
      query.setParameter(AclIndexBean.QueryParams.FINDBY_PATH_PATH, filePath);
      List<?> currentIndex = query.getResultList();

      try {
        Node node = jcrNodeFactoryService.getNode(filePath);
        Property acl = node.getProperty(JCRConstants.MIX_ACL);
        for (Value val : acl.getValues()) {
          AccessControlStatement acs = new JcrAccessControlStatementImpl(val
              .getString());

          switch (type) {
          case Event.PROPERTY_ADDED:
            if (inList(acs, currentIndex) == null) {
              toCreate.add(convert(acs));
            }
            break;
          case Event.PROPERTY_CHANGED:
            AclIndexBean indexBean = inList(acs, currentIndex);
            if (indexBean != null) {
              toUpdate.add(indexBean);
            }
            break;
          case Event.PROPERTY_REMOVED:
            if (inList(acs, currentIndex) == null) {
              toDelete.add(convert(acs));
            }
            break;
          }
        }

        EntityTransaction trans = entityManager.getTransaction();
        trans.begin();
        try {
          if (!toCreate.isEmpty()) {
            for (AclIndexBean bean : toCreate) {
              entityManager.persist(bean);
            }
          } else if (!toUpdate.isEmpty()) {
            for (AclIndexBean bean : toUpdate) {
              entityManager.persist(bean);
            }
          } else if (!toDelete.isEmpty()) {
            for (AclIndexBean bean : toDelete) {
              entityManager.remove(bean);
            }
          }
          trans.commit();
        } catch (Exception e) {
          LOG.error(
              "Transaction rolled back due to a problem when updating the ACL index: "
                  + e.getMessage(), e);
          trans.rollback();
        }
      } catch (PathNotFoundException e) {
        // nothing to care about. this happens when there is no ACL on the node
      } catch (RepositoryException e) {
        // nothing we can do
        LOG.error(e.getMessage(), e);
      } catch (JCRNodeFactoryServiceException e) {
        // nothing we can do
        LOG.error(e.getMessage(), e);
      }
    }
  }

  private AclIndexBean convert(AccessControlStatement acs) {
    AclIndexBean bean = new AclIndexBean();
    bean.setKey(acs.getStatementKey());
    bean.setSubject(acs.getSubject());
    bean.setGranted(acs.isGranted());
    return bean;
  }

  private AclIndexBean inList(AccessControlStatement stmt,
      List<?> list) {
    AclIndexBean found = null;

    boolean stmtNotNull = stmt != null;
    boolean listNotEmpty = list != null && list.size() > 0;

    if (stmtNotNull && listNotEmpty) {
      for (Object listBeanO : list) {
        AclIndexBean listBean = (AclIndexBean) listBeanO;
        boolean same = true;
        same &= stmt.getStatementKey().equals(listBean.getKey());
        same &= stmt.getSubject().getSubjectType().toString().equals(
            listBean.getSubjectType());
        same &= stmt.getSubject().getSubjectToken().equals(
            listBean.getSubjectToken());
        same &= stmt.getSubject().getPermissionToken().equals(
            listBean.getPermissionToken());
        if (same) {
          found = listBean;
          break;
        }
      }
    }

    return found;
  }
}
