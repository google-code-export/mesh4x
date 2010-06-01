package org.mesh4j.meshes.ui.wizard;

import java.awt.Desktop;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;

import org.mesh4j.meshes.controller.CreateMeshWizardController;
import org.mesh4j.meshes.ui.resource.ResourceManager;

public class CreateMeshStepSevenView extends BaseWizardPanel {

	private static final long serialVersionUID = -5773369351266179486L;
	private static String ID = "STEP_SEVEN";
	
	private CreateMeshWizardController controller;
	
	private JFileChooser fileChooser;
	
	public CreateMeshStepSevenView(CreateMeshWizardController controller) {
		super();
		this.controller = controller;
		initComponents();
	}

	private void initComponents() {
		setLayout(new MigLayout("insets 10"));
		setSize(550, 350);
		
		JLabel titleLabel = new JLabel("<html><h2>Congratulations! Your mesh is ready to sync</h2></html>");
		add(titleLabel, "span");
		
		JLabel subTitleLabel = new JLabel();
		subTitleLabel.setText("<html><h4>Your data will be synchronized every day. You can trigger the synchonization yourself by double " +
							  "clicking the Meshes icon in your traybar:</h4></html>");
		add(subTitleLabel, "span");
		
		Icon trayIcon = new ImageIcon(ResourceManager.getLogo().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
		JLabel trayIconLabel = new JLabel(trayIcon);
		add(trayIconLabel, "span, align center");
		
		JLabel saveLabel = new JLabel();
		saveLabel.setText("<html><h4>Save the configuration file of your mesh by clicking the button below and distribute to your colleagues " +
							  "to have them sync to your mesh:</h4></html>");
		add(saveLabel, "span");
		
		JButton saveButton = new JButton();
		saveButton.setText("<html><h4>Save configuration file ...</h4></html>");
		add(saveButton, "span");
		
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveButtonActionPerformed(e);
			}
		});
		
		fileChooser = new JFileChooser();
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Mesh Configuration File", "mesh");
		fileChooser.setFileFilter(filter);
		File file = new File("configuration.mesh");
		fileChooser.setSelectedFile(file);
	}
	
	private void saveButtonActionPerformed(ActionEvent e) {
		int returnVal = fileChooser.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			if (selectedFile != null) {
				try {
					controller.saveConfiguration(selectedFile);
					if (Desktop.isDesktopSupported())
						Desktop.getDesktop().open(selectedFile.getParentFile());
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(this, ex.getMessage(), "The mesh configuration file could not be saved", JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				}
			}
		}
	}
		
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

}
