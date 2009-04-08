package org.mesh4j.sync.ui.tasks;

import java.awt.Cursor;

import javax.swing.JFrame;
import javax.swing.SwingWorker;

import org.mesh4j.sync.adapters.http.HttpSyncAdapterFactory;
import org.mesh4j.sync.adapters.msaccess.MsAccessSyncAdapterFactory;
import org.mesh4j.sync.mappings.MSAccessDataSourceMapping;
import org.mesh4j.sync.properties.PropertiesProvider;
import org.mesh4j.sync.ui.LogFrame;
import org.mesh4j.sync.ui.translator.MeshCompactUITranslator;
import org.mesh4j.sync.utils.SyncEngineUtil;

public class DownloadSchemaAndMappingsTask extends SwingWorker<Void, Void> {
	 
	// MODEL VARIABLE
	private IDownloadListener downloadListener;
	private JFrame frame;
	private String url;
	private MSAccessDataSourceMapping dataSource;
	private PropertiesProvider propertiesProvider;
	
	// BUSINESS METHODS
	
	public DownloadSchemaAndMappingsTask(
			IDownloadListener downloadListener, JFrame frame,
			PropertiesProvider propertiesProvider, String url, MSAccessDataSourceMapping dataSource) {
		super();
		this.dataSource = dataSource;
		this.downloadListener = downloadListener;
		this.frame = frame;
		this.propertiesProvider = propertiesProvider;
		this.url = url;
	}
	
	@Override
    public Void doInBackground() {
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		downloadListener.setInProcess(MeshCompactUITranslator.getMapsWindowMessageDownloadMappingsStart());
		if(!HttpSyncAdapterFactory.isValidURL(url)){
			downloadListener.setError(MeshCompactUITranslator.getErrorInvalidURL());
			return null;
		}
		
		if(dataSource == null || !MsAccessSyncAdapterFactory.isValidAccessTable(dataSource.getFileName(), dataSource.getTableName())){
			downloadListener.setError(MeshCompactUITranslator.getErrorInvalidMSAccessTable());
			return null;
		}
		
		try{
			SyncEngineUtil.downloadMappings(url, dataSource.getAlias(), propertiesProvider);
			SyncEngineUtil.downloadSchema(url, dataSource.getAlias(), propertiesProvider);
			downloadListener.setOk(MeshCompactUITranslator.getMapsWindowMessageDownloadMappingsEnd());
		} catch(Throwable e){
			LogFrame.Logger.error(e.getMessage(), e);
			downloadListener.setError(MeshCompactUITranslator.getMapsWindowMessageDownloadMappingsFailed());
		}
		return null;
    }

	@Override
    public void done() {
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}
