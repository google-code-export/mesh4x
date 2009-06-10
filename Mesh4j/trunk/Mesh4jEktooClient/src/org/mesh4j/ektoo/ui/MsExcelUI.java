package org.mesh4j.ektoo.ui;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.controller.MsExcelUIController;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.ektoo.ui.validator.MsExcelUIValidator;
import org.mesh4j.ektoo.validator.IValidationStatus;
import org.mesh4j.sync.adapters.msexcel.MsExcelSyncAdapterFactory;

public class MsExcelUI extends TableUI implements IValidationStatus {
	// CONSTANTS
	private static final long serialVersionUID = -5022572211883785527L;
	private static final Log LOGGER = LogFactory.getLog(MsExcelUI.class);

	// MODEL VARIABLES
	private MsExcelUIController controller;

	// BUSINESS METHODS
	public MsExcelUI(String fileName, MsExcelUIController controller) {
		super();
		this.controller = controller;
		this.controller.addView(this);

		this.getFileChooser().setAcceptAllFileFilterUsed(false);
		this.getFileChooser().setFileFilter(
				new FileNameExtensionFilter(EktooUITranslator
						.getExcelFileSelectorTitle(), "xls", "xlsx", "XLS",
						"XLSX"));
		this.getFileChooser().setFileSelectionMode(JFileChooser.FILES_ONLY);

		this.getTxtFile().setText(fileName);
		this.getTxtFile().setToolTipText(fileName);
		setList(fileName);
	}

	@Override
	public void setList(String fileName) {
		JComboBox sheetList = getTableList();
		sheetList.removeAllItems();

		JComboBox columnList = getColumnList();
		columnList.removeAllItems();

		Set<String> sheetNames = MsExcelSyncAdapterFactory.getSheetNames(fileName);
		for (String sheetName : sheetNames) {
			sheetList.addItem(sheetName);
		}

		if(sheetNames.isEmpty()){
			this.controller.changeWorksheetName("");
			this.controller.changeUniqueColumnName("");
			
			if(this.getController().acceptsCreateDataset()){
				File file = new File(fileName);
				if(!file.exists()){
					this.setMessageText(EktooUITranslator.getMessageNewFile());
				} else {
					this.setMessageText(EktooUITranslator.getMessageUpdateFile());
				}
			} else {
				this.setMessageText(EktooUITranslator.getMessageUpdateFile());
			}
		} else {
			this.setMessageText(EktooUITranslator.getMessageUpdateFile());
		}
		this.controller.changeWorkbookName(fileName);
	}

	@Override
	public void setList(String fileName, String sheetName) {
		JComboBox columnList = getColumnList();
		columnList.removeAllItems();

		Set<String> columnHeaderNames = MsExcelSyncAdapterFactory.getColumnHeaderNames(fileName, sheetName);
		for (String columnName : columnHeaderNames) {
			columnList.addItem(columnName);
		}
		
		this.controller.changeWorksheetName(sheetName);
	}

	@Override
	public void setList(String fileName, String sheetName, String columnName) {
		try {
			this.controller.changeUniqueColumnName(columnName);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public void setController(MsExcelUIController controller) {
		this.controller = controller;
	}

	public MsExcelUIController getController() {
		return controller;
	}

	@Override
	public void modelPropertyChange(final PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(MsExcelUIController.WORKBOOK_NAME_PROPERTY)) {
			String newStringValue = evt.getNewValue().toString();
			if (!getTxtFile().getText().equals(newStringValue)){
				getTxtFile().setText(newStringValue);
			}
		} else if (evt.getPropertyName().equals(MsExcelUIController.WORKSHEET_NAME_PROPERTY)) {
			String newStringValue = evt.getNewValue().toString();
			if (!((String) getTableList().getSelectedItem()).equals(newStringValue)){
				getTableList().setSelectedItem(newStringValue);
			}
		} else if (evt.getPropertyName().equals(MsExcelUIController.UNIQUE_COLUMN_NAME_PROPERTY)) {
			String newStringValue = evt.getNewValue().toString();
			if (!((String) getColumnList().getSelectedItem()).equals(newStringValue)){
				getColumnList().setSelectedItem(newStringValue);
			}
		}
	}

	@Override
	public boolean verify() {
		return (new MsExcelUIValidator(this, controller.getModel(), null)).verify();
	}
}