package org.mesh4j.ektoo.controller;

import java.util.ArrayList;
import java.util.List;

import org.mesh4j.ektoo.GoogleSpreadSheetInfo;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.model.GSSheetModel;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.ektoo.ui.EktooFrame;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.composite.CompositeSyncAdapter;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadSheetContentAdapter;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadsheet;
import org.mesh4j.sync.adapters.googlespreadsheet.IGoogleSpreadSheet;
import org.mesh4j.sync.adapters.googlespreadsheet.mapping.GoogleSpreadsheetToRDFMapping;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.validations.Guard;

public class GSSheetUIController extends AbstractUIController {
	
	public static final String USER_NAME_PROPERTY = "UserName";
	public static final String USER_PASSWORD_PROPERTY = "UserPassword";
	public static final String SPREADSHEET_NAME_PROPERTY = "SpreadsheetName";
	public static final String WORKSHEET_NAME_PROPERTY = "WorksheetName";
	public static final String UNIQUE_COLUMN_NAME_PROPERTY = "UniqueColumnName";
	public static final String UNIQUE_COLUMN_POSITION_PROPERTY = "UniqueColumnPosition";
	public static final String LASTUPDATE_COLUMN_NAME_PROPERTY = "LastUpdatedColumnName";
	public static final String LASTUPDATE_COLUMN_POSITION_PROPERTY = "LastUpdatedColumnPosition";

	// MODEL VARIABLES
	private SyncAdapterBuilder adapterBuilder;

	// BUSINESS METHODS
	public GSSheetUIController(PropertiesProvider propertiesProvider, boolean acceptsCreateDataset) {
		super(acceptsCreateDataset);
		
		Guard.argumentNotNull(propertiesProvider, "propertiesProvider");
		this.adapterBuilder = new SyncAdapterBuilder(propertiesProvider);
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

	public void changeUniqueColumnName(String uniqueColumnName) {
		setModelProperty(UNIQUE_COLUMN_NAME_PROPERTY, uniqueColumnName);
	}

	public void changeUniqueColumnPosition(int uniqueColumnPosition) {
		setModelProperty(UNIQUE_COLUMN_POSITION_PROPERTY, uniqueColumnPosition);
	}

	public void changeLastUpdatedColumnName(String lastUpdatedColumnName) {
		setModelProperty(LASTUPDATE_COLUMN_NAME_PROPERTY, lastUpdatedColumnName);
	}

	public void changeLastUpdatedColumnPosition(int lastUpdatedColumnPosition) {
		setModelProperty(LASTUPDATE_COLUMN_POSITION_PROPERTY,
				lastUpdatedColumnPosition);
	}

	@Override
	public ISyncAdapter createAdapter() {
		GSSheetModel model = (GSSheetModel) this.getModel();
		IGoogleSpreadSheet gss = new GoogleSpreadsheet(model.getSpreadsheetName(), model.getUserName(), model.getUserPassword());

		ArrayList<String> pks = new ArrayList<String>();
		pks.add(model.getUniqueColumnName());
		
		IRDFSchema rdfSchema = GoogleSpreadsheetToRDFMapping.extractRDFSchema(
			gss, 
			model.getWorksheetName(),
			pks, 
			model.getLastUpdatedColumnName(),
			this.adapterBuilder.getBaseRDFUrl());

		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				model.getSpreadsheetName(), model.getUserName(), 
				model.getUserPassword(), model.getUniqueColumnName(), 
				model.getWorksheetName(), model.getWorksheetName());

		return adapterBuilder.createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfo, rdfSchema);
	}

	@Override
	public List<IRDFSchema> fetchSchema(ISyncAdapter adapter) {
		List<IRDFSchema> schema = new ArrayList<IRDFSchema>();
		if(EktooFrame.multiModeSync && adapter instanceof CompositeSyncAdapter){
			// TODO (SHARIF/RAJU) ????
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

		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				model.getSpreadsheetName(), model.getUserName(), 
				model.getUserPassword(), model.getUniqueColumnName(), 
				model.getWorksheetName(), model.getWorksheetName());
		
		IRDFSchema rdfSchema = schemas == null || schemas.size() == 0 ? null : schemas.get(0);
		return adapterBuilder.createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfo, rdfSchema);
	}
}
