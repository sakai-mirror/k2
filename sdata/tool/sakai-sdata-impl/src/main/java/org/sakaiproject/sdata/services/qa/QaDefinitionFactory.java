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

package org.sakaiproject.sdata.services.qa;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.Kernel;
import org.sakaiproject.announcement.api.AnnouncementService;
import org.sakaiproject.message.api.MessageService;
import org.sakaiproject.sdata.tool.api.SDataException;
import org.sakaiproject.sdata.tool.api.ServiceDefinition;
import org.sakaiproject.sdata.tool.api.ServiceDefinitionFactory;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.time.api.TimeService;
import org.sakaiproject.tool.api.SessionManager;

import java.io.IOException;

/**
 * A service definition factory for the Quick Announcements service
 * 
 * @author
 */
public class QaDefinitionFactory implements ServiceDefinitionFactory
{

	private SessionManager sessionManager;

	private SiteService siteService;

	private AnnouncementService announcementService;

	private MessageService messageService;

	private TimeService timeService;

	/**
	 * Create a the Quick Announcements Service Definition Factory
	 */
	public QaDefinitionFactory()
	{
		siteService = Kernel.siteService();
		sessionManager = Kernel.sessionManager();
		announcementService = Kernel.announcementService();
		messageService = Kernel.announcementService();
		timeService = Kernel.timeService();
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
			throw new SDataException(HttpServletResponse.SC_UNAUTHORIZED, "Not Logged In");
		}

		return new QaBean(sessionManager, messageService, timeService, siteService,
				announcementService, request, response);
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
