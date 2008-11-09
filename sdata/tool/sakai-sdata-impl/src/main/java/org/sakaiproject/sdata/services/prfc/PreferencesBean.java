/**********************************************************************************
 * $URL$
 * $Id$
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

package org.sakaiproject.sdata.services.prfc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.event.cover.NotificationService;
import org.sakaiproject.sdata.tool.api.ServiceDefinition;
import org.sakaiproject.time.cover.TimeService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.user.api.Preferences;
import org.sakaiproject.user.api.PreferencesEdit;
import org.sakaiproject.user.api.PreferencesService;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.util.ResourceLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A Preferences definition bean
 * 
 */
public class PreferencesBean implements ServiceDefinition
{
	private static final Log log = LogFactory.getLog(PreferencesBean.class);
	
	private Session currentSession;

	private Map<String, Object> map = new HashMap<String, Object>();
	
	public static final String NOTIF_OPTION = "notifOption";
	public static final String NOTIF_VALUE = "notifValue";
	
	/** Should research/collab specific preferences (no syllabus) be displayed */
	private static final String PREFS_RESEARCH = "prefs.research.collab";

	public PreferencesBean(HttpServletRequest request, HttpServletResponse response, PreferencesService ps, 
			SessionManager sessionManager, UserDirectoryService userDirectoryService)
	{
		User user = null;
		currentSession = sessionManager.getCurrentSession();

		try
		{
			user = userDirectoryService.getUser(currentSession.getUserId());
		}
		catch (UserNotDefinedException e)
		{
			log.error(e.getMessage(), e);
		}

		map.put("userid", user.getId());
		map.put("firstname", user.getFirstName());
		map.put("lastname", user.getLastName());
		map.put("displayId", user.getDisplayId());
		map.put("email", user.getEmail());

		Preferences prefs = (PreferencesEdit) ps.getPreferences(user.getId());
		ResourceProperties props = prefs.getProperties(TimeService.APPLICATION_ID);
		String timeZone = props.getProperty(TimeService.TIMEZONE_KEY);
		if(timeZone != null)
			map.put("timezone", timeZone);
		else
		{
			map.put("timezone", TimeService.getLocalTimeZone().getID());
		}
		props = prefs.getProperties(ResourceLoader.APPLICATION_ID);
		String locale = props.getProperty(ResourceLoader.LOCALE_KEY);
		if(locale != null)
			map.put("locale", locale);
		else
			map.put("locale", Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry());

		String[] timeZoneArray = TimeZone.getAvailableIDs();
		Arrays.sort(timeZoneArray);
		List timeZones = new ArrayList();
		for (int i = 0; i < timeZoneArray.length; i++)
		{
			timeZones.add(timeZoneArray[i]);
		}

		map.put("timezonelist", timeZones);
		
		Locale[] localeArray = Locale.getAvailableLocales();
		ArrayList <Map<String,String>> languageArray = new ArrayList <Map<String,String>>();
		//Arrays.sort(localeArray);
		for (int i = 0; i < localeArray.length; i++)
		{
			if (localeArray[i].getLanguage() != null && ! localeArray[i].getLanguage().equals("") && localeArray[i].getCountry() != null && ! localeArray[i].getCountry().equals("")){
				Map<String,String> map = new HashMap<String,String>();
				map.put("displayName", localeArray[i].getDisplayName());
				map.put("id", localeArray[i].getLanguage() + "_" + localeArray[i].getCountry());
				languageArray.add(map);
			}
		}
		Collections.sort(languageArray, new languageSorter());
		
		map.put("languages", languageArray);
		
		// retrieve the user's notification preferences
		// AnnouncementService.APPLICATION_ID = "sakai:announcement" - hard-coded for simplicity
		// of dependencies for now
		String anncNotifPref = getNotificationPreference("sakai:announcement", "3", prefs);
		map.put("anncNotifPref", anncNotifPref);
		// set up the preference possibilities
		map.put("anncNotifOptions", getAnncNotifOptions());
		
		// MailArchiveService.APPLICATION_ID = "sakai:mailarchive" - hard-coded for simplicity
		// of dependencies for now
		String mailArchiveNotifPref = getNotificationPreference("sakai:mailarchive", "3", prefs);
		map.put("mailArchiveNotifPref", mailArchiveNotifPref);
		// set up the preference possibilities
		map.put("mailArchiveNotifOptions", getMailArchiveNotifOptions());

		String resourcesNotifPref = getNotificationPreference(ContentHostingService.APPLICATION_ID, "3", prefs);
		map.put("resourcesNotifPref", resourcesNotifPref);
		// set up the preference possibilities
		map.put("resourcesNotifOptions", getResourceNotifOptions());

		// we need to check sakai.properties to see if syllabus should be
		// included
		boolean displaySyllabus = !ServerConfigurationService.getBoolean(PREFS_RESEARCH, false);
		
		if (displaySyllabus) {
			// SyllabusService.APPLICATION_ID = "sakai:syllabus" - hard-coded for simplicity
			// of dependencies for now
			String syllabusNotifPref = getNotificationPreference("sakai:syllabus", "3", prefs);
			map.put("syllabusNotifPref", syllabusNotifPref);	
			// set up the preference possibilities;
			map.put("syllabusNotifOptions", getSyllabusNotifOptions());	
		}
	}
	
	private class languageSorter implements Comparator {

		public int compare(Object o1, Object o2) {
			// TODO Auto-generated method stub
			Map <String,String> map1 = (Map <String,String>)o1;
			Map <String,String> map2 = (Map <String,String>)o2;
			return map1.get("displayName").compareTo(map2.get("displayName"));
		}
		
	}

	public Map<String, Object> getResponseMap() 
	{
		return map;
	}
	
	private String getNotificationPreference(String type, String defaultValue, Preferences prefs)
	{
		String notifPref = defaultValue;
		
		ResourceProperties notifProps = prefs.getProperties(NotificationService.PREFS_TYPE + type);
		String selNotifProp = notifProps.getProperty(new Integer(NotificationService.NOTI_OPTIONAL).toString());
		
		if (selNotifProp != null && selNotifProp.trim().length() > 0) {
			notifPref = selNotifProp;
		}
		
		return notifPref;
	}
	
	private List<Map<String, String>> getAnncNotifOptions() {
		// TODO - internationalize this
		List<Map<String, String>> anncNotifOptions = new ArrayList<Map<String,String>>();
		Map<String,String> anncOption1 = new HashMap<String,String>();
		Map<String,String> anncOption2 = new HashMap<String,String>();
		Map<String,String> anncOption3 = new HashMap<String,String>();
		
		anncOption1.put(NOTIF_OPTION, "Send me each notification separately");
		anncOption1.put(NOTIF_VALUE, "3");
		anncNotifOptions.add(anncOption1);
		
		anncOption2.put(NOTIF_OPTION, "Send me one email per day summarizing all low priority announcements");
		anncOption2.put(NOTIF_VALUE, "2");
		anncNotifOptions.add(anncOption2);
		
		anncOption3.put(NOTIF_OPTION, "Do not send me low priority announcements");
		anncOption3.put(NOTIF_VALUE, "1");
		anncNotifOptions.add(anncOption3);
		
		return anncNotifOptions;
	}
	
	private List<Map<String, String>> getResourceNotifOptions() {
		// TODO - internationalize this
		List<Map<String, String>> resourcesNotifOptions = new ArrayList<Map<String,String>>();
		Map<String,String> resourcesOption1 = new HashMap<String,String>();
		Map<String,String> resourcesOption2 = new HashMap<String,String>();
		Map<String,String> resourcesOption3 = new HashMap<String,String>();
		
		resourcesOption1.put(NOTIF_OPTION, "Send me each resource separately");
		resourcesOption1.put(NOTIF_VALUE, "3");
		resourcesNotifOptions.add(resourcesOption1);
		
		resourcesOption2.put(NOTIF_OPTION, "Send me one email per day summarizing all low priority resource notifications");
		resourcesOption2.put(NOTIF_VALUE, "2");
		resourcesNotifOptions.add(resourcesOption2);
		
		resourcesOption3.put(NOTIF_OPTION, "Do not send me low priority resource notifications");
		resourcesOption3.put(NOTIF_VALUE, "1");
		resourcesNotifOptions.add(resourcesOption3);
		
		return resourcesNotifOptions;
	}
	
	private List<Map<String, String>> getMailArchiveNotifOptions() {
		// TODO - internationalize this
		List<Map<String, String>> mailNotifOptions = new ArrayList<Map<String,String>>();
		Map<String,String> mailOption1 = new HashMap<String,String>();
		Map<String,String> mailOption2 = new HashMap<String,String>();
		Map<String,String> mailOption3 = new HashMap<String,String>();
		
		mailOption1.put(NOTIF_OPTION, "Send me each mail sent to site separately");
		mailOption1.put(NOTIF_VALUE, "3");
		mailNotifOptions.add(mailOption1);
		
		mailOption2.put(NOTIF_OPTION, "Send me one email per day summarizing all emails");
		mailOption2.put(NOTIF_VALUE, "2");
		mailNotifOptions.add(mailOption2);
		
		mailOption3.put(NOTIF_OPTION, "Do not send me emails sent to the site");
		mailOption3.put(NOTIF_VALUE, "1");
		mailNotifOptions.add(mailOption3);
		
		return mailNotifOptions;
	}
	
	private List<Map<String, String>> getSyllabusNotifOptions() {
		// TODO - internationalize this
		List<Map<String, String>> syllNotifOptions = new ArrayList<Map<String,String>>();
		Map<String,String> syllOption1 = new HashMap<String,String>();
		Map<String,String> syllOption2 = new HashMap<String,String>();
		Map<String,String> syllOption3 = new HashMap<String,String>();
		
		syllOption1.put(NOTIF_OPTION, "Send me each notification separately");
		syllOption1.put(NOTIF_VALUE, "3");
		syllNotifOptions.add(syllOption1);
		
		syllOption2.put(NOTIF_OPTION, "Send me one email per day summarizing all notifications");
		syllOption2.put(NOTIF_VALUE, "2");
		syllNotifOptions.add(syllOption2);
		
		syllOption3.put(NOTIF_OPTION, "Do not send me low priority Syllabus items");
		syllOption3.put(NOTIF_VALUE, "1");
		syllNotifOptions.add(syllOption3);
		
		return syllNotifOptions;
	}

}
