/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/trunk/sdata/sdata-tool/impl/src/java/org/sakaiproject/sdata/tool/JCRDumper.java $
 * $Id: JCRDumper.java 45207 2008-02-01 19:01:06Z ian@caret.cam.ac.uk $
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

package org.sakaiproject.sdata.services.me;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.Kernel;
import org.sakaiproject.sdata.tool.api.SDataException;
import org.sakaiproject.sdata.tool.api.ServiceDefinition;
import org.sakaiproject.sdata.tool.api.ServiceDefinitionFactory;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.PreferencesService;
import org.sakaiproject.user.api.UserDirectoryService;

import java.io.IOException;

/**
 * A Factory to generate service definition beans for Me Requests
 * 
 * @author
 */
public class MeServiceDefinitionFactory implements ServiceDefinitionFactory
{

	private SessionManager sessionManager;

	private SiteService siteService;

	private UserDirectoryService userDirectoryService;

	private UserLocale userLocal;

	private PreferencesService preferencesService;

	/**
	 * Create a new MeServiceDefintionFactory
	 */
	public MeServiceDefinitionFactory()
	{
		// siteService = (SiteService)
		// componentManager.get(SiteService.class.getName());
		sessionManager = Kernel.sessionManager();
		userDirectoryService = Kernel.userDirectoryService();
		siteService = Kernel.siteService();
		preferencesService = (PreferencesService) Kernel.componentManager().get(PreferencesService.class.getName());
		
		userLocal = new UserLocale(sessionManager,preferencesService);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.ServiceDefinitionFactory#getSpec(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public ServiceDefinition getSpec(HttpServletRequest request,
			HttpServletResponse response) throws SDataException
	{
		if (request.getRemoteUser() == null){
			return new MeBean(request,userLocal, preferencesService, siteService, sessionManager, userDirectoryService, response, false);
		} else {
			return new MeBean(request,userLocal, preferencesService, siteService, sessionManager, userDirectoryService, response, true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.ServiceDefinitionFactory#init(java.util.Map)
	 */
	public void init(Map<String, String> config)
	{

	}

}
