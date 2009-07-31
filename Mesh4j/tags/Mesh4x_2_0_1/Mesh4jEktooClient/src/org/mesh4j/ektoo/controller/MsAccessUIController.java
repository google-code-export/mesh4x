package org.mesh4j.ektoo.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

	// BUSINESS METHODS
	public MsAccessUIController(PropertiesProvider propertiesProvider, boolean acceptsCreateDataset) {
		super(propertiesProvider, acceptsCreateDataset);
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
			return getAdapterBuilder().createMsAccessMultiTablesAdapter(databaseName, tableNames);
		else
			return getAdapterBuilder().createMsAccessAdapter(databaseName, tableNames[0]);		
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
			if (localSchemas == null || localSchemas.isEmpty()) {
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
			List<IRDFSchema> localSchema = this.fetchSchema(syncAdapter);
			if (localSchema == null || localSchema.size() == 0) {
				return null;
			}
			
			IRDFSchema schemaLocal = localSchema.get(0);
			IRDFSchema schemaRemote = schemas.get(0);

			if (schemaLocal != null && schemaRemote != null
					&& !schemaLocal.isCompatible(schemaRemote)) {
				return null;
			}
		}
		return syncAdapter;
	}

}
