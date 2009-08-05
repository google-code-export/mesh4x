package org.mesh4j.ektoo.ui.conflicts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.mesh4j.ektoo.tasks.IErrorListener;
import org.mesh4j.ektoo.tasks.ResolveConflictsTask;
import org.mesh4j.ektoo.ui.EktooFrame;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.adapters.InMemorySyncAdapter;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.merge.IConflictResolutionListener;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.utils.XMLHelper;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class ConflictsUI extends JPanel implements IErrorListener, IConflictResolutionListener{

	private static final long serialVersionUID = 117588385119200451L;
	public final static Log LOGGER = LogFactory.getLog(ConflictsUI.class);

	// MODEL VARIABLES
	private JTextArea labelStatus;
	private HashMap<String, Item> conflicts = new HashMap<String, Item>();
	private HashMap<String, IWinnerUI> winnerConflicts = new HashMap<String, IWinnerUI>();
	private HashMap<String, JScrollPane> winnerPanels = new HashMap<String, JScrollPane>();
	private JScrollPane currentPanel;
 
	private EktooFrame ownerUI;
	private ISyncAdapter adapter;	
	private int errors = 0;
	
	// BUSINESS METHODS
	
	public ConflictsUI(EktooFrame ui, ISyncAdapter syncAdapter, IRDFSchema rdfSchema) {
		super();
		this.ownerUI = ui;
		this.adapter = syncAdapter;
		
		List<Item> conflictingItems = getConflicts();
		
		setBackground(Color.WHITE);
		setBounds(100, 100, 652, 564);
		setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("6dlu"),
				ColumnSpec.decode("320dlu")},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("81dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("203dlu"),
				RowSpec.decode("48dlu")}));

		final JPanel panelButtons = new JPanel();
		panelButtons.setBackground(Color.WHITE);
		panelButtons.setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("202dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("30dlu"),
				ColumnSpec.decode("6dlu"),
				ColumnSpec.decode("71dlu")},
			new RowSpec[] {
				RowSpec.decode("18dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("18dlu"),
				FormFactory.RELATED_GAP_ROWSPEC}));
		add(panelButtons, new CellConstraints(2, 5));

		final JButton buttonSave = new JButton();
		buttonSave.setText(EktooUITranslator.getConflictLabelSave());
		buttonSave.setContentAreaFilled(false);
		buttonSave.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonSave.setBorderPainted(false);
		buttonSave.setOpaque(false);
		buttonSave.setFont(new Font("", Font.BOLD, 12));
		buttonSave.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e) {
				ResolveConflictsTask task = new ResolveConflictsTask(ConflictsUI.this, adapter, conflicts, getWinners());
				task.execute();
			}			
		});
		buttonSave.setEnabled(!conflictingItems.isEmpty());
		panelButtons.add(buttonSave, new CellConstraints(6, 1, CellConstraints.FILL, CellConstraints.FILL));

		final JButton buttonCancel = new JButton();
		buttonCancel.setText(EktooUITranslator.getConflictLabelCancel());
		buttonCancel.setContentAreaFilled(false);
		buttonCancel.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonCancel.setBorderPainted(false);
		buttonCancel.setOpaque(false);
		buttonCancel.setFont(new Font("", Font.BOLD, 12));
		buttonCancel.addActionListener(new ActionListener(){@Override public void actionPerformed(ActionEvent e) {closePopupViewWindow();}});
		panelButtons.add(buttonCancel, new CellConstraints(4, 1, CellConstraints.FILL, CellConstraints.FILL));
		
		labelStatus = new JTextArea();
		labelStatus.setLineWrap(true);
		labelStatus.setText("");
		labelStatus.setWrapStyleWord(true);
		panelButtons.add(labelStatus, new CellConstraints(2, 1, CellConstraints.FILL, CellConstraints.BOTTOM));
		
		for (Item item : conflictingItems) {
			this.conflicts.put(item.getSyncId(), item);
			makeConflictPanel(rdfSchema, item);	
		}

		makeEmptyPanel();
		
		if(conflictingItems.isEmpty()){
			this.currentPanel = this.winnerPanels.get("");
			currentPanel.setVisible(true);
		} else {
			this.currentPanel = this.winnerPanels.get(conflictingItems.get(0).getSyncId());
			currentPanel.setVisible(true);
		}
		
		final JPanel panelTable = new JPanel();
		panelTable.setBorder(new TitledBorder(new LineBorder(Color.gray), EktooUITranslator.getConflictLabelConflictTable(), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		panelTable.setLayout(new BorderLayout());
		panelTable.setBackground(Color.WHITE);

		ConflictsUITableModel tableModel = new ConflictsUITableModel(conflictingItems);
		
		final JTable table = new JTable();
		table.setModel(tableModel);
		table.setFillsViewportHeight(true);
		table.getSelectionModel().setSelectionInterval(0, 0);
		
		ListSelectionListener selectionListener = new ListSelectionListener() {
		    public void valueChanged(ListSelectionEvent e) {
		        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
		    
		        int firstIndex = e.getFirstIndex();
		        int lastIndex = e.getLastIndex();

	        	if(lsm.isSelectedIndex(firstIndex)) {
	        		String syncId = (String)table.getModel().getValueAt(firstIndex, 0);
	        		if(currentPanel != null){
	        			currentPanel.setVisible(false);
	        		}	        		
	        		currentPanel = winnerPanels.get(syncId);
	        		
	        		if(currentPanel != null){
	        			currentPanel.setVisible(true);
	        		}
	        	} else if(lsm.isSelectedIndex(lastIndex)) {
	        		String syncId = (String)table.getModel().getValueAt(lastIndex, 0);
	        		if(currentPanel != null){
	        			currentPanel.setVisible(false);
	        		}	        		
	        		currentPanel = winnerPanels.get(syncId);
	        		
	        		if(currentPanel != null){
	        			currentPanel.setVisible(true);
	        		}	        		
	        	} else {
	        		String syncId = "";
	        		if(currentPanel != null){
	        			currentPanel.setVisible(false);
	        		}	        		
	        		currentPanel = winnerPanels.get(syncId);
	        		
	        		if(currentPanel != null){
	        			currentPanel.setVisible(true);
	        		}	
	        	}
		    }
		};

		
		table.getSelectionModel().addListSelectionListener(selectionListener);
		
		
		panelTable.add(table.getTableHeader(), BorderLayout.PAGE_START);
		panelTable.add(table, BorderLayout.CENTER);

		add(panelTable, new CellConstraints(2, 2, CellConstraints.FILL, CellConstraints.FILL));
	}

	private List<Item> getConflicts() {
		if(adapter instanceof ISyncAware){
			((ISyncAware) adapter).beginSync();
		}
		
		List<Item> conflictingItems = adapter.getConflicts();			
		
		if(adapter instanceof ISyncAware){
			((ISyncAware) adapter).endSync();
		}
		return conflictingItems;
	}
	
	private void makeConflictPanel(IRDFSchema rdfSchema, Item item) {
		JPanel panelEdit = new JPanel();
		panelEdit.setBorder(new TitledBorder(new LineBorder(Color.gray), EktooUITranslator.getConflictLabelConflictDetails(), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		panelEdit.setBackground(Color.WHITE);

		List<Item> allItemConflicts = item.getSync().getConflicts();

		int columnSpectSize = (allItemConflicts.size() *2) + 2;
		columnSpectSize = columnSpectSize + 2;   // for item editor ui
				
		ColumnSpec[] columnSpec = new ColumnSpec[columnSpectSize];
		for (int i = 0; i < columnSpectSize; i++) {
			if(i%2 != 0){
				columnSpec[i]=ColumnSpec.decode("100dlu");
			} else {
				columnSpec[i]= FormFactory.RELATED_GAP_COLSPEC;
			}
		}
		
		panelEdit.setLayout(new FormLayout(
			columnSpec,
			new RowSpec[] {
				RowSpec.decode("170dlu"),
				FormFactory.RELATED_GAP_ROWSPEC}));

		ConflictItemUI winnerUI = new ConflictItemUI(this, item, rdfSchema, true);
		panelEdit.add(winnerUI, new CellConstraints(2, 1, CellConstraints.FILL, CellConstraints.FILL));
		this.winnerConflicts.put(item.getSyncId(), winnerUI);		
		
		for (int i = 0; i < allItemConflicts.size(); i++) {
			Item conflict = allItemConflicts.get(i);
			ConflictItemUI conflictItemUI = new ConflictItemUI(this, conflict, rdfSchema, false);
			panelEdit.add(conflictItemUI, new CellConstraints((i*2)+4, 1, CellConstraints.FILL, CellConstraints.FILL));
		}

		ItemEditorUI itemEditor = new ItemEditorUI(this, rdfSchema, item);
		panelEdit.add(itemEditor, new CellConstraints(columnSpectSize, 1, CellConstraints.FILL, CellConstraints.FILL));
		
		
		JScrollPane scrollPane = new JScrollPane(panelEdit);
		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		scrollPane.setVisible(false);
		add(scrollPane, new CellConstraints(2, 4, CellConstraints.FILL, CellConstraints.FILL));
		
		this.winnerPanels.put(item.getSyncId(), scrollPane);
	}
	
	private void makeEmptyPanel() {
		JPanel panelEdit = new JPanel();
		panelEdit.setBorder(new TitledBorder(new LineBorder(Color.gray), EktooUITranslator.getConflictLabelConflictDetails(), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		panelEdit.setBackground(Color.WHITE);

		panelEdit.setLayout(new FormLayout(
			new ColumnSpec[]{
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("100dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("100dlu")
			},
			new RowSpec[] {
				RowSpec.decode("170dlu"),
				FormFactory.RELATED_GAP_ROWSPEC}));

		ConflictItemUI winnerUI = new ConflictItemUI(this, null, null, true);
		panelEdit.add(winnerUI, new CellConstraints(2, 1, CellConstraints.FILL, CellConstraints.FILL));
		this.winnerConflicts.put(winnerUI.getSyncId(), winnerUI);	
		
		ConflictItemUI conflictItemUI = new ConflictItemUI(this, null, null, false);
		panelEdit.add(conflictItemUI, new CellConstraints(4, 1, CellConstraints.FILL, CellConstraints.FILL));

		JScrollPane scrollPane = new JScrollPane(panelEdit);
		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		scrollPane.setVisible(false);
		add(scrollPane, new CellConstraints(2, 4, CellConstraints.FILL, CellConstraints.FILL));
		
		this.winnerPanels.put(winnerUI.getSyncId(), scrollPane);
	}

	public void setWinner(IWinnerUI newWinner) {
		String syncId = newWinner.getSyncId();
		IWinnerUI winner = this.winnerConflicts.get(syncId);
		if(winner != null){
			winner.setAsConflict();
		}
		this.winnerConflicts.put(syncId, newWinner);		
	}	
	
	protected List<Item> getWinners() {
		List<Item> winners = new ArrayList<Item>();
		for (IWinnerUI ui : winnerConflicts.values()) {
			Item item = ui.getWinner();
			if(item != null){
				winners.add(item);
			}
		}
		return winners;
	}

	// TableModel
	class ConflictsUITableModel extends AbstractTableModel {
		
		private static final long serialVersionUID = -391843927926100462L;
		
		// MODEL VARIABLES
		private String[] columns;
		private String[][] cells;
		
		// BUSINESS METHODS
		
		public ConflictsUITableModel(List<Item> conflicts) {
			
			this.columns = new String[]{
				EktooUITranslator.getConflictLabelSyncId(), 
				EktooUITranslator.getConflictLabelNumberOfConflictVersions(), 
				EktooUITranslator.getConflictLabelUsers()
			};
			
			if(conflicts.isEmpty()){
				this.cells = new String[1][3];
				this.cells[0][0]="";
				this.cells[0][1]="";
				this.cells[0][2]="";
				
			} else {
				this.cells = new String[conflicts.size()][3];
		
				for (int i = 0; i < conflicts.size(); i++) {
					Item item = conflicts.get(i);			
					
					StringBuffer sb = new StringBuffer();
					sb.append(item.getLastUpdate().getBy());
					sb.append(",");
					
					for (Item conflictedItem : item.getSync().getConflicts()) {
						sb.append(conflictedItem.getLastUpdate().getBy());
						sb.append(",");
					}
					this.cells[i]=new String[]{item.getSyncId(), String.valueOf(item.getSync().getConflicts().size()), sb.toString()};
				}
			}			
		}

		public int getRowCount() {
			return this.cells.length;
		}

		public int getColumnCount() {
			return this.columns.length;
		}

		public String getColumnName(int column) {
			return this.columns[column];
		}

		public Object getValueAt(int row, int column) {
			return this.cells[row].length > column ? this.cells[row][column] : "";
		}
		
		@Override
		public boolean isCellEditable(int x, int y){
			return false;
		}
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					boolean useRDF = false;
					RDFSchema rdfSchema = null;
					List<Item> conflicts = new ArrayList<Item>();
					 
					if(useRDF){
				        rdfSchema = new RDFSchema("Oswego", "http://mesh4x/Oswego#", "Oswego");
				        rdfSchema.addStringProperty("code", "Code", "en");
				        rdfSchema.addStringProperty("name", "Name", "en");
				        rdfSchema.addIntegerProperty("age", "Age", "en");
				        rdfSchema.addBooleanProperty("ill", "is Ill", "en");
				        rdfSchema.addDateTimeProperty("dateOnset", "DateOnset", "en");
				        rdfSchema.setIdentifiablePropertyName("code");
				        
				        
				        String code = "1";
				        RDFInstance instance = rdfSchema.createNewInstance("uri:urn:"+code);
				        instance.setProperty("code", code);
				        instance.setProperty("name", "juan marcelo");
				        instance.setProperty("age", 35);
				        instance.setProperty("ill", true);
				        instance.setProperty("dateOnset", new Date());
				        
				        RDFInstance instanceConflict = rdfSchema.createNewInstance("uri:urn:"+code);
				        instanceConflict.setProperty("code", code);
				        instanceConflict.setProperty("name", "juan marcelo");
				        instanceConflict.setProperty("age", 35);
				        instanceConflict.setProperty("ill", false);
				        instanceConflict.setProperty("dateOnset", new Date());
				        
				        // item1 
				        Item item = new Item(
				        	new XMLContent(code, "title", "desc", instance.asElementRDFXML()), 
				        	new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false));
				        
				        Sync conflictSync = item.getSync().clone();
				        
				        item.getSync().update("jmt", new Date());
				        
				        Item conflict = new Item(
				        	new XMLContent(code, "title", "desc", instanceConflict.asElementRDFXML()), 
					        conflictSync.update("bia", new Date()));
				        			        
				        item.getSync().markWithConflicts();
				        item.getSync().addConflict(conflict);
				        
				        conflicts.add(item);
				        
				        // item2
				        
				        String code2 = "2";
				        RDFInstance instance2 = rdfSchema.createNewInstance("uri:urn:"+code2);
				        instance2.setProperty("code", code2);
				        instance2.setProperty("name", "josefa");
				        instance2.setProperty("age", 35);
				        instance2.setProperty("ill", true);
				        instance2.setProperty("dateOnset", new Date());
				        
				        RDFInstance instanceConflict2 = rdfSchema.createNewInstance("uri:urn:"+code2);
				        instanceConflict2.setProperty("code", code2);
				        instanceConflict2.setProperty("name", "pepe");
				        instanceConflict2.setProperty("age", 35);
				        instanceConflict2.setProperty("ill", false);
				        instanceConflict2.setProperty("dateOnset", new Date());
				        
				        Item item2 = new Item(
					        	new XMLContent(code, "title", "desc", instance2.asElementRDFXML()), 
					        	new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false));
					        
				        Sync conflictSync2 = item2.getSync().clone();
				        Sync conflictSync3 = item2.getSync().clone();
				        Sync conflictSync4 = item2.getSync().clone();
				        Sync conflictSync5 = item2.getSync().clone();
				        
				        //item2.getSync().update("jmt", new Date());
				        item2.getSync().delete("jmt", new Date());
				        
				        Item conflict2 = new Item(
				        	new XMLContent(code, "title", "desc", instanceConflict2.asElementRDFXML()), 
					        conflictSync2.update("bia", new Date()));
				        
				        Item conflict3 = new Item(
					        	new XMLContent(code, "title", "desc", instanceConflict2.asElementRDFXML()), 
						        conflictSync3.update("bia", new Date()));
				        
				        Item conflict4 = new Item(
					        	new XMLContent(code, "title", "desc", instanceConflict2.asElementRDFXML()), 
						        conflictSync4.update("bia", new Date()));
				        
				        Item conflict5 = new Item(
					        	new NullContent(code), 
						        conflictSync5.delete("bia", new Date()));
				        			        
				        item2.getSync().markWithConflicts();
				        item2.getSync().addConflict(conflict2);
				        item2.getSync().addConflict(conflict3);
				        item2.getSync().addConflict(conflict4);
				        item2.getSync().addConflict(conflict5);
				        
				        conflicts.add(item2);
					} else{
						
						Element payload = XMLHelper.parseElement("<foo><bar>1</bar></foo>");
						
						// item1 
						String code = "1";
				        Item item = new Item(
				        	new XMLContent(code, "title", "desc", payload), 
				        	new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false));
				        
				        Sync conflictSync = item.getSync().clone();
				        
				        item.getSync().update("jmt", new Date());
				        
				        Item conflict = new Item(
				        	new XMLContent(code, "title", "desc", payload), 
					        conflictSync.update("bia", new Date()));
				        			        
				        item.getSync().markWithConflicts();
				        item.getSync().addConflict(conflict);
				        
				        conflicts.add(item);
				        
				        // item2
				        
				        String code2 = "2";
				        
				        Item item2 = new Item(
					        	new XMLContent(code2, "title", "desc", payload), 
					        	new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false));
					        
				        Sync conflictSync2 = item2.getSync().clone();
				        Sync conflictSync3 = item2.getSync().clone();
				        Sync conflictSync4 = item2.getSync().clone();
				        Sync conflictSync5 = item2.getSync().clone();
				        
				        //item2.getSync().update("jmt", new Date());
				        item2.getSync().delete("jmt", new Date());
				        
				        Item conflict2 = new Item(
				        	new XMLContent(code2, "title", "desc", payload), 
					        conflictSync2.update("bia", new Date()));
				        
				        Item conflict3 = new Item(
					        	new XMLContent(code2, "title", "desc", payload), 
						        conflictSync3.update("bia", new Date()));
				        
				        Item conflict4 = new Item(
					        	new XMLContent(code2, "title", "desc", payload), 
						        conflictSync4.update("bia", new Date()));
				        
				        Item conflict5 = new Item(
					        	new NullContent(code2), 
						        conflictSync5.delete("bia", new Date()));
				        			        
				        item2.getSync().markWithConflicts();
				        item2.getSync().addConflict(conflict2);
				        item2.getSync().addConflict(conflict3);
				        item2.getSync().addConflict(conflict4);
				        item2.getSync().addConflict(conflict5);
				        
				        conflicts.add(item2);
					}
										
			        // ui
					InMemorySyncAdapter adapter = new InMemorySyncAdapter("", NullIdentityProvider.INSTANCE, conflicts);
			        ConflictsUI ui = new ConflictsUI(null, adapter, rdfSchema);
					ui.setVisible(true);
					
					JDialog popupviewWindow = new JDialog();
					popupviewWindow.setLayout(new BorderLayout());
					popupviewWindow.add(ui);
					popupviewWindow.setSize(ui.getWidth(), ui.getHeight());
					popupviewWindow.pack();
					popupviewWindow.setResizable(false);
					popupviewWindow.setVisible(true);
					
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		});
	}
	

	public void closePopupViewWindow() {
		this.ownerUI.closePopupViewWindow();		
	}
	

	// IErrorListener

	@Override
	public void notifyError(String error) {
		labelStatus.setForeground(Color.red);
		labelStatus.setText(error);
	}
	
	public void notifyStatusMessage(String msg) {
		labelStatus.setText(msg);
	}
	
	// IConflictResolutionListener
	
	@Override
	public void notifyConflictResolutionError(String syncId, Throwable e) {
		LOGGER.error(e.getMessage(), e);
		this.errors = this.errors + 1;
		this.notifyError(EktooUITranslator.getConflictsResolutionMessageProcessError(syncId));		
	}

	@Override
	public void notifyConflictResolutionDone() {
		if(this.errors > 0){
			this.ownerUI.notifyError(EktooUITranslator.getConflictsResolutionMessageFailed(this.errors));
		} else {
			this.ownerUI.notifyStatusMessage(EktooUITranslator.getConflictsResolutionMessageSuccessful());
		}
		this.closePopupViewWindow();
	}

	@Override
	public void notifyEndConflictResolution() {
		this.notifyStatusMessage(EktooUITranslator.getConflictsResolutionMessageProcessEnd());
	}

	@Override
	public void notifyResolvingConflict(String syncId, int i, int size) {
		this.notifyStatusMessage(EktooUITranslator.getConflictsResolutionMessageProcessResolving(syncId, i, size));		
	}

	@Override
	public void notifyStartConflictResolution() {
		this.errors = 0;
		this.notifyStatusMessage(EktooUITranslator.getConflictsResolutionMessageProcessStart());		
	}

	@Override
	public void notifyUpdatingConflict(String syncId, int i, int size) {
		this.notifyStatusMessage(EktooUITranslator.getConflictsResolutionMessageProcessUpdating(syncId, i, size));		
	}

	@Override
	public void notifyUpdatingConflicts(int size) {
		this.notifyStatusMessage(EktooUITranslator.getConflictsResolutionMessageProcessUpdating(size));
	}
}
