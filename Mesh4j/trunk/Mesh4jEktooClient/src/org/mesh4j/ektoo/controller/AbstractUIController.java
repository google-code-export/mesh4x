package org.mesh4j.ektoo.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.mesh4j.ektoo.Event;
import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.IUIController;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.model.AbstractModel;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.ektoo.ui.AbstractUI;
import org.mesh4j.geo.coder.GeoCoderLatitudePropertyResolver;
import org.mesh4j.geo.coder.GeoCoderLocationPropertyResolver;
import org.mesh4j.geo.coder.GeoCoderLongitudePropertyResolver;
import org.mesh4j.sync.payload.mappings.Mapping;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.Guard;

public abstract class AbstractUIController implements PropertyChangeListener, IUIController{
  private final static Log LOGGER = LogFactory.getLog(AbstractUIController.class);
	
	// MODEL VARIABLES
	private ArrayList<AbstractUI> registeredViews = new ArrayList<AbstractUI>();
	private ArrayList<AbstractModel> registeredModels = new ArrayList<AbstractModel>();
	private boolean acceptsCreateDataset = false;
	private Event currentEvent = null;
	private ISyncAdapterBuilder adapterBuilder;
	
	// BUSINESS METHODS
	public AbstractUIController(PropertiesProvider propertiesProvider, boolean acceptsCreateDataset) {

		Guard.argumentNotNull(propertiesProvider, "propertiesProvider");
		this.adapterBuilder = new SyncAdapterBuilder(propertiesProvider);
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

	// mappings
	public ISyncAdapterBuilder getAdapterBuilder(){
		return this.adapterBuilder;
	}
	
	public PropertiesProvider getPropertiesProvider() {
		return getAdapterBuilder().getPropertiesProvider();
	}
	
	public Mapping getMappings(){
		AbstractModel model = this.getModel();
		if(model != null){
			return model.getMappings();
		} else {
			return null;
		}
	}
	
	public void setEmptyMappings() {
		AbstractModel model = this.getModel();
		if(model != null){
			model.setMappings(null);
		}
	}
	
	public void setMappings(String alias, String title, String description) {
		String xml = MessageFormat.format(
			"<mappings><item.title>{0}</item.title><item.description>{1}</item.description></mappings>", 
			title, 
			description);
		Element element = XMLHelper.parseElement(xml);

		AbstractModel model = this.getModel();
		if(model != null){
			model.setMappings(new Mapping(element, this.adapterBuilder.getMappingPropertyResolvers()));
		}
	}
	
	public void setMappings(String alias, String title, String description, String address) {
		String addressAttribute = Mapping.makeAttribute(alias, address);
		String location = Mapping.makeMapping(GeoCoderLocationPropertyResolver.makeMapping(addressAttribute));
		String latitude = Mapping.makeMapping(GeoCoderLatitudePropertyResolver.makeMapping(addressAttribute, true)); 
		String longitude = Mapping.makeMapping(GeoCoderLongitudePropertyResolver.makeMapping(addressAttribute, true));
		String xml = MessageFormat.format(
			"<mappings><item.title>{0}</item.title><item.description>{1}</item.description><geo.location>{2}</geo.location><geo.longitude>{3}</geo.longitude><geo.latitude>{4}</geo.latitude></mappings>", 
			title, 
			description, 
			location,
			longitude,
			latitude);
		
		Element element = XMLHelper.parseElement(xml);

		AbstractModel model = this.getModel();
		if(model != null){
			model.setMappings(new Mapping(element, this.adapterBuilder.getMappingPropertyResolvers()));
		}
	}
	
	public void setMappings(String alias, String title, String description, String lat, String lon) {
		String attrLat = Mapping.makeAttribute(alias, lat);
		String attrLon = Mapping.makeAttribute(alias, lon);
		String latitude = Mapping.makeMapping(GeoCoderLatitudePropertyResolver.makeMapping(attrLat, false)); 
		String longitude = Mapping.makeMapping(GeoCoderLongitudePropertyResolver.makeMapping(attrLon, false));
		String xml = MessageFormat.format(
			"<mappings><item.title>{0}</item.title><item.description>{1}</item.description><geo.longitude>{2}</geo.longitude><geo.latitude>{3}</geo.latitude></mappings>", 
			title, 
			description, 
			longitude,
			latitude);
		Element element = XMLHelper.parseElement(xml);
		
		AbstractModel model = this.getModel();
		if(model != null){
			model.setMappings(new Mapping(element, this.adapterBuilder.getMappingPropertyResolvers()));
		}
	}
}
