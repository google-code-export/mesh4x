package org.mesh4j.ektoo.ui;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.controller.MsAccessUIController;
import org.mesh4j.ektoo.ui.component.messagedialog.MessageDialog;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.ektoo.ui.validator.MsAccessUIValidator;
import org.mesh4j.ektoo.ui.validator.MySQLConnectionValidator;
import org.mesh4j.ektoo.validator.IValidationStatus;
import org.mesh4j.sync.adapters.msaccess.MsAccessHelper;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */
public class MsAccessUI extends TableUI implements IValidationStatus {

	private static final long serialVersionUID = 4708875346159085594L;
	private static final Log LOGGER = LogFactory.getLog(MsAccessUI.class);

	// MODEL VARIABLES
	private MsAccessUIController controller;

	// BUSINESS METHODS
	public MsAccessUI(String fileName, MsAccessUIController controller) {
		super();
		this.controller = controller;
		this.controller.addView(this);
		initialize();
		
		File file = new File(fileName);
		setFile(file);
		setList(file);
		getTxtFile().setText(file.getName());
	}

	private void initialize() {
		this.showColumn(false);
		this.getFileChooser().setAcceptAllFileFilterUsed(false);
		this.getFileChooser().setFileFilter(new FileNameExtensionFilter(EktooUITranslator.getMSAccessFileSelectorTitle(), "mdb", "MDB"));
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

	@Override
	public void validationFailed(Hashtable<Object, String> errorTable) {
		Object key = null;
		StringBuffer err = new StringBuffer();
		Enumeration<Object> keys = errorTable.keys();
		while (keys.hasMoreElements()) {
			key = keys.nextElement(); 
			err.append(errorTable.get(key) + "\n");
		}
		MessageDialog.showErrorMessage(JOptionPane.getRootFrame(), err.toString());
	}
	
	@Override
	public void validationPassed() {
		// TODO (Nobel)
	}

	@Override
	public boolean verify() {
		boolean valid = (new MsAccessUIValidator(this,
				controller.getModel(), null)).verify();
		return valid;
	}
}