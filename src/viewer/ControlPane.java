package viewer;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ControlPane extends JPanel implements Observer {

	final int MAX_CLASSES = 10;
	final int MIN_CLASSES = 1;
	final int MAX_CLUSSTERS = 10;
	final int MIN_CLUSSTERS = 1;
	final int MAX_CLASS_SIZE = 2000;
	final int MIN_CLASS_SIZE = 1;
	final int MAX_CLASS_DISP = 1;
	final int MIN_CLASS_DISP = 0;
	final int MAX_CLASS_DENSITY = 1;
	final int MIN_CLASS_DENSITY = 0;
	
	NnHandler nnh;
	GraphicPane gpn;
	
	JPanel pnlControls;
	JPanel pnlClasses;
	JPanel pnlNetwork;
	
	JLabel lblControls;
	JLabel lblClasses;
	JLabel lblNetwork;
	JLabel lblCombinedSuccessTitle;
	JLabel lblCombinedSuccess;
	JLabel lblCurrentSuccessTitle;
	JLabel lblCurrentSuccess;
	JLabel lblDrawZones;
	JLabel lblDrawDistribution;
	JLabel lblDrawData;
	JLabel lblDrawTestData;
	JLabel lblDrawTrainingData;
	JLabel lblNbclassesTitle;
	JLabel lblClusstersPerClassTitle;
	JLabel lblClassSize;
	JLabel lblClassDispersionCoeff;
	JLabel lblClassDensityCoeff;

	JButton btnTrain;
	JButton btnGenerate;
	
	JSpinner spnNbClasses;
	JSpinner spnClusstersPerClass;
	JSpinner spnClassSize;
	JSpinner spnClassDispersionCoeff;
	JSpinner spnClassDensityCoeff;
	
	JComboBox<String> cmbxFeedClass;
	
	JCheckBox chkDrawZones;
	JCheckBox chkDrawDistribution;
	JCheckBox chkDrawData;
	JCheckBox chkDrawTestData;
	JCheckBox chkDrawTrainingData;
	
	Border pnlBorder;
	
	boolean trainStarted = false;
	
	public ControlPane(NnHandler nnh, GraphicPane gpn) {
		this.nnh = nnh;
		this.gpn = gpn;
		initInterface();
	}

	private void initInterface() {
		setLayout(new GridLayout(3,1));
		
		pnlBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black);
		
		
		initControlsPane();
		
		initClassesPane();
		initNetworkPane();
		
		
		
//		
//		String[] classOptions = getClassOptions();
//		cmbxFeedClass = new JComboBox<String>(classOptions);
//		this.add(cmbxFeedClass);
//		
//
//		lblDrawDistribution = new JLabel("Draw distribution");
//		lblDrawDistribution.setBounds(10, 10, 100, 20);
//		add(lblDrawDistribution);
//		
//		chkDrawDistribution = new JCheckBox();
//		add(chkDrawDistribution);
//		
//		cmbxFeedClass.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				int index = ((JComboBox<String>)e.getSource()).getSelectedIndex()-1;
//				nnh.feedClass(index);
//			}
//		});
//		

//		
//		chkDrawDistribution.addItemListener(new ItemListener() {
//			public void itemStateChanged(ItemEvent e) {
//				gpn.drawDistribution = e.getStateChange() == ItemEvent.SELECTED;
//			}
//		});
	}
	
	private void initControlsPane() {
		pnlControls = new JPanel();
		pnlControls.setLayout(null);
		pnlControls.setBorder(pnlBorder);
		add(pnlControls);
		
		lblControls = new JLabel("Controls");
		lblControls.setFont(new Font("", Font.BOLD, 20));
		lblControls.setBounds(10,10,100,20);
		pnlControls.add(lblControls);
		
		btnTrain = new JButton("Train");
		btnTrain.setBounds(10,50,80,35);
		pnlControls.add(btnTrain);
		
		
		lblCombinedSuccessTitle = new JLabel("Training success rate : ");
		lblCombinedSuccessTitle.setBounds(110, 45, 150, 20);
		pnlControls.add(lblCombinedSuccessTitle);

		lblCombinedSuccess = new JLabel("0%");
		lblCombinedSuccess.setBounds(260, 45, 80, 20);
		pnlControls.add(lblCombinedSuccess);
		
		lblCurrentSuccessTitle = new JLabel("Test success rate : ");
		lblCurrentSuccessTitle.setBounds(110, 65, 200, 20);
		pnlControls.add(lblCurrentSuccessTitle);

		lblCurrentSuccess = new JLabel("0%");
		lblCurrentSuccess.setBounds(260, 65, 80, 20);
		pnlControls.add(lblCurrentSuccess);
		

		chkDrawData = new JCheckBox();
		chkDrawData.setBounds(10, 110, 20, 20);
		chkDrawData.setSelected(gpn.drawData);
		pnlControls.add(chkDrawData);
		
		lblDrawData = new JLabel("Draw Data");
		lblDrawData.setBounds(35, 110, 100, 20);
		pnlControls.add(lblDrawData);
		
		chkDrawTestData = new JCheckBox();
		chkDrawTestData.setBounds(20, 130, 20, 20);
		chkDrawTestData.setSelected(gpn.drawTestData);
		pnlControls.add(chkDrawTestData);
		
		lblDrawTestData = new JLabel("Select Test Data");
		lblDrawTestData.setBounds(55, 130, 100, 20);
		pnlControls.add(lblDrawTestData);
		
		chkDrawTrainingData = new JCheckBox();
		chkDrawTrainingData.setBounds(20, 150, 20, 20);
		chkDrawTrainingData.setSelected(gpn.drawTrainingData);
		pnlControls.add(chkDrawTrainingData);
		
		lblDrawTrainingData = new JLabel("Select Training Data");
		lblDrawTrainingData.setBounds(55, 150, 150, 20);
		pnlControls.add(lblDrawTrainingData);
		
		chkDrawZones = new JCheckBox();
		chkDrawZones.setBounds(10, 170, 20, 20);
		chkDrawZones.setSelected(gpn.drawZones);
		pnlControls.add(chkDrawZones);
		
		lblDrawZones = new JLabel("Draw Class Zones");
		lblDrawZones.setBounds(35, 170, 150, 20);
		pnlControls.add(lblDrawZones);
		
		
		
		btnTrain.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(!trainStarted) {
					nnh.start();
					gpn.start();
					trainStarted = true;
				}
				
				nnh.train();
				gpn.paint();
				btnTrain.setText(nnh.doTrain? "Stop" : "Train");
				
				nnh.computeTestSuccessRate();
			}
			
		});
		
		chkDrawData.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				gpn.drawData(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		
		chkDrawTestData.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				gpn.drawTestData(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		
		chkDrawTrainingData.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				gpn.drawTrainingData(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		
		chkDrawZones.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				gpn.drawZones(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
	}
	
	private void initClassesPane() {
		pnlClasses = new JPanel();
		pnlClasses.setLayout(null);
		pnlClasses.setBorder(pnlBorder);
		add(pnlClasses);
		
		lblClasses = new JLabel("Data");
		lblClasses.setFont(new Font("", Font.BOLD, 20));
		lblClasses.setBounds(10,10,100,20);
		pnlClasses.add(lblClasses);
		
		lblNbclassesTitle = new JLabel("Number of classes");
		lblNbclassesTitle.setBounds(10,40,150,20);
		pnlClasses.add(lblNbclassesTitle);
		
		spnNbClasses = new JSpinner(new SpinnerNumberModel(nnh.dataClassesNb, MIN_CLASSES, MAX_CLASSES, 1));
		spnNbClasses.setBounds(150, 40, 60, 25);
		pnlClasses.add(spnNbClasses);
		
		lblClusstersPerClassTitle = new JLabel("Clussters per class");
		lblClusstersPerClassTitle.setBounds(10,70,150,20);
		pnlClasses.add(lblClusstersPerClassTitle);
		
		spnClusstersPerClass = new JSpinner(new SpinnerNumberModel(nnh.clustersPerClass, MIN_CLUSSTERS, MAX_CLUSSTERS, 1));
		spnClusstersPerClass.setBounds(150, 70, 60,25);
		pnlClasses.add(spnClusstersPerClass);
		
		lblClassSize = new JLabel("Class size");
		lblClassSize.setBounds(10,100,150,20);
		pnlClasses.add(lblClassSize);
		
		spnClassSize = new JSpinner(new SpinnerNumberModel(nnh.classSize, MIN_CLASS_SIZE, MAX_CLASS_SIZE, 10));
		spnClassSize.setBounds(150, 100, 60,25);
		pnlClasses.add(spnClassSize);
		
		lblClassDispersionCoeff = new JLabel("Dispersion coefficient");
		lblClassDispersionCoeff.setBounds(10,130,150,20);
		pnlClasses.add(lblClassDispersionCoeff);
		
		spnClassDispersionCoeff = new JSpinner(new SpinnerNumberModel(nnh.classDispersionCoeff, MIN_CLASS_DISP, MAX_CLASS_DISP, 0.1));
		spnClassDispersionCoeff.setBounds(150, 130, 60,25);
		pnlClasses.add(spnClassDispersionCoeff);
		
		lblClassDensityCoeff = new JLabel("Density coefficient");
		lblClassDensityCoeff.setBounds(10,160,150,20);
		pnlClasses.add(lblClassDensityCoeff);
		
		spnClassDensityCoeff = new JSpinner(new SpinnerNumberModel(nnh.classDensityCoeff, MIN_CLASS_DENSITY, MAX_CLASS_DENSITY, 0.1));
		spnClassDensityCoeff.setBounds(150, 160, 60,25);
		pnlClasses.add(spnClassDensityCoeff);
		
		
		btnGenerate = new JButton("Generate");
		btnGenerate.setBounds(10, 195, 100, 25);
		pnlClasses.add(btnGenerate);
		
		
		spnNbClasses.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if((int)spnNbClasses.getValue() > MAX_CLASSES)
					spnNbClasses.setValue(MAX_CLASSES);
				else if((int)spnNbClasses.getValue() < MIN_CLASSES)
					spnNbClasses.setValue(MIN_CLASSES);
				System.out.println((int)spnNbClasses.getValue());
			}
		});
		spnClusstersPerClass.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if((int)spnClusstersPerClass.getValue() > MAX_CLUSSTERS)
					spnClusstersPerClass.setValue(MAX_CLUSSTERS);
				else if((int)spnClusstersPerClass.getValue() < MIN_CLUSSTERS)
					spnClusstersPerClass.setValue(MIN_CLUSSTERS);
			}
		});
		spnClassSize.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if((int)spnClassSize.getValue() > MAX_CLASS_SIZE)
					spnClassSize.setValue(MAX_CLASS_SIZE);
				else if((int)spnClassSize.getValue() < MIN_CLASS_SIZE)
					spnClassSize.setValue(MIN_CLASS_SIZE);
			}
		});
		spnClassDispersionCoeff.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if((double)spnClassDispersionCoeff.getValue() > MAX_CLASS_DISP)
					spnClassDispersionCoeff.setValue(MAX_CLASS_DISP);
				else if((double)spnClassDispersionCoeff.getValue() < MIN_CLASS_DISP)
					spnClassDispersionCoeff.setValue(MIN_CLASS_DISP);
			}
		});
		spnClassDensityCoeff.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if((double)spnClassDensityCoeff.getValue() > MAX_CLASS_DENSITY)
					spnClassDensityCoeff.setValue(MAX_CLASS_DENSITY);
				else if((double)spnClassDensityCoeff.getValue() < MIN_CLASS_DENSITY)
					spnClassDensityCoeff.setValue(MIN_CLASS_DENSITY);
			}
		});
		
		btnGenerate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				nnh.regenData(
						(int)spnNbClasses.getValue(),
						(int)spnClusstersPerClass.getValue(),
						(int)spnClassSize.getValue(),
						(double)spnClassDispersionCoeff.getValue(),
						(double)spnClassDensityCoeff.getValue()
						);

				btnTrain.setText(nnh.doTrain? "Stop" : "Train");
				
				nnh.computeTestSuccessRate();
				
				gpn.repaint();
			}
		});
		
	}
	
	private void initNetworkPane() {
		pnlNetwork = new JPanel();
		pnlNetwork.setLayout(null);
		add(pnlNetwork);
		
		lblNetwork = new JLabel("Network");
		lblNetwork.setFont(new Font("", Font.BOLD, 20));
		lblNetwork.setBounds(10,10,100,20);
		pnlNetwork.add(lblNetwork);
	}
	
	private String[] getClassOptions() {
		String[] classOptions = new String[nnh.dataClassesNb+1];
		classOptions[0] = "ALL";
		for(int i=1; i<classOptions.length; i++)
			classOptions[i] = (i-1)+"";
		return classOptions;
	}

	@Override
	public void notify(int combinedRate, int currentRate) {
		lblCombinedSuccess.setText(combinedRate+"%");
		lblCurrentSuccess.setText(currentRate+"%");
	}
	
}
