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

package org.sakaiproject.resteasy;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.json.JSONObject;

/**
 * Provides JSON serialization for arbitrary pojos returned by JAX-RS annotated
 * resources as long as no {@link XmlRootElement} or {@link XmlType} annotations
 * are present.
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class DefaultJsonProvider implements MessageBodyWriter<Object> {
	private static final Log log = LogFactory.getLog(DefaultJsonProvider.class);

	public long getSize(Object model, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		// TODO Cache in Threadlocal if this duplicate serialization becomes problematic
		try {
			return JSONObject.fromObject(model).toString().getBytes("UTF-8").length;
		} catch (UnsupportedEncodingException e) {
			log.error(e);
			return 0;
		}
	}

	public boolean isWriteable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return !type.isAnnotationPresent(XmlRootElement.class);
	}

	public void writeTo(Object model, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> headers, OutputStream os)
			throws IOException, WebApplicationException {
		JSONObject jsonObject = JSONObject.fromObject(model);
		os.write(jsonObject.toString().getBytes("UTF-8"));
	}
}
