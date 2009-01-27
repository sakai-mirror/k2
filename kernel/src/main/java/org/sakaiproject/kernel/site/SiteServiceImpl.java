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
package org.sakaiproject.kernel.site;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
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
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 *
 */
public class SiteServiceImpl implements SiteService {

  private final EntityManager entityManager;
  private final JCRNodeFactoryService jcrNodeFactoryService;
  private final BeanConverter beanConverter;
  private final UserEnvironmentResolverService userEnvRes;
  private final SessionManagerService sessMgr;

  @Inject
  public SiteServiceImpl(
      EntityManager entityManager,
      JCRNodeFactoryService jcrNodeFactoryService,
      @Named(BeanConverter.REPOSITORY_BEANCONVETER) BeanConverter beanConverter,
      UserEnvironmentResolverService userEnvRes, SessionManagerService sessMgr) {
    this.entityManager = entityManager;
    this.jcrNodeFactoryService = jcrNodeFactoryService;
    this.beanConverter = beanConverter;
    this.userEnvRes = userEnvRes;
    this.sessMgr = sessMgr;
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
      new SiteException(e.getMessage(), e);
    } catch (IOException e) {
      new SiteException(e.getMessage(), e);
    } catch (RepositoryException e) {
      new SiteException(e.getMessage(), e);
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
   * @todo Refactor to use synchronous event call which needs to be created.
   */
  public void saveSite(SiteBean site) throws SiteException,
      SiteCreationException {
    EntityTransaction trans = entityManager.getTransaction();

    String json = beanConverter.convertToString(site);
    String fileNode = buildFilePath(site.getId());
    InputStream in = null;
    try {
      
      in = new ByteArrayInputStream(json.getBytes("UTF-8"));      
      Node node = jcrNodeFactoryService.setInputStream(fileNode, in);
      
      
      SiteIndexBean bean = new SiteIndexBean();
      bean.setId(site.getId());
      bean.setName(site.getName());
      bean.setRef(fileNode);
      if (!trans.isActive()) {
        trans.begin();
      }
      entityManager.persist(bean);
      trans.commit();
      node.save();

    } catch (RepositoryException e) {
      if (trans.isActive()) {
        trans.rollback();
      }
      throw new SiteCreationException(e);
    } catch (JCRNodeFactoryServiceException e) {
      if (trans.isActive()) {
        trans.rollback();
      }
      throw new SiteCreationException(e);
    } catch (UnsupportedEncodingException e) {
      if (trans.isActive()) {
        trans.rollback();
      }
      throw new SiteCreationException(e);
    } finally {
      try {
        in.close();
      } catch (Exception ex) {
      }
    }
  }
}
