package org.mesh4j.ektoo.controller;

import java.util.List;

import org.mesh4j.ektoo.model.FolderModel;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;

public class FolderUIController extends AbstractUIController
{
	public static final String FOLDER_NAME_PROPERTY = "FolderName";
	
	// BUSINESS METHODS
	public FolderUIController( boolean acceptsCreateDataset) {
		super( acceptsCreateDataset);
	}
	
	public void changeFileName(String fileName) {
		setModelProperty(FOLDER_NAME_PROPERTY, fileName);
	}

	@Override
	public ISyncAdapter createAdapter() {
		return createAdapter(null);
	}

	@Override
	public List<IRDFSchema> fetchSchema(ISyncAdapter adapter) {
		return null;
	}

	@Override
	public ISyncAdapter createAdapter(List<IRDFSchema> schema) {
		if(schema != null && schema.size() > 0){
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
		return getAdapterBuilder().createFolderAdapter(folderName);
	}

}
