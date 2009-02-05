package org.mesh4j.sync.ui;

import java.awt.Image;

import javax.swing.Icon;

import org.mesh4j.sync.utils.SyncAdapterFactory;

import com.swtdesigner.SwingResourceManager;

public class IconManager {

	public static Icon getStatusErrorIcon() {
		return SwingResourceManager.getIcon(KmlUI.class, "/error.png");
	}
	
	public static Icon getStatusInProgressIcon() {
		return SwingResourceManager.getIcon(KmlUI.class, "/inProgress.gif");
	}
	
	public static Icon getStatusOkIcon() {
		return SwingResourceManager.getIcon(KmlUI.class, "/ok.png");
	}
	
	public static Icon getCloudIcon() {
		return SwingResourceManager.getIcon(KmlUI.class, "/viewCloud.jpg");
	}

	public static Icon getTrademarkIcon() {
		return SwingResourceManager.getIcon(KmlUI.class, "/mesh4x.png");
	}

	public static Icon getMapIcon() {
		return SwingResourceManager.getIcon(KmlUI.class, "/map.jpg");
	}
	
	public static Image getInsteddImage() {
		return SwingResourceManager.getImage(KmlUI.class, "/logo-instedd.png");
	}

	public static Icon getViewIcon(SyncAdapterFactory syncAdapterFactory, String endpoint) {
		if(syncAdapterFactory.isKml(endpoint)){
			return getMapIcon();	
		} else if(syncAdapterFactory.isHTTP(endpoint)){
			return getCloudIcon();
		} else {
			return null;
		}
		
	}

	public static Icon getStatusReadyIcon() {
		return null;
	}

}
