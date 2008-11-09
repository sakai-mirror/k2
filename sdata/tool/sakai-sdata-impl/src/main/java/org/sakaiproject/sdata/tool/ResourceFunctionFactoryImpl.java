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

package org.sakaiproject.sdata.tool;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.sdata.tool.api.ResourceFunctionFactory;
import org.sakaiproject.sdata.tool.api.SDataFunction;

/**
 * @author ieb
 */
public class ResourceFunctionFactoryImpl implements ResourceFunctionFactory
{

	private static final Log log = LogFactory.getLog(ResourceFunctionFactoryImpl.class);

	private Map<String, SDataFunction> functions = new HashMap<String, SDataFunction>();

	/**
	 * @param config
	 */
	public ResourceFunctionFactoryImpl(Map<String, String> config)
	{
		ClassLoader cl = this.getClass().getClassLoader();
		for (String k : config.keySet())
		{
			if (k.startsWith("function."))
			{
				log.info("Loading SDataFunction " + k + ":" + config.get(k));
				try
				{
					Class<?> c = (Class<?>) cl.loadClass(config
							.get(k));
					SDataFunction sdf = (SDataFunction) c.newInstance();
					functions.put(k.substring("function.".length()), sdf);
				}
				catch (ClassNotFoundException e)
				{
					log.error("Failed to load class for key " + k + ":" + e.getMessage());
				}
				catch (InstantiationException e)
				{
					log.error("Failed to load class for key " + k, e);
				}
				catch (IllegalAccessException e)
				{
					log.error("Failed to load class for key " + k, e);
				}

			}
		}

	}
	
	public void destroy() 
	{
		for ( SDataFunction sdf : functions.values() ) 
		{
			sdf.destroy();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.ResourceFunctionFactory#getFunction(java.lang.String)
	 */
	public SDataFunction getFunction(String definition)
	{
		return functions.get(definition);
	}

}
