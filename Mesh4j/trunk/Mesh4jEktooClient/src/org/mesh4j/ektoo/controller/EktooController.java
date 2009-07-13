package org.mesh4j.ektoo.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.Event;
import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.model.CloudModel;
import org.mesh4j.ektoo.model.MsAccessModel;
import org.mesh4j.ektoo.model.MySQLAdapterModel;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.ektoo.ui.EktooFrame;
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

	public String sync(EktooFrame ui, SyncItemUI source, SyncItemUI target, Date since) {
		
		if(EktooFrame.multiModeSync && target.isCloudUI()){
			if(ui.syncProcessUI.isVisible()){
				ui.syncProcessUI.toFront();
			} else {
				ui.syncProcessUI.pack();
				ui.syncProcessUI.setVisible(true);
			}
			
			CloudModel cloudModel = (CloudModel)target.getCurrentController().getModel();
			
			if(source.isMsAccessUI()){
				MsAccessModel msAccessModel = (MsAccessModel)source.getCurrentController().getModel();
				ui.syncProcessUI.synchronizeMsAccessVsCloud(
					since,
					msAccessModel.getDatabaseName(), 
					new TreeSet<String>(Arrays.asList(msAccessModel.getTableNames())), 
					cloudModel.getBaseUri(), 
					cloudModel.getMeshName(), 
					adapterBuilder.getIdentityProvider(), 
					adapterBuilder.getBaseDirectory());
			} else {
				MySQLAdapterModel mySqlModel = (MySQLAdapterModel)source.getCurrentController().getModel();
				ui.syncProcessUI.synchronizeMySqlVsCloud(
					since,
					mySqlModel.getUserName(),
					mySqlModel.getUserPassword(),
					mySqlModel.getHostName(),
					mySqlModel.getPortNo(),	
					mySqlModel.getDatabaseName(),
					new TreeSet<String>(Arrays.asList(mySqlModel.getTableNames())), 
					cloudModel.getBaseUri(), 
					cloudModel.getMeshName(), 
					adapterBuilder.getIdentityProvider(), 
					adapterBuilder.getBaseDirectory());
			}
			
			if(ui.syncProcessUI.getStatus().isError()){
				return SYNCHRONIZATION_FAILED;
			} else if(ui.syncProcessUI.getStatus().isFailed()){
				return SYNCHRONIZATION_CONFLICTED;
			} else {
				return SYNCHRONIZATION_SUCCEED;
			}
		} else {
			ISyncAdapter sourceAdapter = null;
			ISyncAdapter targetAdapter = null;
			
			source.getCurrentController().setCurrentEvent(Event.sync_event);
			target.getCurrentController().setCurrentEvent(Event.sync_event);
			
			sourceAdapter = source.createAdapter();
			List<IRDFSchema> schemas = source.fetchSchema(sourceAdapter);
			targetAdapter = target.createAdapter(schemas);

			if(sourceAdapter == null || targetAdapter == null){
				return SYNCHRONIZATION_ERROR_CREATING_ADAPTER;
			}
			return sync(sourceAdapter, targetAdapter, since);
		}
	}

	public String sync(ISyncAdapter sourceAdapter, ISyncAdapter targetAdapter, Date since) {
		try {
			SyncEngine engine = new SyncEngine(sourceAdapter, targetAdapter);
			List<Item> items = engine.synchronize(since);
			if (items != null && items.size() > 0) {
				return SYNCHRONIZATION_CONFLICTED;
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			//TODO: need to add a popup message here
			return SYNCHRONIZATION_FAILED;
		}

		return SYNCHRONIZATION_SUCCEED;
	}
}
