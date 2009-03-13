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
import com.google.inject.name.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.KernelConstants;
import org.sakaiproject.kernel.api.authz.AuthzResolverService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
import org.sakaiproject.kernel.api.rest.RestProvider;
import org.sakaiproject.kernel.api.serialization.BeanConverter;
import org.sakaiproject.kernel.api.session.SessionManagerService;
import org.sakaiproject.kernel.api.site.SiteException;
import org.sakaiproject.kernel.api.site.SiteService;
import org.sakaiproject.kernel.model.SiteBean;
import org.sakaiproject.kernel.util.IOUtils;
import org.sakaiproject.kernel.util.MapUtils;
import org.sakaiproject.kernel.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 *
 */
public class SiteServiceImpl implements SiteService {

  private static final Log LOG = LogFactory.getLog(SiteServiceImpl.class);

  private final JCRNodeFactoryService jcrNodeFactoryService;
  private final BeanConverter beanConverter;
  private final SessionManagerService sessionManagerService;

  private String defaultTemplate;

  private Map<String, String> siteTemplateMap;

  private long entropy = System.currentTimeMillis();

  private AuthzResolverService authzResolverService;

  @Inject
  public SiteServiceImpl(JCRNodeFactoryService jcrNodeFactoryService,
      BeanConverter beanConverter, SessionManagerService sessionManagerService,
      AuthzResolverService authzResolverService,
      @Named(KernelConstants.JCR_SITE_TEMPLATES) String siteTemplates,
      @Named(KernelConstants.JCR_SITE_DEFAULT_TEMPLATE) String defaultTemplate) {
    this.jcrNodeFactoryService = jcrNodeFactoryService;
    this.beanConverter = beanConverter;
    this.sessionManagerService = sessionManagerService;
    this.siteTemplateMap = MapUtils.convertToImmutableMap(siteTemplates);
    this.defaultTemplate = defaultTemplate;
    this.authzResolverService = authzResolverService;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.site.SiteService#getSite(java.lang.String)
   */
  public SiteBean getSite(String path) {
    String sitePath = buildFilePath(path);
    InputStream in = null;
    try {
      in = jcrNodeFactoryService.getInputStream(sitePath);
      String siteBody = IOUtils.readFully(in, "UTF-8");
      SiteBean bean = beanConverter.convertToObject(siteBody, SiteBean.class);
      bean.location(sitePath);
      bean.service(this);
      return bean;
    } catch (UnsupportedEncodingException e) {
      LOG.error("Failed to find site " + e.getMessage());
    } catch (IOException e) {
      LOG.error("Failed to find site " + e.getMessage());
    } catch (RepositoryException e) {
      LOG.error("Failed to find site " + e.getMessage());
    } catch (JCRNodeFactoryServiceException e) {
      LOG.debug("Failed to find site " + e.getMessage());
    } finally {
      try {
        in.close();
      } catch (Exception ex) {
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.site.SiteService#siteExists(java.lang.String)
   */
  public boolean siteExists(String path) {
    String sitePath = buildFilePath(path);
    try {
      Node n = jcrNodeFactoryService.getNode(sitePath);
      return (n != null);
    } catch (JCRNodeFactoryServiceException e) {
      return false;
    } catch (RepositoryException e) {
      return false;
    }
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
  public void save(SiteBean siteBean) throws SiteException {

    ByteArrayInputStream bais = null;
    try {

      String path = siteBean.location();
      // save the template
      String siteBeanDef = beanConverter.convertToString(siteBean);
      LOG.info("Saving Site to " + path + " as " + siteBeanDef);
      bais = new ByteArrayInputStream(siteBeanDef.getBytes("UTF-8"));
      Node siteNode = jcrNodeFactoryService.setInputStream(path, bais,
          RestProvider.CONTENT_TYPE);

      // make the private and shares spaces for the user owned by this used.
      jcrNodeFactoryService.setOwner(buildSiteFolder(path), siteBean.getOwners()[0]);

    } catch (UnsupportedEncodingException e) {
      LOG.error(e);
    } catch (JCRNodeFactoryServiceException e) {
      throw new SiteException("Failed to save site ", e);
    } catch (RepositoryException e) {
      throw new SiteException("Failed to save site ", e);
    } finally {
      try {
        bais.close();
      } catch (Exception e) {
        LOG.warn("Failed to close internal stream " + e.getMessage());
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.api.site.SiteService#createSite(java.lang.String,
   *      java.lang.String)
   */
  public SiteBean createSite(String path, String siteType) throws SiteException {
    if (siteExists(path)) {
      throw new SiteException("Site at " + path + " already exists, cant create");
    }
    String userId = sessionManagerService.getCurrentUserId();
    String siteTemplatePath = getSiteTemplate(siteType);
    String sitePath = buildFilePath(path);

    InputStream templateInputStream = null;
    try {

      // load the template
      authzResolverService.setRequestGrant("Loading Site Template");
      String template = null;
      try {
        templateInputStream = jcrNodeFactoryService.getInputStream(siteTemplatePath);
        template = IOUtils.readFully(templateInputStream, "UTF-8");
      } finally {
        authzResolverService.clearRequestGrant();
      }
      LOG.info("Loading Site Template from " + siteTemplatePath + " as " + template);
      SiteBean siteBean = beanConverter.convertToObject(template, SiteBean.class);

      // make the template this user
      siteBean.setOwners(new String[] { userId });
      siteBean.setId(generateSiteUuid(path));
      siteBean.setType(siteType);
      siteBean.location(sitePath);
      siteBean.service(this);

      return siteBean;

    } catch (RepositoryException e) {
      LOG.error(e.getMessage(), e);
    } catch (JCRNodeFactoryServiceException e) {
      LOG.error(e.getMessage(), e);
    } catch (UnsupportedEncodingException e) {
      LOG.error(e.getMessage(), e);
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    } finally {
      try {
        templateInputStream.close();
      } catch (Exception ex) {
        // not interested
      }
    }
    return null;
  }

  /**
   * Build the full path with file name to the group definition for a given site ID.
   * 
   * @param id
   * @return
   */
  private String buildFilePath(String path) {
    return buildSiteFolder(path) + "/" + FILE_GROUPDEF;
  }

  /**
   * @param path
   * @return
   */
  private String buildSiteFolder(String path) {
    return path + PATH_SITE;
  }

  /**
   * @param path
   * @return
   */
  private String generateSiteUuid(String path) {
    try {
      return StringUtils.sha1Hash(path + entropy);
    } catch (UnsupportedEncodingException e) {
      LOG.error(e);
    } catch (NoSuchAlgorithmException e) {
      LOG.error(e);
    }
    return null;
  }

  public String getSiteTemplate(String siteType) {
    if (siteType == null) {
      return defaultTemplate;
    }
    String template = siteTemplateMap.get(siteType);
    if (template == null) {
      return defaultTemplate;
    }
    return template;
  }
}
