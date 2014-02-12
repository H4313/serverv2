package com.h4313.deephouse.server.neuralnetwork;

import java.util.ArrayList;

public abstract class NeuralNetworkTrainer {
	
	public static void trainTemperatures(NeuralNetwork network, int nbEpochs, int nbIters) {
		ArrayList<Double> inputs = new ArrayList<Double>();
		for(int i = 0 ; i < nbEpochs ; i++) {
			for(int j = 0 ; j < nbIters ; j++) {
				inputs.clear();
				Double month = Math.floor(Math.random()*12) + 1;
				Double hour = Math.floor(Math.random()*24);
				inputs.add(month/6.0-1.0);
				inputs.add(hour/12.0-1.0);
				network.propagate(inputs);
				System.out.println("Iteration "+j+" (Epoch "+i+") | RESULT = " + network.getOutputScaled() +" | Month = "+month+" | Hour = "+hour);
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
					desiredOutput -= 1.5;
				}
				else if(month <= 5.0 || month >= 9.0) {
					//nothing changes
				}
				else {
					desiredOutput += 2.0;
				}
				network.backpropagate(desiredOutput);
				
			}
			network.setGlobalLearningRate(network.getGlobalLearningRate()/1.2);
		}
	}
}
