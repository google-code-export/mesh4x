package org.mesh4j.meshes.ui.component;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

import org.mesh4j.sync.model.History;
import org.mesh4j.sync.model.Item;

@SuppressWarnings("serial")
public class ItemView extends JPanel {
	
	private final Item item;
	private final ConflictsView conflictsView;
	private AbstractAction chooseAction;
	private static DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT);
	
	public static int ITEM_VIEW_MODE_NORMAL = 0;
	public static int ITEM_VIEW_MODE_CURRENT = 1;
	public static int ITEM_VIEW_MODE_CUSTOM = 2;
	private final int mode;
	private final Map<String, Object> properties;
	private final Map<String, Set<Object>> propertyValues;
	

	public ItemView(ConflictsView conflictsView, Item item, Map<String, Object> properties, Map<String, Set<Object>> propertyValues, int mode) {
		this.conflictsView = conflictsView;
		this.item = item;
		this.properties = properties;
		this.propertyValues = propertyValues;
		this.mode = mode;
		initializeView();
	}

	private void initializeView() {
		setLayout(new MigLayout("insets 5, fill"));

		if (mode != ITEM_VIEW_MODE_CUSTOM) {
			History lastHistory = item.getSync().getUpdatesHistory().peek();
			add(new JLabel("<html><b>By: </b>" + lastHistory.getBy() + "</html>"), "wrap");
			add(new JLabel("<html><b>Date: </b>" + dateFormat.format(lastHistory.getWhen()) + "</html>"), "wrap");
		} else {
			add(new JLabel("Manually enter a new version"), "wrap");
			add(new JLabel(" "), "wrap");
		}
		
		DefaultTableModel model = new DefaultTableModel(new String[] { "Field", "Value" }, 0);
		JTable table;
		add(new JScrollPane(table = new JTable(model)), "push, wrap");
		table.getColumnModel().getColumn(1).setCellEditor(new ValueEditor());
		
		add(new JButton(chooseAction = new AbstractAction(mode == ITEM_VIEW_MODE_CURRENT ? "Leave as winner" : "Choose as winner") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(conflictsView, "Are you sure you want to choose this revision?", "Choose revision", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					conflictsView.chooseConflictWinner(item);
				}	
			}
		}), "dock south");
		
		for (String key : propertyValues.keySet()) {
			Object value = null;
			if (mode == ITEM_VIEW_MODE_CUSTOM) {
				Set<Object> values = propertyValues.get(key);
				if (values.size() == 1)
					value = values.iterator().next();
			} else {
				value = String.valueOf(properties.get(key));
			}
			model.addRow(new Object[] { key, value });
		}
	}
	
	private Item getItem() {
		if (item != null)
			return item;
		
		Item item = new Item(null, null);
		return item;
	}
	
	private class ValueEditor extends DefaultCellEditor {

		public ValueEditor() {
			super(new JComboBox());
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			JComboBox combo = (JComboBox) super.getTableCellEditorComponent(table, value, isSelected, row, column);
			combo.removeAllItems();
			String key = table.getValueAt(row, 0).toString();
			Set<Object> values = ItemView.this.propertyValues.get(key);
			for (Object v : values) {
				combo.addItem(v);
			}
			combo.setSelectedItem(value);
			
			return combo;
		}
	}
}
