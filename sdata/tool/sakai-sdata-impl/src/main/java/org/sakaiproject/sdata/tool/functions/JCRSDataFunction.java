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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.sdata.tool.api.SDataFunction;

/**
 * The base SDataFunction for JCR
 * 
 * @author ieb
 */
public abstract class JCRSDataFunction implements SDataFunction
{

	private static final Log log = LogFactory.getLog(JCRSDataFunction.class);

	/**
	 * @param string
	 * @param e
	 */
	protected void logException(String string, Exception e)
	{
		if (log.isDebugEnabled())
		{
			log.warn("Type missmatch ", e);
		}
		else
		{
			log.warn("Type missmatch " + e.getMessage());
		}
	}

	
	public void destroy() 
	{
	}

}
