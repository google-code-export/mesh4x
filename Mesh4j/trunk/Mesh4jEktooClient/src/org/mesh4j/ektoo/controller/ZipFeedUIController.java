package org.mesh4j.ektoo.controller;

import java.util.List;

import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.model.ZipFeedModel;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.validations.Guard;


public class ZipFeedUIController extends AbstractUIController
{
	public static final String FILE_NAME_PROPERTY = "FileName";
	
	// MODEL VARIABLES
	ISyncAdapterBuilder adapterBuilder;

	// BUSINESS METHODS
	public ZipFeedUIController(PropertiesProvider propertiesProvider, boolean acceptsCreateDataset) {
		super(acceptsCreateDataset);
		
		Guard.argumentNotNull(propertiesProvider, "propertiesProvider");
		this.adapterBuilder = new SyncAdapterBuilder(propertiesProvider);
	}
	
	public void changeFileName(String fileName) {
		setModelProperty(FILE_NAME_PROPERTY, fileName);
	}

	@Override
	public ISyncAdapter createAdapter() {
		ZipFeedModel model = (ZipFeedModel) this.getModel();
		if (model == null){
			return null;
		}
		
		String zipFileName = model.getFileName();
		if (zipFileName == null || zipFileName.trim().length() == 0){
			return null;
		}
		return this.adapterBuilder.createZipFeedAdapter(zipFileName);
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
