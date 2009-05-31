package org.mesh4j.ektoo.controller;

import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.model.FeedModel;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.validations.Guard;


public class FeedUIController extends AbstractUIController
{
	public static final String FILE_NAME_PROPERTY = "FileName";
	
	// MODEL VARIABLES
	ISyncAdapterBuilder adapterBuilder;

	// BUSINESS METHODS
	public FeedUIController(PropertiesProvider propertiesProvider) {
		Guard.argumentNotNull(propertiesProvider, "propertiesProvider");
		this.adapterBuilder = new SyncAdapterBuilder(propertiesProvider);
	}
	
	public void changeFileName(String fileName) {
		setModelProperty(FILE_NAME_PROPERTY, fileName);
	}

	@Override
	public ISyncAdapter createAdapter() {
		FeedModel model = (FeedModel) this.getModel();
		if (model == null){
			return null;
		}
		
		String feedFileName = model.getFileName();
		if (feedFileName == null || feedFileName.trim().length() == 0){
			return null;
		}
		return this.adapterBuilder.createFeedAdapter(model.getFeedTile(), model.getFeedDescription(), model.getFeedLink(), feedFileName, model.getSyndicationFormat());
	}

	@Override
	public IRDFSchema fetchSchema(ISyncAdapter adapter) {
		// TODO create Schema
		return null;
	}

	@Override
	public ISyncAdapter createAdapter(IRDFSchema schema) {
		// TODO create Adapter
		return null;
	}

}
