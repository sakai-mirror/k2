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
package org.sakaiproject.kernel.social;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
import org.sakaiproject.kernel.api.serialization.BeanConverter;
import org.sakaiproject.kernel.api.session.SessionManagerService;
import org.sakaiproject.kernel.api.social.FriendsResolverService;
import org.sakaiproject.kernel.api.user.ProfileResolverService;
import org.sakaiproject.kernel.api.user.UserProfile;
import org.sakaiproject.kernel.jcr.api.JcrContentListener;
import org.sakaiproject.kernel.model.FriendBean;
import org.sakaiproject.kernel.model.FriendsBean;
import org.sakaiproject.kernel.model.FriendsIndexBean;
import org.sakaiproject.kernel.user.jcr.JcrUserFactoryService;
import org.sakaiproject.kernel.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

/**
 *
 */
public class FriendsListener implements JcrContentListener {

  private static final Log LOG = LogFactory.getLog(FriendsListener.class);
  private final BeanConverter beanConverter;
  private final JCRNodeFactoryService jcrNodeFactoryService;
  private final EntityManager entityManager;
  private String sharedPrivatePathBase;
  private ProfileResolverService profileResolverService;

  /**
   * Constructor with all required dependencies.
   * 
   * @param jcrNodeFactoryService
   * @param userEnvironmentBase
   * @param beanConverter
   * @param sessionManagerService
   * @param userEnvironmentResolverService
   * @param entityManager
   */
  @Inject
  public FriendsListener(

      JCRNodeFactoryService jcrNodeFactoryService,
      @Named(JcrUserFactoryService.PRIVATE_PATH_BASE) String sharedPrivatePathBase,
      @Named(BeanConverter.REPOSITORY_BEANCONVETER) BeanConverter beanConverter,
      SessionManagerService sessionManagerService,
      EntityManager entityManager) {
    this.jcrNodeFactoryService = jcrNodeFactoryService;
    this.beanConverter = beanConverter;
    this.entityManager = entityManager;
    this.sharedPrivatePathBase = sharedPrivatePathBase;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sakaiproject.kernel.jcr.api.JcrContentListener#onEvent(int,
   *      java.lang.String, java.lang.String, java.lang.String)
   */
  public void onEvent(int type, String userID, String filePath, String fileName) {
    if (filePath.startsWith(sharedPrivatePathBase)) {
      if (fileName.equals(FriendsResolverService.FRIENDS_FILE)) {
        String friendsBody = null;
        InputStream in = null;
        try {
          in = jcrNodeFactoryService.getInputStream(filePath);
          friendsBody = IOUtils.readFully(in, "UTF-8");
          if (friendsBody != null && friendsBody.length() > 0) {
            System.err.println("Converting " + friendsBody + " to Bean");
            FriendsBean friendsBean = beanConverter.convertToObject(
                friendsBody, FriendsBean.class);

            Query query = entityManager
                .createNamedQuery(FriendsIndexBean.FINDBY_UUID);
            query.setParameter(FriendsIndexBean.PARAM_UUID, friendsBean
                .getUuid());
            List<?> friendsIndexBeanList = query.getResultList();
            List<FriendsIndexBean> toAdd = Lists.newArrayList();
            List<FriendsIndexBean> toRemove = Lists.newArrayList();

            for (Object o : friendsIndexBeanList) {
              FriendsIndexBean friendIndexBean = (FriendsIndexBean) o;
              if (!friendsBean.hasFriend(friendIndexBean.getFriendUuid())) {
                toRemove.add(friendIndexBean);
              }
            }

            for (FriendBean friendBean : friendsBean.getFriends()) {
              boolean found = false;
              String newFriendUuid = friendBean.getFriendUuid();
              for (Object o : friendsIndexBeanList) {
                FriendsIndexBean friendIndexBean = (FriendsIndexBean) o;
                String friendUuid = friendIndexBean.getFriendUuid();
                if (newFriendUuid.equals(friendUuid)) {
                  found = true;
                  break;
                }
              }
              if (!found) {
                UserProfile userProfile = profileResolverService
                    .resolve(newFriendUuid);
                Map<String, Object> properties = userProfile.getProperties();
                toAdd.add(new FriendsIndexBean(friendBean.getPersonUuid(),
                    newFriendUuid, String.valueOf(properties.get("firstName")),
                    String.valueOf(properties.get("lastName"))));
              }
            }


            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            for (FriendsIndexBean gm : toRemove) {
              entityManager.remove(gm);
            }
            for (FriendsIndexBean gm : toAdd) {
              entityManager.persist(gm);
            }
            transaction.commit();
          }

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
        } finally {
          try {
            in.close();
          } catch (Exception ex) {
          }
        }
      }
    }
  }
}