package com.h4313.deephouse.server.webServices;

import org.json.JSONObject;

import com.h4313.deephouse.exceptions.DeepHouseException;
import com.h4313.deephouse.housemodel.House;

public interface DeepHouseServices {
	
	public String connect() throws DeepHouseException;
	
	public String addSensor(String piece, String idCapteur, String type) throws DeepHouseException;
	
	public String addActuator(String piece,  String idActionneur, String type) throws DeepHouseException;
	
	public House getHouse();
	
	public String userAction(String piece, String typeAction, String valeur, String idActionneur) throws DeepHouseException;
}
