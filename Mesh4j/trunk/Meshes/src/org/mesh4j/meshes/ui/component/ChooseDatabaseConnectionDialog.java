package org.mesh4j.meshes.ui.component;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.mesh4j.meshes.model.HibernateDataSource;
import org.mesh4j.meshes.model.MeshVisitor;
import org.mesh4j.meshes.ui.wizard.DatabaseEngine;
import org.mesh4j.meshes.ui.wizard.FillDatabases;

public class ChooseDatabaseConnectionDialog extends JDialog {
	
	private static final long serialVersionUID = -8436108589942443275L;
	
	private final HibernateDataSource dataSource;
	private HibernateDataSource resolved;
	private String databaseName;
	private List<String> tableNames;
	
	private JComboBox uiEngine;
	private JTextField uiHost;
	private JTextField uiPort;
	private JTextField uiUser;
	private JPasswordField uiPassword;
	private JComboBox uiDatabase;
	private JLabel uiError;
	
	public ChooseDatabaseConnectionDialog(HibernateDataSource dataSource) {
		this.dataSource = dataSource;
		computeDatabaseInfo();
		initComponents();
		setMinimumSize(new Dimension(640, 480));
	}
	
	public HibernateDataSource getResolved() {
		return resolved;
	}
	
	private void computeDatabaseInfo() {
		final String url = dataSource.getConnectionURL();
		int index = url.lastIndexOf('|');
		if (index >= 0) {
			this.databaseName = url.substring(0, index);
			this.tableNames = new ArrayList<String>();
			
			dataSource.getDataSet().getMesh().accept(new MeshVisitor() {
				@Override
				public boolean visit(HibernateDataSource dataSource) {
					if (url.equals(dataSource.getConnectionURL())) {
						tableNames.add(dataSource.getTableName());
					}
					return true;
				}
			});
		}
	}
	
	private void initComponents() {
		setLayout(new MigLayout("insets 10, fill"));
		
		add(new JLabel("<html><h2>Database connection</h2></html>"), "span");
		add(new JLabel("<html><h4>Tell us how to connect to the '" + databaseName + "' database.<br/>It should have the following tables:<br/>" + toString(tableNames) + "</h4></html>"), "span, wrap 5");
		
		add(new JLabel("Engine: "));
		add(uiEngine = new JComboBox((Object[])DatabaseEngine.values()), "span, wrap");
		
		add(new JLabel("Host: "));
		add(uiHost = new JTextField("localhost"), "growx, wrap");
		
		add(new JLabel("Port: "));
		add(uiPort = new JTextField("3306"), "growx, wrap");
		
		add(new JLabel("User: "));
		add(uiUser = new JTextField(), "growx, wrap");
		
		add(new JLabel("Password: "));
		add(uiPassword = new JPasswordField(), "growx, wrap");
		
		add(new JLabel("Database: "));
		add(uiDatabase = new JComboBox(), "growx, wrap");
		
		add(uiError = new JLabel(), "span 2, wrap");
		uiError.setMinimumSize(new Dimension(1, 20));
		
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout());
		add(panel, "span 2, wrap");
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DatabaseEngine engine = (DatabaseEngine) uiEngine.getSelectedItem();
				String host = uiHost.getText();
				String port = uiPort.getText();
				String user = uiUser.getText();
				String password = new String(uiPassword.getPassword());
				String database = (String) uiDatabase.getSelectedItem();
				
				if (host == null || host.trim().length() == 0) {
					setErrorMessage("Host is required");
					return;
				}
				
				if (port == null || port.trim().length() == 0) {
					setErrorMessage("Port is required");
					return;
				}
				
				if (user == null || user.trim().length() == 0) {
					setErrorMessage("User is required");
					return;
				}
				
				if (database == null) {
					return;
				}
				
				String url = engine.getConnectionUrl(host, port, database);
				
				try {
					Set<String> foundTableNames = new HashSet<String>();
					engine.addTableNames(url, user, password, foundTableNames);
					for(String tableName : tableNames) {
						if (!(foundTableNames.contains(tableName))) {
							setErrorMessage("Table '" + tableName + "' not found in '" + database + "' database");
							return;
						}	
					}
					
					resolved = new HibernateDataSource();
					resolved.setConnectionURL(url);
					resolved.setDataSet(dataSource.getDataSet());
					resolved.setDialectClass(dataSource.getDialectClass());
					resolved.setDriverClass(dataSource.getDriverClass());
					resolved.setHasConflicts(dataSource.hasConflicts());
					resolved.setLastSyncDate(dataSource.getLastSyncDate());
					resolved.setPassword(password);
					resolved.setTableName(dataSource.getTableName());
					resolved.setUser(user);
					
					setVisible(false);
				} catch (Exception ex) {
					setErrorMessage(ex.getMessage());
				}
			}
		});
		
		panel.add(cancel);
		panel.add(ok);
		
		FocusListener focusListener = new FillDatabasesFocusListener();
		uiEngine.addFocusListener(focusListener);
		uiHost.addFocusListener(focusListener);
		uiPort.addFocusListener(focusListener);
		uiUser.addFocusListener(focusListener);
		uiPassword.addFocusListener(focusListener);
		
		uiEngine.setSelectedIndex(0);
	}
	
	private void setErrorMessage(String errorMessage) {
		if (errorMessage == null) {
			uiError.setText("");
		} else {
			uiError.setText("<html><span style=\"color:red\">" + errorMessage + "</span></html>");
		}
	}
	
	private String toString(List<String> names) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < names.size(); i++) {
			if (i != 0) sb.append(", ");
			sb.append(names.get(i));
		}
		return sb.toString();
	}
	
	private class FillDatabasesFocusListener extends FocusAdapter {
		
		private FillDatabases fillDatabases = new FillDatabases();
		
		@Override
		public void focusLost(FocusEvent e) {
			DatabaseEngine engine = (DatabaseEngine) uiEngine.getSelectedItem();
			String host = uiHost.getText();
			String port = uiPort.getText();
			String user = uiUser.getText();
			String password = new String(uiPassword.getPassword());
			
			try {
				fillDatabases.fill(engine, host, port, user, password, uiDatabase);
				if (databaseName != null) {
					uiDatabase.setSelectedItem(databaseName);
				}
				setErrorMessage(null);
			} catch (Exception ex) {
				setErrorMessage(ex.getMessage());
			}
		}
	}


}
