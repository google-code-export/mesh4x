package org.mesh4j.ektoo.ui.image;

import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.mesh4j.ektoo.ui.EktooFrame;
import org.mesh4j.ektoo.ui.SyncItemUI;

import com.swtdesigner.SwingResourceManager;

public class ImageManager {

	public static Image getLogoSmall() {
		return SwingResourceManager.getImage(EktooFrame.class,
				"/instedd_small.png");
	}

	public static Image getMesh4xLogoSmall() {
		return SwingResourceManager.getImage(EktooFrame.class, "/mesh4x.png");
	}

	public static Icon getUndefinedSourceIcon() {
		return SwingResourceManager.getIcon(EktooFrame.class, "/ds.png");
	}

	public static Icon getFolderSourceIcon() {
		return SwingResourceManager.getIcon(EktooFrame.class, "/folder_ds.png");
	}

	public static Icon getDataSourceSmall() {
		return SwingResourceManager.getIcon(EktooFrame.class,
				"/dataSourceSmall.png");
	}

	public static Icon getDatabaseConnectionIcon() {
		return SwingResourceManager.getIcon(EktooFrame.class,
				"/connection_16.png");
	}

	public static Icon getSourceImage(String source, boolean remote) {
		if (source == null) {
			return SwingResourceManager.getIcon(EktooFrame.class, "/ds.png");
		} else if (source.startsWith(SyncItemUI.CLOUD_PANEL)) {
			return SwingResourceManager.getIcon(EktooFrame.class,
					"/cloud_ds.png");
		} else if (source.startsWith(SyncItemUI.KML_PANEL)) {
			return SwingResourceManager
					.getIcon(EktooFrame.class, "/kml_ds.png");
		} else if (source.startsWith(SyncItemUI.MS_ACCESS_PANEL)) {
			return SwingResourceManager.getIcon(EktooFrame.class,
					"/access_ds.png");
		} else if (source.startsWith(SyncItemUI.MS_EXCEL_PANEL)) {
			return SwingResourceManager.getIcon(EktooFrame.class,
					"/excel_ds.png");
		} else if (source.startsWith(SyncItemUI.MYSQL_PANEL)) {
			return SwingResourceManager.getIcon(EktooFrame.class,
					"/mysql_ds.png");
		} else if (source.startsWith(SyncItemUI.GOOGLE_SPREADSHEET_PANEL)) {
			return SwingResourceManager.getIcon(EktooFrame.class, "/gs_ds.png");
		} else if (source.startsWith(SyncItemUI.FOLDER_PANEL)) {
			return SwingResourceManager.getIcon(EktooFrame.class,
					"/folder_ds.png");
		} else if (source.startsWith(SyncItemUI.RSS_FILE_PANEL)) {
			return SwingResourceManager
					.getIcon(EktooFrame.class, "/rss_ds.png");
		} else if (source.startsWith(SyncItemUI.ATOM_FILE_PANEL)) {
			return SwingResourceManager.getIcon(EktooFrame.class,
					"/atom_ds.png");
		} else if (source.startsWith(SyncItemUI.ZIP_FILE_PANEL)) {
			return SwingResourceManager.getIcon(EktooFrame.class,
					"/zip_ds.png");
		} else {
			return SwingResourceManager.getIcon(EktooFrame.class, "/ds.png");
		}
	}

	public static Icon getSyncModeIcon(boolean sendChanges, boolean receiveChanges) {
		if(sendChanges && receiveChanges){
			return SwingResourceManager.getIcon(EktooFrame.class, "/2WaySync.png");
		}
		else if(sendChanges){
			return SwingResourceManager.getIcon(EktooFrame.class, "/sendChangesOnly.png");
		} else {
			return SwingResourceManager.getIcon(EktooFrame.class, "/receiveChangesOnly.png");
		}
	}  
  
	public static Icon getSyncIcon(boolean active) {
		if (active) {
			return SwingResourceManager.getIcon(EktooFrame.class, "/sync_active.gif");
		} else {
			return SwingResourceManager.getIcon(EktooFrame.class, "/sync_inactive.gif");
		}
	} 
  
	public static Icon getStatusErrorIcon() {
		return SwingResourceManager.getIcon(EktooFrame.class, "/error.png");
	}

	public static Icon getStatusInProgressIcon() {
		return SwingResourceManager
				.getIcon(EktooFrame.class, "/inProgress.gif");
	}

	public static Icon getStatusOkIcon() {
		return SwingResourceManager.getIcon(EktooFrame.class, "/ok.png");
	}

	public static Icon getStatusProcessingIcon() {
		return SwingResourceManager
				.getIcon(EktooFrame.class, "/processing.png");
	}

	public static Icon getTrademarkIcon() {
		return SwingResourceManager.getIcon(EktooFrame.class, "/mesh4x.png");
	}
	
	public static Icon getCloudErrorIcon() {
		return SwingResourceManager
				.getIcon(EktooFrame.class, "/cloudError.jpg");
	}

	public static Icon getCloudIcon() {
		return SwingResourceManager.getIcon(EktooFrame.class, "/cloud.jpg");
	}

	public static ImageIcon getSyncProcessIcon() {
		return SwingResourceManager.getIcon(EktooFrame.class, "/sync.gif");
	}

	public static Image getSyncProcessImage() {
		return SwingResourceManager.getImage(EktooFrame.class, "/sync.gif");
	}

	public static Icon getErrorIcon() {
		return SwingResourceManager.getIcon(EktooFrame.class,
				"/16x16/error.png");
	}

	public static Icon getWarningIcon() {
		return SwingResourceManager.getIcon(EktooFrame.class,
				"/16x16/warning.png");
	}

	public static Icon getInfoIcon() {
		return SwingResourceManager
				.getIcon(EktooFrame.class, "/16x16/info.png");
	}

	public static Icon getSuccessIcon() {
		return SwingResourceManager.getIcon(EktooFrame.class,
				"/16x16/accept.png");
	}

	public static Icon getSeperatorIcon() {
		return SwingResourceManager.getIcon(EktooFrame.class, "/separator.png");
	}

	public static Icon getProgressIcon() {
		return SwingResourceManager.getIcon(EktooFrame.class,
				"/16x16/progress.gif");
	}

	public static Icon getViewIcon() {
		return SwingResourceManager.getIcon(EktooFrame.class, "/view.jpg");
	}
}
