package org.mesh4j.ektoo.controller;

import java.beans.PropertyChangeEvent;

import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.IUIController;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.model.MySQLAdapterModel;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.validations.Guard;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */
public class MySQLUIController extends AbstractController implements
		IUIController {
	
	private static final String USER_NAME_PROPERTY = "UserName";
	private static final String USER_PASSWORD_PROPERTY = "UserPassword";
	private static final String HOST_NAME_PROPERTY = "HostName";
	private static final String PORT_NO_PROPERTY = "PortNo";
	private static final String DATABASE_NAME_PROPERTY = "DatabaseName";
	private static final String TABLE_NAME_PROPERTY = "TableName";

	// MODEL VARIABLES
	private ISyncAdapterBuilder adapterBuilder;
	private PropertiesProvider propertiesProvider;

	// BUSINESS METHODS
	public MySQLUIController(PropertiesProvider propertiesProvider) {
		Guard.argumentNotNull(propertiesProvider, "propertiesProvider");
		this.adapterBuilder = new SyncAdapterBuilder(propertiesProvider);
		this.propertiesProvider = propertiesProvider;
	}

	public void changeUserName(String userName) {
		setModelProperty(USER_NAME_PROPERTY, userName);
	}

	public void changeUserPassword(String userPassword) {
		setModelProperty(USER_PASSWORD_PROPERTY, userPassword);
	}

	public void changeHostName(String hostName) {
		setModelProperty(HOST_NAME_PROPERTY, hostName);
	}

	public void changePortNo(int portNo) {
		setModelProperty(PORT_NO_PROPERTY, portNo);
	}

	public void changeDatabaseName(String databaseName) {
		setModelProperty(DATABASE_NAME_PROPERTY, databaseName);
	}

	public void changeTableName(String tableName) {
		setModelProperty(TABLE_NAME_PROPERTY, tableName);
	}

	@Override
	public ISyncAdapter createAdapter() {
		MySQLAdapterModel model = (MySQLAdapterModel) this.getModel();
		if (model == null){
			return null;
		}

		String userName = model.getUserName();
		if (userName == null){
			return null;
		}

		String userPassword = model.getUserPassword();
		if (userPassword == null){
			return null;
		}

		String hostName = model.getHostName();
		if (hostName == null){
			return null;
		}

		int portNo = model.getPortNo();
		if (portNo < 0){
			return null;
		}

		String databaseName = model.getDatabaseName();
		if (databaseName == null){
			return null;
		}

		String tableName = model.getTableName();
		if (tableName == null){
			return null;
		}

		return adapterBuilder.createMySQLAdapter(userName, userPassword,
				hostName, portNo, databaseName, tableName);
	}

	@Override
	// TODO (NBL) improve this signature
	public IRDFSchema createSchema() {
		ISyncAdapter mysqlAdapter = createAdapter();
		SplitAdapter splitAdapter = (SplitAdapter) mysqlAdapter;
		ISchema sourceSchema = ((HibernateContentAdapter) splitAdapter
				.getContentAdapter()).getMapping().getSchema();
		return (IRDFSchema) sourceSchema;
	}

	@Override
	public ISyncAdapter createAdapter(IRDFSchema schema) {
		// TODO create Adapter
		return null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO property Change
	}

	// PROPERTIES
	public String getDefaultMySQLHost() {
		return this.propertiesProvider.getDefaultMySQLHost();
	}

	public String getDefaultMySQLPort() {
		return this.propertiesProvider.getDefaultMySQLPort();
	}

	public String getDefaultMySQLSchema() {
		return this.propertiesProvider.getDefaultMySQLSchema();
	}

	public String getDefaultMySQLUser() {
		return this.propertiesProvider.getDefaultMySQLUser();
	}

	public String getDefaultMySQLPassword() {
		return this.propertiesProvider.getDefaultMySQLPassword();
	}

}
