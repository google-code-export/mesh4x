package org.mesh4j.ektoo.tasks;

import java.awt.Cursor;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.mesh4j.ektoo.ui.EktooUI;
import org.mesh4j.ektoo.ui.SyncItemUI;

public class SynchronizeTask extends SwingWorker<String, Void> {
	 
	// MODEL VARIABLEs
	private EktooUI ui;
	private String result = null;
	
	// BUSINESS METHODS
	public SynchronizeTask(EktooUI ui)
	{
		super();
		this.ui = ui;
	}
	
	@Override
  public String doInBackground() 
	{
		ui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		ui.setConsole("Start sync...");
		try
		{
			SyncItemUI sourceItem =  ui.getSourceItem();
			SyncItemUI targetItem =  ui.getTargetItem();
			
			result = ui.getController().sync(sourceItem, targetItem);
			return result;
		} 
		catch(Throwable t)
		{
		}
		
		return null;
    }

	@Override
	public void done() 
	{
		try 
		{
			result = get();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		catch (ExecutionException e) 
		{
			e.printStackTrace();
		}
		ui.setConsole(result);
		ui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
  }
}
