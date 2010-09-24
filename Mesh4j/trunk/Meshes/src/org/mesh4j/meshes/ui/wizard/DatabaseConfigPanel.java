package org.mesh4j.meshes.ui.wizard;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.mesh4j.meshes.controller.CreateMeshWizardController;

@SuppressWarnings("serial")
public class DatabaseConfigPanel extends ConfigPanel { 

	private CreateMeshWizardController controller;
	
	private JComboBox uiEngine;
	private JTextField uiHost;
	private JTextField uiPort;
	private JTextField uiUser;
	private JTextField uiPassword;
	private JComboBox uiDatabase;
	
	public DatabaseConfigPanel(CreateMeshWizardController controller) {
		super();
		this.controller = controller;
		initComponents();
	}
	
	private void initComponents() {
		setLayout(new MigLayout("insets 10"));
		
		add(new JLabel("<html><h2>Database connection</h2></html>"), "span");
		add(new JLabel("<html><h4>Tell us how to connect to the database:</h4></html>"), "span, wrap 5");
		
		add(new JLabel("Engine: "));
		add(uiEngine = WizardUtils.newJComboBox(controller, "datasource.engine", (Object[])DatabaseEngine.values()), "span, wrap");
		
		add(new JLabel("Host: "));
		add(uiHost = WizardUtils.newTextField(controller, "datasource.host", "localhost"), "growx, wrap");
		
		add(new JLabel("Port: "));
		add(uiPort = WizardUtils.newTextField(controller, "datasource.port", "3306"), "growx, wrap");
		
		add(new JLabel("User: "));
		add(uiUser = WizardUtils.newTextField(controller, "datasource.user"), "growx, wrap");
		
		add(new JLabel("Password: "));
		add(uiPassword = WizardUtils.newPasswordField(controller, "datasource.password"), "growx, wrap");
		
		add(new JLabel("Database: "));
		add(uiDatabase = WizardUtils.newJComboBox(controller, "datasource.database"), "growx, wrap");
		
		FocusListener focusListener = new FillDatabasesFocusListener();
		uiEngine.addFocusListener(focusListener);
		uiHost.addFocusListener(focusListener);
		uiPort.addFocusListener(focusListener);
		uiUser.addFocusListener(focusListener);
		uiPassword.addFocusListener(focusListener);
		
		uiEngine.setSelectedIndex(0);
	}
	
	@Override
	public String getErrorMessage() {
		String host = controller.getStringValue("datasource.host");
		String port = controller.getStringValue("datasource.port");
		String user = controller.getStringValue("datasource.user");
		String password = controller.getStringValue("datasource.password");
		String database = controller.getStringValue("datasource.database");
		
		if (host == null || host.trim().length() == 0)
			return "Host is required";
		if (port == null || port.trim().length() == 0)
			return "Port is required";
		if (user == null || user.trim().length() == 0)
			return "User is required";
		if (password == null || password.trim().length() == 0)
			return "Password is required";
		if (database == null || database.trim().length() == 0)
			return "Database is required";
		return null;
	}
	
	private class FillDatabasesFocusListener extends FocusAdapter {
		
		private FillDatabases fillDatabases = new FillDatabases();
		
		@Override
		public void focusLost(FocusEvent e) {
			DatabaseEngine engine = (DatabaseEngine) controller.getValue("datasource.engine");
			String host = controller.getStringValue("datasource.host");
			String port = controller.getStringValue("datasource.port");
			String user = controller.getStringValue("datasource.user");
			String password = controller.getStringValue("datasource.password");
			
			try {
				fillDatabases.fill(engine, host, port, user, password, uiDatabase);
			} catch (Exception ex) {
				controller.setErrorMessage(ex.getMessage());
			}
		}
	}
	
}
