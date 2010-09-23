package org.mesh4j.meshes.ui.wizard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.mesh4j.meshes.controller.CreateMeshWizardController;
import org.mesh4j.sync.adapters.epiinfo.EpiInfoSyncAdapterFactory;

@SuppressWarnings("serial")
public class EpiInfoConfigPanel2 extends ConfigPanel {

	private CreateMeshWizardController controller;
	private JPanel panel;
	
	public EpiInfoConfigPanel2(CreateMeshWizardController controller) {
		super();
		this.controller = controller;
		initComponents();
	}
	
	private void initComponents() {
		setLayout(new MigLayout("insets 10"));
		
		add(new JLabel("<html><h2>Choose tables</h2></html>"), "span");
		add(new JLabel("<html><h4>Choose the tables you want to synhcronize:</h4></html>"), "span, wrap");
		
		panel = new JPanel(new MigLayout("insets 0, fill"));
		add(new JScrollPane(panel), "width 90%!, height 45%!, wrap"); 
		
		add(new JLabel("<html><table><tr><td valign=baseline><b>Note:</b></td><td>View tables for the selected tables will be automatically synchronized,<br/>as well as all code tables.</td></tr></table></html>"), "span, wrap");
	}
	
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		if ("epiinfo.location".equals(evt.getPropertyName())) {
			panel.removeAll();
			
			List<String> tableNames = EpiInfoSyncAdapterFactory.getTableNames(evt.getNewValue().toString());
			controller.setValue("epiinfo.tableNames", tableNames);
			
			for(final String tableName : tableNames) {
				final JCheckBox check = new JCheckBox(tableName);
				check.setSelected(true);
				check.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						@SuppressWarnings("unchecked")
						List<String> tableNames = (List<String>) controller.getValue("epiinfo.tableNames");
						if (check.isSelected()) {
							tableNames.add(tableName);
						} else {
							tableNames.remove(tableName);
						}
						controller.setValue("epiinfo.tableNames", tableNames);
					}
				});
				panel.add(check, "growx, wrap");
			}
		}
		super.modelPropertyChange(evt);
	}
	
	
	@Override
	public String getErrorMessage() {
		@SuppressWarnings("unchecked")
		List<String> tableNames = (List<String>) controller.getValue("epiinfo.tableNames");
		if (tableNames == null || tableNames.isEmpty()) {
			return "You must select at least one table";
		}
		return null;
	}
	
}
