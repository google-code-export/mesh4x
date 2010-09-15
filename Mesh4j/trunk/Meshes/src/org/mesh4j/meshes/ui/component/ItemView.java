package org.mesh4j.meshes.ui.component;

import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.schema.ISchema;

@SuppressWarnings("serial")
public class ItemView extends JPanel {
	
	private final ISchema schema;
	private final Item item;

	public ItemView(Item item, ISchema schema) {
		this.item = item;
		this.schema = schema;
		initializeView();
	}

	private void initializeView() {
		setLayout(new MigLayout("insets 0"));
		Map<String, Object> properties = schema.getPropertiesAsMap(item.getContent().getPayload());
		
		DefaultTableModel model = new DefaultTableModel(new String[] { "Field", "Value" }, 0);
		add(new JScrollPane(new JTable(model)), "grow");
		
		for (String key : properties.keySet()) {
			model.addRow(new String[] { key, String.valueOf(properties.get(key)) });
		}
	}

}
