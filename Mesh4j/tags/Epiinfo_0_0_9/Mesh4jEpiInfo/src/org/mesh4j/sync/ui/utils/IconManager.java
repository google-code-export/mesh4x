package org.mesh4j.sync.ui.utils;

import java.awt.Image;

import javax.swing.Icon;

import org.mesh4j.sync.adapters.feed.FeedSyncAdapterFactory;
import org.mesh4j.sync.adapters.http.HttpSyncAdapterFactory;
import org.mesh4j.sync.adapters.kml.KMLDOMLoaderFactory;
import org.mesh4j.sync.adapters.msaccess.MsAccessSyncAdapterFactory;
import org.mesh4j.sync.adapters.msexcel.MsExcelSyncAdapterFactory;
import org.mesh4j.sync.ui.MeshCompactUI;

import com.swtdesigner.SwingResourceManager;

public class IconManager {

	public static Icon getSourceImage(String source, boolean remote) {
		return SwingResourceManager.getIcon(MeshCompactUI.class, getSourceImageName(source, remote));
	}
	
	private static String getSourceImageName(String source, boolean remote){
		
		if(source == null){
			return "/undefinedDataSource.png";
		} else if(source.startsWith(FeedSyncAdapterFactory.SOURCE_TYPE)){
			return "/feedRSSDataSource.png";
		} else if (source.startsWith(HttpSyncAdapterFactory.SOURCE_TYPE)){
			return "/httpDataSource.png";
		} else if (source.startsWith(KMLDOMLoaderFactory.SOURCE_TYPE)){
			return "/kmlDataSource.png";
		} else if (source.startsWith(MsAccessSyncAdapterFactory.SOURCE_TYPE)){
			if(remote){
				return "/msAccessDataSourceRemote.png";
			} else {
				return "/msAccessDataSource.png";
			}
		} else if (source.startsWith(MsExcelSyncAdapterFactory.SOURCE_TYPE)){
			return "/msExcelDataSource.png";
		} else {
			return "/undefinedDataSource.png";
		}
	}

	public static Icon getUndefinedSourceImage() {
		return SwingResourceManager.getIcon(MeshCompactUI.class, "/undefinedDataSource.png");
	}
	
	public static Icon getDataSourceSamll() {
		return SwingResourceManager.getIcon(MeshCompactUI.class, "/dataSourceSmall.png");
	}
	
	public static Icon getViewDataSource() {
		return SwingResourceManager.getIcon(MeshCompactUI.class, "/viewDataSource.png");
	}
	
	public static Icon getSyncModeIcon(boolean sendChanges, boolean receiveChanges){
		if(sendChanges && receiveChanges){
			return SwingResourceManager.getIcon(MeshCompactUI.class, "/2WaySync.png");
		}else if(sendChanges){
			return SwingResourceManager.getIcon(MeshCompactUI.class, "/sendChangesOnly.png");
		} else {
			return SwingResourceManager.getIcon(MeshCompactUI.class, "/receiveChangesOnly.png");
		}
	}

	public static Icon getStatusErrorIcon() {
		return SwingResourceManager.getIcon(MeshCompactUI.class, "/error.png");
	}
	
	public static Icon getStatusInProgressIcon() {
		return SwingResourceManager.getIcon(MeshCompactUI.class, "/inProgress.gif");
	}
	
	public static Icon getStatusOkIcon() {
		return SwingResourceManager.getIcon(MeshCompactUI.class, "/ok.png");
	}
	
	public static Icon getStatusProcessingIcon() {
		return SwingResourceManager.getIcon(MeshCompactUI.class, "/processing.png");
	}
	
	public static Icon getStatusReadyIcon() {
		return null;
	}

	public static Image getCDCImage() {
		return SwingResourceManager.getImage(MeshCompactUI.class, "/cdc.gif");
	}
	
	public static Icon getViewImage() {
		return SwingResourceManager.getIcon(MeshCompactUI.class, "/view.jpg");
	}

	public static Icon getInOutIcon() {
		return SwingResourceManager.getIcon(MeshCompactUI.class, "/inOut.png");
	}

	public static Icon getTrademarkIcon() {
		return SwingResourceManager.getIcon(MeshCompactUI.class, "/mesh4x.png");
	}

	public static Icon getSyncMode2WayIcon() {
		return SwingResourceManager.getIcon(MeshCompactUI.class, "/2WaySync.png");
	}

	public static Icon getDownloadImage() {
		return SwingResourceManager.getIcon(MeshCompactUI.class, "/download.jpg");
	}

	public static Icon getMapImage() {
		return SwingResourceManager.getIcon(MeshCompactUI.class, "/map.jpg");
	}

	public static Icon getCloudErrorIcon() {
		return SwingResourceManager.getIcon(MeshCompactUI.class, "/cloudError.jpg");
	}

	public static Icon getCloudIcon() {
		return SwingResourceManager.getIcon(MeshCompactUI.class, "/cloud.jpg");
	}

	public static Icon getCloudConflictsIcon() {
		return SwingResourceManager.getIcon(MeshCompactUI.class, "/cloudConflicts.jpg");
	}

	public static Icon getViewCloudIcon() {
		return SwingResourceManager.getIcon(MeshCompactUI.class, "/viewCloud.jpg");
	}

}
