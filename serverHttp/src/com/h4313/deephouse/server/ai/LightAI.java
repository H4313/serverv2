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
	
	private static ArrayList<Integer> previousOrder;
	
	public static void initLightAI() {
		lastDetectedPresence = new ArrayList<Long>();
		delayBeforeLightsOff = new ArrayList<Long>();
		lastTimeLightsOn = new ArrayList<Long>();
		previousOrder = new ArrayList<Integer>();
		int nRooms = House.getInstance().getRooms().size();
		long initTime = DeepHouseCalendar.getInstance().getCalendar().getTimeInMillis()/1000;
		for(int i = 0 ; i < nRooms ; i++) {
			lastDetectedPresence.add(initTime);
			delayBeforeLightsOff.add(Constant.DELAY_BEFORE_LIGHTS_OFF_INIT);
			lastTimeLightsOn.add(initTime);
			previousOrder.add(0);
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
		Sensor<Object> lightSensor = r.getSensorByType(SensorType.LIGHT).get(0);
		int hour = DeepHouseCalendar.getInstance().getCalendar().get(Calendar.HOUR_OF_DAY);
		if(hour < 21 && hour > 8) {
			for(int i = 0 ; i < presence.size() ; i++) {
				//Somebody in the room => Lights on
				if(((Boolean)(presence.get(i).getLastValue())).booleanValue()
					&& (!((Boolean)(lightSensor.getLastValue())).booleanValue())) {
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
		for(int i = 0 ; i < lights.size() ; i++) {
			//User wants to light the lights using the tablet
			if(((Boolean)(lights.get(i).getUserValue())).booleanValue()
				&& (!((Boolean)(lightSensor.getLastValue())).booleanValue())
				&& (!previousOrder.get(n).equals(1))) {
				
				previousOrder.set(n,1);
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
	
	public static void setLightsOff(int n) {
		Room r = House.getInstance().getRooms().get(n);
		ArrayList<Sensor<Object>> presence = r.getSensorByType(SensorType.PRESENCE);
		ArrayList<Actuator<Object>> lights = r.getActuatorByType(ActuatorType.LIGHTCONTROL);
		ArrayList<Sensor<Object>> lightSensors = r.getSensorByType(SensorType.LIGHT);
		boolean lightsOn = false;
		for(int i = 0 ; i < lightSensors.size() ; i++) {
			if(((Boolean) lightSensors.get(i).getLastValue()).booleanValue()) {
				lightsOn = true;
				break;
			}
		}
		for(Sensor<Object> p : presence) {
			//Lights off because of delay
			if(((Boolean)p.getLastValue()).booleanValue()) {
				lastDetectedPresence.set(n,DeepHouseCalendar.getInstance().getCalendar().getTimeInMillis()/1000);
			}
			long deltaTime = DeepHouseCalendar.getInstance().getCalendar().getTimeInMillis()/1000 
							- lastDetectedPresence.get(n);
			if(deltaTime > delayBeforeLightsOff.get(n) && lightsOn) {
				for(int i = 0 ; i < lights.size() ; i++) {
					lights.get(i).setLastValue(true);
					lights.get(i).setModified(true);
				}
				lastTimeLightsOn.set(n,DeepHouseCalendar.getInstance().getCalendar().getTimeInMillis()/1000);
				delayBeforeLightsOff.set(n, (long) (delayBeforeLightsOff.get(n)*0.8));
				break;
			}
		}
		for(int i = 0 ; i < lights.size() ; i++) {
			//Lights off because user wants to using the tablet
			if(!((Boolean)lights.get(i).getUserValue()).booleanValue() && lightsOn && !previousOrder.get(n).equals(2)) {
				previousOrder.set(n,2);
				for(int j = 0 ; j < lights.size() ; j++) {
					lights.get(j).setLastValue(true);
					lights.get(j).setModified(true);
				}
				lastTimeLightsOn.set(n,DeepHouseCalendar.getInstance().getCalendar().getTimeInMillis()/1000);
				break;
			}
		}
		for(int i = 0 ; i < lightSensors.size() ; i++) {
			//Lights off manually
			if(!((Boolean)lightSensors.get(i).getLastValue()).booleanValue() && lightsOn) {
				lastTimeLightsOn.set(n,DeepHouseCalendar.getInstance().getCalendar().getTimeInMillis()/1000);
				break;	
			}
		}
	}
	
}
