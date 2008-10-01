package org.sakaiproject.osgi.sample.simplewebapp.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloOsgiWorldServlet  extends HttpServlet {
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		addHelloWorld(resp, req.getMethod());
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		addHelloWorld(resp, req.getMethod());
	}

	public String getServletInfo() {
		return "Simple Osgi World Servlet";
	}

	private void addHelloWorld(HttpServletResponse response, String method)
	throws IOException {
		response.setContentType("text/html");

		ServletOutputStream out = response.getOutputStream();
		out.println("<html>");
		out.println("<head><title>Hello Osgi World</title></head>");
		out.println("<body>");
		out.println("<h1>Hello OSGi World</h1>");
		out.println("<h2>http method used:" + method + "</h2>");
		out.println("</body></html>");
	}
}
