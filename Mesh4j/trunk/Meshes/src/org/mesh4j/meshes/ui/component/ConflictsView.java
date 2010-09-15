package org.mesh4j.meshes.ui.component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

import org.mesh4j.meshes.model.DataSource;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.model.Item;

@SuppressWarnings("serial")
public class ConflictsView extends JPanel {

	private DataSource dataSource;
	private DefaultTableModel conflictsModel;

	public ConflictsView(DataSource dataSource) {
		this.dataSource = dataSource;
		initializeView();
		loadConflicts();
	}
	
	private void initializeView() {
		setLayout(new MigLayout("insets 10, fill"));

		add(new JLabel("Items:"), "wrap");
		JTable conflictTable = new JTable();
		add(new JScrollPane(conflictTable), "growx, wrap");
		conflictsModel = new DefaultTableModel(new String[] { "Item ID", "# of Conflicting Versions", "Conflicting Users" }, 0);
		conflictTable.setModel(conflictsModel);
	}
	
	private void loadConflicts() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				ISyncAdapter syncAdapter = dataSource.createSyncAdapter();
				List<Item> itemsWithConflicts = syncAdapter.getConflicts();

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
}
