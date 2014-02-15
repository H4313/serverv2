package com.h4313.deephouse.server.ai;

import java.util.ArrayList;

import com.h4313.deephouse.actuator.Actuator;
import com.h4313.deephouse.actuator.ActuatorType;
import com.h4313.deephouse.housemodel.House;
import com.h4313.deephouse.housemodel.Room;
import com.h4313.deephouse.sensor.Sensor;
import com.h4313.deephouse.sensor.SensorType;
import com.h4313.deephouse.server.neuralnetwork.NeuralNetwork;
import com.h4313.deephouse.util.DeepHouseCalendar;

public abstract class TemperatureAI {
	
	//Previous time used in the AI in each room in seconds
	public static ArrayList<Long> previousTime;
	
	public static ArrayList<Double> integralFactor;
	
	public static ArrayList<Double> proportionalFactor;
	
	public static ArrayList<Double> integral;
	
	public static ArrayList<NeuralNetwork> nets;
	
	/**
	 * Initialize the AI. Has to be called before the first call of a method
	 */
	public static void initTemperatureAI() {
		previousTime = new ArrayList<Long>();
		integralFactor = new ArrayList<Double>();
		proportionalFactor = new ArrayList<Double>();
		integral = new ArrayList<Double>();
		nets = new ArrayList<NeuralNetwork>();
		int nRooms = House.getInstance().getRooms().size();
		for(int i = 0 ; i < nRooms ; i++) {
			previousTime.add((long)0);
			integralFactor.add(0.3);
			proportionalFactor.add(0.8);
			integral.add(0.0);
			nets.add(new NeuralNetwork("fileconfig/temperatures.xml",0.05));
		}
	}
	
	/**
	 * Controls the heaters of every room
	 */
	public static void run() {
		for(int i = 0 ; i < House.getInstance().getRooms().size() ; i++) {
			evaluateDesiredValue(i);
			piControl(i);
		}
	}
	
	/***
	 * Set the desired value of a heater according to the neural network
	 * @param n	The index of the room in which the heater is located
	 */
	public static void evaluateDesiredValue(int n) {
		Room r = House.getInstance().getRooms().get(n);
		Double month = (double) DeepHouseCalendar.getInstance().getCalendar().MONTH + 1;
		Double hour = (double) DeepHouseCalendar.getInstance().getCalendar().HOUR_OF_DAY;
		ArrayList<Double> inputs = new ArrayList<Double>();
		inputs.add(month/6.0 - 1.0);
		inputs.add(hour/12.0 -1.0);
		nets.get(n).propagate(inputs);
		try {
			r.getActuatorByType(ActuatorType.RADIATOR).get(0).setDesiredValue(nets.get(n).getOutputScaled());	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Proportional Integral heater controller
	 * @param n	The index of the room to control
	 */
	public static void piControl(int n) {
		Room r = House.getInstance().getRooms().get(n);
		Actuator<Object> heater = r.getActuatorByType(ActuatorType.RADIATOR).get(0);
		Sensor<Object> temp = r.getSensorByType(SensorType.TEMPERATURE).get(0);		
		
		Double error = (Double) heater.getDesiredValue() - (Double) temp.getLastValue();
		if(previousTime.get(n) == 0) {
			previousTime.set(n,DeepHouseCalendar.getInstance().getCalendar().getTimeInMillis()/1000);
		}
		Long deltaT = DeepHouseCalendar.getInstance().getCalendar().getTimeInMillis()/1000 - previousTime.get(n);
		previousTime.set(n,DeepHouseCalendar.getInstance().getCalendar().getTimeInMillis()/1000);
		integral.set(n, (integral.get(n) + error*deltaT.doubleValue())/3600);
		Double output = (Double) temp.getLastValue() + proportionalFactor.get(n)*error + integralFactor.get(n)*integral.get(n);
		heater.setLastValue(output);
		heater.setModified(true);
		
		System.out.println("TEST : desired = "+heater.getDesiredValue());
		System.out.println("TEST : sensor = "+temp.getLastValue());
		System.out.println("TEST : error = "+error);
		System.out.println("TEST : integral = "+integral.get(n));
		System.out.println("TEST : output = "+output);
	}
}
