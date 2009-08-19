package org.mesh4j.ektoo.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mesh4j.ektoo.model.MySQLAdapterModel;
import org.mesh4j.ektoo.ui.EktooFrame;
import org.mesh4j.ektoo.ui.settings.AppProperties;
import org.mesh4j.ektoo.ui.settings.prop.AppPropertiesProvider;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.composite.CompositeSyncAdapter;
import org.mesh4j.sync.adapters.composite.IIdentifiableSyncAdapter;
import org.mesh4j.sync.adapters.composite.IdentifiableSyncAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.mapping.IHibernateToXMLMapping;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.validations.Guard;

public class MySQLUIController extends AbstractUIController
{	
	public static final String USER_NAME_PROPERTY = "UserName";
	public static final String USER_PASSWORD_PROPERTY = "UserPassword";
	public static final String HOST_NAME_PROPERTY = "HostName";
	public static final String PORT_NO_PROPERTY = "PortNo";
	public static final String DATABASE_NAME_PROPERTY = "DatabaseName";
	public static final String TABLE_NAME_PROPERTY = "TableNames";

	// BUSINESS METHODS
	public MySQLUIController( boolean acceptsCreateDataset) {
		super( acceptsCreateDataset);
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

	public void changeTableNames(String[] tableNames) {
		setModelProperty(TABLE_NAME_PROPERTY, tableNames);
	}

	@Override
	public ISyncAdapter createAdapter() 
	{
		MySQLAdapterModel model = (MySQLAdapterModel) this.getModel();
		if (model == null){
			return null;
		}

		String userName = model.getUserName();
		if (userName == null){
			return null;
		}

		String userPassword = model.getUserPassword();

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

		String[] tableNames = model.getTableNames();
		if (tableNames == null || tableNames.length == 0){
			return null;
		}		
		
		if (EktooFrame.multiModeSync)
			return getAdapterBuilder().createMySQLAdapterForMultiTables(userName,
					userPassword, hostName, portNo, databaseName, tableNames);
		else
			return getAdapterBuilder().createMySQLAdapter(userName, userPassword,
					hostName, portNo, databaseName, tableNames[0]);
	}

	@Override
	public List<IRDFSchema> fetchSchema(ISyncAdapter adapter) {
		ArrayList<IRDFSchema> schema = new ArrayList<IRDFSchema>();
		if(EktooFrame.multiModeSync && adapter instanceof CompositeSyncAdapter){
			for (IIdentifiableSyncAdapter identifiableAdapter : ((CompositeSyncAdapter) adapter).getAdapters()) {
				SplitAdapter splitAdapter = (SplitAdapter)((IdentifiableSyncAdapter)identifiableAdapter).getSyncAdapter();
				HibernateContentAdapter hibernateContentAdapter = (HibernateContentAdapter)splitAdapter.getContentAdapter();

				IRDFSchema rdfSchema = (IRDFSchema)hibernateContentAdapter.getMapping().getSchema();
				schema.add(rdfSchema);
			}			
		} else {
			HibernateContentAdapter hibernateContentAdapter = (HibernateContentAdapter)((SplitAdapter)adapter).getContentAdapter();
			IHibernateToXMLMapping mapping = hibernateContentAdapter.getMapping();
			IRDFSchema rdfSchema = (IRDFSchema) mapping.getSchema();
			schema.add(rdfSchema);
		}
		return schema;
	}	
	
	@Override
	public ISyncAdapter createAdapter(List<IRDFSchema> schemas) {
		ISyncAdapter syncAdapter = null; 
		
		if(EktooFrame.multiModeSync){
		
			syncAdapter = this.createAdapter();
			List<IRDFSchema> localSchemas = this.fetchSchema(syncAdapter);
			if (localSchemas == null || localSchemas.size() == 0) {
				return null;
			}			
			
			// the following logic has been implemented for making sure all the
			// selected source tables are available in target with compatible schema
			if(localSchemas.size() != schemas.size())
				Guard.throwsException("INVALID_COMBINATION_OF_SELECTION");

			boolean compatible = true;
			for (IRDFSchema remoteSchema : schemas){
				boolean subCompatible = false;
				
				IRDFSchema localSchema = null;
				Iterator<IRDFSchema> it = localSchemas.iterator();
				while(it.hasNext()){
					localSchema = it.next();
					if(remoteSchema.isCompatible(localSchema)){
						//found a match
						subCompatible = true;
						break;
					}	
				}
				if(!subCompatible){
					Guard.throwsException("INCOMPATIBLE_RDF_SCHEMA");
				}else{
					localSchemas.remove(localSchema);
				}
			}
			return compatible ? syncAdapter : null;
			
		} else {
			syncAdapter = this.createAdapter();
			List<IRDFSchema> localSchemas = this.fetchSchema(syncAdapter);
			if (localSchemas == null || localSchemas.size() == 0) {
				return null;
			}
			
			IRDFSchema schemaLocal = localSchemas.get(0);
			IRDFSchema schemaRemote = schemas.get(0);

			if (schemaLocal != null && schemaRemote != null
					&& !schemaLocal.isCompatible(schemaRemote)) {
				return null;
			}
		}
		return syncAdapter;
	}	

	// PROPERTIES
	public String getDefaultMySQLHost() {
		return AppPropertiesProvider.getPropetyManager().
		getPropertyAsDecrepted(AppProperties.HOST_NAME_MYSQL_DEFAULT);
//		return getPropertiesProvider().getDefaultMySQLHost();
	}

	public String getDefaultMySQLPort() {
		return AppPropertiesProvider.getPropetyManager().
		getPropertyAsDecrepted(AppProperties.PORT_MYSQL_DEFAULT);
//		return getPropertiesProvider().getDefaultMySQLPort();
	}

	public String getDefaultMySQLSchema() {
		return AppPropertiesProvider.getPropetyManager().
		getPropertyAsDecrepted(AppProperties.DATABASE_NAME_MYSQL_DEFAULT);
		
//		return getPropertiesProvider().getDefaultMySQLSchema();
	}

	public String getDefaultMySQLUser() {
		return AppPropertiesProvider.getPropetyManager().
		getProperty(AppProperties.USER_NAME_MYSQL_DEFAULT);
		
//		return getPropertiesProvider().getDefaultMySQLUser();
	}

	public String getDefaultMySQLPassword() {
		return AppPropertiesProvider.getPropetyManager().
		getPropertyAsDecrepted(AppProperties.USER_PASSWORD_MYSQL_DEFAULT);
		
//		return getPropertiesProvider().getDefaultMySQLPassword();
	}

	public String getDefaultMySQLTable() {
		
		return AppPropertiesProvider.getPropetyManager().
		getProperty(AppProperties.TABLE_NAME_MYSQL_DEFAULT);
		
//		return getPropertiesProvider().getDefaultMySQLTable();
	}

	public String generateFeed() {
		MySQLAdapterModel model = (MySQLAdapterModel) this.getModel();
		if (model == null){
			return null;
		}

		String userName = model.getUserName();
		if (userName == null){
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

		String[] tableNames = model.getTableNames();
		if (tableNames == null || tableNames.length == 0){
			tableNames = new String[]{""};
		}
		
		String userPassword = model.getUserPassword();
		if (userPassword == null){
			userPassword = "";
		}
		
		return getAdapterBuilder().generateMySqlFeed(userName, userPassword, hostName, portNo, databaseName, tableNames[0]);
	}
}
