package com.h4313.deephouse.server.webServices;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.json.JSONObject;

import com.h4313.deephouse.exceptions.DeepHouseException;

@Path("/rest")
public class DeepHouseServicesImpl implements DeepHouseServices {

	//@GET()
	//@Produces("text/plain")
	//@Path("/1")
	@Override
	public void addSensor(JSONObject json) throws DeepHouseException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addActuator(JSONObject json) throws DeepHouseException {
		// TODO Auto-generated method stub

	}

	@Override
	public void getHouse() throws DeepHouseException {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 @GET()
	@Produces("text/plain")
	@Path("/2")
	public String sayHello1() {
	    return "Hello World!  2";
	} 
	 */

}
