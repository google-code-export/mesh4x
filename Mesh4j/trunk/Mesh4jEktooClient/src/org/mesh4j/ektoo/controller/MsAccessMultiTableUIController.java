package org.mesh4j.ektoo.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.UISchema;
import org.mesh4j.ektoo.model.MsAccessMultiTableModel;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.validations.Guard;

public class MsAccessMultiTableUIController extends AbstractUIController
{
	private static final Log LOGGER = LogFactory.getLog(MsAccessMultiTableUIController.class);
	public static final String DATABASE_NAME_PROPERTY = "DatabaseName";
	public static final String TABLE_NAMES_PROPERTY = "TableNames";

	// MODEL VARIABLEs
	private ISyncAdapterBuilder adapterBuilder;

	// BUSINESS METHODS
	public MsAccessMultiTableUIController(PropertiesProvider propertiesProvider, boolean acceptsCreateDataset) {
		super(acceptsCreateDataset);
		
		Guard.argumentNotNull(propertiesProvider, "propertiesProvider");
		this.adapterBuilder = new SyncAdapterBuilder(propertiesProvider);
	}

	public void changeDatabaseName(String databaseName) {
		LOGGER.debug("MsAccessUIController>changeDatabaseName");
		setModelProperty(DATABASE_NAME_PROPERTY, databaseName);
	}

	public void changeTableNames(Object[] tableNames) {
		LOGGER.debug("MsAccessUIController>changeTableName");
		setModelProperty(TABLE_NAMES_PROPERTY, tableNames);
	}

	@Override
	public ISyncAdapter createAdapter() {
		MsAccessMultiTableModel model = (MsAccessMultiTableModel) this.getModel();
		if (model == null){
			return null;
		}

		String databaseName = model.getDatabaseName();
		if (databaseName == null || databaseName.trim().length() == 0){
			return null;
		}

		Object[] tableNames = model.getTabletNames();
		if (tableNames == null || tableNames.length == 0){
			return null;
		}

		return adapterBuilder.createMsAccessMultiTablesAdapter(databaseName, tableNames);
	}


	@Override
	public UISchema fetchSchema(ISyncAdapter adapter) {
		return null;
	}

	@Override
	public ISyncAdapter createAdapter(UISchema schema) {
		return null;
	}

}
