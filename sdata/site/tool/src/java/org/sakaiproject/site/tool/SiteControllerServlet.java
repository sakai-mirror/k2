package org.sakaiproject.site.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.jcr.api.JCRConstants;
import org.sakaiproject.jcr.support.api.JCRNodeFactoryService;
import org.sakaiproject.jcr.support.api.JCRNodeFactoryServiceException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.Kernel;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import java.net.URLEncoder;

public class SiteControllerServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 0L;
	
	private static final Log log = LogFactory.getLog(SiteControllerServlet.class);
	
	private org.sakaiproject.site.api.SiteService siteservice = Kernel.siteService();
	
	private JCRNodeFactoryService jcrNodeFactory;
	

	public SiteControllerServlet()
	{
		jcrNodeFactory = Kernel.jcrNodeFactoryService();
	}
	
	public void destroy() 
	{
		
	}
	
	public void doDelete(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException{
		
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException
	{
		try {
			
			response.setContentType("text/html");
			
			int level = request.getPathInfo().split("/").length;
			
			String site = request.getPathInfo().split("/")[1];
			site = java.net.URLDecoder.decode(site);
			
			Site csite = null;
			try {
				csite = SiteService.getSite(site);
			} catch (IdUnusedException e1) {
				//response.sendError(HttpServletResponse.SC_NOT_FOUND);
				//e1.printStackTrace();
			}
			
			boolean loggedIn = true;
			
			if (csite == null || ! csite.isAllowed(Kernel.sessionManager().getCurrentSessionUserId(), "site.visit")){
				if (Kernel.sessionManager().getCurrentSessionUserId() == null){
					loggedIn = false;
				} else {
					loggedIn = false;
				}
			}
			
			Node n = null;
			try {
				n = jcrNodeFactory.getNode("/sakai/sdata/" + site);
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (JCRNodeFactoryServiceException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (Exception ex){
				//ex.printStackTrace();
			}
			if (n == null) {
				loggedIn = false;
			}
			
			Node pages = null;
			try {
				pages = n.getNode("pages");
			} catch (PathNotFoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (Exception ex){
				//ex.printStackTrace();
			}
			
			Node config = null;
			try {
				config = n.getNode("pageconfiguration");
			} catch (PathNotFoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (Exception e){
				//e.printStackTrace();
			}

			String menu = "<br/>";
			
			if (config != null){
				try {
					
					Node resource = config.getNode(JCRConstants.JCR_CONTENT);
					Property content = resource.getProperty(JCRConstants.JCR_DATA);
					
					String json = content.getString();
					JSONObject obj = JSONObject.fromObject(json);
					JSONArray items = obj.getJSONArray("items");
					menu += "Top pages:<br/><br/>";
					for (int i = 0; i < items.size(); i++){
						JSONObject det = (JSONObject) items.get(i);
						if (det.getBoolean("top")){
							if (request.getPathInfo().split("/").length > 2 && det.getString("id").equals(request.getPathInfo().split("/")[2])){
								menu += "<a style='margin-left:10px;text-decoration:none;color:#000000' href='/site/" + site + "/" + det.getString("id") + "'>" + det.getString("title") + "</a><br/>";
							} else {
								menu += "<a style='margin-left:10px' href='/site/" + site + "/" + det.getString("id") + "'>" + det.getString("title") + "</a><br/>";
							}
						}
					}
					menu += "<br/>Non-top pages:<br/><br/>";
					for (int i = 0; i < items.size(); i++){
						JSONObject det = (JSONObject) items.get(i);
						if (det.getBoolean("top") == false){
							if (request.getPathInfo().split("/").length > 2 && det.getString("id").equals(request.getPathInfo().split("/")[2])){
								menu += "<a style='margin-left:10px;text-decoration:none;color:#000000' href='/site/" + site + "/" + det.getString("id") + "'>" + det.getString("title") + "</a><br/>";
							} else {
								menu += "<a style='margin-left:10px' href='/site/" + site + "/" + det.getString("id") + "'>" + det.getString("title") + "</a><br/>";
							}
						}
					}
					
				} catch (PathNotFoundException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				} catch (RepositoryException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				} catch (Exception ex){
					//ex.printStackTrace();
				}
			}
					
			try {
			
				if (true) {
					
					//log.error(level);

					if (level == 2){
					
						//log.error("IK BEN HIER !!!!!!!!!!!");
						
						PrintWriter writer = response.getWriter();
						
						String noscript = "<div class='container_child'><noscript>Note: Please turn on JavaScript to enjoy all of the features of Sakai. You are seeing a degraded version of this page right now.</noscript></div>";
						
						
						File f= new File(request.getRealPath("") + "/../dev/site.html");						
						BufferedReader read = new BufferedReader(new FileReader(f));
						
						String s = read.readLine();
						while (s != null){
							if (loggedIn == true){
								
								log.error("++++++++++++++++++ = " + s.toLowerCase());
								
								if (s.toLowerCase().indexOf("<div id=\"initialcontent\">") != -1){
									String toInsert = "<h1>" + csite.getTitle() + "</h1><hr/>";
									toInsert += noscript;
									toInsert += menu;
									s = s.replace("<div id=\"initialcontent\">", "<div id=\"initialcontent\">" + toInsert);
								}
								/*
								if (s.toLowerCase().indexOf("<div id=\"sidebar-content-pages\" class=\"sidebar-content\">") != -1){
									s = s.replace("<div id=\"sidebar-content-pages\" class=\"sidebar-content\">", "<div id=\"sidebar-content-pages\" class=\"sidebar-content\">" + menu);
								} else if (s.toLowerCase().indexOf("<div id=\"container\">") != -1){
									s = s.replace("<div id=\"container\">", "<div id=\"container\">" + noscript);
								} else if (s.toLowerCase().indexOf("<h1 id=\"sitetitle\">") != -1){
									s = s.replace("<h1 id=\"sitetitle\">", "<h1 id=\"sitetitle\">" + csite.getTitle());
								}
								*/
							}
							writer.write(s);
							s = read.readLine();
						}
						
					} else if (level == 3){
						
						Node n1 = pages.getNode(request.getPathInfo().split("/")[2]);
						Node n2 = n1.getNode("content");
						Node resource = n2.getNode(JCRConstants.JCR_CONTENT);
						Property content = resource
							.getProperty(JCRConstants.JCR_DATA);
						
						PrintWriter writer = response.getWriter();
						
						File f= new File(request.getRealPath("") + "/../dev/site.html");						
						BufferedReader read = new BufferedReader(new FileReader(f));
						
						String noscript = "<div class='container_child'><noscript>Note: Please turn on JavaScript to enjoy all of the features of CamTools. You are seeing a degraded version of this page right now.</noscript>";
						noscript += "<br/><hr/>";
						noscript += content.getString() + "</div>";
						
						String s = read.readLine();
						while (s != null){
							if (loggedIn == true){
								/*
								if (s.toLowerCase().indexOf("<div id=\"sidebar-content-pages\" class=\"sidebar-content\">") != -1){
									s = s.replace("<div id=\"sidebar-content-pages\" class=\"sidebar-content\">", "<div id=\"sidebar-content-pages\" class=\"sidebar-content\">" + menu);
								} else if (s.toLowerCase().indexOf("<div id=\"container\">") != -1){
									s = s.replace("<div id=\"container\">", "<div id=\"container\">" + noscript);
								} else if (s.toLowerCase().indexOf("<h1 id=\"sitetitle\">") != -1){
									s = s.replace("<h1 id=\"sitetitle\">", "<h1 id=\"sitetitle\">" + csite.getTitle());
								}
								*/
							}
							if (s.toLowerCase().indexOf("<div id=\"initialcontent\">") != -1){
								String toInsert = "<h1>" + csite.getTitle() + "</h1><hr/>";
								toInsert += menu;
								toInsert += "<hr/>";
								toInsert += noscript;
								s = s.replace("<div id=\"initialcontent\">", "<div id=\"initialcontent\">" + toInsert);
							}
							writer.write(s);
							if (s.indexOf("<body") != -1){
								writer.write("<script type='text/javascript' language='JavaScript'>");
								writer.write("document.location = '/site/" + site + "#" + request.getPathInfo().split("/")[2] + "'");
								writer.write("</script>");
							}
							s = read.readLine();
						}
						
					}
					
				}
			} catch (Exception ex){
				//ex.printStackTrace();
			}
		} catch (Exception ex){
			//ex.printStackTrace();
		}
		
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException
	{

	}
	
	public void doHead(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException
	{

	}
	
	public void doPut(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException
	{

	}
	
	public void init(Map<String, String> config) throws ServletException
	{
		jcrNodeFactory = Kernel.jcrNodeFactoryService();
	}
	
}
