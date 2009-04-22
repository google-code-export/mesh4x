package org.mesh4j.ektoo.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.mesh4j.ektoo.model.AbstractModel;
import org.mesh4j.ektoo.ui.AbstractUI;
/**
 * @author Bhuiyan Mohammad Iklash
 *
 */
public abstract class AbstractController implements PropertyChangeListener
{
	private ArrayList<AbstractUI> registeredViews = null;
	private ArrayList<AbstractModel> registeredModels = null;
	
	public AbstractController()
	{
		registeredViews = new ArrayList<AbstractUI>();
		registeredModels = new ArrayList<AbstractModel>();
	}
	
	public void addModel(AbstractModel model)
	{
		registeredModels.add(model);
		model.addPropertyChangeListner(this);
	}
	
	public ArrayList<AbstractModel> getModels()
	{
		return this.registeredModels;
	}
	public AbstractModel getModel()
	{
		if ( this.registeredModels.isEmpty() )
			return null;
			
		return (AbstractModel)this.registeredModels.get(0);
	}
	public AbstractModel getModel(int index)
	{
		if ( this.registeredModels.isEmpty() || this.registeredModels.size() < index)
			return null;

		return (AbstractModel)this.registeredModels.get(index);
	}

	
	public void removeModel(AbstractModel model)
	{
		registeredModels.remove(model);
		model.removePropertyChangeListner(this);
	}

	public void addView(AbstractUI view)
	{
		registeredViews.add(view);
	}

	public void removeModel(AbstractUI view)
	{
		registeredViews.remove(view);
	}
	
	public void propertyChanged(PropertyChangeEvent evt)
	{
		for(AbstractUI view : registeredViews)
		{
			view.modelPropertyChange(evt);
		}
	}
	
	protected void setModelProperty(String propertyName, Object newValue)
	{
		for(AbstractModel model :  registeredModels)
		{
			try	
			{
				Method method = model.getClass().getMethod(	"set"+propertyName, 
															new Class[]
															{
																newValue.getClass()
															}
														  );
				method.invoke(model, newValue);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
  protected void setModelProperty(String propertyName, int newValue)
  {
    for(AbstractModel model :  registeredModels)
    {
      try 
      {
        Method[] methods = model.getClass().getMethods();
        Method method = null;
        for (int i=0; i < methods.length; i++)
        {
          method = methods[i];
          if ( method.getName().equals("set"+propertyName))
          {
            method.invoke(model, newValue);
            break;    
          }
        }
      }
      catch(Exception ex)
      {
        ex.printStackTrace();
      }
    }
  }
	
}
