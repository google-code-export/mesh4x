package org.mesh4j.meshes.ui.wizard;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.mesh4j.meshes.controller.CreateMeshWizardController;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadsheet;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadsheetUtils;
import org.mesh4j.sync.adapters.googlespreadsheet.IGoogleSpreadSheet;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSCell;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSRow;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSWorksheet;

import com.google.gdata.data.spreadsheet.SpreadsheetEntry;

public class GSSheetConfigPanel extends JPanel {
	
	private static final long serialVersionUID = 7546728738425169284L;
	private static final Logger LOGGER = Logger.getLogger(GSSheetConfigPanel.class);

	private CreateMeshWizardController controller;
	
	private JTextField userTextField;
	private JTextField passwordTextField;
	private JComboBox spreadsheetComboBox;
	private JComboBox worksheetComboBox;
	private JComboBox uniqueColumnComboBox;
	private JButton fetchButton;
	
	private IGoogleSpreadSheet spreadsheet;
	
	public GSSheetConfigPanel(CreateMeshWizardController controller) {
		super(new MigLayout("insets 10"));
		this.controller = controller;
		initComponents();
	}
	
	private void initComponents() {
		setSize(550, 350);
		
		JLabel titleLabel = new JLabel("Add Google Spreadsheet");
		add(titleLabel, "span, wrap 30");
		
		JLabel userLabel = new JLabel("User:");
		userTextField = new JTextField();
		userTextField.setText("gspreadsheet.test@gmail.com"); // TODO remove this
		add(userLabel, "gapright 10");
		add(userTextField, "pushx, growx, span, wrap 10");
		
		JLabel passwordLabel = new JLabel("Password:");
		passwordTextField = new JTextField();
		passwordTextField.setText("java123456"); // TODO remove this
		add(passwordLabel, "gapright 10");
		add(passwordTextField, "pushx, growx, span, wrap 10");
		
		JLabel spreadsheetLabel = new JLabel("Spreadsheet:");
		spreadsheetComboBox = new JComboBox();
		fetchButton = new JButton();
		add(spreadsheetLabel, "gapright 10");
		add(spreadsheetComboBox, "pushx, growx");
		add(fetchButton, "wrap 10");
		
		JLabel worksheetLabel = new JLabel("Worksheet:");
		worksheetComboBox = new JComboBox();
		add(worksheetLabel, "gapright 10");
		add(worksheetComboBox, "pushx, growx, span, wrap 10");
		
		JLabel uniqueColumnLabel = new JLabel("Unique Column:");
		uniqueColumnComboBox = new JComboBox();
		add(uniqueColumnLabel, "gapright 10");
		add(uniqueColumnComboBox, "pushx, growx, span");
		
		userTextField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				userTextFieldFocusLost(e);
			}
		});
		
		passwordTextField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				passwordTextFieldFocusLost(e);
			}
		});
		
		fetchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fetchButtonActionPerformed(e);
			}
		});
		
		spreadsheetComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				spreadsheetComboBoxItemStateChanged(e);
			}
		});
		
		worksheetComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				worksheetComboBoxItemStateChanged(e);
			}
		});
		
		uniqueColumnComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				uniqueColumnComboBoxItemStateChanged(e);
			}
		});
	}
	
	private void userTextFieldFocusLost(FocusEvent e) {
		String userName = userTextField.getText();
		//controller.changeGSSheetUserName(userName);
	}
	
	private void passwordTextFieldFocusLost(FocusEvent e) {
		String password = passwordTextField.getText();
		//controller.changeGSSheetPassword(password);
	}
	
	private void fetchButtonActionPerformed(ActionEvent e) {
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				freezeUI(true);
				
				fillSpreadsheetComboBox();
				
				return null;
			}
			
			@Override
			protected void done() {
				freezeUI(false);
			}
		};
		
		worker.execute();
	}
	
	private void spreadsheetComboBoxItemStateChanged(ItemEvent e) {
		final String spreadsheetName = (String)e.getItem();
		
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				freezeUI(true);
				
				fillWorksheetComboBox(spreadsheetName);
				
				return null;
			}
			
			@Override
			protected void done() {
				freezeUI(false);
			}
		};
		
		//controller.changeGSSheetSpreadsheetName(spreadsheetName);
		
		worker.execute();
	}
	
	private void worksheetComboBoxItemStateChanged(ItemEvent e) {
		final String worksheetName = (String)e.getItem();
		
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				freezeUI(true);
				
				fillUniqueColumnComboBox(worksheetName);
				
				return null;
			}
			
			@Override
			protected void done() {
				freezeUI(false);
			}
		};
		
		//controller.changeGSSheetWorksheetName(worksheetName);
		
		worker.execute();
	}
	
	private void uniqueColumnComboBoxItemStateChanged(ItemEvent e) {
		String uniqueColumnName = (String)e.getItem();
		//controller.changeGSSheetUniqueColumnName(uniqueColumnName);
	}
	
	private void fillSpreadsheetComboBox() {
		spreadsheetComboBox.removeAllItems();
		worksheetComboBox.removeAllItems();
		uniqueColumnComboBox.removeAllItems();
		
		String user = userTextField.getText();
		String pass = passwordTextField.getText();
		
		try {
			List<SpreadsheetEntry> spreadsheetList = GoogleSpreadsheetUtils.getAllSpreadsheet(user, pass);
			
			for (SpreadsheetEntry entry : spreadsheetList) {
				spreadsheetComboBox.addItem(entry.getTitle().getPlainText());
			}
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void fillWorksheetComboBox(String spreadsheetName) {
		worksheetComboBox.removeAllItems();
		uniqueColumnComboBox.removeAllItems();
		
		String user = userTextField.getText();
		String pass = passwordTextField.getText();
		
		spreadsheet = new GoogleSpreadsheet(spreadsheetName, user, pass);
		
		for (Entry<String, GSWorksheet> spSheet : spreadsheet.getGSSpreadsheet().getGSWorksheets().entrySet()) {
			GSWorksheet<GSRow<GSCell>> workSheet = spSheet.getValue();
			String worksheetName = workSheet.getName();
			if (worksheetName != null && !worksheetName.endsWith("_sync")) {
				worksheetComboBox.addItem(worksheetName);
			}
		}

	}
	
	@SuppressWarnings("unchecked")
	private void fillUniqueColumnComboBox(String worksheetName) {
		uniqueColumnComboBox.removeAllItems();
		
		String user = userTextField.getText();
		String pass = passwordTextField.getText();
		String spreadsheetName = (String) spreadsheetComboBox.getSelectedItem();
		
		spreadsheet = new GoogleSpreadsheet(spreadsheetName, user, pass);
		
		GSWorksheet<GSRow<GSCell>> workSheet = spreadsheet.getGSSpreadsheet().getGSWorksheetBySheetName(worksheetName);

		GSRow<GSCell> headerRow = workSheet.getGSRow(1);

		for(String columnName : headerRow.getGSCells().keySet()){
			uniqueColumnComboBox.addItem(columnName);		
		}
	}

	private void freezeUI(boolean shouldFreeze){
		setCursor(Cursor.getPredefinedCursor(shouldFreeze ? Cursor.WAIT_CURSOR : Cursor.DEFAULT_CURSOR));
		for (Component comp : this.getComponents()){
			comp.setEnabled(!shouldFreeze);
		}
	}
}
