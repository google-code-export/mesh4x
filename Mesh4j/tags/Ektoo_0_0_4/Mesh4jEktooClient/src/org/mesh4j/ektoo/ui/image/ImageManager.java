package org.mesh4j.ektoo.ui.image;

import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.mesh4j.ektoo.ui.EktooUI;
import org.mesh4j.ektoo.ui.SyncItemUI;

import com.swtdesigner.SwingResourceManager;

public class ImageManager 
{
	
  public static Image getLogoSmall() 
  {
    return SwingResourceManager.getImage(EktooUI.class, "/instedd_small.png");
  }
  public static Image getMesh4xLogoSmall() 
  {
    return SwingResourceManager.getImage(EktooUI.class, "/mesh4x.png");
  }

  public static Icon getUndefinedSourceIcon() 
  {
    return SwingResourceManager.getIcon(EktooUI.class, "/ds.png");
  }
  
  public static Icon getFolderSourceIcon() 
  {
    return SwingResourceManager.getIcon(EktooUI.class, "/folder_ds.png");
  }
  
  public static Icon getDataSourceSmall() 
  {
    return SwingResourceManager.getIcon(EktooUI.class, "/dataSourceSmall.png");
  }
  
  public static Icon getDatabaseConnectionIcon() 
  {
    return SwingResourceManager.getIcon(EktooUI.class, "/connection_16.png");
  }

  public static Icon getSyncModeIcon(boolean sendChanges, boolean receiveChanges)
  {
    if(sendChanges && receiveChanges)
    {
      return SwingResourceManager.getIcon(EktooUI.class, "/2WaySync.png");
    }
    else if(sendChanges)
    {
      return SwingResourceManager.getIcon(EktooUI.class, "/sendChangesOnly.png");
    }
    else 
    {
      return SwingResourceManager.getIcon(EktooUI.class, "/receiveChangesOnly.png");
    }
  }  
  
  public static Icon getSourceImage(String source, boolean remote)
  {
    if(source == null)
    {
      return SwingResourceManager.getIcon(EktooUI.class, "/ds.png");
    } 
    else if (source.startsWith(SyncItemUI.CLOUD_PANEL))
    {
      return SwingResourceManager.getIcon(EktooUI.class, "/cloud_ds.png");      
    } 
    else if (source.startsWith(SyncItemUI.KML_PANEL))
    {
      return SwingResourceManager.getIcon(EktooUI.class, "/kml_ds.png");
    } 
    else if (source.startsWith(SyncItemUI.MS_ACCESS_PANEL))
    {
      return SwingResourceManager.getIcon(EktooUI.class, "/access_ds.png");
    } 
    else if (source.startsWith(SyncItemUI.MS_EXCEL_PANEL))
    {
      return SwingResourceManager.getIcon(EktooUI.class, "/excel_ds.png");
    }
    else if (source.startsWith(SyncItemUI.MYSQL_PANEL))
    {
      return SwingResourceManager.getIcon(EktooUI.class, "/mysql_ds.png");
    } 
    else if (source.startsWith(SyncItemUI.GOOGLE_SPREADSHEET_PANEL))
    {
      return SwingResourceManager.getIcon(EktooUI.class, "/gs_ds.png");
    }     
    else if (source.startsWith(SyncItemUI.FOLDER_PANEL))
    {
      return SwingResourceManager.getIcon(EktooUI.class, "/folder_ds.png");
    } 
    else if (source.startsWith(SyncItemUI.RSS_FILE_PANEL))
    {
      return SwingResourceManager.getIcon(EktooUI.class, "/rss_ds.png");
    } 
    else if (source.startsWith(SyncItemUI.ATOM_FILE_PANEL))
    {
      return SwingResourceManager.getIcon(EktooUI.class, "/atom_ds.png");
    } 
    else 
    {
      return SwingResourceManager.getIcon(EktooUI.class, "/ds.png");
    }
  }
  
  public static Icon getStatusErrorIcon() 
  {
    return SwingResourceManager.getIcon(EktooUI.class, "/error.png");
  }

  public static Icon getStatusInProgressIcon() 
  {
    return SwingResourceManager.getIcon(EktooUI.class, "/inProgress.gif");
  }
  
  public static Icon getStatusOkIcon() {
    return SwingResourceManager.getIcon(EktooUI.class, "/ok.png");
  }
  
  public static Icon getStatusProcessingIcon() {
    return SwingResourceManager.getIcon(EktooUI.class, "/processing.png");
  }
 
  public static Icon getTrademarkIcon() {
    return SwingResourceManager.getIcon(EktooUI.class, "/mesh4x.png");
  }  
  
  public static Icon getCloudErrorIcon() {
    return SwingResourceManager.getIcon(EktooUI.class, "/cloudError.jpg");
  }

  public static Icon getCloudIcon() {
    return SwingResourceManager.getIcon(EktooUI.class, "/cloud.jpg");
  }

  public static ImageIcon getSyncProcessIcon()
  {
    return SwingResourceManager.getIcon(EktooUI.class, "/sync.gif");
  }

  public static Image getSyncProcessImage()
  {
    return SwingResourceManager.getImage(EktooUI.class, "/sync.gif");
  }
  
  public static Icon getErrorIcon()
  {
    return SwingResourceManager.getIcon(EktooUI.class, "/16x16/error.png");
  }

  public static Icon getWarningIcon()
  {
    return SwingResourceManager.getIcon(EktooUI.class, "/16x16/warning.png");
  }

  public static Icon getInfoIcon()
  {
    return SwingResourceManager.getIcon(EktooUI.class, "/16x16/info.png");
  }

  public static Icon getSuccessIcon()
  {
    return SwingResourceManager.getIcon(EktooUI.class, "/16x16/accept.png");
  }
  public static Icon getSeperatorIcon()
  {
    return SwingResourceManager.getIcon(EktooUI.class, "/separator.png");
  }
}
