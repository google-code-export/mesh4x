package org.mesh4j.ektoo.tasks;

import java.awt.Cursor;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.ui.EktooUI;
import org.mesh4j.ektoo.ui.SyncItemUI;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;

public class SynchronizeTask extends SwingWorker<String, Void> {

	private final static Log LOGGER = LogFactory.getLog(SynchronizeTask.class);
	
	// MODEL VARIABLEs
	private EktooUI ui;
	private String result = null;

	// BUSINESS METHODS
	public SynchronizeTask(EktooUI ui) {
		super();
		this.ui = ui;
	}

	@Override
	public String doInBackground() 
	{
		ui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		ui.setConsole(EktooUITranslator.getMessageStartSync(
		    ui.getSourceItem().toString(),
		    ui.getTargetItem().toString(),
		    new Date()
		    ));
		
		try 
		{
			SyncItemUI sourceItem = ui.getSourceItem();
			SyncItemUI targetItem = ui.getTargetItem();
			result = ui.getController().sync(sourceItem, targetItem);
			
			return result;
		}
		catch (Throwable t) 
		{
		  //TODO (NBL) handle exception
		  t.printStackTrace();
			LOGGER.error(t.getMessage(), t);
		}
		return null;
	}

	@Override
	public void done() 
	{
	  try 
		{
			result = get();
			if (result != null && result.startsWith("success")) 
			{
			  ui.setConsole(EktooUITranslator.getMessageSyncSyccessfuly(
            ui.getSourceItem().toString(),
            ui.getTargetItem().toString(),
            new Date()
            ));			  
			}
			else
			{
			  ui.setConsole(EktooUITranslator.getMessageSyncConflicts(
		        ui.getSourceItem().toString(),
		        ui.getTargetItem().toString(),
		        new Date()
		        ));
			}
			
		} 
		catch (InterruptedException e) 
		{
		  ui.setConsole(EktooUITranslator.getMessageSyncFailed(
          ui.getSourceItem().toString(),
          ui.getTargetItem().toString(),
          new Date()
          ));		  
			LOGGER.error(e.getMessage(), e);
		} 
		catch (ExecutionException e) 
		{
		  ui.setConsole(EktooUITranslator.getMessageSyncFailed(
          ui.getSourceItem().toString(),
          ui.getTargetItem().toString(),
          new Date()
          ));   		  
			LOGGER.error(e.getMessage(), e);
		}
		ui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
}
