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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.sdata.tool.api.Handler;
import org.sakaiproject.sdata.tool.api.ResourceDefinition;
import org.sakaiproject.sdata.tool.api.SDataException;

/**
 * This has not been implemented as yet.
 * 
 * @author ieb
 */
public class JCRMoveFunction extends JCRSDataFunction
{
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
		// TODO To Be implemented
		throw new SDataException(HttpServletResponse.SC_NOT_IMPLEMENTED,
				" Move is not implemented in JCR at the moment ");
	}


}
