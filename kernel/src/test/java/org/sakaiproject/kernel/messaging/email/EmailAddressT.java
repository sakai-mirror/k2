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
package org.sakaiproject.kernel.messaging.email;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sakaiproject.kernel.api.email.EmailAddress;

/**
 *
 */
public class EmailAddressT {
  @Test
  public void createMinimum() {
    String address = "test1@example.com";
    EmailAddress ea = new EmailAddress(address);
    assertEquals(address, ea.getAddress());
  }

  @Test
  public void createFull() {
    String address = "test1@example.com";
    String personal = "Test Person";
    EmailAddress ea = new EmailAddress(address, personal);
    assertEquals(address, ea.getAddress());
    assertEquals(personal, ea.getPersonal());
  }

  @Test
  public void tostringMinimum() {
    String address = "test1@example.com";
    EmailAddress ea = new EmailAddress(address);
    String toString = ea.toString();
    assertEquals(address, toString);
  }

  @Test
  public void tostringFull() {
    String address = "test1@example.com";
    String personal = "Test Person";
    EmailAddress ea = new EmailAddress(address, personal);
    String toString = ea.toString();
    assertEquals("\"" + personal + "\" <" + address + ">", toString);
  }
}
