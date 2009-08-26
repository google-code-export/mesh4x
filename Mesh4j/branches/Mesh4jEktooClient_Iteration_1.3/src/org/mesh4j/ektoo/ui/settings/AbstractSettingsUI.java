package org.mesh4j.ektoo.ui.settings;
import static org.mesh4j.translator.MessageProvider.translate;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;

import org.mesh4j.ektoo.ui.AbstractView;
import org.mesh4j.translator.MessageNames;

public abstract class AbstractSettingsUI extends AbstractView{

	private static final long serialVersionUID = 3246932896145506263L;
	private JCheckBox defultCheckBox;
	private JButton defaultButton;
	

	public AbstractSettingsUI(SettingsController controller) {
		super(controller);
	}

	
	public abstract void modelPropertyChange(PropertyChangeEvent evt);
	public abstract boolean verify();
	public abstract void loadDefault();

	
	 

	protected JCheckBox getDefaultCheckBox(){
		if(defultCheckBox == null){
			defultCheckBox = new JCheckBox("Create  as default");
			defultCheckBox.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				getController().modifySettings(SettingsController.CREATE_PROP_AS_DEFAULT, 
						defultCheckBox.isSelected());
			}
		});
		}
		return defultCheckBox;
	}
	
	
	protected JButton getDefaultButton(){
		if(defaultButton == null){
		defaultButton = new JButton(translate(MessageNames.LABEL_BUTTON_RESTORE_DEFAULTS));
		defaultButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				loadDefault();
			}
		});
		}
		return defaultButton;
	}
	
	
	
	private SettingsController getController(){
		return (SettingsController)this.controller;
	}
}
