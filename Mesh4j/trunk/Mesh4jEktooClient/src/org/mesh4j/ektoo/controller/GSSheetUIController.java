package org.mesh4j.ektoo.controller;

import org.mesh4j.ektoo.GoogleSpreadSheetInfo;
import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.model.GSSheetModel;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.validations.Guard;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */
public class GSSheetUIController extends AbstractController
{
	public static final String USER_NAME_PROPERTY = "UserName";
	public static final String USER_PASSWORD_PROPERTY = "UserPassword";
	public static final String SPREADSHEET_KEY_PROPERTY = "SpreadsheetKey";
	public static final String WORKSHEET_NAME_PROPERTY = "WorksheetName";
	public static final String UNIQUE_COLUMN_NAME_PROPERTY = "UniqueColumnName";
	public static final String UNIQUE_COLUMN_POSITION_PROPERTY = "UniqueColumnPosition";
	public static final String LASTUPDATE_COLUMN_NAME_PROPERTY = "LastUpdatedColumnName";
	public static final String LASTUPDATE_COLUMN_POSITION_PROPERTY = "LastUpdatedColumnPosition";

	// MODEL VARIABLES
	private ISyncAdapterBuilder adapterBuilder;
	
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

    GoogleSpreadSheetInfo spreadSheetInfo = 
			new GoogleSpreadSheetInfo(
				model.getSpreadsheetKey(), 
				model.getUserName(), 
				model.getUserPassword(), 
				model.getUniqueColumnName(), 
				model.getUniqueColumnPosition(), 
				model.getLastUpdatedColumnPosition(), 
				model.getWorksheetName(),
				model.getWorksheetName());
		return adapterBuilder.createGoogleSpreadSheetAdapter(spreadSheetInfo);
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
