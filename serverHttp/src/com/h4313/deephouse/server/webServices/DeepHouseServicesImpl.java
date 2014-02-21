package com.h4313.deephouse.server.webServices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

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
import com.h4313.deephouse.server.controller.Controller;
import com.h4313.deephouse.util.DecToHexConverter;
import com.h4313.deephouse.util.DeepHouseCalendar;
import com.h4313.deephouse.util.Pair;

@Path("/rest")
// @Consumes("application/json")
@Produces("application/json")
/*
 * !Important! Defines getters and setters domain class NO methods with no
 * params starting by get... (WebSevice thinks is a getter) Documentation :
 * RESTFul Web Services http://docs.oracle.com/javaee/6/tutorial/doc/gilik.html
 */
public class DeepHouseServicesImpl implements DeepHouseServices {

//	private HouseDAO houseDAO;
	private ArrayList<Pair<Integer, Double>> temperatures;

	public DeepHouseServicesImpl() throws DeepHouseException {
//		houseDAO = new HouseDAO();
//		House.initInstance(houseDAO);
		
		temperatures = new ArrayList<Pair<Integer, Double>>();
	}
	
	@GET
	@Path("/connect")
	@Override
	public String connect() {
		try {

			// Initialisation de la maison // TODO : RETIRER POUR LA PRODUCTION
			House h = House.getInstance();
			List<Room> rooms = h.getRooms();
			int id = 0;
			for (Room room : rooms) {
				room.addSensor(DecToHexConverter.decToHex(id++),
						SensorType.TEMPERATURE);
				room.addSensor(DecToHexConverter.decToHex(id++),
						SensorType.WINDOW);
				room.addSensor(DecToHexConverter.decToHex(id++),
						SensorType.LIGHT);
				room.addSensor(DecToHexConverter.decToHex(id++),
						SensorType.DOOR);
				room.addSensor(DecToHexConverter.decToHex(id++),
						SensorType.FLAP);
				room.addSensor(DecToHexConverter.decToHex(id++),
						SensorType.PRESENCE);

				room.addActuator(DecToHexConverter.decToHex(id++),
						ActuatorType.RADIATOR);
				room.addActuator(DecToHexConverter.decToHex(id++),
						ActuatorType.WINDOWCLOSER);
				room.addActuator(DecToHexConverter.decToHex(id++),
						ActuatorType.LIGHTCONTROL);
				room.addActuator(DecToHexConverter.decToHex(id++),
						ActuatorType.DOORCONTROL);
				room.addActuator(DecToHexConverter.decToHex(id++),
						ActuatorType.FLAPCLOSER);

				room.establishConnections();
			}
			System.out.println("Done init");
			h.printInformations();
			HouseDAO houseDAO = new HouseDAO();
			houseDAO.createUpdate(h);

			return getSuccessJSONString();
		} catch (Exception e) {
			return getErrorJSONString(e);
		}
	}

	@GET
	@Path("/runServer")
	@Override
	public String runServer() {
		
		try
		{
			// Initialisation du reseau
			Controller.getInstance().initSensorsListener(Integer.valueOf("6001").intValue());
			Controller.getInstance().initActuatorsSender("127.0.0.1", Integer.valueOf("6002").intValue());
			Controller.getInstance().start();
	
			// Initialisation de l'horloge de simulation
			DeepHouseCalendar.getInstance().init();
			
			return getSuccessJSONString();
		} catch (Exception e) {
			e.printStackTrace();
			return getErrorJSONString(e);
		}
	}
	
	@GET
	@Path("/chart")
	@Produces("text/html")
	@Override
	public String chart() {
			SimpleDateFormat formatter = new SimpleDateFormat();
		   String currentDate = formatter.format(DeepHouseCalendar.getInstance().getCalendar().getTime());
		   if(DeepHouseCalendar.getInstance().getCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
			   currentDate = "Lundi " + currentDate;
		   else if(DeepHouseCalendar.getInstance().getCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY)
			   currentDate = "Mardi " + currentDate;
		   else if(DeepHouseCalendar.getInstance().getCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY)
			   currentDate = "Mercredi " + currentDate;
		   else if(DeepHouseCalendar.getInstance().getCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY)
			   currentDate = "Jeudi " + currentDate;
		   else if(DeepHouseCalendar.getInstance().getCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
			   currentDate = "Vendredi " + currentDate;
		   else if(DeepHouseCalendar.getInstance().getCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
			   currentDate = "Samedi " + currentDate;
		   else if(DeepHouseCalendar.getInstance().getCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
			   currentDate = "Dimanche " + currentDate;
		   
		   int hour = DeepHouseCalendar.getInstance().getCalendar().get(Calendar.HOUR_OF_DAY);
		   if(hour == 0)
		   {
			   temperatures.clear();
		   }
		   
		   for(Room room : House.getInstance().getRooms())
		   {
			   temperatures.add(new Pair<Integer, Double>(Integer.valueOf(hour), 
					   (Double) room.getSensorByType(SensorType.TEMPERATURE).get(0).getLastValue()));
		   }
//		   .getLastValue()
//		   stocker les valeurs de last value dans une arraylist
			String str = "<html><head>";
			str += "<script type='text/javascript' src='https://www.google.com/jsapi'></script>";
			str += "<script type='text/javascript'>";
			str += "window.onload=function(){ setTimeout(function(){";
			str += "window.location.reload(1);";
			str += "}, 6000); };";
			str += "google.load('visualization', '1', {packages:['corechart']});";
			str += "google.setOnLoadCallback(drawChart);";
			str += "function drawChart() {";
			str += "var data = google.visualization.arrayToDataTable([";
			str += "['Heures'";
			// Rooms name
			for(Room room : House.getInstance().getRooms())
			{
				str += ", '" + room.getName() + "'";
			}
			str += "],";
			
			int previousHour = Integer.valueOf(-1);
			int tmpHour;
			double tmpTemperature;
			for(Pair<Integer, Double> temperature : temperatures)
			{
				tmpHour = ((Integer) temperature.getFirst()).intValue();
				tmpTemperature = ((Double)temperature.getSecond()).doubleValue();
				
				if(previousHour != tmpHour)
				{
					if(previousHour != -1)
						str += "],";
					
					str += "['" + tmpHour + "'";
					
					previousHour = tmpHour;
				}
				str += ", " + tmpTemperature;
			}
			str += "],";
			str = str.substring(0, str.length()-1);
			str += "]);";
			str += "var options = {";
			str += "width: 1000, height: 500,";
			str += "title: 'Evolution de la temperature des pieces de la maison : " + currentDate + "',";
			str += "hAxis: { minValue: 0, maxValue: 24, viewWindow:{min: 0, max: 24} },";
			str += "vAxis: { minValue: 13, maxValue: 23, viewWindow:{min: 13, max: 23} }";
			str += "};";
			str += "var chart = new google.visualization.LineChart(document.getElementById('chart_div'));";
			str += "chart.draw(data, options);";
			str += "}";
			str += "</script>";
			str += "</head><body>";
			str += "<div id='chart_div' style='width: 1000px; height: 500px;'></div>";
			str += "</body></html>";
			
			return str;
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
			House h = House.getInstance();
			h.addSensor(Integer.valueOf(piece), idCapteur.toUpperCase(), type.toUpperCase());
			HouseDAO houseDAO = new HouseDAO();
			houseDAO.createUpdate(h);
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
			House h = House.getInstance();
			h.addActuator(Integer.valueOf(piece), idActionneur.toUpperCase(), type.toUpperCase());
			HouseDAO houseDAO = new HouseDAO();
			houseDAO.createUpdate(h);
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
		// builder.excludeFieldsWithoutExposeAnnotation();
		final Gson gson = builder.create();

		String json = gson.toJson(House.getInstance());

		return json;
	}

	@Override
	@POST
	@Path("/userAction")
	@Consumes("application/x-www-form-urlencoded")
	public String userAction(@FormParam("piece") String piece,
			@FormParam("typeAction") String typeAction,
			@FormParam("valeur") String valeur) {

		try {
			House.getInstance().userAction(Integer.valueOf(piece), 
					typeAction.toUpperCase(),
					valeur);

			return getSuccessJSONString();
		} catch (Exception e) {
			return getErrorJSONString(e);
		}
	}

	private String getErrorJSONString(Exception e) {
		try {
			JSONObject json = new JSONObject();
			json.put("success", false);
			json.put("msg", "Error message : " + e.getMessage());
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
