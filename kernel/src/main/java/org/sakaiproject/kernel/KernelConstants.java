/*
 * Licensed to the Sakai Foundation (SF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The SF licenses this file
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

package org.sakaiproject.kernel;

/**
 * Holds the configuration properties that are used in the kernel.
 */
public class KernelConstants {

  public static final String JCR_USERENV_BASE = "jcruserenv.base";
  public static final String JCR_USERENV_TEMPLATES = "jcruserenv.templates";
  public static final String JCR_DEFAULT_TEMPLATE = "jcruserenv.templates.default";
  public static final String JCR_PROFILE_TEMPLATES = "jcrprofile.templates";
  public static final String JCR_PROFILE_DEFAUT_TEMPLATES = "jcrprofile.templates.default";
  /**
   * The property name defining the shared private data
   */
  public static final String PRIVATE_SHARED_PATH_BASE = "jcrprivateshared.base";
  /**
   * The property name defining the data that is completely private to the user
   */
  public static final String PRIVATE_PATH_BASE = "jcrprivate.base";
  /**
   * Setting: The time to live of User Env objects the local cache, this should
   * be set in the kernel properties file.
   */
  public static final String TTL = "userenv.ttl";

  public static final String PROP_ANON_ACCOUNTING = "rest.user.anonymous.account.creation";
  public static final String ENTITY_MANAGER_SCOPE = "jpa.entitymanager.scope";


  public static final String JDBC_DRIVER_NAME = "jdbc.driver";
  public static final String JDBC_URL = "jdbc.url";
  public static final String JDBC_USERNAME = "jdbc.username";
  public static final String JDBC_PASSWORD = "jdbc.password";
  public static final String JDBC_VALIDATION_QUERY = "jdbc.validation";
  public static final String JDBC_DEFAULT_READ_ONLY = "jdbc.defaultReadOnly";
  public static final String JDBC_DEFAULT_AUTO_COMMIT = "jdbc.defaultAutoCommit";
  public static final String JDBC_DEFAULT_PREPARED_STATEMENTS = "jdbc.defaultPreparedStatement";
  public static final String TRANSACTION_TIMEOUT_SECONDS = "transaction.timeoutSeconds";
  public static final String DB_MIN_WRITE = "eclipselink.write.min";
  public static final String DB_MIN_NUM_READ = "eclipselink.read.min";
  public static final String DB_UNITNAME = "jpa.unitname";
  public static final String SESSION_COOKIE = "http.global.cookiename";





  public static final String GROUP_FILE_NAME = "groupdef.json";
  /**
   * The Name of the userenv file in the system.
   */
  public static final String USERENV = "userenv";

  /**
   * The name of the profile file.
   */
  public static final String PROFILE_JSON = "profile.json";

  /**
   * The name of the friends file
   */
  public static final String FRIENDS_FILE = "friends.json";


  /**
   * Attribute used in the session to store a list of group memberships.
   */
  public static final String GROUPMEMBERSHIP = "userenv.grouplist";
  public static final String NULLUSERENV = "userenv.null";


  public static final String SUBJECT_PROVIDER_REGISTRY = "subjectstatement.provider";
  /**
   * The name of the registry used for this type of service.
   */
  public static final String AUTHENTICATION_PROVIDER_REGISTRY = "authentication.provider.registry";
  public static final String MANAGER_PROVIDER_REGISTRY = "authentication.manager.provider.registry";
  /**
   * The name of the registry used for this type of service.
   */
  public static final String USER_PROVIDER_REGISTRY = "user.provider.registry";

  public static final String JSON_CLASSMAP = "jsonconverter.classmap";

  public static final String OUTBOX = "outbox";

}
