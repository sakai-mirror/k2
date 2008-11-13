/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2008 Timefields Ltd
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

package org.sakaiproject.sdata.tool.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sakaiproject.sdata.tool.RFC1123Date;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * @author ieb
 */
public class RFC1123DateTest {

  /**
   * @throws ParseException
   */
  @Test
  public void testDate() throws ParseException {
    Calendar cal = Calendar.getInstance();
    cal.set(2007, 5, 5, 5, 5, 0);
    cal.set(Calendar.MILLISECOND, 0);
    for (int i = 0; i < 100; i++) {
      cal.add(Calendar.SECOND, i * 3656 * 24);
      String dateString = RFC1123Date.formatDate(cal.getTime());
      Date parsed = RFC1123Date.parseDate(dateString);
      assertEquals("Dates didnt match ", cal.getTimeInMillis(), parsed
          .getTime());
    }
  }
}
