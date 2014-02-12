package com.h4313.deephouse.server.neuralnetwork;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NeuralNetwork {
	
	/*################################
	 * ATTRIBUTES
	 ###############################*/
	protected ArrayList<NeuralLayer> neuralLayers;
	
	protected Double scaleMax;
	
	protected Double scaleMin;
	
	/*################################
	 * CONSTRUCTORS
	 ###############################*/
	
	public NeuralNetwork(int nbLayers, int[] nbNeuronsPerLayer, Double learningRate) {
		neuralLayers = new ArrayList<NeuralLayer>();
		for(int i = 0 ; i < nbLayers ; i++) {
			NeuralLayer newLayer = new NeuralLayer(nbNeuronsPerLayer[i], learningRate);
			if(i != 0) {
				newLayer.setInputs(neuralLayers.get(i-1));	
			}
			newLayer.randomizeWeights();
			neuralLayers.add(newLayer);
		}
		scaleMax = 1.0;
		scaleMin = -1.0;
	}
	
	public NeuralNetwork(int nbLayers, int[] nbNeuronsPerLayer, Double learningRate, Double scaleMin, Double scaleMax) {
		neuralLayers = new ArrayList<NeuralLayer>();
		for(int i = 0 ; i < nbLayers ; i++) {
			NeuralLayer newLayer = new NeuralLayer(nbNeuronsPerLayer[i], learningRate);
			if(i != 0) {
				newLayer.setInputs(neuralLayers.get(i-1));	
			}
			newLayer.randomizeWeights();
			neuralLayers.add(newLayer);
		}
		this.scaleMax = scaleMax;
		this.scaleMin = scaleMin;
	}
	
	public NeuralNetwork(String filename, Double learningRate) {
		try {
			 
			File fXmlFile = new File(filename);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
		 
			scaleMin = Double.valueOf(doc.getDocumentElement().getAttribute("scaleMin"));
			scaleMax = Double.valueOf(doc.getDocumentElement().getAttribute("scaleMax"));
		 
			NodeList nList = doc.getElementsByTagName("NeuralLayer");
			neuralLayers = new ArrayList<NeuralLayer>();
			for(int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i); 
				Element eElement = (Element) nNode;
				NodeList neuronList = eElement.getElementsByTagName("Neuron");
				NeuralLayer newLayer = new NeuralLayer(neuronList.getLength(), learningRate);
				if(i != 0) {
					newLayer.setInputs(neuralLayers.get(i-1));	
				}
				for(int j = 0 ; j < neuronList.getLength() ; j++) {
					Node neuronNode = neuronList.item(j); 
					Element neuronElement = (Element) neuronNode;
					NodeList weightsList = neuronElement.getElementsByTagName("Weight");
					newLayer.getNeurons().get(j).setBias(Double.valueOf(neuronElement.getElementsByTagName("Bias").item(0).getTextContent()));
					ArrayList<Double> weights = new ArrayList<Double>();
					for(int k = 0 ; k < weightsList.getLength() ; k++) {
						weights.add(Double.valueOf(weightsList.item(k).getTextContent()));
					}
					newLayer.getNeurons().get(j).setWeights(weights);
				}
				neuralLayers.add(newLayer);
			}
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	
	/*################################
	 * PROTECTED METHODS
	 ###############################*/
	
	protected void deltaRule(Double desiredOutput) {
		Double delta = desiredOutput - neuralLayers.get(neuralLayers.size()-1).getOutput();
		neuralLayers.get(neuralLayers.size()-1).getNeurons().get(0).setDelta(delta);
		for(int i = neuralLayers.size()-1 ; i > 0 ; i--) {
			neuralLayers.get(i).deltaRule();
		}
	}
	
	protected void updateWeights() {
		for(int i = 1 ; i < neuralLayers.size() ; i++) {
			neuralLayers.get(i).updateWeights();
		}
	}
	
	/*################################
	 * PUBLIC METHODS
	 ###############################*/
	
	public void propagate(ArrayList<Double> inputs) {
		neuralLayers.get(0).setOutputs(inputs);
		for(int i = 1 ; i < neuralLayers.size() ; i++) {
			neuralLayers.get(i).propagate();
		}
	}
	
	public void backpropagate(Double desiredOutput) {
		desiredOutput = 2*(desiredOutput-scaleMin)/(scaleMax-scaleMin) - 1;
		this.deltaRule(desiredOutput);
		this.updateWeights();
	}

	
	public Double getOutput() {
		return neuralLayers.get(neuralLayers.size()-1).getOutput();
	}
	
	public Double getOutputScaled() {
		return (getOutput()+1)*(scaleMax-scaleMin)/2 + scaleMin;
	}
	
	public void displayNetwork() {
		System.out.println("Number of neural layers: "+neuralLayers.size());
		System.out.println();
		for(int i = 0 ; i < neuralLayers.size() ; i++) {
			System.out.println("Neural Layer "+i+" ("+neuralLayers.get(i).getNeurons().size()+" neurons)");
			System.out.println();
		}
	}
	
	public Double getGlobalLearningRate() {
		return neuralLayers.get(0).getGlobalLearningRate();
	}
	
	public void setGlobalLearningRate(Double learningRate) {
		for(NeuralLayer n : neuralLayers) {
			n.setGlobalLearningRates(learningRate);
		}
	}
	
	public void exportToXML(String filename) {
		try {
		  	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	 
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("NeuralNetwork");
			doc.appendChild(rootElement);
			
			Attr attr = doc.createAttribute("scaleMin");
			attr.setValue(scaleMin.toString());
			rootElement.setAttributeNode(attr);
			
			attr = doc.createAttribute("scaleMax");
			attr.setValue(scaleMax.toString());
			rootElement.setAttributeNode(attr);
	 
			// neural layers elements
			for(NeuralLayer n : neuralLayers) {
				Element neuralLayer = doc.createElement("NeuralLayer");
				rootElement.appendChild(neuralLayer);
				for(int i = 0 ; i < n.getNeurons().size() ; i++) {
					Element neuron = doc.createElement("Neuron");
					neuralLayer.appendChild(neuron);
					for(int j = 0 ; j < n.getNeurons().get(i).getWeights().size() ; j++) {
						Element weight = doc.createElement("Weight");
						weight.appendChild(doc.createTextNode(n.getNeurons().get(i).getWeights().get(j).toString()));
						neuron.appendChild(weight);
					}
					Element bias = doc.createElement("Bias");
					bias.appendChild(doc.createTextNode(n.getNeurons().get(i).getBias().toString()));
					neuron.appendChild(bias);
				}
			}
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(filename));
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(source, result);
	 
			System.out.println("File saved!");
			
		  } catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		  } catch (TransformerException tfe) {
			tfe.printStackTrace();
		  }
	}

}
