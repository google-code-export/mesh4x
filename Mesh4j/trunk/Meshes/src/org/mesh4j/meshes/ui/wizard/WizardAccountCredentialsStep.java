package org.mesh4j.meshes.ui.wizard;

import java.awt.Cursor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.mesh4j.meshes.controller.CreateMeshWizardController;
import org.mesh4j.meshes.server.IMeshServer;
import org.mesh4j.meshes.server.MeshServer;

public class WizardAccountCredentialsStep extends BaseWizardPanel {

	private static final long serialVersionUID = -5220841948982059704L;
	private static String ID = "STEP_ACCOUNT_CREDENTIALS";
	
	private final static String LoginInstructions = "<html><h4>In order to create a new mesh you need a Mesh4x account. Enter your account credentials here.</h4></html>";
	private final static String DontHaveAccountInstructions = "<html>Don't have an account? <a href=\"#\">Create one</a></html>";
	
	private CreateMeshWizardController controller;
	
	private JTextField emailField;
	private JPasswordField passwordField;
	private JPasswordField passwordConfirmationField;
	
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
		
		final JLabel subTitleLabel = new JLabel();
		subTitleLabel.setText(LoginInstructions);
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
		add(passwordField, "growx, wrap");
		
		final JLabel passwordConfirmationLabel = new JLabel("Password confirmation");
		passwordConfirmationLabel.setVisible(false);
		
		passwordConfirmationField = new JPasswordField();
		passwordConfirmationField.setVisible(false);
		
		add(passwordConfirmationLabel, "gapright 20");
		add(passwordConfirmationField, "growx, wrap");
		
		final JLabel newAccountLink = new JLabel();
		newAccountLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		newAccountLink.setText(DontHaveAccountInstructions);
		newAccountLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (passwordConfirmationField.isVisible()) {
					passwordConfirmationLabel.setVisible(false);
					passwordConfirmationField.setVisible(false);
					subTitleLabel.setText(LoginInstructions);
					newAccountLink.setText(DontHaveAccountInstructions);
				} else {
					passwordConfirmationLabel.setVisible(true);
					passwordConfirmationField.setVisible(true);
					subTitleLabel.setText("<html><h4>To create an account specify the following information:</h4></html>");
					newAccountLink.setText("<html><a href=\"#\">Go back to login</a></html>");
				}
			}
		});
		add(newAccountLink, "alignright, span");
		
		WizardUtils.nextWhenEnterPressedOn(controller, emailField);
		WizardUtils.nextWhenEnterPressedOn(controller, passwordField);
		WizardUtils.nextWhenEnterPressedOn(controller, passwordConfirmationField);
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
	public String getId() {
		return ID;
	}
		
	@Override
	public boolean needsValidationBeforeLeave() {
		return true;
	}
	
	@Override
	public String getErrorMessageBeforeLeave() {
		IMeshServer server = MeshServer.getInstance();
		
		if (passwordConfirmationField.isVisible()) {
			String email = emailField.getText();
			String password = new String(passwordField.getPassword());
			String passwordConfirmation = new String(passwordConfirmationField.getPassword());
			if (!password.equals(passwordConfirmation)) {
				return "The password and password confirmation doesn't match";
			}
			
			if (!server.createAccount(email, password)) {
				return "An account with that email already exists, or the email is not a valid one";
			}
		} else {
			String email = emailField.getText();
			String password = new String(passwordField.getPassword());
			if (!server.areValid(email, password)) {
				return "Invalid email/password";
			}
		}
		
		return null;
	}
}
