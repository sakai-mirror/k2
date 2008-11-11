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

package org.sakaiproject.sdata.tool.util;

import com.google.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.sdata.tool.SDataAccessException;
import org.sakaiproject.sdata.tool.api.SDataException;
import org.sakaiproject.sdata.tool.api.SecurityAssertion;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

/**
 * An implementation of the Security Assertion that uses the http method, the
 * path and the sakai security service for perform the assertion. On check it
 * will throw SDataExceptions indicating forbidden if the path is outside its
 * configured range, or it is denied by the the Sakai security service.
 * 
 * @author ieb
 */
public class PathSecurityAssertion implements SecurityAssertion {

  /**
   * the init parameter name for baseLocation
   */
  private static final String BASE_LOCATION_INIT = "locationbase";

  /**
   * The default setting for the baseLocation
   */
  private static final String DEFAULT_BASE_LOCATION = "";

  /**
   * the init parameter for baseResource
   */
  private static final String BASE_REFERENCE_INIT = "referencebase";

  /**
   * the default value for base resource
   */
  private static final String DEFAULT_BASE_REFERENCE = "";

  /**
   * the init parameter name for the lock map
   */
  private static final String LOCK_MAP_INIT = "locks";

  /**
   * the default lock map
   */
  private static final String DEFAULT_LOCK_MAP = "GET:content.read,PUT:content.revise.any,HEAD:content.read,POST:content.revise.any,DELETE:content.delete.any";

  private static final Log log = LogFactory.getLog(PathSecurityAssertion.class);

  /**
   * The base location that is Security Advisor applies to. Only paths that
   * start with this are allowed all others are denied regardless of the method.
   * If the path starts with baseLocation, baseLocation is removed from the path
   * and baseResource is prepended to the patch to generate a full resource
   * location suitable for using with the security service.
   */
  private String baseLocation;

  /**
   * A map mapping http methods to locks
   */
  private Map<String, String> locks;

  /**
   * this is prepended to the resource path, after normalizing (ie removing
   * baseLocation) and before sending to the Sakai security service.
   */
  private String baseReference;

  private boolean inTest = false;

  /**
   * Construct a PathSecurityAssertion class based on the standard configuration
   * map. The Map may have init parameters as defined by BASE_LOCATION_INIT,
   * BASE_RESOURCE_LOCATION_INIT, LOCK_MAP_INIT
   * 
   * @param baseReference
   * @param locks
   * @param inTest
   * 
   * @param config
   */
  @Inject
  public PathSecurityAssertion(String baseLocation, String baseReference,
      Map<String, String> locks, boolean inTest) {
    this.baseLocation = baseLocation;
    this.baseReference = baseReference;
    this.locks = locks;
    this.inTest = inTest;
  }

  /**
   * Performs the security assertion based on the resourceLocation, from the
   * original request and the method begin attempted. Will throw a
   * SDataException with Forbidden if the resource location is outside the
   * configured range, or if permission is denied.
   * 
   * @see org.sakaiproject.sdata.tool.api.SecurityAssertion#check(java.lang.String,java.lang.String,
   *      java.lang.String)
   */
  public void check(String method, String resourceLocation)
      throws SDataException {

    if (inTest) {
      return;
    }
    if (!(baseLocation.length() == 0)
        && (resourceLocation == null || !resourceLocation
            .startsWith(baseLocation))) {
      log.info("Denied " + method + " on [" + resourceLocation
          + "] base mismatch [" + baseLocation + "]");
      throw new SDataException(HttpServletResponse.SC_FORBIDDEN,
          "Access Forbidden");
    }
    String resourceReference = baseReference
        + resourceLocation.substring(baseLocation.length());
    String resourceLock = getResourceLock(method);

    // TODO: implement security

    log.info("All Denied " + method + ":" + resourceLock + " on "
        + resourceLocation + " baseReference:[" + baseReference
        + "] baseLocation:[" + baseLocation + "]");
    throw new SDataAccessException(HttpServletResponse.SC_FORBIDDEN,
        "Access denied for operation " + method);
  }

  /**
   * Convert the HTTP Method into a lock.
   * 
   * @param method
   * @return
   */
  private String getResourceLock(String method) {
    return locks.get(method);
  }

}
