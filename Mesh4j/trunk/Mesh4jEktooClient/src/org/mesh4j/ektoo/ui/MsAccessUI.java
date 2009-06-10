package org.mesh4j.ektoo.ui;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.controller.MsAccessUIController;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.ektoo.ui.validator.MsAccessUIValidator;
import org.mesh4j.sync.adapters.msaccess.MsAccessSyncAdapterFactory;

public class MsAccessUI extends TableUI {

	private static final long serialVersionUID = 4708875346159085594L;
	private static final Log LOGGER = LogFactory.getLog(MsAccessUI.class);

	// MODEL VARIABLES
	private MsAccessUIController controller;

	// BUSINESS METHODS
	public MsAccessUI(String fileName, MsAccessUIController controller) {
		super();
		this.controller = controller;
		this.controller.addView(this);

		this.showColumn(false);
		this.getFileChooser().setAcceptAllFileFilterUsed(false);
		this.getFileChooser().setFileFilter(
				new FileNameExtensionFilter(EktooUITranslator
						.getMSAccessFileSelectorTitle(), "mdb", "MDB"));
		this.getFileChooser().setFileSelectionMode(JFileChooser.FILES_ONLY);

		getTxtFile().setText(fileName);
		getTxtFile().setToolTipText(fileName);
		setList(fileName);
	}

	@Override
	public void setList(String fileName) {
		JComboBox tableList = getTableList();
		tableList.removeAllItems();

		JComboBox columnList = getColumnList();
		columnList.removeAllItems();
		
		try {
			File file = new File(fileName);
			if(file.exists()){
				Set<String> tableNames = MsAccessSyncAdapterFactory.getTableNames(fileName);
				for (String tableName : tableNames) {
					tableList.addItem(tableName);
				}
			} else {
				((SyncItemUI)this.getParent().getParent()).openErrorPopUp(EktooUITranslator.getErrorImpossibleToOpenFileBecauseFileDoesNotExists());
			}
			this.controller.changeDatabaseName(fileName);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Override
	public void setList(String fileName, String table) {
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
	public void modelPropertyChange(final PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(
				MsAccessUIController.DATABASE_NAME_PROPERTY)) {
			String newStringValue = evt.getNewValue().toString();
			if (!getTxtFile().getText().equals(newStringValue))
				getTxtFile().setText(newStringValue);
		} else if (evt.getPropertyName().equals(
				MsAccessUIController.TABLE_NAME_PROPERTY)) {
			String newStringValue = evt.getNewValue().toString();
			if (!((String) getTableList().getSelectedItem())
					.equals(newStringValue)) {
				getTableList().setSelectedItem((String) newStringValue);
			}
		}
	}

	@Override
	public void setList(String fileName, String table, String columnName) {
		// TODO setList
	}

	@Override
	public boolean verify() {
		boolean valid = (new MsAccessUIValidator(this, controller.getModel(),
				null)).verify();
		return valid;
	}

}