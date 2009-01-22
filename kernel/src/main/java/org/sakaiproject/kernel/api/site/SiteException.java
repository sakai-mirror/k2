/*******************************************************************************
 * Copyright 2008 Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.sakaiproject.kernel.api.site;

/**
 *
 */
public class SiteException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  /**
   *
   */
  public SiteException() {
    // TODO Auto-generated constructor stub
  }

  /**
   * @param message
   */
  public SiteException(String message) {
    super(message);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param cause
   */
  public SiteException(Throwable cause) {
    super(cause);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param message
   * @param cause
   */
  public SiteException(String message, Throwable cause) {
    super(message, cause);
    // TODO Auto-generated constructor stub
  }

}