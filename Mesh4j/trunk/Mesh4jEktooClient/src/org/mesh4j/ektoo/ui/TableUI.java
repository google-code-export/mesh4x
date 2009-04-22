package org.mesh4j.ektoo.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
/**
 * @author Bhuiyan Mohammad Iklash
 *
 */
public abstract class TableUI extends AbstractUI
{
	private static final long serialVersionUID = 1L;
	private JFileChooser fileChooser = new JFileChooser();

	private File file = null;
	private String table = null;
	private String column = null;

	private JLabel labelFile = null;
	private JTextField txtFile = null;

	private JLabel labelTable = null;
	private JComboBox listTable = null;


	private JLabel labelColumn = null;
	private JComboBox listColumn = null;

	private JButton btnFile = null;

	public abstract void setList(File file);
	public abstract void setList(File file, int tableIndex);
	public abstract void setList(File file, int tableIndex, String columnName);
	

	public TableUI() {
		super();
		initialize();
	}
	public TableUI(String fileLabel, String tableLable, String fieldLabel)
	{
		super();
		initialize();
		setLabelFile(fileLabel);
		setLabelTable(tableLable);
		setLabelColumn(fieldLabel);

	}

	private void initialize() 
	{
		this.setSize(300, 95);
		this.setLayout(null);
		this.setPreferredSize(new Dimension(300, 95));
		this.setBackground(new Color(106, 237, 238));
		this.add(getLabelFile(), null);
		this.add(getTxtFile(), null);
		this.add(getBtnFile(), null);

		this.add(getlabelTable(), null);
		this.add(getTableList(), null);
		this.add(getLabelColumn(), null);
		this.add(getColumnList(), null);
	}

	private JLabel getLabelFile() 
	{
		if (labelFile == null) 
		{
			labelFile = new JLabel();
			labelFile.setText(EktooUITranslator.getFileLabel());
			labelFile.setSize(new Dimension(85, 16));
			labelFile.setPreferredSize(new Dimension(85, 16));
			labelFile.setLocation(new Point(8, 11));
		}
		return labelFile;
	}

	public void setLabelFile(String label)
	{
		if (labelFile != null) {
			labelFile.setText(label);
		}
	}

	public JTextField getTxtFile() {
		if (txtFile == null) {
			txtFile = new JTextField();
			txtFile.setBounds(new Rectangle(99, 8, 149, 20));
		}
		return txtFile;
	}
	// button??
	public JButton getBtnFile()
	{
		if (btnFile == null) {
			btnFile = new JButton();
			btnFile.setText(EktooUITranslator.getBrowseButtonLabel());
			btnFile.setBounds(new Rectangle(259, 8, 34, 20));
			btnFile.addActionListener(new ActionListener() 
			{
				public void actionPerformed(ActionEvent e) 
				{
					int returnVal = getFileChooser().showOpenDialog( btnFile);
					if(returnVal == JFileChooser.APPROVE_OPTION)
					{
						//System.out.println("You chose to open this file: " +
					  //          getFileChooser().getSelectedFile().getName());
						setFile(getFileChooser().getSelectedFile());
						if(getFile() != null)
						{
							txtFile.setText( getFile().getName() );
							setList(getFile());
						}
					}
				}
			});
		}
		return btnFile;
	}
	private JLabel getlabelTable() {
		if (labelTable == null) {
			labelTable = new JLabel();
			labelTable.setText(EktooUITranslator.getTableLabel());
			labelTable.setLocation(new Point(8, 38));
			labelTable.setSize(new Dimension(85, 16));
			labelTable.setPreferredSize(new Dimension(85, 16));
		}
		return labelTable;
	}

	public void setLabelTable(String label) {
		if (labelTable != null) {
			labelTable.setText(label);
		}
	}
	public JComboBox getTableList() 
	{
		if (listTable == null) {
			listTable = new JComboBox();
			listTable.setBounds(new Rectangle(99, 36, 194, 20));
			listTable.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					//System.out.println("getTableList()->itemStateChanged()");
					int sheetIndex = listTable.getSelectedIndex();
					if (sheetIndex != -1)
					{
						table = (String)listTable.getSelectedItem();
						setList(getFile(), sheetIndex);
					}
				}
			});
		}
		return listTable;
	}


	private JLabel getLabelColumn() {
		if (labelColumn == null) {
			labelColumn = new JLabel();
			labelColumn.setText(EktooUITranslator.getFieldLabel());
			labelColumn.setSize(new Dimension(85, 16));
			labelColumn.setPreferredSize(new Dimension(85, 16));
			labelColumn.setLocation(new Point(8, 65));
			
		}
		return labelColumn;
	}

	public void setLabelColumn(String label) {
		if (labelColumn != null) {
			labelColumn.setText(label);
		}
	}



	public JComboBox getColumnList() {
		if (listColumn == null) {
			listColumn = new JComboBox();
			listColumn.setBounds(new Rectangle(99, 64, 194, 20));
			listColumn.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					int tableIndex = listTable.getSelectedIndex();
					if (tableIndex != -1)
					{
						table = (String)listTable.getSelectedItem();
						int columnIndex = listColumn.getSelectedIndex();
						if (columnIndex != -1)
						{
							setList(getFile(), tableIndex, (String)listColumn.getSelectedItem());
						}
					}
				}
			});			
		}
		return listColumn;
	}
	public void setFileChooser(JFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}
	public JFileChooser getFileChooser() {
		if(fileChooser == null)
			fileChooser = new JFileChooser();
		return fileChooser;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public String getTable() {
		return table;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public String getColumn() 
	{
		if (column == null)
		{
			int index = getColumnList().getSelectedIndex();
			if (index != 1)
			{
				column = (String)getColumnList().getSelectedItem();
			}
		}
		return column;
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	public File getFile() {
		return file;
	}

	public void showColumn(boolean bool)
	{
		getLabelColumn().setVisible(bool);
		getColumnList().setVisible(bool);
	}

}
