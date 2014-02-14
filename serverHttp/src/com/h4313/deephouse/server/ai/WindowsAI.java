package com.h4313.deephouse.server.ai;

import java.util.ArrayList;

import com.h4313.deephouse.actuator.Actuator;
import com.h4313.deephouse.actuator.ActuatorType;
import com.h4313.deephouse.housemodel.House;
import com.h4313.deephouse.housemodel.Room;
import com.h4313.deephouse.sensor.Sensor;
import com.h4313.deephouse.sensor.SensorType;
import com.h4313.deephouse.util.DeepHouseCalendar;

public abstract class WindowsAI {
	
	private static ArrayList<Long> openingTime;
	
	private static ArrayList<Long> closedTime;

	private static ArrayList<Long> openedDuration;
	
	public static void initWindowsAI() {
		openingTime = new ArrayList<Long>();
		closedTime = new ArrayList<Long>();
		openedDuration = new ArrayList<Long>();
		int nRooms = House.getInstance().getRooms().size();
		long initTime = DeepHouseCalendar.getInstance().getCalendar().getTimeInMillis()/1000;
		for(int i = 0 ; i < nRooms ; i++) {
			openingTime.add(initTime);
			openedDuration.add((long) 1800);
			closedTime.add(initTime);
		}
	}
	
	public static void run() {
		for(int i = 0 ; i < House.getInstance().getRooms().size() ; i++) {
			updateOpenedDuration(i);
			stopHeaterIfOpened(i);
			closeWindows(i);
		}
	}
	
	public static void stopHeaterIfOpened(int n) {
		Room r = House.getInstance().getRooms().get(n);
		ArrayList<Sensor<Object>> windows = r.getSensorByType(SensorType.WINDOW);
		Actuator<Object> heater = r.getActuatorByType(ActuatorType.RADIATOR).get(0);
		for(Sensor<Object> w : windows) {
			if(((Boolean) w.getLastValue()).booleanValue()) {
				heater.setLastValue(16.0);
				heater.setModified(true);
				openingTime.set(n,DeepHouseCalendar.getInstance().getCalendar().getTimeInMillis()/1000);
				break;
			}
		}
	}
	
	public static void closeWindows(int n) {
		Room r = House.getInstance().getRooms().get(n);
		ArrayList<Sensor<Object>> windows = r.getSensorByType(SensorType.WINDOW);
		ArrayList<Actuator<Object>> windowClosers = r.getActuatorByType(ActuatorType.WINDOWCLOSER);
		for(Sensor<Object> w : windows) {
			if(((Boolean)w.getLastValue()).booleanValue() && openingTime.get(n) >= openedDuration.get(n)) {
				for(int i = 0 ; i < windowClosers.size() ; i++) {
					windowClosers.get(i).setLastValue(true);
					closedTime.set(n,DeepHouseCalendar.getInstance().getCalendar().getTimeInMillis()/1000);
				}
			}
		}
	}
	
	public static void updateOpenedDuration(int n) {
		Room r = House.getInstance().getRooms().get(n);
		ArrayList<Sensor<Object>> windows = r.getSensorByType(SensorType.WINDOW);
		long deltaTime = DeepHouseCalendar.getInstance().getCalendar().getTimeInMillis()/1000 - closedTime.get(n);
		for(Sensor<Object> w : windows) {
			if(((Boolean)w.getLastValue()).booleanValue() && deltaTime <= 300) {
				//Increases the duration by half
				openedDuration.set(n,3*openedDuration.get(n)/2);
				break;
			}
			else if(!((Boolean)w.getLastValue()).booleanValue() && deltaTime >= 300) {
				//Decreases the duration by half
				openedDuration.set(n,openedDuration.get(n)/2);
				break;
			}
		}
	}
	
}
