package org.mesh4j.ektoo.ui.settings;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class GSSSettingsUI extends AbstractSettingsUI{

	private static final long serialVersionUID = 4427824045519587866L;

	public GSSSettingsUI(AbstractSettingsController controller){
		super(controller);
		this.setLayout(new GridBagLayout());
		init();
	}
	private void init(){
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(50, 10, 0, 0);
		this.add(getUserNameLabel(),c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 1;
		c.insets = new Insets(50, 20, 0, 10);
		this.add(getUserTextBox(), c);
		
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = 2;
		c.insets = new Insets(5, 10, 0, 0);
		this.add(getUserPasswordLabel(), c);
		
	
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 2;
		c.weightx = 0.5;
		c.insets = new Insets(5, 20, 0, 10);
		this.add(getUserPasswordTextBox(), c);
		
		
		c.anchor = GridBagConstraints.PAGE_END; //bottom of space
		c.gridx = 2;
		c.gridy = 4;
		c.weighty = 1;
		c.insets = new Insets(0, 10, 0, 10);
		this.add( getButtonPanel(), c);
		
	}
	
	private JPanel getButtonPanel(){
		JPanel buttonPanel = new JPanel(new BorderLayout());
		JButton testButton = new JButton("default");
		buttonPanel.add(testButton,BorderLayout.EAST);
		return buttonPanel;
	}
	
	private JLabel getUserNameLabel(){
		JLabel langLabel = new JLabel("User name");
		//langLabel.setPreferredSize(new Dimension(150,20));
		return langLabel;
	}
	
	private JLabel getUserPasswordLabel(){
		JLabel langLabel = new JLabel("Password");
		//langLabel.setPreferredSize(new Dimension(150,20));
		return langLabel;
	}
	
	private JTextField getUserTextBox(){
		JTextField userTextField = new JTextField();
		//userTextField.setPreferredSize(new Dimension(150,20));
		return userTextField;
	}
	
	private JPasswordField getUserPasswordTextBox(){
		JPasswordField passwordField = new JPasswordField();
		//passwordField.setPreferredSize(new Dimension(150,20));
		return passwordField;
	}
	
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		
		
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		return false;
	}

}
