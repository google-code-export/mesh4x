package org.mesh4j.meshes.ui.wizard;

import java.util.List;

import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

public class FillDatabases {
	
	private DatabaseEngine oldEngine;
	private String oldHost;
	private String oldPort;
	private String oldUser;
	private String oldPassword;
	
	public FillDatabases() {
	}
	
	public void fill(DatabaseEngine engine, String host, String port, String user, String password, final JComboBox uiDatabase) throws Exception {
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
		
		List<String> databaseNames = engine.getDatabaseNames(url, user, password);
		for(String databaseName : databaseNames) {
			uiDatabase.addItem(databaseName);
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
	}
	
	private boolean equals(String s1, String s2) {
		if ((s1 == null) != (s2 == null))
			return false;
		
		if (s1 == null)
			return true;
		
		return s1.equals(s2);
	}

}
