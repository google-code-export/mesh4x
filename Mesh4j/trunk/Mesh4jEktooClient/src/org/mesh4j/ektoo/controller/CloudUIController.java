package org.mesh4j.ektoo.controller;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.mesh4j.ektoo.Event;
import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.model.CloudModel;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.ektoo.ui.EktooFrame;
import org.mesh4j.geo.coder.GeoCoderLatitudePropertyResolver;
import org.mesh4j.geo.coder.GeoCoderLocationPropertyResolver;
import org.mesh4j.geo.coder.GeoCoderLongitudePropertyResolver;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.http.HttpSyncAdapter;
import org.mesh4j.sync.payload.mappings.IMapping;
import org.mesh4j.sync.payload.mappings.Mapping;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.Guard;

public class CloudUIController extends AbstractUIController{
	
	public static final String MESH_NAME_PROPERTY = "MeshName";
	public static final String DATASET_NAME_PROPERTY = "DatasetName";
	public static final String SYNC_SERVER_URI = "BaseUri";
	

	// MODEL VARIABLES
	private ISyncAdapterBuilder adapterBuilder;

	// BUSINESS METHODS
	public CloudUIController(PropertiesProvider propertiesProvider, boolean acceptsCreateDataset) {
		super(acceptsCreateDataset);
		
		Guard.argumentNotNull(propertiesProvider, "propertiesProvider");
		this.adapterBuilder = new SyncAdapterBuilder(propertiesProvider);
	}

	public void changeMeshName(String meshName) {
		setModelProperty(MESH_NAME_PROPERTY, meshName);
	}

	public void changeDatasetName(String datasetName) {
		setModelProperty(DATASET_NAME_PROPERTY, datasetName);
	}

	public void changeSyncServerUri(String syncServerURI) {
		setModelProperty(SYNC_SERVER_URI, syncServerURI);
	}

	
	@Override
	public ISyncAdapter createAdapter() {
		return createAdapter(null);
	}

	@Override
	public List<IRDFSchema> fetchSchema(ISyncAdapter adapter) {
		ISchema schema = ((HttpSyncAdapter) adapter).getSchema();
		if(schema instanceof IRDFSchema){
			List<IRDFSchema> schemas = new ArrayList<IRDFSchema>();
			schemas.add((IRDFSchema) schema);
			return schemas;
		} else{
			return null;
		}
	}
	
	@Override
	public ISyncAdapter createAdapter(List<IRDFSchema> schemas) {
				
		CloudModel model = (CloudModel) this.getModel();
		if (model == null){
			return null;
		}

		String meshName = model.getMeshName();
		if (meshName == null || meshName.trim().length() == 0){
			return null;
		}
	
		String baseSyncURI = model.getBaseUri();
		if (baseSyncURI == null || baseSyncURI.trim().length() == 0){
			return null;
		}
		
		if(EktooFrame.multiModeSync){
			return adapterBuilder.createHttpSyncAdapterForMultiDataset(baseSyncURI, meshName, schemas);
		}else{
			String datasetName = model.getDatasetName();
			if (datasetName == null || datasetName.trim().length() == 0){
				return null;
			}
			
			IRDFSchema rdfSchema = schemas == null || schemas.size() == 0 ? null : schemas.get(0);
			//if this is schema view task
			if(getCurrentEvent().equals(Event.schema_view_event)){
				return adapterBuilder.createHttpSyncAdapter(baseSyncURI, meshName, datasetName);
			} else {
				IMapping mapping = getMappings();
				return adapterBuilder.createHttpSyncAdapter(baseSyncURI, meshName, datasetName, rdfSchema, mapping);	
			}
				
		}
	}
	
	public String getUri() {
		CloudModel model = (CloudModel) this.getModel();
		if (model == null){
			return null;
		} else {
			return model.getUri();
		}
	}

	public void setMappings(String alias, String title, String description) {
		String xml = MessageFormat.format(
			"<mappings><item.title>{0}</item.title><item.description>{1}</item.description></mappings>", 
			title, 
			description);
		Element element = XMLHelper.parseElement(xml);
		CloudModel model = (CloudModel)this.getModel();
		model.setMappings(new Mapping(element, this.adapterBuilder.getMappingPropertyResolvers()));
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
		CloudModel model = (CloudModel)this.getModel();
		model.setMappings(new Mapping(element, this.adapterBuilder.getMappingPropertyResolvers()));
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
		CloudModel model = (CloudModel)this.getModel();
		model.setMappings(new Mapping(element, this.adapterBuilder.getMappingPropertyResolvers()));
	}

	public Mapping getMappings() {
		CloudModel model = (CloudModel) this.getModel();
		if(model == null){
			return HttpSyncAdapter.getMappings(this.getUri(), this.adapterBuilder.getMappingPropertyResolvers());			
		} else {
			Mapping mapping = model.getMappings();
			if(mapping == null){
				mapping = HttpSyncAdapter.getMappings(this.getUri(), this.adapterBuilder.getMappingPropertyResolvers());
				model.setMappings(mapping);
			}
			return mapping;
		}
	}

	public void setEmptyMappings() {
		CloudModel model = (CloudModel) this.getModel();
		model.setMappings(null);
	}

}
