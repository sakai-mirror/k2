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

package org.sakaiproject.sdata.services.messages;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.sakaiproject.sdata.services.profile.ProfileSqlresult;
import org.sakaiproject.sdata.services.profile.ProfileSqlresult2;

/**
 * 
 * @author
 */
public class MessageSqlresult
{
	
	private int id;
	private String sender;
	private String receiver;
	private String title;
	private String message;
	private boolean isInvite;
	private Date dateTime;
	private String senderToString = "Unknown";
	private String datetimeToString;
	private boolean read;
	private ProfileSqlresult2 profileinfo;
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getSender() {
		return sender;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setInvite(boolean isInvite) {
		this.isInvite = isInvite;
	}

	public boolean isInvite() {
		return isInvite;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
		Date date = new Date();
		if (dateTime.after(new Date(date.getYear(), date.getMonth(), date.getDate(), 0, 0, 0))){
			this.setDatetimeToString(new SimpleDateFormat("HH:mm").format(dateTime));
		} else {
			this.setDatetimeToString(new SimpleDateFormat("dd/MM/yyyy").format(dateTime));
		}
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setSenderToString(String senderToString) {
		this.senderToString = senderToString;
	}

	public String getSenderToString() {
		return senderToString;
	}

	public void setDatetimeToString(String datetimeToString) {
		this.datetimeToString = datetimeToString;
	}

	public String getDatetimeToString() {
		return datetimeToString;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public boolean isRead() {
		return read;
	}

	public void setProfileinfo(ProfileSqlresult2 profileinfo) {
		this.profileinfo = profileinfo;
	}

	public ProfileSqlresult2 getProfileinfo() {
		return profileinfo;
	}

}
