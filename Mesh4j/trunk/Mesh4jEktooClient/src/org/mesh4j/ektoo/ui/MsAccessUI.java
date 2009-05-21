package org.mesh4j.ektoo.ui;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.controller.MsAccessUIController;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.sync.adapters.msaccess.MsAccessHelper;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */
public class MsAccessUI extends TableUI {

	private static final long serialVersionUID = 4708875346159085594L;
	private static final Log LOGGER = LogFactory.getLog(MsAccessUI.class);

	// MODEL VARIABLES
	private MsAccessUIController controller;

	// BUSINESS METHODS
	public MsAccessUI(MsAccessUIController controller) {
		super();
		this.controller = controller;
		this.controller.addView(this);
		initialize();
	}

	private void initialize() {
		this.showColumn(false);
		this.getFileChooser().setAcceptAllFileFilterUsed(false);
		this.getFileChooser().addChoosableFileFilter(new MsAccessFilter());
		this.getFileChooser().setFileSelectionMode(JFileChooser.FILES_ONLY);
	}

	@Override
	public void setList(File file) {
		JComboBox tableList = getTableList();
		tableList.removeAllItems();

		try {
			String tableName = null;
			Set<String> tableNames = MsAccessHelper.getTableNames(file
					.getAbsolutePath());
			Iterator<String> itr = tableNames.iterator();
			while (itr.hasNext()) {
				tableName = (String) itr.next();
				tableList.addItem(tableName);
			}

			this.controller.changeDatabaseName(file.getAbsolutePath());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Override
	public void setList(File file, int tableIndex) {
		try {
			this.controller.changeTableName((String) getTableList()
					.getSelectedItem());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public void setController(MsAccessUIController controller) {
		this.controller = controller;
	}

	public MsAccessUIController getController() {
		return controller;
	}

	@Override
	public void modelPropertyChange(final PropertyChangeEvent evt) 
	{
	  if ( evt.getPropertyName().equals( MsAccessUIController.DATABASE_NAME_PROPERTY ))
	  {
	    String newStringValue = evt.getNewValue().toString();
	    if (! getTxtFile().getText().equals(newStringValue))
	      getTxtFile().setText(newStringValue);
	  }
	  else if ( evt.getPropertyName().equals( MsAccessUIController.TABLE_NAME_PROPERTY))
	  {
      String newStringValue = evt.getNewValue().toString();
      if (! ((String)getTableList().getSelectedItem()).equals(newStringValue))
        getTableList().setSelectedItem((String)newStringValue);
	  }
	}

	@Override
	public void setList(File file, int tableIndex, String columnName) {
		// TODO setList
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

		if (ext != null && ext.equalsIgnoreCase(".mdb"))
			return true;

		return false;
	}

	public String getDescription() {
		return EktooUITranslator.getMSAccessFileSelectorTitle();
	}
}