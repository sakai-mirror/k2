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

package org.sakaiproject.sdata.tool.api;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ieb
 *
 */
public interface HandlerSerialzer {
	/**
	 * Sends an error to the client
	 * 
	 * @param ex
	 * @throws IOException
	 */
	void sendError(Handler handler, HttpServletRequest request, HttpServletResponse response, Throwable ex)
			throws IOException;

	/**
	 * Serailize a Map strucutre to the output stream
	 * 
	 * @param uploads
	 * @throws IOException
	 */
	void sendMap( HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> contetMap) throws IOException;

}
