package org.mesh4j.ektoo.controller;

import java.util.HashMap;

import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.model.CloudModel;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.ektoo.ui.EktooFrame;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.http.HttpSyncAdapter;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.validations.Guard;

public class CloudUIController extends AbstractUIController
{
	
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
	public HashMap<IRDFSchema, String> fetchSchema(ISyncAdapter adapter) {
		ISchema schema = ((HttpSyncAdapter) adapter).getSchema();
		if(schema instanceof IRDFSchema){
			HashMap<IRDFSchema, String> schemas = new HashMap<IRDFSchema, String>();
			schemas.put((IRDFSchema) schema, null);
			return schemas;
		} else{
			return null;
		}
	}

	@Override
	public ISyncAdapter createAdapter(HashMap<IRDFSchema, String> schemas) {
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
			
			IRDFSchema rdfSchema = schemas == null || schemas.size() == 0 ? null : schemas.entrySet().iterator().next().getKey(); 
			return adapterBuilder.createHttpSyncAdapter(baseSyncURI, meshName, datasetName, rdfSchema);
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


}
