package com.h4313.deephouse.server.controller;

import java.io.IOException;
import java.util.ArrayList;

import com.h4313.deephouse.network.CallBack;
import com.h4313.deephouse.network.TcpReceiver;

public class SensorsListener implements CallBack {
	
	private volatile boolean record;
	
	private ArrayList<String> messages;
	
	private TcpReceiver tcpReceiver;
	
	public SensorsListener(int port)
	{
		messages = new ArrayList<String>();
		
		this.tcpReceiver = null;
		try {
			tcpReceiver = new TcpReceiver(port, this);
			tcpReceiver.start();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println(tcpClient.receive());
//		try {
//			tcpReceiver.closeReceiver();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		this.record = true;
	}
	
	public String callBack(String s)
	{
		if(this.record)
		{
			messages.add(s);
//			this.record = false;
		}
		
		return null;
	}
	
	public ArrayList<String> clearBuffer() {
		ArrayList<String> buffer = new ArrayList<String>(messages);
		messages.clear();
		return buffer;
	}
	
	public void stopListener() throws Exception
	{
		this.tcpReceiver.closeReceiver();
	}
}
