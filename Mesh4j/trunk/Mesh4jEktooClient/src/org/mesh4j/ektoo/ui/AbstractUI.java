package org.mesh4j.ektoo.ui;

import java.awt.Color;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.mesh4j.ektoo.ui.image.ImageManager;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.ektoo.validator.IValidationStatus;

public abstract class AbstractUI extends JPanel implements IValidationStatus {
	
	private static final long serialVersionUID = 7515686485315720840L;
	
	// MODEL VARIABLES
	private JTextField txtMessages = null;
	private JButton schemaViewButton = null;
	
	// BUSINESS METHODS
	
	public abstract void modelPropertyChange(PropertyChangeEvent evt);
	public abstract boolean verify();
	

	protected JTextField getMessagesText() {
		if (txtMessages == null) {
			txtMessages = new JTextField();
			txtMessages.setBounds(new Rectangle(0, 170, 400, 20));
			txtMessages.setEditable(false);
		}
		return txtMessages;
	}
	
	protected JButton getSchemaButton(){
		if(schemaViewButton == null){
			schemaViewButton = new JButton();
			schemaViewButton.setIcon(ImageManager.getSchemaViewIcon());
			schemaViewButton.setContentAreaFilled(false);
			schemaViewButton.setBorderPainted(false);
			schemaViewButton.setBorder(new EmptyBorder(0, 0, 0, 0));
			schemaViewButton.setBackground(Color.WHITE);
			schemaViewButton.setBounds(new Rectangle(330, 8, 30, 20));
			schemaViewButton.setToolTipText(EktooUITranslator.getTooltipSchemaView());
		}
		return schemaViewButton;
	}
	
	public void setMessageText(String msg){
		this.txtMessages.setText(msg);
	}
	
	public void cleanMessages() {
		this.txtMessages.setText("");
	}

	@Override
	public void validationFailed(Hashtable<Object, String> errorTable) {
		((SyncItemUI)this.getParent().getParent()).openErrorPopUp(errorTable);
	}
	
	@Override
	public void validationPassed() {
		// nothing to do
	}
	
	protected JFrame getRootFrame() {
		return (JFrame)this.getParent().getParent().getParent().getParent().getParent().getParent().getParent();
	}
}
