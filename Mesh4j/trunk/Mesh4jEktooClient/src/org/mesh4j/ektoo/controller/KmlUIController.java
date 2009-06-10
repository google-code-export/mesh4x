package org.mesh4j.ektoo.controller;

import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.UISchema;
import org.mesh4j.ektoo.model.KmlModel;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.validations.Guard;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */
public class KmlUIController extends AbstractUIController
{
	public static final String FILE_NAME_PROPERTY = "FileName";
	
	// MODEL VARIABLES
	ISyncAdapterBuilder adapterBuilder;

	// BUSINESS METHODS
	public KmlUIController(PropertiesProvider propertiesProvider, boolean acceptsCreateDataset) {
		super(acceptsCreateDataset);
		
		Guard.argumentNotNull(propertiesProvider, "propertiesProvider");
		this.adapterBuilder = new SyncAdapterBuilder(propertiesProvider);
	}
	
	public void changeFileName(String fileName) {
		setModelProperty(FILE_NAME_PROPERTY, fileName);
	}

	@Override
	public ISyncAdapter createAdapter() {
		KmlModel model = (KmlModel) this.getModel();
		if (model == null){
			return null;
		}
		
		String kmlFileName = model.getFileName();
		if (kmlFileName == null || kmlFileName.trim().length() == 0){
			return null;
		}
		return this.adapterBuilder.createKMLAdapter(kmlFileName);
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
		return createAdapter();
	}

}
