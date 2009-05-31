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
public class EktooController 
{
  public final static String SYNCHRONIZATION_FAILED    = "failed";
  public final static String SYNCHRONIZATION_SUCCEED   = "succeed";
  public final static String SYNCHRONIZATION_CONFLICTED= "conflicted";
  

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

		String selectedSourceItem = (String) source.getListType().getSelectedItem();
		String selectedTargetItem = (String) target.getListType().getSelectedItem();
		
		if ((selectedSourceItem.equals(SyncItemUI.MS_EXCEL_PANEL) || selectedSourceItem.equals(SyncItemUI.GOOGLE_SPREADSHEET_PANEL)
				&& (selectedTargetItem
						.equals(SyncItemUI.MYSQL_PANEL) || 
					selectedTargetItem
						.equals(SyncItemUI.MS_ACCESS_PANEL)))) {
			targetAdapter = target.createAdapter();
			sourceAdapter = source.createAdapter(target.fetchSchema(targetAdapter));

		} else {
			if ((selectedTargetItem.equals(SyncItemUI.MS_EXCEL_PANEL) || selectedTargetItem.equals(SyncItemUI.GOOGLE_SPREADSHEET_PANEL))
					&& (selectedSourceItem
							.equals(SyncItemUI.MYSQL_PANEL) || 
						selectedSourceItem
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
