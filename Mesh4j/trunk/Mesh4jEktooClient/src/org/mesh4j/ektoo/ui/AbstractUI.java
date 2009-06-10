package org.mesh4j.ektoo.ui;

import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.mesh4j.ektoo.validator.IValidationStatus;

public abstract class AbstractUI extends JPanel implements IValidationStatus {
	
	private static final long serialVersionUID = 7515686485315720840L;
	
	// MODEL VARIABLES
	private JTextField txtMessages = null;
	
	// BUSINESS METHODS
	
	public abstract void modelPropertyChange(PropertyChangeEvent evt);
	public abstract boolean verify();

	protected JTextField getMessagesText() {
		if (txtMessages == null) {
			txtMessages = new JTextField();
			txtMessages.setBounds(new Rectangle(0, 140, 400, 20));
			txtMessages.setEditable(false);
		}
		return txtMessages;
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
