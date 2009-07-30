package org.mesh4j.ektoo.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.Event;
import org.mesh4j.ektoo.IUIController;
import org.mesh4j.ektoo.model.AbstractModel;
import org.mesh4j.ektoo.ui.AbstractUI;

public abstract class AbstractUIController implements PropertyChangeListener, IUIController{
  private final static Log LOGGER = LogFactory.getLog(AbstractUIController.class);
	
	// MODEL VARIABLES
	private ArrayList<AbstractUI> registeredViews = new ArrayList<AbstractUI>();
	private ArrayList<AbstractModel> registeredModels = new ArrayList<AbstractModel>();
	private boolean acceptsCreateDataset = false;
	private Event currentEvent = null;

	// BUSINESS METHODS
	public AbstractUIController(boolean acceptsCreateDataset) {
		this.acceptsCreateDataset = acceptsCreateDataset;
	}

	public AbstractUIController() {
	}
	
	public void addModel(AbstractModel model) 
	{
		registeredModels.add(model);
		model.addPropertyChangeListner(this);
	}
	
	public String toString()
  {
	  AbstractModel model = getModel();
	  
    return (model == null) ? "" : model.toString();
  }
	
	public ArrayList<AbstractModel> getModels() 
	{
		return this.registeredModels;
	}

	public AbstractModel getModel() 
	{
		if (this.registeredModels.isEmpty())
			return null;
	
		return this.registeredModels.get(0);
	}

	public AbstractModel getModel(int index) 
	{
		if (this.registeredModels.isEmpty() || this.registeredModels.size() < index)
			return null;

		return this.registeredModels.get(index);
	}

	public void removeModel(AbstractModel model) {
		registeredModels.remove(model);
		model.removePropertyChangeListner(this);
	}

	public void addView(AbstractUI view) {
		registeredViews.add(view);
	}

	protected AbstractUI getView(){
		if(!this.registeredViews.isEmpty()){
			return this.registeredViews.get(0);
		}
		return null;
	}
	
	public void removeModel(AbstractUI view) {
		registeredViews.remove(view);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) 
	{
		for (AbstractUI view : registeredViews) {
			view.modelPropertyChange(evt);
		}
	}

	protected void setModelProperty(String propertyName, Object newValue) {
		for (AbstractModel model : registeredModels) {
			String methodName = "set" + propertyName;
			if(isMethodExist(model, methodName)){
				Method method;
				try {
					method = model.getClass().getMethod(
							methodName,new Class[] { newValue.getClass() });
					method.invoke(model, newValue);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				} 
			}
		}
	}

	private boolean isMethodExist(Object obj,String name){
		for(Method method :obj.getClass().getDeclaredMethods()){
			if(name.equals(method.getName())){
				return true;
			}
		}
		return false;
	}
	
	protected void setModelProperty(String propertyName, int newValue) {
		for (AbstractModel model : registeredModels) {
			try {
				Method[] methods = model.getClass().getMethods();
				Method method = null;
				for (int i = 0; i < methods.length; i++) {
					method = methods[i];
					if (method.getName().equals("set" + propertyName)) {
						method.invoke(model, newValue);
						break;
					}
				}
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}
	
	public void setCurrentEvent(Event currentEvent){
		this.currentEvent = currentEvent;
	}
	public Event getCurrentEvent(){
		return this.currentEvent;
	}
	public boolean acceptsCreateDataset() {
		return this.acceptsCreateDataset;
	}

}
