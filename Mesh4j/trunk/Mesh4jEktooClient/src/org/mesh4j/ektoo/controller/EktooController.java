package org.mesh4j.ektoo.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.ektoo.ui.SyncItemUI;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.validations.Guard;

public class EktooController {
	
	private final static Log LOGGER = LogFactory.getLog(EktooController.class);
	
	public final static String SYNCHRONIZATION_FAILED = "failed";
	public final static String SYNCHRONIZATION_SUCCEED = "succeed";
	public final static String SYNCHRONIZATION_CONFLICTED = "conflicted";
	public final static String SYNCHRONIZATION_ERROR_CREATING_ADAPTER = "error_creating_adapter";

	// MODEL VARIABLESs
	ISyncAdapterBuilder adapterBuilder;

	// BUISINESS METHODS
	public EktooController(PropertiesProvider propertiesProvider) {
		Guard.argumentNotNull(propertiesProvider, "propertiesProvider");
		this.adapterBuilder = new SyncAdapterBuilder(propertiesProvider);
	}

	public String sync(SyncItemUI source, SyncItemUI target) {
		ISyncAdapter sourceAdapter = null;
		ISyncAdapter targetAdapter = null;
		
		sourceAdapter = source.createAdapter();
		List<IRDFSchema> schemas = source.fetchSchema(sourceAdapter);
		targetAdapter = target.createAdapter(schemas);

		if(sourceAdapter == null || targetAdapter == null){
			return SYNCHRONIZATION_ERROR_CREATING_ADAPTER;
		}
		return sync(sourceAdapter, targetAdapter);
	}

	public String sync(ISyncAdapter sourceAdapter, ISyncAdapter targetAdapter) {
		try {
			SyncEngine engine = new SyncEngine(sourceAdapter, targetAdapter);
			List<Item> items = engine.synchronize();
			if (items != null && items.size() > 0) {
				return SYNCHRONIZATION_CONFLICTED;
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return SYNCHRONIZATION_FAILED;
		}

		return SYNCHRONIZATION_SUCCEED;
	}
}
