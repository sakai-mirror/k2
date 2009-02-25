package org.sakaiproject.webappsample;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/prototypehello")
public class PrototypeHelloWorld {
	@GET
	@Path("/greeting")
	public String getGreeting() {
		return "This is hello from a request-per-object resource, located in a webapp";
	}

}
