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

package org.sakaiproject.sdata.tool.functions;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.content.api.ContentEntity;
import org.sakaiproject.content.api.GroupAwareEdit;
import org.sakaiproject.sdata.tool.model.CHSNodeMap;
import org.sakaiproject.sdata.tool.api.Handler;
import org.sakaiproject.sdata.tool.api.ResourceDefinition;
import org.sakaiproject.sdata.tool.api.SDataException;
import org.sakaiproject.time.api.Time;

/**
 * <p>
 * Sets the state of hidden, releaseData and retractDate on a ContentCollection
 * or ContentResource. (Folder or File) The File or Folder is specified in the
 * path. There are three parameters; hidden, releaseDate, retractDate.
 * </p>
 * <p>
 * <b>Hidden</b> if hidden is set to true, then the item is hidden, if false
 * the item is show, the release and retract dates control when it is shown.
 * </p>
 * <p>
 * <b>ReleaseDate</b> if hidden is false or not set, and ReleaseDate is set,
 * then the release date is set on the target object.
 * </p>
 * <p>
 * <b>RetractDate</b> if hidden is false or not set, and Retract is set, then
 * the retract date is set on the target object.
 * </p>
 * <p>
 * The target object of the function is taken from the path of the request after
 * processing.
 * </p>
 * <p>
 * If there are issues a SDataExcepion will be thrown with the appropriate
 * status code.
 * </p>
 * 
 * @author ieb
 */
public class CHSHideReleaseFunction extends CHSSDataFunction
{

	public static final String HIDDEN = "hidden";

	public static final String RELEASE_DATE = "releaseDate";

	public static final String RETRACT_DATE = "retractDate";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.SDataFunction#call(org.sakaiproject.sdata.tool.api.Handler,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, java.lang.Object,
	 *      org.sakaiproject.sdata.tool.api.ResourceDefinition)
	 */
	public void call(Handler handler, HttpServletRequest request,
			HttpServletResponse response, Object target, ResourceDefinition rp)
			throws SDataException
	{
		
		SDataFunctionUtil.checkMethod(request.getMethod(), "POST");
		// parse the request
		boolean hidden = Boolean.valueOf(request.getParameter(HIDDEN));
		String releaseDate = request.getParameter(RELEASE_DATE);
		Time releaseDateTime = null;
		if (releaseDate != null && releaseDate.trim().length() > 0)
		{
			long releaseDateEpoch = Long.parseLong(releaseDate);
			releaseDateTime = timeService.newTime(releaseDateEpoch);
		}
		String retractDate = request.getParameter(RETRACT_DATE);
		Time retractDateTime = null;
		if (retractDate != null && retractDate.trim().length() > 0)
		{
			long retractDateEpoch = Long.parseLong(retractDate);
			retractDateTime = timeService.newTime(retractDateEpoch);
		}

		// set the sttus
		GroupAwareEdit edit = editEntity(handler, target, rp.getRepositoryPath());
		edit.setAvailability(hidden, releaseDateTime, retractDateTime);

		// commit
		commitEntity(edit);

		CHSNodeMap nm = new CHSNodeMap((ContentEntity) edit, rp.getDepth(), rp);
		try
		{
			handler.sendMap(request, response, nm);
		}
		catch (IOException e)
		{
			throw new SDataException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"IO Error " + e.getMessage());
		}

	}


}
