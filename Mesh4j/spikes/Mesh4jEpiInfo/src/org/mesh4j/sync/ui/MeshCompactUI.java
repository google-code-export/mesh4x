package org.mesh4j.sync.ui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.mappings.DataSourceMapping;
import org.mesh4j.sync.mappings.EndpointMapping;
import org.mesh4j.sync.mappings.MSAccessDataSourceMapping;
import org.mesh4j.sync.mappings.SyncMode;
import org.mesh4j.sync.message.IMessageSyncAware;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.MessageSyncEngine;
import org.mesh4j.sync.message.channel.sms.connection.ISmsConnectionInboundOutboundNotification;
import org.mesh4j.sync.properties.PropertiesProvider;
import org.mesh4j.sync.ui.tasks.CancelAllSyncTask;
import org.mesh4j.sync.ui.tasks.CancelSyncTask;
import org.mesh4j.sync.ui.tasks.OpenFileTask;
import org.mesh4j.sync.ui.tasks.ReadyToSyncTask;
import org.mesh4j.sync.ui.tasks.ShutdownTask;
import org.mesh4j.sync.ui.tasks.StartUpTask;
import org.mesh4j.sync.ui.tasks.SynchronizeTask;
import org.mesh4j.sync.ui.tasks.TestPhoneTask;
import org.mesh4j.sync.ui.translator.MeshCompactUITranslator;
import org.mesh4j.sync.ui.translator.MeshUITranslator;
import org.mesh4j.sync.ui.utils.IconManager;
import org.mesh4j.sync.ui.utils.ProcessCustomMessages;
import org.mesh4j.sync.utils.SourceIdMapper;
import org.mesh4j.sync.utils.SyncEngineUtil;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class MeshCompactUI implements ISyncSessionViewOwner{

	private final static Log Logger = LogFactory.getLog(MeshCompactUI.class);
	
	// MODEL VARIABLES
	private JFrame frame;
	private JLabel labelSyncWith;
	private JButton buttonSync;
	private JButton buttonReadyToSync;
	private JButton buttonTestPhone;
	private JButton buttonOpenDataSource;
	private JComboBox comboBoxSyncMode;
	private JComboBox comboBoxMappingDataSource;
	private JComboBox comboBoxEndpoint;
	private JButton buttonOpenLog;
	private JButton buttonConfiguration;
	private JButton buttonOpenSessions;
	private JPanel panelSync;

	private SyncSessionView syncSessionView;
	private LogFrame logFrame;
	private ConfigurationFrame cfgFrame;
	private SyncSessionsFrame syncSessionsFrame;
	private SyncCloudFrame syncCloudFrame;
	private MapsFrame mapsFrame;
	
	private PropertiesProvider propertiesProvider;
	private SourceIdMapper sourceIdMapper;
	private MessageSyncEngine syncEngine;
	private ProcessCustomMessages processCustomMessages;
	
	// BUSINESS METHODS
	
	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MeshCompactUI window = new MeshCompactUI();
					window.frame.pack();
					window.frame.setSize(window.frame.getPreferredSize());
					window.frame.setVisible(true);
				} catch (Exception e) {
					Logger.error(e.getMessage(), e);
				}
			}
		});
	}

	public MeshCompactUI() throws Exception {
		this.propertiesProvider = new PropertiesProvider();
		this.sourceIdMapper = new SourceIdMapper(propertiesProvider.getBaseDirectory()+"/myDataSources.properties");
		
		this.mapsFrame = new MapsFrame(this.propertiesProvider, this.sourceIdMapper);
		this.logFrame = new LogFrame(this);
		this.cfgFrame = new ConfigurationFrame(this);
		this.syncSessionsFrame = new SyncSessionsFrame(this, this.sourceIdMapper, this.propertiesProvider);
		this.syncCloudFrame = new SyncCloudFrame(this.propertiesProvider, this.sourceIdMapper, this.syncSessionsFrame);
		
		this.createUI();
		
		IMessageSyncAware[] syncAware = new IMessageSyncAware[] {this.logFrame, this.syncSessionView};
		ISmsConnectionInboundOutboundNotification[] smsAware = new ISmsConnectionInboundOutboundNotification[]{this.logFrame, this.syncSessionView};
		this.syncEngine = SyncEngineUtil.createSyncEngine(this.sourceIdMapper, this.propertiesProvider, syncAware, smsAware);
		this.processCustomMessages = new ProcessCustomMessages(this);
		
		if(this.syncEngine != null){
			this.syncSessionView.initialize(this, this.sourceIdMapper, this.syncEngine.getChannel());
			this.syncSessionsFrame.initialize(this.syncEngine);
		}
	}

	public void startUpSyncEngine(){
		new StartUpTask(this).execute();
	}

	public void shutdownSyncEngine(){
		new ShutdownTask(this).execute();
	}
	
	// Status	
	public void viewSyncSession(ISyncSession syncSession) {
		if(syncSession != null){
			if(!this.syncSessionView.isSyncInProcess()){
				this.syncSessionView.viewSession(syncSession);
				
				this.selectEndpoint(syncSession.getTarget().getEndpointId());
				this.selectDataSource(syncSession.getSourceId());
				this.comboBoxSyncMode.setSelectedItem(SyncMode.getSyncMode(syncSession.shouldSendChanges(), syncSession.shouldReceiveChanges()));
				
				if(syncSession.isOpen() && !syncSession.isCancelled()){
					notifyBeginSync();
				}
			}
		}
	}

	private void selectDataSource(String sourceId) {
		int size = this.comboBoxMappingDataSource.getModel().getSize();
		int i = 0;
		while(i< size){
			DataSourceMapping dataSource = (DataSourceMapping)this.comboBoxMappingDataSource.getModel().getElementAt(i);
			if(dataSource.getAlias().equals(sourceId)){
				this.comboBoxMappingDataSource.setSelectedIndex(i);
				return;
			}
			i = i +1;
		}		
	}

	private void selectEndpoint(String endpointId) {
		int size = this.comboBoxEndpoint.getModel().getSize();
		int i = 0;
		while(i< size){
			EndpointMapping endpoint = (EndpointMapping)this.comboBoxEndpoint.getModel().getElementAt(i);
			if(endpoint.getEndpoint().equals(endpointId)){
				this.comboBoxEndpoint.setSelectedIndex(i);
				return;
			}
			i = i +1;
		}
	}
	
	public void notifyEndSync(boolean error) {
		this.buttonSync.setText(MeshCompactUITranslator.getLabelSync());
		this.enableAllButtons();
		this.notifyOwnerNotWorking();
	}
		
	public void beginSync() {
		this.syncSessionView.viewSession(null);
	}
		
	public void notifyErrorSync(Throwable t)	{
		String msg = MeshUITranslator.getLabelFailed();
		this.syncSessionView.setError(msg);
		this.logFrame.logError(t, msg);
		this.notifyOwnerNotWorking();
	}

	public void notifyBeginCancelSync(EndpointMapping endpointMapping, DataSourceMapping dataSourceMapping)	{
		this.disableAllButtons();
	}

	public void notifyEndCancelSync() {
		this.buttonSync.setText(MeshCompactUITranslator.getLabelSync());
		this.enableAllButtons();
		this.notifyOwnerNotWorking();
	}
	
	public void notifyStartUpOk(){		
		
		if(this.syncEngine != null){
			this.syncSessionView.initialize(this, this.sourceIdMapper, this.syncEngine.getChannel());
			this.syncSessionsFrame.initialize(this.syncEngine);
		}
		
		String msg = MeshCompactUITranslator.getMessageWelcome(this.propertiesProvider.getLoggedUserName());
		this.syncSessionView.setReady(msg);
		
		fullEnableAllButtons();
	}
	
	public void notifyStartUpError(){	
		String msg = MeshCompactUITranslator.getMessageStartUpError();
		this.syncSessionView.setError(msg);

		comboBoxEndpoint.setEnabled(false);
		comboBoxMappingDataSource.setEnabled(false);
		comboBoxSyncMode.setEnabled(false);
		
		buttonReadyToSync.setEnabled(false);
		buttonTestPhone.setEnabled(false);
		buttonSync.setEnabled(false); 
		
		String[] options = new String[] {MeshCompactUITranslator.getMessageConfigurePhone(), MeshCompactUITranslator.getMessagePhoneConnected(), MeshCompactUITranslator.getLabelCancel()};
		int n = JOptionPane.showOptionDialog(
			frame,
			MeshCompactUITranslator.getMessageForPopUpPhoneNotConnected(),
			MeshCompactUITranslator.getTitle(),
			JOptionPane.YES_NO_OPTION,
			JOptionPane.ERROR_MESSAGE,
			null,     //do not use a custom Icon
			options,  //the titles of buttons
			options[0]); //default button title
		if(n == 0){
			cfgFrame.selectPropertiesTab();
			cfgFrame.setVisible(true);
		}else if(n == 1) {			
			notifyStartUpOk();
		}
	}
	
	
	// enable/Disable
	public void enableAllButtons() {
		this.comboBoxEndpoint.setEnabled(true);
		this.comboBoxMappingDataSource.setEnabled(true);
		this.comboBoxSyncMode.setEnabled(true);
		this.buttonOpenDataSource.setEnabled(true);
		
		this.buttonReadyToSync.setEnabled(true);
		this.buttonTestPhone.setEnabled(true);		
	}
	
	public void disableAllButtons() {
		this.comboBoxEndpoint.setEnabled(false);
		this.comboBoxMappingDataSource.setEnabled(false);
		this.comboBoxSyncMode.setEnabled(false);
		this.buttonOpenDataSource.setEnabled(false);
		this.buttonReadyToSync.setEnabled(false);
		this.buttonTestPhone.setEnabled(false);
	}
	
	public void fullEnableAllButtons() {
		enableAllButtons();
		this.buttonSync.setEnabled(true);		
	}
	
	public void fullDisableAllButtons() {
		this.disableAllButtons();
		this.buttonSync.setEnabled(false);
	}
	

	// UI Design
	private void createUI() {
		frame = new JFrame();
		frame.setAlwaysOnTop(true);
		frame.getContentPane().setBackground(Color.WHITE);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		WindowAdapter windowAdapter = new WindowAdapter() {
			public void windowClosing(final WindowEvent e) {
				int numberOfOpenSyncSessions = getNumberOfOpenSyncSessions();
				if(numberOfOpenSyncSessions > 0){
					Object[] options = {MeshCompactUITranslator.getLabelCancelSyncAndCloseWindow(), MeshCompactUITranslator.getLabelCancelCloseWindow()};
					int n = JOptionPane.showOptionDialog(
						frame,
						MeshCompactUITranslator.getMessageForPopUpCloseWindows(numberOfOpenSyncSessions),
						MeshCompactUITranslator.getTitle(),
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,     //do not use a custom Icon
						options,  //the titles of buttons
						options[0]); //default button title
					if(n == 0){
						CancelAllSyncTask cancelSync = new CancelAllSyncTask(MeshCompactUI.this);
						cancelSync.execute();
						
						MeshCompactUI.this.close();
					} else {
						return;
					}
				} else {
					MeshCompactUI.this.close();
				}
			}

			public void windowOpened(final WindowEvent e) {
				startUpSyncEngine();
			}
			
			public void windowClosed(final WindowEvent e) {
				shutdownSyncEngine();
			}
		};
		
		frame.addWindowListener(windowAdapter);
		frame.setIconImage(IconManager.getCDCImage());
		frame.getContentPane().setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("20dlu"),
				ColumnSpec.decode("272dlu")},
			new RowSpec[] {
				RowSpec.decode("6dlu"),
				RowSpec.decode("120dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("129dlu"),
				RowSpec.decode("17dlu"),
				RowSpec.decode("26dlu")}));
		frame.setResizable(false);
		
		if(this.propertiesProvider.isEmulationModeActive()){
			frame.setTitle(MeshUITranslator.getTitleEmulationMode(this.propertiesProvider.getEmulationEndpointId()));
		} else {
			frame.setTitle(MeshUITranslator.getTitle());
		}
		frame.setBounds(100, 100, 590, 516);
		frame.getContentPane().add(getPanelSync(), new CellConstraints(2, 2));

		final JPanel panelStatusButtons = new JPanel();
		panelStatusButtons.setBackground(Color.WHITE);
		panelStatusButtons.setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("44dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("43dlu")},
			new RowSpec[] {
				RowSpec.decode("9dlu")}));
		frame.getContentPane().add(panelStatusButtons, new CellConstraints(2, 5, CellConstraints.FILL, CellConstraints.FILL));

		panelStatusButtons.add(getButtonOpenLog(), new CellConstraints(1, 1, CellConstraints.LEFT, CellConstraints.FILL));
		panelStatusButtons.add(getButtonConfiguration(), new CellConstraints(3, 1, CellConstraints.LEFT, CellConstraints.FILL));

		buttonOpenSessions = new JButton();
		buttonOpenSessions.setContentAreaFilled(false);
		buttonOpenSessions.setBorderPainted(false);
		buttonOpenSessions.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonOpenSessions.setFont(new Font("Calibri", Font.PLAIN, 10));
		buttonOpenSessions.setText(MeshCompactUITranslator.getLabelOpenSyncSessionsWindow());
		buttonOpenSessions.setToolTipText(MeshCompactUITranslator.getToolTipOpenSyncSessionsWindow());
		
		ActionListener openSessionsViewActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(syncSessionsFrame.isVisible()){
					syncSessionsFrame.toFront();
				} else {
					syncSessionsFrame.pack();
					syncSessionsFrame.setVisible(true);
				}
			}
		};	
		buttonOpenSessions.addActionListener(openSessionsViewActionListener);
		panelStatusButtons.add(buttonOpenSessions, new CellConstraints(5, 1, CellConstraints.LEFT, CellConstraints.FILL));
		
		JButton buttonOpenSyncWindow = new JButton();
		buttonOpenSyncWindow.setContentAreaFilled(false);
		buttonOpenSyncWindow.setBorderPainted(false);
		buttonOpenSyncWindow.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonOpenSyncWindow.setFont(new Font("Calibri", Font.PLAIN, 10));
		buttonOpenSyncWindow.setText(MeshCompactUITranslator.getLabelOpenSyncWindow());
		buttonOpenSyncWindow.setToolTipText(MeshCompactUITranslator.getToolTipOpenSyncWindow());
		
		ActionListener openSyncWindowActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(syncCloudFrame.isVisible()){
					syncCloudFrame.toFront();
				} else {
					syncCloudFrame.pack();
					syncCloudFrame.setVisible(true);
				}
			}
		};	
		buttonOpenSyncWindow.addActionListener(openSyncWindowActionListener);
		
		buttonOpenSyncWindow.setEnabled(this.propertiesProvider.isSyncCloudEnabled());
		
		panelStatusButtons.add(buttonOpenSyncWindow, new CellConstraints(7, 1, CellConstraints.LEFT, CellConstraints.FILL));

		final JButton buttonOpenMaps = new JButton();
		buttonOpenMaps.setContentAreaFilled(false);
		buttonOpenMaps.setBorderPainted(false);
		buttonOpenMaps.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonOpenMaps.setFont(new Font("Calibri", Font.PLAIN, 10));
		buttonOpenMaps.setText(MeshCompactUITranslator.getLabelOpenMapsWindow());
		buttonOpenMaps.setToolTipText(MeshCompactUITranslator.getToolTipOpenMapsWindow());
		
		ActionListener openMapsActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(mapsFrame.isVisible()){
					mapsFrame.toFront();
				} else {
					mapsFrame.pack();
					mapsFrame.setVisible(true);
				}
			}
		};	
		buttonOpenMaps.addActionListener(openMapsActionListener);
		panelStatusButtons.add(buttonOpenMaps, new CellConstraints(9, 1, CellConstraints.LEFT, CellConstraints.FILL));

		final JPanel panelTrademark = new JPanel();
		panelTrademark.setBackground(Color.WHITE);
		panelTrademark.setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("285dlu"),
				ColumnSpec.decode("5dlu")},
			new RowSpec[] {
				RowSpec.decode("16dlu"),
				FormFactory.DEFAULT_ROWSPEC}));
		frame.getContentPane().add(panelTrademark, new CellConstraints(1, 6, 2, 1));

		ActionListener openMesh4xActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				openMesh4xURL();
			}
		};
		
		final JButton buttonLabelTrademark = new JButton();
		buttonLabelTrademark.setContentAreaFilled(false);
		buttonLabelTrademark.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonLabelTrademark.setBorderPainted(false);
		buttonLabelTrademark.setBackground(Color.WHITE);
		buttonLabelTrademark.setFont(new Font("Calibri", Font.BOLD, 10));
		buttonLabelTrademark.setText(MeshCompactUITranslator.getTradeMark());
		buttonLabelTrademark.setToolTipText(MeshCompactUITranslator.getToolTipTradeMark(this.propertiesProvider.getMesh4xURL()));
		buttonLabelTrademark.addActionListener(openMesh4xActionListener);
		
		panelTrademark.add(buttonLabelTrademark, new CellConstraints(1, 1, 1, 2, CellConstraints.RIGHT, CellConstraints.BOTTOM));
		
		
		final JButton buttonImageTrademark = new JButton();
		buttonImageTrademark.setContentAreaFilled(false);
		buttonImageTrademark.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonImageTrademark.setBorderPainted(false);
		buttonImageTrademark.setBackground(Color.WHITE);
		buttonImageTrademark.setIcon(IconManager.getTrademarkIcon());
		buttonImageTrademark.setText("");
		buttonImageTrademark.setToolTipText(MeshCompactUITranslator.getToolTipTradeMark(this.propertiesProvider.getMesh4xURL()));
		buttonImageTrademark.addActionListener(openMesh4xActionListener);
		panelTrademark.add(buttonImageTrademark, new CellConstraints(2, 1, 1, 2, CellConstraints.DEFAULT, CellConstraints.BOTTOM));

		frame.getContentPane().add(getSyncSessionView(), new CellConstraints(2, 4, CellConstraints.FILL, CellConstraints.FILL));
	}

	protected void close() {
		this.logFrame.setVisible(false);
		this.logFrame.dispose();
		
		this.cfgFrame.setVisible(false);
		this.cfgFrame.dispose();
		
		this.syncSessionsFrame.setVisible(false);
		this.syncSessionsFrame.dispose();
		
		this.syncCloudFrame.setVisible(false);
		this.syncCloudFrame.dispose();
		
		this.mapsFrame.setVisible(false);
		this.mapsFrame.dispose();
		
		this.frame.setVisible(false);
		this.frame.dispose();
	}

	protected JPanel getPanelSync() {
		if (panelSync == null) {
			panelSync = new JPanel();
			panelSync.setBackground(Color.WHITE);
			panelSync.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.DEFAULT_COLSPEC,
					ColumnSpec.decode("10dlu"),
					ColumnSpec.decode("97dlu"),
					ColumnSpec.decode("27dlu"),
					ColumnSpec.decode("70dlu")},
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC,
					RowSpec.decode("15dlu"),
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					RowSpec.decode("11dlu"),
					FormFactory.DEFAULT_ROWSPEC}));
			panelSync.add(getLabelSyncWith(), new CellConstraints());
			panelSync.add(getComboBoxEndpoint(), new CellConstraints(3, 1));
			panelSync.add(getButtonReadyToSync(), new CellConstraints(5, 3, CellConstraints.DEFAULT, CellConstraints.CENTER));
			panelSync.add(getButtonSync(), new CellConstraints(5, 5));

			final JPanel panel = new JPanel();
			panel.setBackground(Color.WHITE);
			panel.setLayout(new FormLayout(
				new ColumnSpec[] {
					ColumnSpec.decode("75dlu"),
					ColumnSpec.decode("2dlu"),
					ColumnSpec.decode("17dlu"),
					ColumnSpec.decode("6dlu"),
					ColumnSpec.decode("98dlu")},
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC}));
			panelSync.add(panel, new CellConstraints(1, 7, 5, 1));
			panel.add(getComboBoxMappingDataSource(), new CellConstraints());
			panel.add(getComboBoxSyncMode(), new CellConstraints(5, 1));

			
			ActionListener openDataSourceActionListener = new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					MSAccessDataSourceMapping dataSource = (MSAccessDataSourceMapping) getComboBoxMappingDataSource().getSelectedItem();
					if(dataSource != null){
						OpenFileTask task = new OpenFileTask(dataSource.getFileName());
						task.execute();
					}
				}
			};	
			
			buttonOpenDataSource = new JButton();
			buttonOpenDataSource.setContentAreaFilled(false);
			buttonOpenDataSource.setBorderPainted(false);
			buttonOpenDataSource.setBorder(new EmptyBorder(0, 0, 0, 0));
			buttonOpenDataSource.setBackground(Color.WHITE);
			buttonOpenDataSource.setText("");
			buttonOpenDataSource.setToolTipText(MeshCompactUITranslator.getTooltipViewDataSource());
			buttonOpenDataSource.setIcon(IconManager.getViewDataSource());
			buttonOpenDataSource.addActionListener(openDataSourceActionListener);
			panel.add(buttonOpenDataSource, new CellConstraints(3, 1, CellConstraints.FILL, CellConstraints.FILL));

			final JPanel panelSyncButtons = new JPanel();
			panelSyncButtons.setLayout(new FormLayout(
				"70dlu",
				"14dlu"));
			panelSync.add(panelSyncButtons, new CellConstraints(5, 1));
			panelSyncButtons.add(getButtonTestPhone(), new CellConstraints());
		}
		return panelSync;
	}

	protected JLabel getLabelSyncWith() {
		if (labelSyncWith == null) {
			labelSyncWith = new JLabel();
			labelSyncWith.setFont(new Font("Calibri", Font.BOLD, 14));
			labelSyncWith.setText(MeshCompactUITranslator.getLabelSyncWith());
		}
		return labelSyncWith;
	}

	public JComboBox getComboBoxEndpoint() {
		if (comboBoxEndpoint == null) {
			comboBoxEndpoint = new JComboBox();
			comboBoxEndpoint.setFont(new Font("Calibri", Font.PLAIN, 12));
			comboBoxEndpoint.setModel(new DefaultComboBoxModel(SyncEngineUtil.getEndpointMappings(propertiesProvider)));
			comboBoxEndpoint.setToolTipText(MeshCompactUITranslator.getToolTipEndpoints());
		}
		return comboBoxEndpoint;
	}

	protected JButton getButtonTestPhone() {
		if (buttonTestPhone == null) {
			buttonTestPhone = new JButton();
			buttonTestPhone.setFont(new Font("Calibri", Font.BOLD, 12));
			buttonTestPhone.setBackground(UIManager.getColor("Button.background"));
			buttonTestPhone.setText(MeshCompactUITranslator.getLabelTestPhone());
			buttonTestPhone.setToolTipText(MeshCompactUITranslator.getToolTipTestPhone());
			
			ActionListener testPhoneActionListener = new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					TestPhoneTask task = new TestPhoneTask(MeshCompactUI.this);
					task.execute();
				}
			};	
			buttonTestPhone.addActionListener(testPhoneActionListener);
		} 
		return buttonTestPhone;
	}

	protected JButton getButtonReadyToSync() {
		if (buttonReadyToSync == null) {
			buttonReadyToSync = new JButton();
			buttonReadyToSync.setFont(new Font("Calibri", Font.BOLD, 12));
			buttonReadyToSync.setText(MeshCompactUITranslator.getLabelReadyToSync());
			buttonReadyToSync.setToolTipText(MeshCompactUITranslator.getToolTipReadyToSync());
			
			ActionListener readyToSyncActionListener = new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					SwingWorker<Void, Void> task = new ReadyToSyncTask(MeshCompactUI.this);
					task.execute();
				}
			};	
			buttonReadyToSync.addActionListener(readyToSyncActionListener);
		}
		return buttonReadyToSync;
	}

	protected JButton getButtonSync() {
		if (buttonSync == null) {
			buttonSync = new JButton();
			buttonSync.setFont(new Font("Arial", Font.PLAIN, 16));
			buttonSync.setText(MeshCompactUITranslator.getLabelSync());
			buttonSync.setToolTipText(MeshCompactUITranslator.getToolTipSync());
			//buttonSync.setBorder(new EmptyBorder(0, 0, 0, 0));
			//buttonSync.setBorderPainted(false);
			//buttonSync.setContentAreaFilled(false);
			//buttonSync.setIcon(IconManager.getStatusProcessingIcon());
			
			ActionListener synchronizeActionListener = new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					SwingWorker<Void, Void> task = syncSessionView.isSyncInProcess() ? new CancelSyncTask(MeshCompactUI.this) : new SynchronizeTask(MeshCompactUI.this);
					task.execute();
				}
			};	
			buttonSync.addActionListener(synchronizeActionListener);
		}
		return buttonSync;
	}


	public JComboBox getComboBoxMappingDataSource() {
		if (comboBoxMappingDataSource == null) {
			comboBoxMappingDataSource = new JComboBox();
			comboBoxMappingDataSource.setFont(new Font("Calibri", Font.PLAIN, 12));
			comboBoxMappingDataSource.setToolTipText(MeshCompactUITranslator.getToolTipDataSources());
			
			notifyDataSourceMappingListsChanges();
		}
		return comboBoxMappingDataSource;
	}

	public JComboBox getComboBoxSyncMode() {
		if (comboBoxSyncMode == null) {
			comboBoxSyncMode = new JComboBox();
			comboBoxSyncMode.setFont(new Font("Calibri", Font.PLAIN, 12));
			comboBoxSyncMode.setToolTipText(MeshCompactUITranslator.getToolTipSyncMode());

			DefaultComboBoxModel syncTypesTableModel = new DefaultComboBoxModel(new SyncMode[]{SyncMode.SendAndReceiveChanges, SyncMode.SendChangesOnly, SyncMode.ReceiveChangesOnly});
			comboBoxSyncMode.setModel(syncTypesTableModel);
		}
		return comboBoxSyncMode;
	}



	protected JButton getButtonOpenLog() {
		if (buttonOpenLog == null) {
			buttonOpenLog = new JButton();
			Font font = new Font("Calibri", Font.PLAIN, 10);
			buttonOpenLog.setFont(font);
			buttonOpenLog.setOpaque(false);
			buttonOpenLog.setContentAreaFilled(false);
			buttonOpenLog.setBorderPainted(false);
			buttonOpenLog.setBorder(new EmptyBorder(0, 0, 0, 0));
			buttonOpenLog.setText(MeshCompactUITranslator.getLabelOpenLogWindow());
			buttonOpenLog.setToolTipText(MeshCompactUITranslator.getToolTipOpenLogWindow());
			
			buttonOpenLog.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(logFrame.isVisible()){
						logFrame.toFront();
					} else {
						logFrame.pack();
						logFrame.setVisible(true);
					}
				}
			});
		}
		return buttonOpenLog;
	}
	
	protected JButton getButtonConfiguration() {
		if (buttonConfiguration == null) {
			buttonConfiguration = new JButton();
			buttonConfiguration.setContentAreaFilled(false);
			buttonConfiguration.setFont(new Font("Calibri", Font.PLAIN, 10));
			buttonConfiguration.setOpaque(false);
			buttonConfiguration.setBorderPainted(false);
			buttonConfiguration.setBorder(new EmptyBorder(0, 0, 0, 0));
			buttonConfiguration.setText(MeshCompactUITranslator.getLabelOpenConfigurationWindow());
			buttonConfiguration.setToolTipText(MeshCompactUITranslator.getToolTipConfigurationWindow());
			
			buttonConfiguration.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(cfgFrame.isVisible()){
						cfgFrame.toFront();
					} else {
						cfgFrame.pack();
						cfgFrame.setVisible(true);
					}
				}
			});
		}
		return buttonConfiguration;
	}

	public SyncSessionView getSyncSessionView() {
		if(syncSessionView == null){
			syncSessionView = new SyncSessionView(true, this.propertiesProvider);
		}
		return syncSessionView;
	}
	
	public JFrame getFrame() {
		return this.frame;
	}

	public MessageSyncEngine getSyncEngine() {
		return this.syncEngine;
	}
	
	public DataSourceMapping getSelectedDataSource(){
		return (DataSourceMapping)this.getComboBoxMappingDataSource().getSelectedItem();
	}
	
	public EndpointMapping getSelectedEndpoint(){
		return (EndpointMapping)this.getComboBoxEndpoint().getSelectedItem();
	}

	public SourceIdMapper getSourceIdMapper() {
		return this.sourceIdMapper;
	}

	public void notifyEndpointMappingListsChanges() {
		comboBoxEndpoint.setModel(new DefaultComboBoxModel(SyncEngineUtil.getEndpointMappings(propertiesProvider)));		
	}

	public void notifyDataSourceMappingDeleted(String alias) {
		
		if(this.syncSessionView.getSyncSession() != null &&
				alias.equals(this.syncSessionView.getSyncSession().getSourceId())){
			this.syncSessionView.viewSession(null);
		}
		this.syncSessionsFrame.updateSessions();
		notifyDataSourceMappingListsChanges();
	}

	
	public void notifyDataSourceMappingListsChanges() {
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		
		ArrayList<DataSourceMapping> sources = sourceIdMapper.getDataSourceMappings();
		Iterator<DataSourceMapping> sourcesIt = sources.iterator();
		
		while(sourcesIt.hasNext()) {
			model.addElement(sourcesIt.next());			
		}
		comboBoxMappingDataSource.setModel(model);
		
		this.syncCloudFrame.notifyDataSourceMappingListsChanges(sources);
		this.mapsFrame.notifyDataSourceMappingListsChanges(sources);
	}

	public PropertiesProvider getPropertiesProvider() {
		return this.propertiesProvider;
	}

	
	private int getNumberOfOpenSyncSessions() {
		int result = 0;
		if(this.syncEngine != null){
			List<ISyncSession> syncSessions = this.syncEngine.getAllSyncSessions();
			for (ISyncSession syncSession : syncSessions) {
				if(syncSession.isOpen()){
					result = result + 1;
				}
			}
		}
		return result;
	}
	
	// ISyncSessionViewOwner methods
	@Override
	public boolean isWorking() {
		return this.syncSessionView.isSyncInProcess() 
				|| this.processCustomMessages.isPhoneCompatibilityInProcess() 
				|| this.processCustomMessages.isReadyToSyncInProcess();
	}

	
	@Override
	public void notifyNewSync(boolean isSyncSessioninView) {
		this.syncSessionsFrame.notifyNewSync(isSyncSessioninView);
		if(!isSyncSessioninView){
			this.buttonOpenSessions.setForeground(Color.RED);
			this.buttonOpenSessions.setToolTipText(MeshCompactUITranslator.getToolTipOpenSyncSessionsWindowNewSyncSessionss());
		}
	}

	@Override
	public void notifyBeginSync() {
		this.disableAllButtons();		
		this.buttonSync.setText(MeshCompactUITranslator.getLabelCancelSync());
		this.notifyOwnerWorking();
	}

	public void notifyOwnerWorking() {
		this.cfgFrame.notifyOwnerWorking();
		this.syncCloudFrame.notifyOwnerWorking();
		this.mapsFrame.notifyOwnerWorking();
		this.syncSessionsFrame.notifyOwnerWorking();
	}

	public void notifyOwnerNotWorking() {
		this.cfgFrame.notifyOwnerNotWorking();
		this.syncCloudFrame.notifyOwnerNotWorking();
		this.mapsFrame.notifyOwnerNotWorking();
		this.syncSessionsFrame.notifyOwnerNotWorking();
	}

	public ProcessCustomMessages getProcessCustomMessages() {
		return this.processCustomMessages;
	}

	public void notifySyncSessionFrameGainedFocus() {
		this.buttonOpenSessions.setForeground(null);
		this.buttonOpenSessions.setToolTipText(MeshCompactUITranslator.getToolTipOpenSyncSessionsWindow());
	}	
	
	public void notifyNewEndpointMapping(EndpointMapping endpointMapping){
		this.cfgFrame.addNewEndpoint(endpointMapping);
		this.notifyEndpointMappingListsChanges();

		String logText = MeshCompactUITranslator.getMessageEndpointMappingAutomaticallyCreated(endpointMapping.getAlias());
		addMessage(logText);
	}

	@Override
	public void notifyNotAvailableDataSource(String dataSourceAlias, String dataSourceDescription, String endpointId) {
		EndpointMapping endpointMapping = SyncEngineUtil.getEndpointMapping(endpointId, this.propertiesProvider);
		String logText = MeshCompactUITranslator.getMessageNotAvailableDataSource(
			dataSourceAlias, 
			dataSourceDescription, 
			endpointMapping == null ? endpointId : endpointMapping.getAlias());
		addError(logText);
	}
	
	public void notifyReadyToSyncAnswerSent(String dataSourceAlias, String endpointId) {
		EndpointMapping endpointMapping = SyncEngineUtil.getEndpointMapping(endpointId, this.propertiesProvider);
		String logText = MeshCompactUITranslator.getMessageReadyToSyncAnswerSent(
			dataSourceAlias, 
			endpointMapping == null ? endpointId : endpointMapping.getAlias());
		addMessage(logText);
	}
	
	private void addMessage(String logText) {

		this.logFrame.log(logText);
		if(isWorking()){
			this.newMessagesAreAvailables();
		} else {
			this.syncSessionView.setOk(logText);
		}
	}

	private void addError(String logText) {

		this.logFrame.log(logText);
		if(isWorking()){
			this.newMessagesAreAvailables();
		} else {
			this.syncSessionView.setError(logText);
		}
	}

	
	private void newMessagesAreAvailables(){
		this.buttonOpenLog.setForeground(Color.RED);
		this.buttonOpenLog.setToolTipText(MeshCompactUITranslator.getToolTipOpenLogWindowNewMessagesAvailables());
	}
	
	public void notifyLogFrameGainedFocus() {
		this.buttonOpenLog.setForeground(null);
		this.buttonOpenLog.setToolTipText(MeshCompactUITranslator.getToolTipOpenLogWindow());
	}
	
	public void openMesh4xURL(){
        if (Desktop.isDesktopSupported()) {
        	Desktop desktop = Desktop.getDesktop();
        	if (desktop.isSupported(Desktop.Action.BROWSE)) {
        		try{
        			desktop.browse(new URL(this.propertiesProvider.getMesh4xURL()).toURI());
        		} catch(Exception e){
        			LogFrame.Logger.error(e.getMessage(), e);
        		}
        	}
        }
	}

	public SyncCloudFrame getSyncCloudFrame(){
		return this.syncCloudFrame;
	}

	public IMessageSyncProtocol getSyncProtocol() {
		return this.syncEngine.getSyncProtocol();
	}
}