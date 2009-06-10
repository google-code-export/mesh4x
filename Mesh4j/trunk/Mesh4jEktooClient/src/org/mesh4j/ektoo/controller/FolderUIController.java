package org.mesh4j.ektoo.controller;

import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.UISchema;
import org.mesh4j.ektoo.model.FolderModel;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.validations.Guard;

public class FolderUIController extends AbstractUIController
{
	public static final String FOLDER_NAME_PROPERTY = "FolderName";
	
	// MODEL VARIABLES
	ISyncAdapterBuilder adapterBuilder;

	// BUSINESS METHODS
	public FolderUIController(PropertiesProvider propertiesProvider, boolean acceptsCreateDataset) {
		super(acceptsCreateDataset);
		
		Guard.argumentNotNull(propertiesProvider, "propertiesProvider");
		this.adapterBuilder = new SyncAdapterBuilder(propertiesProvider);
	}
	
	public void changeFileName(String fileName) {
		setModelProperty(FOLDER_NAME_PROPERTY, fileName);
	}

	@Override
	public ISyncAdapter createAdapter() {
		return createAdapter(null);
	}

	@Override
	public UISchema fetchSchema(ISyncAdapter adapter) {
		return null;
	}

	@Override
	public ISyncAdapter createAdapter(UISchema schema) {
		if(schema != null){
			return null;
		}
		
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

}
