package com.h4313.deephouse.server.webServices;

import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.json.JSONException;
import org.json.JSONObject;

import com.h4313.deephouse.actuator.ActuatorType;
import com.h4313.deephouse.exceptions.DeepHouseException;
import com.h4313.deephouse.exceptions.DeepHouseFormatException;
import com.h4313.deephouse.housemodel.House;
import com.h4313.deephouse.housemodel.Room;
import com.h4313.deephouse.sensor.SensorType;
import com.h4313.deephouse.util.DecToHexConverter;
import com.h4313.deephouse.util.DeepHouseCalendar;

@Path("/rest")
@Consumes("application/json")
@Produces("application/json")
/*
 * !Important! Defines getters and setters domain class NO methods with no
 * params starting by get... (WebSevice thinks is a getter)
 * Documentation : RESTFul Web Services http://docs.oracle.com/javaee/6/tutorial/doc/gilik.html
 */
public class DeepHouseServicesImpl implements DeepHouseServices {

	@GET
	@Path("/connect")
	@Override
	public String connect() throws DeepHouseException {
		// Initialisation horloge
		DeepHouseCalendar.getInstance().init();
		
		// Initialisation de la maison // TODO : RETIRER POUR LA PRODUCTION
		try
		{
			List<Room> rooms = House.getInstance().getRooms();
			int id = 0;
			for(Room room : rooms)
			{
				room.addSensor(DecToHexConverter.decToHex(id++), SensorType.TEMPERATURE);
				room.addSensor(DecToHexConverter.decToHex(id++), SensorType.WINDOW);
				room.addSensor(DecToHexConverter.decToHex(id++), SensorType.LIGHT);
				room.addSensor(DecToHexConverter.decToHex(id++), SensorType.PRESENCE);

				room.addActuator(DecToHexConverter.decToHex(id++), ActuatorType.LIGHTCONTROL);
				//room.addActuator(DecToHexConverter.decToHex(id++), ActuatorType.AIRCONDITION);
				room.addActuator(DecToHexConverter.decToHex(id++), ActuatorType.RADIATOR);
				room.addActuator(DecToHexConverter.decToHex(id++), ActuatorType.WINDOWCLOSER);
				
				// room.connectSensorActuator(sensorId, actuatorId);
				
				room.establishConnections();
			}
			System.out.println("Done init");
			for(Room room : rooms){
				room.printInformations();
			}
		} 
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		String response = null;
		try {
			response = new JSONObject().put("success", true).toString();
		} catch (JSONException e) {
			throw new DeepHouseFormatException("Json response error : " + e.getMessage());
		}
		
		return response;
	}
	
	@Override
	@POST
	@Path("/addSensor")
	@Consumes("application/x-www-form-urlencoded")
	public String addSensor(@FormParam("piece") String piece, 
			@FormParam("capteur") String idCapteur,
			@FormParam("type") String type) throws DeepHouseException {
		
		House.getInstance().addSensor(Integer.valueOf(piece), idCapteur, type);
		
		String response = null;
		try {
			response = new JSONObject().put("success", true).toString();
		} catch (JSONException e) {
			throw new DeepHouseFormatException("Json response error : " + e.getMessage());
		}
		
		return response;
	}

	@Override
	@POST
	@Path("/addActuator")
	@Consumes("application/x-www-form-urlencoded")
	public String addActuator(@FormParam("piece") String piece, 
			@FormParam("actionneur") String idActionneur,
			@FormParam("type") String type) throws DeepHouseException {
		
		House.getInstance().addActuator(Integer.valueOf(piece), idActionneur, type);
		
		String response = null;
		try {
			response = new JSONObject().put("success", true).toString();
		} catch (JSONException e) {
			throw new DeepHouseFormatException("Json response error : " + e.getMessage());
		}
		
		return response;
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
	@Consumes("application/x-www-form-urlencoded")
	public String userAction(@FormParam("piece") String piece, 
			@FormParam("typeAction")String typeAction, 
			@FormParam("valeur") String valeur, 
			@FormParam("idActionneur") String idActionneur) throws DeepHouseException {
		
		House.getInstance().userAction(Integer.valueOf(piece), typeAction, valeur, idActionneur);
		
		String response = null;
		try {
			response = new JSONObject().put("success", true).toString();
		} catch (JSONException e) {
			throw new DeepHouseFormatException("Json response error : " + e.getMessage());
		}
		
		return response;
	}

}
