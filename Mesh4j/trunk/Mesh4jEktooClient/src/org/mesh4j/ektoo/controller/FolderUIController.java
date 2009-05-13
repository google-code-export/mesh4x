package org.mesh4j.ektoo.controller;

import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.model.FolderModel;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.validations.Guard;

public class FolderUIController extends AbstractController
{
	public static final String FOLDER_NAME_PROPERTY = "FolderName";
	
	// MODEL VARIABLES
	ISyncAdapterBuilder adapterBuilder;

	// BUSINESS METHODS
	public FolderUIController(PropertiesProvider propertiesProvider) {
		Guard.argumentNotNull(propertiesProvider, "propertiesProvider");
		this.adapterBuilder = new SyncAdapterBuilder(propertiesProvider);
	}
	
	public void changeFileName(String fileName) {
		setModelProperty(FOLDER_NAME_PROPERTY, fileName);
	}

	@Override
	public ISyncAdapter createAdapter() {
		FolderModel model = (FolderModel) this.getModel();
		if (model == null){
			return null;
		}
		
		String folderName = model.getFolderName();
		if (folderName == null || folderName.trim().length() == 0){
			return null;
		}
		return this.adapterBuilder.createFolderAdapter(folderName);
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
