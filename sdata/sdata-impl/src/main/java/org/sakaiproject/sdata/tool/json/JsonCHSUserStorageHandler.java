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

package org.sakaiproject.sdata.tool.json;

import org.sakaiproject.sdata.tool.CHSUserStorageHandler;
import org.sakaiproject.sdata.tool.api.HandlerSerialzer;

/**
 * Serializes the output of a UserStorageSevlet as json
 * 
 * @author ieb
 */
public class JsonCHSUserStorageHandler extends CHSUserStorageHandler
{

	private HandlerSerialzer serializer;

	/**
	 * Create a JSON CHS User storage handler
	 */
	public JsonCHSUserStorageHandler()
	{
		serializer = new JsonHandlerSerializer();
	}

	@Override
	public HandlerSerialzer getSerializer() {
		return serializer;
	}

	

}
