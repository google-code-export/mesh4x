package org.mesh4j.ektoo.ui.settings;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import org.mesh4j.ektoo.ui.component.DocumentModelAdapter;

public class GeneralSettingsUI extends AbstractSettingsUI{

	private static final long serialVersionUID = -6752780815799361963L;
	private SettingsController controller = null;
	private JTextField fileSourcePathTextField;
	private JTextField fileTargetPathTextField;
	private JComboBox langComboBox;
	
	

	public GeneralSettingsUI(SettingsController controller) {
		super(controller);
		this.controller = controller;
		this.setLayout(new GridBagLayout());
		init();
	}
	
	
	private void init(){
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth =2;
		c.insets = new Insets(50, 10, 0, 0);
		this.add(getLanguageLabel(), c);
	
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 1;
		c.gridwidth =2;
		c.insets = new Insets(50, 0, 0, 10);
		this.add(getLanguageComboBox(), c);
		
		
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = 2;
		c.insets = new Insets(5, 10, 0, 0);
		this.add(getDefaultSourceLabel(), c);
		
	
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 2;
		c.weightx = 0.5;
		c.gridwidth =2;
		c.insets = new Insets(5, 0, 0, 10);
		this.add(getSourceFileBrowser(), c);
		
		
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = 3;
		c.weightx = 0;
		c.gridwidth =1;
		c.insets = new Insets(5, 10, 0, 0);
		this.add(getDefaultTargetLabel(), c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 3;
		c.weightx = 0.5;
		c.gridwidth =2;
		c.insets = new Insets(5, 0, 0, 10);
		this.add(getTargetFileBrowser(), c);
		
		
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
				GeneralSettingsUI.this.controller.loadDefaultGeneralSettings();		
			}
		});
		buttonPanel.add(testButton,BorderLayout.EAST);
		
		return buttonPanel;
	}
	
	
	private JLabel getLanguageLabel(){
		JLabel langLabel = new JLabel("Language");
		langLabel.setPreferredSize(new Dimension(150,20));
		return langLabel;
	}
	
	private JComboBox getLanguageComboBox(){
		langComboBox = new JComboBox();
		langComboBox.addItem("english");
		langComboBox.addItem("System default");
		return langComboBox;
	}
	
	private JLabel getDefaultSourceLabel(){
		JLabel langLabel = new JLabel("Default source directory");
		langLabel.setPreferredSize(new Dimension(150,20));
		return langLabel;
	}
	
	private JLabel getDefaultTargetLabel(){
		JLabel langLabel = new JLabel("Default Target directory");
		langLabel.setPreferredSize(new Dimension(150,20));
		return langLabel;
	}
	
	private JPanel getSourceFileBrowser(){
		JPanel fileBrowserPanel = new JPanel(new BorderLayout());
		fileSourcePathTextField = new JTextField();
		fileSourcePathTextField.getDocument().addDocumentListener(new DocumentModelAdapter(){
			@Override
			public void insertUpdate(DocumentEvent e) {
				GeneralSettingsUI.this.controller.modifySettings(SettingsController.PATH_SOURCE, 
						fileSourcePathTextField.getText());
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				GeneralSettingsUI.this.controller.modifySettings(SettingsController.PATH_SOURCE, 
						fileSourcePathTextField.getText());
			}
		});
			
		
		JButton fileBrowserButton = new JButton();
	
		fileBrowserPanel.add(fileSourcePathTextField,BorderLayout.CENTER);
		fileBrowserPanel.add(fileBrowserButton,BorderLayout.EAST);
		return fileBrowserPanel;
	}
	
	private JPanel getTargetFileBrowser(){
		JPanel fileBrowserPanel = new JPanel(new BorderLayout());
		fileTargetPathTextField = new JTextField();
		fileTargetPathTextField.getDocument().addDocumentListener(new DocumentModelAdapter(){
			@Override
			public void insertUpdate(DocumentEvent e) {
				GeneralSettingsUI.this.controller.modifySettings(SettingsController.PATH_TARGET, 
						fileTargetPathTextField.getText());
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				GeneralSettingsUI.this.controller.modifySettings(SettingsController.PATH_TARGET, 
						fileTargetPathTextField.getText());
			}
		});
		
		JButton fileBrowserButton = new JButton();
	
		fileBrowserPanel.add(fileTargetPathTextField,BorderLayout.CENTER);
		fileBrowserPanel.add(fileBrowserButton,BorderLayout.EAST);
		return fileBrowserPanel;
	}
	
	public JFileChooser getFileChooser() {
		JFileChooser	fileChooser = new JFileChooser();
		return fileChooser;
	}
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		String newValueAsString = evt.getNewValue().toString();
		if ( evt.getPropertyName().equals( SettingsController.PATH_SOURCE)){
			if(!fileSourcePathTextField.getText().equals(newValueAsString))
			fileSourcePathTextField.setText(newValueAsString);
		} else if(evt.getPropertyName().equals( SettingsController.PATH_TARGET)){
			if(!fileTargetPathTextField.getText().equals(newValueAsString))
			fileTargetPathTextField.setText(newValueAsString);
		} else if(evt.getPropertyName().equals( SettingsController.LANGUAGE)){
			String langValueAsString = langComboBox.getSelectedItem().toString();
			if(!langValueAsString.equals(newValueAsString))
			langComboBox.setSelectedItem(evt.getNewValue().toString());
		}
		
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		return false;
	}

}
