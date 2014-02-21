package com.h4313.deephouse.server.webServices;

public interface DeepHouseServices {
	
	public String connect();
	
	public String runServer();
	
	public String chart();
	
	public String addSensor(String piece, String idCapteur, String type);
	
	public String addActuator(String piece,  String idActionneur, String type);
	
	public String getHouse();
	
	public String userAction(String piece, String typeAction, String valeur);
}
