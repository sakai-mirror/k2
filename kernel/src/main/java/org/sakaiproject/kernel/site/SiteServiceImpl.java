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
import java.io.UnsupportedEncodingException;

import javax.jcr.RepositoryException;
import javax.persistence.EntityManager;
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
      String json = beanConverter.convertToString(site);
      String fileNode = buildFilePath(site.getId());
      try {
        jcrNodeFactoryService.setInputStream(fileNode,
            new ByteArrayInputStream(json.getBytes()));
      } catch (RepositoryException e1) {
        throw new SiteCreationException(e1.getMessage(), e1);
      } catch (JCRNodeFactoryServiceException e2) {
        throw new SiteCreationException(e2.getMessage(), e2);
      }
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
    try {
      Query query = entityManager
          .createNamedQuery(SiteIndexBean.Queries.FINDBY_ID);
      query.setParameter(SiteIndexBean.QueryParams.FINDBY_ID_ID, id);
      SiteIndexBean index = (SiteIndexBean) query.getSingleResult();
      String fileNode = buildFilePath(index.getId());
      String siteBody = IOUtils.readFully(jcrNodeFactoryService
          .getInputStream(fileNode), "UTF-8");
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
    String userPath = userEnvRes.getUserEnvironmentBasePath(user.getUuid());
    String siteNode = userPath + PATH_MYSITES + PathUtils.getUserPrefix(id)
        + FILE_GROUPDEF;
    return siteNode;
  }
}
