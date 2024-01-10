package vizualiser;

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
import javax.swing.border.Border;

public class ControlPane extends JPanel implements Observer {
	
	NnHandler nnh;
	GraphicPane gpn;
	
	JPanel pnlControls;
	JPanel pnlClasses;
	JPanel pnlNetwork;
	
	JLabel lblControls;
	JLabel lblClasses;
	JLabel lblNetwork;
	
	
	JButton btnTrain;
	
	
	JLabel lblCombinedSuccessTitle;
	JLabel lblCombinedSuccess;
	JLabel lblCurrentSuccessTitle;
	JLabel lblCurrentSuccess;
	JLabel lblDrawZones;
	JLabel lblDrawDistribution;
	
	JComboBox<String> cmbxFeedClass;
	
	JCheckBox chkDrawZones;
	JCheckBox chkDrawDistribution;
	
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
		btnTrain.setBounds(10,40,80,30);
		pnlControls.add(btnTrain);
		
		
		lblDrawZones = new JLabel("Draw Zones");
		lblDrawZones.setBounds(135, 40, 100, 20);
		pnlControls.add(lblDrawZones);
		
		chkDrawZones = new JCheckBox();
		chkDrawZones.setBounds(110, 40, 20, 20);
		chkDrawZones.setSelected(gpn.drawZones);
		pnlControls.add(chkDrawZones);
		
		
		lblCombinedSuccessTitle = new JLabel("Combined success rate : ");
		lblCombinedSuccessTitle.setBounds(10, 85, 200, 20);
		pnlControls.add(lblCombinedSuccessTitle);

		lblCombinedSuccess = new JLabel("0");
		lblCombinedSuccess.setBounds(210, 85, 80, 20);
		pnlControls.add(lblCombinedSuccess);
		
		
		lblCurrentSuccessTitle = new JLabel("Current success rate : ");
		lblCurrentSuccessTitle.setBounds(10, 110, 200, 20);
		pnlControls.add(lblCurrentSuccessTitle);

		lblCurrentSuccess = new JLabel("0");
		lblCurrentSuccess.setBounds(210, 110, 80, 20);
		pnlControls.add(lblCurrentSuccess);
		
		
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
				
				nnh.updateSuccessRate();
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
		
		lblClasses = new JLabel("Classes");
		lblClasses.setFont(new Font("", Font.BOLD, 20));
		lblClasses.setBounds(10,10,100,20);
		pnlClasses.add(lblClasses);
		
		
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
		lblCombinedSuccess.setText(combinedRate+"");
		lblCurrentSuccess.setText(currentRate+"");
	}
	
}
