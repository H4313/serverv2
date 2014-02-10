package com.h4313.deephouse.webservices;

import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.h4313.deephouse.dao.DAO;
import com.h4313.deephouse.dao.RoomDAO;
import com.h4313.deephouse.housemodel.House;
import com.h4313.deephouse.housemodel.Room;

@Path("/TestRestService")
public class TestRestService {

	@GET()
	@Produces("text/plain")
	@Path("/1")
	public String sayHello() {
		  DAO<Room> roomDao = new RoomDAO();
	    return "Hello World!";
	}
	
	@GET()
	@Produces("text/plain")
	@Path("/2")
	public String sayHello1() {
	    return "Hello World!  2";
	}
}
