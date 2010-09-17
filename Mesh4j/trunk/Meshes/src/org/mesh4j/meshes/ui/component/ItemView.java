package org.mesh4j.meshes.ui.component;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.schema.ISchema;

@SuppressWarnings("serial")
public class ItemView extends JPanel {
	
	private final ISchema schema;
	private final Item item;
	private final ConflictsView conflictsView;

	public ItemView(ConflictsView conflictsView, Item item, ISchema schema) {
		this.conflictsView = conflictsView;
		this.item = item;
		this.schema = schema;
		initializeView();
	}

	private void initializeView() {
		setLayout(new MigLayout("insets 0, fill"));
		setBorder(LineBorder.createBlackLineBorder());
		Map<String, Object> properties = schema.getPropertiesAsMap(item.getContent().getPayload());
		
		DefaultTableModel model = new DefaultTableModel(new String[] { "Field", "Value" }, 0);
		add(new JScrollPane(new JTable(model)), "height 50%!, wrap");
		
		add(new JButton(new AbstractAction("Choose") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(conflictsView, "Are you sure you want to choose this revision?", "Choose revision", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					conflictsView.chooseConflictWinner(item);
				}	
			}
		}));
		
		for (String key : properties.keySet()) {
			model.addRow(new String[] { key, String.valueOf(properties.get(key)) });
		}
	}

}
