package org.mesh4j.meshes.ui.wizard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;
import org.mesh4j.meshes.controller.CreateMeshWizardController;
import org.mesh4j.sync.adapters.hibernate.msaccess.MsAccessHibernateSyncAdapterFactory;

public class MsAccessConfigPanel extends JPanel {
	
	private static final long serialVersionUID = 7546728738425169284L;
	private static final Logger LOGGER = Logger.getLogger(MsAccessConfigPanel.class);

	private CreateMeshWizardController controller;
	
	private JFileChooser fileChooser;
	private JTextField fileTextField;
	private JList tableList;
	
	public MsAccessConfigPanel(CreateMeshWizardController controller) {
		super(new MigLayout("insets 10"));
		this.controller = controller;
		initComponents();
	}
	
	private void initComponents() {
		setSize(550, 350);
		
		JLabel titleLabel = new JLabel("Add Access Table");
		add(titleLabel, "span, wrap 10");
		
		JLabel fileTitleLabel = new JLabel();
		fileTitleLabel.setText("Tell us where your Access Database is:");
		add(fileTitleLabel, "span, wrap 5");
		
		JLabel fileLabel = new JLabel("File:");
		fileTextField = new JTextField();
		fileTextField.setEditable(false);
		add(fileLabel, "gapright 5");
		add(fileTextField, "growx, push");
		
		fileChooser = new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileFilter(
				new FileNameExtensionFilter("MS Access Files", "mdb", "MDB"));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		JButton fileChooserButton = new JButton("...");
		fileChooserButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				fileChooserButtonActionPerformed(evt);
			}
		});
		add(fileChooserButton, "wrap 10");
		
		JLabel tableTitleLabel = new JLabel();
		tableTitleLabel.setText("Choose the table you'd like to share");
		add(tableTitleLabel, "span, wrap 5");
		
		tableList = new JList();
		tableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				tableListValueChanged(evt);
			}
		});
		JScrollPane tableListScrollPane = new JScrollPane(tableList);
		add(tableListScrollPane, "growy, span");
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
					fillTableList(fileName);
					//controller.changeMsAccessFileName(fileName);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
	}
	
	private void tableListValueChanged(ListSelectionEvent evt) {
		String tableName = (String) tableList.getSelectedValue();
		//controller.changeMsAccessTableName(tableName);
	}
	
	private void fillTableList(String fileName) {
		tableList.removeAll();
		
		try {
			Set<String> tableNames = MsAccessHibernateSyncAdapterFactory.getTableNames(fileName);
			tableList.setListData(tableNames.toArray());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

}
