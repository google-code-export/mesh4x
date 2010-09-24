package org.mesh4j.meshes.ui.wizard;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

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
		
		FocusListener focusListener = new FillDatabases();
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
	
	private class FillDatabases extends FocusAdapter {
		
		private DatabaseEngine oldEngine;
		private String oldHost;
		private String oldPort;
		private String oldUser;
		private String oldPassword;
		
		@Override
		public void focusLost(FocusEvent e) {
			DatabaseEngine engine = (DatabaseEngine) controller.getValue("datasource.engine");
			String host = controller.getStringValue("datasource.host");
			String port = controller.getStringValue("datasource.port");
			String user = controller.getStringValue("datasource.user");
			String password = controller.getStringValue("datasource.password");
			
			if (engine == oldEngine && equals(host, oldHost) && equals(port, oldPort) && equals(user, oldUser) && equals(password, oldPassword))
				return;
			
			oldEngine = engine;
			oldHost = host;
			oldPort = port;
			oldUser = user;
			oldPassword = password;
			
			uiDatabase.removeAllItems();
			
			if (host == null || host.trim().length() == 0)
				return;
			if (port == null || port.trim().length() == 0)
				return;
			if (user == null || user.trim().length() == 0)
				return;
			if (password == null || password.trim().length() == 0)
				return;
			
			String url = engine.getConnectionUrl(host, port);
			try {
				Class.forName(engine.getDriverClass());
				Connection conn = DriverManager.getConnection(url, user, password);
				PreparedStatement ps = conn.prepareStatement(engine.getShowDatabasesQuery());
				ResultSet rs = ps.executeQuery();
				
				List<String> tableNames = new ArrayList<String>();
				while(rs.next()) {
					tableNames.add(rs.getString(1));
				}
				rs.close();
				ps.close();
				conn.close();
				
				Collections.sort(tableNames, new Comparator<String>() {
					@Override
					public int compare(String x, String y) {
						return x.compareToIgnoreCase(y);
					}
				});
				
				for(String tableName : tableNames) {
					uiDatabase.addItem(tableName);
				}
				
				if (uiDatabase.hasFocus()) {
					for (int i = 0; i < 2; i++) {
						final boolean visible = i == 1;
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								uiDatabase.setPopupVisible(visible);
							}
						});
					}
				}
			} catch (Exception ex) {
				controller.setErrorMessage(ex.getMessage());
			}
		}
		
		private boolean equals(String s1, String s2) {
			if ((s1 == null) != (s2 == null))
				return false;
			
			if (s1 == null)
				return true;
			
			return s1.equals(s2);
		}
	}
	
}
