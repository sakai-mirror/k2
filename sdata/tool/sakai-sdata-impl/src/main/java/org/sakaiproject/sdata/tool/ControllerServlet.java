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

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.sdata.tool.api.Handler;
import org.sakaiproject.tool.api.Tool;

/**
 * <p>
 * Loads the a list of handlers as specified in in the init params. These may be
 * off the 2 forms. Either all the properties can be specified in a single init
 * property in web.xml. The name values, separated by = and the properties
 * separated by ;.
 * </p>
 * <p>
 * 
 * <pre>
 *       	 &lt;init-param&gt;
 *       &lt;param-name&gt;config&lt;/param-name&gt;
 *       &lt;param-value&gt; 
 *       1.classname=org.sakaiproject.sdata.tool.JCRDumper;
 *       1.baseurl=d;
 *       2.classname=org.sakaiproject.sdata.tool.json.JsonCHSHandler;
 *       2.baseurl=c;
 *       3.classname=org.sakaiproject.sdata.tool.json.JsonJcrHandler;
 *       3.baseurl=f;
 *       4.classname=org.sakaiproject.sdata.tool.json.JsonJcrUserStorageHandler;
 *       4.baseurl=p;
 *       5.classname=org.sakaiproject.sdata.tool.json.JsonCHSUserStorageHandler;
 *       5.baseurl=cp;
 *       6.classname=org.sakaiproject.sdata.tool.xmlrpc.XmlRpcCHSHandler;
 *       6.baseurl=xc;
 *       7.classname=org.sakaiproject.sdata.tool.xmlrpc.XmlRpcJcrHandler;
 *       7.baseurl=xf;
 *       8.classname=org.sakaiproject.sdata.tool.xmlrpc.XmlRpcJcrUserStorageHandler;
 *       8.baseurl=xp;
 *       9.classname=org.sakaiproject.sdata.tool.xmlrpc.XmlRpcCHSUserStorageHandler;
 *       9.baseurl=xcp;
 *       10.classname=org.sakaiproject.sdata.services.mcp.MyCoursesAndProjectsHandler;
 *       10.baseurl=mcp;
 *       11.classname=org.sakaiproject.sdata.services.mra.MyRecentChangesHandler;
 *       11.baseurl=mra;
 *       12.classname=org.sakaiproject.sdata.services.me.MeHandler;
 *       12.baseurl=me;
 *       13.classname=org.sakaiproject.sdata.services.mff.MyFileFinderHandler;
 *       13.baseurl=mff;
 *       14.classname=org.sakaiproject.sdata.services.motd.MessageOfTheDayHandler;
 *       14.baseurl=motd;
 *       15.classname=org.sakaiproject.sdata.services.mgs.MyGlobalSearchHandler;
 *       15.baseurl=mgs;
 *       16.classname=org.sakaiproject.sdata.services.site.SiteHandler;
 *       16.baseurl=site;
 *       &lt;/param-value&gt;
 *       &lt;/init-param&gt;
 *      
 * </pre>
 * 
 * </p>
 * <p>
 * or as individual properties starting with handler.
 * </p>
 * <p>
 * 
 * <pre>
 *      	 &lt;init-param&gt;
 *                    &lt;param-name&gt;handler.1.classname&lt;/param-name&gt;
 *                    &lt;param-value&gt;org.sakaiproject.sdata.tool.JCRDumper&lt;/param-value&gt;
 *         &lt;/init-param&gt;
 *     	 &lt;init-param&gt;
 *                    &lt;param-name&gt;handler.1.baseurl&lt;/param-name&gt;
 *                    &lt;param-value&gt;d&lt;/param-value&gt;
 *         &lt;/init-param&gt;
 * </pre>
 * 
 * </p>
 * <p>
 * Obviously the former is more compact.
 * </p>
 * <p>
 * When the servlet inits, it will create instances of the classes names in the
 * classname property and then register those against the baseurl property. When
 * processing a request, the path info will be examined and the first element of
 * the path will be used to match a selected handler on baseurl. The handler
 * will then be invoked for the method in question. If no handler is found then
 * a 404 will be sent back to the user.
 * </p>
 * <p>
 * There is an additional url /checkRunning that will respond with some sample
 * random data. This is used for unit testing. The size of the block can be set
 * with a x-testdata-size header in the request. This is limited to 4K maximum.
 * </p>
 * 
 * @author ieb
 */
public class ControllerServlet extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7098194528761855627L;

	private static final Log log = LogFactory.getLog(ControllerServlet.class);

	private Map<String, Handler> handlerRegister;

	/**
	 * Dummy handler used for all those requests that cant be matched.
	 */
	private Handler nullHandler = new 	Handler()
	{

		private Random r = new Random(System.currentTimeMillis());

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.sakaiproject.sdata.tool.api.Handler#doDelete(javax.servlet.http.HttpServletRequest,
		 *      javax.servlet.http.HttpServletResponse)
		 */
		public void doDelete(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException
		{
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.sakaiproject.sdata.tool.api.Handler#doGet(javax.servlet.http.HttpServletRequest,
		 *      javax.servlet.http.HttpServletResponse)
		 */
		public void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException
		{
			int size = 1024;
			try
			{
				size = Integer.parseInt(request.getHeader("x-testdata-size"));
			}
			catch (Exception ex)
			{

			}
			size = Math.min(4096, size);
			byte[] b = new byte[size];
			r.nextBytes(b);
			response.setContentType("application/octet-stream");
			response.setContentLength(b.length);
			response.setStatus(HttpServletResponse.SC_OK);
			response.getOutputStream().write(b);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.sakaiproject.sdata.tool.api.Handler#doHead(javax.servlet.http.HttpServletRequest,
		 *      javax.servlet.http.HttpServletResponse)
		 */
		public void doHead(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException
		{
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.sakaiproject.sdata.tool.api.Handler#doPost(javax.servlet.http.HttpServletRequest,
		 *      javax.servlet.http.HttpServletResponse)
		 */
		public void doPost(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException
		{
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.sakaiproject.sdata.tool.api.Handler#doPut(javax.servlet.http.HttpServletRequest,
		 *      javax.servlet.http.HttpServletResponse)
		 */
		public void doPut(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException
		{
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.sakaiproject.sdata.tool.api.Handler#init(java.util.Map)
		 */
		public void init(Map<String, String> config) throws ServletException
		{
		}
		
		public void destroy() 
		{
			
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.sakaiproject.sdata.tool.api.Handler#setHandlerHeaders(javax.servlet.http.HttpServletResponse)
		 */
		public void setHandlerHeaders(HttpServletRequest request, HttpServletResponse response)
		{
			response.setHeader("x-sdata-url", request.getPathInfo());
			response.setHeader("x-sdata-handler", this.getClass().getName());
		}

		public void sendError(HttpServletRequest request, HttpServletResponse response,
				Throwable ex) throws IOException
		{

		}

		public void sendMap(HttpServletRequest request, HttpServletResponse response,
				Map<String, Object> contetMap) throws IOException
		{

		}

	};

	/**
	 * Construct a Controller servlet
	 */
	public ControllerServlet()
	{
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException
	{
		String handlerName = null;
		try
		{
			try {
				super.init(config);
			} catch ( NullPointerException npe ) {
				log.info("NPE In super, in test mode this is ok ");
			}
			char[] buffer = new char[2048];
			handlerRegister = new HashMap<String, Handler>();
			String configLocation = config.getServletContext().getRealPath("/WEB-INF/config");
			File configLocationFile = new File(configLocation);
			File[] configFiles = configLocationFile.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".sdata");
				}
			});
			for ( File configFile : configFiles ) {
				log.info("++Start Loading definitions from "+configFile.getAbsolutePath());
				Map<String, Map<String, String>> configMap = loadConfigMap(configFile);
				for (String handler : configMap.keySet())
				{
					try {
						handlerName = configFile.getAbsolutePath()+":"+handler;
						Map<String, String> handlerConfig = configMap.get(handler);
						Class<?> c = (Class<?>) Class.forName(handlerConfig.get("classname"));
						Handler h = (Handler) c.newInstance();
						h.init(handlerConfig);
						String baseUrl = handlerConfig.get("baseurl");
						if ( handlerRegister.containsKey(baseUrl) ) {
							throw new ServletException("Duplicate Specifiation for  "+baseUrl+" mapped to "+handlerRegister.get(baseUrl)+" would have been overwritten by "+h);
						} else {
							handlerRegister.put(baseUrl, h);
						}
					}
					catch (ClassNotFoundException e)
					{
						log.info("-- Ignored "+handler+" from, ClassNotFound , config:"+configFile.getAbsolutePath()+" "+e.getMessage());						
					}
				}
				log.info("--Done Loading definitions from "+configFile.getAbsolutePath());
			}
		}
		catch (InstantiationException e)
		{
			throw new ServletException("Failed to instance handler " + handlerName, e);
		}
		catch (IllegalAccessException e)
		{
			throw new ServletException("Failed to instance handler " + handlerName, e);
		} 
		catch (IOException e) 
		{
			throw new ServletException("Failed to instance handler " + handlerName, e);
		}
	}
	
	@Override
	public void destroy() {
		for ( Handler h : handlerRegister.values()) {
			h.destroy();
		}
		super.destroy();
	}


	/**
	 * Load the configuration map from a single string that is parsed
	 * 
	 * @param config configuration in a string
	 * @return a map of all configuration properties
	 * @throws IOException 
	 */
	private Map<String, Map<String, String>> loadConfigMap(File configFile) throws IOException
	{
		
		Properties p = new Properties();
		InputStream in = new FileInputStream(configFile);
		p.load(in);
		in.close();
		
		Map<String, Map<String, String>> configMap = new HashMap<String, Map<String, String>>();
		for (Object k : p.keySet())
		{
			String name = String.valueOf(k).trim();
			String value = String.valueOf(p.get(k)).trim();
			int endkey = name.indexOf(".");

			String key = name.substring(0, endkey);
			String valuekey = name.substring(endkey + 1);
			if (log.isDebugEnabled())
			{
				log.debug("Config Data Adding Key[" + key + "] [" + endkey
						+ "] keyValue[" + valuekey + "] Value[" + value + "]");
			}
			Map<String, String> handlerConfig = configMap.get(key);
			if (handlerConfig == null)
			{
				handlerConfig = new HashMap<String, String>();
				configMap.put(key, handlerConfig);
			}
			handlerConfig.put(valuekey, value);
		}
		return configMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doDelete(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		Handler h = getHandler(request);
		if (h != null)
		{
			h.setHandlerHeaders(request, response);
			h.doDelete(request, response);
		}
		else
		{
			response.reset();
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "No Handler Found");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		Handler h = getHandler(request);
		if (h != null)
		{
			h.setHandlerHeaders(request, response);
			h.doGet(request, response);
		}
		else
		{
			response.reset();
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "No Handler Found");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doHead(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doHead(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		Handler h = getHandler(request);
		if (h != null)
		{
			h.setHandlerHeaders(request, response);
			h.doHead(request, response);
		}
		else
		{
			response.reset();
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "No Handler Found");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		Handler h = getHandler(request);
		if (h != null)
		{
			h.setHandlerHeaders(request, response);
			h.doPost(request, response);
		}
		else
		{
			response.reset();
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "No Handler Found");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		Handler h = getHandler(request);
		if (h != null)
		{
			h.setHandlerHeaders(request, response);
			h.doPut(request, response);
		}
		else
		{
			response.reset();
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "No Handler Found");
		}
	}

	/**
	 * Get the handler mapped to a request path.
	 * 
	 * @param request
	 * @return
	 */
	public Handler getHandler(HttpServletRequest request)
	{
		request.setAttribute(Tool.NATIVE_URL, Tool.NATIVE_URL);
		String pathInfo = request.getPathInfo();
		if (log.isDebugEnabled())
		{
			log.debug("Path is " + pathInfo);
		}
		if ("/checkRunning".equals(pathInfo))
		{
			return nullHandler;
		}
		if (pathInfo == null) return null;

		char[] path = request.getPathInfo().trim().toCharArray();
		if (path.length < 1) return null;
		int start = 0;
		if (path[0] == '/')
		{
			start = 1;
		}
		int end = start;
		for (; end < path.length && path[end] != '/'; end++);
		String key = new String(path, start, end - start);
		return handlerRegister.get("/" + key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		long start = System.currentTimeMillis();
		super.service(req, resp);
		log.info((System.currentTimeMillis() - start) + " ms " + req.getMethod() + ":"
				+ req.getRequestURL());
	}

}
