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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.sdata.tool.api.SDataException;
import org.sakaiproject.sdata.tool.api.ServiceDefinition;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.user.api.Preferences;
import org.sakaiproject.user.api.PreferencesService;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;

/**
 * The MeBean provides a service to get hold of information about the current
 * user.
 * 
 * @author
 */
public class MeBean implements ServiceDefinition {

	private static final Log log = LogFactory.getLog(MeBean.class);

	private Session currentSession;

	private Map<String, Object> map2 = new HashMap<String, Object>();;

	private Map<String, Object> map = new HashMap<String, Object>();

	/**
	 * Create a me bean using the request and the injected services.
	 * 
	 * @param preferencesService
	 * @param userLocal
	 * 
	 * @param sessionManager
	 * @param siteService
	 * @throws SDataException
	 */
	public MeBean(HttpServletRequest request, UserLocale userLocal,
			PreferencesService preferencesService, SiteService siteService,
			SessionManager sessionManager,
			UserDirectoryService userDirectoryService,
			HttpServletResponse response, boolean loggedIn)
			throws SDataException {
		if (!loggedIn) {

			// send the locale back to the user
			map.put("userLocale", localeToMap(userLocal.getLocale(request
					.getLocale())));
			List<Map<String, Object>> localeList = new ArrayList<Map<String, Object>>();
			for (Enumeration<?> locales = request.getLocales(); locales
					.hasMoreElements();) {
				localeList.add(localeToMap((Locale) locales.nextElement()));
			}
			map2.put("items", map);
			
		} else {

			User user = null;
			
			currentSession = sessionManager.getCurrentSession();
			
			if (request.getParameter("user") == null){
			
			try {

				user = userDirectoryService.getUser(currentSession.getUserId());
			} catch (UserNotDefinedException e) {
				log.debug(e);
			}
			
			} else {
				String usr = request.getParameter("user");
				try {
					user = userDirectoryService.getUserByEid(usr);
				} catch (UserNotDefinedException e) {
					// TODO Auto-generated catch block
					throw new SDataException(HttpServletResponse.SC_NOT_FOUND,
					"User not found");
				}
			}

			// serialize user object

			if (user == null) {

				throw new SDataException(HttpServletResponse.SC_NOT_FOUND,
						"User not found");

			} else {

				try {

					Site myWorkSite = (siteService.getSite(siteService
							.getUserSiteId(currentSession.getUserId())));

					map.put("workspace", myWorkSite.getId());

					List<?> pages = (List<?>) myWorkSite.getOrderedPages();

					for (Iterator<?> ipage = pages.iterator(); ipage.hasNext();) {
						SitePage page = (SitePage) ipage.next();

						List<?> lst = page.getTools();

						for (Iterator<?> iconf = lst.iterator(); iconf
								.hasNext();) {
							ToolConfiguration conf = (ToolConfiguration) iconf
									.next();

							Tool t = conf.getTool();

							if (t != null && t.getId() != null) {
								if (t.getId().equals("sakai.membership")
										|| t.getId().equals("sakai.sites")) {
									map.put("cp", conf.getId());
								} else if (t.getId()
										.equals("sakai.preferences")
										|| t.getId().equals("sakai.preference")) {
									map.put("pref", conf.getId());
								}
							}

						}

					}

				} catch (IdUnusedException e) {
					log.error(e);
				}

				map.put("userid", user.getId());
				map.put("firstname", user.getFirstName());
				map.put("lastname", user.getLastName());
				map.put("displayId", user.getDisplayId());
				map.put("email", user.getEmail());
				try {
					map.put("createdBy", user.getCreatedBy().getDisplayName());
				} catch (NullPointerException npe) {
					map.put("createdTime", "na");
				}
				try {
					map.put("createdTime", user.getCreatedTime()
							.toStringLocalFull());
				} catch (NullPointerException npe) {
					map.put("createdTime", "na");

				}
				map.put("userEid", user.getEid());

				Map<String, Object> properties = new HashMap<String, Object>();
				ResourceProperties p = user.getProperties();
				for (Iterator<?> i = p.getPropertyNames(); i.hasNext();) {

					String pname = (String) i.next();
					List<?> l = p.getPropertyList(pname);
					if (l.size() == 1) {
						properties.put(pname, l.get(0));
					} else if (l.size() > 1) {
						properties.put(pname, l);
					}
				}
				map.put("properties", properties);

				// send the locale back to the user
				try {
					map.put("userLocale", localeToMap(userLocal.getLocale(request
						.getLocale())));
					List<Map<String, Object>> localeList = new ArrayList<Map<String, Object>>();
					for (Enumeration<?> locales = request.getLocales(); locales
						.hasMoreElements();) {
						localeList.add(localeToMap((Locale) locales.nextElement()));
					}
				} catch (Exception ex){
					//ignore
				}

				// add the preferences
				Preferences preferences = preferencesService
						.getPreferences(user.getId());
				ResourceProperties rproperties = preferences.getProperties();
				Map<String, Object> preferenceMap = new HashMap<String, Object>();
				for (Iterator<?> pi = rproperties.getPropertyNames(); pi
						.hasNext();) {
					String pkey = (String) pi.next();
					preferenceMap.put(pkey, rproperties.get(pkey));

				}
				map.put("preferences", preferenceMap);

				// map2.put("items", user);
				map2.put("items", map);

			}
		}
	}

	private Map<String, Object> localeToMap(Locale l) {
		Map<String, Object> localeMap = new HashMap<String, Object>();
		localeMap.put("country", l.getCountry());
		localeMap.put("displayCountry", l.getDisplayCountry(l));
		localeMap.put("displayLanguage", l.getDisplayLanguage(l));
		localeMap.put("displayName", l.getDisplayName(l));
		localeMap.put("displayVariant", l.getDisplayVariant(l));
		localeMap.put("ISO3Country", l.getISO3Country());
		localeMap.put("ISO3Language", l.getISO3Language());
		localeMap.put("language", l.getLanguage());
		localeMap.put("variant", l.getVariant());
		return localeMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.ServiceDefinition#getResponseMap()
	 */
	public Map<String, Object> getResponseMap() {

		return map2;
	}

}
