package org.mesh4j.ektoo.controller;

import java.util.ArrayList;
import java.util.List;

import org.mesh4j.ektoo.GoogleSpreadSheetInfo;
import org.mesh4j.ektoo.model.GSSheetModel;
import org.mesh4j.ektoo.ui.EktooFrame;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.composite.CompositeSyncAdapter;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadSheetContentAdapter;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadsheet;
import org.mesh4j.sync.adapters.googlespreadsheet.IGoogleSpreadSheet;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.validations.MeshException;

public class GSSheetUIController extends AbstractUIController {
	
	public static final String USER_NAME_PROPERTY = "UserName";
	public static final String USER_PASSWORD_PROPERTY = "UserPassword";
	public static final String SPREADSHEET_NAME_PROPERTY = "SpreadsheetName";
	public static final String WORKSHEET_NAME_PROPERTY = "WorksheetName";
	public static final String UNIQUE_COLUMN_NAME_PROPERTY = "UniqueColumnNames";
	public static final String GOOGLE_SPREADSHEET_PROPERTY = "GSpreadsheet";

	// BUSINESS METHODS
	public GSSheetUIController( boolean acceptsCreateDataset) {
		super( acceptsCreateDataset);
	}

	public void changeUserName(String userName) {
		setModelProperty(USER_NAME_PROPERTY, userName);
	}

	public void changeUserPassword(String userPassword) {
		setModelProperty(USER_PASSWORD_PROPERTY, userPassword);
	}

	public void changeSpreadsheetName(String spreadsheetName) {
		setModelProperty(SPREADSHEET_NAME_PROPERTY, spreadsheetName);
	}
	
	public void changeWorksheetName(String worksheetName) {
		setModelProperty(WORKSHEET_NAME_PROPERTY, worksheetName);
	}

	public void changeUniqueColumnNames(String[] uniqueColumnNames) {
		setModelProperty(UNIQUE_COLUMN_NAME_PROPERTY, uniqueColumnNames);
	}

	public void changeGSpreadsheet(IGoogleSpreadSheet gSpreadsheet) {
		setModelProperty(GOOGLE_SPREADSHEET_PROPERTY, gSpreadsheet);
	}
	
	@Override
	public ISyncAdapter createAdapter() {
		GSSheetModel model = (GSSheetModel) this.getModel();
		
		IGoogleSpreadSheet gss;
		
		if(model.getGSpreadsheet() == null){
			gss = new GoogleSpreadsheet(model.getSpreadsheetName(), model.getUserName(), model.getUserPassword());
			model.setGSpreadsheet((GoogleSpreadsheet) gss);
		}
		
//		ArrayList<String> pks = new ArrayList<String>();
//		pks.add(model.getUniqueColumnNames());
		
//		IRDFSchema rdfSchema = GoogleSpreadsheetToRDFMapping.extractRDFSchema(
//			model.getGSpreadsheet(), 
//			model.getWorksheetName(),
//			pks, 
//			/*model.getLastUpdatedColumnName(),*/
//			this.adapterBuilder.getBaseRDFUrl());

		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				model.getSpreadsheetName(), model.getUserName(), 
				model.getUserPassword(), model.getUniqueColumnNames(), 
				model.getWorksheetName(), model.getWorksheetName());
		
//		return adapterBuilder.createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfo, rdfSchema);
		return getAdapterBuilder().createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfo, model.getGSpreadsheet(), null);
	}

	@Override
	public List<IRDFSchema> fetchSchema(ISyncAdapter adapter) {
		List<IRDFSchema> schema = new ArrayList<IRDFSchema>();
		if(EktooFrame.multiModeSync && adapter instanceof CompositeSyncAdapter){
			// TODO: more code will be added here in future when gss adapter is
			// upgraded to work in Multi mode sync.
			// see the method fetchSchema(...) in MsAccessUIController
		} else {
			GoogleSpreadSheetContentAdapter contentAdapter = (GoogleSpreadSheetContentAdapter)((SplitAdapter)adapter).getContentAdapter();
			IRDFSchema rdfSchema = (IRDFSchema) contentAdapter.getSchema();
			schema.add(rdfSchema);
		}
		return schema;
	}
	
	@Override
	public ISyncAdapter createAdapter(List<IRDFSchema> schemas) {
		GSSheetModel model = (GSSheetModel) this.getModel();
		IRDFSchema rdfSchema = schemas == null || schemas.size() == 0 ? null : schemas.get(0);
		
		
		String worksheetName = model.getWorksheetName(); 
		String[] uniqueColumnNames = model.getUniqueColumnNames();

		// when user provides a new spreadsheet name in target, worksheet name and
		// uniqueColumn name is not taken from user as input rather it will be taken from source schema.
		// if source schema is not available this is ended up with an exception
		if( worksheetName == null && (uniqueColumnNames == null || uniqueColumnNames.length == 0)){
			if(rdfSchema != null) {
				worksheetName = rdfSchema.getName();
				uniqueColumnNames = (String[]) rdfSchema.getIdentifiablePropertyNames().toArray();
			} else
				throw new MeshException("Unable to create target datasource without source schema.");
		}
		
		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				model.getSpreadsheetName(), model.getUserName(), 
				model.getUserPassword(), uniqueColumnNames, worksheetName, worksheetName);
		
		return getAdapterBuilder().createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfo, rdfSchema);
	}
}
