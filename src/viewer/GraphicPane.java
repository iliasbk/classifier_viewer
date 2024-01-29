package viewer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import javax.swing.JPanel;

import neural_net.Network;
import units.Unit;

public class GraphicPane extends JPanel implements Runnable {
	
	final Thread PROC = new Thread(this);

	App app;
	
	double w;
	double h;
	double originX;
	double originY;

	double pxlPerUnit;
	double pointSize = 5;

	double yScaleFactor;
	
	NnHandler nnHandler;
	
	public boolean drawZones = true;
	public boolean drawData = true;
	public boolean drawTestData = false;
	public boolean drawTrainingData = false;
	
	public boolean drawDistribution;
	
	boolean doPaint = false;
	
	int[][] zones;
	
	Color[] classColors;
	
//	Thread zonesProc;

	public GraphicPane(App app, double w, double h, double pxlPerUnit, NnHandler nnHandler) {
		setBackground(new Color(50,50,50));
		this.app = app;
		this.w = w;
		this.h = h;
		this.originX = w/2;
		this.originY = h/2;
		this.yScaleFactor = h/2;
		this.pxlPerUnit = pxlPerUnit;
		this.pointSize /= pxlPerUnit;
		this.nnHandler = nnHandler;
		
		this.zones = new int[(int) h+1][(int) w+1];
		
		initColors();
		
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				processClick(e.getX(), e.getY());
			}
		});
	}
	
	private void processClick(double x, double y) {
		
		// transform click pos to graphic coords
		x /= pxlPerUnit;
		y /= -pxlPerUnit;
		x -= w/2;
		y += h/2;
		
		int pointClass = nnHandler.computeClass(x, y);
	
		System.out.println(x+" " + y);
		System.out.println("class " + pointClass);
	}
	
	private void initColors() {
		classColors = new Color[nnHandler.dataClassesNb];
		for(int i=0;i<nnHandler.dataClassesNb;i++) {
			classColors[i] = new Color(
					(int)(Math.random() * 200)+50,
					(int)(Math.random() * 200)+50,
					(int)(Math.random() * 200)+50
					);
		}
	}
	
	private Color zToZoneColor(double z) {
		Color c = classColors[(int)z];
		int r = 20;
		return new Color(c.getRed()-r, c.getGreen()-r, c.getBlue()-r);
	}
	
	private Color interpolateColor(Double[] distribution) {
		
		int r=0;
		int g=0;
		int b=0;
		
		for(int c=0; c<distribution.length; c++) {
			
			Color color = classColors[c];
			
			r += distribution[c] * color.getRed();
			g += distribution[c] * color.getGreen();
			b += distribution[c] * color.getBlue();
			
		}
		
		return new Color(	Math.min(r, 255),
							Math.min(g, 255),
							Math.min(b, 255)
						);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.scale(0.95, 0.95);
		g2d.scale(pxlPerUnit, -pxlPerUnit);
		g2d.translate(originX/0.95, -originY/0.95);


		if(drawZones)
			drawZones(g2d);
		if(drawDistribution)
			drawDistribution(g2d);
		
		// draw axis
		
		g2d.setColor(Color.white);
		g2d.setStroke(new BasicStroke((float) (1/pxlPerUnit)));
		
		Line2D.Double xAxis = new Line2D.Double(-originX, 0, originX, 0);
		g2d.draw(xAxis);

		Line2D.Double yAxis = new Line2D.Double(0, originY, 0, -originY);
		g2d.draw(yAxis);
		
		
		// draw data
		
		drawData(g2d);
		
		
		// draw neurons' lines
		
//		Unit[] hiddenUnits = nnHandler.getHiddenNeurons();
//		drawNeurons(g2d, hiddenUnits);
		
	}
	
	private void drawData(Graphics2D g2d) {
		
		if(drawData) {
			Ellipse2D.Double point = new Ellipse2D.Double(0, 0, pointSize, pointSize);
			Color c;
			
			for(Vector3D p : nnHandler.data) {
				boolean contains = nnHandler.testData.contains(p);

//				c = zToColor(p.z);
//				c = classColors[(int) p.z];
				c = zToZoneColor(p.z);
				
				if(drawTestData && contains)
					c = Color.red;
				if(drawTrainingData && !contains)
					c = Color.green;
					
				g2d.setColor(c);
				g2d.translate(p.x, p.y);
				g2d.draw(point);
				g2d.translate(-p.x, -p.y);
			}
		}
	}
	
	private void drawNeurons(Graphics2D g2d, Unit[] units) {
		g2d.setColor(Color.white);
		
		// a unit has 3 inputs [0] for y-intercept, [1] for X, [2] for Y
		// Y = ( [1]*X + [0] ) / [2]
		
		for(Unit u : units) {
			
			Double[] weights = u.getWeights();
			
			double xi = -w/2;
			double xf = -xi;
			
			double yi = ( weights[1]*(xi) + weights[0]*yScaleFactor ) / weights[2];
			double yf = ( weights[1]*(xf) + weights[0]*yScaleFactor ) / weights[2];
			
			g2d.draw(new Line2D.Double(xi, yi, xf, yf));
		}
	}
	
	private void drawZones(Graphics2D g2d) {
		
		int xi = (int) -w/2;
		int yi = (int) h/2;
		int size = 1;
		
		Rectangle2D.Double pixel = new Rectangle2D.Double(xi,yi-size, size, size);
		
		for(int y=yi; y > -h/2; y--) {
			for(int x=xi; x < w/2; x++) {
				
				int z = zones[y+yi][x-xi];
				g2d.setColor(classColors[z]);
				g2d.fill(pixel);
				
				g2d.translate(1, 0);
			}
			g2d.translate(-w, -1);
		}
		
		g2d.translate(0, h);
		
	}

	private void drawDistribution(Graphics2D g2d) {
		
		Network netClone = nnHandler.getNetwork();

		int xi = (int) -w/2;
		int y = (int) h/2;
		int size = 1;
		
		Rectangle2D.Double pixel = new Rectangle2D.Double(xi,y-size, size, size);
		
		for(; y > -h/2; y--) {
			for(int x=xi; x < w/2; x++) {
				
				Double[] out = netClone.compute(new Double[] {(double)x, (double)y});
				
				g2d.setColor(interpolateColor(out));
				g2d.fill(pixel);
				
				g2d.translate(1, 0);
			}
			g2d.translate(-w, -1);
		}	

		g2d.translate(0, h);
	}
	
	private void computeZones() {

		Network netClone = nnHandler.getNetwork();
		
		int xi = (int) -w/2;
		int yi = (int) h/2;
		
		int[][] zones = new int[this.zones.length][this.zones[0].length];
		
		for(int y=yi; y > -h/2; y--) {
			for(int x=xi; x < w/2; x++) {
				
				int z = nnHandler.computeClass(netClone, x, y);
				
				zones[y+yi][x-xi] = z;
				
			}
		}	
		
		this.zones = zones;
		
	}
	

	@Override
	public void run() {
		
		while(true) {
			
			if(doPaint) {
				computeZones();
				repaint();
				nnHandler.computeTestSuccessRate();
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void start() {
		PROC.start();
	}
	
	public void paint() {
		doPaint = !doPaint;
	}
	
	public void drawZones(boolean doDraw) {
		drawZones = doDraw;
		repaint();
	}
	
	public void drawData(boolean doDraw) {
		drawData = doDraw;
		repaint();
	}
	
	public void drawTestData(boolean doDraw) {
		drawTestData = doDraw;
		repaint();
	}
	
	public void drawTrainingData(boolean doDraw) {
		drawTrainingData = doDraw;
		repaint();
	}
	
	public void regen() {
		initColors();
		repaint();
	}
}

































