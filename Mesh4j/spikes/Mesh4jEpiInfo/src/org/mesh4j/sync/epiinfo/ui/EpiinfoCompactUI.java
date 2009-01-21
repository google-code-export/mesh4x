package org.mesh4j.sync.epiinfo.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.epiinfo.ui.utils.EpiInfoIconManager;
import org.mesh4j.sync.mappings.DataSourceMapping;
import org.mesh4j.sync.mappings.EndpointMapping;
import org.mesh4j.sync.mappings.SyncMode;
import org.mesh4j.sync.message.IMessageSyncAware;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.MessageSyncEngine;
import org.mesh4j.sync.message.channel.sms.connection.ISmsConnectionInboundOutboundNotification;
import org.mesh4j.sync.properties.PropertiesProvider;
import org.mesh4j.sync.ui.tasks.CancelSyncTask;
import org.mesh4j.sync.ui.tasks.EmulateIncomingCancelSyncTask;
import org.mesh4j.sync.ui.tasks.EmulateIncomingSyncTask;
import org.mesh4j.sync.ui.tasks.EmulateReadyToSyncTask;
import org.mesh4j.sync.ui.tasks.ReadyToSyncResponseTask;
import org.mesh4j.sync.ui.tasks.ReadyToSyncTask;
import org.mesh4j.sync.ui.tasks.ShutdownTask;
import org.mesh4j.sync.ui.tasks.StartUpTask;
import org.mesh4j.sync.ui.tasks.SynchronizeTask;
import org.mesh4j.sync.ui.tasks.TestPhoneTask;
import org.mesh4j.sync.ui.translator.EpiInfoCompactUITranslator;
import org.mesh4j.sync.ui.translator.EpiInfoUITranslator;
import org.mesh4j.sync.utils.EpiinfoSourceIdResolver;
import org.mesh4j.sync.utils.SyncEngineUtil;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class EpiinfoCompactUI implements ISmsConnectionInboundOutboundNotification, ISyncSessionViewOwner{

	private final static Log Logger = LogFactory.getLog(EpiinfoCompactUI.class);
	
	// MODEL VARIABLES
	private JFrame frame;
	private JLabel labelSyncWith;
	private JButton buttonSync;
	private JButton buttonReadyToSync;
	private JButton buttonTestPhone;
	private JComboBox comboBoxSyncMode;
	private JComboBox comboBoxMappingDataSource;
	private JComboBox comboBoxEndpoint;
	private JButton buttonOpenLog;
	private JButton buttonConfiguration;
	private JPanel panelSync;

	private SyncSessionView syncSessionView;
	private LogFrame logFrame;
	private ConfigurationFrame cfgFrame;
	private SyncSessionsFrame syncSessionsFrame;
	
	private PropertiesProvider propertiesProvider;
	private EpiinfoSourceIdResolver sourceIdResolver;
	private MessageSyncEngine syncEngine;
	
	private  IFilter<String> messageFilter;
	private boolean readyToSyncInProcess = false;
	private EndpointMapping readyToSyncEndpoint;
	private DataSourceMapping readyToSyncDataSource;
	
	private boolean phoneCompatibilityInProcess = false;
	private EndpointMapping phoneCompatibilityEndpoint;
	private String phoneCompatibilityId;
	
	// BUSINESS METHODS
	
	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EpiinfoCompactUI window = new EpiinfoCompactUI();
					window.frame.pack();
					window.frame.setSize(window.frame.getPreferredSize());
					window.frame.setVisible(true);
				} catch (Exception e) {
					Logger.error(e.getMessage(), e);
				}
			}
		});
	}

	public EpiinfoCompactUI() throws Exception {
		this.propertiesProvider = new PropertiesProvider();
		this.sourceIdResolver = new EpiinfoSourceIdResolver(propertiesProvider.getBaseDirectory()+"/myDataSources.properties");
				
		this.messageFilter = new IFilter<String>(){		
			@Override public boolean applies(String message) {
				return ReadyToSyncTask.isQuestion(message) || TestPhoneTask.isQuestion(message);
			}			
		};

		this.createUI();
		
		this.logFrame = new LogFrame(this.sourceIdResolver);
		this.cfgFrame = new ConfigurationFrame(this);
		this.syncSessionsFrame = new SyncSessionsFrame(this, this.sourceIdResolver, this.propertiesProvider);
		
		IMessageSyncAware[] syncAware = new IMessageSyncAware[] {this.logFrame, this.syncSessionView};
		ISmsConnectionInboundOutboundNotification[] smsAware = new ISmsConnectionInboundOutboundNotification[]{this, this.logFrame, this.syncSessionView};
		this.syncEngine = SyncEngineUtil.createSyncEngine(this.sourceIdResolver, this.propertiesProvider, syncAware, smsAware);
		if(this.syncEngine != null){
			this.syncSessionView.initialize(this, this.sourceIdResolver, this.syncEngine.getChannel());
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
			if(dataSource.getSourceId().equals(sourceId)){
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
		this.buttonSync.setText(EpiInfoCompactUITranslator.getLabelSync());
		this.enableAllButtons();
		this.cfgFrame.notifyOwnerNotWorking();
	}
		
	public void beginSync() {
		this.syncSessionView.viewSession(null);
	}
		
	public void notifyErrorSync(Throwable t)	{
		String msg = EpiInfoUITranslator.getLabelFailed();
		this.syncSessionView.setError(msg);
		this.logFrame.logError(t, msg);
		this.cfgFrame.notifyOwnerNotWorking();
	}

	public void notifyBeginCancelSync(EndpointMapping endpointMapping, DataSourceMapping dataSourceMapping)	{
		this.disableAllButtons();
	}

	public void notifyEndCancelSync() {
		this.buttonSync.setText(EpiInfoCompactUITranslator.getLabelSync());
		this.enableAllButtons();
		this.cfgFrame.notifyOwnerNotWorking();
	}
	
	public void notifyStartUpOk(){		
		
		if(this.syncEngine != null){
			this.syncSessionView.initialize(this, this.sourceIdResolver, this.syncEngine.getChannel());
			this.syncSessionsFrame.initialize(this.syncEngine);
		}
		
		String msg = EpiInfoCompactUITranslator.getMessageWelcome();
		this.syncSessionView.setReady(msg);
		
		fullEnableAllButtons();
	}
	
	public void notifyStartUpError(){	
		String msg = EpiInfoCompactUITranslator.getMessageStartUpError();
		this.syncSessionView.setError(msg);

		comboBoxEndpoint.setEnabled(false);
		comboBoxMappingDataSource.setEnabled(false);
		comboBoxSyncMode.setEnabled(false);
		
		buttonReadyToSync.setEnabled(false);
		buttonTestPhone.setEnabled(false);
		buttonSync.setEnabled(false); 
		
		String[] options = new String[] {EpiInfoCompactUITranslator.getMessageConfigurePhone(), EpiInfoCompactUITranslator.getMessagePhoneConnected(), EpiInfoCompactUITranslator.getLabelCancel()};
		int n = JOptionPane.showOptionDialog(
			frame,
			EpiInfoCompactUITranslator.getMessageForPopUpPhoneNotConnected(),
			EpiInfoCompactUITranslator.getTitle(),
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
	
	public void notifyStartTestForPhoneCompatibility(EndpointMapping endpoint, String id){
		this.phoneCompatibilityInProcess = true;
		this.phoneCompatibilityEndpoint = endpoint;
		this.phoneCompatibilityId = id;
		this.fullDisableAllButtons();
		
		String msg = EpiInfoCompactUITranslator.getMessageTestingPhoneCompatibility();
		this.syncSessionView.setInProcess(msg);
		this.cfgFrame.notifyOwnerWorking();
		
		Action errorAction = new AbstractAction(){

			private static final long serialVersionUID = 4028395273128514170L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(phoneCompatibilityInProcess){
					phoneCompatibilityInProcess = false;
					
					String msg = EpiInfoCompactUITranslator.getMessageTimeOutPhoneCompatibility();
					syncSessionView.setError(msg);
					
					phoneCompatibilityEndpoint = null;
					phoneCompatibilityId = null;
					fullEnableAllButtons();
					
					cfgFrame.notifyOwnerNotWorking();
				}
			}
		};
		new Timer(getPropertiesProvider().getDefaultTestPhoneDelay(), errorAction).start();
	}
	
	public void notifyPhoneIsCompatible() {
		this.phoneCompatibilityInProcess = false;
		this.phoneCompatibilityEndpoint = null;
		this.phoneCompatibilityId = null;
		
		this.syncSessionView.setReady(EpiInfoCompactUITranslator.getMessagePhoneIsCompatible());
		this.fullEnableAllButtons();
		this.cfgFrame.notifyOwnerNotWorking();
	}
	
	public void notifyStartReadyToSync(EndpointMapping endpoint, DataSourceMapping dataSource){

		this.readyToSyncInProcess = true;
		this.readyToSyncEndpoint = endpoint;
		this.readyToSyncDataSource = dataSource;

		this.fullDisableAllButtons();

		String msg = EpiInfoCompactUITranslator.getMessageProcessingReadyToSync(endpoint.getAlias(), dataSource.getAlias());
		this.syncSessionView.setInProcess(msg);
		
		this.cfgFrame.notifyOwnerWorking();
		
		Action errorReadyToSync = new AbstractAction(){
			private static final long serialVersionUID = 4028395273128514170L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(readyToSyncInProcess){
					notifyEndpointIsNotReadyToSync();
				}
			}
		};
		new Timer(getPropertiesProvider().getDefaultReadyToSyncDelay(), errorReadyToSync).start();
	}
	
	public void notifyEndpointIsReadyToSync(){
		this.readyToSyncInProcess = false;
		
		this.syncSessionView.setReady(EpiInfoCompactUITranslator.getMessageEndpointIsReadyToSync(readyToSyncEndpoint.getAlias(), readyToSyncDataSource.getAlias()));
		this.readyToSyncEndpoint = null;
		this.readyToSyncDataSource = null;
		this.fullEnableAllButtons();
		
		this.cfgFrame.notifyOwnerNotWorking();
	}
	
	public void notifyEndpointIsNotReadyToSync(){
		readyToSyncInProcess = false;
		
		String msg = EpiInfoCompactUITranslator.getMessageEndpointIsNotReadyToSync(readyToSyncEndpoint.getAlias(), readyToSyncDataSource.getAlias());
		this.syncSessionView.setError(msg);
		readyToSyncEndpoint = null;
		readyToSyncDataSource = null;
		fullEnableAllButtons();
		
		this.cfgFrame.notifyOwnerNotWorking();
	}
	
	// enable/Disable
	public void enableAllButtons() {
		this.comboBoxEndpoint.setEnabled(true);
		this.comboBoxMappingDataSource.setEnabled(true);
		this.comboBoxSyncMode.setEnabled(true);
		
		this.buttonReadyToSync.setEnabled(true);
		this.buttonTestPhone.setEnabled(true);		
	}
	
	public void disableAllButtons() {
		this.comboBoxEndpoint.setEnabled(false);
		this.comboBoxMappingDataSource.setEnabled(false);
		this.comboBoxSyncMode.setEnabled(false);
		
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
		frame.getContentPane().setBackground(Color.WHITE);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		WindowAdapter windowAdapter = new WindowAdapter() {
			public void windowClosing(final WindowEvent e) {
				if(syncSessionView.isSyncInProcess()){
					Object[] options = {EpiInfoCompactUITranslator.getLabelCancelSyncAndCloseWindow(), EpiInfoCompactUITranslator.getLabelCancelCloseWindow()};
					int n = JOptionPane.showOptionDialog(
						frame,
						EpiInfoCompactUITranslator.getMessageForPopUpCloseWindows(getSelectedDataSource(), getSelectedEndpoint()),
						EpiInfoCompactUITranslator.getTitle(),
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,     //do not use a custom Icon
						options,  //the titles of buttons
						options[0]); //default button title
					if(n == 0){
						CancelSyncTask cancelSync = new CancelSyncTask(EpiinfoCompactUI.this);
						cancelSync.execute();
						
						EpiinfoCompactUI.this.close();
					} else {
						return;
					}
				} else {
					EpiinfoCompactUI.this.close();
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
		frame.setIconImage(EpiInfoIconManager.getCDCImage());
		frame.getContentPane().setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("20dlu"),
				ColumnSpec.decode("272dlu")},
			new RowSpec[] {
				RowSpec.decode("6dlu"),
				RowSpec.decode("120dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("120dlu"),
				RowSpec.decode("17dlu"),
				RowSpec.decode("28dlu")}));
		frame.setResizable(false);
		frame.setTitle(EpiInfoUITranslator.getTitle());
		frame.setBounds(100, 100, 590, 504);
		frame.getContentPane().add(getPanelSync(), new CellConstraints(2, 2));

		final JPanel panelStatusButtons = new JPanel();
		panelStatusButtons.setBackground(Color.WHITE);
		panelStatusButtons.setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("31dlu"),
				ColumnSpec.decode("36dlu"),
				ColumnSpec.decode("41dlu"),
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.DEFAULT_COLSPEC},
			new RowSpec[] {
				RowSpec.decode("9dlu")}));
		frame.getContentPane().add(panelStatusButtons, new CellConstraints(2, 5, CellConstraints.FILL, CellConstraints.FILL));

		panelStatusButtons.add(getButtonOpenLog(), new CellConstraints(1, 1, CellConstraints.CENTER, CellConstraints.FILL));
		panelStatusButtons.add(getButtonConfiguration(), new CellConstraints(3, 1, CellConstraints.CENTER, CellConstraints.FILL));

		final JButton emulateSyncButton = new JButton();
		emulateSyncButton.setFont(new Font("Calibri", Font.PLAIN, 10));
		emulateSyncButton.setContentAreaFilled(false);
		emulateSyncButton.setBorder(new EmptyBorder(0, 0, 0, 0));
		emulateSyncButton.setBorderPainted(false);
		emulateSyncButton.setName("");
		emulateSyncButton.setOpaque(false);
		emulateSyncButton.setText("Incoming Sync");
		ActionListener emulateSyncActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				EmulateIncomingSyncTask task = new EmulateIncomingSyncTask(EpiinfoCompactUI.this);
				task.execute();
			}
		};	
		emulateSyncButton.addActionListener(emulateSyncActionListener);
		
		panelStatusButtons.add(emulateSyncButton, new CellConstraints(5, 1, CellConstraints.CENTER, CellConstraints.FILL));

		final JButton emulatereadtyosyncButton = new JButton();
		emulatereadtyosyncButton.setContentAreaFilled(false);
		emulatereadtyosyncButton.setBorderPainted(false);
		emulatereadtyosyncButton.setBorder(new EmptyBorder(0, 0, 0, 0));
		emulatereadtyosyncButton.setFont(new Font("Calibri", Font.PLAIN, 10));
		emulatereadtyosyncButton.setOpaque(false);
		emulatereadtyosyncButton.setText("Ready to sync ok");
		ActionListener emulateReadyToSyncActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				EmulateReadyToSyncTask task = new EmulateReadyToSyncTask(EpiinfoCompactUI.this, true);
				task.execute();
			}
		};	
		emulatereadtyosyncButton.addActionListener(emulateReadyToSyncActionListener);
		
		panelStatusButtons.add(emulatereadtyosyncButton, new CellConstraints(6, 1, CellConstraints.CENTER, CellConstraints.FILL));

		final JButton emulateReadyNotOkButton = new JButton();
		emulateReadyNotOkButton.setContentAreaFilled(false);
		emulateReadyNotOkButton.setBorderPainted(false);
		emulateReadyNotOkButton.setBorder(new EmptyBorder(0, 0, 0, 0));
		emulateReadyNotOkButton.setFont(new Font("Calibri", Font.PLAIN, 10));
		emulateReadyNotOkButton.setText("Ready to sync not ok");
		
		ActionListener emulateReadyToSyncNotOkActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				EmulateReadyToSyncTask task = new EmulateReadyToSyncTask(EpiinfoCompactUI.this, false);
				task.execute();
			}
		};	
		emulateReadyNotOkButton.addActionListener(emulateReadyToSyncNotOkActionListener);
		panelStatusButtons.add(emulateReadyNotOkButton, new CellConstraints(7, 1));

		final JButton buttonOpenSessions = new JButton();
		buttonOpenSessions.setContentAreaFilled(false);
		buttonOpenSessions.setBorderPainted(false);
		buttonOpenSessions.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonOpenSessions.setFont(new Font("Calibri", Font.PLAIN, 10));
		buttonOpenSessions.setText("Open Sessions");
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
		panelStatusButtons.add(buttonOpenSessions, new CellConstraints(9, 1));

		final JButton emulateCancelSyncButton = new JButton();
		emulateCancelSyncButton.setFont(new Font("Calibri", Font.PLAIN, 10));
		emulateCancelSyncButton.setContentAreaFilled(false);
		emulateCancelSyncButton.setBorder(new EmptyBorder(0, 0, 0, 0));
		emulateCancelSyncButton.setBorderPainted(false);
		emulateCancelSyncButton.setName("");
		emulateCancelSyncButton.setOpaque(false);
		emulateCancelSyncButton.setText("Incoming Cancel");
		ActionListener emulateCancelSyncActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				EmulateIncomingCancelSyncTask task = new EmulateIncomingCancelSyncTask(EpiinfoCompactUI.this);
				task.execute();
			}
		};	
		emulateCancelSyncButton.addActionListener(emulateCancelSyncActionListener);
		panelStatusButtons.add(emulateCancelSyncButton, new CellConstraints(8, 1));

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

		final JLabel labelTrademark = new JLabel();
		labelTrademark.setFont(new Font("Calibri", Font.BOLD, 10));
		labelTrademark.setText(EpiInfoCompactUITranslator.getTradeMark());
		labelTrademark.setToolTipText(EpiInfoCompactUITranslator.getToolTipTradeMark());
		
		panelTrademark.add(labelTrademark, new CellConstraints(1, 1, 1, 2, CellConstraints.RIGHT, CellConstraints.BOTTOM));
		
		final JLabel imageTrademark = new JLabel();
		imageTrademark.setIcon(EpiInfoIconManager.getTrademarkIcon());
		imageTrademark.setText("");
		panelTrademark.add(imageTrademark, new CellConstraints(2, 1, 1, 2, CellConstraints.DEFAULT, CellConstraints.BOTTOM));

		frame.getContentPane().add(getSyncSessionView(), new CellConstraints(2, 4, CellConstraints.FILL, CellConstraints.FILL));
	}

	protected void close() {
		this.logFrame.setVisible(false);
		this.logFrame.dispose();
		
		this.cfgFrame.setVisible(false);
		this.cfgFrame.dispose();
		
		this.syncSessionsFrame.setVisible(false);
		this.syncSessionsFrame.dispose();
		
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
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("98dlu")},
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC}));
			panelSync.add(panel, new CellConstraints(1, 7, 5, 1));
			panel.add(getComboBoxMappingDataSource(), new CellConstraints());
			panel.add(getComboBoxSyncMode(), new CellConstraints(3, 1));

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
			labelSyncWith.setText(EpiInfoCompactUITranslator.getLabelSyncWith());
		}
		return labelSyncWith;
	}

	public JComboBox getComboBoxEndpoint() {
		if (comboBoxEndpoint == null) {
			comboBoxEndpoint = new JComboBox();
			comboBoxEndpoint.setFont(new Font("Calibri", Font.PLAIN, 12));
			comboBoxEndpoint.setModel(new DefaultComboBoxModel(SyncEngineUtil.getEndpointMappings(propertiesProvider)));
			comboBoxEndpoint.setToolTipText(EpiInfoCompactUITranslator.getToolTipEndpoints());
		}
		return comboBoxEndpoint;
	}

	protected JButton getButtonTestPhone() {
		if (buttonTestPhone == null) {
			buttonTestPhone = new JButton();
			buttonTestPhone.setFont(new Font("Calibri", Font.BOLD, 12));
			buttonTestPhone.setBackground(UIManager.getColor("Button.background"));
			buttonTestPhone.setText(EpiInfoCompactUITranslator.getLabelTestPhone());
			buttonTestPhone.setToolTipText(EpiInfoCompactUITranslator.getToolTipTestPhone());
			
			ActionListener testPhoneActionListener = new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					TestPhoneTask task = new TestPhoneTask(EpiinfoCompactUI.this);
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
			buttonReadyToSync.setText(EpiInfoCompactUITranslator.getLabelReadyToSync());
			buttonReadyToSync.setToolTipText(EpiInfoCompactUITranslator.getToolTipReadyToSync());
			
			ActionListener readyToSyncActionListener = new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					SwingWorker<Void, Void> task = new ReadyToSyncTask(EpiinfoCompactUI.this);
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
			buttonSync.setText(EpiInfoCompactUITranslator.getLabelSync());
			buttonSync.setToolTipText(EpiInfoCompactUITranslator.getToolTipSync());
			
			ActionListener synchronizeActionListener = new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					SwingWorker<Void, Void> task = syncSessionView.isSyncInProcess() ? new CancelSyncTask(EpiinfoCompactUI.this) : new SynchronizeTask(EpiinfoCompactUI.this);
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
			comboBoxMappingDataSource.setToolTipText(EpiInfoCompactUITranslator.getToolTipDataSources());
			
			notifyDataSourceMappingListsChanges();
		}
		return comboBoxMappingDataSource;
	}

	public JComboBox getComboBoxSyncMode() {
		if (comboBoxSyncMode == null) {
			comboBoxSyncMode = new JComboBox();
			comboBoxSyncMode.setFont(new Font("Calibri", Font.PLAIN, 12));
			comboBoxSyncMode.setToolTipText(EpiInfoCompactUITranslator.getToolTipSyncMode());

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
			buttonOpenLog.setText(EpiInfoCompactUITranslator.getLabelOpenLogWindow());
			buttonOpenLog.setToolTipText(EpiInfoCompactUITranslator.getToolTipOpenLogWindow());
			
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
			buttonConfiguration.setText(EpiInfoCompactUITranslator.getLabelOpenConfigurationWindow());
			buttonConfiguration.setToolTipText(EpiInfoCompactUITranslator.getToolTipConfigurationWindow());
			
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
			syncSessionView = new SyncSessionView(true);
		}
		return syncSessionView;
	}
	
	public Component getFrame() {
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

	public EpiinfoSourceIdResolver getSourceIdResolver() {
		return this.sourceIdResolver;
	}

	public void notifyEndpointMappingListsChanges() {
		comboBoxEndpoint.setModel(new DefaultComboBoxModel(SyncEngineUtil.getEndpointMappings(propertiesProvider)));		
	}

	public void notifyDataSourceMappingListsChanges() {
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		Iterator<DataSourceMapping> sources = sourceIdResolver.getDataSourceMappings().iterator();
		while(sources.hasNext()) {
			model.addElement(sources.next());			
		}
		comboBoxMappingDataSource.setModel(model);
	}

	public PropertiesProvider getPropertiesProvider() {
		return this.propertiesProvider;
	}

	// ISmsConnectionInboundOutboundNotification
	@Override
	public void notifyReceiveMessageError(String endpointId, String message, Date date) {
		// nothing to do		
	}

	@Override
	public void notifyReceiveMessageWasNotProcessed(String endpointId, String message, Date date) {
		if(this.messageFilter.applies(message)){
			if(ReadyToSyncTask.isQuestion(message)){
				String dataSourceAlias = ReadyToSyncTask.getDataSourceAlias(message);
				
				boolean isDataSourceAvailable = sourceIdResolver.isDataSourceAvailable(dataSourceAlias);
				ReadyToSyncResponseTask responseTask = new ReadyToSyncResponseTask(this, endpointId, dataSourceAlias, isDataSourceAvailable);
				responseTask.execute();
			}
			
			if(this.readyToSyncInProcess 
					&& this.readyToSyncEndpoint.getEndpoint().equals(endpointId)){
				if(ReadyToSyncTask.isAnswerOk(message, this.readyToSyncDataSource.getAlias())){
					this.notifyEndpointIsReadyToSync();
				}
				
				if(ReadyToSyncTask.isAnswerNotOk(message, this.readyToSyncDataSource.getAlias())){
					this.notifyEndpointIsNotReadyToSync();
				}
			} 

			if(this.phoneCompatibilityInProcess 
					&& this.phoneCompatibilityEndpoint.getEndpoint().equals(endpointId) 
					&& TestPhoneTask.makeAnswer(this.phoneCompatibilityId).equals(message)){
				this.notifyPhoneIsCompatible();
			} 		
		}
	}

	@Override
	public void notifySendMessage(String endpointId, String message) {
		// nothing to do		
	}

	@Override
	public void notifySendMessageError(String endpointId, String message) {
		// nothing to do		
	}

	@Override
	public void notifyReceiveMessage(String endpointId, String message, Date date) {
		// nothing to do		
	}

	// ISyncSessionViewOwner methods
	@Override
	public void notifyNewSync() {
		this.syncSessionsFrame.notifyNewSync();
	}

	@Override
	public void notifyBeginSync() {
		this.disableAllButtons();		
		this.buttonSync.setText(EpiInfoCompactUITranslator.getLabelCancelSync());
		this.cfgFrame.notifyOwnerWorking();
	}

}