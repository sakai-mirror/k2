/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/trunk/sdata/sdata-tool/impl/src/java/org/sakaiproject/sdata/tool/StreamRequestFilter.java $
 * $Id: StreamRequestFilter.java 45207 2008-02-01 19:01:06Z ian@caret.cam.ac.uk $
 ***********************************************************************************
 *
 * Copyright (c) 2008 Timefields Ltd
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

package org.sakaiproject.sdata.tool.json;

import javax.servlet.ServletException;

import org.sakaiproject.sdata.tool.ServiceHandler;
import org.sakaiproject.sdata.tool.api.HandlerSerialzer;
import org.sakaiproject.sdata.tool.api.ServiceDefinitionFactory;

/**
 * A base class for Handling JSON service requests
 * 
 * @author ieb
 */
public class JSONServiceHandler extends ServiceHandler {

	/**
	 * The serialization version number
	 */
	private static final long serialVersionUID = 1L;

	private HandlerSerialzer serializer;

	/**
	 * Create a JSON CHS User storage handler
	 */
	public JSONServiceHandler() {
		serializer = new JsonHandlerSerializer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.ServiceServlet#getServiceDefinitionFactory()
	 */
	@Override
	protected ServiceDefinitionFactory getServiceDefinitionFactory()
			throws ServletException {
		throw new ServletException("No Default ServiceDefinitionFactory");
	}

	@Override
	public HandlerSerialzer getSerializer() {
		return serializer;
	}

}
