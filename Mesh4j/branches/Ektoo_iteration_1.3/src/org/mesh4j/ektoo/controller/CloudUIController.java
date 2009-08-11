package org.mesh4j.ektoo.controller;

import java.util.ArrayList;
import java.util.List;

import org.mesh4j.ektoo.Event;
import org.mesh4j.ektoo.model.CloudModel;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.ektoo.ui.EktooFrame;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.http.HttpSyncAdapter;
import org.mesh4j.sync.payload.mappings.IMapping;
import org.mesh4j.sync.payload.mappings.Mapping;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;

public class CloudUIController extends AbstractUIController{
	
	public static final String MESH_NAME_PROPERTY = "MeshName";
	public static final String DATASET_NAME_PROPERTY = "DatasetName";
	public static final String SYNC_SERVER_URI = "BaseUri";
	
	// BUSINESS METHODS
	public CloudUIController(PropertiesProvider propertiesProvider, boolean acceptsCreateDataset) {
		super(propertiesProvider, acceptsCreateDataset);
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
			return getAdapterBuilder().createHttpSyncAdapterForMultiDataset(baseSyncURI, meshName, schemas);
		}else{
			String datasetName = model.getDatasetName();
			if (datasetName == null || datasetName.trim().length() == 0){
				return null;
			}
			
			IRDFSchema rdfSchema = schemas == null || schemas.size() == 0 ? null : schemas.get(0);
			//if this is schema view task
			Event currentEvent = getCurrentEvent();
			if(currentEvent != null && (currentEvent.equals(Event.mappings_view_event) || currentEvent.equals(Event.schema_view_event))){
				return getAdapterBuilder().createHttpSyncAdapter(baseSyncURI, meshName, datasetName);
			} else {
				IMapping mapping = getMapping();
				return getAdapterBuilder().createHttpSyncAdapter(baseSyncURI, meshName, datasetName, rdfSchema, mapping);	
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

	@Override
	public Mapping getMapping() {
		CloudModel model = (CloudModel) this.getModel();
		if(model == null){
			return HttpSyncAdapter.getMappings(this.getUri(), getAdapterBuilder().getMappingPropertyResolvers());			
		} else {
			Mapping mapping = model.getMapping();
			if(mapping == null){
				mapping = HttpSyncAdapter.getMappings(this.getUri(), getAdapterBuilder().getMappingPropertyResolvers());
				model.setMapping(mapping);
			}
			return mapping;
		}
	}

}
