package viewer;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class App extends JFrame{
	
	final int WINDOW_WIDTH = 1100;
	final int WINDOW_HEIGHT = 700;
	final int GRAPHIC_PANE_WIDTH = WINDOW_WIDTH/10*7;
	final int GRAPHIC_PANE_HEIGHT = WINDOW_HEIGHT;
	final int CONTROL_PANE_WIDTH = WINDOW_WIDTH/10*3;
	final int CONTROL_PANE_HEIGHT = WINDOW_HEIGHT;
	
	final double GRAPHIC_MAX_WIDTH = 500;
	final double GRAPHIC_MAX_HEIGHT = GRAPHIC_MAX_WIDTH * ((double)GRAPHIC_PANE_HEIGHT/(double)GRAPHIC_PANE_WIDTH);

	final double PXL_PER_UNIT = GRAPHIC_MAX_WIDTH > GRAPHIC_MAX_HEIGHT ?
								(double)GRAPHIC_PANE_WIDTH / GRAPHIC_MAX_WIDTH :
								(double)GRAPHIC_PANE_HEIGHT / GRAPHIC_MAX_HEIGHT;
	

	JPanel contentPane;
	GraphicPane gpn;
	ControlPane cpn;
	
	NnHandler nnHandler;

	public static void main(String[] args) {
		new App();
	}
	
	public App() {


		System.out.println(GRAPHIC_MAX_WIDTH);
		System.out.println(GRAPHIC_MAX_HEIGHT);
		
		initApp();
		
		initNnHandler();
		
		initGpn();
		initCpn();
		
		pack();
		setVisible(true);
	}
	
	private void initApp() {
		setBounds(150,25,0,0);
		contentPane = new JPanel();
		contentPane.setLayout(null);
		contentPane.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		setContentPane(contentPane);
	}
	
	private void initNnHandler() {
		nnHandler = new NnHandler(GRAPHIC_MAX_WIDTH, GRAPHIC_MAX_HEIGHT);
	}
	
	private void initGpn() {
		gpn = new GraphicPane(this, GRAPHIC_MAX_WIDTH, GRAPHIC_MAX_HEIGHT, PXL_PER_UNIT, nnHandler);
		gpn.setBounds(0,0,GRAPHIC_PANE_WIDTH, GRAPHIC_PANE_HEIGHT);
		gpn.setBackground(Color.green);
		contentPane.add(gpn);
	}
	
	private void initCpn() {
		cpn = new ControlPane(nnHandler, gpn);
		cpn.setBounds(GRAPHIC_PANE_WIDTH,0,CONTROL_PANE_WIDTH, CONTROL_PANE_HEIGHT);
		cpn.setBackground(Color.white);
		contentPane.add(cpn);
		
		nnHandler.subscribe(cpn);
	}
}



















