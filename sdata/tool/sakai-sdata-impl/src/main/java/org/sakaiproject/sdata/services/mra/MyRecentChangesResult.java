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
 * Bean to represent recent changes
 * 
 * @author
 */
public class MyRecentChangesResult extends MyRecentChangesSqlresult
{

	private String sitename;

	private String reference;

	private String cleanVersion;

	/**
	 * Construct by injection
	 * 
	 * @param context
	 * @param name
	 * @param tool
	 * @param version
	 */
	public MyRecentChangesResult(String context, String name, String tool, String version)
	{

		super.setContext(context);
		super.setName(name);
		super.setTool(tool);
		super.setVersion(version);

	}

	/**
	 * Empty constructor
	 */
	public MyRecentChangesResult()
	{

	}

	/**
	 * The Site name of the change
	 * 
	 * @param sitename
	 */
	public void setSitename(String sitename)
	{
		this.sitename = sitename;
	}

	/**
	 * The site name of the change
	 * 
	 * @return
	 */
	public String getSitename()
	{
		return sitename;
	}

	/**
	 * The reference
	 * 
	 * @param reference
	 */
	public void setReference(String reference)
	{
		this.reference = "/access" + reference;
	}

	/**
	 * The reference
	 * 
	 * @return
	 */
	public String getReference()
	{
		return reference;
	}

	/**
	 * 
	 * 
	 * @param cleanVersion
	 */
	public void setCleanVersion(String cleanVersion)
	{
		this.cleanVersion = cleanVersion;
	}

	/**
	 * 
	 * @return
	 */
	public String getCleanVersion()
	{
		return cleanVersion;
	}

}
