/**
 * $Id: Taggable.java 51318 2008-08-24 05:28:47Z csev@umich.edu $
 * $URL: https://source.sakaiproject.org/svn/entitybroker/branches/sakai_2-6-x/api/src/java/org/sakaiproject/entitybroker/entityprovider/capabilities/Taggable.java $
 * AutoRegister.java - entity-broker - 31 May 2007 7:01:11 PM - azeckoski
 **************************************************************************
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
 **/

package org.sakaiproject.entitybroker.entityprovider.capabilities;

import org.sakaiproject.entitybroker.entityprovider.EntityProvider;

/**
 * Allows an entity to have tags associated with it which can be searched for or simply used as a
 * way to link to this entity <br/>
 * This uses the internal tag storage mechanism or a central tag storage mechanism
 * to store the tag applications<br/>
 * This is one of the capability extensions for the {@link EntityProvider} interface<br/>
 * 
 * @author Aaron Zeckoski (aaron@caret.cam.ac.uk)
 */
public interface Taggable extends EntityProvider {

   // this space left blank intentionally

}