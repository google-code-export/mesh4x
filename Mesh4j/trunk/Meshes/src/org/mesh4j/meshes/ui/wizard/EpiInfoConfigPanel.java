package org.mesh4j.meshes.ui.wizard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;
import org.mesh4j.meshes.controller.CreateMeshWizardController;
import org.mesh4j.meshes.ui.FileChooserUtil;

@SuppressWarnings("serial")
public class EpiInfoConfigPanel extends ConfigPanel {
	
	private static final Logger LOGGER = Logger.getLogger(EpiInfoConfigPanel.class);

	private CreateMeshWizardController controller;
	private JTextField fileTextField;
	
	public EpiInfoConfigPanel(CreateMeshWizardController controller) {
		super();
		this.controller = controller;
		initComponents();
	}
	
	private void initComponents() {
		setLayout(new MigLayout("insets 10"));
		//setSize(550, 350);
		
		add(new JLabel("<html><h2>Confirm the location of your EpiInfo data</h2></html>"), "span");
		add(new JLabel("<html><h4>Tell us where you have your EpiInfo data:</h4></html>"), "span, wrap 5");
		
		JLabel fileLabel = new JLabel("EpiInfo mdb file:");
		fileTextField = new JTextField();
		fileTextField.setEditable(false);
		fileTextField.setFocusable(false);
		add(fileLabel, "gapright 5");
		add(fileTextField, "grow x, push");
		
		JButton fileChooserButton = new JButton("Browse");
		fileChooserButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				fileChooserButtonActionPerformed(evt);
			}
		});
		add(fileChooserButton, "wrap 10");
	}
	
	private void fileChooserButtonActionPerformed(ActionEvent evt) {
		
		File selectedFile = FileChooserUtil.chooseEpiInfoFile(this, new File(fileTextField.getText()));
		if (selectedFile != null) {
			try {
				String fileName = selectedFile.getCanonicalPath();
				fileTextField.setText(fileName);
				controller.setValue("epiinfo.location", fileName);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}
	
	public String getErrorMessage() {
		File file = new File(fileTextField.getText());
		if (!file.exists()) {
			return "The file does not exist";
		}
		return null;
	}
}
