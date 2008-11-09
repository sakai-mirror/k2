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

package org.sakaiproject.sdata.services.cms;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.Kernel;
import org.sakaiproject.sdata.tool.api.SDataException;
import org.sakaiproject.sdata.tool.api.ServiceDefinition;
import org.sakaiproject.sdata.tool.api.ServiceDefinitionFactory;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.component.cover.ComponentManager;

/**
 * The Service definition factory for CourseManagement beans
 * 
 */
public class CourseManagementServiceDefinitionFactory implements ServiceDefinitionFactory
{
	private CourseManagementService courseManagementService = null;
	
	private SessionManager sessionManager;
	
	private UserDirectoryService userDirectoryService;

	public CourseManagementServiceDefinitionFactory()
	{
		courseManagementService = (CourseManagementService) ComponentManager.get(CourseManagementService.class);

		sessionManager = Kernel.sessionManager();
		userDirectoryService = Kernel.userDirectoryService();
	}

	public ServiceDefinition getSpec(HttpServletRequest request,
			HttpServletResponse response) throws SDataException
	{
		if (request.getRemoteUser() == null)
		{
			throw new SDataException(HttpServletResponse.SC_UNAUTHORIZED, "Not Logged In");
		}

		return new CourseManagementBean(request, response, courseManagementService, sessionManager, userDirectoryService);
	}

	public void init(Map<String, String> config)
	{
	}
}
