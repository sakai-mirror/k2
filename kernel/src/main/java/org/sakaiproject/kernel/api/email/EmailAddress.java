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
package org.sakaiproject.kernel.api.email;

import java.io.Serializable;

/**
 * Container for an email address.
 */
public class EmailAddress implements Serializable {
  /**
   * Default serial version UID
   */
  private static final long serialVersionUID = 1L;

  /**
   * holds the personal part of the email address (ie. name)
   */
  private String personal;

  /**
   * an address to be used as an email message recipient
   */
  private final String address;

  /**
   * Constructor for the minimum values of this class.
   *
   * @param address
   *          Email address of recipient
   * @throws IllegalArgumentException
   *           If address is null or empty.
   */
  public EmailAddress(String address) {
    this.address = address;
  }

  /**
   * Constructor for all values of this class.
   *
   * @param name
   *          Personal part of an email address.
   * @param address
   *          Actual address of email recipient.
   * @throws IllegalArgumentException
   *           If address is null or empty.
   * @see org.sakaiproject.email.api.EmailAddress#init(String)
   */
  public EmailAddress(String address, String name) {
    this(address);
    this.personal = name;
  }

  /**
   * Get the name associated to this email addressee.
   *
   * @return The personal part of this email address.
   */
  public String getPersonal() {
    return personal;
  }

  /**
   * Get the recipient's email address.
   *
   * @return The email address of the recipient.
   */
  public String getAddress() {
    return address;
  }

  /**
   * Create a string representation of this email address.
   *
   * @return A String that uses following format:<br/>
   *         if personal part of email is available:
   *         "First Last <email@example.com>"<br/>
   *         if only the address part is available: email@example.com
   */
  @Override
  public String toString() {
    String retval = getAddress();

    boolean personalEmpty = getPersonal() == null
        || getPersonal().trim().length() == 0;

    if (!personalEmpty) {
      retval = "\"" + getPersonal() + "\" <" + getAddress() + ">";
    }

    return retval;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    if (address != null) {
      hash = address.hashCode();
    }
    return hash;
  }

  @Override
  public boolean equals(Object o) {
    boolean equals = false;

    if (o != null) {
      if (this == o) {
        equals = true;
      } else if (o instanceof EmailAddress) {
        EmailAddress ea = (EmailAddress) o;
        if (address == ea.getAddress()
            || (address != null && address.equals(ea.getAddress()))) {
          equals = true;
        }
      }
    }

    return equals;
  }
}
