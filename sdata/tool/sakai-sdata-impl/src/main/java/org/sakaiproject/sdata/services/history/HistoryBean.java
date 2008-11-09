/**********************************************************************************
 * $URL:  $
 * $Id:  $
 ***********************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.sdata.services.history;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.Kernel;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.jcr.api.JCRConstants;
import org.sakaiproject.jcr.support.api.JCRNodeFactoryService;
import org.sakaiproject.jcr.support.api.JCRNodeFactoryServiceException;
import org.sakaiproject.sdata.tool.api.ServiceDefinition;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.GroupProvider;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.time.cover.TimeService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.util.ResourceLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.component.cover.ComponentManager;

/**
 * A CourseManagement definition bean
 * 
 */
public class HistoryBean implements ServiceDefinition {
	private static final Log log = LogFactory.getLog(HistoryBean.class);

	private Session currentSession;

	private Map<String, Object> map2 = new HashMap<String, Object>();

	private AuthzGroupService authzGroupService = (AuthzGroupService) ComponentManager
			.get(AuthzGroupService.class);

	private JCRNodeFactoryService jcrNodeFactory = Kernel
			.jcrNodeFactoryService();

	public HistoryBean(HttpServletRequest request,
			HttpServletResponse response, SessionManager sessionManager,
			UserDirectoryService userDirectoryService) {
		String toopen = request.getParameter("file");
		String showcontent = request.getParameter("revision");

		if (showcontent.equalsIgnoreCase("list")) {

			Node node = null;
			try {

				node = jcrNodeFactory.getNode("/sakai/sdata/" + toopen);

				if (node == null) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
				} else {

					Node resource = node.getNode(JCRConstants.JCR_CONTENT);
					
					ArrayList<Map<String, String>> arl = new ArrayList<Map<String, String>>();

					VersionHistory history = resource.getVersionHistory();
					VersionIterator it = history.getAllVersions();
					while (it.hasNext()) {
						Version version = (Version) it.next();
						
						Property author = version
								.getProperty(JCRConstants.JCR_CREATED);
						Property content = version
								.getProperty(JCRConstants.JCR_DATA);
						Property date = version
								.getProperty(JCRConstants.JCR_CREATED);
						Map<String, String> map = new HashMap<String, String>();
						map.put("author", author.getString());
						map.put("content", content.getString());
						map.put("date", date.getString());
						arl.add(map);
					}

					map2.put("items", arl);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {

		}

	}

	public Map<String, Object> getResponseMap() {
		return map2;
	}

}
