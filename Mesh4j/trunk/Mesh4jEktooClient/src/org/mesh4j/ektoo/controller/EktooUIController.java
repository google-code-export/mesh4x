package org.mesh4j.ektoo.controller;
import java.io.File;
import java.util.List;

import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.validations.Guard;

/**
 * @author Asus
 *
 */
public class EktooUIController {
	
	// MODEL VARIABLES
	ISyncAdapterBuilder adapterBuilder;
	
	// BUISINESS METHODS
	
	public EktooUIController(PropertiesProvider propertiesProvider) {
		Guard.argumentNotNull(propertiesProvider, "propertiesProvider");
		this.adapterBuilder = new SyncAdapterBuilder(propertiesProvider);
	}
	
	public String sync(File sourceFile, String sourceWorksheet, String sourceWorksheeColumn,
					   File targetFile, String targetWorksheet, String targetWorksheeColumn)
	{
		
		ISyncAdapter sourceAdapter = this.adapterBuilder.createMsExcelAdapter(sourceWorksheet, sourceWorksheeColumn, sourceFile.getAbsolutePath());
		ISyncAdapter targetAdapter = this.adapterBuilder.createMsExcelAdapter(targetWorksheet, targetWorksheeColumn, targetFile.getAbsolutePath());

		SyncEngine engine = new SyncEngine(sourceAdapter, targetAdapter);
		List<Item> items = engine.synchronize();
		
		if (items != null && items.size() > 0){
			return EktooUITranslator.getMessageConflicts();
		} else {
			return EktooUITranslator.getMessageSyncSyccessfuly();
		}
		
	}

	
} 
