/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/component/branches/SAK-12134/component-loader/component-loader-common/impl/src/java/org/sakaiproject/component/loader/common/CommonLifecycleListener.java $
 * $Id: CommonLifecycleListener.java 38536 2007-11-21 17:00:20Z ian@caret.cam.ac.uk $
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

package org.sakaiproject.kernel.loader.common;

/**
 * A listener that listens for lifecycle events in the shared component manager
 * 
 * @author ieb
 * 
 */
public interface CommonLifecycleListener {
  void lifecycleEvent(CommonLifecycleEvent event);

}
