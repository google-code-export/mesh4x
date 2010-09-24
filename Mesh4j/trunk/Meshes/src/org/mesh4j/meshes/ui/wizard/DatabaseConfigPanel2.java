package org.mesh4j.meshes.ui.wizard;

import java.util.List;

import org.mesh4j.meshes.controller.CreateMeshWizardController;

@SuppressWarnings("serial")
public class DatabaseConfigPanel2 extends ChooseTablesConfigPanel {
	
	public DatabaseConfigPanel2(CreateMeshWizardController controller) {
		super(controller);
	}
	
	@Override
	protected List<String> getTableNames() throws Exception {
		DatabaseEngine engine = (DatabaseEngine) controller.getValue("datasource.engine");
		String host = controller.getStringValue("datasource.host");
		String port = controller.getStringValue("datasource.port");
		String user = controller.getStringValue("datasource.user");
		String password = controller.getStringValue("datasource.password");
		String database = controller.getStringValue("datasource.database");
		String url = engine.getConnectionUrl(host, port, database);
		return engine.getTableNames(url, user, password);
	}
}
