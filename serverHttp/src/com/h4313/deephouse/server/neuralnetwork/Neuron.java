package com.h4313.deephouse.server.neuralnetwork;

import java.util.ArrayList;

public interface Neuron {
	
	public void setDelta(Double delta);
	
	public void setOutput(Double output);
	
	public Double getOutput();
	
	public void setInputs(NeuralLayer previousLayer);
	
	public void addToDelta(Double delta);
	
	public void randomizeWeights();
	
	public void propagate();
	
	public void deltaRule();
	
	public void updateWeights();
	
	public Double getLearningRate();
	
	public void setLearningRate(Double lr);
	
	public ArrayList<Double> getWeights();
	
	public Double getBias();
	
	public void setWeights(ArrayList<Double> weights);
	
	public void setBias(Double bias);
}
