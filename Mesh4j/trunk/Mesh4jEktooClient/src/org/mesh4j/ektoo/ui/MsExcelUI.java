package org.mesh4j.ektoo.ui;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.mesh4j.ektoo.controller.MsExcelUIController;
import org.mesh4j.ektoo.ui.component.messagedialog.MessageDialog;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.ektoo.ui.validator.MsExcelUIValidator;
import org.mesh4j.ektoo.validator.IValidationStatus;
import org.mesh4j.sync.adapters.msexcel.MsExcel;
import org.mesh4j.sync.adapters.msexcel.MsExcelUtils;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */
public class MsExcelUI extends TableUI implements IValidationStatus {	
	// CONSTANTS
	private static final long serialVersionUID = -5022572211883785527L;
	private static final Log LOGGER = LogFactory.getLog(MsExcelUI.class);
	
	// MODEL VARIABLES
	private MsExcelUIController controller;

	// BUSINESS METHODS
	public MsExcelUI(String fileName, MsExcelUIController controller) 
	{
		super();
		this.controller = controller;
		this.controller.addView(this);
		initialize();
		File file = new File(fileName);
		setFile(file);
		this.getTxtFile().setText(file.getName());
		setList(file);
	}

	private void initialize() 
	{
		this.getFileChooser().setAcceptAllFileFilterUsed(false);
		this.getFileChooser().setFileFilter(new FileNameExtensionFilter(EktooUITranslator.getExcelFileSelectorTitle(), "xls", "xlsx", "XLS", "XLSX"));
		this.getFileChooser().setFileSelectionMode(JFileChooser.FILES_ONLY);
	}

	
	@Override
	public void setList(File file) 
	{
		JComboBox sheetList = getTableList();
		sheetList.removeAllItems();

		MsExcel excelFile = new MsExcel(file.getAbsolutePath());
		Workbook workbook = excelFile.getWorkbook();

		if (workbook != null) {
			int sheetNum = workbook.getNumberOfSheets();
			for (int i = 0; i < sheetNum; i++) {
				String sheetName = workbook.getSheetName(i);
				if (sheetName != null) {
					sheetList.addItem(sheetName);
				}
			}
		}

		try {
			this.controller.changeWorkbookName(file.getAbsolutePath());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setList(File file, int tableIndex) 
	{
		JComboBox columnList = getColumnList();
		columnList.removeAllItems();

		MsExcel excelFile = new MsExcel(file.getAbsolutePath());
		Workbook workbook = excelFile.getWorkbook();
		Sheet sheet = workbook.getSheetAt(tableIndex);

		Row row = MsExcelUtils.getOrCreateRowHeaderIfAbsent(sheet);

		Cell cell = null;
		String label = null;

		Iterator cells = row.cellIterator();
		while (cells.hasNext()) {
			cell = (Cell) cells.next();
			label = cell.getRichStringCellValue().getString();
			columnList.addItem(label);
		}

		try {
			this.controller.changeWorksheetName(workbook
					.getSheetName(tableIndex));
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Override
	public void setList(File file, int tableIndex, String columnName) 
	{
		try {
			this.controller.changeUniqueColumnName(columnName);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public void setController(MsExcelUIController controller) 
	{
		this.controller = controller;
	}

	public MsExcelUIController getController() 
	{
		return controller;
	}

	@Override
	public void modelPropertyChange(final PropertyChangeEvent evt) 
	{
    if ( evt.getPropertyName().equals( MsExcelUIController.WORKBOOK_NAME_PROPERTY)){
      String newStringValue = evt.getNewValue().toString();
      if (!  getTxtFile().getText().equals(newStringValue))
        getTxtFile().setText(newStringValue);
    }		
    else if ( evt.getPropertyName().equals( MsExcelUIController.WORKSHEET_NAME_PROPERTY)){
      String newStringValue = evt.getNewValue().toString();
      if(isMustCreateSchema()){
    	  if(comboContains(getTableList(), newStringValue)){
    		  getTableList().setSelectedItem(newStringValue);
    	  }else {
    		  getTableList().addItem(newStringValue);  
    		  getTableList().setSelectedItem(newStringValue);
    	  }
    	    
      } else {
    	  if (!((String)getTableList().getSelectedItem()).equals(newStringValue))
    		  getTableList().setSelectedItem(newStringValue);
      }
    }   
    else if ( evt.getPropertyName().equals( MsExcelUIController.UNIQUE_COLUMN_NAME_PROPERTY)){
    	String newStringValue = evt.getNewValue().toString();
    	if(isMustCreateSchema()){
    		if(comboContains(getColumnList(), newStringValue)){
    			getColumnList().setSelectedItem(newStringValue);	
    		} else {
    			getColumnList().addItem(newStringValue);
    			getColumnList().setSelectedItem(newStringValue);
    		}
    	} else {
    		if (!  ((String)getColumnList().getSelectedItem()).equals(newStringValue))
    	        getColumnList().setSelectedItem(newStringValue);	
    	}
    }   

	}
	
	//checks the combo box model if it contains the provided value
	//TODO(raju) improve and use it in modelPropertyChange() method
	private boolean comboContains(JComboBox comboBox,String value){
		int size = comboBox.getModel().getSize();
		for(int i = 0 ; i<size ; i++){
			String retrivedValue = comboBox.getModel().getElementAt(i).toString();
			if(retrivedValue == null || retrivedValue.equals("")){
				return false;
			}else if(retrivedValue.equals(value)){
				return true;
			}
		}
		return false;
	}

	public void updateUiForSchemaCreation(boolean isEanble){
		getTableList().setEnabled(isEanble);
		getColumnList().setEnabled(isEanble);
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
		if(isMustCreateSchema()){//just verify the file name or location
			List<JComponent> uiFieldList = new LinkedList<JComponent>();
			uiFieldList.add(getTxtFile());
			return (new MsExcelUIValidator(this,controller.getModel(), uiFieldList)).verify();
		}
		return (new MsExcelUIValidator(this,
				controller.getModel(), null)).verify();
	}

	
	

}