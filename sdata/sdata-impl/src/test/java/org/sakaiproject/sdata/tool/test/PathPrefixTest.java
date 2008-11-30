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

package org.sakaiproject.sdata.tool.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.sakaiproject.kernel.util.PathPrefix;

/**
 * @author ieb
 */
public class PathPrefixTest 
{

	private static final String[] testUsers = new String[] { null, "", "~test", "ieb236" };

	private static final Log log = LogFactory.getLog(PathPrefixTest.class);

	
	/**
	 * 
	 */
	@Test
	public void testGetPath()
	{

		for (String user : testUsers)
		{
			log.info("User:" + user + ":" + PathPrefix.getPrefix(user));
		}
	}

}
