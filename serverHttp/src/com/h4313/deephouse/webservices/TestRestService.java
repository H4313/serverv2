package com.h4313.deephouse.webservices;

import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/TestRestService")
public class TestRestService {

	@GET()
	@Produces("text/plain")
	public String sayHello() {
	    return "Hello World!";
	}
}
