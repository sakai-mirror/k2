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
public class JCRUserStorageHandler extends JCRHandler
{

  private static final String BASE_NAME = "jcruserhandler";

  public static final String BASE_PATH = BASE_NAME + ".basePath";

  public static final String BASE_URL = BASE_NAME + ".baseURL";

  public static final String RESOURCE_DEFINITION_FACTORY = BASE_NAME
      + ".resourceDefinitionFactory";

  public static final String RESOURCE_FUNCTION_FACTORY = BASE_NAME
      + ".resourceFuntionFactory";

  public static final String RESOURCE_SERIALIZER = BASE_NAME + "resourceSerialzer";


	/**
   * @param basePath
   * @param baseUrl
   * @param jcrNodeFactory
   * @param resourceDefinitionFactory
   * @param resourceFunctionFactory
   */
  @Inject
  public JCRUserStorageHandler(@Named(BASE_PATH) String basePath,
      @Named(BASE_URL) String baseUrl, JCRNodeFactoryService jcrNodeFactory,
      @Named(RESOURCE_DEFINITION_FACTORY) ResourceDefinitionFactory resourceDefinitionFactory,
      @Named(RESOURCE_FUNCTION_FACTORY) Map<String, SDataFunction> resourceFunctionFactory,
      @Named(RESOURCE_SERIALIZER) HandlerSerialzer serializer) {
    super(basePath, baseUrl, jcrNodeFactory, resourceDefinitionFactory,
        resourceFunctionFactory,serializer);
  }

}
