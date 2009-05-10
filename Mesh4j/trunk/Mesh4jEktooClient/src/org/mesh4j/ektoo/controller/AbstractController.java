package org.mesh4j.ektoo.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.IUIController;
import org.mesh4j.ektoo.model.AbstractModel;
import org.mesh4j.ektoo.ui.AbstractUI;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */
public abstract class AbstractController implements PropertyChangeListener, IUIController
{
  private final static Log LOGGER = LogFactory.getLog(AbstractController.class);
	
	// MODEL VARIABLES
	private ArrayList<AbstractUI> registeredViews = new ArrayList<AbstractUI>();
	private ArrayList<AbstractModel> registeredModels = new ArrayList<AbstractModel>();

	// BUSINESS METHODS
	public AbstractController() {}

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

	public void removeModel(AbstractUI view) {
		registeredViews.remove(view);
	}

	public void propertyChanged(PropertyChangeEvent evt) {
		for (AbstractUI view : registeredViews) {
			view.modelPropertyChange(evt);
		}
	}

	protected void setModelProperty(String propertyName, Object newValue) {
	  System.out.println("3...");
		for (AbstractModel model : registeredModels) {
			try {
				Method method = model.getClass().getMethod(
						"set" + propertyName,
						new Class[] { newValue.getClass() });
				method.invoke(model, newValue);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
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
}
