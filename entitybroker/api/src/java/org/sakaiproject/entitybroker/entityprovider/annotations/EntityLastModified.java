/**
 * $Id: EntityLastModified.java 51318 2008-08-24 05:28:47Z csev@umich.edu $
 * $URL: https://source.sakaiproject.org/svn/entitybroker/branches/sakai_2-6-x/api/src/java/org/sakaiproject/entitybroker/entityprovider/annotations/EntityLastModified.java $
 * EntityId.java - entity-broker - Apr 13, 2008 12:17:49 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Sakai Foundation
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
 */

package org.sakaiproject.entitybroker.entityprovider.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Date;

/**
 * Marks a getter method or field as the last modified time code (unix time code) for an entity,
 * this can be a {@link Date}, {@link Long}, long, or {@link String} (will attempt to convert this to a long)<br/>
 * the convention is to try to convert the return from the 
 * "getLastModified" method or the value in the "lastModified" field<br/>
 * <b>NOTE:</b> This annotation should only be used once in a class,
 * the getter method must take no arguments and return an object
 * 
 * @author Aaron Zeckoski (aaron@caret.cam.ac.uk)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface EntityLastModified { }