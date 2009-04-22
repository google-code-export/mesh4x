package org.mesh4j.ektoo.ui;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.filechooser.FileFilter;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.mesh4j.ektoo.controller.MsExcelUIController;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.sync.adapters.msexcel.MsExcel;
import org.mesh4j.sync.adapters.msexcel.MsExcelUtils;

/**
 * @author Bhuiyan Mohammad Iklash
 *
 */
public class MsExcelUI extends TableUI
{
	// CONSTANTS
	private static final long serialVersionUID = -5022572211883785527L;
	private MsExcelUIController controller;

	// BUSINESS METHODS
	public MsExcelUI() {
		super();
		initialize();
	}

	public MsExcelUI(MsExcelUIController controller) 
	{
		super();
		this.controller = controller;
		initialize();
	}

	private void initialize()
	{
		this.getFileChooser().setDialogTitle(EktooUITranslator.getExcelFileSelectorTitle());
		this.getFileChooser().setAcceptAllFileFilterUsed(false);
		this.getFileChooser().addChoosableFileFilter(new MsExcelFilter());
	}

	@Override
	public void setList(File file)
	{
	  System.out.println("1...");
		JComboBox sheetList = getTableList();
		sheetList.removeAllItems();

		MsExcel excelFile = new MsExcel(file.getAbsolutePath());
		HSSFWorkbook workbook = excelFile.getWorkbook();

		if(workbook != null)
		{
			int sheetNum = workbook.getNumberOfSheets();
			for(int i=0; i < sheetNum; i++)
			{
				String sheetName = workbook.getSheetName(i);
				if (sheetName != null)
				{
					sheetList.addItem(sheetName);
				}
			}
		}
		
		try
		{
			this.controller.changeWorkbookName(file.getAbsolutePath()); 
		}
		catch(Exception e)
		{
			
		}		
	}

	@Override
	public void setList(File file, int tableIndex)
	{
	  System.out.println("2...");
		JComboBox columnList = getColumnList();
		columnList.removeAllItems();

		MsExcel excelFile = new MsExcel(file.getAbsolutePath());
		HSSFWorkbook workbook = excelFile.getWorkbook();
		HSSFSheet sheet = workbook.getSheetAt(tableIndex);
		
		HSSFRow row = MsExcelUtils.getOrCreateRowHeaderIfAbsent(sheet);

		HSSFCell cell = null;
		String label = null;

		Iterator cells = row.cellIterator();
		while(cells.hasNext())
		{
			cell = (HSSFCell) cells.next();
			label = cell.getStringCellValue();
			columnList.addItem(label);
		}

		try
		{
			this.controller.changeWorksheetName( workbook.getSheetName(tableIndex));
		}
		catch(Exception e)
		{
			
		}		
	}

	@Override
	public void setList(File file, int tableIndex, String columnName) 
	{
	  System.out.println("3...");
		try
		{
			this.controller.changeUniqueColumnName(columnName);
		}
		catch(Exception e)
		{
			
		}		
	}

	public void setController(MsExcelUIController controller) {
		this.controller = controller;
	}

	public MsExcelUIController getController() {
		return controller;
	}

	@Override
	public void modelPropertyChange(final PropertyChangeEvent evt) 
	{
		System.out.println("Model changes....");
	}

}

class MsExcelFilter extends FileFilter {

    //Accept all directories and all xls files.
    public boolean accept(File file) 
    {
        if (file.isDirectory())
        	return true;
        
        int pos = file.getName().lastIndexOf(".");
        String ext = file.getName().substring(pos);
        
        if (ext != null && ext.equals(".xls")) 
        	return true;

        return false;
    }

    public String getDescription() 
    {
        return EktooUITranslator.getExcelFileDescription();
    }
}