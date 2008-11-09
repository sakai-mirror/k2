/**********************************************************************************
 * $URL$
 * $Id$
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

package org.sakaiproject.sdata.tool.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.sdata.tool.api.ResourceDefinition;
import org.sakaiproject.sdata.tool.api.ResourceDefinitionFactory;
import org.sakaiproject.sdata.tool.api.SDataException;
import org.sakaiproject.sdata.tool.api.SecurityAssertion;
import org.sakaiproject.tool.api.Tool;

/**
 * Base Class for a resource definition factory
 * 
 * @author ieb
 */
public class ResourceDefinitionFactoryImpl implements ResourceDefinitionFactory
{

	private static final Log log = LogFactory.getLog(ResourceDefinitionFactoryImpl.class);

	private String basePath;

	private String baseUrl;

	private SecurityAssertion securityAssertion;

	/**
	 * construct a resource definition factory with a base URL and a base Path.
	 * 
	 * @param config
	 * @param basePath
	 * @param basePath2
	 */
	public ResourceDefinitionFactoryImpl(Map<String, String> config, String baseUrl,
			String basePath)
	{
		this.basePath = basePath;
		this.baseUrl = baseUrl;
		securityAssertion = new PathSecurityAssertion(config);
		log.info("Definition Factory Created with base path as " + basePath);
	}

	public ResourceDefinitionFactoryImpl(Map<String, String> config, String baseUrl,
			String basePath, SecurityAssertion securityAssertion)
	{
		this.basePath = basePath;
		this.baseUrl = baseUrl;
		this.securityAssertion = securityAssertion;
		log.info("Definition Factory Created with base path as " + basePath);
	}

	public void destroy() 
	{
		
	}

	/**
	 * Get the ResourceDefinition bean based on the request
	 * 
	 * @param path
	 * @return
	 * @throws SDataException
	 */
	public ResourceDefinition getSpec(final HttpServletRequest request)
			throws SDataException
	{

		request.setAttribute(Tool.NATIVE_URL, Tool.NATIVE_URL);

		String path = request.getPathInfo();
		path = path.substring(baseUrl.length());

		if (path.endsWith("/"))
		{
			path = path.substring(0, path.length() - 1);
		}

		String v = request.getParameter("v"); // version
		int version = -1;
		if (v != null && v.trim().length() > 0)
		{
			version = Integer.parseInt(v);
		}
		String f = request.getParameter("f"); // function
		String d = request.getParameter("d"); // function
		int depth = 1;
		if (d != null && d.trim().length() > 0)
		{
			depth = Integer.parseInt(d);
		}
		return new ResourceDefinitionImpl(request.getMethod(), f, depth, basePath, path,
				version, securityAssertion);
	}

}
