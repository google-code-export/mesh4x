package org.mesh4j.ektoo.ui;

import java.awt.Component;
import java.awt.Cursor;

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
	public static final String DROPDOWN_CREATE_NEW_ITEM = "Create New";
	public static final String DROPDOWN_SELECT_ITEM = "Select Item";
	
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
	
	/**
	 * return whether the UI works as source or not
	 */
	public boolean isUIForSource(){
		return ((SyncItemUI)getParent().getParent()).getUiType().equals(SyncItemUI.UI_AS_SOURCE);
	}
	
	/**
	 * return whether the UI works as target or not
	 */
	public boolean isUIForTarget(){
		return ((SyncItemUI)getParent().getParent()).getUiType().equals(SyncItemUI.UI_AS_TARGET);
	}
	
	/**
	 * get localized UI Type name (Source/Target)
	 * @return
	 */
	private String getLocalizedUITypeName() {
		return isUIForSource() ? EktooUITranslator
				.getSourceSyncItemSelectorTitle():EktooUITranslator
				.getTargetSyncItemSelectorTitle();
	}
	
	/**
	 * when called with a value true for shouldFreeze, it disable all the ui
	 * components and sets the cursor busy and with a value false, it enables
	 * all the ui components and sets the cursor to default
	 * 
	 * @param shouldFreeze
	 */
	public void frzzeUI(boolean shouldFreeze){
		setCursor(Cursor.getPredefinedCursor(shouldFreeze ? Cursor.WAIT_CURSOR : Cursor.DEFAULT_CURSOR));
		for (Component comp : this.getComponents()){
			comp.setEnabled(!shouldFreeze);
		}
	}
	
	protected AbstractUI getMe(){
		return this;
	}
}
