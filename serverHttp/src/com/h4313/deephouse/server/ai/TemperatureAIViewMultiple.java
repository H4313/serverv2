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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.h4313.deephouse.housemodel.Room;

public class TemperatureAIViewMultiple {
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
	
	private static ArrayList<Double> measured;
	
	private static double previousTime;
	
	private static List<Room> rooms;
	
	private static ArrayList<Double> previousMeasured;
	
	private static List<List<Line2D>> measuredLines;
	
	private static ArrayList<Color> colors;
	
	/*###################################
	 * INIT
	 ####################################*/
	
	public static void initTemperatureAIViewMultiple(Double minValueX, Double maxValueX, Double minValueY, Double maxValueY, List<Room> rooms)
	{
		TemperatureAIViewMultiple.rooms = rooms;
        frame = new JFrame("Temperatures (°C)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationByPlatform(true);
        frame.setResizable(false);
        Integer sizeX = 800;
        Integer sizeY = 400;
        frame.setSize(sizeX,sizeY);
        TemperatureAIViewMultiple.minValueX = minValueX;
        TemperatureAIViewMultiple.maxValueX = maxValueX;
        TemperatureAIViewMultiple.minValueY = minValueY;
        TemperatureAIViewMultiple.maxValueY = maxValueY;
        TemperatureAIViewMultiple.time = 0.0;
        TemperatureAIViewMultiple.measured = new ArrayList<Double>();
        TemperatureAIViewMultiple.previousTime = 0.0;
        TemperatureAIViewMultiple.previousMeasured = new ArrayList<Double>();
	    measuredLines = new ArrayList<List<Line2D>>();
	    colors = new ArrayList<Color>();
	    for(int i = 0 ; i < rooms.size() ; i++) {
	    	measuredLines.add(new ArrayList<Line2D>());
	    	measured.add(0.0);
	    	previousMeasured.add(0.0);
	    }
	    colors.add(Color.blue);
	    colors.add(Color.green);
	    colors.add(Color.yellow);
	    colors.add(Color.red);
	    colors.add(Color.magenta);
	    colors.add(Color.cyan);
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
		       int zeroX = frame.getWidth()/12; 
		       Double scaleX = (double)(frame.getWidth()-zeroX)/(maxValueX-minValueX);
		       Double scaleY = (double)(zeroY)/(maxValueY-minValueY);
		       
		       for(int i = 0 ; i < rooms.size() ; i++) {
			       g2.setColor(colors.get(i%colors.size()));
			       Line2D measuredLine = new Line2D.Double((previousTime-minValueX)*scaleX+zeroX,
							   (zeroY - ((previousMeasured.get(i)-minValueY)*scaleY)),
						   (time-minValueX)*scaleX+zeroX,
						   (zeroY - ((measured.get(i)-minValueY)*scaleY)));
			       measuredLines.get(i).add(measuredLine);
				
			       for(int j = 0 ; j < measuredLines.get(i).size() ; j++) {
			    	   g2.draw(measuredLines.get(i).get(j));
			       }
			       previousMeasured.set(i, measured.get(i));  
		       }
		       previousTime = time; 
		       
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
		       g2.drawString(numberFormat.format(minValueX), zeroX+5, zeroY+15);
			   g2.drawString(numberFormat.format(maxValueX), frame.getWidth()-8*(String.valueOf(numberFormat.format(maxValueX)).length()), zeroY+15);
		       g2.drawString(numberFormat.format(maxValueY), zeroX-8*(String.valueOf(numberFormat.format(maxValueY)).length()), 0+15);
		       g2.drawString(numberFormat.format(minValueY), zeroX-8*(String.valueOf(numberFormat.format(minValueY)).length()), zeroY-8);
		       
		       for(int i = 0 ; i < rooms.size() ; i++) {
			       g2.setColor(colors.get(i%colors.size()));
			       g2.drawString(rooms.get(i).getClass().getSimpleName(),0, 100+15*i);   
		       }
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
	public static void updateView(double time, ArrayList<Double> measured)
	{
		if(initialized) {
			if(previousTime > time) {
				TemperatureAIViewMultiple.previousTime -= 24.0;
				for(int i = 0 ; i < rooms.size() ; i++) {
				    measuredLines.get(i).clear();	
				}
			}
			TemperatureAIViewMultiple.time = time;
			for(int i = 0 ; i < rooms.size() ; i++) {
				TemperatureAIViewMultiple.measured.set(i,measured.get(i));
			}
			frame.repaint();	
		}
	}
}
