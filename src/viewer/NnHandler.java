package viewer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import layers.LayerLinear;
import layers.LayerSigmoid;
import learning_module.LearningModule;
import neural_net.Network;
import nn_interface.Node;
import units.Type;
import units.Unit;

public class NnHandler implements Runnable {

	final Thread PROC = new Thread(this);
	
	/** 1st - X, 2nd - Y */
	final int NB_INPUTS = 2;
	
	public final int NB_HIDDEN_NEURONS = 10;
	
	public int dataClassesNb = 4;
	int clustersPerClass = 3;
	int classSize = 1000;
	double classDispersionCoeff = 8;
	double classDensityCoeff = 4;

	public final int DATA_SIZE = classSize * clustersPerClass * dataClassesNb;
	

	int classRadius;
	
	double w;
	double h;
	
	Network net;
	LearningModule learningMod;
	
	public ArrayList<Vector3D> data;
	
	int dataPointer = 0;
	
	int feedClass = -1;

	Queue<Boolean> successHistory;
	
	HashSet<Observer> viewers;
	
	boolean doTrain = false;
	boolean trainStarted = false;
	
	int currentSuccessRate = 0;
	
	public NnHandler(double maxW, double maxH) {
		this.w = maxW;
		this.h = maxH;
		
		classRadius = (int) (Math.min(maxW, maxH)/classDispersionCoeff);
				
		data = new ArrayList<Vector3D>(DATA_SIZE);
		
		generateData(maxW, maxH);
		initNet();
		
		learningMod = new LearningModule(net);
		
		successHistory = new LinkedList<Boolean>();
		
		viewers = new HashSet<Observer>();
		
	}
	
	void generateData(double maxW, double maxH) {
		
		
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
					double pos = Math.pow(Math.random(),classDensityCoeff) * classRadius;
					double theta = Math.random() * Math.PI * 2;
					
					double x = Math.cos(theta) * pos + centerX;
					double y = Math.sin(theta) * pos + centerY;
					
					Vector3D point = new Vector3D(x, y, c);
	
					data.add(point);
				}
			}
		}

	}

	void initNet() {
		try {
			
			net = new Network(Type.SIGMOID, dataClassesNb);
			
			if(NB_HIDDEN_NEURONS > 0) {
				
				LayerLinear layer1 = new LayerLinear(NB_HIDDEN_NEURONS);
				net.addLayer(layer1);
				LayerSigmoid layer2 = new LayerSigmoid(NB_HIDDEN_NEURONS);
				net.addLayer(layer2);
				LayerSigmoid layer3 = new LayerSigmoid(NB_HIDDEN_NEURONS);
				net.addLayer(layer3);
				
			}
			
			net.initConnections(NB_INPUTS);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		
		Vector3D point;
		
		int i = (int)(Math.random() * DATA_SIZE);
		
		if(feedClass < 0) {
			point = data.get(i);
		} else {
			for(; data.get(i).z != feedClass; i = (++i)%DATA_SIZE);
			point = data.get(i);
		}
		
		// normalize
//		double x = point.x / w;
//		double y = point.y / h;
		int z = (int) point.z;
//		
//		Double[] inputs = new Double[] {x, y};
		Double[] expectedOut = new Double[dataClassesNb];
		Arrays.fill(expectedOut, 0.0);
		expectedOut[z] = 1.0;
//		
//		Double[] out = net.compute(inputs);
//		boolean classified = outToClass(out) == point.z;
		
		int out = computeClass(point.x, point.y);
		boolean classified = out == z;
		
		addToHistory(classified);
		
		// train
		try {
			learningMod.propagateSquaredDifference(expectedOut);
		} catch (Exception e) {
			e.printStackTrace();
		}

		notifyViewers();
		
	}
	
	public void feedClass(int index) {
		feedClass = index;
	}
	
	public Unit[] getHiddenNeurons() {
		Node[] nodes = net.getLayer(0).getNodes();
		Unit[] units = new Unit[NB_HIDDEN_NEURONS];
		
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
		if(successHistory.size() >= DATA_SIZE)
			successHistory.remove();
		successHistory.add(val);
	}
	
	public void resetHistory() {
		successHistory.clear();
	}

	public int getCombinedSuccessRate() {
		double sum = 0;
		for(boolean e : successHistory)
			sum += e ? 1 : 0;
		return (int) (sum/DATA_SIZE * 100);
	}
	
	public void updateSuccessRate() {
		int c = 0;
		for(Vector3D point : data) {
			int z = computeClass(point.x, point.y);
			if(z == point.z)
				c++;
		}
		currentSuccessRate = (int) ((double)c/(double)DATA_SIZE * 100);
		notifyViewers();
		
		System.out.println(currentSuccessRate);
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
	
	private void notifyViewers() {
		int combinedRate = getCombinedSuccessRate();
		viewers.forEach(x -> x.notify(combinedRate, currentSuccessRate));
	}
	
	public void subscribe(Observer v) {
		viewers.add(v);
	}
}





















