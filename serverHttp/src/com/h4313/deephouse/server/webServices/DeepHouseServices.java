package com.h4313.deephouse.server.webServices;

import org.json.JSONObject;

import com.h4313.deephouse.exceptions.DeepHouseException;
import com.h4313.deephouse.housemodel.House;

public interface DeepHouseServices {
	
	public void addSensor(JSONObject json) throws DeepHouseException;
	
	public void addActuator(JSONObject json) throws DeepHouseException;
	
	public House getHouse();
	
	public void userAction(JSONObject json) throws DeepHouseException;
}
