/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2007, 2008 Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.cluster.impl;

/**
 * methods for accessing cluster data in a db2 database.
 */
public class SakaiClusterServiceSqlDb2 extends SakaiClusterServiceSqlDefault
{
   public String getListExpiredServers(long timeout)
   {
      return "select SERVER_ID from SAKAI_CLUSTER where SERVER_ID != ? and UPDATE_TIME < CURRENT TIMESTAMP - " + timeout + " SECONDS";
   }

}