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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.announcement.api.AnnouncementMessage;
import org.sakaiproject.announcement.api.AnnouncementMessageEdit;
import org.sakaiproject.announcement.api.AnnouncementService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.message.api.MessageEdit;
import org.sakaiproject.message.api.MessageService;
import org.sakaiproject.sdata.tool.api.ServiceDefinition;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.time.api.TimeService;
import org.sakaiproject.tool.api.SessionManager;

/**
 * The Quick Announcements Bean
 * 
 * @author
 */
public class QaBean implements ServiceDefinition
{
	private Map<String, Object> map2 = new HashMap<String, Object>();;

	/**
	 * Create a Quick announcements bean injecting the necessary services
	 * 
	 * @param sessionManager
	 * @param messageservice
	 * @param timeService
	 * @param siteService
	 * @param announcementService
	 * @param request
	 * @param response
	 */
	public QaBean(SessionManager sessionManager, MessageService messageservice,
			TimeService timeService, SiteService siteService,
			AnnouncementService announcementService, HttpServletRequest request,
			HttpServletResponse response)
	{

		if (request.getMethod().toLowerCase().equals("get")){
			
			String siteId = request.getParameter("siteid");
			
			try {
				
				ArrayList <Object> result = new ArrayList<Object>();
				List <AnnouncementMessage> arl = announcementService.getAnnouncementChannel("/announcement/channel/" + siteId + "/main")
					.getMessages(null, true);
				for (AnnouncementMessage msg : arl){
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("id", msg.getId());
					map.put("url", msg.getUrl());
					map.put("title", msg.getAnnouncementHeader().getSubject());
					map.put("body", msg.getBody());
					map.put("author", msg.getAnnouncementHeader().getFrom().getDisplayName());
					SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
					map.put("date", format.format(new Date(msg.getHeader().getDate().getTime())));
					result.add(map);
				}
				map2.put("items", result);
				
			} catch (PermissionException e) {
				map2.put("status", "failed");
				e.printStackTrace();
			} catch (IdUnusedException e) {
				map2.put("status", "failed");
				e.printStackTrace();
			}
			
			
			
		} else {
		
			try
			{
	
				String siteId = request.getParameter("siteid");
				String subject = request.getParameter("subject");
				String body = request.getParameter("body");
				String action = request.getParameter("action");
				
				if (action != null && action.equals("update")){
					
					String anncid = request.getParameter("action");
					AnnouncementMessageEdit edit = announcementService.getAnnouncementChannel(
							"/announcement/channel/" + siteId + "/main").editAnnouncementMessage(anncid);
					edit.setBody(body);
					edit.getAnnouncementHeaderEdit().setSubject(subject);
					
					map2.put("status", "success");
					
				} else {
				
					AnnouncementMessage edit = announcementService.getAnnouncementChannel(
							"/announcement/channel/" + siteId + "/main").addAnnouncementMessage(
							subject, false, null, body);
					// announcementService.getAnnouncementChannel("/announcement/channel/"
					// + siteId + "/main").commitMessage(edit);
		
					map2.put("status", "success");
					
				}
	
			}
			catch (Exception ex)
			{
	
				map2.put("status", "failed");
	
			}
		
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.ServiceDefinition#getResponseMap()
	 */
	public Map<String, Object> getResponseMap()
	{

		return map2;
	}

}
