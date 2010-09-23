package org.mesh4j.meshes.ui.wizard;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
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
		add(new JScrollPane(panel), "width 90%!, height 35%!, wrap"); 
		
		JPanel panel2 = new JPanel(new MigLayout("insets 0"));
		add(panel2, "wrap");
		for (int i = 0; i < 2; i++) {
			final boolean selected = i == 0;
			JButton selectAllButton = new JButton(selected ? "Select all" : "Unselect all");
			selectAllButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					List<String> tableNames = new ArrayList<String>();
					
					for(Component component : panel.getComponents()) {
						if (!(component instanceof JCheckBox))
							continue;
						
						JCheckBox check = (JCheckBox) component;
						check.setSelected(selected);
						if (selected)
							tableNames.add(check.getText());
					}
					
					controller.setValue("epiinfo.tableNames", tableNames);
				}
			});
			panel2.add(selectAllButton);
		}
		
		add(new JLabel("<html><table><tr><td valign=baseline><b>Note:</b></td><td>View tables for the selected tables will be automatically synchronized,<br/>as well as all code tables.</td></tr></table></html>"), "span, wrap");
	}
	
	@Override
	public void showInWizard() {
		panel.removeAll();
		
		List<String> tableNames = EpiInfoSyncAdapterFactory.getTableNames(controller.getStringValue("epiinfo.location"));
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
