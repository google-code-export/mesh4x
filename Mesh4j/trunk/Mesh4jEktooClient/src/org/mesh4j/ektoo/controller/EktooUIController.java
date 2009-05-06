package org.mesh4j.ektoo.controller;

import java.util.List;

import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.ektoo.ui.SyncItemUI;
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

	public boolean sync(SyncItemUI source, SyncItemUI target) 
	{
		ISyncAdapter sourceAdapter = source.createAdapter();
		ISyncAdapter targetAdapter = target.createAdapter();

		// TODO (NBL) make it generic
		if (targetAdapter == null) 
		{
			targetAdapter = target.createAdapter(source.createSchema());
		}
		return sync(sourceAdapter, targetAdapter);
	}

	public boolean sync(ISyncAdapter sourceAdapter, ISyncAdapter targetAdapter) 
	{
		SyncEngine engine = new SyncEngine(sourceAdapter, targetAdapter);
		List<Item> items = engine.synchronize();
		if (items != null && items.size() > 0) 
		  return false;
		
		return true;
	}
}
