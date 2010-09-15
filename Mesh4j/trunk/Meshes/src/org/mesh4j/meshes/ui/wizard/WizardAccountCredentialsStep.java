package org.mesh4j.meshes.ui.wizard;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.mesh4j.meshes.controller.CreateMeshWizardController;
import org.mesh4j.meshes.server.MeshServer;

public class WizardAccountCredentialsStep extends BaseWizardPanel {

	private static final long serialVersionUID = -5220841948982059704L;
	private static String ID = "STEP_ACCOUNT_CREDENTIALS";
	
	private CreateMeshWizardController controller;
	
	private JTextField emailField;
	private JPasswordField passwordField;
	
	public WizardAccountCredentialsStep(CreateMeshWizardController controller) {
		super();
		this.controller = controller;
		initComponents();
	}

	private void initComponents() {
		setLayout(new MigLayout("insets 10"));
		setSize(550, 350);
		
		JLabel titleLabel = new JLabel("<html><h2>Enter your Mesh4x credentials</h2></html>");
		add(titleLabel, "span");
		
		JLabel subTitleLabel = new JLabel();
		subTitleLabel.setText("<html><h4>In order to create a new mesh you need a Mesh4x account. " +
				"Enter your account credentials here.</h4></html>");
		add(subTitleLabel, "span, wrap 10");
		
		JLabel emailLabel = new JLabel("Email");
		emailField = new JTextField();
		emailField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				emailFieldKeyReleased(e);
			}
		});
		add(emailLabel, "gapright 20");
		add(emailField, "growx, wrap");
		
		JLabel passwordLabel = new JLabel("Password");
		passwordField = new JPasswordField();
		passwordField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				passwordFieldKeyReleased(e);
			}
		});
		add(passwordLabel, "gapright 20");
		add(passwordField, "growx");
		
		WizardUtils.nextWhenEnterPressedOn(controller, emailField);
		WizardUtils.nextWhenEnterPressedOn(controller, passwordField);
	}

	private void passwordFieldKeyReleased(KeyEvent evt) {
		String password = new String(passwordField.getPassword());
		controller.setValue("account.password", password);	
	}
	
	private void emailFieldKeyReleased(KeyEvent evt) {
		String email = emailField.getText();
		controller.setValue("account.email", email);
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
	}

	@Override
	public String getId() {
		return ID;
	}
		
	@Override
	public boolean needsValidationBeforeLeave() {
		return true;
	}
	
	@Override
	public String getErrorMessageBeforeLeave() {
		String email = emailField.getText();
		String password = new String(passwordField.getPassword());
		if (MeshServer.getInstance().areValid(email, password)) {
			return null;
		} else {
			return "Invalid email/password";
		}
	}
}
