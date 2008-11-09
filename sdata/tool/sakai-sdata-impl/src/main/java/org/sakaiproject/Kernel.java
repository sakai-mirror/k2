/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2007 The Sakai Foundation.
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

package org.sakaiproject;

import org.sakaiproject.announcement.api.AnnouncementService;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.component.api.ComponentManager;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.event.api.UsageSessionService;
import org.sakaiproject.jcr.api.JCRService;
import org.sakaiproject.jcr.support.api.JCRNodeFactoryService;
import org.sakaiproject.message.api.MessageService;
import org.sakaiproject.search.api.SearchService;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.thread_local.api.ThreadLocalManager;
import org.sakaiproject.time.api.TimeService;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.UserDirectoryService;

public class Kernel
{

	private static ContentHostingService contentHostingService;

	private static SecurityService securityService;

	private static AuthzGroupService authzGroupService;

	private static JCRNodeFactoryService jcrNodeFactoryService;

	private static ComponentManager componentManager;

	private static SessionManager sessionManager;

	private static UserDirectoryService userDirectoryService;

	private static SiteService siteService;

	private static SearchService searchService;

	private static TimeService timeService;

	private static MessageService messageService;

	private static AnnouncementService announcementService;

	private static SqlService sqlService;

	private static UsageSessionService usageSessionService;

	private static EventTrackingService eventTrackingService;

	private static EntityManager entityManager;

	private static JCRService jcrService;

	private static ThreadLocalManager threadLocalManager;
	
	private static ServerConfigurationService serverConfigurationService;

	public static ComponentManager componentManager()
	{
		if (componentManager == null)
		{
			componentManager = org.sakaiproject.component.cover.ComponentManager
					.getInstance();
		}
		return componentManager;
	}

	public static ContentHostingService contentHostingService()
	{
		if (contentHostingService == null)
		{
			contentHostingService = (ContentHostingService) componentManager().get(
					ContentHostingService.class.getName());
		}
		return contentHostingService;
	}

	public static SecurityService securityService()
	{
		if (securityService == null)
		{
			securityService = (SecurityService) componentManager().get(
					SecurityService.class.getName());
		}
		return securityService;
	}

	public static AuthzGroupService authzGroupService()
	{
		if (authzGroupService == null)
		{
			authzGroupService = (AuthzGroupService) componentManager().get(
					AuthzGroupService.class.getName());
		}
		return authzGroupService;

	}

	public static JCRNodeFactoryService jcrNodeFactoryService()
	{
		if (jcrNodeFactoryService == null)
		{
			jcrNodeFactoryService = (JCRNodeFactoryService) componentManager().get(
					JCRNodeFactoryService.class.getName());
		}
		return jcrNodeFactoryService;
	}

	public static SessionManager sessionManager()
	{
		if (sessionManager == null)
		{
			sessionManager = (SessionManager) componentManager().get(
					SessionManager.class.getName());
		}
		return sessionManager;
	}

	public static UserDirectoryService userDirectoryService()
	{
		if (userDirectoryService == null)
		{
			userDirectoryService = (UserDirectoryService) componentManager().get(
					UserDirectoryService.class.getName());
		}
		return userDirectoryService;
	}

	public static SiteService siteService()
	{
		if (siteService == null)
		{
			siteService = (SiteService) componentManager().get(
					SiteService.class.getName());
		}
		return siteService;
	}

	public static SearchService searchService()
	{
		if (searchService == null)
		{
			searchService = (SearchService) componentManager().get(
					SearchService.class.getName());
		}
		return searchService;
	}

	public static AnnouncementService announcementService()
	{
		if (announcementService == null)
		{
			announcementService = (AnnouncementService) componentManager().get(
					AnnouncementService.class.getName());
		}
		return announcementService;
	}


	public static TimeService timeService()
	{
		if (timeService == null)
		{
			timeService = (TimeService) componentManager().get(
					TimeService.class.getName());
		}
		return timeService;
	}

	public static SqlService sqlService()
	{
		if (sqlService == null)
		{
			sqlService = (SqlService) componentManager().get(SqlService.class.getName());
		}
		return sqlService;
	}

	public static UsageSessionService usageSessionService()
	{
		if (usageSessionService == null)
		{
			usageSessionService = (UsageSessionService) componentManager().get(
					UsageSessionService.class.getName());
		}
		return usageSessionService;
	}

	public static EventTrackingService eventTrackingService()
	{
		if (eventTrackingService == null)
		{
			eventTrackingService = (EventTrackingService) componentManager().get(
					EventTrackingService.class.getName());
		}
		return eventTrackingService;
	}

	public static EntityManager entityManager()
	{
		if (entityManager == null)
		{
			entityManager = (EntityManager) componentManager().get(
					EntityManager.class.getName());
		}
		return entityManager;
	}

	public static JCRService jcrService()
	{
		if (jcrService == null)
		{
			jcrService = (JCRService) componentManager().get(JCRService.class.getName());
		}
		return jcrService;
	}

	public static ThreadLocalManager threadLocalManager()
	{
		if (threadLocalManager == null)
		{
			threadLocalManager = (ThreadLocalManager) componentManager().get(
					ThreadLocalManager.class.getName());
		}
		return threadLocalManager;
	}
	public static ServerConfigurationService serverConfigurationService()
	{
		if (serverConfigurationService == null)
		{
			serverConfigurationService = (ServerConfigurationService) componentManager().get(
					ServerConfigurationService.class.getName());
		}
		return serverConfigurationService;
	}

	public static void setComponentManager(UnitTestComponentManager mockComponentManager)
	{
		componentManager = mockComponentManager;
	}

}
