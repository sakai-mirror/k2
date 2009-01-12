/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.sakaiproject.kernel.rest.test;


import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.junit.Test;
import org.sakaiproject.kernel.api.session.Session;
import org.sakaiproject.kernel.api.userenv.UserEnvironmentResolverService;
import org.sakaiproject.kernel.rest.UserLocale;

import java.util.Locale;

/**
 * 
 */
public class UserLocaleTest {
  @Test
  public void testUserLocale() {
    UserEnvironmentResolverService userEnvironmentResolverService = createMock(UserEnvironmentResolverService.class);
    Session session = createMock(Session.class);
    
    expect(userEnvironmentResolverService.resolve(session)).andReturn(null).anyTimes();
    expect(session.getAttribute("sakai.locale.")).andReturn(null).anyTimes();
    replay(userEnvironmentResolverService,session);
    UserLocale ul = new UserLocale(userEnvironmentResolverService);
    ul.getLocale(null, session);
    verify(userEnvironmentResolverService,session);
  }
  
  @Test
  public void testUserLocaleToMap() {
    UserEnvironmentResolverService userEnvironmentResolverService = createMock(UserEnvironmentResolverService.class);
    Session session = createMock(Session.class);
    
    expect(userEnvironmentResolverService.resolve(session)).andReturn(null).anyTimes();
    expect(session.getAttribute("sakai.locale.")).andReturn(null).anyTimes();
    replay(userEnvironmentResolverService,session);
    UserLocale ul = new UserLocale(userEnvironmentResolverService);

    System.err.println(ul.localeToMap(ul.getLocale(null, session)));
    System.err.println(ul.localeToMap(new Locale("")));
    System.err.println(ul.localeToMap(new Locale("en","US")));
    verify(userEnvironmentResolverService,session);
  }
}
