package com.h4313.deephouse.server.webServices;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;

import org.json.JSONObject;

import com.h4313.deephouse.exceptions.DeepHouseException;
import com.h4313.deephouse.housemodel.House;

@Path("/rest")
@Consumes("application/json")
@Produces("application/json")
/*
 * !Important! Defines getters and setters domain class NO methods with no
 * params starting by get... (WebSevice thinks is a getter)
 */
public class DeepHouseServicesImpl implements DeepHouseServices {

	@Override
	@POST
	@Path("/addSensor")
	public void addSensor(JSONObject json) throws DeepHouseException {
		House.getInstance().addSensor(json);
	}

	@Override
	@POST
	@Path("/addActuator")
	public void addActuator(JSONObject json) throws DeepHouseException {
		House.getInstance().addActuator(json);
	}

	@GET
	@Path("/houseModel")
	@Override
	public House getHouse() {
		/*
		 * testing House h = House.getInstance(); Room r = h.getRooms().get(0);
		 * r.addActuator("aa", ActuatorType.RADIATOR); r.addSensor("aaaaaa",
		 * SensorType.TEMPERATURE); r.establishConnections();
		 * r.printInformations();
		 */
		return House.getInstance();
	}

	@Override
	@PUT
	@Path("/userAction")
	public void userAction(JSONObject json) throws DeepHouseException {
		House.getInstance().userAction(json);
	}

}
