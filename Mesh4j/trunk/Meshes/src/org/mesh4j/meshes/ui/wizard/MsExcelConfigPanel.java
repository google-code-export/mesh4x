package org.mesh4j.meshes.ui.wizard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.meshes.controller.CreateMeshWizardController;
import org.mesh4j.sync.adapters.msexcel.MsExcelSyncAdapterFactory;

public class MsExcelConfigPanel extends JPanel {
	
	private static final long serialVersionUID = 7546728738425169284L;
	private static final Log LOGGER = LogFactory.getLog(MsExcelConfigPanel.class);

	private CreateMeshWizardController controller;
	
	private JFileChooser fileChooser;
	private JTextField fileTextField;
	private JComboBox worksheetComboBox;
	private JComboBox uniqueColumnComboBox;
	
	public MsExcelConfigPanel(CreateMeshWizardController controller) {
		super(new MigLayout("insets 10"));
		this.controller = controller;
		initComponents();
	}
	
	private void initComponents() {
		setSize(550, 350);
		
		JLabel titleLabel = new JLabel("Add Excel Table");
		add(titleLabel, "span, wrap 10");
		
		JLabel fileTitleLabel = new JLabel();
		fileTitleLabel.setText("Tell us where your Excel Database is:");
		add(fileTitleLabel, "span, wrap 5");
		
		JLabel fileLabel = new JLabel("File:");
		fileTextField = new JTextField();
		fileTextField.setEditable(false);
		add(fileLabel, "gapright 5");
		add(fileTextField, "growx, pushx");
		
		fileChooser = new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileFilter(
				new FileNameExtensionFilter("MS Excel Files", "xls", "xlsx", "XLS","XLSX"));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		JButton fileChooserButton = new JButton("...");
		fileChooserButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				fileChooserButtonActionPerformed(evt);
			}
		});
		add(fileChooserButton, "wrap 10");
		
		JLabel worksheetLabel = new JLabel();
		worksheetLabel.setText("Worksheet:");
		add(worksheetLabel, "gapright 5");
		
		worksheetComboBox = new JComboBox();
		worksheetComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				worksheetComboBoxItemStateChanged(e);
			}
		});
		add(worksheetComboBox, "growx, wrap 10");
		
		JLabel uniqueColumnLabel = new JLabel();
		uniqueColumnLabel.setText("Unique Column:");
		add(uniqueColumnLabel, "gapright 5");
		
		uniqueColumnComboBox = new JComboBox();
		uniqueColumnComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				uniqueColumnComboBoxItemStateChanged(e);
			}
		});
		add(uniqueColumnComboBox, "growx");
	}
	
	private void fileChooserButtonActionPerformed(ActionEvent evt) {
		fileChooser.setSelectedFile(new File(fileTextField.getText()));
		int returnVal = fileChooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			if (selectedFile != null) {
				try {
					String fileName = selectedFile.getCanonicalPath();
					fileTextField.setText(fileName);
					fillWorksheetComboBox(fileName);
					//controller.changeMsExcelFileName(fileName);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
	}
	

	private void worksheetComboBoxItemStateChanged(ItemEvent e) {
		String worksheet = (String) e.getItem();
		fillUniqueColumnComboBox(worksheet);
		//controller.changeMsExcelWorksheetName(worksheet);
	}
	
	private void uniqueColumnComboBoxItemStateChanged(ItemEvent e) {
		String uniqueColumnName = (String) e.getItem();
		//controller.changeMsExcelUniqueColumnName(uniqueColumnName);
	}
	
	private void fillWorksheetComboBox(String fileName) {
		worksheetComboBox.removeAllItems();
		Set<String> worksheets = MsExcelSyncAdapterFactory.getSheetNames(fileName);
		
		for (String worksheet : worksheets) {
			worksheetComboBox.addItem(worksheet);
		}
	}
	
	private void fillUniqueColumnComboBox(String worksheet) {
		uniqueColumnComboBox.removeAllItems();
		String fileName = fileTextField.getText();
		Set<String> columns = MsExcelSyncAdapterFactory.getColumnHeaderNames(fileName, worksheet);
		
		for (String column : columns) {
			uniqueColumnComboBox.addItem(column);
		}
	}

}
