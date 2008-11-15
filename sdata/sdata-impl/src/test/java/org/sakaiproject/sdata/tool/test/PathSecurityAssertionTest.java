/**********************************************************************************
 * $URL$
 * $Id$
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

package org.sakaiproject.sdata.tool.test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.sakaiproject.kernel.api.authz.AuthzResolverService;
import org.sakaiproject.kernel.api.authz.PermissionDeniedException;
import org.sakaiproject.kernel.api.authz.PermissionQuery;
import org.sakaiproject.sdata.tool.api.SDataException;
import org.sakaiproject.sdata.tool.util.PathSecurityAssertion;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

/**
 */
public class PathSecurityAssertionTest {
  private static final Log log = LogFactory
      .getLog(PathSecurityAssertionTest.class);


  private static final String BASE_URL = "/somelocation";


  private static final String BASE_RESOURCE = "/Aresource";


  private String[] tests = { 
      "GET,/sfsfdffsd,/sfsfdffsd,m",
      getSpec("GET","/resourceA/sdfsfd","g"),
      getSpec("GET","/resourceA/","d"),
      getSpec("PUT","/resourceA/sdfsfd","g"),
      getSpec("PUT","/resourceA/","d"),
      getSpec("POST","/resourceA/sdfsfd","g"),
      getSpec("POST","/resourceA/","d"),
      getSpec("DELETE","/resourceA/sdfsfd","g"),
      getSpec("DELETE","/resourceA/","d"),
      getSpec("OPTIONS","/resourceA/sdfsfd","d"),
      getSpec("OPTIONS","/resourceA/","d"),
      getSpec("BADMETHOD","/resourceA/sdfsfd","d")
 
  };

  @Test
  public void testAssertions() throws ServletException {

    AuthzResolverService authzResolverService = createMock(AuthzResolverService.class);
    Map<String,PermissionQuery> locks = new HashMap<String, PermissionQuery>();
    PermissionQuery getPermission = createMock(PermissionQuery.class);
    PermissionQuery putPermission = createMock(PermissionQuery.class);
    PermissionQuery postPermission = createMock(PermissionQuery.class);
    PermissionQuery deletePermission = createMock(PermissionQuery.class);
    PermissionQuery headPermission = createMock(PermissionQuery.class);
    PermissionQuery optionsPermission = createMock(PermissionQuery.class);
    locks.put("GET", getPermission);
    locks.put("PUT", putPermission);
    locks.put("POST", postPermission);
    locks.put("DELETE", deletePermission);
    locks.put("HEAD", headPermission);
    locks.put("OPTIONS", optionsPermission);

    PathSecurityAssertion psa = new PathSecurityAssertion(authzResolverService);
    psa.setBaseURL(BASE_URL);
    psa.setBaseResource(BASE_RESOURCE);
    psa.setLocks(locks);

    for (String test : tests) {
      reset(authzResolverService);
      String[] t = test.split(",");
      boolean granted = "g".equals(t[3]);
      boolean denied = "d".equals(t[3]);
      boolean mismatch = "m".equals(t[3]);

      if ( ! mismatch ) {
        authzResolverService.check(t[2], locks.get(t[0]));
        if ( !granted || denied ) {
          expectLastCall().andThrow(new PermissionDeniedException());
        }
      }
      replay(authzResolverService);
      

      try {
        psa.check(t[0], t[1]);
        assertTrue(granted);
      } catch (PermissionDeniedException pde ) {
        assertTrue(denied);
      } catch (SDataException sde) {
        assertTrue(mismatch);
      }
      verify(authzResolverService);
    }
  }

  /**
   * @param string
   * @param string2
   * @param string3
   * @return
   */
  private String getSpec(String method, String path, String response) {
    return method+","+BASE_URL+path+","+BASE_RESOURCE+path+","+response;
  }

}
