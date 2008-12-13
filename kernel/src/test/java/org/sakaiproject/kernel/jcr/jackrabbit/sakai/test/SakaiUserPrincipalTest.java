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
package org.sakaiproject.kernel.jcr.jackrabbit.sakai.test;

import static org.junit.Assert.*;

import static org.easymock.EasyMock.*;
import org.junit.Test;
import org.sakaiproject.kernel.api.user.User;
import org.sakaiproject.kernel.jcr.jackrabbit.JCRAnonymousPrincipal;
import org.sakaiproject.kernel.jcr.jackrabbit.sakai.SakaiUserPrincipalImpl;


/**
 * 
 */
public class SakaiUserPrincipalTest {

  @Test
  public void testSakaiUserPrincipal() {
    User user = createMock(User.class);
    User user2 = createMock(User.class);
    User user3 = createMock(User.class);
    
    expect(user.getEid()).andReturn("ib236").anyTimes();
    expect(user.getId()).andReturn("uid:ib236").anyTimes();
    expect(user2.getEid()).andReturn("ib236").anyTimes();
    expect(user2.getId()).andReturn("uid:ib236").anyTimes();
    expect(user3.getEid()).andReturn("ib236-3").anyTimes();
    expect(user3.getId()).andReturn("uid:ib236-3").anyTimes();
    
    replay(user,user2,user3);
    SakaiUserPrincipalImpl sakaiPrincipal = new SakaiUserPrincipalImpl(user);
    SakaiUserPrincipalImpl sakaiPrincipal2 = new SakaiUserPrincipalImpl(user2);
    SakaiUserPrincipalImpl sakaiPrincipal3 = new SakaiUserPrincipalImpl(user3);
    assertTrue(sakaiPrincipal.equals(sakaiPrincipal2));
    assertFalse(sakaiPrincipal.equals(sakaiPrincipal3));
    assertEquals("SakaiUserPrincipal", sakaiPrincipal.toString());
    sakaiPrincipal.hashCode();
    assertEquals("uid:ib236", sakaiPrincipal.getName());
    
    verify(user,user2,user3);
  }
}
