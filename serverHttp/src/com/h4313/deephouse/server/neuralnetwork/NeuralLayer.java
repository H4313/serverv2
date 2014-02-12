package com.h4313.deephouse.server.neuralnetwork;

import java.util.ArrayList;

public class NeuralLayer {

	/*################################
	 * ATTRIBUTES
	 ###############################*/
	
	protected ArrayList<Neuron> neurons;
	
	
	/*################################
	 * GETTERS
	 ###############################*/
	
	public ArrayList<Neuron> getNeurons() {
		return this.neurons;
	}
	
	/*################################
	 * CONSTRUCTORS
	 ###############################*/
	
	public NeuralLayer(int nbNeurons, Double learningRate) {
		neurons = new ArrayList<Neuron>();
		for(int i = 0 ; i < nbNeurons ; i++) {
			Neuron newNeuron = new SimpleNeuron(learningRate);
			neurons.add(newNeuron);
		}
	}
	
	/*################################
	 * PUBLIC METHODS
	 ###############################*/
	
	public void setInputs(NeuralLayer previousLayer) {
		for(int i = 0 ; i < neurons.size() ; i++) {
			neurons.get(i).setInputs(previousLayer);
		}
	}
	
	public void setOutputs(ArrayList<Double> outputs) {
		for(int i = 0 ; i < outputs.size() ; i++) {
			neurons.get(i).setOutput(outputs.get(i));
		}
	}
	
	public void randomizeWeights() {
		for(int i = 0 ; i < neurons.size() ; i++) {
			neurons.get(i).randomizeWeights();
		}
	}
	
	public void propagate() {
		for(int i = 0 ; i < neurons.size() ; i++) {
			neurons.get(i).propagate();
		}
	}
	
	public void deltaRule() {
		for(int i = 0 ; i < neurons.size() ; i++) {
			neurons.get(i).deltaRule();
		}
	}
	
	public void updateWeights() {
		for(int i = 0 ; i < neurons.size() ; i++) {
			neurons.get(i).updateWeights();
		}
	}
	
	public Double getOutput() {
		return neurons.get(0).getOutput();
	}
	
	public Double getGlobalLearningRate() {
		return neurons.get(0).getLearningRate();
	}
	
	public void setGlobalLearningRates(Double learningRate) {
		for(int i = 0 ; i < neurons.size() ; i++) {
			neurons.get(i).setLearningRate(learningRate);
		}
	}

}
