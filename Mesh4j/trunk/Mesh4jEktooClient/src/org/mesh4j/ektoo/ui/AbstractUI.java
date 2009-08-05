package org.mesh4j.ektoo.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.mesh4j.ektoo.Event;
import org.mesh4j.ektoo.controller.AbstractUIController;
import org.mesh4j.ektoo.tasks.OpenMappingsViewTask;
import org.mesh4j.ektoo.tasks.OpenResolveConflictsViewTask;
import org.mesh4j.ektoo.tasks.SchemaViewTask;
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
	private JButton viewButton = null;
	private JButton mappingsButton = null;
	private JButton conflictsButton = null;
	protected AbstractUIController controller = null;
	
	// BUSINESS METHODS
	public AbstractUI(AbstractUIController controller){		
		this.controller = controller;
		this.controller.addView(this);
	}
	
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
	
	public JButton getSchemaViewButton(){
		if(schemaViewButton == null){
			schemaViewButton = new JButton();
			schemaViewButton.setIcon(ImageManager.getSchemaViewIcon());
			schemaViewButton.setContentAreaFilled(false);
			schemaViewButton.setBorderPainted(false);
			schemaViewButton.setBorder(new EmptyBorder(0, 0, 0, 0));
			schemaViewButton.setBackground(Color.WHITE);
			schemaViewButton.setBounds(new Rectangle(330, 60, 25, 25));
			schemaViewButton.setToolTipText(EktooUITranslator.getTooltipSchemaView());
			schemaViewButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (verify()) {
						controller.setCurrentEvent(Event.schema_view_event);
						EktooFrame ektooFrame = ((EktooFrame)getRootFrame());
						SchemaViewTask task = new SchemaViewTask(ektooFrame, controller, ektooFrame);
						task.execute();
					}
				}
			});
		}
		return schemaViewButton;
	}
	
	protected JButton getViewButton(){
		if (viewButton == null) {
			viewButton = new JButton();
			viewButton.setIcon(ImageManager.getViewIcon());
			viewButton.setContentAreaFilled(false);
			viewButton.setBorderPainted(false);
			viewButton.setBorder(new EmptyBorder(0, 0, 0, 0));
			viewButton.setBackground(Color.WHITE);
			viewButton.setText("");
			viewButton.setToolTipText(EktooUITranslator.getTooltipView());
			viewButton.setBounds(new Rectangle(295, 0, 30, 40));
		}
		return viewButton;
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
	protected String getLocalizedUITypeName() {
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
	public void freezeUI(boolean shouldFreeze){
		setCursor(Cursor.getPredefinedCursor(shouldFreeze ? Cursor.WAIT_CURSOR : Cursor.DEFAULT_CURSOR));
		for (Component comp : this.getComponents()){
			comp.setEnabled(!shouldFreeze);
		}
	}
	
	protected AbstractUI getMe(){
		return this;
	}
	
	public JButton getMappingsButton() {
		if (mappingsButton == null) {
			mappingsButton = new JButton();
			mappingsButton.setIcon(ImageManager.getMappingsIcon());
			mappingsButton.setContentAreaFilled(false);
			mappingsButton.setBorderPainted(false);
			mappingsButton.setBorder(new EmptyBorder(0, 0, 0, 0));
			mappingsButton.setBackground(Color.WHITE);
			mappingsButton.setBounds(new Rectangle(330, 34, 25, 25));
			mappingsButton.setToolTipText(EktooUITranslator
					.getTooltipMappingView());
			mappingsButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (verify()) {
						EktooFrame ektooFrame = ((EktooFrame)getRootFrame());
						OpenMappingsViewTask task = new OpenMappingsViewTask(ektooFrame, controller, true);
						task.execute();
					}
				}
			});
		}
		return mappingsButton;
	}
	
	public JButton getConflictsButton() {
		if (conflictsButton == null) {
			conflictsButton = new JButton();
			conflictsButton.setIcon(ImageManager.getConflictsIcon());
			conflictsButton.setContentAreaFilled(false);
			conflictsButton.setBorderPainted(false);
			conflictsButton.setBorder(new EmptyBorder(0, 0, 0, 0));
			conflictsButton.setBackground(Color.WHITE);
			conflictsButton.setBounds(new Rectangle(330, 8, 25, 25));
			conflictsButton.setToolTipText(EktooUITranslator.getTooltipConflictsView());
			conflictsButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (verify()) {
						EktooFrame ektooFrame = ((EktooFrame)getRootFrame());
						OpenResolveConflictsViewTask task = new OpenResolveConflictsViewTask(ektooFrame, controller);
						task.execute();
					}
				}
			});
		}
		return conflictsButton;
	}
}
