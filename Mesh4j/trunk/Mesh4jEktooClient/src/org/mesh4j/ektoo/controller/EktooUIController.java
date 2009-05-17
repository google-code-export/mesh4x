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
  public final static String SYNCHRONIZATION_FAILED    = "failed";
  public final static String SYNCHRONIZATION_SUCCEED   = "succeed";
  public final static String SYNCHRONIZATION_CONFLICTED= "conflicted";
  

  // MODEL VARIABLESs
	ISyncAdapterBuilder adapterBuilder;

	// BUISINESS METHODS
	public EktooUIController(PropertiesProvider propertiesProvider) {
		Guard.argumentNotNull(propertiesProvider, "propertiesProvider");
		this.adapterBuilder = new SyncAdapterBuilder(propertiesProvider);
	}

	public String sync(SyncItemUI source, SyncItemUI target) {
		ISyncAdapter sourceAdapter = null;
		ISyncAdapter targetAdapter = null;

		if (((String) source.getListType().getSelectedItem())
				.equals(SyncItemUI.MS_EXCEL_PANEL)
				&& (((String) target.getListType().getSelectedItem())
						.equals(SyncItemUI.MYSQL_PANEL) || ((String) target
						.getListType().getSelectedItem())
						.equals(SyncItemUI.MS_ACCESS_PANEL))) {
			targetAdapter = target.createAdapter();
			sourceAdapter = source.createAdapter(target.fetchSchema(targetAdapter));

		} else {
			if (((String) target.getListType().getSelectedItem())
					.equals(SyncItemUI.MS_EXCEL_PANEL)
					&& (((String) source.getListType().getSelectedItem())
							.equals(SyncItemUI.MYSQL_PANEL) || ((String) source
							.getListType().getSelectedItem())
							.equals(SyncItemUI.MS_ACCESS_PANEL))) {
				sourceAdapter = source.createAdapter();
				targetAdapter = target.createAdapter(source.fetchSchema(sourceAdapter));
			} else {
				sourceAdapter = source.createAdapter();
				targetAdapter = target.createAdapter();
			}
		}

		return sync(sourceAdapter, targetAdapter);
	}

	public String sync(ISyncAdapter sourceAdapter, ISyncAdapter targetAdapter) 
	{
	  try
    {
	    SyncEngine engine = new SyncEngine(sourceAdapter, targetAdapter);
	    List<Item> items = engine.synchronize();
	    if (items != null && items.size() > 0)
	    {
	      return SYNCHRONIZATION_CONFLICTED;
	    }
    }
    catch (Exception e)
    {
      return SYNCHRONIZATION_FAILED;
    }

		return SYNCHRONIZATION_SUCCEED;
	}
}
