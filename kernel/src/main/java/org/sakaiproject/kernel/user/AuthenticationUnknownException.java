/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2005, 2006, 2008 Sakai Foundation
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

package org.sakaiproject.kernel.user;

import org.sakaiproject.kernel.api.user.AuthenticationException;

/**
 * <p>
 * AuthenticationUnknownException models authetication evidence that the
 * AuthenticationManager doesn't understand how to process.
 * </p>
 */
public class AuthenticationUnknownException extends AuthenticationException {

  /**
   * 
   */
  private static final long serialVersionUID = -8046090795443123389L;

  /**
   * 
   */
  public AuthenticationUnknownException() {
  }

  /**
   * @param arg0
   */
  public AuthenticationUnknownException(String arg0) {
    super(arg0);
  }

  /**
   * @param arg0
   */
  public AuthenticationUnknownException(Throwable arg0) {
    super(arg0);
  }

  /**
   * @param arg0
   * @param arg1
   */
  public AuthenticationUnknownException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }
}
