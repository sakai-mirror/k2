package org.sakaiproject.webappsample;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/prototypegoodbye")
public class PrototypeGoodbyeWorld {
	@GET
	@Path("/greeting")
	public String getGreeting() {
		return "This is goodbye from a request-per-object resource, located in a webapp";
	}
}
