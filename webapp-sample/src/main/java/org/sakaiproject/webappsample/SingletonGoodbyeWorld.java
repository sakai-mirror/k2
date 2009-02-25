package org.sakaiproject.webappsample;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/singletongoodbye")
public class SingletonGoodbyeWorld {
	@GET
	@Path("/greeting")
	public String getGreeting() {
		return "This is goodbye from a singleton resource, located in a webapp";
	}
}
