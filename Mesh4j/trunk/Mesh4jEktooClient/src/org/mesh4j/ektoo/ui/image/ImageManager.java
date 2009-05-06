package org.mesh4j.ektoo.ui.image;

import java.awt.Image;

import javax.swing.Icon;

import org.mesh4j.ektoo.ui.EktooUI;

import com.swtdesigner.SwingResourceManager;

public class ImageManager 
{
	
  public static Image getLogoSmall() 
  {
    return SwingResourceManager.getImage(EktooUI.class, "/instedd_small.jpg");
  }

  public static Icon getUndefinedSourceIcon() 
  {
    return SwingResourceManager.getIcon(EktooUI.class, "/undefinedDataSource.png");
  }
  
  public static Icon getDataSourceSmall() 
  {
    return SwingResourceManager.getIcon(EktooUI.class, "/dataSourceSmall.png");
  }
  
  public static Icon getDatabaseConnectionIcon() 
  {
    return SwingResourceManager.getIcon(EktooUI.class, "/connect_db.jpg");
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
      return SwingResourceManager.getIcon(EktooUI.class, "/undefinedDataSource.png");
    } 
    else if (source.startsWith("Cloud"))
    {
      return SwingResourceManager.getIcon(EktooUI.class, "/httpDataSource.png");      
    } 
    else if (source.startsWith("KML"))
    {
      return SwingResourceManager.getIcon(EktooUI.class, "/kmlDataSource.png");
    } 
    else if (source.startsWith("MS Access"))
    {
      return SwingResourceManager.getIcon(EktooUI.class, "/msAccessDataSource.png");
    } 
    else if (source.startsWith("MS Excel"))
    {
      return SwingResourceManager.getIcon(EktooUI.class, "/msExcelDataSource.png");
    }
    else if (source.startsWith("MySQL"))
    {
      return SwingResourceManager.getIcon(EktooUI.class, "/MySQLDataSource.png");
    }     
    else 
    {
      return SwingResourceManager.getIcon(EktooUI.class, "/undefinedDataSource.png");
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
}
