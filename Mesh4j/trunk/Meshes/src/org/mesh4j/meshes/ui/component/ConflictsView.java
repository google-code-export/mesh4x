package org.mesh4j.meshes.ui.component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

import org.mesh4j.meshes.model.DataSource;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.split.IContentAdapter;
import org.mesh4j.sync.adapters.split.IIdentifiableContentAdapter;
import org.mesh4j.sync.adapters.split.SplitAdapter;
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
		add(new JScrollPane(versionsPanel = new JPanel(new MigLayout("insets 0, fill")), JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), "growx, height 50%!");
	}
	
	private void loadConflicts() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				syncAdapter = dataSource.createSyncAdapter();
				
				if (syncAdapter instanceof SplitAdapter) {
					IContentAdapter contentAdapter = ((SplitAdapter)syncAdapter).getContentAdapter();
					if (contentAdapter instanceof IIdentifiableContentAdapter) {
						schema = ((IIdentifiableContentAdapter) contentAdapter).getSchema();
					}
				}
				
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
		Item item = itemsWithConflicts.get(conflictTable.getSelectedRow());
		versionsPanel.removeAll();
		
		ItemView itemView;
		versionsPanel.add(itemView = new ItemView(this, item, schema), "growy, width 200px::");
		versionsPanel.getParent().doLayout();
		versionsPanel.doLayout();
		itemView.doLayout();
		
		for (Item conflictItem : item.getSync().getConflicts()) {
			versionsPanel.add(itemView = new ItemView(this, conflictItem, schema), "growy, width 200px::");
			versionsPanel.getParent().doLayout();
			versionsPanel.doLayout();
			itemView.doLayout();
		}
	}

	public void chooseConflictWinner(Item winner) {
		Item item = itemsWithConflicts.get(conflictTable.getSelectedRow());
		Item resolvedItem = new Item(winner.getContent(), item.getSync());
		
		syncAdapter.update(resolvedItem, true);
	}
}
