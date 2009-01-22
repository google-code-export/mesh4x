package org.mesh4j.sync.ui.tasks;

import java.awt.Cursor;

import javax.swing.SwingWorker;

import org.mesh4j.sync.mappings.DataSourceMapping;
import org.mesh4j.sync.mappings.EndpointMapping;
import org.mesh4j.sync.mappings.SyncMode;
import org.mesh4j.sync.ui.MeshCompactUI;
import org.mesh4j.sync.utils.SyncEngineUtil;

public class SynchronizeTask extends SwingWorker<Void, Void> {
	 
	// MODEL VARIABLEs
	private MeshCompactUI ui;
	
	// BUSINESS METHODS
	public SynchronizeTask(MeshCompactUI ui){
		super();
		this.ui = ui;
	}
	
	@Override
    public Void doInBackground() {
		ui.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		try{
			DataSourceMapping dataSource = (DataSourceMapping)ui.getComboBoxMappingDataSource().getSelectedItem();
			EndpointMapping endpoint = (EndpointMapping)ui.getComboBoxEndpoint().getSelectedItem();
			SyncMode syncMode = (SyncMode)ui.getComboBoxSyncMode().getSelectedItem();
			
			ui.beginSync();
			SyncEngineUtil.synchronize(ui.getSyncEngine(), syncMode, endpoint, dataSource, ui.getSourceIdResolver(), ui.getPropertiesProvider());

		} catch(Throwable t){
			ui.notifyErrorSync(t);
		}
		
		return null;
    }

	@Override
    public void done() {
		ui.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}
