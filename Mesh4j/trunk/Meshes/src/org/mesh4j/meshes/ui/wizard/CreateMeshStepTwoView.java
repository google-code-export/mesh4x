package org.mesh4j.meshes.ui.wizard;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


import net.miginfocom.swing.MigLayout;

public class CreateMeshStepTwoView extends JPanel {

	private static final long serialVersionUID = -5220841948982059704L;
	
	private WizardPanelDescriptor descriptor;
	
	private JPasswordField passwordField;
	private JPasswordField confirmPasswordField;
	
	public CreateMeshStepTwoView(WizardPanelDescriptor descriptor) {
		super();
		this.descriptor = descriptor;
		initComponents();
	}

	private void initComponents() {
		setLayout(new MigLayout("insets 10"));
		setSize(550, 350);
		
		JLabel titleLabel = new JLabel("Secure Access to your mesh");
		add(titleLabel, "span 2, wrap 20");
		
		JLabel subTitleLabel = new JLabel();
		subTitleLabel.setText("<html>You should secure access to your mesh data. Please enter the username and password " +
							  "you want to use to share data with your mesh. Many users and applications can use the same " +
							  "password if needed</html>");
		add(subTitleLabel, "span 2, wrap 20");
		
		JLabel passwordLabel = new JLabel("Password");
		passwordField = new JPasswordField();
		add(passwordLabel, "gapright 20");
		add(passwordField, "growx, wrap");
		
		JLabel confirmPasswordLabel = new JLabel("Confirm Password");
		confirmPasswordField = new JPasswordField();
		add(confirmPasswordLabel, "gapright 20");
		add(confirmPasswordField, "growx");
	}
	
}
