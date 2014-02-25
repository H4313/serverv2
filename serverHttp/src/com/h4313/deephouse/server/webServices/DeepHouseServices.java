package com.h4313.deephouse.server.webServices;

public interface DeepHouseServices {
	
	public String connect();
	
	public String runServer();
	
	public String dailyTemperature();
	
	public String weeklyTemperature();
	
	public String addSensor(String piece, String idCapteur, String type);
	
	public String addActuator(String piece,  String idActionneur, String type);
	
	public String getHouse();
	
	public String getDate();
	
	public String userAction(String piece, String typeAction, String valeur);
}
