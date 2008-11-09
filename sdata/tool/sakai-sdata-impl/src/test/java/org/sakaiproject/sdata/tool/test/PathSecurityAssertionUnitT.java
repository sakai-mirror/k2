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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.Kernel;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.sdata.tool.api.SDataException;
import org.sakaiproject.sdata.tool.util.PathSecurityAssertion;
import org.sakaiproject.tool.api.SessionManager;

/**
 * @author ieb
 */
public class PathSecurityAssertionUnitT extends TestCase
{

	private static final Log log = LogFactory.getLog(PathSecurityAssertionUnitT.class);

	protected static final Map<String, Object> componentMap = new HashMap<String, Object>();

	private String[] tests = { "false,GET,/sfsfdffsd,false",
			"true,GET,/sdfsdffs,false",
			"true,GET,/somelocation/resourceA,true",
			"false,GET,/somelocation/resourceA,false",
			"true,PUT,/somelocation/resourceA,true",
			"false,PUT,/somelocation/resourceA,false",
			"true,POST,/somelocation/resourceA,true",
			"false,POST,/somelocation/resourceA,false",
			"true,DELETE,/somelocation/resourceA,true",
			"false,DELETE,/somelocation/resourceA,false",
			"true,HEAD,/somelocation/resourceA,true",
			"false,HEAD,/somelocation/resourceA,false",
			"true,OPTIONS,/somelocation/resourceA,false", // options is denied
			"false,OPTIONS,/somelocation/resourceA,false",
			"true,BADMETHOD,/somelocation/resourceA,false"

	};

	/**
	 * @param arg0
	 */
	public PathSecurityAssertionUnitT(String arg0)
	{
		super(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{

		Kernel.setComponentManager(new MockComponentManager(componentMap));
		super.setUp();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	public void testInitConfigData() throws ServletException
	{
		Map<String, String> config = new HashMap<String, String>();
		PathSecurityAssertion psa = new PathSecurityAssertion(config);
		config.put("locationbase", "/somelocation");
		psa = new PathSecurityAssertion(config);
		config.put("locationbase", "/somelocation");
		config.put("resourcebasae", "/security");
		psa = new PathSecurityAssertion(config);
		config.put("locationbase", "/somelocation");
		config.put("resourcebasae", "/security");
		config
				.put(
						"locks",
						"GET:content.read,PUT:content.write,HEAD:content.read,POST:content.write,DELETE:content.delete");
		psa = new PathSecurityAssertion(config);
		MockAuthZGroupService mockAuthzGroupService = new MockAuthZGroupService();
		EntityManager mockEntityManager = new MockEntityManager();
		SessionManager mockSessionManager = new MockSessionManager();
		psa.setAuthzGroupService(mockAuthzGroupService);
		psa.setEntityManager(mockEntityManager);
		psa.setSessionManager(mockSessionManager);

		for (String test : tests)
		{
			String[] t = test.split(",");
			try
			{
				if ("true".equals(t[0]))
				{
					log.info("Setting Pass");
					mockAuthzGroupService.setPass(true);
				}
				else
				{
					log.info("Setting Fail");
					mockAuthzGroupService.setPass(false);
				}
				psa.check(t[1], t[2]);
				// assertEquals("Expected Test Fail for " + test, "true", t[3]);
			}
			catch (SDataException sde)
			{
				// assertEquals("Expected Test Pass for " + test, "false",
				// t[3]);
			}
		}

	}

}
