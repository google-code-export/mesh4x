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
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.controller.GSSheetUIController;
import org.mesh4j.ektoo.controller.MySQLUIController;
import org.mesh4j.ektoo.tasks.IErrorListener;
import org.mesh4j.ektoo.tasks.OpenURLTask;
import org.mesh4j.ektoo.ui.component.messagedialog.MessageDialog;
import org.mesh4j.ektoo.ui.image.ImageManager;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.ektoo.ui.validator.GssUIValidator;
import org.mesh4j.ektoo.ui.validator.MySQLConnectionValidator;
import org.mesh4j.ektoo.validator.IValidationStatus;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadsheet;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadsheetUtils;
import org.mesh4j.sync.adapters.googlespreadsheet.IGoogleSpreadSheet;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSCell;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSRow;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSWorksheet;

import com.google.gdata.data.spreadsheet.SpreadsheetEntry;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */
public class GSSheetUI extends AbstractUI  implements IValidationStatus {

	private static final long serialVersionUID = 5090713642670266848L;
	private static final Log LOGGER = LogFactory.getLog(GSSheetUI.class);
	
	// MODEL VARIABLES
	private JLabel labelUser = null;
	private JTextField txtUser = null;

	private JLabel labelPass = null;
	private JPasswordField txtPass = null;

//	private JLabel labelKey = null;
//	private JTextField txtKey = null;

	private JLabel labelName = null;
	private JComboBox listName = null;	
	
	private JLabel labelTable = null;
	private JComboBox listTable = null;

	private JLabel labelColumn = null;
	private JComboBox listColumn = null;
	 
	private JButton btnConnect = null;
	private GSSheetUIController controller = null;
	
	private JButton btnView = null;
	private JTextField txtURL = null;

	// BUSINESS METHODS
	public GSSheetUI(GSSheetUIController controller) {
		super();
		this.controller = controller;
		this.controller.addView(this);
		initialize();
		// TODO (RAJU/SHARIF) remove harcode url
		this.txtURL.setText("http://docs.google.com/");
	}

	private void initialize() {
		this.setLayout(null);
		this.setBackground(Color.WHITE);

		this.add(getUserLabel(), null);
		this.add(getUserText(), null);

		this.add(getPassLabel(), null);
		this.add(getPassText(), null);

//		this.add(getKeyLabel(), null);
//		this.add(getKeyText(), null);
		
		this.add(getNameLabel(), null);
		this.add(getNameList(), null);		
		this.add(getConnectButton(), null);
		
		this.add(getTableLabel(), null);
		this.add(getTableList(), null);
		this.add(getLabelColumn(), null);
		this.add(getColumnList(), null);
		this.add(getURLText(), null);
		this.add(getBtnView(), null);
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
			txtUser.setToolTipText(EktooUITranslator.getTooltipGoogleDocsUsername());
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
			txtPass.setToolTipText(EktooUITranslator.getTooltipGoogleDocsPassword());
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

/*	private JLabel getKeyLabel() {
		if (labelKey == null) {
			labelKey = new JLabel();
			labelKey.setText(EktooUITranslator.getGoogleKeyLabel());
			labelKey.setSize(new Dimension(100, 16));
			labelKey.setPreferredSize(new Dimension(100, 16));
			labelKey.setLocation(new Point(8, 59));
		}
		return labelKey;
	}

	private JTextField getKeyText() {
		if (txtKey == null) {
			txtKey = new JTextField();
			txtKey.setBounds(new Rectangle(101, 55, 155, 20));
			
			txtKey.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          try {
            getController().changeSpreadsheetKey(txtKey.getText());
          } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            // TODO Handle exception
          }
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
	}*/
	

	private JLabel getNameLabel() {
		if (labelName == null) {
			labelName = new JLabel();
			labelName.setText(EktooUITranslator.getGoogleSpreadsheetNameLabel());
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
			listName.setToolTipText(EktooUITranslator.getTooltipGoogleSpreadsheetName());
			listName.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) 
					{
						getController().changeSpreadsheetName(
							(String) listName.getSelectedItem());
						
						int sheetIndex = listName.getSelectedIndex();
	  					if (sheetIndex != -1) 
	  					{
	  						SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
	  							public Void doInBackground() {
	  								setCursor(Cursor
	  										.getPredefinedCursor(Cursor.WAIT_CURSOR));
	  								setWorksheetList(getUser(), getPass(), getSpreadsheetName());
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
				}
			});
		}
		return listName;
	}	
	
	private JButton getConnectButton() 
  {
    if (btnConnect == null) 
    {
      btnConnect = new JButton();
      btnConnect.setBounds(new Rectangle(264, 55, 20, 20));
      btnConnect.setIcon(ImageManager.getDatabaseConnectionIcon());
      btnConnect.setToolTipText(EktooUITranslator.getTooltipFetchSpreadsheets());
      btnConnect.addActionListener(new ActionListener() 
      {
        public void actionPerformed(ActionEvent ae) 
        {
			List<JComponent> uiFieldListForValidation = new ArrayList<JComponent>();
			uiFieldListForValidation.add(getUserText());
			uiFieldListForValidation.add(getPassText());
			
			boolean valid = (new GssUIValidator(GSSheetUI.this,
					controller.getModel(), uiFieldListForValidation)).verify();
			if (valid) {
				
	          System.out.println("1...");
	          SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
	            public Void doInBackground() {
	              setCursor(Cursor
	                  .getPredefinedCursor(Cursor.WAIT_CURSOR));
	              setSpreadsheetList(getUser(), getPass());
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
			listTable.setToolTipText(EktooUITranslator.getTooltipSelectWorksheet());
			
			listTable.addItemListener(new ItemListener() 
			{
				public void itemStateChanged(ItemEvent evt) 
				{
				  if (evt.getStateChange() == ItemEvent.SELECTED) 
				  {
  					getController().changeWorksheetName(
  							(String) listTable.getSelectedItem());
  
  					int sheetIndex = listTable.getSelectedIndex();
  					if (sheetIndex != -1) 
  					{
  						SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
  							public Void doInBackground() {
  								setCursor(Cursor
  										.getPredefinedCursor(Cursor.WAIT_CURSOR));
  								setList(getUser(), getPass(), /*getKey()*/ getSpreadsheetName(),
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
				}
			});

		}
		return listTable;
	}	
	
	private JLabel getLabelColumn() {
		if (labelColumn == null) {
			labelColumn = new JLabel();
			labelColumn.setText(EktooUITranslator.getGoogleWorksheetColumnLabel());
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
			listColumn.setToolTipText(EktooUITranslator.getTooltipIdColumnName());
			listColumn.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) 
				{
				  if (evt.getStateChange() == ItemEvent.SELECTED) 
          {				  
  					getController().changeUniqueColumnPosition(
  							listColumn.getSelectedIndex() + 1);
  					getController().changeUniqueColumnName(
  							(String) listColumn.getSelectedItem());
  					
  					// TODO (NBL) need to improve of adapter creation api
  					getController().changeLastUpdatedColumnPosition( listColumn.getItemCount());
          }
				}
			});
			

		}
		return listColumn;
	}


	public void setSpreadsheetList(String user, String pass) {
	  System.out.println("2...");
    
		JComboBox sheetList = getNameList();
		sheetList.removeAllItems();

		JComboBox worksheetList = getTableList();
		worksheetList.removeAllItems();
		
		JComboBox columnList = getColumnList();
		columnList.removeAllItems();		
		
		try {
			List<SpreadsheetEntry> spreadsheetList = GoogleSpreadsheetUtils
					.getAllSpreadsheet(user, pass);
			for (SpreadsheetEntry spSheet : spreadsheetList) {
				sheetList.addItem(spSheet.getTitle().getPlainText());
			}
		} catch (Exception e) {	
		  e.printStackTrace();
            LOGGER.error(e.getMessage(), e);
            // TODO Handle exception
		}
	}	
	
	
	@SuppressWarnings("unchecked")
	public void setWorksheetList(String user, String pass, /*String key,*/ String spreadsheetName) {
	  System.out.println("3...");
    
		JComboBox sheetList = getTableList();
		sheetList.removeAllItems();
		 
		try {
			IGoogleSpreadSheet spreadsheet = new GoogleSpreadsheet(/*key,*/ spreadsheetName,
					user, pass);
			String sheetName = null;
			for (Entry<String, GSWorksheet> spSheet : spreadsheet
					.getGSSpreadsheet().getGSWorksheets().entrySet()) {

				GSWorksheet<GSRow<GSCell>> workSheet = spSheet.getValue();
				sheetName = workSheet.getName();
				System.out.println("Worksheet->" + sheetName);
				if (sheetName != null) {
					sheetList.addItem(workSheet.getName());
				}
			}
		} catch (Exception e) {	
		  e.printStackTrace();
            LOGGER.error(e.getMessage(), e);
            // TODO Handle exception
		}
	}

	@SuppressWarnings("unchecked")
	public  void setList(String user, String pass, /*String key,*/ String spreadsheetName, String sheetName) {
		JComboBox columnList = getColumnList();
		columnList.removeAllItems();

		IGoogleSpreadSheet spreadsheet = new GoogleSpreadsheet(/*key,*/ spreadsheetName, user, pass);
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

	private JTextField getURLText() {
		if (txtURL == null) {
			txtURL = new JTextField();
			txtURL.setBounds(new Rectangle(0, 140, 400, 20));
			txtURL.setEditable(false);
		}
		return txtURL;
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
			btnView.setBounds(new Rectangle(290, 5, 34, 40));
			btnView.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFrame frame = GSSheetUI.this.getRootFrame();
					String url = txtURL.getText();
					// TODO (RAJU/SHARIF) add to base url the spreadshet data
					
					OpenURLTask task = new OpenURLTask(frame, (IErrorListener)frame, url);
					task.execute();
				}
			});
		}
		return btnView;
	}
	
	// TODO (nobel) improve it
	protected JFrame getRootFrame() {
		return (JFrame)this.getParent().getParent().getParent().getParent().getParent().getParent();
	}
	
	public String getUser() {
		return getUserText().getText();
	}

	public String getPass() {
		return new String(getPassText().getPassword());
	}

//	public String getKey() {
//		return getKeyText().getText();
//	}
	
	public String getSpreadsheetName(){
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

//	public void setKeyLabel(String googleKeyLabel) {
//		getKeyLabel().setText(googleKeyLabel);
//	}
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
  public void modelPropertyChange(final PropertyChangeEvent evt)
  {
//    if ( evt.getPropertyName().equals( GSSheetUIController.SPREADSHEET_KEY_PROPERTY))
//    {
//      String newStringValue = evt.getNewValue().toString();
//      if (!  getKeyText().getText().equals(newStringValue))
//        getKeyText().setText(newStringValue);
//    }
	if ( evt.getPropertyName().equals( GSSheetUIController.SPREADSHEET_NAME_PROPERTY))
    {
      String newStringValue = evt.getNewValue().toString();
      if (!  ((String)getNameList().getSelectedItem()).equals(newStringValue))
    	  getNameList().setSelectedItem(newStringValue);
    }	  
    else if ( evt.getPropertyName().equals( GSSheetUIController.USER_NAME_PROPERTY))
    {
      String newStringValue = evt.getNewValue().toString();
      if (!  getUserText().getText().equals(newStringValue))
        getUserText().setText(newStringValue);
    }
    else if ( evt.getPropertyName().equals( GSSheetUIController.USER_PASSWORD_PROPERTY))
    {
      String newStringValue = evt.getNewValue().toString();
      if (!  new String(getPassText().getPassword()).equals(newStringValue))
        getPassText().setText(newStringValue);
    }
    else if ( evt.getPropertyName().equals( GSSheetUIController.WORKSHEET_NAME_PROPERTY))
    {
      String newStringValue = evt.getNewValue().toString();
      if (!  ((String)getTableList().getSelectedItem()).equals(newStringValue))
        getTableList().setSelectedItem(newStringValue);
    }
    else if ( evt.getPropertyName().equals( GSSheetUIController.UNIQUE_COLUMN_NAME_PROPERTY))
    {
      String newStringValue = evt.getNewValue().toString();
      if (!  ((String)getColumnList().getSelectedItem()).equals(newStringValue))
        getColumnList().setSelectedItem(newStringValue);
    }
    else if ( evt.getPropertyName().equals( GSSheetUIController.UNIQUE_COLUMN_POSITION_PROPERTY))
    {
      String newStringValue = evt.getNewValue().toString();
      if (!( getColumnList().getSelectedIndex() == Integer.parseInt(newStringValue))
    		  && getColumnList().getItemCount() > Integer.parseInt(newStringValue))
        getColumnList().setSelectedIndex(Integer.parseInt(newStringValue));
    }
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
		boolean valid = (new GssUIValidator(this,
				controller.getModel(), null)).verify();
		return valid;
	}
}
