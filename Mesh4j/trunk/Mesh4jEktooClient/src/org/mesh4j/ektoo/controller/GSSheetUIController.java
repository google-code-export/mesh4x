package org.mesh4j.ektoo.controller;

import org.mesh4j.ektoo.GoogleSpreadSheetInfo;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.model.GSSheetModel;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadSheetContentAdapter;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadsheet;
import org.mesh4j.sync.adapters.googlespreadsheet.IGoogleSpreadSheet;
import org.mesh4j.sync.adapters.googlespreadsheet.mapping.GoogleSpreadsheetToRDFMapping;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.validations.Guard;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */
public class GSSheetUIController extends AbstractController {
	public static final String USER_NAME_PROPERTY = "UserName";
	public static final String USER_PASSWORD_PROPERTY = "UserPassword";
	public static final String SPREADSHEET_KEY_PROPERTY = "SpreadsheetKey";
	public static final String WORKSHEET_NAME_PROPERTY = "WorksheetName";
	public static final String UNIQUE_COLUMN_NAME_PROPERTY = "UniqueColumnName";
	public static final String UNIQUE_COLUMN_POSITION_PROPERTY = "UniqueColumnPosition";
	public static final String LASTUPDATE_COLUMN_NAME_PROPERTY = "LastUpdatedColumnName";
	public static final String LASTUPDATE_COLUMN_POSITION_PROPERTY = "LastUpdatedColumnPosition";

	// MODEL VARIABLES
	private SyncAdapterBuilder adapterBuilder;

	// BUSINESS METHODS
	public GSSheetUIController(PropertiesProvider propertiesProvider) {
		Guard.argumentNotNull(propertiesProvider, "propertiesProvider");
		this.adapterBuilder = new SyncAdapterBuilder(propertiesProvider);
	}

	public void changeUserName(String userName) {
		setModelProperty(USER_NAME_PROPERTY, userName);
	}

	public void changeUserPassword(String userPassword) {
		setModelProperty(USER_PASSWORD_PROPERTY, userPassword);
	}

	public void changeSpreadsheetKey(String spreadsheetKey) {
		setModelProperty(SPREADSHEET_KEY_PROPERTY, spreadsheetKey);
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
		IGoogleSpreadSheet gss = new GoogleSpreadsheet(model.getSpreadsheetKey(), model.getUserName(), model.getUserPassword());
		IRDFSchema rdfSchema = GoogleSpreadsheetToRDFMapping.extractRDFSchema(gss, model.getWorksheetName(), this.adapterBuilder.getBaseRDFUrl());
		return createAdapter(rdfSchema);
	}

	@Override
	public IRDFSchema fetchSchema(ISyncAdapter adapter) {
		return (IRDFSchema)((GoogleSpreadSheetContentAdapter)((SplitAdapter)adapter).getContentAdapter()).getSchema();
	}

	@Override
	public ISyncAdapter createAdapter(IRDFSchema schema) {
		GSSheetModel model = (GSSheetModel) this.getModel();

		GoogleSpreadSheetInfo spreadSheetInfo = new GoogleSpreadSheetInfo(
				model.getSpreadsheetKey(), model.getUserName(), 
				model.getUserPassword(), model.getUniqueColumnName(), 
				model.getWorksheetName(), model.getWorksheetName());
		return adapterBuilder.createRdfBasedGoogleSpreadSheetAdapter(spreadSheetInfo, schema);
	}
}
