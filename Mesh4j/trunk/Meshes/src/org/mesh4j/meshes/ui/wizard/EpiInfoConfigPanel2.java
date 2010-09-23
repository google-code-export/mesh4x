package org.mesh4j.meshes.ui.wizard;

import java.util.List;

import javax.swing.JLabel;

import org.mesh4j.meshes.controller.CreateMeshWizardController;
import org.mesh4j.sync.adapters.epiinfo.EpiInfoSyncAdapterFactory;

@SuppressWarnings("serial")
public class EpiInfoConfigPanel2 extends ChooseTablesConfigPanel {
	
	public EpiInfoConfigPanel2(CreateMeshWizardController controller) {
		super(controller);
	}
	
	@Override
	protected void initComponents() {
		super.initComponents();
		add(new JLabel("<html><table><tr><td valign=baseline><b>Note:</b></td><td>View tables for the selected tables will be automatically synchronized,<br/>as well as all code tables.</td></tr></table></html>"), "span, wrap");
	}
	
	@Override
	protected List<String> getTableNames() throws Exception {
		return EpiInfoSyncAdapterFactory.getTableNames(controller.getStringValue("datasource.location"));
	}
	
}
