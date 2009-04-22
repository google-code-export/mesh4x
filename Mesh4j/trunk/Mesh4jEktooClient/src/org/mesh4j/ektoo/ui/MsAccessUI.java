package org.mesh4j.ektoo.ui;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.filechooser.FileFilter;

import org.mesh4j.ektoo.controller.MsAccessUIController;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.sync.adapters.msaccess.MsAccessHelper;
/**
 * @author Bhuiyan Mohammad Iklash
 *
 */
public class MsAccessUI extends TableUI
{
	private static final long serialVersionUID = 1L;
	private MsAccessUIController controller;

	public MsAccessUI() 
	{
		super();
		initialize();
	}
	
	public MsAccessUI(MsAccessUIController controller) 
	{
		super();
		this.controller = controller;
		initialize();
	}

	private void initialize()
	{
		this.showColumn(false);
		this.getFileChooser().setDialogTitle( EktooUITranslator.getExcelFileSelectorTitle());
		this.getFileChooser().setAcceptAllFileFilterUsed(false);
		this.getFileChooser().addChoosableFileFilter(new MsAccessFilter());
	}

	@Override
	public void setList(File file)
	{
		JComboBox tableList = getTableList();
		tableList.removeAllItems();
		
		try
		{
			String tableName = null;
			Set<String> tableNames = MsAccessHelper.getTableNames(file.getAbsolutePath());
			Iterator<String> itr = tableNames.iterator();
			while(itr.hasNext())
			{
				tableName = (String)itr.next();
				tableList.addItem(tableName);
			}
		}
		catch(IOException ioe)
		{
			
		}
		
		try
		{
			this.controller.changeDatabaseName(file.getAbsolutePath()); 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}

	@Override
	public void setList(File file, int tableIndex)
	{
		try
		{
			this.controller.changeTableName( (String)getTableList().getSelectedItem() );
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}

	public void setController(MsAccessUIController controller) {
		this.controller = controller;
	}

	public MsAccessUIController getController() {
		return controller;
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) 
	{
	}

	@Override
	public void setList(File file, int tableIndex, String columnName) 
	{
	}
}

class MsAccessFilter extends FileFilter 
{
  public boolean accept(File file) 
  {
    if (file.isDirectory())
      return true;
        
    int pos = file.getName().lastIndexOf(".");
    String ext = file.getName().substring(pos);
        
    if (ext != null && ext.equals(".mdb")) 
      return true;

    return false;
  }

  public String getDescription() 
  {
    return EktooUITranslator.getExcelFileDescription();
  }
}