package org.mesh4j.ektoo.tasks;

import java.awt.Cursor;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.controller.EktooUIController;
import org.mesh4j.ektoo.ui.EktooUI;
import org.mesh4j.ektoo.ui.SyncItemUI;
import org.mesh4j.ektoo.ui.component.statusbar.Statusbar;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;

public class SynchronizeTask extends SwingWorker<String, Void> {

	private final static Log LOGGER = LogFactory.getLog(SynchronizeTask.class);
	
	// MODEL VARIABLEs
	private EktooUI ui;
	private String result = null;
	private ISynchronizeTaskListener synchronizeTaskListener = null;

	// BUSINESS METHODS
	public SynchronizeTask(EktooUI ui, ISynchronizeTaskListener synchronizeTaskListener) {
		super();
		this.ui = ui;
		this.synchronizeTaskListener = synchronizeTaskListener;
	}

	@Override
	public String doInBackground() 
	{
		ui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		ui.setStatusbarText(EktooUITranslator.getMessageStartSync(
		    ui.getSourceItem().toString(),
		    ui.getTargetItem().toString(),
		    new Date()
		    ), Statusbar.NORMAL_STATUS);
		
		try 
		{
			SyncItemUI sourceItem = ui.getSourceItem();
			SyncItemUI targetItem = ui.getTargetItem();
			result = ui.getController().sync(sourceItem, targetItem);
			
			return result;
		}
		catch (Throwable t) 
		{
			LOGGER.error(t.getMessage(), t);
			synchronizeTaskListener.notifySynchronizeTaskError(t.getMessage());
		}
		return null;
	}

	@Override
	public void done() 
	{
	  try 
		{
			result = get();
			if (result != null)
			{
			  if ( result.equals(EktooUIController.SYNCHRONIZATION_SUCCEED))
			  {
			    synchronizeTaskListener.notifySynchronizeTaskSuccess(
	            EktooUITranslator.getMessageSyncSyccessfuly(
	            ui.getSourceItem().toString(),
	            ui.getTargetItem().toString(),
	            new Date()));
			  }
			  else if(result.equals(EktooUIController.SYNCHRONIZATION_CONFLICTED) ) 
			  {
			    synchronizeTaskListener.notifySynchronizeTaskConflict(EktooUITranslator.getMessageSyncConflicts(
	            ui.getSourceItem().toString(),
	            ui.getTargetItem().toString(),
	            new Date()
	            ));   
			  }
			  else if ( result.equals(EktooUIController.SYNCHRONIZATION_FAILED) ) 
			  {
			    synchronizeTaskListener.notifySynchronizeTaskError(EktooUITranslator.getMessageSyncFailed(
	            ui.getSourceItem().toString(),
	            ui.getTargetItem().toString(),
	            new Date()
	            )); 
			  }
			}
			else
			{
			  synchronizeTaskListener.notifySynchronizeTaskError(EktooUITranslator.getMessageSyncFailed(
	          ui.getSourceItem().toString(),
	          ui.getTargetItem().toString(),
	          new Date()
	          ));			  
			}
		} 
		catch (InterruptedException e) 
		{
		  synchronizeTaskListener.notifySynchronizeTaskError(EktooUITranslator.getMessageSyncFailed(
          ui.getSourceItem().toString(),
          ui.getTargetItem().toString(),
          new Date()
          ));
		  
		  	  
			LOGGER.error(e.getMessage(), e);
		} 
		catch (ExecutionException e) 
		{
		  synchronizeTaskListener.notifySynchronizeTaskError(EktooUITranslator.getMessageSyncFailed(
          ui.getSourceItem().toString(),
          ui.getTargetItem().toString(),
          new Date()
          ));		  
			LOGGER.error(e.getMessage(), e);
		}
		ui.showSyncImageLabel(false);
		ui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
}
