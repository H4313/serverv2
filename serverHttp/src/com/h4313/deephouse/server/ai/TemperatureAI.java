package com.h4313.deephouse.server.ai;

import java.util.ArrayList;
import java.util.Calendar;

import com.h4313.deephouse.actuator.Actuator;
import com.h4313.deephouse.actuator.ActuatorType;
import com.h4313.deephouse.housemodel.House;
import com.h4313.deephouse.housemodel.Room;
import com.h4313.deephouse.sensor.Sensor;
import com.h4313.deephouse.sensor.SensorType;
import com.h4313.deephouse.server.neuralnetwork.NeuralNetwork;
import com.h4313.deephouse.server.util.Constant;
import com.h4313.deephouse.util.DeepHouseCalendar;

public abstract class TemperatureAI {
	
	//Previous time used in the AI in each room in seconds
	public static ArrayList<Long> previousTime;
	
	public static ArrayList<Double> integralFactor;
	
	public static ArrayList<Double> proportionalFactor;
	
	public static ArrayList<Double> integral;
	
	public static ArrayList<NeuralNetwork> nets;
	
	public static Integer roomViewed;
	
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
			proportionalFactor.add(1.0);
			integral.add(0.0);
			nets.add(new NeuralNetwork("temperatures2.xml",0.05));
		}
		roomViewed = 3;
		//Comment those two lines if you dont want to get the temperature graphs
//		TemperatureAIView.initTemperatureAIView(0.0, 24.0, 13.0, 25.0, House.getInstance().getRooms().get(roomViewed));
//		TemperatureAIViewMultiple.initTemperatureAIViewMultiple(0.0, 24.0, 13.0, 25.0, House.getInstance().getRooms());
	}
	
	/**
	 * Controls the heaters of every room
	 */
	public static void run() {
		ArrayList<Double> measured = new ArrayList<Double>();
		for(int i = 0 ; i < House.getInstance().getRooms().size() ; i++) {
			evaluateDesiredValue(i);
			piControl(i);
//			if(i == roomViewed) {
//				TemperatureAIView.updateView( (double) DeepHouseCalendar.getInstance().getCalendar().get(Calendar.HOUR_OF_DAY)
//													 + DeepHouseCalendar.getInstance().getCalendar().get(Calendar.MINUTE)/60.0
//											, (Double) House.getInstance().getRooms().get(i).getActuatorByType(ActuatorType.RADIATOR).get(0).getDesiredValue()
//											, (Double) House.getInstance().getRooms().get(i).getSensorByType(SensorType.TEMPERATURE).get(0).getLastValue()
//											, (Double) House.getInstance().getRooms().get(i).getActuatorByType(ActuatorType.RADIATOR).get(0).getLastValue());
//			}
			measured.add((Double) House.getInstance().getRooms().get(i).getSensorByType(SensorType.TEMPERATURE).get(0).getLastValue());
		}
//		TemperatureAIViewMultiple.updateView((double) DeepHouseCalendar.getInstance().getCalendar().get(Calendar.HOUR_OF_DAY)
//				 + DeepHouseCalendar.getInstance().getCalendar().get(Calendar.MINUTE)/60.0
//			, measured);
	}
	
	/***
	 * Set the desired value of a heater according to the neural network
	 * @param n	The index of the room in which the heater is located
	 */
	public static void evaluateDesiredValue(int n) {
		Room r = House.getInstance().getRooms().get(n);
		Sensor<Object> presence = r.getSensorByType(SensorType.PRESENCE).get(0);
		Actuator<Object> heater = r.getActuatorByType(ActuatorType.RADIATOR).get(0);
		Double output;
		if(heater.getUserValue() == null) {
			//USE THE MODEL VALUE
			if(((Boolean)presence.getLastValue()).booleanValue()) {
				//If present => use current prefered value
				Double month = (double) DeepHouseCalendar.getInstance().getCalendar().get(Calendar.MONTH);
				Double hour = (double) DeepHouseCalendar.getInstance().getCalendar().get(Calendar.HOUR_OF_DAY);
				ArrayList<Double> inputs = new ArrayList<Double>();
				inputs.add(month/6.0 - 1.0);
				inputs.add(hour/12.0 -1.0);
				nets.get(n).propagate(inputs);
				output = nets.get(n).getOutputScaled();
			}
			else {
				//Not present
				output = Constant.EMPTY_ROOM_TEMPERATURE;
			}
		}
		else {
			//USE THE USER VALUE AND LEARN
			output = (Double)heater.getUserValue();
			Double month = (double) DeepHouseCalendar.getInstance().getCalendar().get(Calendar.MONTH);
			Double hour = (double) DeepHouseCalendar.getInstance().getCalendar().get(Calendar.HOUR_OF_DAY);
			ArrayList<Double> inputs = new ArrayList<Double>();
			inputs.add(month/6.0 - 1.0);
			inputs.add(hour/12.0 -1.0);
			nets.get(n).propagate(inputs);
			//If first day of the month => resets the learning rate
			if(DeepHouseCalendar.getInstance().getCalendar().get(Calendar.DAY_OF_MONTH) == 1) {
				nets.get(n).setGlobalLearningRate(Constant.LEARNING_RATE_INIT);
			}
			//Backpropagation learning, decrease the learning rate
			nets.get(n).backpropagate(output);
			//Learning rate decrease
			nets.get(n).setGlobalLearningRate(nets.get(n).getGlobalLearningRate()*0.8);
			try {
				heater.setUserValue(null);		
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}


		try {
			r.getActuatorByType(ActuatorType.RADIATOR).get(0).setDesiredValue(output);	
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
		integral.set(n, (integral.get(n) + error*deltaT.doubleValue())/1800);
		Double output = (Double) temp.getLastValue() + proportionalFactor.get(n)*error + integralFactor.get(n)*integral.get(n);
		heater.setLastValue(output);
		heater.setModified(true);
	}
}
