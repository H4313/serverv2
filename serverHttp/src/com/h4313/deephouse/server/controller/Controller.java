package com.h4313.deephouse.server.controller;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import com.h4313.deephouse.actuator.Actuator;
import com.h4313.deephouse.exceptions.DeepHouseException;
import com.h4313.deephouse.frame.Frame;
import com.h4313.deephouse.housemodel.House;
import com.h4313.deephouse.housemodel.Room;
import com.h4313.deephouse.server.ai.TemperatureAI;

public class Controller extends Thread {
	
	private volatile boolean alive;
	
	private static volatile Controller instance = null;
	
	protected SensorsListener sensorsListener;
	
	protected ActuatorsSender actuatorsSender;
	
	/**
	 * Constructeur du controleur
	 */
	private Controller() {
		super();
        this.alive = true;
	}
	
	public static final Controller getInstance() {
        if (Controller.instance == null) {
            synchronized(Controller.class) {
              if (Controller.instance == null) {
             	 Controller.instance = new Controller();
              }
            }
            TemperatureAI.initTemperatureAI();
         }
         return Controller.instance;
	}
	
	
    public void initActuatorsSender(String host, int port) {
    	actuatorsSender = new ActuatorsSender(host,port);
    }
    
    public void initSensorsListener(int port) {
    	sensorsListener = new SensorsListener(port);
    }
	
    @Override
	public void run() {
		try {
			while(alive)
			{
				if(updateSensors()) {
					updateModel();
					sendActuators();	
				}
				else {
					Thread.sleep(5000);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
    /**
     * Clears the listener buffer and updates every sensor according
     * to the frames received
     * @return	True if at least one sensor has been updated, false otherwise
     * @throws DeepHouseException
     */
	private boolean updateSensors() throws DeepHouseException {
		ArrayList<String> messages = sensorsListener.clearBuffer();
		for(String message : messages) {
			Frame frame = new Frame(message);
			House.getInstance().updateSensor(frame);
		}
		return (!messages.isEmpty());
	}
	
	/**
	 * Sends a frame for each actuator that has been updated
	 * through the sender
	 */
	private void sendActuators() {
		for(Room r : House.getInstance().getRooms()) {
			Map<String,Actuator<Object>> actuators = r.getActuators();
	        Set<Map.Entry<String, Actuator<Object>>> set = actuators.entrySet();
	        for(Map.Entry<String,Actuator<Object>> entry : set) {
	        	if(entry.getValue().getModified()) {
	        		actuatorsSender.submitMessage(entry.getValue().composeFrame());
	        		entry.getValue().setModified(false);
	        	}
	        }
		}
	}
	
	/**
	 * Calls every AI to update the model
	 */
	private void updateModel() {
		/*
		for(Room r : House.getInstance().getRooms()) {
			ArrayList<Sensor<Object>> light = r.getSensorByType(SensorType.LIGHT);
			ArrayList<Actuator<Object>> lightcontrol = r.getActuatorByType(ActuatorType.LIGHTCONTROL);
			if(light.size() != 0 && r.getIdRoom() == RoomConstants.ID_BEDROOM && lightcontrol.size() !=0) {
				if(((Boolean)light.get(0).getLastValue()).booleanValue()) {
					lightcontrol.get(0).setLastValue(false);
					lightcontrol.get(0).setModified(true);
				}
			}
		}
		*/
		TemperatureAI.run();
	}
	
    public void stopController() {
    	this.alive = false;
    	try {
	    	this.sensorsListener.stopListener();
	    	this.actuatorsSender.stopSender();
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }
	
}
