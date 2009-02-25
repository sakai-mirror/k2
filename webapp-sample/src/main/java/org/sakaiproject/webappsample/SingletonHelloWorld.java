package org.sakaiproject.webappsample;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

@Path("/singletonhello")
public class SingletonHelloWorld {
	@GET
	@Path("/greeting")
	public String getGreeting() {
		return "This is hello from a singleton resource, located in a webapp";
	}
	
	@GET
	@Path("/model.json")
	@Produces(MediaType.APPLICATION_JSON)
	public MyModel getModelAsJson() {
		return new MyModel();
	}

	@GET
	@Path("/model.xml")
	@Produces(MediaType.APPLICATION_XML)
	public MyModel getModelAsXml() {
		return new MyModel();
	}

	@GET
	@Path("/stream")
	public InputStream getModelAsStream() {
		String s = "{ \"keyFromJcr\" : \"valueFromJcr\" }";
		try {
			return new ByteArrayInputStream(s.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new WebApplicationException(e);
		}
	}
	
	/**
	 * A sample object to be returned by the methods in this resource
	 */
	public class MyModel {

		public MyModel() {
			d = new Date();
			s = "A MyModel delivered via a JAX-RS resource";
			l = Long.MAX_VALUE;
		}

		protected String s;
		protected Date d;
		protected Long l;
		
		public String getS() {
			return s;
		}
		public void setS(String s) {
			this.s = s;
		}
		public Date getD() {
			return d;
		}
		public void setD(Date d) {
			this.d = d;
		}
		public Long getL() {
			return l;
		}
		public void setL(Long l) {
			this.l = l;
		}
	}
}
