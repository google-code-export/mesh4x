package org.mesh4j.meshes.ui.component;

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Scrollable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

import org.mesh4j.meshes.io.ConfigurationManager;
import org.mesh4j.meshes.model.DataSource;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.schema.ISchema;

@SuppressWarnings("serial")
public class ConflictsView extends JPanel implements ListSelectionListener {

	private DataSource dataSource;
	private DefaultTableModel conflictsModel;
	private JPanel versionsPanel;
	private List<Item> itemsWithConflicts;
	private ISyncAdapter syncAdapter;
	private ISchema schema;
	private JTable conflictTable;

	public ConflictsView(DataSource dataSource) {
		this.dataSource = dataSource;
		initializeView();
		loadConflicts();
	}
	
	private void initializeView() {
		setLayout(new MigLayout("insets 10, fill"));

		add(new JLabel("Items:"), "wrap");
		conflictTable = new JTable();
		add(new JScrollPane(conflictTable), "growx, wrap");
		conflictsModel = new DefaultTableModel(new String[] { "Item ID", "# of Conflicting Versions", "Conflicting Users" }, 0);
		conflictTable.setModel(conflictsModel);
		conflictTable.getSelectionModel().addListSelectionListener(this);
		
		add(new JLabel("Conflicting versions:"), "wrap");
		add(new JScrollPane(versionsPanel = new LPMPanel(new MigLayout("insets 0, fill, nogrid")), JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), "growx, height 60%!");
	}
	
	private class LPMPanel extends JPanel implements Scrollable {

		public LPMPanel(LayoutManager layout) {
			super(layout);
			setBackground(null);
		}
		
		@Override
		public Dimension getPreferredScrollableViewportSize() {
			return new Dimension(1, 1);
		}

		@Override
		public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
			if (getComponentCount() > 0) {
				return getComponent(0).getWidth();
			}
			return 100;
		}

		@Override
		public boolean getScrollableTracksViewportHeight() {
			return true;
		}

		@Override
		public boolean getScrollableTracksViewportWidth() {
			return false;
		}

		@Override
		public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
			return 10;
		}
		
	}
	
	private void loadConflicts() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				syncAdapter = dataSource.createSyncAdapter();
				schema = syncAdapter.getSchema();
				itemsWithConflicts = syncAdapter.getConflicts();

				for (Item item : itemsWithConflicts) {
					Set<String> users = new HashSet<String>();
					users.add(getItemAuthor(item));
					for (Item conflictItem : item.getSync().getConflicts()) {
						users.add(getItemAuthor(conflictItem));
					}

					StringBuilder conflictingUsers = new StringBuilder(); 
					for (String user : users) {
						if (conflictingUsers.length() > 0)
							conflictingUsers.append(", ");
						conflictingUsers.append(user);
					}

					conflictsModel.addRow(new String[] { item.getSyncId(), Integer.toString(item.getSync().getConflicts().size() + 1), conflictingUsers.toString() });
				}
			}
		}).start();
	}
	private String getItemAuthor(Item item) {
		return item.getSync().getUpdatesHistory().peek().getBy();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) return;
		
		int selectedRow = conflictTable.getSelectedRow();
		if (selectedRow < 0) {
			versionsPanel.removeAll();
			versionsPanel.getParent().doLayout();
			versionsPanel.doLayout();
			return;
		}
		
		Item item = itemsWithConflicts.get(selectedRow);
		
		versionsPanel.removeAll();
		
		Map<String, Set<Object>> propertyValues = new HashMap<String, Set<Object>>();
		addItemView(propertyValues, item.getContent().getId(), item, ItemView.ITEM_VIEW_MODE_CURRENT);
		for (Item conflictItem : item.getSync().getConflicts()) {
			addItemView(propertyValues, item.getContent().getId(), conflictItem, ItemView.ITEM_VIEW_MODE_NORMAL);
		}
		addItemView(propertyValues, item.getContent().getId(), null, ItemView.ITEM_VIEW_MODE_CUSTOM);
	}
	
	private void addItemView(Map<String, Set<Object>> propertyValues, String id, Item item, int mode) {
		ItemView itemView;
		Map<String, Object> values = null;
		if (item != null) {
			values = collectPropertyValues(propertyValues, item);
		}
		versionsPanel.add(itemView = new ItemView(this, schema, id, item, values, propertyValues, mode), "growy, width 200px!, align left");
		versionsPanel.getParent().doLayout();
		versionsPanel.doLayout();
		itemView.doLayout();
	}
	
	private Map<String, Object> collectPropertyValues(Map<String, Set<Object>> propertyValues, Item item) {
		Map<String, Object> properties = schema.getPropertiesAsMap(item.getContent().getPayload());
		for (String key : properties.keySet()) {
			Set<Object> values = propertyValues.get(key);
			if (values == null) {
				values = new HashSet<Object>();
				propertyValues.put(key, values);
			}
			values.add(properties.get(key));
		}
		
		return properties;
	}

	public void chooseConflictWinner(IContent content) {
		int conflictRow = conflictTable.getSelectedRow();
		
		Item item = itemsWithConflicts.get(conflictRow);
		Item resolvedItem = new Item(content, item.getSync());
		
		syncAdapter.update(resolvedItem, true);
		
		conflictsModel.removeRow(conflictRow);
		
		if (conflictsModel.getRowCount() == 0) {
			try {
				dataSource.setHasConflicts(false);
				ConfigurationManager.getInstance().saveMesh(dataSource.getDataSet().getMesh());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
