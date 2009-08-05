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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.controller.GSSheetUIController;
import org.mesh4j.ektoo.tasks.IErrorListener;
import org.mesh4j.ektoo.tasks.OpenURLTask;
import org.mesh4j.ektoo.ui.component.messagedialog.MessageDialog;
import org.mesh4j.ektoo.ui.image.ImageManager;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.ektoo.ui.validator.GssUIValidator;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadsheet;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadsheetUtils;
import org.mesh4j.sync.adapters.googlespreadsheet.IGoogleSpreadSheet;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSCell;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSRow;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSWorksheet;

import com.google.gdata.client.GoogleService.InvalidCredentialsException;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;

public class GSSheetUI extends AbstractUI {

	private static final long serialVersionUID = 5090713642670266848L;
	private static final Log LOGGER = LogFactory.getLog(GSSheetUI.class);

	// MODEL VARIABLES
	private IGoogleSpreadSheet spreadsheet = null;
	
	private JLabel labelUser = null;
	private JTextField txtUser = null;

	private JLabel labelPass = null;
	private JPasswordField txtPass = null;

	private JLabel labelName = null;
	private JComboBox listName = null;

	private JLabel labelTable = null;
	private JComboBox listTable = null;

	private JLabel labelColumn = null;
	private JComboBox listColumn = null;

	private JButton btnConnect = null;
	
	private String googleURL = "";
	
	private int newSpreadsheetNameIndex = -1;

	// BUSINESS METHODS
	public GSSheetUI(GSSheetUIController controller, String googleURL) {
		super(controller);
		this.googleURL = googleURL;
		initialize();
		this.setMessageText(googleURL);
	}

	private void initialize() {
		this.setLayout(null);
		this.setBackground(Color.WHITE);

		this.add(getUserLabel(), null);
		this.add(getUserText(), null);

		this.add(getPassLabel(), null);
		this.add(getPassText(), null);

		this.add(getNameLabel(), null);
		this.add(getNameList(), null);
		this.add(getConnectButton(), null);

		this.add(getTableLabel(), null);
		this.add(getTableList(), null);
		this.add(getLabelColumn(), null);
		this.add(getColumnList(), null);
		this.add(getMessagesText(), null);
		this.add(getBtnView(), null);
		this.add(getConflictsButton());
		this.add(getSchemaViewButton(), null);
		this.add(getMappingsButton());
	}

	public int getNewSpreadsheetNameIndex() {
		return newSpreadsheetNameIndex;
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

	public JTextField getUserText() {
		if (txtUser == null) {
			txtUser = new JTextField();
			txtUser.setBounds(new Rectangle(101, 5, 183, 20));
			txtUser.setToolTipText(EktooUITranslator
					.getTooltipGoogleDocsUsername());
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

	public JPasswordField getPassText() {
		if (txtPass == null) {
			txtPass = new JPasswordField();
			txtPass.setToolTipText(EktooUITranslator
					.getTooltipGoogleDocsPassword());
			txtPass.setBounds(new Rectangle(101, 30, 183, 20));
			txtPass.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					try {
						getController().changeUserPassword(new String(txtPass.getPassword()));
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
						// TODO Handle exception
					}
				}
			});
			txtPass.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent evt) {
					try {
						getController().changeUserPassword(new String(txtPass.getPassword()));
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
						// TODO Handle exception
					}
				}
			});
		}
		return txtPass;
	}

	/*
	 * private JLabel getKeyLabel() { if (labelKey == null) { labelKey = new
	 * JLabel(); labelKey.setText(EktooUITranslator.getGoogleKeyLabel());
	 * labelKey.setSize(new Dimension(100, 16)); labelKey.setPreferredSize(new
	 * Dimension(100, 16)); labelKey.setLocation(new Point(8, 59)); } return
	 * labelKey; }
	 * 
	 * private JTextField getKeyText() { if (txtKey == null) { txtKey = new
	 * JTextField(); txtKey.setBounds(new Rectangle(101, 55, 155, 20));
	 * 
	 * txtKey.addActionListener(new ActionListener() { public void
	 * actionPerformed(ActionEvent evt) { try {
	 * getController().changeSpreadsheetKey(txtKey.getText()); } catch
	 * (Exception e) { LOGGER.error(e.getMessage(), e); // TODO Handle exception
	 * } } }); txtKey.addFocusListener(new FocusAdapter() { public void
	 * focusLost(FocusEvent evt) { try {
	 * getController().changeSpreadsheetKey(txtKey.getText()); } catch
	 * (Exception e) { LOGGER.error(e.getMessage(), e); // TODO Handle exception
	 * } } }); } return txtKey; }
	 */

	private JLabel getNameLabel() {
		if (labelName == null) {
			labelName = new JLabel();
			labelName
					.setText(EktooUITranslator.getGoogleSpreadsheetNameLabel());
			labelName.setLocation(new Point(8, 59));
			labelName.setSize(new Dimension(85, 16));
			labelName.setPreferredSize(new Dimension(85, 16));
		}
		return labelName;
	}

	public JComboBox getNameList() {
		if (listName == null) {
			listName = new JComboBox();
			listName.setBounds(new Rectangle(101, 55, 160, 20));
			listName.setToolTipText(EktooUITranslator
					.getTooltipGoogleSpreadsheetName());
			listName.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						getController().changeSpreadsheetName(
								(String) listName.getSelectedItem());
						
						if(((String) listName.getSelectedItem()).equals(DROPDOWN_CREATE_NEW_ITEM)){
							String newSpreadsheetName = MessageDialog.showInputDialogue(getRootFrame(), 
									EktooUITranslator.getMessageNewSpreadsheetName());
							if(newSpreadsheetName == null){
								listName.setSelectedIndex(0);
							}else{
								if(!isSpreadsheetNameUnique(newSpreadsheetName)){
									MessageDialog.showErrorMessage(getRootFrame(), EktooUITranslator.
											getErrorSpreadsheetNameAlreadyExists(newSpreadsheetName));
									listName.setSelectedIndex(0);
								}else
									addNewSpreadsheetNameInCombo(newSpreadsheetName);
							}
							//return;
						}
						
						if(newSpreadsheetNameIndex != -1 && newSpreadsheetNameIndex == listName.getSelectedIndex()){
							//listName.setEditable(true);
							getMe().remove(getTableLabel());
							getMe().remove(getTableList());
							getMe().remove(getLabelColumn());
							getMe().remove(getColumnList());
							getMe().updateUI();
							return;
						}else{
							//listName.setEditable(false);
							if(! Arrays.asList(getMe().getComponents()).contains(getTableLabel()) ){
								getMe().add(getTableLabel());
								getMe().add(getTableList());
								getMe().add(getLabelColumn());
								getMe().add(getColumnList());
								getMe().updateUI();
							}
						}

						int sheetIndex = listName.getSelectedIndex();
						if (sheetIndex != 0) {
							
							LOGGER.info("Populating worksheet list...");
							spreadsheet = null;
							
							SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
								public Void doInBackground() {
//									setCursor(Cursor
//											.getPredefinedCursor(Cursor.WAIT_CURSOR));
									freezeUI(true);
									
									setWorksheetList(getUser(), getPass(),
											getSpreadsheetName());
									return null;
								}

								public void done() {
//									setCursor(Cursor
//											.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
									freezeUI(false);
								}
							};
							worker.execute();
						}else{
							getTableList().removeAllItems();
							getColumnList().removeAllItems();
						}
					}
				}
			});
		}
		return listName;
	}

	protected boolean isSpreadsheetNameUnique(String newSpreadsheetName) {
		int itemCount = listName.getItemCount();
		for(int i=0; i < itemCount; i++){
			if (newSpreadsheetName.equals((String)listName.getItemAt(i)))
				return false;
		}
		return true;
	}

	private void addNewSpreadsheetNameInCombo(String newSpreadsheetName) {
		LOGGER.debug("adding new item in dropdown...");
		ItemListener[] tmp = listName.getItemListeners();
		
		listName.removeItemListener(tmp[0]);
				
		if(newSpreadsheetNameIndex != -1) //remove old one if any
			listName.removeItemAt(newSpreadsheetNameIndex); 

		listName.removeItem(DROPDOWN_CREATE_NEW_ITEM);
		listName.addItem(newSpreadsheetName);
		newSpreadsheetNameIndex = listName.getItemCount() - 1;
		listName.setSelectedIndex(newSpreadsheetNameIndex);
		listName.addItem(DROPDOWN_CREATE_NEW_ITEM);
		getController().changeSpreadsheetName((String) listName.getSelectedItem());
		listName.addItemListener(tmp[0]);
	}	
	
	public JButton getConnectButton() {
		if (btnConnect == null) {
			btnConnect = new JButton();
			btnConnect.setBounds(new Rectangle(264, 55, 20, 20));
			btnConnect.setIcon(ImageManager.getDatabaseConnectionIcon());
			btnConnect.setToolTipText(EktooUITranslator
					.getTooltipFetchSpreadsheets());
			btnConnect.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					List<JComponent> uiFieldListForValidation = new ArrayList<JComponent>();
					uiFieldListForValidation.add(getUserText());
					uiFieldListForValidation.add(getPassText());

					boolean valid = (new GssUIValidator(GSSheetUI.this,
							controller.getModel(), uiFieldListForValidation))
							.verify();
					if (valid) {

						//System.out.println("1...");
						LOGGER.info("Populating spreadsheet list...");
						spreadsheet = null;
						
						SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
							public Void doInBackground() {
//								setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
								freezeUI(true);
								
								try{
									setSpreadsheetList(getUser(), getPass());
								}catch (Exception e) {
									if (e.getCause() instanceof InvalidCredentialsException){
										MessageDialog.showErrorMessage(getRootFrame(), EktooUITranslator.getErrorInvalidCredentials());
									}
									LOGGER.error(e.getMessage(), e);
									return null;
								}
								return null;
							}

							public void done() {
//								setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
								freezeUI(false);
							}
						};
						worker.execute();

					}
				}
			});
		}
		return btnConnect;
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
			listTable.setToolTipText(EktooUITranslator
					.getTooltipSelectWorksheet());

			listTable.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					if (evt.getStateChange() == ItemEvent.SELECTED) {
						getController().changeWorksheetName(
								(String) listTable.getSelectedItem());

						int sheetIndex = listTable.getSelectedIndex();
						
						if (sheetIndex != 0) {
							
							LOGGER.info("Populating column list...");
							
							SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
								public Void doInBackground() {
//									setCursor(Cursor
//											.getPredefinedCursor(Cursor.WAIT_CURSOR));
									freezeUI(true);
									setColumnList(getUser(), getPass(), /* getKey() */
											getSpreadsheetName(),
											(String) listTable
													.getSelectedItem());
									return null;
								}

								public void done() {
//									setCursor(Cursor
//											.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
									freezeUI(false);
								}
							};
							worker.execute();
						}else{
							getColumnList().removeAllItems();
						}
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
			listColumn.setToolTipText(EktooUITranslator
					.getTooltipIdColumnName());
			listColumn.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					if (evt.getStateChange() == ItemEvent.SELECTED) {
						
						int tableIndex = listTable.getSelectedIndex();
						if (tableIndex != -1) {
							
							int columnIndex = listColumn.getSelectedIndex();
							if (columnIndex != -1) {
								getController().changeUniqueColumnNames(new String[]{
										(String) listColumn.getSelectedItem()});
								
							}	
						}
					}
				}
			});

		}
		return listColumn;
	}


	public void setSpreadsheetList(String user, String pass) throws Exception {
		JComboBox sheetList = getNameList();
		sheetList.removeAllItems();

		JComboBox worksheetList = getTableList();
		worksheetList.removeAllItems();

		JComboBox columnList = getColumnList();
		columnList.removeAllItems();
		
		newSpreadsheetNameIndex = -1;

		try {
			List<SpreadsheetEntry> spreadsheetList = GoogleSpreadsheetUtils
					.getAllSpreadsheet(user, pass);
			
			sheetList.addItem(DROPDOWN_SELECT_ITEM);
			
			for (SpreadsheetEntry spSheet : spreadsheetList) {
				sheetList.addItem(spSheet.getTitle().getPlainText());
			}
			
			if(this.isUIForTarget())
				sheetList.addItem(DROPDOWN_CREATE_NEW_ITEM);
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			MessageDialog.showErrorMessage(getRootFrame(), e.getMessage());
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public void setWorksheetList(String user, String pass, String spreadsheetName) {
		JComboBox sheetList = getTableList();
		sheetList.removeAllItems();

		JComboBox columnList = getColumnList();
		columnList.removeAllItems();

		try {
			if(spreadsheet == null) {
				spreadsheet = new GoogleSpreadsheet(spreadsheetName, user, pass);
				getController().changeGSpreadsheet(spreadsheet);
			}
			
			String sheetName = null;
			
			if( spreadsheet.getGSSpreadsheet().getGSWorksheets().entrySet().size() > 0)
				sheetList.addItem(DROPDOWN_SELECT_ITEM);
			
			for (Entry<String, GSWorksheet> spSheet : spreadsheet
					.getGSSpreadsheet().getGSWorksheets().entrySet()) {

				GSWorksheet<GSRow<GSCell>> workSheet = spSheet.getValue();
				sheetName = workSheet.getName();
				if (sheetName != null && !sheetName.endsWith("_sync")) {
					sheetList.addItem(workSheet.getName());
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			MessageDialog.showErrorMessage(getRootFrame(), e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public void setColumnList(String user, String pass, String spreadsheetName, String sheetName) {
		JComboBox columnList = getColumnList();
		columnList.removeAllItems();

		if(spreadsheet == null) {
			spreadsheet = new GoogleSpreadsheet(spreadsheetName, user, pass);
			getController().changeGSpreadsheet(spreadsheet);
		}
		
		GSWorksheet<GSRow<GSCell>> workSheet = spreadsheet.getGSSpreadsheet()
				.getGSWorksheetBySheetName(sheetName);
		
		GSRow<GSCell> headerRow = workSheet.getGSRow(1);
		
//		if(headerRow.getGSCells().size() > 0)
//			columnList.addItem(DROPDOWN_SELECT_ITEM);
		
		for(String columnName : headerRow.getGSCells().keySet()){
			columnList.addItem(columnName);		
		}
	}

	
	private JButton getBtnView() {
		getViewButton().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFrame frame = GSSheetUI.this.getRootFrame();
					OpenURLTask task = new OpenURLTask(frame, (IErrorListener) frame, googleURL);
					task.execute();
				}
			});
		return getViewButton();
	}

	public String getUser() {
		return getUserText().getText();
	}

	public String getPass() {
		return new String(getPassText().getPassword());
	}

	public String getSpreadsheetName() {
		return (String) getNameList().getSelectedItem();
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

	public GSSheetUIController getController() {
		return (GSSheetUIController)controller;
	}

	public void setUserLabel(String googleUserLabel) {
		getUserLabel().setText(googleUserLabel);
	}

	public void setPasswordLabel(String googlePasswordLabel) {
		getPassLabel().setText(googlePasswordLabel);
	}

	public void setNameLabel(String googleNameLabel) {
		getNameLabel().setText(googleNameLabel);
	}

	public void WorksheetLabel(String googleWSorksheetLabel) {
		getTableLabel().setText(googleWSorksheetLabel);
	}

	public void setUniqueColumnLabel(String uniqueColumnNameLabel) {
		getLabelColumn().setText(uniqueColumnNameLabel);
	}

	@Override
	public void modelPropertyChange(final PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(
				GSSheetUIController.SPREADSHEET_NAME_PROPERTY)) {
			String newStringValue = evt.getNewValue().toString();
			if (!((String) getNameList().getSelectedItem())
					.equals(newStringValue))
				getNameList().setSelectedItem(newStringValue);
		} else if (evt.getPropertyName().equals(
				GSSheetUIController.USER_NAME_PROPERTY)) {
			String newStringValue = evt.getNewValue().toString();
			if (!getUserText().getText().equals(newStringValue))
				getUserText().setText(newStringValue);
		} else if (evt.getPropertyName().equals(
				GSSheetUIController.USER_PASSWORD_PROPERTY)) {
			String newStringValue = evt.getNewValue().toString();
			if (!new String(getPassText().getPassword()).equals(newStringValue))
				getPassText().setText(newStringValue);
		} else if (evt.getPropertyName().equals(
				GSSheetUIController.WORKSHEET_NAME_PROPERTY)) {
			String newStringValue = evt.getNewValue().toString();
			if (!((String) getTableList().getSelectedItem())
					.equals(newStringValue))
				getTableList().setSelectedItem(newStringValue);
		} else if (evt.getPropertyName().equals(
				GSSheetUIController.UNIQUE_COLUMN_NAME_PROPERTY)) {
			String newStringValue = evt.getNewValue().toString();
			if (!((String) getColumnList().getSelectedItem())
					.equals(newStringValue))
				getColumnList().setSelectedItem(newStringValue);
		}
	}

	@Override
	public boolean verify() {
		List<JComponent> uiFieldListForValidation = new ArrayList<JComponent>();
		uiFieldListForValidation.add(getUserText());
		uiFieldListForValidation.add(getPassText());
		uiFieldListForValidation.add(getNameList());
		if(newSpreadsheetNameIndex == -1){
			uiFieldListForValidation.add(getTableList());
			uiFieldListForValidation.add(getColumnList());
		}
		
		boolean valid = (new GssUIValidator(this, controller.getModel(), uiFieldListForValidation))
				.verify();
		return valid;
	}

	

}
