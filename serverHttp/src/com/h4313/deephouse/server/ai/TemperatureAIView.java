package com.h4313.deephouse.server.ai;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import com.h4313.deephouse.housemodel.Room;


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
        frame = new JFrame(room.getClass().getSimpleName()+" "+room.getIdRoom()+" Temperature (°C)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationByPlatform(true);
        frame.setResizable(false);
        Integer sizeX = 800;
        Integer sizeY = 400;
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
//	    setTextFieldsAndButton();
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
		       Double scaleY = (double)(frame.getHeight()-zeroY)/(maxValueY-minValueY);

		       Line2D desiredLine = new Line2D.Double((previousTime-minValueX)*scaleX+zeroX,
			    		   							  (frame.getHeight() - ((previousDesired-minValueY)*scaleY+zeroY)),
			    		   							  (time-minValueX)*scaleX+zeroX,
			    		   							  (frame.getHeight() - ((desired-minValueY)*scaleY+zeroY)));
		       desiredLines.add(desiredLine);
		       
		       Line2D measuredLine = new Line2D.Double((previousTime-minValueX)*scaleX+zeroX,
													   (frame.getHeight() - ((previousMeasured-minValueY)*scaleY+zeroY)),
													   (time-minValueX)*scaleX+zeroX,
													   (frame.getHeight() - ((measured-minValueY)*scaleY+zeroY)));
		       measuredLines.add(measuredLine);
		       
		       Line2D outputLine = new Line2D.Double((previousTime-minValueX)*scaleX+zeroX,
												     (frame.getHeight() - ((previousOutput-minValueY)*scaleY+zeroY)),
												     (time-minValueX)*scaleX+zeroX,
												     (frame.getHeight() - ((output-minValueY)*scaleY+zeroY)));
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
