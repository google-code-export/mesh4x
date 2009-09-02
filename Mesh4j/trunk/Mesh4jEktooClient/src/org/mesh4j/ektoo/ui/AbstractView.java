package org.mesh4j.ektoo.ui;

import java.beans.PropertyChangeEvent;

import javax.swing.JPanel;

import org.mesh4j.ektoo.controller.AbstractViewController;
/**
 * View could be domain specific like Settings view,data source view but in 
 * MVC model, all view must  listen or registered with model through the
 * Controller.
 */
public abstract class AbstractView extends JPanel {
	
	private static final long serialVersionUID = 4357113922140033715L;
	protected AbstractViewController controller;
	
	public AbstractView(AbstractViewController controller){
		this.controller = controller;
		this.controller.addView(this);
	}
	/**
	 * Every UI notified when any particular model property
	 * changes in its corresponding model component.
	 * @param evt, the property change event 
	 */
	public abstract void modelPropertyChange(PropertyChangeEvent evt);
	
	/**
	 * @return true or false , indicate if the UI component field has been 
	 * verified or not.
	 */
	public abstract boolean verify();
	 
	/**
	 * As every view attached with a particular controller
	 * subclass of AbstractView should  provide known <br>
	 * controller class name to get the particular controller 
	 * @param <T>, the class name which must extend from 
	 * 						<code>AbstractViewController </code> 
	 * @param clazz
	 * @return T which provided as the class name as parameter
	 */
	public <T extends AbstractViewController> T getController(Class<T> clazz){
		return  (T)controller;
	}
	
	
	
}
