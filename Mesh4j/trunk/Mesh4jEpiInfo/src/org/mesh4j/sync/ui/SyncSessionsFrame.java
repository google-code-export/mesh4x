package org.mesh4j.sync.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.mappings.EndpointMapping;
import org.mesh4j.sync.mappings.MSAccessDataSourceMapping;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.MessageSyncEngine;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.properties.PropertiesProvider;
import org.mesh4j.sync.ui.tasks.IErrorListener;
import org.mesh4j.sync.ui.tasks.OpenFileTask;
import org.mesh4j.sync.ui.translator.MeshCompactUITranslator;
import org.mesh4j.sync.ui.utils.IconManager;
import org.mesh4j.sync.utils.EndpointProvider;
import org.mesh4j.sync.utils.SourceIdMapper;
import org.mesh4j.sync.utils.SyncEngineUtil;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class SyncSessionsFrame extends JFrame implements ISyncSessionViewOwner, WindowFocusListener, IErrorListener{
	
	private static final long serialVersionUID = 142343742087435808L;

	// MODEL VARIABLES
	private DefaultMutableTreeNode rootNode;
	private JTree treeSessions;
	private JButton buttonSyncSession;
	private JButton buttonOpenDataSource;
	
	private MessageSyncEngine syncEngine;
	private SourceIdMapper sourceIdMapper;
	private PropertiesProvider propertiesProvider;
	private SyncSessionView syncSessionView;
	private MeshCompactUI owner;

	// BUSINESS METHODS

	public SyncSessionsFrame(MeshCompactUI ui, SourceIdMapper ownerSourceIdMapper, PropertiesProvider ownerPropertiesProvider) {
		super();
		
		this.owner = ui;
		this.propertiesProvider = ownerPropertiesProvider;
		this.sourceIdMapper = ownerSourceIdMapper;
		
//		setAlwaysOnTop(true);
		setIconImage(IconManager.getCDCImage());
		getContentPane().setBackground(Color.WHITE);
		setTitle(MeshCompactUITranslator.getSyncSessionWindowTitle());
		setResizable(false);
		setBounds(100, 100, 827, 394);
		getContentPane().setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("408dlu")},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("200dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC}));

		final JPanel panelButtons = new JPanel();
		panelButtons.setBackground(Color.WHITE);
		panelButtons.setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("17dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("54dlu")},
			new RowSpec[] {
				RowSpec.decode("12dlu")}));
		getContentPane().add(panelButtons, new CellConstraints(2, 4));

		final JButton buttonClose = new JButton();
		buttonClose.setOpaque(true);
		buttonClose.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonClose.setBorderPainted(false);
		buttonClose.setContentAreaFilled(false);
		buttonClose.setFont(new Font("Calibri", Font.BOLD, 12));
		buttonClose.setText(MeshCompactUITranslator.getSyncSessionWindowLabelClose());
		buttonClose.setToolTipText(MeshCompactUITranslator.getSyncSessionWindowToolTipClose());
		
		ActionListener closeActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				SyncSessionsFrame.this.setVisible(false);
			}
		};
		
		buttonClose.addActionListener(closeActionListener);
		
		panelButtons.add(buttonClose, new CellConstraints(1, 1, CellConstraints.FILL, CellConstraints.FILL));

		ActionListener syncActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeSessions.getLastSelectedPathComponent();
    			
    			if (node == null){
    				return;
    			}
    			
    			if(isSyncSession(node)){
    				owner.viewSyncSession(syncSessionView.getSyncSession());
    				
    				JFrame ownerFrame = owner.getFrame();
    				if(ownerFrame.isVisible()){
    					ownerFrame.toFront();
    				} else {
    					ownerFrame.pack();
    					ownerFrame.setVisible(true);
    				}		
    			} else if(isCloudSync(node)){
    				
    				CloudSyncSessionWrapper cloudSyncSessionWrapper = (CloudSyncSessionWrapper) node.getUserObject();
    				
    				SyncCloudFrame cloudFrame = owner.getSyncCloudFrame();
    				
    				cloudFrame.viewCloudSession(
    					cloudSyncSessionWrapper.getUrl(), 
    					cloudSyncSessionWrapper.getSourceId(),
    					cloudSyncSessionWrapper.getSyncMode());
    				
    				if(cloudFrame.isVisible()){
    					cloudFrame.toFront();
    				} else {
    					cloudFrame.pack();
    					cloudFrame.setVisible(true);
    				}	
    			}
			}
		};	
		
		buttonSyncSession = new JButton();
		buttonSyncSession.setOpaque(true);
		buttonSyncSession.setContentAreaFilled(false);
		buttonSyncSession.setBorderPainted(false);
		buttonSyncSession.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonSyncSession.setFont(new Font("Calibri", Font.BOLD, 12));
		buttonSyncSession.setText(MeshCompactUITranslator.getSyncSessionWindowLabelChooseSync());
		buttonSyncSession.setToolTipText(MeshCompactUITranslator.getSyncSessionWindowToolTipChooseSync());
		buttonSyncSession.addActionListener(syncActionListener);
		panelButtons.add(buttonSyncSession, new CellConstraints(5, 1, CellConstraints.FILL, CellConstraints.FILL));

		ActionListener openDataSourceActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeSessions.getLastSelectedPathComponent();
    			
    			if (node == null){
    				return;
    			}
    			
    			String sourceId = null;
    			
    			if(isDataSource(node)){
    				sourceId= (String)node.getUserObject();
    			} else if(isSyncSession(node)){
   					sourceId = ((SyncSessionWrapper) node.getUserObject()).getSourceId();		
    			} else if(isCloudSync(node)){
   					sourceId = ((CloudSyncSessionWrapper) node.getUserObject()).getSourceId();		
    			}			
    			
    			if(sourceId != null){
    				MSAccessDataSourceMapping dataSource = sourceIdMapper.getDataSource(sourceId);
    				if(dataSource != null){
    					OpenFileTask task = new OpenFileTask(SyncSessionsFrame.this, SyncSessionsFrame.this, dataSource.getFileName());
    					task.execute();
    				}
				}
			}
		};	
		
		buttonOpenDataSource = new JButton();
		buttonOpenDataSource.setOpaque(true);
		buttonOpenDataSource.setContentAreaFilled(false);
		buttonOpenDataSource.setBorderPainted(false);
		buttonOpenDataSource.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonOpenDataSource.setFont(new Font("Calibri", Font.BOLD, 12));
		buttonOpenDataSource.setText(MeshCompactUITranslator.getSyncSessionWindowLabelOpenDataSource());
		buttonOpenDataSource.setToolTipText(MeshCompactUITranslator.getTooltipViewDataSource());
		buttonOpenDataSource.addActionListener(openDataSourceActionListener);
		panelButtons.add(buttonOpenDataSource, new CellConstraints(3, 1, CellConstraints.FILL, CellConstraints.FILL));
	
		rootNode = new DefaultMutableTreeNode(MeshCompactUITranslator.getSyncSessionWindowLabelAllSessions());
		createSyncSessionTreeModel();
		
	    treeSessions = new JTree(rootNode);
	    treeSessions.setBorder(new EmptyBorder(0, 0, 0, 0));
	    treeSessions.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	    treeSessions.setShowsRootHandles(true);
	    treeSessions.setCellRenderer(new TreeSessionRenderer());
	    
	    treeSessions.addTreeSelectionListener(
	    	new TreeSelectionListener() {
	    		public void valueChanged(TreeSelectionEvent e) {
	    			DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeSessions.getLastSelectedPathComponent();
	    			
	    			if (node == null){
	    				syncSessionView.setVisible(true);
	    				syncSessionView.viewSession(null);
	    				buttonOpenDataSource.setEnabled(false);
	    				buttonSyncSession.setEnabled(false);
	    				return;
	    			}
	    			
	    			if(isDataSource(node)){
	    				syncSessionView.setVisible(true);
	    				syncSessionView.viewSession(null);
	    				buttonOpenDataSource.setEnabled(true);
		    			buttonSyncSession.setEnabled(false);
	    			} else if(isSyncSession(node)){
	    				syncSessionView.setVisible(true);
	    				SyncSessionWrapper syncSessionWrapper = (SyncSessionWrapper)(node.getUserObject());
	    				syncSessionView.viewSession(syncSessionWrapper.getSyncSession());
	    				 
	    				buttonOpenDataSource.setEnabled(true);
		    			buttonSyncSession.setEnabled(true);
	    			} else if(isCloudSync(node)){
	    				syncSessionView.setVisible(false);
	    				syncSessionView.viewSession(null);
	    				buttonOpenDataSource.setEnabled(true);
		    			buttonSyncSession.setEnabled(true);
	    			} else {
	    				syncSessionView.setVisible(true);
	    				syncSessionView.viewSession(null);
	    				buttonOpenDataSource.setEnabled(false);
		    			buttonSyncSession.setEnabled(false);
	    			}
	    		}
	        }
	    );

		final JScrollPane scrollPaneSessions = new JScrollPane(treeSessions);
		
		final JPanel panelViewSession = new JPanel();
		panelViewSession.setBorder(new EmptyBorder(0, 0, 0, 0));
		panelViewSession.setBackground(Color.WHITE);
		panelViewSession.setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("20dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("271dlu")},
			new RowSpec[] {
				RowSpec.decode("197dlu")}));
		
		syncSessionView = new SyncSessionView(false, this.propertiesProvider);
		panelViewSession.add(syncSessionView, new CellConstraints(3, 1, CellConstraints.FILL, CellConstraints.FILL ));

		final JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerLocation(210);
		splitPane.setLeftComponent(scrollPaneSessions);
		splitPane.setContinuousLayout(true);
		splitPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		splitPane.setOneTouchExpandable(true);
		splitPane.setRightComponent(panelViewSession);
		
		getContentPane().add(splitPane, new CellConstraints(2, 2, CellConstraints.FILL, CellConstraints.FILL));
	
		this.addWindowFocusListener(this);
	}
	
	private void createSyncSessionTreeModel() {

		HashMap<String, DefaultMutableTreeNode> sourceNodes = new HashMap<String, DefaultMutableTreeNode>();
		
		if(this.syncEngine != null){
			for (ISyncSession syncSession : this.syncEngine.getAllSyncSessions()) {
				DefaultMutableTreeNode sourceNode = sourceNodes.get(syncSession.getSourceId());
				if(sourceNode == null){
					sourceNode = new DefaultMutableTreeNode(syncSession.getSourceId());
					sourceNodes.put(syncSession.getSourceId(), sourceNode);
					rootNode.add(sourceNode);
				}
				sourceNode.add(new DefaultMutableTreeNode(new SyncSessionWrapper(syncSession)));

				Collection<MutableTreeNode> cloudNodes = createSyncCloudTreeNode(syncSession.getSourceId());
				if(!cloudNodes.isEmpty()){
					for (MutableTreeNode cloudNode : cloudNodes) {
						sourceNode.add(cloudNode);	
					}
				}
			}
		}
	}

	private Collection<MutableTreeNode> createSyncCloudTreeNode(String sourceAlias) {

		HashMap<String, MutableTreeNode> nodes = new HashMap<String, MutableTreeNode>();
		
		FeedAdapter adapter = SyncEngineUtil.getCloudSyncTraceAdapter(sourceAlias, this.propertiesProvider.getIdentityProvider(), this.propertiesProvider.getBaseDirectory());
		List<Item> items = adapter.getAll();

		if(!items.isEmpty()){		
			for (Item item : items) {				
				String url = ((XMLContent)item.getContent()).getLink();				
				DefaultMutableTreeNode syncNode = (DefaultMutableTreeNode) nodes.get(url);
				if(syncNode == null){
					syncNode = new DefaultMutableTreeNode(new CloudSyncSessionWrapper(sourceAlias, url, item));
					nodes.put(url, syncNode);
				} else {
					((CloudSyncSessionWrapper)syncNode.getUserObject()).updateStatus(item);
				}
			}
		}
		return nodes.values();
	}

	private class TreeSessionRenderer extends DefaultTreeCellRenderer {

		private static final long serialVersionUID = 7410616073430311206L;

		public TreeSessionRenderer() {
			super();
			this.setFont(new Font("Calibri", Font.PLAIN, 10));
		}

	    public Component getTreeCellRendererComponent(
	                        JTree tree,
	                        Object value,
	                        boolean sel,
	                        boolean expanded,
	                        boolean leaf,
	                        int row,
	                        boolean hasFocus) {

	        super.getTreeCellRendererComponent(
	                        tree, value, sel,
	                        expanded, leaf, row,
	                        hasFocus);

       	 	DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
       	 
	        if (!leaf && isRootNode(node)) {
	            setToolTipText(MeshCompactUITranslator.getSyncSessionWindowToolTipAllSessions());
	        } else if(!leaf && isDataSource(node)){
	            setIcon(IconManager.getDataSourceSamll());
	            setToolTipText(MeshCompactUITranslator.getSyncSessionWindowToolTipDataSource());
	        } else if(leaf && isSyncSession(node)){
		        SyncSessionWrapper syncSession = (SyncSessionWrapper)node.getUserObject();

		        if(syncSession.isBroken()){
		        	setIcon(IconManager.getStatusErrorIcon());
		        } else if(syncSession.isOpen()){
		        	setIcon(IconManager.getStatusProcessingIcon());	
		        } else {
		        	setIcon(IconManager.getStatusOkIcon());
		        }
	            setToolTipText(MeshCompactUITranslator.getSyncSessionWindowToolTipSyncSession());
	        } else if(leaf && isCloudSync(node)){
		        CloudSyncSessionWrapper syncSession = (CloudSyncSessionWrapper)node.getUserObject();

		        if(syncSession.isError()){
		        	setIcon(IconManager.getCloudErrorIcon());
		        } else if(syncSession.hasConflicts()){
		        	setIcon(IconManager.getCloudConflictsIcon());
		        } else {
		        	setIcon(IconManager.getCloudIcon());
		        }
	            setToolTipText(syncSession.toString());
	        } else {
	            setToolTipText(null); //no tool tip
	        } 
	        
	        return this;
	    }

	}
	
    protected boolean isRootNode(DefaultMutableTreeNode node) {
        return node == rootNode;
    }
    
    protected boolean isDataSource(DefaultMutableTreeNode node) {
        return node != rootNode && !isSyncSession(node) && !isCloudSync(node);
    }
    
    protected boolean isSyncSession(DefaultMutableTreeNode node) {
        return node.getUserObject() instanceof SyncSessionWrapper;
    }
    
    protected boolean isCloudSync(DefaultMutableTreeNode node) {
        return node.getUserObject() instanceof CloudSyncSessionWrapper;
    }
    
	private class SyncSessionWrapper{
		
		private ISyncSession syncSession;
		
		protected SyncSessionWrapper(ISyncSession syncSession){
			super();
			this.syncSession = syncSession;
		}
		
		public String getSourceId() {
			return this.getSyncSession().getSourceId();
		}

		public ISyncSession getSyncSession() {
			return this.syncSession;
		}

		public boolean isOpen() {
			return this.syncSession.isOpen();
		}

		@Override
		public String toString(){
			StringBuffer sb = new StringBuffer();

			EndpointMapping endpoint = EndpointProvider.getEndpointMapping(this.syncSession.getTarget().getEndpointId(), propertiesProvider);
			if(endpoint == null){
				sb.append(this.syncSession.getTarget().getEndpointId());
			} else {
				sb.append(endpoint.getAlias());
			}
			
			if(isCancelled()){
				sb.append("[");
				sb.append(MeshCompactUITranslator.getLabelCancelled());
				sb.append("]");
			}
	    				
			return sb.toString();
		}
		
		protected boolean isCancelled() {
			return this.syncSession.isCancelled();
		}
		public boolean isBroken() {
			return this.syncSession.isBroken();
		}
		protected boolean shouldSendChanges(){
			return this.syncSession.shouldSendChanges();
		}
		
		protected boolean shouldReceiveChanges(){
			return this.syncSession.shouldReceiveChanges();
		}
	}
	
	public class CloudSyncSessionWrapper{

		// MODEL VARIABLES
		private String sourceId;
		private String url;
		private Date startDate;
		private String start;
		private String end;
		private boolean error;
		private int conflicts;
		private String syncMode;
		
		// BUSINESS METHODS
		public CloudSyncSessionWrapper(String sourceId, String url, Item item) {
			super();
			this.url = url;
			this.sourceId = sourceId;
			this.updateStatus(item);
		}

		public String getSyncMode() {
			return this.syncMode;
		}

		public String getSourceId() {
			return sourceId;
		}
		
		public String getUrl() {
			return url;
		}
		
		public boolean isError() {
			return error;
		}

		public boolean hasConflicts() {
			return this.conflicts > 0;
		}
		
		public void updateStatus(Item item) {
			SyncEngineUtil.updateCloudSyncWrapper(this, item);
		}
		
		@Override
		public String toString(){
			return MeshCompactUITranslator.getSyncSessionWindowToolTipCloudSyncSession(this.url, this.start, this.end, this.conflicts);
		}

		public Date getStartDate() {
			return this.startDate;
		}
		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}

		public void setStart(String start) {
			this.start = start;
		}
		
		public void setEnd(String end) {
			this.end = end;
		}

		public void setConflicts(int conflicts) {
			this.conflicts = conflicts;
		}

		public void setError(boolean error) {
			this.error = error;
		}
		
		public void setSyncMode(String syncMode){
			this.syncMode = syncMode;
		}
	}
	
	// ISyncSessionViewOwner methods
	
	@Override
	public void notifyNewSync(boolean isSyncSessionInView) {
		updateSessions();
	}

	@Override
	public void notifyBeginSync() {
		// nothing to do
	}
	
	@Override
	public void notifyEndCancelSync() {
		this.treeSessions.repaint();
	}

	@Override
	public void notifyEndSync(boolean error) {
		this.treeSessions.repaint();
	}
	
	@Override
	public void notifyNewEndpointMapping(EndpointMapping endpointMapping) {
		// nothing to do		
	}
	
	@Override
	public void notifyNotAvailableDataSource(String dataSourceAlias, String dataSourceDescription, String endpointId) {
		// nothing to do
		
	}

	public void updateSessions(){
		this.buttonOpenDataSource.setEnabled(false);
		this.buttonSyncSession.setEnabled(false);
		
		this.rootNode = new DefaultMutableTreeNode(MeshCompactUITranslator.getSyncSessionWindowLabelAllSessions());
		this.createSyncSessionTreeModel();		
		this.treeSessions.setModel(new DefaultTreeModel(rootNode));
		this.treeSessions.repaint();
		
		this.syncSessionView.viewSession(null);
	}
	
	public void initialize(MessageSyncEngine syncEngine) {
		this.syncEngine = syncEngine;
		this.syncSessionView.initialize(this, this.sourceIdMapper, this.syncEngine.getChannel());
		updateSessions();
	}

	@Override
	public boolean isWorking() {
		return false;
	}

	
	// WindowFocusListener methods
	@Override
	public void windowGainedFocus(WindowEvent e) {
        this.owner.notifySyncSessionFrameGainedFocus(); 
	}

	@Override
	public void windowLostFocus(WindowEvent e) {
		// nothing to do
		
	}

	public void notifyOwnerWorking(){
		this.buttonSyncSession.setEnabled(false);
		this.buttonOpenDataSource.setEnabled(false);
	}
	
	public void notifyOwnerNotWorking(){
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeSessions.getLastSelectedPathComponent();
		
		if (node == null){
			buttonOpenDataSource.setEnabled(false);
			buttonSyncSession.setEnabled(false);
			return;
		}
		
		if(isDataSource(node)){
			buttonOpenDataSource.setEnabled(true);
			buttonSyncSession.setEnabled(false);
		} else if(isSyncSession(node)){
			buttonOpenDataSource.setEnabled(true);
			buttonSyncSession.setEnabled(true);
		} else if(isCloudSync(node)){
			buttonOpenDataSource.setEnabled(true);
			buttonSyncSession.setEnabled(true);
		} else {
			buttonOpenDataSource.setEnabled(false);
			buttonSyncSession.setEnabled(false);
		}
	}
	
	@Override
	public void notifyError(String error) {
		JOptionPane.showMessageDialog(this, error, MeshCompactUITranslator.getSyncWindowTitle(), JOptionPane.ERROR_MESSAGE);
	}
}
