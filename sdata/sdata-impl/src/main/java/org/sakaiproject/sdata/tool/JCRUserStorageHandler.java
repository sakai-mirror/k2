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


package org.sakaiproject.sdata.tool;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.sdata.tool.api.HandlerSerialzer;
import org.sakaiproject.sdata.tool.api.ResourceDefinitionFactory;
import org.sakaiproject.sdata.tool.api.SDataFunction;

import java.util.Map;

/**
 * A user storage servlet performs storage based on the logged in user, as
 * defined by the Sakai session. It uses the UserResourceDefinitionFactory to
 * locate the location of the users storage within the underlying jcr
 * repository. This servlet extends the JCRServlet and uses its methods and
 * handling to respond to the content.
 * 
 * @author ieb
 */
public class JCRUserStorageHandler extends JCRHandler {

  /**
   * 
   */
  private static final long serialVersionUID = -7527973143563221845L;

  private static final String BASE_NAME = "jcruserhandler";

  public static final String BASE_REPOSITORY_PATH = BASE_NAME
      + ".baseRepositoryPath";

  public static final String BASE_URL = BASE_NAME + ".baseURL";

  public static final String HANDLER_KEY = BASE_NAME + ".handlerKey";

  public static final String RESOURCE_DEFINITION_FACTORY = BASE_NAME
      + ".resourceDefinitionFactory";

  public static final String RESOURCE_FUNCTION_FACTORY = BASE_NAME
      + ".resourceFuntionFactory";

  public static final String RESOURCE_SERIALIZER = BASE_NAME
      + ".resourceSerialzer";

  public static final String SECURITY_ASSERTION = BASE_NAME
      + ".securityAssertion";

  public static final String FUNCTION_CREATEFOLDER = BASE_NAME
      + ".function.createfolder";
  public static final String FUNCTION_MOVE = BASE_NAME + ".function.move";
  public static final String FUNCTION_NODE = BASE_NAME + ".function.node";
  public static final String FUNCTION_PERMISSION = BASE_NAME
      + ".function.permission";
  public static final String FUNCTION_PROPERTIES = BASE_NAME
      + ".function.properties";
  public static final String FUNCTION_TAG = BASE_NAME + ".function.tag";
  public static final String FUNCTION_HIDE_RELEASE = BASE_NAME
      + ".function.hiderelease";

  public static final String LOCK_DEFINITION = BASE_NAME + ".lockDefinition";

  /**
   * Construct a JCRUserStorageHandler, and use a Resource Definition factory to
   * translate the request URL into the repository location.
   * 
   * @param jcrNodeFactory
   * @param resourceDefinitionFactory
   * @param resourceFunctionFactory
   */
  @Inject
  public JCRUserStorageHandler(
      JCRNodeFactoryService jcrNodeFactory,
      @Named(RESOURCE_DEFINITION_FACTORY) ResourceDefinitionFactory resourceDefinitionFactory,
      @Named(RESOURCE_FUNCTION_FACTORY) Map<String, SDataFunction> resourceFunctionFactory,
      @Named(RESOURCE_SERIALIZER) HandlerSerialzer serializer) {
    super(jcrNodeFactory, resourceDefinitionFactory, resourceFunctionFactory,
        serializer);
    
    System.err.println(this+" Resource Defintion Factory is "+resourceDefinitionFactory);
  }

}
