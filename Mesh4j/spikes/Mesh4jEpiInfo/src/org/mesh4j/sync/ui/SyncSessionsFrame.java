package org.mesh4j.sync.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
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
import javax.swing.tree.TreeSelectionModel;

import org.mesh4j.sync.mappings.EndpointMapping;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.MessageSyncEngine;
import org.mesh4j.sync.properties.PropertiesProvider;
import org.mesh4j.sync.ui.translator.MeshCompactUITranslator;
import org.mesh4j.sync.ui.utils.IconManager;
import org.mesh4j.sync.utils.SourceIdMapper;
import org.mesh4j.sync.utils.SyncEngineUtil;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class SyncSessionsFrame extends JFrame implements ISyncSessionViewOwner, WindowFocusListener{
	
	private static final long serialVersionUID = 142343742087435808L;

	// MODEL VARIABLES
	private DefaultMutableTreeNode rootNode;
	private JTree treeSessions;
	private MessageSyncEngine syncEngine;
	private SourceIdMapper sourceIdMapper;
	private PropertiesProvider propertiesProvider;
	private SyncSessionView syncSessionView;
	private MeshCompactUI owner;

	// BUSINESS METHODS

	public SyncSessionsFrame(MeshCompactUI ui, SourceIdMapper sourceIdMapper, PropertiesProvider propertiesProvider) {
		super();
		
		this.owner = ui;
		this.propertiesProvider = propertiesProvider;
		this.sourceIdMapper = sourceIdMapper;
		
		setAlwaysOnTop(true);
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
				ColumnSpec.decode("20dlu")},
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
		
		panelButtons.add(buttonClose, new CellConstraints());

		ActionListener syncActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				owner.viewSyncSession(syncSessionView.getSyncSession());
				
				JFrame ownerFrame = owner.getFrame();
				if(ownerFrame.isVisible()){
					ownerFrame.toFront();
				} else {
					ownerFrame.pack();
					ownerFrame.setVisible(true);
				}
			}
		};	
		
		final JButton buttonSyncSession = new JButton();
		buttonSyncSession.setOpaque(true);
		buttonSyncSession.setContentAreaFilled(false);
		buttonSyncSession.setBorderPainted(false);
		buttonSyncSession.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonSyncSession.setFont(new Font("Calibri", Font.BOLD, 12));
		buttonSyncSession.setText(MeshCompactUITranslator.getSyncSessionWindowLabelChooseSync());
		buttonSyncSession.setToolTipText(MeshCompactUITranslator.getSyncSessionWindowToolTipChooseSync());
		buttonSyncSession.addActionListener(syncActionListener);
		panelButtons.add(buttonSyncSession, new CellConstraints(3, 1));
	
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
	    			DefaultMutableTreeNode node = (DefaultMutableTreeNode)
	                treeSessions.getLastSelectedPathComponent();
	    			
	    			if (node == null){
	    				return;
	    			}
	    			
	    			if(isSyncSession(node)){
	    				 SyncSessionWrapper syncSessionWrapper = (SyncSessionWrapper)(node.getUserObject());
	    				 syncSessionView.viewSession(syncSessionWrapper.getSyncSession());	
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
			}
		}
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
		        SyncSessionWrapper syncSession = (SyncSessionWrapper)(node.getUserObject());

		        if(syncSession.isBroken()){
		        	setIcon(IconManager.getStatusErrorIcon());
		        } else if(syncSession.isOpen()){
		        	setIcon(IconManager.getStatusProcessingIcon());	
		        } else {
		        	setIcon(IconManager.getStatusOkIcon());
		        }
	            setToolTipText(MeshCompactUITranslator.getSyncSessionWindowToolTipSyncSession());
	        } else{
	            setToolTipText(null); //no tool tip
	        } 
	        
	        return this;
	    }

	}
	
    protected boolean isRootNode(DefaultMutableTreeNode node) {
        return node == rootNode;
    }
    
    protected boolean isDataSource(DefaultMutableTreeNode node) {
        return node != rootNode;
    }
    
    protected boolean isSyncSession(DefaultMutableTreeNode node) {
        Object nodeInfo =(Object)(node.getUserObject());
        return nodeInfo instanceof SyncSessionWrapper;
    }
    
	private class SyncSessionWrapper{
		
		private ISyncSession syncSession;
		
		protected SyncSessionWrapper(ISyncSession syncSession){
			super();
			this.syncSession = syncSession;
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

			EndpointMapping endpoint = SyncEngineUtil.getEndpointMapping(this.syncSession.getTarget().getEndpointId(), propertiesProvider);
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

}
