package org.mesh4j.ektoo.controller;

import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.mesh4j.ektoo.Event;
import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.IUIController;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.model.AbstractModel;
import org.mesh4j.geo.coder.GeoCoderLatitudePropertyResolver;
import org.mesh4j.geo.coder.GeoCoderLocationPropertyResolver;
import org.mesh4j.geo.coder.GeoCoderLongitudePropertyResolver;
import org.mesh4j.sync.payload.mappings.Mapping;
import org.mesh4j.sync.utils.XMLHelper;

public abstract class AbstractUIController extends AbstractViewController implements  IUIController{
  private final static Log LOGGER = LogFactory.getLog(AbstractUIController.class);
	
//	// MODEL VARIABLES
//	private ArrayList<AbstractUI> registeredViews = new ArrayList<AbstractUI>();
//	private ArrayList<AbstractModel> registeredModels = new ArrayList<AbstractModel>();
	private boolean acceptsCreateDataset = false;
	private Event currentEvent = null;
	private ISyncAdapterBuilder adapterBuilder;
	
	// BUSINESS METHODS
	public AbstractUIController(boolean acceptsCreateDataset) {

//		Guard.argumentNotNull(propertiesProvider, "propertiesProvider");
		this.adapterBuilder = new SyncAdapterBuilder();
		this.acceptsCreateDataset = acceptsCreateDataset;
	}

	public AbstractUIController() {
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
	
//	public PropertiesProvider getPropertiesProvider() {
//		return getAdapterBuilder().getPropertiesProvider();
//	}
	
	public Mapping getMapping(){
		AbstractModel model = this.getModel();
		if(model != null){
			return model.getMapping();
		} else {
			return null;
		}
	}
	
	public Mapping makeMapping(String alias, String title, String description) {
		String xml = MessageFormat.format(
			"<mappings><item.title>{0}</item.title><item.description>{1}</item.description></mappings>", 
			title, 
			description);
		Element element = XMLHelper.parseElement(xml);
		Mapping mapping = new Mapping(element, this.adapterBuilder.getMappingPropertyResolvers());
		return mapping;
	}
	
	public Mapping makeMapping(String alias, String title, String description, String address) {
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

		return new Mapping(element, this.adapterBuilder.getMappingPropertyResolvers());
	}
	
	public Mapping makeMapping(String alias, String title, String description, String lat, String lon) {
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
		
		return new Mapping(element, this.adapterBuilder.getMappingPropertyResolvers());
	}
	
	public void setMapping(Mapping mapping){
		AbstractModel model = this.getModel();
		if(model != null){
			model.setMapping(mapping);
		}

	}
}
