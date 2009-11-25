package org.mesh4j.meshes.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.meshes.model.AbstractModel;
import org.mesh4j.meshes.ui.AbstractView;

public abstract class AbstractController implements PropertyChangeListener {
	
	private final static Log LOGGER = LogFactory.getLog(AbstractController.class);

	private List<AbstractView> registeredViews;
	private List<AbstractModel> registeredModels;

	public AbstractController() {
		this.registeredViews = new ArrayList<AbstractView>();
		this.registeredModels = new ArrayList<AbstractModel>();
	}

	public void addModel(AbstractModel model) {
		registeredModels.add(model);
		model.addPropertyChangeListener(this);
	}

	public void removeModel(AbstractModel model) {
		registeredModels.remove(model);
		model.removePropertyChangeListener(this);
	}

	public void addView(AbstractView view) {
		registeredViews.add(view);
	}

	public void removeView(AbstractView view) {
		registeredViews.remove(view);
	}

	// Propagate property changes from model into views
	public void propertyChange(PropertyChangeEvent evt) {
		for (AbstractView view : registeredViews) {
			view.modelPropertyChange(evt);
		}
	}

	protected void setModelProperty(String propertyName, Object newValue) {
		for (AbstractModel model : registeredModels) {
			try {
				Method method = model.getClass().getMethod(
						"set" + propertyName,
						new Class[] { newValue.getClass() }
				);
				method.invoke(model, newValue);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

}
