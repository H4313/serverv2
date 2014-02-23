package com.h4313.deephouse.server.ai;

import java.util.ArrayList;
import java.util.Calendar;

import com.h4313.deephouse.actuator.Actuator;
import com.h4313.deephouse.actuator.ActuatorType;
import com.h4313.deephouse.housemodel.House;
import com.h4313.deephouse.housemodel.Room;
import com.h4313.deephouse.sensor.Sensor;
import com.h4313.deephouse.sensor.SensorType;
import com.h4313.deephouse.server.util.Constant;
import com.h4313.deephouse.util.DeepHouseCalendar;

public abstract class LightAI {

	private static ArrayList<Long> lastDetectedPresence;
	
	private static ArrayList<Long> delayBeforeLightsOff;
	
	private static ArrayList<Long> lastTimeLightsOn;
	
	public static void initLightAI() {
		lastDetectedPresence = new ArrayList<Long>();
		delayBeforeLightsOff = new ArrayList<Long>();
		lastTimeLightsOn = new ArrayList<Long>();
		int nRooms = House.getInstance().getRooms().size();
		long initTime = DeepHouseCalendar.getInstance().getCalendar().getTimeInMillis()/1000;
		for(int i = 0 ; i < nRooms ; i++) {
			lastDetectedPresence.add(initTime);
			delayBeforeLightsOff.add(Constant.DELAY_BEFORE_LIGHTS_OFF_INIT);
			lastTimeLightsOn.add(initTime);
		}
	}
	
	public static void run() {
		for(int i = 0 ; i < House.getInstance().getRooms().size() ; i++) {
			setLightsOn(i);
			setLightsOff(i);
		}
	}
	
	public static void setLightsOn(int n) {
		Room r = House.getInstance().getRooms().get(n);
		ArrayList<Sensor<Object>> presence = r.getSensorByType(SensorType.PRESENCE);
		ArrayList<Actuator<Object>> lights = r.getActuatorByType(ActuatorType.LIGHTCONTROL);
		int hour = DeepHouseCalendar.getInstance().getCalendar().get(Calendar.HOUR_OF_DAY);
		if(hour < 21 && hour > 8) {
			for(int i = 0 ; i < presence.size() ; i++) {
				//Somebody in the room => Lights on
				if(((Boolean)(presence.get(i).getLastValue())).booleanValue()) {
					for(int j = 0 ; j < lights.size() ; j++) {
						lights.get(j).setLastValue(true);
						lights.get(j).setModified(true);
					}
					long deltaTime = DeepHouseCalendar.getInstance().getCalendar().getTimeInMillis()/1000 
									- lastTimeLightsOn.get(n);
					if(deltaTime < 300) {
						delayBeforeLightsOff.set(n, (long) (delayBeforeLightsOff.get(n)*1.2));
					}
				}
			}
			for(int i = 0 ; i < lights.size() ; i++) {
				//User wants to light the lights using the tablet
				if(((Boolean)(lights.get(i).getUserValue())).booleanValue()) {
					for(int j = 0 ; j < lights.size() ; j++) {
						lights.get(j).setLastValue(true);
						lights.get(j).setModified(true);
					}
					long deltaTime = DeepHouseCalendar.getInstance().getCalendar().getTimeInMillis()/1000 
									- lastTimeLightsOn.get(n);
					if(deltaTime < 300) {
						delayBeforeLightsOff.set(n, (long) (delayBeforeLightsOff.get(n)*1.2));
					}
				}
			}	
		}
	}
	
	public static void setLightsOff(int n) {
		Room r = House.getInstance().getRooms().get(n);
		ArrayList<Sensor<Object>> presence = r.getSensorByType(SensorType.PRESENCE);
		ArrayList<Actuator<Object>> lights = r.getActuatorByType(ActuatorType.LIGHTCONTROL);
		boolean lightsOn = false;
		for(int i = 0 ; i < lights.size() ; i++) {
			if(((Boolean) lights.get(i).getLastValue()).booleanValue()) {
				lightsOn = true;
				break;
			}
		}
		for(Sensor<Object> p : presence) {
			if(((Boolean)p.getLastValue()).booleanValue()) {
				lastDetectedPresence.set(n,DeepHouseCalendar.getInstance().getCalendar().getTimeInMillis()/1000);
			}
			long deltaTime = DeepHouseCalendar.getInstance().getCalendar().getTimeInMillis()/1000 
							- lastDetectedPresence.get(n);
			if(deltaTime > delayBeforeLightsOff.get(n) && lightsOn) {
				for(int i = 0 ; i < lights.size() ; i++) {
					lights.get(i).setLastValue(false);
					lights.get(i).setModified(true);
				}
				delayBeforeLightsOff.set(n, (long) (delayBeforeLightsOff.get(n)*0.8));
				break;
			}
		}
	}
	
}
