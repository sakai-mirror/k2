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
import org.sakaiproject.sdata.tool.model.CHSNodeMap;
import org.sakaiproject.sdata.tool.api.Handler;
import org.sakaiproject.sdata.tool.api.ResourceDefinition;
import org.sakaiproject.sdata.tool.api.SDataException;
import org.sakaiproject.sdata.tool.api.SDataFunction;

/**
 * <p>
 * Get the node metadata
 * </p>
 * <p>
 * The content entity is specified by the URL. The response will contain the
 * metadata associated with the node, down to the depth specified in the
 * ResourceDefinition object
 * </p>
 * 
 * @author ieb
 */
public class CHSNodeMetadata implements SDataFunction
{

	public CHSNodeMetadata()
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.SDataFunction#call(org.sakaiproject.sdata.tool.api.Handler,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	public void call(Handler handler, HttpServletRequest request,
			HttpServletResponse response, Object target, ResourceDefinition rp)
			throws SDataException
	{
		
		SDataFunctionUtil.checkMethod(request.getMethod(), "GET");

		try
		{
			ContentEntity n = (ContentEntity) target;
			CHSNodeMap nm = new CHSNodeMap(n, rp.getDepth(), rp);
			handler.sendMap(request, response, nm);
		}
		catch (IOException e)
		{
			throw new SDataException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e
					.getMessage());
		}

	}

	public void destroy() 
	{
	}


}
