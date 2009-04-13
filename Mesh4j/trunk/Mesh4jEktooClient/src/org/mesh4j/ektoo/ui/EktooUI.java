/**
 *
 */
package org.mesh4j.ektoo.ui;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.mesh4j.ektoo.controller.EktooUIController;
import org.mesh4j.sync.adapters.msexcel.MsExcel;
import org.mesh4j.sync.adapters.msexcel.MsExcelUtils;

/**
 * @author Asus
 *
 */
/**
 * @author Asus
 *
 */
public class EktooUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private final String SourceOrTargetType = "KML~Sync Server~MS Access~Googll Spread Sheet~MS Excell";//EktooUITranslator.getDataSourceType();

	private JFileChooser chooser = null;// = new JFileChooser();
	private MsExcel sourceMsExcelFile = null;
	private MsExcel targetMsExcelFile = null;

	private File sourceFile = null;
	private File targetFile = null;

	private JPanel jPanel = null;
	private JLayeredPane sourcePane = null;
	private JLayeredPane targetPane = null;
	private JLayeredPane viaPane = null;
	private JLayeredPane typePane = null;
	private JButton btnSync = null;
	private JComboBox sourceType = null;


	private JLabel labelSourceType = null;
	private JComboBox targetType = null;
	private JLabel labelSourceType1 = null;

	private JLabel labelSourceFile = null;
	private JTextField txtSourceFile = null;

	private JLabel labelSourceWorksheet = null;
	private JComboBox listSourceWorksheet = null;


	private JLabel labelSourceWorksheetColumn = null;
	private JComboBox listSourceWorksheetColumn = null;

	private JButton btnSourceFile = null;


	private JRadioButton rbWeb = null;
	private JRadioButton rbSMS = null;
	private JRadioButton rbFile = null;
	private JRadioButton rbSent = null;
	private JRadioButton rbReceive = null;
	private JRadioButton rbSendReceive = null;

	private JLabel labelTargetFile = null;
	private JTextField txtTargetFile = null;
	private JButton btnTargetFile = null;

	private JLabel labelTargetWorksheet = null;
	private JComboBox listTargetWorksheet = null;

	private JLabel labelTargetWorksheetColumn = null;
	private JComboBox listTargetWorksheetColumn = null;

	private ButtonGroup btngSyncVia = new ButtonGroup();
	private ButtonGroup btngSyncType = new ButtonGroup();
	private JLabel txtConsole = null;
	/**
	 * This method initializes this
	 *
	 */
	private void initialize() {
        this.setSize(new Dimension(564, 511));
        this.setContentPane(getJPanel());
        if (chooser == null)
        	chooser = new JFileChooser();
        
        showHide(0,0);
        showHide(0,1);
	}

	/**
	 * This method initializes jPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel()
	{
		if (jPanel == null) {
			txtConsole = new JLabel();
			txtConsole.setBounds(new Rectangle(15, 389, 525, 16));
			txtConsole.setText("");
			jPanel = new JPanel();
			jPanel.setLayout(null);
			jPanel.add(getSourcePane(), null);
			jPanel.add(getTargetPane(), null);
			jPanel.add(getViaPane(), null);
			jPanel.add(getTypePane(), null);
			jPanel.add(getBtnSync(), null);
			jPanel.add(txtConsole, null);
		}
		return jPanel;
	}

	/**
	 * This method initializes sourcePane
	 *
	 * @return javax.swing.JPanel
	 */
	private JLayeredPane getSourcePane() {
		if (sourcePane == null) {
			sourcePane = new JLayeredPane();
			//sourcePane.setLayout(new GridBagLayout());
			sourcePane.setBorder(BorderFactory.createTitledBorder(
		    "Source"));
			sourcePane.setSize(new Dimension(350, 164));
			sourcePane.setLocation(new Point(16, 16));
			sourcePane.add(getSourceType(), null);
			sourcePane.add(getLabelSourceType(), null);
			sourcePane.add(getlabelSourceFile(), null);
			sourcePane.add(getTxtSourceFile(), null);
			sourcePane.add(getlabelSourceWorksheet(), null);
			sourcePane.add(getlistSourceWorksheet(), null);
			sourcePane.add(getlabelSourceWorksheetColumn(), null);
			sourcePane.add(getlistSourceWorksheetColumn(), null);
			sourcePane.add(getBtnSourceFile(), null);
		}
		return sourcePane;
	}

	/**
	 * This method initializes targetPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JLayeredPane getTargetPane()
	{
		if (targetPane == null) {
			labelTargetWorksheetColumn = new JLabel();
			labelTargetWorksheetColumn.setBounds(new Rectangle(16, 122, 56, 16));
			labelTargetWorksheetColumn.setText("Id Column");
			labelTargetWorksheet = new JLabel();
			labelTargetWorksheet.setBounds(new Rectangle(16, 89, 69, 16));
			labelTargetWorksheet.setText("Work Sheet");
			labelTargetFile = new JLabel();
			labelTargetFile.setBounds(new Rectangle(16, 56, 19, 16));
			labelTargetFile.setText("File");
			targetPane = new JLayeredPane();
			targetPane.setBorder(BorderFactory.createTitledBorder(
		    "Target"));
			targetPane.setSize(new Dimension(350, 166));
			targetPane.setLocation(new Point(15, 210));
			targetPane.add(getTargetType(), null);
			targetPane.add(getLabelSourceType1(), null);
			targetPane.add(labelTargetFile, null);
			targetPane.add(getTxtTargetFile(), null);
			targetPane.add(labelTargetWorksheet, null);
			targetPane.add(getlistTargetWorksheet(), null);
			targetPane.add(labelTargetWorksheetColumn, null);
			targetPane.add(getlistTargetWorksheetColumn(), null);
			targetPane.add(getBtnTargetFile(), null);
		}
		return targetPane;
	}

	/**
	 * This method initializes viaPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JLayeredPane getViaPane() {
		if (viaPane == null) {
			viaPane = new JLayeredPane();
			//viaPane.setLayout(new GridBagLayout());
			viaPane.setBorder(BorderFactory.createTitledBorder(
		    "Sync Via"));
			viaPane.setSize(new Dimension(150, 164));
			viaPane.setLocation(new Point(390, 16));
			viaPane.add(getRbWeb(), null);
			viaPane.add(getRbSMS(), null);
			viaPane.add(getRbFile(), null);


		}
		return viaPane;
	}

	/**
	 * This method initializes typePane
	 *
	 * @return javax.swing.JPanel
	 */
	private JLayeredPane getTypePane() {
		if (typePane == null) {
			typePane = new JLayeredPane();
			typePane.setBorder(BorderFactory.createTitledBorder(
		    "Sync Type"));
			typePane.setSize(new Dimension(150, 166));
			typePane.setLocation(new Point(387, 210));
			typePane.add(getRbSent(), null);
			typePane.add(getRbReceive(), null);
			typePane.add(getRbSendReceive(), null);
		}
		return typePane;
	}

	/**
	 * This method initializes btnSync
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getBtnSync() {
		if (btnSync == null) {
			btnSync = new JButton();
			btnSync.setBounds(new Rectangle(219, 429, 127, 28));
			btnSync.setText("Sync Now");
			btnSync.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()

				
					txtConsole.setText("");
					String result = new EktooUIController().sync(sourceFile, (String)listSourceWorksheet.getSelectedItem(), (String)listSourceWorksheetColumn.getSelectedItem(), 
															   targetFile, (String)listTargetWorksheet.getSelectedItem(), (String)listTargetWorksheetColumn.getSelectedItem());
					txtConsole.setText(result);
					//System.out.println("????????->" + test);
					//new EktooUIController().sync(sourceFile, sourceSyncFile, targetFile, targetSyncFile);

				}
			});
		}
		return btnSync;
	}

	/**
	 * This method initializes sourceType
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getSourceType() {
		if (sourceType == null) {
			sourceType = new JComboBox();
			sourceType.setBounds(new Rectangle(118, 22, 194, 20));

			if (SourceOrTargetType != null)
			{
				String[] types = SourceOrTargetType.split("~");

				for(int i=0; i < types.length; i++)
				{
					if (types[i] != null && types[i].length()!= 0)
						sourceType.addItem(types[i]);
				}
			}
			sourceType.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					System.out.println("itemStateChanged()"); // TODO Auto-generated Event stub itemStateChanged()
					int index = sourceType.getSelectedIndex();
					if (index != -1)
						showHide(index, 0);
				}
			});

		}
		return sourceType;
	}

	/**
	 * This method initializes labelSourceType
	 *
	 * @return javax.swing.JLabel
	 */
	private JLabel getLabelSourceType() {
		if (labelSourceType == null) {
			labelSourceType = new JLabel();
			labelSourceType.setBounds(new Rectangle(15, 24, 75, 17));
			labelSourceType.setText("Type");
		}
		return labelSourceType;
	}

	/**
	 * This method initializes targetType
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getTargetType() {
		if (targetType == null) {
			targetType = new JComboBox();
			targetType.setBounds(new Rectangle(118, 22, 194, 20));

			if (SourceOrTargetType != null)
			{
				String[] types = SourceOrTargetType.split("~");

				for(int i=0; i < types.length; i++)
				{
					if (types[i] != null && types[i].length()!= 0)
						targetType.addItem(types[i]);
				}
			}
			targetType.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					System.out.println("itemStateChanged()"); // TODO Auto-generated Event stub itemStateChanged()
					int index = targetType.getSelectedIndex();
					if (index != -1)
						showHide(index, 1);
				}
			});

		}
		return targetType;
	}

	/**
	 * This method initializes labelSourceType1
	 *
	 * @return javax.swing.JLabel
	 */
	private JLabel getLabelSourceType1() {
		if (labelSourceType1 == null) {
			labelSourceType1 = new JLabel();
			labelSourceType1.setBounds(new Rectangle(16, 23, 54, 16));
			labelSourceType1.setText("Type");
		}
		return labelSourceType1;
	}

	/**
	 * This method initializes labelSourceFile
	 *
	 * @return javax.swing.JLabel
	 */
	private JLabel getlabelSourceFile() {
		if (labelSourceFile == null) {
			labelSourceFile = new JLabel();
			labelSourceFile.setBounds(new Rectangle(16, 57, 75, 17));
			labelSourceFile.setText("File");
		}
		return labelSourceFile;
	}

	/**
	 * This method initializes txtSourceFile
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtSourceFile() {
		if (txtSourceFile == null) {
			txtSourceFile = new JTextField();
			txtSourceFile.setBounds(new Rectangle(119, 56, 149, 20));

		}
		return txtSourceFile;
	}

	/**
	 * This method initializes labelSourceWorksheet
	 *
	 * @return javax.swing.JLabel
	 */
	private JLabel getlabelSourceWorksheet() {
		if (labelSourceWorksheet == null) {
			labelSourceWorksheet = new JLabel();
			labelSourceWorksheet.setBounds(new Rectangle(15, 90, 75, 17));
			labelSourceWorksheet.setText("Work Sheet");
		}
		return labelSourceWorksheet;
	}

	/**
	 * This method initializes listSourceWorksheet
	 *
	 * @return javax.swing.JTextField
	 */
	private JComboBox getlistSourceWorksheet() {
		if (listSourceWorksheet == null) {
			listSourceWorksheet = new JComboBox();
			listSourceWorksheet.setBounds(new Rectangle(119, 90, 194, 20));
			listSourceWorksheet.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					System.out.println("itemStateChanged()"); // TODO Auto-generated Event stub itemStateChanged()
					int sheetIndex = listSourceWorksheet.getSelectedIndex();
					if (sheetIndex != -1)
						setColumnList( listSourceWorksheetColumn, sourceMsExcelFile, sheetIndex);
				}
			});
		}
		return listSourceWorksheet;
	}

	/**
	 * This method initializes labelSourceWorksheetColumn
	 *
	 * @return javax.swing.JLabel
	 */
	private JLabel getlabelSourceWorksheetColumn() {
		if (labelSourceWorksheetColumn == null) {
			labelSourceWorksheetColumn = new JLabel();
			labelSourceWorksheetColumn.setBounds(new Rectangle(14, 123, 75, 17));
			labelSourceWorksheetColumn.setText("Id Column");
		}
		return labelSourceWorksheetColumn;
	}

	/**
	 * This method initializes listSourceWorksheetColumn
	 *
	 * @return javax.swing.JTextField
	 */
	private JComboBox getlistSourceWorksheetColumn() {
		if (listSourceWorksheetColumn == null) {
			listSourceWorksheetColumn = new JComboBox();
			listSourceWorksheetColumn.setBounds(new Rectangle(119, 124, 194, 20));
		}
		return listSourceWorksheetColumn;
	}

	/**
	 * This method initializes rbWeb
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getRbWeb() {
		if (rbWeb == null) {
			rbWeb = new JRadioButton();
			rbWeb.setBounds(new Rectangle(18, 26, 95, 24));
			rbWeb.setText("Web");
			btngSyncVia.add(rbWeb);
		}
		return rbWeb;
	}

	/**
	 * This method initializes rbSMS
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getRbSMS() {
		if (rbSMS == null) {
			rbSMS = new JRadioButton();
			rbSMS.setBounds(new Rectangle(18, 49, 92, 24));
			rbSMS.setText("SMS");
			btngSyncVia.add(rbSMS);
		}
		return rbSMS;
	}

	/**
	 * This method initializes rbFile
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getRbFile() {
		if (rbFile == null) {
			rbFile = new JRadioButton();
			rbFile.setBounds(new Rectangle(19, 77, 107, 24));
			rbFile.setText("File");
			rbFile.setSelected(true);
			btngSyncVia.add(rbFile);
		}
		return rbFile;
	}

	/**
	 * This method initializes rbSent
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getRbSent() {
		if (rbSent == null) {
			rbSent = new JRadioButton();
			rbSent.setBounds(new Rectangle(13, 28, 124, 21));
			rbSent.setText("Send");
			btngSyncType.add(rbSent);
		}
		return rbSent;
	}

	/**
	 * This method initializes rbReceive
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getRbReceive() {
		if (rbReceive == null) {
			rbReceive = new JRadioButton();
			rbReceive.setBounds(new Rectangle(13, 53, 124, 21));
			rbReceive.setText("Receive");
			btngSyncType.add(rbReceive);
		}
		return rbReceive;
	}

	/**
	 * This method initializes rbSendReceive
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getRbSendReceive() {
		if (rbSendReceive == null) {
			rbSendReceive = new JRadioButton();
			rbSendReceive.setBounds(new Rectangle(13, 78, 124, 21));
			rbSendReceive.setText("Send & Receive");
			rbSendReceive.setSelected(true);
			btngSyncType.add(rbSendReceive);
		}
		return rbSendReceive;
	}

	/**
	 * This method initializes txtTargetFile
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtTargetFile() {
		if (txtTargetFile == null) {
			txtTargetFile = new JTextField();
			txtTargetFile.setBounds(new Rectangle(118, 57, 152, 20));
		}
		return txtTargetFile;
	}

	/**
	 * This method initializes listTargetWorksheet
	 *
	 * @return javax.swing.JTextField
	 */
	private JComboBox getlistTargetWorksheet() {
		if (listTargetWorksheet == null) {
			listTargetWorksheet = new JComboBox();
			listTargetWorksheet.setBounds(new Rectangle(118, 90, 203, 20));
			listTargetWorksheet.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					System.out.println("itemStateChanged()"); // TODO Auto-generated Event stub itemStateChanged()
					int sheetIndex = listTargetWorksheet.getSelectedIndex();
					if (sheetIndex != -1)
						setColumnList( listTargetWorksheetColumn, targetMsExcelFile, sheetIndex);
				}
			});
		}
		return listTargetWorksheet;
	}

	/**
	 * This method initializes listTargetWorksheetColumn
	 *
	 * @return javax.swing.JTextField
	 */
	private JComboBox getlistTargetWorksheetColumn() {
		if (listTargetWorksheetColumn == null) {
			listTargetWorksheetColumn = new JComboBox();
			listTargetWorksheetColumn.setBounds(new Rectangle(118, 123, 203, 20));
		}
		return listTargetWorksheetColumn;
	}

	/**
	 * This method initializes btnSourceFile
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getBtnSourceFile() {
		if (btnSourceFile == null) {
			btnSourceFile = new JButton();
			btnSourceFile.setBounds(new Rectangle(277, 55, 34, 20));
			btnSourceFile.setText("...");
			btnSourceFile.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
					int returnVal = chooser.showOpenDialog( getJPanel() );
					if(returnVal == JFileChooser.APPROVE_OPTION)
					{
						System.out.println("You chose to open this file: " +
					            chooser.getSelectedFile().getName());
						sourceFile = chooser.getSelectedFile();
						if(sourceFile != null)
						{
							txtSourceFile.setText( sourceFile.getName() );
							sourceMsExcelFile = new MsExcel(sourceFile.getAbsolutePath());

							setWorksheetList( listSourceWorksheet,  sourceMsExcelFile);
						}



					}

				}
			});
		}
		return btnSourceFile;
	}

	/**
	 * This method initializes btnTargetFile
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getBtnTargetFile() {
		if (btnTargetFile == null) {
			btnTargetFile = new JButton();
			btnTargetFile.setBounds(new Rectangle(276, 58, 43, 20));
			btnTargetFile.setText("...");
			btnTargetFile.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()

					int returnVal = chooser.showOpenDialog( getJPanel() );
					if(returnVal == JFileChooser.APPROVE_OPTION)
					{
						System.out.println("You chose to open this file: " +
					            chooser.getSelectedFile().getName());
						targetFile = chooser.getSelectedFile();
						if(targetFile != null)
						{
							txtTargetFile.setText( targetFile.getName() );
							targetMsExcelFile = new MsExcel(targetFile.getAbsolutePath());

							setWorksheetList( listTargetWorksheet,  targetMsExcelFile);
						}



					}


				}
			});

		}
		return btnTargetFile;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				EktooUI thisClass = new EktooUI();
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setVisible(true);
			}
		});
	}

	/**
	 * This is the default constructor
	 */
	public EktooUI() {
		super();
		initialize();
	}

	private void setWorksheetList(JComboBox comp, MsExcel excelFile)
	{
		HSSFWorkbook workbook = excelFile.getWorkbook();
		if(workbook != null)
		{
			int sheetNum = workbook.getNumberOfSheets();
			for(int i=0; i < sheetNum; i++)
			{
				String sheetName = workbook.getSheetName(i);
				if (sheetName != null)
				{
					comp.addItem(sheetName);
				}
			}

		}
	}







	private void setColumnList(JComboBox comp, MsExcel excelFile, int sheetIndex)
	{
		HSSFWorkbook workbook = excelFile.getWorkbook();
		HSSFSheet sheet = workbook.getSheetAt(sheetIndex);
		HSSFRow row = MsExcelUtils.getOrCreateRowHeaderIfAbsent(sheet);

		HSSFCell cell = null;
		String label = null;
		Iterator cells = row.cellIterator();
		while(cells.hasNext())
		{
			cell = (HSSFCell) cells.next();
			label = cell.getStringCellValue();
			comp.addItem(label);
		}

	}


	private void showHide(int index, int direcion)
	{
		switch(index)
		{
			case 0:
				if (direcion == 0)
				{
					labelSourceFile.setVisible(true);
					txtSourceFile.setVisible(true);
					btnSourceFile.setVisible(true);

					labelSourceWorksheet.setVisible(false);
					listSourceWorksheet.setVisible(false);

					labelSourceWorksheetColumn.setVisible(false);
					listSourceWorksheetColumn.setVisible(false);
				}
				else
				{
					labelTargetFile.setVisible(true);
					txtTargetFile.setVisible(true);
					btnTargetFile.setVisible(true);

					labelTargetWorksheet.setVisible(false);
					listTargetWorksheet.setVisible(false);

					labelTargetWorksheetColumn.setVisible(false);
					listTargetWorksheetColumn.setVisible(false);
				}
				break;
			case 1:
				if (direcion == 0)
				{
				labelSourceFile.setVisible(true);
				txtSourceFile.setVisible(true);
				btnSourceFile.setVisible(false);

				labelSourceWorksheet.setVisible(false);
				listSourceWorksheet.setVisible(false);

				labelSourceWorksheetColumn.setVisible(false);
				listSourceWorksheetColumn.setVisible(false);
				}
				else
				{
					labelTargetFile.setVisible(true);
					txtTargetFile.setVisible(true);
					btnTargetFile.setVisible(false);

					labelTargetWorksheet.setVisible(false);
					listTargetWorksheet.setVisible(false);

					labelTargetWorksheetColumn.setVisible(false);
					listTargetWorksheetColumn.setVisible(false);
				}
				break;

			case 2:
			case 3:
			case 4:
				if (direcion == 0)
				{
				labelSourceFile.setVisible(true);
				txtSourceFile.setVisible(true);

				labelSourceWorksheet.setVisible(true);
				listSourceWorksheet.setVisible(true);

				labelSourceWorksheetColumn.setVisible(true);
				listSourceWorksheetColumn.setVisible(true);
				}
				else
				{
					labelTargetFile.setVisible(true);
					txtTargetFile.setVisible(true);

					labelTargetWorksheet.setVisible(true);
					listTargetWorksheet.setVisible(true);

					labelTargetWorksheetColumn.setVisible(true);
					listTargetWorksheetColumn.setVisible(true);

				}
			default:
				break;
		}
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
