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

package org.sakaiproject.sdata.services.connections;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.db.api.SqlReader;

/**
 *  reader to read MyRecentChanges
 * 
 * @author
 */
public class ConnectionSqlreader implements SqlReader
{
	private static final Log log = LogFactory.getLog(ConnectionSqlreader.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.db.api.SqlReader#readSqlResultRecord(java.sql.ResultSet)
	 */
	public ConnectionSqlresult readSqlResultRecord(ResultSet result)
	{

		ConnectionSqlresult res = new ConnectionSqlresult();

		try
		{
			
			res.setId(result.getInt("id"));
			res.setAccepted(result.getBoolean("accepted"));
			res.setReceiver(result.getString("receiver"));
			res.setConnectionType(result.getInt("connectiontype"));
			res.setInviter(result.getString("inviter"));

		}
		catch (SQLException e)
		{
			log.error("Error Executing sql reader ", e);

		}

		return res;
	}
}
