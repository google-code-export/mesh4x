package org.mesh4j.ektoo.controller;

import java.util.List;

import org.mesh4j.ektoo.model.FeedModel;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;


public class FeedUIController extends AbstractUIController
{
	public static final String FILE_NAME_PROPERTY = "FileName";
	
	// BUSINESS METHODS
	public FeedUIController(PropertiesProvider propertiesProvider, boolean acceptsCreateDataset) {
		super(propertiesProvider, acceptsCreateDataset);
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
		return getAdapterBuilder().createFeedAdapter(model.getFeedTile(), model.getFeedDescription(), model.getFeedLink(), feedFileName, model.getSyndicationFormat());
	}

	@Override
	public List<IRDFSchema> fetchSchema(ISyncAdapter adapter) {
		return null;
	}

	@Override
	public ISyncAdapter createAdapter(List<IRDFSchema> schemas) {
		return createAdapter();
	}

}
