package com.h4313.deephouse.server.ai;

import java.util.ArrayList;

import com.h4313.deephouse.actuator.Actuator;
import com.h4313.deephouse.actuator.ActuatorType;
import com.h4313.deephouse.housemodel.House;
import com.h4313.deephouse.housemodel.Room;
import com.h4313.deephouse.sensor.Sensor;
import com.h4313.deephouse.sensor.SensorType;
import com.h4313.deephouse.server.util.Constant;
import com.h4313.deephouse.util.DeepHouseCalendar;

public abstract class WindowsAI {
	
	private static ArrayList<Long> openingTime;
	
	private static ArrayList<Long> closedTime;

	private static ArrayList<Long> openedDuration;
	
	private static ArrayList<Boolean> opened;
	
	private static ArrayList<Integer> previousOrder;
	
	public static void initWindowsAI() {
		openingTime = new ArrayList<Long>();
		closedTime = new ArrayList<Long>();
		openedDuration = new ArrayList<Long>();
		opened = new ArrayList<Boolean>();
		previousOrder = new ArrayList<Integer>();
		int nRooms = House.getInstance().getRooms().size();
		long initTime = DeepHouseCalendar.getInstance().getCalendar().getTimeInMillis()/1000;
		for(int i = 0 ; i < nRooms ; i++) {
			openingTime.add(initTime);
			openedDuration.add(Constant.OPENED_DURATION_INIT);
			closedTime.add(initTime);
			opened.add(false);
			previousOrder.add(0);
		}
	}
	
	public static void run() {
		for(int i = 0 ; i < House.getInstance().getRooms().size() ; i++) {
			if(closedTime.get(i) != null) {
				updateOpenedDuration(i);
			}
			if(opened.get(i)) {
				closeWindows(i);
			}
			else {
				stopHeaterIfOpened(i);
			}
		}
	}
	
	public static void stopHeaterIfOpened(int n) {
		Room r = House.getInstance().getRooms().get(n);
		ArrayList<Actuator<Object>> windowClosers = r.getActuatorByType(ActuatorType.WINDOWCLOSER);
		Actuator<Object> heater = r.getActuatorByType(ActuatorType.RADIATOR).get(0);
		for(Actuator<Object> w : windowClosers) {
			Sensor<Object> wsensor = w.getSensors().entrySet().iterator().next().getValue();
			if(((Boolean) wsensor.getLastValue()).booleanValue()) {
				heater.setLastValue(Constant.EMPTY_ROOM_TEMPERATURE);
				heater.setModified(true);
				openingTime.set(n,DeepHouseCalendar.getInstance().getCalendar().getTimeInMillis()/1000);
				opened.set(n, true);
				break;
			}
			else if(((Boolean)w.getUserValue()).booleanValue()
					&& !previousOrder.get(n).equals(1)) {
				previousOrder.set(n, 1);
				heater.setLastValue(Constant.EMPTY_ROOM_TEMPERATURE);
				heater.setModified(true);
				openingTime.set(n,DeepHouseCalendar.getInstance().getCalendar().getTimeInMillis()/1000);
				opened.set(n, true);
				for(int i = 0 ; i < windowClosers.size() ; i++) {
					windowClosers.get(i).setLastValue(true);
					windowClosers.get(i).setModified(true);
				}
				break;
			}
		}
	}
	
	public static void closeWindows(int n) {
		Room r = House.getInstance().getRooms().get(n);
		ArrayList<Actuator<Object>> windowClosers = r.getActuatorByType(ActuatorType.WINDOWCLOSER);
		long deltaTime = DeepHouseCalendar.getInstance().getCalendar().getTimeInMillis()/1000 - openingTime.get(n);
		for(Actuator<Object> w : windowClosers) {
			Sensor<Object> wsensor = w.getSensors().entrySet().iterator().next().getValue();
			if(!(((Boolean)wsensor.getLastValue()).booleanValue())) {
				//Closed by hand
				closedTime.set(n,DeepHouseCalendar.getInstance().getCalendar().getTimeInMillis()/1000);
				opened.set(n, false);
			}
			else if(!(((Boolean)w.getUserValue()).booleanValue())
					&& !previousOrder.get(n).equals(2)) {
				previousOrder.set(n, 2);
				//Closed by tablet
				for(int i = 0 ; i < windowClosers.size() ; i++) {
					windowClosers.get(i).setLastValue(true);
					windowClosers.get(i).setModified(true);
				}
				closedTime.set(n,DeepHouseCalendar.getInstance().getCalendar().getTimeInMillis()/1000);
				opened.set(n, false);
			}
			else if(deltaTime > openedDuration.get(n)) {
				//Closed by delay
				for(int i = 0 ; i < windowClosers.size() ; i++) {
					windowClosers.get(i).setLastValue(true);
					windowClosers.get(i).setModified(true);
				}
				closedTime.set(n,DeepHouseCalendar.getInstance().getCalendar().getTimeInMillis()/1000);
				opened.set(n, false);
			}
				
		}
	}
	
	public static void updateOpenedDuration(int n) {
		Room r = House.getInstance().getRooms().get(n);
		ArrayList<Actuator<Object>> windowClosers = r.getActuatorByType(ActuatorType.WINDOWCLOSER);
		long deltaTime = DeepHouseCalendar.getInstance().getCalendar().getTimeInMillis()/1000 - closedTime.get(n);
		for(Actuator<Object> w : windowClosers) {
			if(((Boolean)w.getUserValue()).booleanValue() && deltaTime <= 300) {
				//Increases the duration by half
				openedDuration.set(n,3*openedDuration.get(n)/2);
				closedTime.set(n,null);
				break;
			}
			else if(!((Boolean)w.getUserValue()).booleanValue() && deltaTime >= 300) {
				//Decreases the duration by half
				openedDuration.set(n,openedDuration.get(n)/2);
				closedTime.set(n,null);
				break;
			}
		}
	}
	
}
