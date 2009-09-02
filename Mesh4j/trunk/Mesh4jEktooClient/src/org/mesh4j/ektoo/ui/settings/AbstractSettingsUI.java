package org.mesh4j.ektoo.ui.settings;
import static org.mesh4j.translator.MessageProvider.translate;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import org.mesh4j.ektoo.ui.AbstractView;
import org.mesh4j.translator.MessageNames;

public abstract class AbstractSettingsUI extends AbstractView{

	private static final long serialVersionUID = 3246932896145506263L;
	private JCheckBox defultCheckBox;
	private JButton defaultButton;
	private JPanel headerPane;
	private String title = "";
	

	public AbstractSettingsUI(SettingsController controller,String title) {
		super(controller);
		this.title = title;
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
				getController(SettingsController.class).
				modifySettings(SettingsController.CREATE_PROP_AS_DEFAULT, 
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
	
	
	private JComponent getSeperator(){ 
		 JPanel spePanel = new JPanel(new GridLayout(1,1,0,0));
		 spePanel.setOpaque(false);
		 JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
		 spePanel.add(separator);
	    return spePanel;
	  }
	
	protected JPanel getHeaderPane(){
		if(headerPane == null){
			headerPane = new JPanel(new BorderLayout());
			JPanel titlePane = new JPanel();
			JLabel titleLabel = new JLabel(title);
			titleLabel.setFont(new Font(Font.SANS_SERIF,Font.BOLD,15));
			titlePane.add(titleLabel);
			headerPane.add(titlePane,BorderLayout.CENTER);
			headerPane.add(getSeperator(),BorderLayout.SOUTH);
			headerPane.setPreferredSize(new Dimension(100,40));
		}
		return headerPane;
	}
	
	
}
