package org.mesh4j.meshes.ui.wizard;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

import org.mesh4j.meshes.controller.CreateMeshWizardController;
import org.mesh4j.meshes.ui.resource.ResourceManager;

public class WizardConfirmMeshStep extends BaseWizardPanel {

	private static final long serialVersionUID = -5773369351266179486L;
	private static String ID = "STEP_CONFIRM_MESH";
	
	private CreateMeshWizardController controller;
	
	public WizardConfirmMeshStep(CreateMeshWizardController controller) {
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
		
		final JCheckBox saveCheck = new JCheckBox();
		saveCheck.setText("<html><h4>Save the configuration file of this new mesh to distribute to your colleages to have them sync to your mesh <b>after clicking Finish</b>.</h4></html>");
		add(saveCheck, "span");
		
		saveCheck.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setValue("saveConfigurationFile", saveCheck.isSelected());
			}
		});
	}

	@Override
	public String getId() {
		return ID;
	}

}
