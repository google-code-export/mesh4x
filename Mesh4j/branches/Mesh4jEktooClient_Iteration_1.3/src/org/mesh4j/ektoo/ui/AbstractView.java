package org.mesh4j.ektoo.ui;

import java.beans.PropertyChangeEvent;

import javax.swing.JPanel;

import org.mesh4j.ektoo.controller.AbstractViewController;

public abstract class AbstractView extends JPanel {
	
	private static final long serialVersionUID = 4357113922140033715L;
	protected AbstractViewController controller;
	
	public AbstractView(AbstractViewController controller){
		this.controller = controller;
		this.controller.addView(this);
	}
	public abstract void modelPropertyChange(PropertyChangeEvent evt);
	public abstract boolean verify();
	 
	
}
