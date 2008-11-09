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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.entity.api.ResourceProperties;
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
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;

import org.sakaiproject.component.cover.ComponentManager;

/**
 * A CourseManagement definition bean
 * 
 */
public class CourseManagementBean implements ServiceDefinition
{
	private static final Log log = LogFactory.getLog(CourseManagementBean.class);
	
	private Session currentSession;

	private Map<String, Object> map2 = new HashMap<String, Object>();
	
	
	private GroupProvider groupProvider = (GroupProvider) ComponentManager.get(GroupProvider.class);
	
	private AuthzGroupService authzGroupService = (AuthzGroupService) ComponentManager.get(AuthzGroupService.class);
	
	@SuppressWarnings("unchecked")
	private List<Map> myMappedTerms = new ArrayList<Map>();
	/**
	 * get myMappedTerms
	 * 
	 * @return Return a list of mapped terms
	 */
	@SuppressWarnings("unchecked")
	public List<Map> getMyMappedTerms()
	{
		return myMappedTerms;
	}
	
	@SuppressWarnings("unchecked")
	private List<Map> myMappedCourses = new ArrayList<Map>();
	/**
	 * get myMappedCourses
	 * 
	 * @return Return a list of mapped courses
	 */
	@SuppressWarnings("unchecked")
	public List<Map> getMyMappedCourses()
	{
		return myMappedCourses;
	}

	public CourseManagementBean(HttpServletRequest request, HttpServletResponse response, 
			CourseManagementService cms, SessionManager sessionManager, UserDirectoryService userDirectoryService)
	{
		List<AcademicSession> terms = cms.getCurrentAcademicSessions();
		for(AcademicSession term:terms)
		{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("termEid", term.getEid());
			map.put("termTitle", term.getTitle());
			getMyMappedTerms().add(map);
		}
		map2.put("currentAcadmemicSessions", getMyMappedTerms());
		
		if (request.getMethod().toLowerCase().equals("post"))
		{
			String action = request.getParameter("action");
			if (action!= null && action.equals("updateRoster"))
			{
				String termEid = request.getParameter("termEid");
				
				// 1st tier: Course object
				String currentUserId = sessionManager.getCurrentSessionUserId();
				List<CourseObject> courses = prepareCourseAndSectionListing(currentUserId, termEid, cms);
				for(CourseObject course:courses)
				{
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("courseEid", course.getEid());
					map.put("courseTitle", course.getTitle());
					
					// 2nd tier: CourseOffering object
					List<Map> myMappedCourseOfferings = new ArrayList<Map>();
					List<CourseOfferingObject> courseOfferings = course.getCourseOfferingObjects();
					for(CourseOfferingObject courseOffering:courseOfferings)
					{
						Map<String, Object> coMap = new HashMap<String, Object>();
						coMap.put("courseOfferingEid", courseOffering.getEid());
						coMap.put("courseOfferingTitle", courseOffering.getTitle());
						
						// 3rd tier: Section object
						List<Map> myMappedSections = new ArrayList<Map>();
						List<SectionObject> sections = courseOffering.getSections();
						for(SectionObject section:sections)
						{
							Map<String, Object> sMap = new HashMap<String, Object>();
							sMap.put("sectionEid", section.getEid());
							sMap.put("sectionTitle", section.getTitle());
							sMap.put("sectionCategory", section.getCategory());
							sMap.put("sectionCategoryDescription", section.getCategoryDescription());
							sMap.put("sectionAttached", section.getAttached());
							myMappedSections.add(sMap);
						}
						coMap.put("sections", myMappedSections);
						myMappedCourseOfferings.add(coMap);
					}
					map.put("courseOfferings", myMappedCourseOfferings);
					getMyMappedCourses().add(map);
				}
				map2.put("courses", getMyMappedCourses());
			}
		}
	}

	public Map<String, Object> getResponseMap() 
	{
		return map2;
	}
	
	private List prepareCourseAndSectionListing(String userId, String academicSessionEid, CourseManagementService cms) {
		HashMap<String, CourseOffering> courseOfferingHash = new HashMap();
		HashMap sectionHash = new HashMap();
		prepareCourseAndSectionMap(cms, userId, academicSessionEid,
				courseOfferingHash, sectionHash);
		// courseOfferingHash & sectionHash should now be filled with stuffs
		// put section list in state for later use

		ArrayList offeringList = new ArrayList();
		Set keys = courseOfferingHash.keySet();
		for (Iterator i = keys.iterator(); i.hasNext();) {
			CourseOffering o = courseOfferingHash.get((String) i.next());
			offeringList.add(o);
		}

		Collection offeringListSorted = offeringList;
		ArrayList<CourseObject> resultedList = new ArrayList<CourseObject>();

		// use this to keep track of courseOffering that we have dealt with
		// already
		// this is important 'cos cross-listed offering is dealt with together
		// with its
		// equivalents
		ArrayList dealtWith = new ArrayList();

		for (Iterator j = offeringListSorted.iterator(); j.hasNext();) {
			CourseOffering o = (CourseOffering) j.next();
			if (!dealtWith.contains(o.getEid())) {
				// 1. construct list of CourseOfferingObject for CourseObject
				ArrayList l = new ArrayList();
				CourseOfferingObject coo = new CourseOfferingObject(o,
						(ArrayList) sectionHash.get(o.getEid()));
				l.add(coo);

				// 2. check if course offering is cross-listed
				Set set = cms.getEquivalentCourseOfferings(o.getEid());
				if (set != null)
				{
					for (Iterator k = set.iterator(); k.hasNext();) {
						CourseOffering eo = (CourseOffering) k.next();
						if (courseOfferingHash.containsKey(eo.getEid())) {
							// => cross-listed, then list them together
							CourseOfferingObject coo_equivalent = new CourseOfferingObject(
									eo, (ArrayList) sectionHash.get(eo.getEid()));
							l.add(coo_equivalent);
							dealtWith.add(eo.getEid());
						}
					}
				}
				CourseObject co = new CourseObject(o, l);
				dealtWith.add(o.getEid());
				resultedList.add(co);
			}
		}
		return resultedList;
	} // prepareCourseAndSectionListing
	
	/**
	 * 
	 * @param cms
	 * @param userId
	 * @param academicSessionEid
	 * @param courseOfferingHash
	 * @param sectionHash
	 */
	private void prepareCourseAndSectionMap(CourseManagementService cms, String userId,
			String academicSessionEid, HashMap<String, CourseOffering> courseOfferingHash,
			HashMap sectionHash) {

		// looking for list of courseOffering and sections that should be
		// included in
		// the selection list. The course offering must be offered
		// 1. in the specific academic Session
		// 2. that the specified user has right to attach its section to a
		// course site
		// map = (section.eid, sakai rolename)
		if (groupProvider == null)
		{
			log.warn("Group provider not found");
			return;
		}
		
		Map map = groupProvider.getGroupRolesForUser(userId);
		if (map == null)
			return;

		Set keys = map.keySet();
		Set roleSet = getRolesAllowedToAttachSection();
		for (Iterator i = keys.iterator(); i.hasNext();) {
			String sectionEid = (String) i.next();
			String role = (String) map.get(sectionEid);
			if (includeRole(role, roleSet)) {
				Section section = null;
				getCourseOfferingAndSectionMap(cms, academicSessionEid, courseOfferingHash, sectionHash, sectionEid, section);
			}
		}
		
		// now consider those user with affiliated sections
		/*List affiliatedSectionEids = affiliatedSectionProvider.getAffiliatedSectionEids(userId, academicSessionEid);
		if (affiliatedSectionEids != null)
		{
			for (int k = 0; k < affiliatedSectionEids.size(); k++) {
				String sectionEid = (String) affiliatedSectionEids.get(k);
				Section section = null;
				getCourseOfferingAndSectionMap(academicSessionEid, courseOfferingHash, sectionHash, sectionEid, section);
			}
		}*/
		
		
	} // prepareCourseAndSectionMap

	private void getCourseOfferingAndSectionMap(CourseManagementService cms, String academicSessionEid, HashMap courseOfferingHash, HashMap sectionHash, String sectionEid, Section section) {
		try {
			section = cms.getSection(sectionEid);
		} catch (IdNotFoundException e) {
			log.warn(this + ".getCourseOfferingAndSectionMap:" + " cannot find section id=" + sectionEid, e);
		}
		if (section != null) {
			String courseOfferingEid = section.getCourseOfferingEid();
			CourseOffering courseOffering = cms
					.getCourseOffering(courseOfferingEid);
			String sessionEid = courseOffering.getAcademicSession()
					.getEid();
			if (academicSessionEid.equals(sessionEid)) {
				// a long way to the conclusion that yes, this course
				// offering
				// should be included in the selected list. Sigh...
				// -daisyf
				ArrayList sectionList = (ArrayList) sectionHash
						.get(courseOffering.getEid());
				if (sectionList == null) {
					sectionList = new ArrayList();
				}
				sectionList.add(new SectionObject(section, cms));
				sectionHash.put(courseOffering.getEid(), sectionList);
				courseOfferingHash.put(courseOffering.getEid(),
						courseOffering);
			}
		}
	}

	protected Set getRolesAllowedToAttachSection() {
		// Use !site.template.[site_type]
		String azgId = "!site.template.course";
		AuthzGroup azgTemplate;
		try {
			azgTemplate = authzGroupService.getAuthzGroup(azgId);
		} catch (GroupNotDefinedException e) {
			log.warn(this + ".getRolesAllowedToAttachSection: Could not find authz group " + azgId, e);
			return new HashSet();
		}
		Set roles = azgTemplate.getRolesIsAllowed("site.upd");
		roles.addAll(azgTemplate.getRolesIsAllowed("realm.upd"));
		return roles;
	} // getRolesAllowedToAttachSection
	
	/** 
	 * @param role
	 * @return
	 */
	private boolean includeRole(String role, Set roleSet) {
		boolean includeRole = false;
		for (Iterator i = roleSet.iterator(); i.hasNext();) {
			String r = (String) i.next();
			if (r.equals(role)) {
				includeRole = true;
				break;
			}
		}
		return includeRole;
	} // includeRole
	
	class SectionObject {
		public Section section;

		public String eid;

		public String title;

		public String category;

		public String categoryDescription;

		public boolean isLecture;

		public boolean attached;

		public String authorizer;

		public SectionObject(Section section, CourseManagementService cms) {
			this.section = section;
			this.eid = section.getEid();
			this.title = section.getTitle();
			this.category = section.getCategory();
			this.categoryDescription = cms
					.getSectionCategoryDescription(section.getCategory());
			if ("01.lct".equals(section.getCategory())) {
				this.isLecture = true;
			} else {
				this.isLecture = false;
			}
			Set set = authzGroupService.getAuthzGroupIds(section.getEid());
			if (set != null && !set.isEmpty()) {
				this.attached = true;
			} else {
				this.attached = false;
			}
		}

		public Section getSection() {
			return section;
		}

		public String getEid() {
			return eid;
		}

		public String getTitle() {
			return title;
		}

		public String getCategory() {
			return category;
		}

		public String getCategoryDescription() {
			return categoryDescription;
		}

		public boolean getIsLecture() {
			return isLecture;
		}

		public boolean getAttached() {
			return attached;
		}

		public String getAuthorizer() {
			return authorizer;
		}

		public void setAuthorizer(String authorizer) {
			this.authorizer = authorizer;
		}

	} // SectionObject constructor

	class CourseObject {
		public String eid;
		
		public String title;
		
		public List<CourseOfferingObject> courseOfferingObjects;
		
		public CourseObject(CourseOffering offering, List courseOfferingObjects) {
			this.eid = offering.getEid();
			this.title = offering.getTitle();
			this.courseOfferingObjects = courseOfferingObjects;
		}
		
		public String getEid() {
			return eid;
		}
		
		public String getTitle() {
			return title;
		}
		
		public List<CourseOfferingObject> getCourseOfferingObjects() {
			return courseOfferingObjects;
		}

	} // CourseObject constructor

	class CourseOfferingObject {
		public String eid;
		
		public String title;
		
		public List<SectionObject> sections;
		
		public CourseOfferingObject(CourseOffering offering,
				List unsortedSections) {
			List propsList = new ArrayList();
			propsList.add("category");
			propsList.add("eid");
			this.sections = unsortedSections;
			this.eid = offering.getEid();
			this.title = offering.getTitle();
		}
		
		public String getEid() {
			return eid;
		}
		
		public String getTitle() {
			return title;
		}
		
		public List<SectionObject> getSections() {
			return sections;
		}
	} // CourseOfferingObject constructor
}
