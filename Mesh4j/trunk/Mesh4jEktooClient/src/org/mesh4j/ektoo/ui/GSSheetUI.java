package org.mesh4j.ektoo.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.controller.GSSheetUIController;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.GoogleSpreadsheet;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.IGoogleSpreadSheet;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSCell;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSRow;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSWorksheet;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */
public class GSSheetUI extends JPanel {

	private static final long serialVersionUID = 5090713642670266848L;
	private static final Log LOGGER = LogFactory.getLog(GSSheetUI.class);
	
	// MODEL VARIABLES
	private JLabel labelUser = null;
	private JTextField txtUser = null;

	private JLabel labelPass = null;
	private JPasswordField txtPass = null;

	private JLabel labelKey = null;
	private JTextField txtKey = null;

	private JLabel labelTable = null;
	private JComboBox listTable = null;

	private JLabel labelColumn = null;
	private JComboBox listColumn = null;
	private GSSheetUIController controller = null;

	// BUSINESS METHODS
	public GSSheetUI() {
		super();
		initialize();
	}

	public GSSheetUI(GSSheetUIController controller) {
		super();
		this.controller = controller;
		initialize();
	}

	private void initialize() {
		this.setSize(300, 135);
		this.setLayout(null);
		this.setPreferredSize(new Dimension(300, 95));
		this.setBackground(new Color(106, 237, 238));

		this.add(getUserLabel(), null);
		this.add(getUserText(), null);

		this.add(getPassLabel(), null);
		this.add(getPassText(), null);

		this.add(getKeyLabel(), null);
		this.add(getKeyText(), null);

		this.add(getTableLabel(), null);
		this.add(getTableList(), null);
		this.add(getLabelColumn(), null);
		this.add(getColumnList(), null);

		this.setDefaultValues();
	}

	private void setDefaultValues() {
		//txtUser.setText("gspreadsheet.test@gmail.com");
		//txtPass.setText("java123456");
		// txtKey.setText("peo4fu7AitTo8e3v0D8FCew");
		//txtKey.setText("peo4fu7AitTryKJCgRNloaQ");
	}

	private JLabel getUserLabel() {
		if (labelUser == null) {
			labelUser = new JLabel();
			labelUser.setText(EktooUITranslator.getGoogleUserLabel());
			labelUser.setSize(new Dimension(85, 16));
			labelUser.setPreferredSize(new Dimension(85, 16));
			labelUser.setLocation(new Point(8, 9));
		}
		return labelUser;
	}

	private JTextField getUserText() {
		if (txtUser == null) {
			txtUser = new JTextField();
			txtUser.setBounds(new Rectangle(101, 5, 183, 20));
			txtUser.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					try {
						getController().changeUserName(txtUser.getText());
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
						// TODO Handle exception
					}
				}
			});
			txtUser.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent evt) {
					try {
						getController().changeUserName(txtUser.getText());
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
						// TODO Handle exception
					}
				}
			});
		}
		return txtUser;
	}

	private JLabel getPassLabel() {
		if (labelPass == null) {
			labelPass = new JLabel();
			labelPass.setText(EktooUITranslator.getGooglePasswordLabel());
			labelPass.setSize(new Dimension(85, 16));
			labelPass.setPreferredSize(new Dimension(85, 16));
			labelPass.setLocation(new Point(8, 34));
		}
		return labelPass;
	}

	private JPasswordField getPassText() {
		if (txtPass == null) {
			txtPass = new JPasswordField();
			txtPass.setBounds(new Rectangle(101, 30, 183, 20));
			txtPass.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					try {
						getController().changeUserPassword(
								new String(txtPass.getPassword()));
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
						// TODO Handle exception
					}
				}
			});
			txtPass.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent evt) {
					try {
						getController().changeUserPassword(
								new String(txtPass.getPassword()));
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
						// TODO Handle exception
					}
				}
			});
		}
		return txtPass;
	}

	private JLabel getKeyLabel() {
		if (labelKey == null) {
			labelKey = new JLabel();
			labelKey.setText(EktooUITranslator.getGoogleKeyLabel());
			labelKey.setSize(new Dimension(85, 16));
			labelKey.setPreferredSize(new Dimension(85, 16));
			labelKey.setLocation(new Point(8, 59));
		}
		return labelKey;
	}

	private JTextField getKeyText() {
		if (txtKey == null) {
			txtKey = new JTextField();
			txtKey.setBounds(new Rectangle(101, 55, 183, 20));
			txtKey.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					try {
						getController().changeSpreadsheetKey(txtKey.getText());
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
						// TODO Handle exception
					}

					SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
						public Void doInBackground() {
							setCursor(Cursor
									.getPredefinedCursor(Cursor.WAIT_CURSOR));
							setList(getUser(), getPass(), getKey());
							return null;
						}

						public void done() {
							setCursor(Cursor
									.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						}
					};
					worker.execute();
				}
			});

			txtKey.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent evt) {
					try {
						getController().changeSpreadsheetKey(txtKey.getText());
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
						// TODO Handle exception
					}
				}
			});
		}
		return txtKey;
	}

	private JLabel getTableLabel() {
		if (labelTable == null) {
			labelTable = new JLabel();
			labelTable.setText(EktooUITranslator.getGoogleWorksheetLabel());
			labelTable.setLocation(new Point(8, 84));
			labelTable.setSize(new Dimension(85, 16));
			labelTable.setPreferredSize(new Dimension(85, 16));
		}
		return labelTable;
	}

	public JComboBox getTableList() {
		if (listTable == null) {
			listTable = new JComboBox();

			listTable.setBounds(new Rectangle(101, 80, 183, 20));

			listTable.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					getController().changeWorksheetName(
							(String) listTable.getSelectedItem());

					int sheetIndex = listTable.getSelectedIndex();
					if (sheetIndex != -1) {
						SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
							public Void doInBackground() {
								setCursor(Cursor
										.getPredefinedCursor(Cursor.WAIT_CURSOR));
								setList(getUser(), getPass(), getKey(),
										(String) listTable.getSelectedItem());
								return null;
							}

							public void done() {
								setCursor(Cursor
										.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
							}
						};
						worker.execute();
					}
				}
			});

		}
		return listTable;
	}

	private JLabel getLabelColumn() {
		if (labelColumn == null) {
			labelColumn = new JLabel();
			labelColumn.setText(EktooUITranslator
					.getGoogleWorksheetColumnLabel());
			labelColumn.setSize(new Dimension(85, 16));
			labelColumn.setPreferredSize(new Dimension(85, 16));
			labelColumn.setLocation(new Point(8, 109));
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
			listColumn.setBounds(new Rectangle(101, 105, 183, 20));
			listColumn.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					getController().changeUniqueColumnPosition(
							listColumn.getSelectedIndex());
					getController().changeUniqueColumnName(
							(String) listColumn.getSelectedItem());
				}
			});

		}
		return listColumn;
	}

	@SuppressWarnings("unchecked")
	public void setList(String user, String pass, String key) {
		JComboBox sheetList = getTableList();
		sheetList.removeAllItems();

		try {
			IGoogleSpreadSheet spreadsheet = new GoogleSpreadsheet(key, user,
					pass);
			String sheetName = null;
			for (Entry<String, GSWorksheet> spSheet : spreadsheet
					.getGSSpreadsheet().getGSWorksheets().entrySet()) {

				GSWorksheet<GSRow<GSCell>> workSheet = spSheet.getValue();
				sheetName = workSheet.getName();
				if (sheetName != null) {
					sheetList.addItem(workSheet.getName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void setList(String user, String pass, String key, String sheetName) {
		JComboBox columnList = getColumnList();
		columnList.removeAllItems();

		IGoogleSpreadSheet spreadsheet = new GoogleSpreadsheet(key, user, pass);
		GSWorksheet<GSRow<GSCell>> workSheet = spreadsheet.getGSSpreadsheet()
				.getGSWorksheetBySheetName(sheetName);
		String columnName = null;
		for (Map.Entry<String, GSRow<GSCell>> gsRowMap : workSheet.getGSRows()
				.entrySet()) {
			GSRow<GSCell> row = gsRowMap.getValue();
			for (Map.Entry<String, GSCell> cell : row.getGSCells().entrySet()) {
				columnName = cell.getValue().getCellValue();

				if (columnName != null) {
					columnList.addItem(columnName);
				}
			}
			break;
		}
	}

	public String getUser() {
		return getUserText().getText();
	}

	public String getPass() {
		return new String(getPassText().getPassword());
	}

	public String getKey() {
		return getKeyText().getText();
	}

	public String getSheet() {
		return (String) getTableList().getSelectedItem();
	}

	public String getColumn() {
		return (String) getColumnList().getSelectedItem();
	}

	public int getColumnPosition() {
		return (int) getColumnList().getSelectedIndex() + 1;
	}

	public int getUpdateColumnPosition() {
		return (int) getColumnList().getItemCount() + 1;
	}

	public void setController(GSSheetUIController controller) {
		this.controller = controller;
	}

	public GSSheetUIController getController() {
		return controller;
	}

	public void setUserLabel(String googleUserLabel) {
		getUserLabel().setText(googleUserLabel);
	}

	public void setPasswordLabel(String googlePasswordLabel) {
		getPassLabel().setText(googlePasswordLabel);
	}

	public void setKeyLabel(String googleKeyLabel) {
		getKeyLabel().setText(googleKeyLabel);
	}

	public void WorksheetLabel(String googleWSorksheetLabel) {
		getTableLabel().setText(googleWSorksheetLabel);
	}

	public void setUniqueColumnLabel(String uniqueColumnNameLabel) {
		getLabelColumn().setText(uniqueColumnNameLabel);
	}

}
