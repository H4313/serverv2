package com.h4313.deephouse.server.ai;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.h4313.deephouse.housemodel.Room;
import com.h4313.deephouse.util.DeepHouseCalendar;


/**
 * This class is a view of the class TemperatureAI
 * 
 * @author Julien Cumin
 *
 */
public abstract class TemperatureAIView 
{
	/*###################################
	 * PRIVATE ATTRIBUTES
	 ####################################*/
	
	private static boolean initialized;
	
	private static Double minValueX;
	
	private static Double maxValueX;
	
	private static double maxValueY;
	
	private static double minValueY;
	
	private static JFrame frame;
	
	private static double time;
	
	private static double desired;
	
	private static double measured;
	
	private static double output;
	
	private static double previousTime;
	
	private static double previousDesired;
	
	private static double previousMeasured;
	
	private static double previousOutput;
	
	private static ArrayList<Line2D> desiredLines;
	
	private static ArrayList<Line2D> measuredLines;
	
	private static ArrayList<Line2D> outputLines;
	
	/*###################################
	 * INIT
	 ####################################*/
	
	public static void initTemperatureAIView(Double minValueX, Double maxValueX, Double minValueY, Double maxValueY, Room room)
	{
        frame = new JFrame(room.getClass().getSimpleName()+" "+room.getIdRoom()+" Temperature (�C)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationByPlatform(true);
        frame.setResizable(false);
        Integer sizeX = 1200;
        Integer sizeY = 800;
        frame.setSize(sizeX,sizeY);
	    TemperatureAIView.minValueX = minValueX;
	    TemperatureAIView.maxValueX = maxValueX;
	    TemperatureAIView.minValueY = minValueY;
	    TemperatureAIView.maxValueY = maxValueY;
	    TemperatureAIView.desired = 0.0;
	    TemperatureAIView.time = 0.0;
	    TemperatureAIView.measured = 0.0;
	    TemperatureAIView.output = 0.0;
	    TemperatureAIView.previousDesired = 0.0;
	    TemperatureAIView.previousTime = 0.0;
	    TemperatureAIView.previousMeasured = 0.0;
	    TemperatureAIView.previousOutput = 0.0;
	    desiredLines = new ArrayList<Line2D>();
	    measuredLines = new ArrayList<Line2D>();
	    outputLines = new ArrayList<Line2D>();
	    setPaintAndWheel();
	    initialized = true;
        frame.setVisible(true);
	}
	
	
	/*###################################
	 * PRIVATE METHODS
	 ####################################*/
	
	/**
	 * Sets the paint function and the scrolling function of the frame
	 */
	private static void setPaintAndWheel()
	{
        JPanel panel = new JPanel() 
 	   	{
			private static final long serialVersionUID = 1L;
			public void paintComponent( Graphics g ) 
            {
               super.paintComponent(g);
               Graphics2D g2 = (Graphics2D)g;
               //Anti-aliasing
               g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
               
               //g2.clearRect(0,0,frame.getWidth(),frame.getHeight());
               
		       int zeroY = frame.getHeight()-frame.getHeight()/6;
		       int zeroX = frame.getWidth()/12+50; 
		       Double scaleX = (double)(frame.getWidth()-zeroX)/(maxValueX-minValueX);
		       Double scaleY = (double)(zeroY)/(maxValueY-minValueY);
		       
		       g2.setColor(Color.black);
		       g2.setStroke(new BasicStroke(1));
		       Line2D xAxis = new Line2D.Double(0,zeroY,(maxValueX-minValueX)*scaleX+zeroX,zeroY);
		       g2.draw(xAxis);
		       Line2D yAxis = new Line2D.Double(zeroX,0,zeroX,frame.getHeight());
		       g2.draw(yAxis);
		       Font fontExtremeBounds = new Font(g2.getFont().getName(),Font.BOLD,g2.getFont().getSize());
		       g2.setFont(fontExtremeBounds);
		       DecimalFormat numberFormat = new DecimalFormat("#.00");
		       DecimalFormatSymbols numberSeparator = new DecimalFormatSymbols();
		       numberSeparator.setDecimalSeparator('.');
		       numberFormat.setDecimalFormatSymbols(numberSeparator);

		       g2.drawString(String.valueOf(minValueX.intValue()), zeroX+5, zeroY+25);
			   g2.drawString(String.valueOf(maxValueX.intValue()), frame.getWidth()-10*(String.valueOf(maxValueX.intValue()).length()), zeroY+25);
		       g2.drawString(numberFormat.format(maxValueY), zeroX-9*(String.valueOf(numberFormat.format(maxValueY)).length()), 10);
		       g2.drawString(numberFormat.format(minValueY), zeroX-9*(String.valueOf(numberFormat.format(minValueY)).length()), zeroY-5);
		       
		       for(int i = 1 ; i < (maxValueX-minValueX) ; i++) {
		    	   g2.setColor(Color.black);
		    	   int xGrad = (int) (minValueX+i);
		    	   g2.drawString(String.valueOf(xGrad), (int) ((xGrad-minValueX)*scaleX+zeroX), zeroY+25);
		    	   Line2D xGradLine = new Line2D.Double(((xGrad-minValueX)*scaleX+zeroX),zeroY,((xGrad-minValueX)*scaleX+zeroX),zeroY+10);
		    	   g2.draw(xGradLine);
		    	   
		    	   g2.setColor(Color.lightGray);
		    	   Line2D xLine = new Line2D.Double(((xGrad-minValueX)*scaleX+zeroX),0,((xGrad-minValueX)*scaleX+zeroX),zeroY-1);
		    	   g2.draw(xLine);
		       }
		       
		       for(int i = 1 ; i < (maxValueY-minValueY) ; i++) {
		    	   g2.setColor(Color.black);
		    	   int yGrad = (int) (minValueY+i);
		    	   g2.drawString(numberFormat.format(yGrad), zeroX-9*(String.valueOf(numberFormat.format(yGrad)).length()), (int) (zeroY - ((yGrad-minValueY)*scaleY)));
		    	   Line2D yGradLine = new Line2D.Double(zeroX-10,(int) (zeroY - ((yGrad-minValueY)*scaleY)),zeroX,(int) (zeroY - ((yGrad-minValueY)*scaleY)));
		    	   g2.draw(yGradLine);
		    	   
		    	   g2.setColor(Color.lightGray);
		    	   Line2D yLine = new Line2D.Double(zeroX+1,(int) (zeroY - ((yGrad-minValueY)*scaleY)),frame.getWidth(),(int) (zeroY - ((yGrad-minValueY)*scaleY)));
		    	   g2.draw(yLine);
		       }
		       
		       g2.setColor(Color.black);
		       Font fontDate = new Font(g2.getFont().getName(),Font.BOLD,g2.getFont().getSize()+5);
		       g2.setFont(fontDate);
		       SimpleDateFormat formatter = new SimpleDateFormat();
		       String currentDate = formatter.format(DeepHouseCalendar.getInstance().getCalendar().getTime());
		       if(DeepHouseCalendar.getInstance().getCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
		    	   currentDate = "Monday " + currentDate;
		       else if(DeepHouseCalendar.getInstance().getCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY)
		    	   currentDate = "Tuesday " + currentDate;
		       else if(DeepHouseCalendar.getInstance().getCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY)
		    	   currentDate = "Wednesday " + currentDate;
		       else if(DeepHouseCalendar.getInstance().getCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY)
		    	   currentDate = "Thursday " + currentDate;
		       else if(DeepHouseCalendar.getInstance().getCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
		    	   currentDate = "Friday " + currentDate;
		       else if(DeepHouseCalendar.getInstance().getCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
		    	   currentDate = "Saturday " + currentDate;
		       else if(DeepHouseCalendar.getInstance().getCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
		    	   currentDate = "Sunday " + currentDate;
		       
		       g2.drawString(currentDate,frame.getWidth()/2,frame.getHeight()-50);
		       
		       
		       g2.setColor(Color.green);
		       g2.drawString("Desired",0, 115);
		       g2.setColor(Color.red);
		       g2.drawString("Measured",0, 130);
		       g2.setColor(Color.blue);
		       g2.drawString("Heater",0,145);
		       
		       Line2D desiredLine = new Line2D.Double((previousTime-minValueX)*scaleX+zeroX,
													  (zeroY - ((previousDesired-minValueY)*scaleY)),
													  (time-minValueX)*scaleX+zeroX,
													  (zeroY - ((desired-minValueY)*scaleY)));
		       desiredLines.add(desiredLine);
			
		       Line2D measuredLine = new Line2D.Double((previousTime-minValueX)*scaleX+zeroX,
													   (zeroY - ((previousMeasured-minValueY)*scaleY)),
													   (time-minValueX)*scaleX+zeroX,
													   (zeroY - ((measured-minValueY)*scaleY)));
		       measuredLines.add(measuredLine);
			
		       Line2D outputLine = new Line2D.Double((previousTime-minValueX)*scaleX+zeroX,
													 (zeroY - ((previousOutput-minValueY)*scaleY)),
												     (time-minValueX)*scaleX+zeroX,
												     (zeroY - ((output-minValueY)*scaleY)));
		       outputLines.add(outputLine);
			
		       for(int i = 0 ; i < desiredLines.size() ; i++) {
					g2.setColor(Color.green);
					g2.draw(desiredLines.get(i));
					g2.setColor(Color.red);
					g2.draw(measuredLines.get(i));
					g2.setColor(Color.blue);
					g2.draw(outputLines.get(i));	
		       }
			
		       previousDesired = desired;
		       previousMeasured = measured;
		       previousOutput = output;
		       previousTime = time;
            }
	    };
	    
        frame.add(panel);
	}

	
	
	/*###################################
	 * PUBLIC METHODS
	 ####################################*/
	
	/**
	 * Refreshes the frame
	 */
	public static void updateView(double time, double desired, double measured, double output)
	{
		if(initialized) {
			if(previousTime > time) {
			    TemperatureAIView.previousTime -= 24.0;
			    desiredLines.clear();
			    measuredLines.clear();
			    outputLines.clear();
			}
			TemperatureAIView.desired = desired;
			TemperatureAIView.time = time;
			TemperatureAIView.measured = measured;
			TemperatureAIView.output = output;
			frame.repaint();	
		}
	}
}
