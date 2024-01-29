package viewer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import layers.Layer;
import layers.LayerLinear;
import layers.LayerSigmoid;
import learning_module.LearningModule;
import neural_net.Network;
import nn_interface.Node;
import tools.NodeFactory;
import units.Type;
import units.Unit;

public class NnHandler implements Runnable {

	final Thread PROC = new Thread(this);
	
	/** 1st - X, 2nd - Y */
	final int NB_INPUTS = 2;
	
	double w;
	double h;
	
	Network net;
	LearningModule learningMod;
	
	public int neuronsPerLayer = 10;
	
	public ArrayList<Type> layerTypes;
	final int DEFAULT_LAYER_NB = 3;
	
	public ArrayList<Vector3D> data;
	public ArrayList<Vector3D> trainingData;
	public HashSet<Vector3D> testData;
	
	public int dataClassesNb = 4;
	int clustersPerClass = 3;
	int classSize = 1000;
	double classDispersionCoeff = 0.3;
	double classDensityCoeff = 0.25;

	int dataSize;
	int trainingDataSize;
	int testDataSize;
	double testTrainingDataProportion = 0.3;
	
	int classRadius;
	
	int dataPointer = 0;
	
	int feedClass = -1;

	Queue<Boolean> successHistory;
	int currentSuccessRate;
	
	HashSet<Observer> viewers;
	
	boolean doTrain = false;
	boolean isTraining = false;
	
	public NnHandler(double maxW, double maxH) {
		this.w = maxW;
		this.h = maxH;	

		initLayers();
		
		initAll();
		
		viewers = new HashSet<Observer>();
		
		computeTestSuccessRate();
		notifyViewers();
	}
	
	void initAll() {
		generateData(w, h);
		initNet();	
		successHistory = new LinkedList<Boolean>();
		currentSuccessRate = 0;
	}
	
	void computeClassRadius() {
		classRadius = (int) (Math.min(w, h)/2*classDispersionCoeff);
	}
	
	void computeDataSize() {
		dataSize = classSize * clustersPerClass * dataClassesNb;
		trainingDataSize = (int) (dataSize/(1+testTrainingDataProportion));
		testDataSize = dataSize - trainingDataSize;
	}
	
	void generateData(double maxW, double maxH) {
		
		computeDataSize();
		computeClassRadius();
		
		data = new ArrayList<Vector3D>(dataSize);
		trainingData = new ArrayList<Vector3D>(trainingDataSize);
		testData = new HashSet<Vector3D>(testDataSize);
		
		double genZoneW = maxW-classRadius*2;
		double genZoneH = maxH-classRadius*2;
		double adjustZoneX = -maxW/2 + classRadius;
		double adjustZoneY = -maxH/2 + classRadius;
		
		for(int c=0; c<dataClassesNb; c++) {

			for(int i=0; i<clustersPerClass; i++) {
				// generate center
				
				double centerX = Math.random() * genZoneW + adjustZoneX;
				double centerY = Math.random() * genZoneH + adjustZoneY;
				
				// distribute data around the center

				for(int p=0; p<classSize; p++) {
					double pos = Math.pow(Math.random(),1/classDensityCoeff) * classRadius;
					double theta = Math.random() * Math.PI * 2;
					
					double x = Math.cos(theta) * pos + centerX;
					double y = Math.sin(theta) * pos + centerY;
					
					Vector3D point = new Vector3D(x, y, c);
	
					data.add(point);
				}
			}
		}
		
		separateSets();
	}	
	
	void separateSets() {
		trainingData = (ArrayList<Vector3D>) data.clone();
		for(int c=0;c<testDataSize;c++) {
			int i = (int)(Math.random() * (dataSize-c));
			testData.add(trainingData.remove(i));
		}
	}
	
	public void regenData(int dataClassesNb, int clustersPerClass, int classSize, double classDispersionCoeff, double classDensityCoeff) {
		boolean temp = doTrain;
		
		endTraining();
		
		this.dataClassesNb = dataClassesNb;
		this.clustersPerClass = clustersPerClass;
		this.classSize = classSize;
		this.classDispersionCoeff = classDispersionCoeff;
		this.classDensityCoeff = classDensityCoeff;

		initAll();
		
		doTrain = temp;
	}

	void initLayers() {
		layerTypes = new ArrayList<Type>();
		for(int i=0; i<DEFAULT_LAYER_NB; i++)
			layerTypes.add(Type.SIGMOID);
	}
	
	void initNet() {
		boolean temp = doTrain;
		endTraining();
		try {
			
			net = new Network(Type.SIGMOID, dataClassesNb);
			
			if(neuronsPerLayer > 0) 
				for(Type type : layerTypes) 
					net.addLayer(NodeFactory.createLayer(type, neuronsPerLayer));

			net.initConnections(NB_INPUTS);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		learningMod = new LearningModule(net);
		
		System.out.println(layerTypes.toString());
		doTrain = temp;
	}
	
	public int computeClass(double x, double y) {
		return computeClass(net, x, y);
	}
	
	public int computeClass(Network net, double x, double y) {
		// normalize
		x = x / w;
		y = y / h;
		
		Double[] out = net.compute(new Double[] {x, y});
		
		return outToClass(out);
	}
	
	public void trainNext() {
		isTraining = true;
		
		Vector3D point;
		
		int i = (int)(Math.random() * trainingDataSize);
		
		if(feedClass < 0) {
			point = trainingData.get(i);
		} else {
			for(; trainingData.get(i).z != feedClass; i = (++i)%trainingDataSize);
			point = trainingData.get(i);
		}
		
		// normalize
		int z = (int) point.z;
		
		Double[] expectedOut = new Double[dataClassesNb];
		Arrays.fill(expectedOut, 0.0);
		expectedOut[z] = 1.0;
		
		int out = computeClass(point.x, point.y);
		
		// train
		learningMod.propagateSquaredDifference(expectedOut);

		
		boolean classified = out == z;
		addToHistory(classified);
		notifyViewers();
		
		isTraining = false;
	}
	
	public void feedClass(int index) {
		feedClass = index;
	}
	
	public Unit[] getHiddenNeurons() {
		Node[] nodes = net.getLayer(0).getNodes();
		Unit[] units = new Unit[neuronsPerLayer];
		
		for(int i=0; i<nodes.length; i++)
			units[i] = (Unit) nodes[i];
		
		return units;
	}
	
	public Unit[] getOutNeurons() {
		Node[] nodes = net.getLayer(1).getNodes();
		Unit[] units = new Unit[dataClassesNb];
		
		for(int i=0; i<nodes.length; i++)
			units[i] = (Unit) nodes[i];
		
		return units;
	}
	
	public Network getNetwork() {
		return net.clone();
	}
	
	public int outToClass(Double[] out) {
		int maxClass = 0;
		
		for(int i=0; i<out.length; i++) 
			if(out[i] > out[maxClass])
				maxClass = i;
		
		return maxClass;
	}
	
	public void addToHistory(boolean val) {
		if(successHistory.size() >= trainingDataSize)
			successHistory.remove();
		successHistory.add(val);
	}
	
	public void resetHistory() {
		successHistory.clear();
	}

	public int getTrainingSuccessRate() {
		double sum = 0;
		for(boolean e : successHistory)
			sum += e ? 1 : 0;
		return (int) (sum/trainingDataSize * 100);
	}
	
	public void computeTestSuccessRate() {
		int c = 0;
		for(Vector3D point : testData) {
			int z = computeClass(point.x, point.y);
			if(z == point.z)
				c++;
		}
		currentSuccessRate = (int) ((double)c/(double)testDataSize * 100);
	}
	
	@Override
	public void run() {
		while(true) {
			if(doTrain)
				trainNext();
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	

	public void start() {
		PROC.start();
	}
	
	public void train() {
		doTrain = !doTrain;
	}
	
	private void endTraining() {
		doTrain = false;
		while(isTraining)
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			};
	}
	
	private void notifyViewers() {
		int trainingRate = getTrainingSuccessRate();
		viewers.forEach(x -> x.notify(trainingRate, currentSuccessRate));
	}
	
	public void subscribe(Observer v) {
		viewers.add(v);
	}
}





















