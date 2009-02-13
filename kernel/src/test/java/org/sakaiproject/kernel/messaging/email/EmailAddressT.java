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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
  public void toStringMinimum() {
    String address = "test1@example.com";
    EmailAddress ea = new EmailAddress(address);
    String toString = ea.toString();
    assertEquals(address, toString);
  }

  @Test
  public void toStringFull() {
    String address = "test1@example.com";
    String personal = "Test Person";
    EmailAddress ea = new EmailAddress(address, personal);
    String toString = ea.toString();
    assertEquals("\"" + personal + "\" <" + address + ">", toString);
  }

  @Test
  public void equals() {
    String e1 = "test1@example.com";
    String e2 = "test2@example.com";
    EmailAddress ea1 = new EmailAddress(e1);
    EmailAddress ea1_1 = new EmailAddress(e1);
    EmailAddress ea2 = new EmailAddress("test3@example.com");

    // hash codes are equal
    assertEquals(ea1.hashCode(), ea1_1.hashCode());

    // EmailAddress doesn't equal String
    assertFalse(ea1.equals(e1));

    // EmailAddress equals self
    assertTrue(ea1.equals(ea1));

    // Converse equals
    assertTrue(ea1.equals(ea1_1));
    assertTrue(ea1_1.equals(ea1));

    // null arguments doesn't equal null address
    assertFalse(new EmailAddress(null).equals(null));

    // null doesn't equal not-null
    assertFalse(new EmailAddress(null).equals("whatever"));

    // not-null doesn't equal null
    assertFalse(ea2.equals(null));
  }
}
