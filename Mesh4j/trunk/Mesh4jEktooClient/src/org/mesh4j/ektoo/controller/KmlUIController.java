package org.mesh4j.ektoo.controller;

import java.util.List;

import org.mesh4j.ektoo.model.KmlModel;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */
public class KmlUIController extends AbstractUIController
{
	public static final String FILE_NAME_PROPERTY = "FileName";
	
	// BUSINESS METHODS
	public KmlUIController(PropertiesProvider propertiesProvider, boolean acceptsCreateDataset) {
		super(propertiesProvider, acceptsCreateDataset);
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
		return getAdapterBuilder().createKMLAdapter(kmlFileName);
	}

	@Override
	public List<IRDFSchema> fetchSchema(ISyncAdapter adapter) {
		return null;
	}

	@Override
	public ISyncAdapter createAdapter(List<IRDFSchema> schemas) {
		if(schemas != null && schemas.size() > 0 ){
			return null;
		} 
		return createAdapter();
	}

}
