package org.mesh4j.ektoo.controller;

import org.mesh4j.ektoo.GoogleSpreadSheetInfo;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.UISchema;
import org.mesh4j.ektoo.model.GSSheetModel;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadSheetContentAdapter;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadsheet;
import org.mesh4j.sync.adapters.googlespreadsheet.IGoogleSpreadSheet;
import org.mesh4j.sync.adapters.googlespreadsheet.mapping.GoogleSpreadsheetToRDFMapping;
import org.mesh4j.sync.adapters.googlespreadsheet.mapping.IGoogleSpreadsheetToXMLMapping;
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
		IRDFSchema rdfSchema = GoogleSpreadsheetToRDFMapping.extractRDFSchema(gss, model.getWorksheetName(), this.adapterBuilder.getBaseRDFUrl());

		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				model.getSpreadsheetName(), model.getUserName(), 
				model.getUserPassword(), model.getUniqueColumnName(), 
				model.getWorksheetName(), model.getWorksheetName());

		return adapterBuilder.createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfo, rdfSchema);
	}

	@Override
	public UISchema fetchSchema(ISyncAdapter adapter) {
		GoogleSpreadSheetContentAdapter contentAdapter = (GoogleSpreadSheetContentAdapter)((SplitAdapter)adapter).getContentAdapter();
		IRDFSchema rdfSchema = (IRDFSchema) contentAdapter.getSchema();
		IGoogleSpreadsheetToXMLMapping mapper = contentAdapter.getMapper();
		return new UISchema(rdfSchema, mapper.getIdColumnName());
	}

	@Override
	public ISyncAdapter createAdapter(UISchema schema) {
		GSSheetModel model = (GSSheetModel) this.getModel();

		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				model.getSpreadsheetName(), model.getUserName(), 
				model.getUserPassword(), model.getUniqueColumnName(), 
				model.getWorksheetName(), model.getWorksheetName());
		
		IRDFSchema rdfSchema = schema == null ? null : schema.getRDFSchema();
		return adapterBuilder.createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfo, rdfSchema);
	}
}
