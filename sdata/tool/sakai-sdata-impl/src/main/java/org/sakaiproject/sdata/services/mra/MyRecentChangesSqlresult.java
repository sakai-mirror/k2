/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/trunk/sdata/sdata-tool/impl/src/java/org/sakaiproject/sdata/tool/JCRDumper.java $
 * $Id: JCRDumper.java 45207 2008-02-01 19:01:06Z ian@caret.cam.ac.uk $
 ***********************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation.
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

package org.sakaiproject.sdata.services.mra;

/**
 * 
 * @author
 */
public class MyRecentChangesSqlresult
{

	private String version;

	private String context;

	private String name;

	private String tool;

	/**
	 * 
	 * @param version
	 */
	public void setVersion(String version)
	{
		this.version = version;
	}

	/**
	 * 
	 * @return
	 */
	public String getVersion()
	{
		return version;
	}

	/**
	 * 
	 * @param context
	 */
	public void setContext(String context)
	{
		this.context = context;
	}

	/**
	 * 
	 * @return
	 */
	public String getContext()
	{
		return context;
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * 
	 * @return
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * 
	 * @param tool
	 */
	public void setTool(String tool)
	{
		this.tool = tool;
	}

	/**
	 * 
	 * @return
	 */
	public String getTool()
	{
		return tool;
	}

}
