package com.h4313.deephouse.server.neuralnetwork;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public abstract class NeuralNetworkTester {

	public static void testTemperatures(NeuralNetwork network, int nbTests) {
		ArrayList<Double> inputs = new ArrayList<Double>();
		Double meanErrorNumerator = 0.0;
		Double meanErrorDivisor = 0.0;
		for(int i = 0 ; i < nbTests ; i++) {
			inputs.clear();
			Double month = Math.floor(Math.random()*12) + 1;
			Double hour = Math.floor(Math.random()*24);
			inputs.add(month/6.0-1.0);
			inputs.add(hour/12.0-1.0);
			network.propagate(inputs);
			Double desiredOutput = 0.0;
			if(hour <= 6.0 || hour >= 23.0) {
				desiredOutput = 17.0;
			}
			else if(hour <= 8.0 || hour >= 21) {
				desiredOutput = 20.0;
			}
			else {
				desiredOutput = 22.0;
			}
			if(month <= 2.0 || month >= 11.0) {
				desiredOutput -= 2.0;
			}
			else if(month <= 5.0 || month >= 9.0) {
				//nothing changes
			}
			else {
				desiredOutput += 2.0;
			}
			System.out.println("Test "+i+" | Month = "+month+" | Hour = "+hour);
			System.out.println("RESULT = " + network.getOutputScaled() +" | EXPECTED = "+desiredOutput);
			System.out.println();
			
			meanErrorNumerator += network.getOutputScaled() - desiredOutput;
			meanErrorDivisor++;
		}
		System.out.println("#############################################");
		System.out.println("Mean Temperature Error = "+meanErrorNumerator/meanErrorDivisor);
	}
	
	public static void printTemperaturesText(NeuralNetwork network) {
		try {
	        BufferedWriter out = new BufferedWriter(new FileWriter("temperatures.txt"));
			ArrayList<Double> inputs = new ArrayList<Double>();
			for(int m = 1 ; m <= 12 ; m++) {
				for(int h = 0 ; h < 24 ; h++) {
					inputs.clear();
					inputs.add((double)m/6.0 - 1.0);
					inputs.add((double)h/12.0 - 1.0);
					network.propagate(inputs);
		            out.write(m+";"+h+";"+network.getOutputScaled());
		            out.newLine();
				}
				out.newLine();
			}
            out.close();
        } catch (IOException e) {}
	}
	
}
