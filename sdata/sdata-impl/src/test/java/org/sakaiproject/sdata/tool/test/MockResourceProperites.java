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

package org.sakaiproject.sdata.tool.test;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Stack;

import org.sakaiproject.entity.api.EntityPropertyNotDefinedException;
import org.sakaiproject.entity.api.EntityPropertyTypeException;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.time.api.Time;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ContentHandler;

/**
 * @author ieb
 */
public class MockResourceProperites implements ResourcePropertiesEdit
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#addAll(org.sakaiproject.entity.api.ResourceProperties)
	 */
	public void addAll(ResourceProperties arg0)
	{


	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#addAll(java.util.Properties)
	 */
	public void addAll(Properties arg0)
	{


	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#addProperty(java.lang.String,
	 *      java.lang.String)
	 */
	public void addProperty(String arg0, String arg1)
	{


	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#addPropertyToList(java.lang.String,
	 *      java.lang.String)
	 */
	public void addPropertyToList(String arg0, String arg1)
	{


	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#clear()
	 */
	public void clear()
	{


	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#get(java.lang.String)
	 */
	public Object get(String arg0)
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getBooleanProperty(java.lang.String)
	 */
	public boolean getBooleanProperty(String arg0)
			throws EntityPropertyNotDefinedException, EntityPropertyTypeException
	{

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getContentHander()
	 */
	public ContentHandler getContentHander()
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getLongProperty(java.lang.String)
	 */
	public long getLongProperty(String arg0) throws EntityPropertyNotDefinedException,
			EntityPropertyTypeException
	{

		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropAssignmentDeleted()
	 */
	public String getNamePropAssignmentDeleted()
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropCalendarLocation()
	 */
	public String getNamePropCalendarLocation()
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropCalendarType()
	 */
	public String getNamePropCalendarType()
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropChatRoom()
	 */
	public String getNamePropChatRoom()
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropCollectionBodyQuota()
	 */
	public String getNamePropCollectionBodyQuota()
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropContentLength()
	 */
	public String getNamePropContentLength()
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropContentType()
	 */
	public String getNamePropContentType()
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropCopyright()
	 */
	public String getNamePropCopyright()
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropCopyrightAlert()
	 */
	public String getNamePropCopyrightAlert()
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropCopyrightChoice()
	 */
	public String getNamePropCopyrightChoice()
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropCreationDate()
	 */
	public String getNamePropCreationDate()
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropCreator()
	 */
	public String getNamePropCreator()
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropDescription()
	 */
	public String getNamePropDescription()
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropDisplayName()
	 */
	public String getNamePropDisplayName()
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropIsCollection()
	 */
	public String getNamePropIsCollection()
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropModifiedBy()
	 */
	public String getNamePropModifiedBy()
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropModifiedDate()
	 */
	public String getNamePropModifiedDate()
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropNewAssignmentCheckAddDueDate()
	 */
	public String getNamePropNewAssignmentCheckAddDueDate()
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropNewAssignmentCheckAutoAnnounce()
	 */
	public String getNamePropNewAssignmentCheckAutoAnnounce()
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropReplyStyle()
	 */
	public String getNamePropReplyStyle()
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropStructObjType()
	 */
	public String getNamePropStructObjType()
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropSubmissionPreviousFeedbackComment()
	 */
	public String getNamePropSubmissionPreviousFeedbackComment()
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropSubmissionPreviousFeedbackText()
	 */
	public String getNamePropSubmissionPreviousFeedbackText()
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropSubmissionPreviousGrades()
	 */
	public String getNamePropSubmissionPreviousGrades()
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropSubmissionScaledPreviousGrades()
	 */
	public String getNamePropSubmissionScaledPreviousGrades()
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropTo()
	 */
	public String getNamePropTo()
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getProperty(java.lang.String)
	 */
	public String getProperty(String arg0)
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getPropertyFormatted(java.lang.String)
	 */
	public String getPropertyFormatted(String arg0)
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getPropertyList(java.lang.String)
	 */
	public List getPropertyList(String arg0)
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getPropertyNames()
	 */
	public Iterator getPropertyNames()
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getTimeProperty(java.lang.String)
	 */
	public Time getTimeProperty(String arg0) throws EntityPropertyNotDefinedException,
			EntityPropertyTypeException
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#getTypeUrl()
	 */
	public String getTypeUrl()
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#isLiveProperty(java.lang.String)
	 */
	public boolean isLiveProperty(String arg0)
	{

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#removeProperty(java.lang.String)
	 */
	public void removeProperty(String arg0)
	{


	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#set(org.sakaiproject.entity.api.ResourceProperties)
	 */
	public void set(ResourceProperties arg0)
	{


	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.entity.api.ResourceProperties#toXml(org.w3c.dom.Document,
	 *      java.util.Stack)
	 */
	public Element toXml(Document arg0, Stack arg1)
	{

		return null;
	}

}
