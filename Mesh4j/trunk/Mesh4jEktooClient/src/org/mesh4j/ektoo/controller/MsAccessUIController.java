package org.mesh4j.ektoo.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.model.MsAccessModel;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.ektoo.ui.EktooFrame;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.composite.CompositeSyncAdapter;
import org.mesh4j.sync.adapters.composite.IIdentifiableSyncAdapter;
import org.mesh4j.sync.adapters.composite.IdentifiableSyncAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.mapping.IHibernateToXMLMapping;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.validations.Guard;

public class MsAccessUIController extends AbstractUIController
{
	private static final Log LOGGER = LogFactory.getLog(MsAccessUIController.class);
	public static final String DATABASE_NAME_PROPERTY = "DatabaseName";
	public static final String TABLE_NAME_PROPERTY = "TableNames";

	// MODEL VARIABLEs
	private ISyncAdapterBuilder adapterBuilder;

	// BUSINESS METHODS
	public MsAccessUIController(PropertiesProvider propertiesProvider, boolean acceptsCreateDataset) {
		super(acceptsCreateDataset);
		
		Guard.argumentNotNull(propertiesProvider, "propertiesProvider");
		this.adapterBuilder = new SyncAdapterBuilder(propertiesProvider);
	}

	public void changeDatabaseName(String databaseName) {
		LOGGER.debug("MsAccessUIController>changeDatabaseName");
		setModelProperty(DATABASE_NAME_PROPERTY, databaseName);
	}

	public void changeTableNames(String[] tableNames) {
		LOGGER.debug("MsAccessUIController>changeTableName");
		setModelProperty(TABLE_NAME_PROPERTY, tableNames);
	}

	@Override
	public ISyncAdapter createAdapter() {
		MsAccessModel model = (MsAccessModel) this.getModel();
		if (model == null){
			return null;
		}

		String databaseName = model.getDatabaseName();
		if (databaseName == null || databaseName.trim().length() == 0){
			return null;
		}

//		String tableName = model.getTabletName();
//		if (tableName == null || tableName.trim().length() == 0){
//			return null;
//		}
		String[] tableNames = model.getTableNames();
		if (tableNames == null || tableNames.length == 0){
			return null;
		}		

		if (EktooFrame.multiModeSync)
			return adapterBuilder.createMsAccessMultiTablesAdapter(databaseName, tableNames);
		else
			return adapterBuilder.createMsAccessAdapter(databaseName, tableNames[0]);		
	}

	@Override
	public HashMap<IRDFSchema, String> fetchSchema(ISyncAdapter adapter) {
		HashMap<IRDFSchema, String> schema = new HashMap<IRDFSchema, String>();
		if(EktooFrame.multiModeSync && adapter instanceof CompositeSyncAdapter){
			for (IIdentifiableSyncAdapter identifiableAdapter : ((CompositeSyncAdapter) adapter).getAdapters()) {
				SplitAdapter splitAdapter = (SplitAdapter)((IdentifiableSyncAdapter)identifiableAdapter).getSyncAdapter();
				HibernateContentAdapter hibernateContentAdapter = (HibernateContentAdapter)splitAdapter.getContentAdapter();
				String id = hibernateContentAdapter.getMapping().getIDNode();
				IRDFSchema rdfSchema = (IRDFSchema)hibernateContentAdapter.getMapping().getSchema();
				schema.put(rdfSchema, id);
			}			
		} else {
			HibernateContentAdapter hibernateContentAdapter = (HibernateContentAdapter)((SplitAdapter)adapter).getContentAdapter();
			IHibernateToXMLMapping mapping = hibernateContentAdapter.getMapping();
			IRDFSchema rdfSchema = (IRDFSchema) mapping.getSchema();
			schema.put(rdfSchema, mapping.getIDNode());
		}
		return schema;
	}

	@Override
	public ISyncAdapter createAdapter(HashMap<IRDFSchema, String> schema) {
		ISyncAdapter syncAdapter = null; 
		
		if(EktooFrame.multiModeSync){
			syncAdapter = this.createAdapter();
			HashMap<IRDFSchema, String> localSchema = this.fetchSchema(syncAdapter);
			if (localSchema == null || localSchema.size() == 0) {
				return null;
			}			
			
			// the following logic has been implemented for making sure all the
			// selected source tables are available in target with compatible schema
			if(localSchema.size() != schema.size())
				Guard.throwsException("INVALID_COMBINATION_OF_SELECTION");
			
			boolean compatible = true;
			for (Map.Entry<IRDFSchema, String> remoteSchemaEntry : schema.entrySet()){
				boolean subCompatible = false;
				for (Map.Entry<IRDFSchema, String> localSchemaEntry : localSchema.entrySet()){
					if(remoteSchemaEntry.getKey().isCompatible(localSchemaEntry.getKey())){
						//found a match
						localSchema.remove(localSchemaEntry);
						subCompatible = true;
						break;
					}	
				}
				if(subCompatible){
					continue;
				}else{
					compatible = false;
					Guard.throwsException("INCOMPATIBLE_RDF_SCHEMA");
					//break;
				}
			}
			return compatible ? syncAdapter : null;			
		} else {
			syncAdapter = this.createAdapter();
			HashMap<IRDFSchema, String> localSchema = this.fetchSchema(syncAdapter);
			if (localSchema == null || localSchema.size() == 0) {
				return null;
			}
			
			IRDFSchema schemaLocal = localSchema.entrySet().iterator().next().getKey();
			IRDFSchema schemaRemote = schema.entrySet().iterator().next().getKey();

			if (schemaLocal != null && schemaRemote != null
					&& !schemaLocal.isCompatible(schemaRemote)) {
				return null;
			}
		}
		return syncAdapter;
	}

}
