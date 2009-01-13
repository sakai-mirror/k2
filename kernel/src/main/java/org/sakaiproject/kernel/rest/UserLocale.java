/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
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
package org.sakaiproject.kernel.rest;

import com.google.inject.Inject;

import org.sakaiproject.kernel.api.session.Session;
import org.sakaiproject.kernel.api.user.User;
import org.sakaiproject.kernel.api.userenv.UserEnvironment;
import org.sakaiproject.kernel.api.userenv.UserEnvironmentResolverService;
import org.sakaiproject.kernel.util.StringUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UserLocale {

  protected String LOCALE_SESSION_KEY = "sakai.locale.";

  /** Preferences key for user's regional language locale */
  String LOCALE_KEY = "locale";

  private UserEnvironmentResolverService userEnvironmentResolverService;

  /**
   * 
   */
  @Inject
  public UserLocale(
      UserEnvironmentResolverService userEnvironmentResolverService) {
    this.userEnvironmentResolverService = userEnvironmentResolverService;
  }

  /**
   * * Return user's prefered locale * First: return locale from Sakai user
   * preferences, if available * Second: return locale from user session, if
   * available * Last: return system default locale
   * 
   * @param locale * *
   * @return user's Locale object
   */
  public Locale getLocale(Locale browserLocale, Session session) {
    Locale loc = null;

    User user = session.getUser();
    UserEnvironment userEnvironment = null;
    if (user != null && user.getUuid() != null) {
      userEnvironment = userEnvironmentResolverService.resolve(user);
    }
    String localeKey = (String) session.getAttribute(LOCALE_SESSION_KEY);
    if (userEnvironment != null && localeKey == null) {
      localeKey = userEnvironment.getLocale();
    }
    String[] locValues = StringUtils.split(localeKey, '_');
    if (locValues.length > 1) {
      loc = new Locale(locValues[0], locValues[1]);
    } else if (locValues.length == 1) {
      loc = new Locale(locValues[0]);
    } else if (browserLocale != null) {
      loc = browserLocale;
    } else {
      loc = Locale.getDefault();
    }
    return loc;
  }

  /**
   * @param locale
   * @return
   */
  public Map<String, Object> localeToMap(Locale l) {
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

}
