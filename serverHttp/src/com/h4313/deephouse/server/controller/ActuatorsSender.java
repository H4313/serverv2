package com.h4313.deephouse.server.controller;

import java.util.ArrayList;

import com.h4313.deephouse.network.CallBack;
import com.h4313.deephouse.network.TcpSender;

public class ActuatorsSender implements CallBack {
	
	private ArrayList<String> messages;
	
	private TcpSender tcpSender;
	
	public ActuatorsSender(String ip, int port)
	{
		messages = new ArrayList<String>();
		
		tcpSender = null;
		try {
			tcpSender = new TcpSender(ip, port, this);
			tcpSender.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Termination
//		try {
//			tcpSender.closeSender();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
	public void submitMessage(String s)
	{
		messages.add(s);
	}
	
	public String callBack(String s)
	{
 		String value = null;
		
		if(messages.size() > 0)
		{
			// Takes 1st message
			value = messages.get(0);
			messages.remove(0);
		}
		
		return value;
	}
	
	public void stopSender() throws Exception
	{
		this.tcpSender.closeSender();
	}
}
