package com.h4313.deephouse.server.webServices;

import org.json.JSONObject;

import com.h4313.deephouse.exceptions.DeepHouseException;

public interface DeepHouseServices {
	
	public void addSensor(JSONObject json) throws DeepHouseException;
	
	public void addActuator(JSONObject json) throws DeepHouseException;
	
	public void getHouse() throws DeepHouseException;
}
