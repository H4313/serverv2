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
	private static HashMap<Integer, ArrayList<Double>> lastWeek;
	private static int previousHour = -1;
	
	public static void initTemperatureHistory() {
		lastDay = new HashMap<Integer, ArrayList<Double>>();
		lastWeek = new HashMap<Integer, ArrayList<Double>>();
		for(Room room : House.getInstance().getRooms())
		{
			lastDay.put(room.getIdRoom(), new ArrayList<Double>());
			lastWeek.put(room.getIdRoom(), new ArrayList<Double>());
		}
	}
	
	public static void run() {
		Calendar cal = DeepHouseCalendar.getInstance().getCalendar();
		int currentHour = cal.get(Calendar.HOUR_OF_DAY);
		int currentDay = cal.get(Calendar.DAY_OF_WEEK);
		
		if(currentHour != previousHour)
		{		
			ArrayList<Double> dailyTemperatures = null;
			ArrayList<Double> weeklyTemperatures = null;
			Double temperature = null;
			for(Room room : House.getInstance().getRooms())
			{
				dailyTemperatures = lastDay.get(room.getIdRoom());
				weeklyTemperatures = lastWeek.get(room.getIdRoom());
				
				if(currentHour == 0)
				{
					dailyTemperatures.clear();
					
					if(currentDay == Calendar.MONDAY)
					{
						weeklyTemperatures.clear();
					}
				}
				
				temperature = (Double) room.getSensorByType(SensorType.TEMPERATURE).get(0).getLastValue();
				dailyTemperatures.add(new Double(temperature.doubleValue()));
				weeklyTemperatures.add(new Double(temperature.doubleValue()));
			}
			
			previousHour = currentHour;
		}
	}
	
	public static HashMap<Integer, ArrayList<Double>> getLastDay()
	{
		return lastDay;
	}
	
	public static HashMap<Integer, ArrayList<Double>> getLastWeek()
	{
		return lastWeek;
	}
}
