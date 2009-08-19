package org.mesh4j.ektoo.ui.settings;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import org.mesh4j.ektoo.ui.component.DocumentModelAdapter;

public class MySqlSettingsUI extends AbstractSettingsUI{

	private static final long serialVersionUID = -4780427959572991381L;
	private JTextField userTextField;
	private JPasswordField passwordField;
	private JTextField hostTextField;
	private JTextField portTextField;
	private JTextField dataBaseTextField;
	
	
	
	public MySqlSettingsUI(SettingsController controller){
		super(controller);
		this.setLayout(new GridBagLayout());
		initComponents();
	}
	
	private void initComponents(){
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(50, 10, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		this.add(getUserNameLabel(),c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 1;
		c.weightx = 0.5;
		c.insets = new Insets(50, 20, 0, 10);
		this.add(getUserTextBox(), c);
		
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = 2;
		c.weightx = 0;
		c.insets = new Insets(5, 10, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		this.add(getPasswordLabel(), c);
		
	
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 2;
		c.weightx = 0.5;
		c.insets = new Insets(5, 20, 0, 10);
		this.add(getPasswordTextBox(), c);
		
		
		
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = 3;
		c.weightx = 0;
		c.insets = new Insets(5, 10, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		this.add(getHostPortLabel(), c);
		
	
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 3;
		c.weightx = 0.5;
		c.insets = new Insets(5, 20, 0, 10);
		this.add(getHostPort(), c);
		
		
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = 4;
		c.weightx = 0;
		c.insets = new Insets(5, 10, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		this.add(getDataBaseNameLabel(), c);

		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 4;
		c.weightx = 0.5;
		c.insets = new Insets(5, 20, 0, 10);
		this.add(getDataBaseTextBox(), c);
		
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 5;
		c.weightx = 0.5;
		c.insets = new Insets(5, 20, 0, 10);
		this.add(getDefaultCheckBox(), c);
		
		
		c.anchor = GridBagConstraints.PAGE_END; //bottom of space
		c.gridx = 2;
		c.gridy = 6;
		c.weighty = 1;
		c.weightx = 0;
		c.insets = new Insets(0, 10, 0, 10);
		this.add( getButtonPanel(), c);
	}
	
	
	
	
	
	private JPanel getButtonPanel(){
		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.add(getDefaultButton(),BorderLayout.EAST);
		return buttonPanel;
	}
	
	private JLabel getUserNameLabel(){
		JLabel langLabel = new JLabel("User name");
		//langLabel.setPreferredSize(new Dimension(150,20));
		return langLabel;
	}
	
	
	
	private JTextField getUserTextBox(){
		userTextField = new JTextField();
		userTextField.getDocument().addDocumentListener(new DocumentModelAdapter(){
			@Override
			public void insertUpdate(DocumentEvent e) {
				getController().modifySettings(SettingsController.USER_NAME_MYSQL, 
						userTextField.getText());
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				getController().modifySettings(SettingsController.USER_NAME_MYSQL, 
						userTextField.getText());
			}
		});
		return userTextField;
	}
	
	
	private JLabel getPasswordLabel(){
		JLabel langLabel = new JLabel("Password");
		//langLabel.setPreferredSize(new Dimension(150,20));
		return langLabel;
	}
	
	private JPasswordField getPasswordTextBox(){
		 passwordField = new JPasswordField();
		 passwordField.getDocument().addDocumentListener(new DocumentModelAdapter(){
				@Override
				public void insertUpdate(DocumentEvent e) {
					getController().modifySettings(SettingsController.USER_PASSWORD_MYSQL, 
							new String(passwordField.getPassword()));
				}
				@Override
				public void removeUpdate(DocumentEvent e) {
					getController().modifySettings(SettingsController.USER_PASSWORD_MYSQL, 
							new String(passwordField.getPassword()));
				}
			});
		return passwordField;
	}
	
	private JPanel getHostPort(){
		JPanel hostPortPanel = new JPanel(new GridBagLayout());
		
		hostTextField = new JTextField();
		portTextField = new JTextField();
		portTextField.setPreferredSize(new Dimension(50,20));
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 0.5;
		hostPortPanel.add(hostTextField,c);
		
		JLabel colonLabel = new JLabel(":");
		c.fill = GridBagConstraints.NONE;
		c.gridx = 2;
		c.gridy = 1;
		c.weightx = 0;
		c.insets = new Insets(0,5,0,0);
		hostPortPanel.add(colonLabel,c);
		
		
		c.fill = GridBagConstraints.NONE;
		c.gridx = 3;
		c.gridy = 1;
		c.insets = new Insets(0,5,0,0);
		hostPortPanel.add(portTextField,c);
		
		
		//action
		hostTextField.getDocument().addDocumentListener(new DocumentModelAdapter(){
				@Override
				public void insertUpdate(DocumentEvent e) {
					getController().modifySettings(SettingsController.HOST_NAME_MYSQL, 
							hostTextField.getText());
				}
				@Override
				public void removeUpdate(DocumentEvent e) {
					getController().modifySettings(SettingsController.HOST_NAME_MYSQL, 
							hostTextField.getText());
				}
			});
		
		portTextField.getDocument().addDocumentListener(new DocumentModelAdapter(){
			@Override
			public void insertUpdate(DocumentEvent e) {
				getController().modifySettings(SettingsController.PORT_MYSQL, 
						portTextField.getText());
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				getController().modifySettings(SettingsController.PORT_MYSQL, 
						portTextField.getText());
			}
		});
		
		
		return hostPortPanel;
	}
	
	private JLabel getHostPortLabel(){
		JLabel langLabel = new JLabel("Host : Port");
		return langLabel;
	}
	

	
	
	private JLabel getDataBaseNameLabel(){
		JLabel langLabel = new JLabel("Database name");
		//langLabel.setPreferredSize(new Dimension(150,20));
		return langLabel;
	}
	private JTextField getDataBaseTextBox(){
		 dataBaseTextField = new JTextField();
		 dataBaseTextField.getDocument().addDocumentListener(new DocumentModelAdapter(){
				@Override
				public void insertUpdate(DocumentEvent e) {
					getController().modifySettings(SettingsController.DATABASE_NAME_MYSQL, 
							dataBaseTextField.getText());
				}
				@Override
				public void removeUpdate(DocumentEvent e) {
					getController().modifySettings(SettingsController.DATABASE_NAME_MYSQL, 
							dataBaseTextField.getText());
				}
			});
		return dataBaseTextField;
	}
	
	private SettingsController getController(){
		return (SettingsController)this.controller;
	}
	
	
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		String newValueAsString = evt.getNewValue().toString();
		if ( evt.getPropertyName().equals( SettingsController.USER_NAME_MYSQL)){
			if(!userTextField.getText().equals(newValueAsString))
				userTextField.setText(newValueAsString);
		} else if ( evt.getPropertyName().equals( SettingsController.USER_PASSWORD_MYSQL)){
			if(!new String(passwordField.getPassword()).equals(newValueAsString))
				passwordField.setText("test");
		} else if ( evt.getPropertyName().equals( SettingsController.HOST_NAME_MYSQL)){
			if(!hostTextField.getText().equals(newValueAsString))
				hostTextField.setText(newValueAsString);
		} else if ( evt.getPropertyName().equals( SettingsController.PORT_MYSQL)){
			if(!portTextField.getText().equals(newValueAsString))
				portTextField.setText(newValueAsString);
		} else if ( evt.getPropertyName().equals( SettingsController.DATABASE_NAME_MYSQL)){
			if(!dataBaseTextField.getText().equals(newValueAsString))
				dataBaseTextField.setText(newValueAsString);
		}

		
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void loadDefault() {
		getController().loadDefaultMySqlSettings();
	}

}
