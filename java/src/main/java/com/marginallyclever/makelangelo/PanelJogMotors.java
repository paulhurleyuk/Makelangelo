package com.marginallyclever.makelangelo;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PanelJogMotors
extends JPanel
implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1126664005558490581L;
	
	protected JButton buttonAneg;
	protected JButton buttonApos;
	protected JButton buttonBneg;
	protected JButton buttonBpos;
	
	protected JCheckBox m1i;
	protected JCheckBox m2i;

	protected MainGUI gui;
	protected MultilingualSupport translator;
	protected MakelangeloRobot machineConfiguration;


	public PanelJogMotors(MainGUI _gui, MultilingualSupport _translator, MakelangeloRobot _machineConfiguration) {
		gui = _gui;
		translator = _translator;
		machineConfiguration = _machineConfiguration;

	    setLayout(new GridBagLayout());
	    GridBagConstraints c = new GridBagConstraints();
	
	    buttonAneg = new JButton(_translator.get("JogIn"));
	    buttonApos = new JButton(_translator.get("JogOut"));
	    m1i = new JCheckBox(_translator.get("Invert"), _machineConfiguration.invertMotor1);
	
	    buttonBneg = new JButton(_translator.get("JogIn"));
	    buttonBpos = new JButton(_translator.get("JogOut"));
	    m2i = new JCheckBox(_translator.get("Invert"), _machineConfiguration.invertMotor2);
	
	    c.gridx = 0;
	    c.gridy = 0;
	    add(new JLabel(_translator.get("Left")), c);
	    c.gridx = 0;
	    c.gridy = 1;
	    add(new JLabel(_translator.get("Right")), c);
	
	    c.gridx = 1;
	    c.gridy = 0;
	    add(buttonAneg, c);
	    c.gridx = 1;
	    c.gridy = 1;
	    add(buttonBneg, c);
	
	    c.gridx = 2;
	    c.gridy = 0;
	    add(buttonApos, c);
	    c.gridx = 2;
	    c.gridy = 1;
	    add(buttonBpos, c);
	
	    c.gridx = 3;
	    c.gridy = 0;
	    add(m1i, c);
	    c.gridx = 3;
	    c.gridy = 1;
	    add(m2i, c);

	    buttonApos.addActionListener(this);
	    buttonAneg.addActionListener(this);

	    buttonBpos.addActionListener(this);
	    buttonBneg.addActionListener(this);

	    m1i.addActionListener(this);
	    m2i.addActionListener(this);
	}

    public void actionPerformed(ActionEvent e) {
      Object subject = e.getSource();
      if (subject == buttonApos) gui.sendLineToRobot("D00 L400");
      if (subject == buttonAneg) gui.sendLineToRobot("D00 L-400");
      if (subject == buttonBpos) gui.sendLineToRobot("D00 R400");
      if (subject == buttonBneg) gui.sendLineToRobot("D00 R-400");
    
      if (subject == m1i || subject == m2i) {
        machineConfiguration.invertMotor1 = m1i.isSelected();
        machineConfiguration.invertMotor2 = m2i.isSelected();
        machineConfiguration.saveConfig();
        gui.sendConfig();
      }
    }
}
