package com.h4313.deephouse.server.main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import com.h4313.deephouse.actuator.ActuatorType;
import com.h4313.deephouse.dao.HouseDAO;
import com.h4313.deephouse.housemodel.House;
import com.h4313.deephouse.housemodel.Room;
import com.h4313.deephouse.sensor.Sensor;
import com.h4313.deephouse.sensor.SensorType;
import com.h4313.deephouse.server.controller.Controller;
import com.h4313.deephouse.util.DecToHexConverter;
import com.h4313.deephouse.util.DeepHouseCalendar;

public class Main {
	public static void main(String[] args) throws Exception
	{
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

		// Persistance de la maison test : TODO A RETIRER POUR LA PRODUCTION
		HouseDAO houseDao = new HouseDAO();
		houseDao.createUpdate(House.getInstance());

		// http://www.mkyong.com/java/how-do-convert-java-object-to-from-json-format-gson-api/
//		try {
//			Gson gson = new Gson();
//			//write converted json data to a file named "file.json"
//			FileWriter writer = new FileWriter("/Users/macbric/Desktop/house.json");
//			String json = gson.toJson(House.getInstance());
//			writer.write(json);
//			System.out.println(json);
//			writer.close();
//	 
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
//		System.exit(0);
		
		// Initialisation du reseau
		Controller.getInstance().initSensorsListener(Integer.valueOf(args[0]).intValue());
		Controller.getInstance().initActuatorsSender(args[1], Integer.valueOf(args[2]).intValue());
		Controller.getInstance().start();
		
		
		// En attente de l'arret de la machine
		String str = "";
		Scanner scExit;
		do {
			scExit = new Scanner(System.in);
			System.out.println("/// Tapez 'EXIT' pour arreter la machine ///");
			str = scExit.nextLine();
		} while (!str.toLowerCase().contains((CharSequence) "exit"));
		scExit.close();
	
		Controller.getInstance().stopController();
		
		
		System.out.println("Arret du serveur");

		System.exit(0);
	}
}
