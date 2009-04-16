package org.mesh4j.ektoo.ui;

import java.io.File;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.sync.adapters.msexcel.MsExcel;
import org.mesh4j.sync.adapters.msexcel.MsExcelUtils;

public class MsExcelUI extends TableUI
{

	private static final long serialVersionUID = -5022572211883785527L;

	private JFileChooser chooser = null;// = new JFileChooser();

	/**
	 * This method initializes
	 *
	 */
	public MsExcelUI() {
		super();
		initialize();
	}

	public MsExcelUI(String fileLabel, String tableLable, String fieldLabel) {
		super(fileLabel, tableLable, fieldLabel);
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 */
	private void initialize()
	{
		this.getFileChooser().setDialogTitle(EktooUITranslator.getSelectExcel());
		this.getFileChooser().setAcceptAllFileFilterUsed(false);
		this.getFileChooser().addChoosableFileFilter(new MsExcelFilter());
	}

	@Override
	public void setList(File file)
	{
		JComboBox sheetList = getTableList();

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
	}

	@Override
	public void setList(File file, int tableIndex)
	{
		JComboBox sheetList = getColumnList();

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
			sheetList.addItem(label);
		}
	}
}  //  @jve:decl-index=0:visual-constraint="-4,-28"


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
        return EktooUITranslator.getReturnExcel();
    }
}