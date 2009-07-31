package org.mesh4j.ektoo.controller;

import java.util.ArrayList;
import java.util.List;

import org.mesh4j.ektoo.model.MsExcelModel;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.ektoo.ui.EktooFrame;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.composite.CompositeSyncAdapter;
import org.mesh4j.sync.adapters.msexcel.MsExcelContentAdapter;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;

public class MsExcelUIController extends AbstractUIController {

	public static final String WORKBOOK_NAME_PROPERTY = "WorkbookName";
	public static final String WORKSHEET_NAME_PROPERTY = "WorksheetName";
	public static final String UNIQUE_COLUMN_NAME_PROPERTY = "UniqueColumnName";

	// BUSINESS METHODS
	public MsExcelUIController(PropertiesProvider propertiesProvider, boolean acceptsCreateDataset) {
		super(propertiesProvider, acceptsCreateDataset);
	}

	public void changeWorkbookName(String workbookName) {
		setModelProperty(WORKBOOK_NAME_PROPERTY, workbookName);
	}

	public void changeWorksheetName(String worksheetName) {
		setModelProperty(WORKSHEET_NAME_PROPERTY, worksheetName);

	}

	public void changeUniqueColumnName(String uniqueColumnName) {
		setModelProperty(UNIQUE_COLUMN_NAME_PROPERTY, uniqueColumnName);
	}

	@Override
	public ISyncAdapter createAdapter() {
		MsExcelModel model = (MsExcelModel) this.getModel();
		if (model == null) {
			return null;
		}

		String workbookName = model.getWorkbookName();
		if (workbookName == null || workbookName.trim().length() == 0) {
			return null;
		}

		String worksheetName = model.getWorksheetName();
		if (worksheetName == null || worksheetName.trim().length() == 0) {
			return null;
		}

		String uniqueColumnName = model.getUniqueColumnName();
		if (uniqueColumnName == null || uniqueColumnName.trim().length() == 0) {
			return null;
		}
		
		ArrayList<String> pks = new ArrayList<String>();
		pks.add(uniqueColumnName);

		return getAdapterBuilder().createMsExcelAdapter(workbookName, worksheetName, new String[]{uniqueColumnName}, true); 
	}

	@Override
	public ISyncAdapter createAdapter(List<IRDFSchema> schemas) {
		
		if(EktooFrame.multiModeSync){
			MsExcelModel model = (MsExcelModel) this.getModel();
			if (model == null) { return null; }
			
			String workbookName = model.getWorkbookName();
			if (workbookName == null || workbookName.trim().length() == 0) {
				return null;
			}
			return getAdapterBuilder().createMsExcelAdapterForMultiSheets(workbookName, schemas);		
		
		} else {
					
			if (schemas == null || schemas.size() == 0) {
				return null;
			}
			
			IRDFSchema sourceSchema = schemas.get(0);
			
			MsExcelModel model = (MsExcelModel) this.getModel();
			if (model == null) {
				return null;
			}
	
			String workbookName = model.getWorkbookName();
			if (workbookName == null || workbookName.trim().length() == 0) {
				return null;
			}
			return getAdapterBuilder().createMsExcelAdapter(workbookName, sourceSchema);
		}
		
	}

	@Override
	public List<IRDFSchema> fetchSchema(ISyncAdapter adapter) {
		ArrayList<IRDFSchema> schemas = new ArrayList<IRDFSchema>();
		if(EktooFrame.multiModeSync && adapter instanceof CompositeSyncAdapter){
			// TODO (SHARIF/RAJU) ????
		} else {
			MsExcelContentAdapter contentAdapter = (MsExcelContentAdapter) ((SplitAdapter) adapter).getContentAdapter();
			IRDFSchema rdfSchema = (IRDFSchema)contentAdapter.getSchema();
			schemas.add(rdfSchema);
		}
		return schemas;
	}

}
