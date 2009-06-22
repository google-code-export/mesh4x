package org.mesh4j.ektoo.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.controller.MsExcelUIController;
import org.mesh4j.ektoo.tasks.IErrorListener;
import org.mesh4j.ektoo.tasks.OpenFileTask;
import org.mesh4j.ektoo.ui.image.ImageManager;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.ektoo.ui.validator.MsExcelUIValidator;
import org.mesh4j.ektoo.validator.IValidationStatus;
import org.mesh4j.sync.adapters.msexcel.MsExcelSyncAdapterFactory;

public class MsExcelUI extends AbstractUI implements IValidationStatus {
	
	// CONSTANTS
	private static final long serialVersionUID = -5022572211883785527L;
	private static final Log LOGGER = LogFactory.getLog(MsExcelUI.class);

	// MODEL VARIABLES
	private JFileChooser fileChooser = new JFileChooser();

	private String table = null;
	private String column = null;

	private JLabel labelFile = null;
	private JTextField txtFileName = null;

	private JLabel labelTable = null;
	private JComboBox listTable = null;

	private JLabel labelColumn = null;
	private JComboBox listColumn = null;

	private JButton btnFile = null;
	private JButton btnView = null;
	private MsExcelUIController controller;

	// BUSINESS METHODS
	public MsExcelUI(String fileName, MsExcelUIController controller) {
		super();
		initialize();
		
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

	protected void initialize() {
		this.setLayout(null);
		this.setBackground(Color.WHITE);
		this.add(getLabelFile(), null);
		this.add(getTxtFile(), null);
		this.add(getBtnFile(), null);
		this.add(getBtnView(), null);

		this.add(getlabelTable(), null);
		this.add(getTableList(), null);
		this.add(getLabelColumn(), null);
		this.add(getColumnList(), null);
		this.add(getMessagesText(), null);
	}
	
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

	public void setList(String fileName, String sheetName) {
		JComboBox columnList = getColumnList();
		columnList.removeAllItems();

		Set<String> columnHeaderNames = MsExcelSyncAdapterFactory.getColumnHeaderNames(fileName, sheetName);
		for (String columnName : columnHeaderNames) {
			columnList.addItem(columnName);
		}
		
		this.controller.changeWorksheetName(sheetName);
	}

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
	
	private JLabel getLabelFile() {
		if (labelFile == null) {
			labelFile = new JLabel();
			labelFile.setText(EktooUITranslator.getExcelFileLabel());
			labelFile.setSize(new Dimension(85, 16));
			labelFile.setPreferredSize(new Dimension(85, 16));
			labelFile.setLocation(new Point(8, 11));
		}
		return labelFile;
	}

	public String getFilePath() {
		return getTxtFile().getText().trim();
	}

	public JTextField getTxtFile() {
		if (txtFileName == null) {
			txtFileName = new JTextField();
			txtFileName.setBounds(new Rectangle(99, 8, 149, 20));
			txtFileName.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					try {
						txtFileName.setToolTipText(txtFileName.getText());
						setList(txtFileName.getText());
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
						// TODO Handle exception
					}
				}
			});
			txtFileName.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent evt) {
					try {
						txtFileName.setToolTipText(txtFileName.getText());
						setList(txtFileName.getText());
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
						// TODO Handle exception
					}
				}
			});
		}
		return txtFileName;
	}

	public JButton getBtnFile() {
		if (btnFile == null) {
			btnFile = new JButton();
			btnFile.setText(EktooUITranslator.getBrowseButtonLabel());
			btnFile.setToolTipText(EktooUITranslator.getTooltipSeleceDataFile("Excel"));

			btnFile.setBounds(new Rectangle(259, 8, 34, 20));
			btnFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getFileChooser().setSelectedFile(
							new File(txtFileName.getText()));
					int returnVal = getFileChooser().showOpenDialog(btnFile);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File selectedFile = getFileChooser().getSelectedFile();
						if (selectedFile != null) {
							try {
								txtFileName.setText(selectedFile
										.getCanonicalPath());
								txtFileName.setToolTipText(txtFileName
										.getText());
								setList(txtFileName.getText());
							} catch (Exception ex) {
								LOGGER.debug(ex.getMessage(), ex);
							}
						}
					}
				}
			});
		}
		return btnFile;
	}

	public JButton getBtnView() {
		if (btnView == null) {
			btnView = new JButton();
			btnView.setIcon(ImageManager.getViewIcon());
			btnView.setContentAreaFilled(false);
			btnView.setBorderPainted(false);
			btnView.setBorder(new EmptyBorder(0, 0, 0, 0));
			btnView.setBackground(Color.WHITE);
			btnView.setText("");
			btnView.setToolTipText(EktooUITranslator.getTooltipView());
			btnView.setBounds(new Rectangle(299, 8, 34, 40));
			btnView.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFrame frame = MsExcelUI.this.getRootFrame();
					IErrorListener errorListener = MsExcelUI.this.getErrorListener();
					OpenFileTask task = new OpenFileTask(frame, errorListener,
							txtFileName.getText());
					task.execute();
				}
			});
		}
		return btnView;
	}

	protected IErrorListener getErrorListener() {
		return (IErrorListener) getRootFrame();
	}

	protected JLabel getlabelTable() {
		if (labelTable == null) {
			labelTable = new JLabel();
			labelTable.setText(EktooUITranslator.getExcelWorksheetLabel());
			labelTable.setLocation(new Point(8, 38));
			labelTable.setSize(new Dimension(85, 16));
			labelTable.setPreferredSize(new Dimension(85, 16));
		}
		return labelTable;
	}

	public JComboBox getTableList() {
		if (listTable == null) {
			listTable = new JComboBox();
			listTable.setBounds(new Rectangle(99, 36, 194, 20));
			listTable.setToolTipText(EktooUITranslator.getTooltipSelectWorksheet());

			listTable.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					if (!listTable.isEnabled()) {
						return;
					}
					if (evt.getStateChange() == ItemEvent.SELECTED) {
						int sheetIndex = listTable.getSelectedIndex();
						if (sheetIndex != -1) {
							table = (String) listTable.getSelectedItem();
							setList(txtFileName.getText(), table);
						}
					}
				}
			});
		}
		return listTable;
	}

	protected JLabel getLabelColumn() {
		if (labelColumn == null) {
			labelColumn = new JLabel();
			labelColumn.setText(EktooUITranslator.getExcelUniqueColumnLabel());
			labelColumn.setSize(new Dimension(85, 16));
			labelColumn.setPreferredSize(new Dimension(85, 16));
			labelColumn.setLocation(new Point(8, 65));

		}
		return labelColumn;
	}

	public JComboBox getColumnList() {
		if (listColumn == null) {
			listColumn = new JComboBox();
			listColumn.setBounds(new Rectangle(99, 64, 194, 20));
			listColumn.setToolTipText(EktooUITranslator
					.getTooltipIdColumnName());
			listColumn.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					if (evt.getStateChange() == ItemEvent.SELECTED) {
						int tableIndex = listTable.getSelectedIndex();
						if (tableIndex != -1) {
							table = (String) listTable.getSelectedItem();
							int columnIndex = listColumn.getSelectedIndex();
							if (columnIndex != -1) {
								setList(txtFileName.getText(), table,
										(String) listColumn.getSelectedItem());
							}
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
		if (fileChooser == null)
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

	public String getColumn() {
		if (column == null) {
			int index = getColumnList().getSelectedIndex();
			if (index != 1) {
				column = (String) getColumnList().getSelectedItem();
			}
		}
		return column;
	}

	public void showColumn(boolean bool) {
		getLabelColumn().setVisible(bool);
		getColumnList().setVisible(bool);
	}
}