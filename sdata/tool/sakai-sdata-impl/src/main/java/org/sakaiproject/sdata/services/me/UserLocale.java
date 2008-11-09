package org.sakaiproject.sdata.services.me;

import java.util.Locale;

import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.Preferences;
import org.sakaiproject.user.api.PreferencesService;

public class UserLocale {

	protected String LOCALE_SESSION_KEY = "sakai.locale.";

	/**
	 * The type string for this "application": should not change over time as it
	 * may be stored in various parts of persistent entities.
	 */
	String APPLICATION_ID = "sakai:resourceloader";

	/** Preferences key for user's regional language locale */
	String LOCALE_KEY = "locale";

	private SessionManager sessionManager;

	private PreferencesService preferencesService;

	public UserLocale(SessionManager sessionManager,
			PreferencesService preferencesService) {
		this.sessionManager = sessionManager;
		this.preferencesService = preferencesService;
	}

	/**
	 * * Return user's prefered locale * First: return locale from Sakai user
	 * preferences, if available * Second: return locale from user session, if
	 * available * Last: return system default locale
	 * 
	 * @param locale * *
	 * @return user's Locale object
	 */
	public Locale getLocale(Locale browserLocale) {
		Locale loc = null;
		String userId = sessionManager.getCurrentSessionUserId();
		try {
			loc = (Locale) sessionManager.getCurrentSession().getAttribute(
					LOCALE_SESSION_KEY + userId);
			// no session locale, set one
			if (loc == null) {
				loc = getPreferredLocale(userId);

				loc = getContextLocale(browserLocale,userId);
			}
		} catch (NullPointerException e) {
			// The locale is not in the session.
			// Look for it and set in session
			loc = getContextLocale(browserLocale,userId);
		}

		return loc;
	}

	/***************************************************************************
	 * * Get user's preferred locale
	 **************************************************************************/
	private Locale getPreferredLocale(String userId) {
		Locale loc = null;
		Preferences prefs = preferencesService.getPreferences(userId);
		ResourceProperties locProps = prefs.getProperties(APPLICATION_ID);
		String localeString = locProps.getProperty(LOCALE_KEY);

		if (localeString != null) {
			String[] locValues = localeString.split("_");
			if (locValues.length > 1)
				loc = new Locale(locValues[0], locValues[1]); // language,
																// country
			else if (locValues.length == 1)
				loc = new Locale(locValues[0]); // just language
		}

		return loc;
	}

	/**
	 * * Sets user's prefered locale in context * First: sets locale from Sakai
	 * user preferences, if available * Second: sets locale from user session,
	 * if available * Last: sets system default locale * *
	 * 
	 * @return user's Locale object
	 */
	public Locale getContextLocale(Locale browserLocale, String userId) {
		// First : find locale from Sakai user preferences, if available
		Locale loc = null;
		try {
			if (userId != null) {
				loc = getPreferredLocale(userId);
				if (loc != null) {
					// Write the sakai locale in the session
					try {
						sessionManager.getCurrentSession().setAttribute(
								LOCALE_SESSION_KEY + userId, loc);
					} catch (Exception e) {
					} // Ignore and continue
				}
			}
		} catch (Exception e) {
		} // ignore and continue

		// Second: use the browser locale
		if (loc == null) {
			loc = browserLocale;
		}

		// Last: find system default locale
		if (loc == null) {
			// fallback to default.
			loc = Locale.getDefault();
		} else if (!Locale.getDefault().getLanguage().equals("en")
				&& loc.getLanguage().equals("en")) {
			// Tweak for English: en is default locale. It has no suffix in
			// filename.
			loc = new Locale("");
		}

		return loc;
	}

}
