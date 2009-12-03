package org.mesh4j.meshes.ui.wizard;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.JLabel;
import javax.swing.JPasswordField;

import net.miginfocom.swing.MigLayout;

import org.mesh4j.meshes.controller.CreateMeshWizardController;

public class CreateMeshStepTwoView extends BaseWizardPanel {

	private static final long serialVersionUID = -5220841948982059704L;
	private static String ID = "STEP_TWO";
	
	private CreateMeshWizardController controller;
	
	private JPasswordField passwordField;
	private JPasswordField confirmPasswordField;
	
	public CreateMeshStepTwoView(CreateMeshWizardController controller) {
		super();
		this.controller = controller;
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
		passwordField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent evt) {
				passwordFieldFocusLost(evt);
			}
		});
		add(passwordLabel, "gapright 20");
		add(passwordField, "growx, wrap");
		
		JLabel confirmPasswordLabel = new JLabel("Confirm Password");
		confirmPasswordField = new JPasswordField();
		add(confirmPasswordLabel, "gapright 20");
		add(confirmPasswordField, "growx");
	}

	private void passwordFieldFocusLost(FocusEvent evt) {
		char[] passwordArray = passwordField.getPassword();
		String password = "";
		for (int i = 0; i < passwordArray.length; i++) {
			password += passwordArray[i];
		}
		controller.changeMeshPassword(password);	
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
	}

	@Override
	public String getId() {
		return ID;
	}
}
