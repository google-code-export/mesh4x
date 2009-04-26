package org.mesh4j.ektoo.controller;
import java.util.List;

import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.ektoo.ui.SyncItemUI;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.validations.Guard;

/**
 * @author Bhuiyan Mohammad Iklash
 *
 */
public class EktooUIController 
{
	// MODEL VARIABLESs
	ISyncAdapterBuilder adapterBuilder;
	
	// BUISINESS METHODS
	public EktooUIController(PropertiesProvider propertiesProvider) 
	{
		Guard.argumentNotNull(propertiesProvider, "propertiesProvider");
		this.adapterBuilder = new SyncAdapterBuilder(propertiesProvider);
	}

	public String sync(SyncItemUI source, SyncItemUI target)
	{	
		return sync(source.createAdapter(), target.createAdapter());	
	}
	
	public String  sync(ISyncAdapter sourceAdapter, ISyncAdapter targetAdapter)
	{
		SyncEngine engine = new SyncEngine(sourceAdapter, targetAdapter);
		List<Item> items = engine.synchronize();
		if (items != null && items.size() > 0)
		{
			return EktooUITranslator.getMessageSyncConflicts();
		}
		return EktooUITranslator.getMessageSyncSyccessfuly();
	}
} 
