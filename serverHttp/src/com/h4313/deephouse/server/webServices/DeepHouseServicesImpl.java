package com.h4313.deephouse.server.webServices;

import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.h4313.deephouse.actuator.Actuator;
import com.h4313.deephouse.actuator.ActuatorType;
import com.h4313.deephouse.adapter.ActuatorAdapter;
import com.h4313.deephouse.adapter.RoomAdapter;
import com.h4313.deephouse.adapter.SensorAdapter;
import com.h4313.deephouse.dao.HouseDAO;
import com.h4313.deephouse.exceptions.DeepHouseException;
import com.h4313.deephouse.housemodel.House;
import com.h4313.deephouse.housemodel.Room;
import com.h4313.deephouse.sensor.Sensor;
import com.h4313.deephouse.sensor.SensorType;
import com.h4313.deephouse.util.DecToHexConverter;
import com.h4313.deephouse.util.DeepHouseCalendar;

@Path("/rest")
//@Consumes("application/json")
@Produces("application/json")
/*
 * !Important! Defines getters and setters domain class NO methods with no
 * params starting by get... (WebSevice thinks is a getter) Documentation :
 * RESTFul Web Services http://docs.oracle.com/javaee/6/tutorial/doc/gilik.html
 */
public class DeepHouseServicesImpl implements DeepHouseServices {

	private HouseDAO houseDAO;

	public DeepHouseServicesImpl() throws DeepHouseException {
		houseDAO = new HouseDAO();
		House.initInstance(houseDAO);

		House h = House.getInstance();
		h.printInformations();

		// h.addActuator(0, "Haha1", ActuatorType.DOORCONTROL.name());
		// h.printInformations();

		// houseDAO.createUpdate(h);

	}

	@GET
	@Path("/connect")
	@Override
	public String connect() {
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
				room.addSensor(DecToHexConverter.decToHex(id++), SensorType.DOOR);
				room.addSensor(DecToHexConverter.decToHex(id++), SensorType.FLAP);
				room.addSensor(DecToHexConverter.decToHex(id++), SensorType.PRESENCE);

				room.addActuator(DecToHexConverter.decToHex(id++), ActuatorType.RADIATOR);
				room.addActuator(DecToHexConverter.decToHex(id++), ActuatorType.WINDOWCLOSER);
				room.addActuator(DecToHexConverter.decToHex(id++), ActuatorType.LIGHTCONTROL);
				room.addActuator(DecToHexConverter.decToHex(id++), ActuatorType.DOORCONTROL);
				room.addActuator(DecToHexConverter.decToHex(id++), ActuatorType.FLAPCLOSER);
				
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
		
		try {
			return getSuccessJSONString();
		} catch (Exception e) {
			return getErrorJSONString(e);
		}
	}

	@Override
	@POST
	@Path("/addSensor")
	@Consumes("application/x-www-form-urlencoded")
	/* example type = SensorType.DOOR.name(); */
	public String addSensor(@FormParam("piece") String piece,
			@FormParam("capteur") String idCapteur,
			@FormParam("type") String type) {

		try {

			House.getInstance().addSensor(Integer.valueOf(piece), idCapteur,
					type);
			return getSuccessJSONString();
		} catch (Exception e) {
			return getErrorJSONString(e);
		}
	}

	@Override
	@POST
	@Path("/addActuator")
	@Consumes("application/x-www-form-urlencoded")
	/* example type = ActuatorType.DOORCONTROL.name(); */
	public String addActuator(@FormParam("piece") String piece,
			@FormParam("actionneur") String idActionneur,
			@FormParam("type") String type) {

		try {
			House.getInstance().addActuator(Integer.valueOf(piece),
					idActionneur, type);
			return getSuccessJSONString();
		} catch (Exception e) {
			return getErrorJSONString(e);
		}
	}

	@GET
	@Path("/houseModel")
	@Override
	public String getHouse() {
		
		final GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Room.class, new RoomAdapter());
		builder.registerTypeAdapter(Sensor.class, new SensorAdapter());
		builder.registerTypeAdapter(Actuator.class, new ActuatorAdapter());
//	    builder.excludeFieldsWithoutExposeAnnotation();
	    final Gson gson = builder.create();
	 
		String json = gson.toJson(House.getInstance());
		
		return json;
	}

	@Override
	@PUT
	@Path("/userAction")
	@Consumes("application/x-www-form-urlencoded")
	public String userAction(@FormParam("piece") String piece,
			@FormParam("typeAction") String typeAction,
			@FormParam("valeur") String valeur,
			@FormParam("idActionneur") String idActionneur) {

		try {
			House.getInstance().userAction(Integer.valueOf(piece), typeAction,
					valeur, idActionneur);

			return getSuccessJSONString();
		} catch (Exception e) {
			return getErrorJSONString(e);
		}
	}

	private String getErrorJSONString(Exception e) {
		try {
			JSONObject json = new JSONObject();
			json.put("success", false);
			json.put("msg", e.getMessage());
			return json.toString();
		} catch (JSONException e1) {
			System.out.println(e1.getMessage());
			return "Json response error : " + e1.getMessage();
		}
	}

	private String getSuccessJSONString() {
		try {
			JSONObject json = new JSONObject();
			json.put("success", true);
			return json.toString();
		} catch (JSONException e1) {
			System.out.println(e1.getMessage());
			return "Json response error : " + e1.getMessage();
		}
	}
}
