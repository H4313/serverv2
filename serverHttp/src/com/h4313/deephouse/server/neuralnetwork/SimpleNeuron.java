package com.h4313.deephouse.server.neuralnetwork;

import java.util.ArrayList;
import java.util.Random;

public class SimpleNeuron implements Neuron{
	
	/*################################
	 * ATTRIBUTES
	 ###############################*/

	protected Double output;
	
	protected ArrayList<Neuron> inputs;
	
	protected ArrayList<Double> weights;
	
	protected Double bias;
	
	protected Double delta;
	
	protected Double learningRate;
	
	/*################################
	 * SETTERS
	 ###############################*/
	
	public void setOutput(Double output) {
		this.output = output;
	}
	
	public void setDelta(Double delta) {
		this.delta = delta;
	}
	
	public void setLearningRate(Double lr) {
		this.learningRate = lr;
	}
	
	public void setWeights(ArrayList<Double> weights) {
		this.weights = weights;
	}
	
	public void setBias(Double bias) {
		this.bias = bias;
	}
	
	/*################################
	 * GETTERS
	 ###############################*/
	
	public Double getOutput() {
		return this.output;
	}
	
	public Double getLearningRate() {
		return learningRate;
	}
	
	public ArrayList<Double> getWeights() {
		return weights;
	}
	
	public Double getBias() {
		return bias;
	}
	
	/*################################
	 * CONSTRUCTORS
	 ###############################*/
	
	public SimpleNeuron(Double learningRate) {
		inputs = new ArrayList<Neuron>();
		weights = new ArrayList<Double>();
		this.learningRate = learningRate;
		this.delta = 0.0;
	}
	
	
	/*################################
	 * PRIVATE METHODS
	 ###############################*/
	
	private Double activationFunction(Double x) {
		return 1.7159*Math.tanh((2.0/3.0)*x);
	}
	
	private Double activationFunctionDerivative(Double x) {
		return ((1.7159-x)*(1.7159+x)*((2.0/3.0)/1.7159));
	}
	
	/*################################
	 * PUBLIC METHODS
	 ###############################*/
	
	public void setInputs(NeuralLayer previousLayer) {
		this.inputs = previousLayer.getNeurons();
	}
	
	public void addToDelta(Double delta) {
		this.delta += delta;
	}
	
	public void randomizeWeights() {
		weights.clear();
		Random randomNumber = new Random();
		for(int i = 0 ; i < inputs.size() ; i++) {
			weights.add(randomNumber.nextGaussian()/Math.sqrt(inputs.size()));
		}
		bias = randomNumber.nextGaussian()/Math.sqrt(inputs.size());
	}
	
	public void propagate() {
		Double temp = 0.0;
		for(int i = 0 ; i < inputs.size() ; i++) {
			temp += weights.get(i) * inputs.get(i).getOutput();
		}
		//temp /= inputs.size();
		temp += bias;
		temp = activationFunction(temp);
		output = temp;
	}
	
	public void deltaRule() {
		delta = delta * activationFunctionDerivative(output);
		for(int i = 0 ; i < inputs.size() ; i++) {
			inputs.get(i).addToDelta(weights.get(i)*delta);
		}
	}
	
	public void updateWeights() {
		for(int i = 0 ; i < weights.size() ; i++) {
			weights.set(i , weights.get(i) + learningRate * delta * inputs.get(i).getOutput());
		}
		bias = bias + learningRate * delta;
		delta = 0.0;
	}
	

}
