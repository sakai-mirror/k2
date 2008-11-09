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

package org.sakaiproject.sdata.services.profile;

/**
 * 
 * @author
 */
public class ProfileSqlresult2 implements Comparable<ProfileSqlresult2>
{
	
	private String email;
	private String firstName;
	private String lastName;
	private String userid;
	private String basic;
	private String contactinfo;
	private String education;
	private String job;
	private String websites;
	private String academic;
	private String aboutme;
	private String picture;
	private int count = 0;
	
	public ProfileSqlresult2(){
		basic = "";
		contactinfo = "";
		education = "";
		job = "";
		websites = "";
		academic = "";
		aboutme = "";
	}
	
	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUserid() {
		return userid;
	}
	
	public void setBasic(String basic) {
		this.basic = basic;
	}

	public String getBasic() {
		return basic;
	}

	public void setContactinfo(String contactinfo) {
		this.contactinfo = contactinfo;
	}

	public String getContactinfo() {
		return contactinfo;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getEducation() {
		return education;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getJob() {
		return job;
	}

	public void setWebsites(String websites) {
		this.websites = websites;
	}

	public String getWebsites() {
		return websites;
	}

	public void setAcademic(String academic) {
		this.academic = academic;
	}
	
	public String getAcademic() {
		return academic;
	}
	
	public void setAboutme(String aboutme) {
		this.aboutme = aboutme;
	}
	
	public String getAboutme() {
		return aboutme;
	}
	
	public void setPicture(String picture) {
		this.picture = picture;
	}
	
	public String getPicture() {
		return picture;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public int compareTo(ProfileSqlresult2 o) {
		if (getCount() > o.getCount()){
			return 1;
		} else if (getCount() < o.getCount()){
			return -1;
		} else {
			return 0;
		}
	}

}
