package com.h4313.deephouse.server.webServices;

import com.h4313.deephouse.housemodel.House;

public interface DeepHouseServices {
	
	public String connect();
	
	public String addSensor(String piece, String idCapteur, String type);
	
	public String addActuator(String piece,  String idActionneur, String type);
	
	public House getHouse();
	
	public String userAction(String piece, String typeAction, String valeur, String idActionneur);
}
