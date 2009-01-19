package org.mesh4j.sync.epiinfo.ui.utils;

import java.awt.Image;

import javax.swing.Icon;

import org.mesh4j.sync.adapters.feed.FeedSyncAdapterFactory;
import org.mesh4j.sync.adapters.http.HttpSyncAdapterFactory;
import org.mesh4j.sync.adapters.kml.KMLDOMLoaderFactory;
import org.mesh4j.sync.adapters.msaccess.MsAccessSyncAdapterFactory;
import org.mesh4j.sync.adapters.msexcel.MsExcelSyncAdapterFactory;
import org.mesh4j.sync.epiinfo.ui.EpiinfoCompactUI;
import org.mesh4j.sync.epiinfo.ui.EpiinfoUI;

import com.swtdesigner.SwingResourceManager;

public class EpiInfoIconManager {

	public static Icon getSourceImage(String source, boolean remote) {
		return SwingResourceManager.getIcon(EpiinfoCompactUI.class, getSourceImageName(source, remote));
	}
	
	private static String getSourceImageName(String source, boolean remote){
		if(source.startsWith(FeedSyncAdapterFactory.SOURCE_TYPE)){
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
		return SwingResourceManager.getIcon(EpiinfoCompactUI.class, "/undefinedDataSource.png");
	}
	
	public static Icon getDataSourceSamll() {
		return SwingResourceManager.getIcon(EpiinfoCompactUI.class, "/dataSourceSmall.png");
	}
	
	public static Icon getSyncModeIcon(boolean sendChanges, boolean receiveChanges){
		if(sendChanges && receiveChanges){
			return SwingResourceManager.getIcon(EpiinfoCompactUI.class, "/2WaySync.png");
		}else if(sendChanges){
			return SwingResourceManager.getIcon(EpiinfoCompactUI.class, "/sendChangesOnly.png");
		} else {
			return SwingResourceManager.getIcon(EpiinfoCompactUI.class, "/receiveChangesOnly.png");
		}
	}

	public static Icon getStatusErrorIcon() {
		return SwingResourceManager.getIcon(EpiinfoCompactUI.class, "/error.png");
	}
	
	public static Icon getStatusInProcessIcon() {
		return SwingResourceManager.getIcon(EpiinfoUI.class, "/inProcess.gif");
	}
	
	public static Icon getStatusOkIcon() {
		return SwingResourceManager.getIcon(EpiinfoUI.class, "/ok.png");
	}
	
	public static Icon getStatusReadyIcon() {
		return null;
	}

	public static Image getCDCImage() {
		return SwingResourceManager.getImage(EpiinfoCompactUI.class, "/cdc.gif");
	}

	public static Icon getInOutIcon() {
		return SwingResourceManager.getIcon(EpiinfoCompactUI.class, "/inOut.png");
	}

	public static Icon getTrademarkIcon() {
		return SwingResourceManager.getIcon(EpiinfoCompactUI.class, "/mesh4x.png");
	}

	public static Icon getSyncMode2WayIcon() {
		return SwingResourceManager.getIcon(EpiinfoCompactUI.class, "/2WaySync.png");
	}
}
