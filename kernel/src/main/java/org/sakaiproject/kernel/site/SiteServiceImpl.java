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

package org.sakaiproject.kernel.site;

import com.google.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.authz.AuthzResolverService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
import org.sakaiproject.kernel.api.rest.RestProvider;
import org.sakaiproject.kernel.api.serialization.BeanConverter;
import org.sakaiproject.kernel.api.session.SessionManagerService;
import org.sakaiproject.kernel.api.site.NonUniqueIdException;
import org.sakaiproject.kernel.api.site.SiteCreationException;
import org.sakaiproject.kernel.api.site.SiteException;
import org.sakaiproject.kernel.api.site.SiteService;
import org.sakaiproject.kernel.api.user.User;
import org.sakaiproject.kernel.api.userenv.UserEnvironmentResolverService;
import org.sakaiproject.kernel.model.SiteBean;
import org.sakaiproject.kernel.model.SiteIndexBean;
import org.sakaiproject.kernel.util.IOUtils;
import org.sakaiproject.kernel.util.PathUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 *
 */
public class SiteServiceImpl implements SiteService {

  private static final Log log = LogFactory.getLog(SiteServiceImpl.class);

  private final EntityManager entityManager;
  private final JCRNodeFactoryService jcrNodeFactoryService;
  private final BeanConverter beanConverter;
  private final UserEnvironmentResolverService userEnvRes;
  private final SessionManagerService sessMgr;

  private AuthzResolverService authzResolverService;

  @Inject
  public SiteServiceImpl(EntityManager entityManager,
      JCRNodeFactoryService jcrNodeFactoryService, BeanConverter beanConverter,
      UserEnvironmentResolverService userEnvRes, SessionManagerService sessMgr,
      AuthzResolverService authzResolverService) {
    this.entityManager = entityManager;
    this.jcrNodeFactoryService = jcrNodeFactoryService;
    this.beanConverter = beanConverter;
    this.userEnvRes = userEnvRes;
    this.sessMgr = sessMgr;
    this.authzResolverService = authzResolverService;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.site.SiteService#createSite(org.sakaiproject.kernel.model.SiteBean)
   */
  public void createSite(SiteBean site) throws SiteCreationException,
      NonUniqueIdException {
    if (!siteExists(site.getId())) {
      saveSite(site);
    } else {
      throw new NonUniqueIdException("Site ID [" + site.getId() + "] exists");
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.site.SiteService#getSite(java.lang.String)
   */
  public SiteBean getSite(String id) throws SiteException {
    // convert to a bean, the
    SiteBean bean = null;
    InputStream in = null;
    authzResolverService.setRequestGrant("INSECURE REMOVE THIS Getting site");
    try {
      Query query = entityManager
          .createNamedQuery(SiteIndexBean.Queries.FINDBY_ID);
      query.setParameter(SiteIndexBean.QueryParams.FINDBY_ID_ID, id);

      SiteIndexBean index = (SiteIndexBean) query.getSingleResult();
      String fileNode = index.getRef();
      in = jcrNodeFactoryService.getInputStream(fileNode);
      String siteBody = IOUtils.readFully(in, "UTF-8");
      bean = beanConverter.convertToObject(siteBody, SiteBean.class);
    } catch (UnsupportedEncodingException e) {
      throw new SiteException(e.getMessage(), e);
    } catch (IOException e) {
      throw new SiteException(e.getMessage(), e);
    } catch (RepositoryException e) {
      throw new SiteException(e.getMessage(), e);
    } catch (NoResultException e) {
      // this happens when the query doesn't find anything
      bean = null;
    } catch (JCRNodeFactoryServiceException e) {
      // this happens when the node isn't found
      bean = null;
    } finally {
      try {
        in.close();
      } catch (Exception ex) {
      }
      authzResolverService.clearRequestGrant();
    }
    return bean;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.site.SiteService#siteExists(java.lang.String)
   */
  public boolean siteExists(String id) {
    Query query = entityManager
        .createNamedQuery(SiteIndexBean.Queries.COUNTBY_ID);
    query.setParameter(SiteIndexBean.QueryParams.COUNTBY_ID_ID, id);
    long count = (Long) query.getSingleResult();
    return count != 0;
  }

  /**
   * Build the full path with file name to the group definition for a given site
   * ID.
   * 
   * @param id
   * @return
   */
  private String buildFilePath(String id) {
    User user = sessMgr.getCurrentSession().getUser();
    if (user == null || user.getUuid() == null) {
      throw new SecurityException("Permission Denied: Not logged in");
    }
    String userPath = userEnvRes.getUserEnvironmentBasePath(user.getUuid());
    String siteNode = userPath + PATH_MYSITES + PathUtils.getUserPrefix(id)
        + FILE_GROUPDEF;
    return siteNode;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.site.SiteService#deleteSite(java.lang.String)
   */
  public void deleteSite(String id) {
  }

  /**
   * {@inheritDoc}
   * 
   * @throws UnsupportedEncodingException
   * 
   * @see org.sakaiproject.kernel.api.site.SiteService#saveSite(org.sakaiproject.kernel.model.SiteBean)
   */
  public void saveSite(SiteBean site) throws SiteException,
      SiteCreationException {

    try {
      authzResolverService.setRequestGrant("Saving Site into User Environment");

      String json = beanConverter.convertToString(site);

      // check the index for a pre-existing record with the same ID
      Query query = entityManager
          .createNamedQuery(SiteIndexBean.Queries.FINDBY_ID);
      query.setParameter(SiteIndexBean.QueryParams.FINDBY_ID_ID, site.getId());
      SiteIndexBean index = null;
      try {
        index = (SiteIndexBean) query.getSingleResult();
      } catch (NoResultException e) {
        // acceptable exception
        log.info("Didn't find a site with ID=[" + site.getId()
            + "].  Creating a new site.");
      }

      // the location of the JCR node
      String fileNode = null;

      // if site exists, update it
      if (index != null) {
        fileNode = index.getRef();
      }
      // create a new node if not found
      else {
        fileNode = buildFilePath(site.getId());
      }

      InputStream in = null;
      try {
        in = new ByteArrayInputStream(json.getBytes("UTF-8"));
        Node node = jcrNodeFactoryService.setInputStream(fileNode, in,
            RestProvider.CONTENT_TYPE);

        if (index == null) {
          index = new SiteIndexBean();
        }
        index.setId(site.getId());
        index.setName(site.getName());
        index.setRef(fileNode);
        entityManager.persist(index);
        // find the first parent.
        Node n = node;
        while ( n.isNew() ) {
          n = n.getParent(); 
        }
        n.save();
        
      } catch (RepositoryException e) {
        throw new SiteCreationException(e);
      } catch (JCRNodeFactoryServiceException e) {
        throw new SiteCreationException(e);
      } catch (UnsupportedEncodingException e) {
        throw new SiteCreationException(e);
      } finally {
        try {
          in.close();
        } catch (Exception ex) {
          // nothing we can do
        }
      }
    } finally {
      authzResolverService.clearRequestGrant();
    }
  }
}
