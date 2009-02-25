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
package org.sakaiproject.kernel.messaging;

import org.sakaiproject.kernel.api.messaging.Email;

/**
 *
 */
public class EmailImpl extends MessageImpl implements Email {
  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Email#getAddress()
   */
  public String getAddress() {
    return getField(Email.Field.ADDRESS.toString());
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.messaging.Email#setAddress(java.lang.String)
   */
  public void setAddress(String address) {
    setField(Email.Field.ADDRESS.toString(), address);
  }



}
