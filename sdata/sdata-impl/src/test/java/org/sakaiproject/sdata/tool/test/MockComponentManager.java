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

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.sakaiproject.UnitTestComponentManager;

/**
 * @author ieb
 */
public class MockComponentManager implements UnitTestComponentManager
{

	private Map<String, Object> componentMap;

	/**
	 * @param componentMap
	 */
	public MockComponentManager(Map<String, Object> componentMap)
	{
		this.componentMap = componentMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.component.api.ComponentManager#close()
	 */
	public void close()
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.component.api.ComponentManager#contains(java.lang.Class)
	 */
	public boolean contains(Class arg0)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.component.api.ComponentManager#contains(java.lang.String)
	 */
	public boolean contains(String arg0)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.component.api.ComponentManager#get(java.lang.Class)
	 */
	public Object get(Class arg0)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.component.api.ComponentManager#get(java.lang.String)
	 */
	public Object get(String key)
	{
		return componentMap.get(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.component.api.ComponentManager#getConfig()
	 */
	public Properties getConfig()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.component.api.ComponentManager#getRegisteredInterfaces()
	 */
	public Set getRegisteredInterfaces()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.component.api.ComponentManager#hasBeenClosed()
	 */
	public boolean hasBeenClosed()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.component.api.ComponentManager#loadComponent(java.lang.Class,
	 *      java.lang.Object)
	 */
	public void loadComponent(Class arg0, Object arg1)
	{

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.component.api.ComponentManager#loadComponent(java.lang.String,
	 *      java.lang.Object)
	 */
	public void loadComponent(String arg0, Object arg1)
	{

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.component.api.ComponentManager#waitTillConfigured()
	 */
	public void waitTillConfigured()
	{

	}

}
