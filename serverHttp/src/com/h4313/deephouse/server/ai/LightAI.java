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

public abstract class LightAI {

	private static ArrayList<Long> lastDetectedPresence;
	
	private static ArrayList<Long> delayBeforeLightsOff;
	
	public static void initLightAI() {
		lastDetectedPresence = new ArrayList<Long>();
		delayBeforeLightsOff = new ArrayList<Long>();
		int nRooms = House.getInstance().getRooms().size();
		long initTime = DeepHouseCalendar.getInstance().getCalendar().getTimeInMillis()/1000;
		for(int i = 0 ; i < nRooms ; i++) {
			lastDetectedPresence.add(initTime);
			delayBeforeLightsOff.add(Constant.DELAY_BEFORE_LIGHTS_OFF_INIT);
		}
	}
	
	public static void run() {
		for(int i = 0 ; i < House.getInstance().getRooms().size() ; i++) {
			setLightsOff(i);
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
				break;
			}
		}
	}
	
}
