package org.mesh4j.ektoo.controller;

import java.util.List;

import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.ektoo.ui.SyncItemUI;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.msexcel.MsExcelContentAdapter;
import org.mesh4j.sync.adapters.split.SplitAdapter;
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
		
		//right now ms-excel only supports automatic schema creation
		//some of the few adapter(googlespread adapter) doesn't but some limitation and
		//we will apply those after fix issue.
		if((selectedSourceItem.equals(SyncItemUI.MS_EXCEL_PANEL) || 
				selectedSourceItem.equals(SyncItemUI.MYSQL_PANEL))
				&& (selectedTargetItem.equals(SyncItemUI.MS_EXCEL_PANEL))){
			sourceAdapter = source.createAdapter();
			if(target.isMustCreateSchema()){
				processAutomaticSchemaCreation(sourceAdapter,target);
			}
			targetAdapter = target.createAdapter(source.fetchSchema(sourceAdapter));
		}else if ((selectedSourceItem.equals(SyncItemUI.MS_EXCEL_PANEL) || 
				selectedSourceItem.equals(SyncItemUI.GOOGLE_SPREADSHEET_PANEL))
				&& (selectedTargetItem.equals(SyncItemUI.MYSQL_PANEL) || 
					selectedTargetItem.equals(SyncItemUI.MS_ACCESS_PANEL))) {
			targetAdapter = target.createAdapter();
			sourceAdapter = source.createAdapter(target.fetchSchema(targetAdapter));

		} else if(selectedSourceItem.equals(SyncItemUI.MS_EXCEL_PANEL) || selectedSourceItem.equals(SyncItemUI.GOOGLE_SPREADSHEET_PANEL)
				|| selectedSourceItem.equals(SyncItemUI.MYSQL_PANEL) || selectedSourceItem.equals(SyncItemUI.MS_ACCESS_PANEL)
				&& selectedTargetItem.equals(SyncItemUI.CLOUD_PANEL)){ 
			sourceAdapter = source.createAdapter();
			targetAdapter = target.createAdapter(source.fetchSchema(sourceAdapter));
		} else if(selectedTargetItem.equals(SyncItemUI.MS_EXCEL_PANEL) || selectedTargetItem.equals(SyncItemUI.GOOGLE_SPREADSHEET_PANEL)
				|| selectedTargetItem.equals(SyncItemUI.MYSQL_PANEL) || selectedTargetItem.equals(SyncItemUI.MS_ACCESS_PANEL)
				&& selectedSourceItem.equals(SyncItemUI.CLOUD_PANEL)){ 
			targetAdapter = target.createAdapter();
			sourceAdapter = source.createAdapter(target.fetchSchema(targetAdapter));
		} else {
			sourceAdapter = source.createAdapter();
			targetAdapter = target.createAdapter();
		}

		return sync(sourceAdapter, targetAdapter);
	}

	
	private void processAutomaticSchemaCreation(ISyncAdapter sourceAdapter,SyncItemUI target){
	
		String idNode = "";
		String entity = "";
		String targetFilePath = "";
		SplitAdapter splitAdapter = ((SplitAdapter)sourceAdapter);
		if(splitAdapter.getContentAdapter() instanceof HibernateContentAdapter){
			idNode = ((HibernateContentAdapter)splitAdapter.getContentAdapter()).getMapping().getIDNode();
			entity = ((HibernateContentAdapter)splitAdapter.getContentAdapter()).getMapping().getEntityNode();
			targetFilePath = target.getTargetFilePath();
		} else if (splitAdapter.getContentAdapter() instanceof MsExcelContentAdapter){
			idNode = ((MsExcelContentAdapter)splitAdapter.getContentAdapter()).getMapping().getIdColumnName();
			entity = ((MsExcelContentAdapter)splitAdapter.getContentAdapter()).getSheetName();
			targetFilePath = target.getTargetFilePath();
		}
		
		if(target.getCurrentController() instanceof MsExcelUIController){
			MsExcelUIController controller = (MsExcelUIController)target.getCurrentController();
			controller.changeWorksheetName(entity);
			controller.changeUniqueColumnName(idNode);
			controller.changeWorkbookName(targetFilePath);
		}
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
