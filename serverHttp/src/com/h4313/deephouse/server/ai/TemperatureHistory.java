package com.h4313.deephouse.server.ai;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.h4313.deephouse.housemodel.House;
import com.h4313.deephouse.housemodel.Room;
import com.h4313.deephouse.sensor.SensorType;
import com.h4313.deephouse.server.util.Constant;
import com.h4313.deephouse.util.DeepHouseCalendar;

public abstract class TemperatureHistory
{
	private static HashMap<Integer, ArrayList<Double>> lastDay;
	private static int previousHour = -1;
//	private static int previousDay;
	
	public static void initTemperatureHistory() {
		lastDay = new HashMap<Integer, ArrayList<Double>>();
		for(Room room : House.getInstance().getRooms())
		{
			lastDay.put(room.getIdRoom(), new ArrayList<Double>());
		}
	}
	
	public static void run() {
		Calendar cal = DeepHouseCalendar.getInstance().getCalendar();
		int currentHour = cal.get(Calendar.HOUR_OF_DAY);
		
		if(currentHour != previousHour)
		{		
			ArrayList<Double> temperatures = null;
			for(Room room : House.getInstance().getRooms())
			{
				temperatures = lastDay.get(room.getIdRoom());
				
				if(currentHour == 0)
					temperatures.clear();
				
				temperatures.add((Double) room.getSensorByType(SensorType.TEMPERATURE).get(0).getLastValue());
			}
			
			previousHour = currentHour;
		}
	}
	
	public static HashMap<Integer, ArrayList<Double>> getLastDay()
	{
		return lastDay;
	}
}
