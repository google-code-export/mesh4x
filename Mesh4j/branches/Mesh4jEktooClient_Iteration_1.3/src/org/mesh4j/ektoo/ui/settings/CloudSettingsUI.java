package org.mesh4j.ektoo.ui.settings;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import org.mesh4j.ektoo.ui.AbstractView;
import org.mesh4j.ektoo.ui.component.DocumentModelAdapter;

public class CloudSettingsUI extends AbstractView{

	private static final long serialVersionUID = -8852831618021847782L;
	private JTextField dataSetTextField;
	private JTextField meshNameTextField;
	private JTextField syncRootUriTextField;
	
	
	public CloudSettingsUI(SettingsController controller){
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
		c.anchor = GridBagConstraints.WEST;
		this.add(getSyncServerUriLabel(),c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 1;
		c.weightx = 0.5;
		c.insets = new Insets(50, 20, 0, 10);
		this.add(getSyncServerUriTextBox(), c);
		
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = 2;
		c.weightx = 0;
		c.insets = new Insets(5, 10, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		this.add(getMeshNameLabel(), c);
		
	
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 2;
		c.weightx = 0.5;
		c.insets = new Insets(5, 20, 0, 10);
		this.add(getMeshNameTextBox(), c);
		
		
		
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = 3;
		c.weightx = 0;
		c.insets = new Insets(5, 10, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		this.add(getDataSetNameLabel(), c);
		
	
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 3;
		c.weightx = 0.5;
		c.weightx = 0.5;
		c.insets = new Insets(5, 20, 0, 10);
		this.add(getDataSetTextBox(), c);
		
		
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
		testButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				getController().loadDefaultCloudSettings();
			}
		});
		buttonPanel.add(testButton,BorderLayout.EAST);
		return buttonPanel;
	}
	
	private JLabel getSyncServerUriLabel(){
		JLabel langLabel = new JLabel("Sync server base Uri");
		//langLabel.setPreferredSize(new Dimension(150,20));
		return langLabel;
	}
	
	
	
	private JTextField getSyncServerUriTextBox(){
		 syncRootUriTextField = new JTextField();
		 syncRootUriTextField.getDocument().addDocumentListener(new DocumentModelAdapter(){
				@Override
				public void insertUpdate(DocumentEvent e) {
					getController().modifySettings(SettingsController.CLOUD_ROOT_URI, 
							syncRootUriTextField.getText());
				}
				@Override
				public void removeUpdate(DocumentEvent e) {
					getController().modifySettings(SettingsController.CLOUD_ROOT_URI, 
							syncRootUriTextField.getText());
				}
			});
		//userTextField.setPreferredSize(new Dimension(150,20));
		return syncRootUriTextField;
	}
	
	
	private JLabel getMeshNameLabel(){
		JLabel langLabel = new JLabel("Mesh name");
		//langLabel.setPreferredSize(new Dimension(150,20));
		return langLabel;
	}
	
	private JTextField getMeshNameTextBox(){
		meshNameTextField = new JTextField();
		meshNameTextField.getDocument().addDocumentListener(new DocumentModelAdapter(){
				@Override
				public void insertUpdate(DocumentEvent e) {
					getController().modifySettings(SettingsController.CLOUD_MESH_NAME, 
							meshNameTextField.getText());
				}
				@Override
				public void removeUpdate(DocumentEvent e) {
					getController().modifySettings(SettingsController.CLOUD_MESH_NAME, 
							meshNameTextField.getText());
				}
			});
		//userTextField.setPreferredSize(new Dimension(150,20));
		return meshNameTextField;
	}
	
	private JLabel getDataSetNameLabel(){
		JLabel langLabel = new JLabel("Data set");
		//langLabel.setPreferredSize(new Dimension(150,20));
		return langLabel;
	}
	private JTextField getDataSetTextBox(){
		 dataSetTextField = new JTextField();
		 dataSetTextField.getDocument().addDocumentListener(new DocumentModelAdapter(){
				@Override
				public void insertUpdate(DocumentEvent e) {
					getController().modifySettings(SettingsController.CLOUD_DATASET_NAME, 
							dataSetTextField.getText());
				}
				@Override
				public void removeUpdate(DocumentEvent e) {
					getController().modifySettings(SettingsController.CLOUD_DATASET_NAME, 
							dataSetTextField.getText());
				}
			});
		//userTextField.setPreferredSize(new Dimension(150,20));
		return dataSetTextField;
	}
	
	private SettingsController getController(){
		return (SettingsController)this.controller;
	}
	
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		String newValueAsString = evt.getNewValue().toString();
		if ( evt.getPropertyName().equals( SettingsController.CLOUD_ROOT_URI)){
			if(!syncRootUriTextField.getText().equals(newValueAsString))
			syncRootUriTextField.setText(newValueAsString);
		} else if ( evt.getPropertyName().equals( SettingsController.CLOUD_MESH_NAME)){
			if(!meshNameTextField.getText().equals(newValueAsString))
				meshNameTextField.setText(newValueAsString);
		} else if ( evt.getPropertyName().equals( SettingsController.CLOUD_DATASET_NAME)){
			if(!dataSetTextField.getText().equals(newValueAsString))
				dataSetTextField.setText(newValueAsString);
		}
		
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		return false;
	}

}
