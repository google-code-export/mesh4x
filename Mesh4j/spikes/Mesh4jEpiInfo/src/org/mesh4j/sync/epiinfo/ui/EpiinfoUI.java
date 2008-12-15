package org.mesh4j.sync.epiinfo.ui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.adapters.http.HttpSyncAdapterFactory;
import org.mesh4j.sync.adapters.kml.exporter.KMLExporter;
import org.mesh4j.sync.adapters.msaccess.MsAccessHelper;
import org.mesh4j.sync.adapters.msaccess.MsAccessSyncAdapterFactory;
import org.mesh4j.sync.message.MessageSyncEngine;
import org.mesh4j.sync.message.channel.sms.connection.smslib.Modem;
import org.mesh4j.sync.message.channel.sms.connection.smslib.ModemHelper;
import org.mesh4j.sync.message.encoding.IMessageEncoding;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.properties.PropertiesProvider;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.ui.translator.EpiInfoUITranslator;
import org.mesh4j.sync.utils.EpiInfoConsoleNotification;
import org.mesh4j.sync.utils.FileNameResolver;
import org.mesh4j.sync.utils.SyncEngineUtil;

import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.swtdesigner.FocusTraversalOnArray;
import com.swtdesigner.SwingResourceManager;

public class EpiinfoUI{

	private final static Log Logger = LogFactory.getLog(EpiinfoUI.class);

	// CONSTANTS
	private final static int SYNCHRONIZE_HTTP = -1;
	private final static int SYNCHRONIZE_SMS = 0;
	private final static int CANCEL_SYNC = 1;
	private final static int ADD_DATA_SOURCE = 2;
	private static final int DISCOVERY_MODEMS = 3;
	private final static int CHANGE_DEVICE = 4;
	private final static int SAVE_DEFAULTS = 5;
	private final static int GENERATE_KML = 6;
	private final static int GENERATE_KML_WEB = 7;
	private final static int DOWNLOAD_SCHEMA = 8;
	private final static int DOWNLOAD_MAPPINGS = 9;
	
	// MODEL VARIABLES
	private JFrame frame;
	private JComboBox comboSMSDevice;
	private JTextField textFieldPhoneNumber;
	private JTextField textFieldDataSource;
	private JComboBox comboTables;
	private JTextArea textAreaConsole;
	private JButton buttonSynchronize;
	private JButton buttonCancel;
	private JButton buttonClean;
	private JScrollPane scrollPaneConsole;
	private JButton buttonModemDiscovery;
	private JButton buttonAddDataSource;
	private JButton buttonOpenFileDataSource;
	private ButtonGroup buttonGroup = new ButtonGroup();	
	private JTextField textFieldURL;
	private JLabel labelUrl;
	private JLabel labelSMSDevice;
	private JLabel labelPhoneNumber;
	private JRadioButton radioEndpointSMS;
	private JRadioButton radioEndpointHTTP;
	private JButton buttonSaveDefaults;
	private JButton buttonKmlGenerator;
	private JButton buttonKmlWebGenerator;
	private JButton buttonDownloadSchema;
	private JButton buttonHideShowConsole;
	private JButton buttonHideShowConsole1;
	private JButton buttonHideShowConsole2;
	private JLabel imageStatus;
	
	private EpiInfoConsoleNotification consoleNotification;
	
	private Modem modem = null;
	private FileNameResolver fileNameResolver;
	private MessageSyncEngine syncEngine;
	private boolean emulate = false;
	private boolean syncInProcess = false;
	private int channel = SYNCHRONIZE_HTTP;
	
	private IIdentityProvider identityProvider = NullIdentityProvider.INSTANCE;
	private String baseDirectory;
	private int senderDelay;
	private int receiverDelay;
	private int maxMessageLenght;
	private int readDelay;
	private int channelDelay;
	private IMessageEncoding messageEncoding;
	private String portName;
	private int baudRate;
	private String defaultPhoneNumber;
	private String defaultDataSource;
	private String defaultTableName;
	private String defaultURL;
	private String kmlTemplateFileName;
	private String geoCoderKey;
	private boolean discoveryModems = false;
	
	// BUSINESS METHODS
	
	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EpiinfoUI window = new EpiinfoUI();
					window.frame.pack();
					window.frame.setSize(window.frame.getPreferredSize());
					window.frame.setVisible(true);
				} catch (Exception e) {
					Logger.error(e.getMessage(), e);
				}
			}
		});
	}

	public EpiinfoUI() throws Exception {
		this.initializeProperties();
		this.initializeModem();
		this.createUI();
		this.consoleNotification = new EpiInfoConsoleNotification(this.textAreaConsole, this.imageStatus, this);
		this.consoleNotification.setReadyImageStatus();
		this.startUpSyncEngine();
	}

	private void initializeModem() {
		if(portName.length() > 0 && baudRate > 0){
			modem = ModemHelper.getModem(portName, baudRate);
		}
	}

	private void initializeProperties() throws Exception {
		PropertiesProvider propertiesProvider = new PropertiesProvider();
		
		this.baseDirectory = propertiesProvider.getBaseDirectory();
		this.senderDelay = propertiesProvider.getInt("default.sms.sender.delay");
		this.receiverDelay = propertiesProvider.getInt("default.sms.receiver.delay");
		this.maxMessageLenght = propertiesProvider.getInt("default.sms.max.message.lenght");
		this.readDelay = propertiesProvider.getInt("default.sms.demo.read.delay");
		this.channelDelay = propertiesProvider.getInt("default.sms.demo.channel.delay");
		this.identityProvider = propertiesProvider.getIdentityProvider();
		this.messageEncoding = propertiesProvider.getDefaultMessageEncoding();
		this.portName = propertiesProvider.getDefaultPort();
		this.baudRate = propertiesProvider.getDefaultBaudRate();
		this.defaultPhoneNumber = propertiesProvider.getDefaultPhoneNumber();
		this.defaultDataSource = propertiesProvider.getDefaultDataSource();
		this.defaultTableName = propertiesProvider.getDefaultTable();
		this.defaultURL = propertiesProvider.getDefaultURL();
		this.kmlTemplateFileName = propertiesProvider.getDefaultKMLTemplateFileName();
		this.geoCoderKey = propertiesProvider.getGeoCoderKey();
	}
	
	protected void startUpSyncEngine() throws Exception {
		this.fileNameResolver = new FileNameResolver(baseDirectory+"/myFiles.properties");
		
		if(modem != null && !modem.getManufacturer().equals(EpiInfoUITranslator.getLabelDemo())){
			this.syncEngine = SyncEngineUtil.createSyncEngine(fileNameResolver, modem, baseDirectory, senderDelay, receiverDelay, maxMessageLenght,
				identityProvider, messageEncoding, consoleNotification, consoleNotification);  
		}
				
		if(this.syncEngine == null){
			this.syncEngine = SyncEngineUtil.createEmulator(fileNameResolver, consoleNotification, consoleNotification, EpiInfoUITranslator.getLabelDemo(), messageEncoding, identityProvider, baseDirectory, senderDelay, receiverDelay, readDelay, channelDelay, maxMessageLenght);
			this.emulate = true;
		}
	}

	private void createUI() {
		frame = new JFrame();
		
		WindowAdapter windowAdapter = new WindowAdapter() {
			public void windowClosed(final WindowEvent e) {
				shutdownSyncEngine();
			}
		};
		
		frame.addWindowListener(windowAdapter);
		frame.setIconImage(SwingResourceManager.getImage(EpiinfoUI.class, "/cdc.gif"));
		frame.getContentPane().setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("322dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("108dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("41dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("103dlu"),
				FormFactory.RELATED_GAP_ROWSPEC}));
		frame.setResizable(false);
		frame.setTitle(EpiInfoUITranslator.getTitle());
		frame.setBounds(100, 100, 664, 572);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final JPanel panelCommunications = new JPanel();
		panelCommunications.setFocusCycleRoot(true);
		panelCommunications.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED), EpiInfoUITranslator.getGroupCommunications(), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		panelCommunications.setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("44dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("221dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("36dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("12dlu"),
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				RowSpec.decode("9dlu"),
				RowSpec.decode("11dlu"),
				RowSpec.decode("14dlu"),
				FormFactory.RELATED_GAP_ROWSPEC}));
		frame.getContentPane().add(panelCommunications, new CellConstraints(2, 2, CellConstraints.FILL, CellConstraints.FILL));

		textFieldPhoneNumber = new JTextField();
		textFieldPhoneNumber.setToolTipText(EpiInfoUITranslator.getToolTipPhoneNumber());
		panelCommunications.add(textFieldPhoneNumber, new CellConstraints(4, 5, 3, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
		textFieldPhoneNumber.setText(this.defaultPhoneNumber);
		
		labelSMSDevice = DefaultComponentFactory.getInstance().createLabel(EpiInfoUITranslator.getLabelSMSDevice());
		panelCommunications.add(labelSMSDevice, new CellConstraints(2, 3, CellConstraints.RIGHT, CellConstraints.DEFAULT));

		labelPhoneNumber = DefaultComponentFactory.getInstance().createLabel(EpiInfoUITranslator.getLabelPhoneNumber());
		panelCommunications.add(labelPhoneNumber, new CellConstraints(2, 5, CellConstraints.RIGHT, CellConstraints.DEFAULT));

		ActionListener deviceActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Task task = new Task(CHANGE_DEVICE);
				task.execute();
			}
		};
		
		comboSMSDevice = new JComboBox();
		comboSMSDevice.setFocusable(false);
		
		Modem demoModem = getDemoModem();
		comboSMSDevice.setModel(modem == null ? new DefaultComboBoxModel(new Modem[]{demoModem}) : new DefaultComboBoxModel(new Modem[]{modem}));
		comboSMSDevice.setToolTipText(modem == null ? demoModem.toString() : modem.toString());
		comboSMSDevice.addActionListener(deviceActionListener);
		panelCommunications.add(comboSMSDevice, new CellConstraints(4, 3, CellConstraints.FILL, CellConstraints.DEFAULT));

		ActionListener modemDiscoveryActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(isDicoveringModems()){
					consoleNotification.stopDiscovery();
				} else {
					Task task = new Task(DISCOVERY_MODEMS);
					task.execute();
				}
			}
		};	
		
		buttonModemDiscovery = new JButton();
		buttonModemDiscovery.setFocusable(false);
		buttonModemDiscovery.setText(EpiInfoUITranslator.getLabelModemDiscovery());
		buttonModemDiscovery.setToolTipText(EpiInfoUITranslator.getToolTipAutoDetect());
		buttonModemDiscovery.addActionListener(modemDiscoveryActionListener);
		panelCommunications.add(buttonModemDiscovery, new CellConstraints(6, 3, CellConstraints.FILL, CellConstraints.FILL));

		labelUrl = new JLabel();
		labelUrl.setText(EpiInfoUITranslator.getLabelURL());
		panelCommunications.add(labelUrl, new CellConstraints(2, 8, CellConstraints.RIGHT, CellConstraints.DEFAULT));

		textFieldURL = new JTextField();
		textFieldURL.setText(this.defaultURL);
		panelCommunications.add(textFieldURL, new CellConstraints(4, 8, 3, 1));

		ActionListener channelSMSActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				textFieldURL.setEnabled(false);
				labelUrl.setEnabled(false);
				
				textFieldPhoneNumber.setEnabled(true);
				comboSMSDevice.setEnabled(true);
				buttonModemDiscovery.setEnabled(true);
				labelSMSDevice.setEnabled(true);
				labelPhoneNumber.setEnabled(true);
				
				channel = SYNCHRONIZE_SMS;
			}
		};
		
		radioEndpointSMS = new JRadioButton();
		buttonGroup.add(radioEndpointSMS);
		radioEndpointSMS.setText(EpiInfoUITranslator.getLabelChannelSMS());
		radioEndpointSMS.addActionListener(channelSMSActionListener);
		panelCommunications.add(radioEndpointSMS, new CellConstraints(2, 2));

		ActionListener channelHTTPActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				textFieldURL.setEnabled(true);
				labelUrl.setEnabled(true);
				
				textFieldPhoneNumber.setEnabled(false);
				comboSMSDevice.setEnabled(false);
				buttonModemDiscovery.setEnabled(false);
				labelSMSDevice.setEnabled(false);
				labelPhoneNumber.setEnabled(false);
				 
				channel = SYNCHRONIZE_HTTP;
			}
		};
		
		radioEndpointHTTP = new JRadioButton();
		buttonGroup.add(radioEndpointHTTP);
		radioEndpointHTTP.setText(EpiInfoUITranslator.getLabelChannelWEB());
		radioEndpointHTTP.setSelected(true);
		radioEndpointHTTP.addActionListener(channelHTTPActionListener);

		panelCommunications.add(radioEndpointHTTP, new CellConstraints(2, 7));
		panelCommunications.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[] {labelSMSDevice, labelPhoneNumber, textFieldPhoneNumber, buttonSynchronize}));

		final JPanel panelDataSource = new JPanel();
		panelDataSource.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED), EpiInfoUITranslator.getLabelDataSource(), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		panelDataSource.setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("251dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("35dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC}));
		frame.getContentPane().add(panelDataSource, new CellConstraints(2, 4, CellConstraints.FILL, CellConstraints.FILL));

		textFieldDataSource = new JTextField();
		textFieldDataSource.setToolTipText(EpiInfoUITranslator.getToolTipDataSource());
		textFieldDataSource.setText(this.defaultDataSource);
		panelDataSource.add(textFieldDataSource, new CellConstraints(2, 2, 3, 1, CellConstraints.FILL, CellConstraints.FILL));

		ActionListener fileChooserFileActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String selectedFileName = openFileDialog(textFieldDataSource.getText());
				if(selectedFileName != null){
					textFieldDataSource.setText(selectedFileName);
				}
			}
		};
		
		buttonOpenFileDataSource = new JButton();
		buttonOpenFileDataSource.setToolTipText(EpiInfoUITranslator.getToolTipFileChooser());
		buttonOpenFileDataSource.setText(EpiInfoUITranslator.getLabelFileChooser());
		buttonOpenFileDataSource.addActionListener(fileChooserFileActionListener);
		panelDataSource.add(buttonOpenFileDataSource, new CellConstraints(6, 2, CellConstraints.FILL, CellConstraints.FILL));

		final JPanel panelButtons = new JPanel();
		panelButtons.setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC},
			new RowSpec[] {
				}));
		panelDataSource.add(panelButtons, new CellConstraints(2, 6, 5, 1));

		final JLabel labelTable = new JLabel();
		labelTable.setText(EpiInfoUITranslator.getLabelTable());
		panelDataSource.add(labelTable, new CellConstraints(2, 4));

		comboTables = new JComboBox();
		comboTables.setModel(this.getDataSourceTableModel());
		panelDataSource.add(comboTables, new CellConstraints(4, 4));

		ActionListener addDataSourceActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Task task = new Task(ADD_DATA_SOURCE);
				task.execute();
			}
		};	
		
		buttonAddDataSource = new JButton();
		buttonAddDataSource.setText(EpiInfoUITranslator.getLabelAddDataSource());
		buttonAddDataSource.addActionListener(addDataSourceActionListener);
		panelDataSource.add(buttonAddDataSource, new CellConstraints(6, 4));
		
		final JPanel panelFooter = new JPanel();
		panelFooter.setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("37dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("177dlu"),
				ColumnSpec.decode("5dlu"),
				ColumnSpec.decode("95dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				RowSpec.decode("104dlu")}));
		frame.getContentPane().add(panelFooter, new CellConstraints(2, 8, CellConstraints.FILL, CellConstraints.FILL));

		scrollPaneConsole = new JScrollPane();
		scrollPaneConsole.setBorder(new BevelBorder(BevelBorder.LOWERED));
		scrollPaneConsole.setAutoscrolls(true);
		panelFooter.add(scrollPaneConsole, new CellConstraints("2, 1, 5, 1, fill, fill"));

		textAreaConsole = new JTextArea();
		scrollPaneConsole.setViewportView(textAreaConsole);
		textAreaConsole.setFocusAccelerator('\b');
		textAreaConsole.setAutoscrolls(true);
		textAreaConsole.setWrapStyleWord(false);
		textAreaConsole.setLineWrap(false);
		textAreaConsole.setOpaque(false);
		textAreaConsole.setToolTipText("");
		textAreaConsole.setName("");
		textAreaConsole.setEditable(false);

		scrollPaneConsole.setVisible(false);

		final JLabel labelLogoEpiinfo = new JLabel();
		labelLogoEpiinfo.setIcon(SwingResourceManager.getIcon(EpiinfoUI.class, "/Epi2002.jpg"));
		labelLogoEpiinfo.setText("");
		panelFooter.add(labelLogoEpiinfo, new CellConstraints(3, 1, 4, 1, CellConstraints.FILL, CellConstraints.FILL));

		imageStatus = new JLabel();
		imageStatus.setText("");
		panelFooter.add(imageStatus, new CellConstraints(1, 1, CellConstraints.CENTER, CellConstraints.CENTER));

		// disable sms channel group
		textFieldPhoneNumber.setEnabled(false);
		comboSMSDevice.setEnabled(false);
		buttonModemDiscovery.setEnabled(false);
		labelSMSDevice.setEnabled(false);
		labelPhoneNumber.setEnabled(false);

		// Tabbed panel
		JPanel panelExchange = new JPanel(false);
		panelExchange.setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("53dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("91dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("65dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC}));
				
		ActionListener synchronizeActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Task task = new Task(channel);
				task.execute();
			}
		};	
		buttonSynchronize = new JButton();
		panelExchange.add(buttonSynchronize, new CellConstraints(2, 2, CellConstraints.FILL, CellConstraints.FILL));
		buttonSynchronize.setText(EpiInfoUITranslator.getSynchronize());
		buttonSynchronize.addActionListener(synchronizeActionListener);
		
		ActionListener cancelActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Task task = new Task(CANCEL_SYNC);
				task.execute();
			}
		};	
		buttonCancel = new JButton();
		panelExchange.add(buttonCancel, new CellConstraints(4, 2, CellConstraints.FILL, CellConstraints.FILL));
		buttonCancel.setText(EpiInfoUITranslator.getCancel());
		buttonCancel.addActionListener(cancelActionListener);
		
		ActionListener hiShowActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				scrollPaneConsole.setVisible(!scrollPaneConsole.isVisible());
				if(scrollPaneConsole.isVisible()){
					buttonHideShowConsole.setText(EpiInfoUITranslator.getLabelHideConsole());
					buttonHideShowConsole.setToolTipText(EpiInfoUITranslator.getToolTipHideConsole());
					buttonHideShowConsole1.setText(EpiInfoUITranslator.getLabelHideConsole());
					buttonHideShowConsole1.setToolTipText(EpiInfoUITranslator.getToolTipHideConsole());
					buttonHideShowConsole2.setText(EpiInfoUITranslator.getLabelHideConsole());
					buttonHideShowConsole2.setToolTipText(EpiInfoUITranslator.getToolTipHideConsole());
					labelLogoEpiinfo.setVisible(false);
				}else{
					buttonHideShowConsole.setText(EpiInfoUITranslator.getLabelShowConsole());
					buttonHideShowConsole.setToolTipText(EpiInfoUITranslator.getToolTipShowConsole());
					buttonHideShowConsole1.setText(EpiInfoUITranslator.getLabelShowConsole());
					buttonHideShowConsole1.setToolTipText(EpiInfoUITranslator.getToolTipShowConsole());
					buttonHideShowConsole2.setText(EpiInfoUITranslator.getLabelShowConsole());
					buttonHideShowConsole2.setToolTipText(EpiInfoUITranslator.getToolTipShowConsole());
					labelLogoEpiinfo.setVisible(true);
				}				
				frame.pack();
				frame.repaint();
			}
		};	
		buttonHideShowConsole = new JButton();
		buttonHideShowConsole.setText(EpiInfoUITranslator.getLabelShowConsole());
		buttonHideShowConsole.setToolTipText(EpiInfoUITranslator.getToolTipShowConsole());
		buttonHideShowConsole.addActionListener(hiShowActionListener);
		panelExchange.add(buttonHideShowConsole, new CellConstraints(6, 2, CellConstraints.CENTER, CellConstraints.FILL));
	       
	    JPanel panelMap = new JPanel(false);
	    panelMap.setLayout(new FormLayout(
	    	new ColumnSpec[] {
	    		FormFactory.RELATED_GAP_COLSPEC,
	    		ColumnSpec.decode("53dlu"),
	    		FormFactory.RELATED_GAP_COLSPEC,
	    		ColumnSpec.decode("47dlu"),
	    		FormFactory.RELATED_GAP_COLSPEC,
	    		ColumnSpec.decode("65dlu"),
	    		FormFactory.RELATED_GAP_COLSPEC},
	    	new RowSpec[] {
	    		FormFactory.RELATED_GAP_ROWSPEC,
	    		FormFactory.DEFAULT_ROWSPEC,
	    		FormFactory.RELATED_GAP_ROWSPEC}));
	    
		ActionListener kmlGeneratorActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Task task = new Task(GENERATE_KML);
				task.execute();
			}
		};
	    buttonKmlGenerator = new JButton();
	    buttonKmlGenerator.addActionListener(kmlGeneratorActionListener);
	    buttonKmlGenerator.setText(EpiInfoUITranslator.getLabelKML());
	    panelMap.add(buttonKmlGenerator, new CellConstraints(2, 2));


		ActionListener kmlWebGeneratorActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Task task = new Task(GENERATE_KML_WEB);
				task.execute();
			}
		};
	    buttonKmlWebGenerator = new JButton();
	    buttonKmlWebGenerator.setText(EpiInfoUITranslator.getLabelKMLWEB());
	    buttonKmlWebGenerator.addActionListener(kmlWebGeneratorActionListener);
	    panelMap.add(buttonKmlWebGenerator, new CellConstraints(4, 2));

		buttonHideShowConsole1 = new JButton();
		buttonHideShowConsole1.setText(EpiInfoUITranslator.getLabelShowConsole());
		buttonHideShowConsole1.setToolTipText(EpiInfoUITranslator.getToolTipShowConsole());
		buttonHideShowConsole1.addActionListener(hiShowActionListener);
	    panelMap.add(buttonHideShowConsole1, new CellConstraints(6, 2));
	    	    
	    JPanel panelSettings = new JPanel(false);
	    panelSettings.setLayout(new FormLayout(
	    	new ColumnSpec[] {
	    		FormFactory.RELATED_GAP_COLSPEC,
	    		ColumnSpec.decode("57dlu"),
	    		FormFactory.RELATED_GAP_COLSPEC,
	    		ColumnSpec.decode("78dlu"),
	    		FormFactory.RELATED_GAP_COLSPEC,
	    		ColumnSpec.decode("72dlu"),
	    		FormFactory.RELATED_GAP_COLSPEC,
	    		ColumnSpec.decode("76dlu"),
	    		FormFactory.RELATED_GAP_COLSPEC},
	    	new RowSpec[] {
	    		FormFactory.RELATED_GAP_ROWSPEC,
	    		FormFactory.DEFAULT_ROWSPEC,
	    		FormFactory.RELATED_GAP_ROWSPEC}));
	    
		ActionListener saveDefaultActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Task task = new Task(SAVE_DEFAULTS);
				task.execute();
			}
		};		
		buttonSaveDefaults = new JButton();
		buttonSaveDefaults.setText(EpiInfoUITranslator.getLabelSaveDefaults());
		buttonSaveDefaults.addActionListener(saveDefaultActionListener);
	    panelSettings.add(buttonSaveDefaults, new CellConstraints(2, 2, CellConstraints.FILL, CellConstraints.CENTER));

		ActionListener downloadSchemaActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Task task = new Task(DOWNLOAD_MAPPINGS);
				task.execute();
			}
		};
		buttonDownloadSchema = new JButton();
		buttonDownloadSchema.setText(EpiInfoUITranslator.getLabelDownloadMappings());
		buttonDownloadSchema.addActionListener(downloadSchemaActionListener);
		panelSettings.add(buttonDownloadSchema, new CellConstraints(4, 2, CellConstraints.FILL, CellConstraints.CENTER));

		ActionListener cleanConsoleActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				textAreaConsole.setText("");
			}
		};	
		buttonClean = new JButton();
		buttonClean.setText(EpiInfoUITranslator.getLabelCleanConsole());
		buttonClean.addActionListener(cleanConsoleActionListener);
	    panelSettings.add(buttonClean, new CellConstraints(6, 2, CellConstraints.FILL, CellConstraints.CENTER));

		buttonHideShowConsole2 = new JButton();
		buttonHideShowConsole2.setText(EpiInfoUITranslator.getLabelShowConsole());
		buttonHideShowConsole2.setToolTipText(EpiInfoUITranslator.getToolTipShowConsole());
		buttonHideShowConsole2.addActionListener(hiShowActionListener);
	    panelSettings.add(buttonHideShowConsole2, new CellConstraints(8, 2));
	    
	    JTabbedPane tabbedPane = new JTabbedPane();
	    tabbedPane.addTab(EpiInfoUITranslator.getLabelTabDataExchange(), panelExchange);
	    tabbedPane.addTab(EpiInfoUITranslator.getLabelTabMap(), panelMap);
	    tabbedPane.addTab(EpiInfoUITranslator.getLabelTabSettings(), panelSettings);
	    
	    frame.getContentPane().add(tabbedPane, new CellConstraints(2, 6));
	}

	private Modem getDemoModem() {
		return new Modem("", 0, EpiInfoUITranslator.getLabelDemo(), "", "", "", 0, 0);
	}

	private ComboBoxModel getDataSourceTableModel() {
		try{
			Set<String> tableNames = MsAccessHelper.getTableNames(this.defaultDataSource);
			ComboBoxModel model = new DefaultComboBoxModel(tableNames.toArray());
			model.setSelectedItem(this.defaultTableName);
			return model;
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			return new DefaultComboBoxModel();
		}
	}

	private void shutdownSyncEngine(){
		try{
			this.syncEngine.getChannel().shutdown();
		} catch(Throwable e){
			Logger.error(e.getMessage(), e);
		}
	}
	
	private class Task extends SwingWorker<Void, Void> {
		 
		// MODEL VARIABLES
		private int action = 0;
		 
		// BUSINESS METHODS
	    public Task(int action) {
			super();
			this.action = action;
		}
	
		@Override
	    public Void doInBackground() {
			disableAllButtons();
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			consoleNotification.setInProcessImageStatus();
			
    		if(action == SYNCHRONIZE_SMS){
    			syncInProcess = true;
    			textAreaConsole.setText("");
    			
				String dataSource = textFieldDataSource.getText();
				String tableName = (String)comboTables.getSelectedItem();
				if(!MsAccessSyncAdapterFactory.isValidAccessTable(dataSource, tableName)){
	    			consoleNotification.log(EpiInfoUITranslator.getErrorInvalidMSAccessTable());
	    			consoleNotification.setErrorImageStatus();
					return null;
				}
    			
    			SyncEngineUtil.addDataSource(fileNameResolver, dataSource);

    			try{
	    			if(emulate){	    				
	    				SyncEngineUtil.registerNewEndpointToEmulator(syncEngine, textFieldPhoneNumber.getText(), messageEncoding, 
	    					identityProvider, baseDirectory, senderDelay, receiverDelay, readDelay, channelDelay, maxMessageLenght);
	    			}
					SyncEngineUtil.synchronize(syncEngine, textFieldPhoneNumber.getText(), 
	    				dataSource, tableName, identityProvider, baseDirectory, fileNameResolver);

	    		} catch(Throwable t){
	    			consoleNotification.setErrorImageStatus();
	    			consoleNotification.logError(t, EpiInfoUITranslator.getLabelFailed());
//	    			consoleNotification.logStatus(EpiInfoUITranslator.getLabelFailed());
	    		}
    		} 

    		if(action == SYNCHRONIZE_HTTP){
				String url = textFieldURL.getText();
				if(!HttpSyncAdapterFactory.isValidURL(url)){
	    			consoleNotification.log(EpiInfoUITranslator.getErrorInvalidURL());
	    			consoleNotification.setErrorImageStatus();
					return null;
				}
				
				String dataSource = textFieldDataSource.getText();
				String tableName = (String)comboTables.getSelectedItem();
				if(!MsAccessSyncAdapterFactory.isValidAccessTable(dataSource, tableName)){
	    			consoleNotification.log(EpiInfoUITranslator.getErrorInvalidMSAccessTable());
	    			consoleNotification.setErrorImageStatus();
					return null;
				}
				
    			try{
    				consoleNotification.beginSync(url, dataSource, tableName);
    				List<Item> conflicts = SyncEngineUtil.synchronize(textFieldURL.getText(), textFieldDataSource.getText(), (String)comboTables.getSelectedItem(), identityProvider, baseDirectory, fileNameResolver);
    				consoleNotification.endSync(textFieldURL.getText(), textFieldDataSource.getText(), (String)comboTables.getSelectedItem(), conflicts);
	    			consoleNotification.setEndSyncImageStatus();
	    		} catch(Throwable t){
	    			consoleNotification.setErrorImageStatus();
	    			consoleNotification.logError(t, EpiInfoUITranslator.getLabelFailed());
//	    			consoleNotification.logStatus(EpiInfoUITranslator.getLabelFailed());
	    		}
    		} 

    		if(action == CANCEL_SYNC){
    			SyncEngineUtil.cancelSynchronize(syncEngine, textFieldPhoneNumber.getText(), textFieldDataSource.getText(), (String)comboTables.getSelectedItem());
    			consoleNotification.setEndSyncImageStatus();
    		} 

    		if(action == ADD_DATA_SOURCE){
    			try{
    				SyncEngineUtil.addDataSource(fileNameResolver, textFieldDataSource.getText());
    				consoleNotification.setEndSyncImageStatus();
	    		} catch(Throwable t){
	    			consoleNotification.setErrorImageStatus();
	    			consoleNotification.logError(t, t.getMessage());
	    		}
    		}
    		
    		if(action == DISCOVERY_MODEMS){
				consoleNotification.log(EpiInfoUITranslator.getMessageBeginModemDiscovery());
    			setModeDiscoveryModems();
				buttonModemDiscovery.setText(EpiInfoUITranslator.getLabelStopModemDiscovery());
				buttonModemDiscovery.setToolTipText(EpiInfoUITranslator.getToolTipStopAutoDetect());    			
    			
    			Modem[] modems = SyncEngineUtil.getAvailableModems(consoleNotification, getDemoModem());
    			comboSMSDevice.setModel(new DefaultComboBoxModel(modems));
    			    			
    			setModeNoDiscoveryModems();
    			buttonModemDiscovery.setText(EpiInfoUITranslator.getLabelModemDiscovery());
    			buttonModemDiscovery.setToolTipText(EpiInfoUITranslator.getToolTipAutoDetect());
    			
    			consoleNotification.log(EpiInfoUITranslator.getMessageEndModemDiscovery(modems.length));
    			consoleNotification.setEndSyncImageStatus();
    		}
    		
    		if(action == CHANGE_DEVICE){
				modem = (Modem)comboSMSDevice.getSelectedItem();
				shutdownSyncEngine();
				
				try{
					startUpSyncEngine();
	    			consoleNotification.setEndSyncImageStatus();
				} catch (Exception exc) {
					shutdownSyncEngine();
					consoleNotification.setErrorImageStatus();
					consoleNotification.logError(exc, EpiInfoUITranslator.getLabelDeviceConnectionFailed(modem.toString()));
					Logger.error(exc.getMessage(), exc);
				}
				comboSMSDevice.setToolTipText(modem.toString());
    		}
    		
    		if(action == SAVE_DEFAULTS){
    			try{
    				SyncEngineUtil.saveDefaults(modem, textFieldPhoneNumber.getText(), textFieldDataSource.getText(), (String)comboTables.getSelectedItem(), textFieldURL.getText());
    				consoleNotification.setEndSyncImageStatus();
	    		} catch(Throwable t){
	    			consoleNotification.setErrorImageStatus();
	    			consoleNotification.logError(t, t.getMessage());
	    		}
    		}
    		
    		if(action == GENERATE_KML){
				String dataSource = textFieldDataSource.getText();
				String tableName = (String)comboTables.getSelectedItem();
				if(!MsAccessSyncAdapterFactory.isValidAccessTable(dataSource, tableName)){
					consoleNotification.setErrorImageStatus();
	    			consoleNotification.log(EpiInfoUITranslator.getErrorInvalidMSAccessTable());
					return null;
				}
    			try{
    				SyncEngineUtil.generateKML(geoCoderKey, kmlTemplateFileName, getModemPhoneNumber(), dataSource, tableName, baseDirectory, fileNameResolver, identityProvider);
	    			consoleNotification.setEndSyncImageStatus();
	    		} catch(Throwable t){
	    			consoleNotification.setErrorImageStatus();
	    			consoleNotification.logError(t, EpiInfoUITranslator.getLabelKMLFailed());
	    		}
    		}
    		
    		if(action == GENERATE_KML_WEB){
				String url = textFieldURL.getText();
				if(!HttpSyncAdapterFactory.isValidURL(url)){
					consoleNotification.setErrorImageStatus();
	    			consoleNotification.log(EpiInfoUITranslator.getErrorInvalidURL());
					return null;
				}
				
				String dataSource = textFieldDataSource.getText();
				String tableName = (String)comboTables.getSelectedItem();
				if(!MsAccessSyncAdapterFactory.isValidAccessTable(dataSource, tableName)){
					consoleNotification.setErrorImageStatus();
	    			consoleNotification.log(EpiInfoUITranslator.getErrorInvalidMSAccessTable());
					return null;
				}
				
    			try{
	    			String urlWebKml = url + "?format=kml";
	    			String fileName = baseDirectory + "/"+ tableName + "_web.kml";

	    			KMLExporter.makeKMLWithNetworkLink(fileName, tableName, urlWebKml);
	    			consoleNotification.setEndSyncImageStatus();
	    		} catch(Throwable t){
	    			consoleNotification.setErrorImageStatus();
	    			consoleNotification.logError(t, EpiInfoUITranslator.getLabelKMLFailed());
	    		}
	    		
    		}
    		
    		if(action == DOWNLOAD_SCHEMA){
				String url = textFieldURL.getText();
				if(!HttpSyncAdapterFactory.isValidURL(url)){
					consoleNotification.setErrorImageStatus();
	    			consoleNotification.log(EpiInfoUITranslator.getErrorInvalidURL());
					return null;
				}
				
				String dataSource = textFieldDataSource.getText();
				String tableName = (String)comboTables.getSelectedItem();
				if(!MsAccessSyncAdapterFactory.isValidAccessTable(dataSource, tableName)){
					consoleNotification.setErrorImageStatus();
	    			consoleNotification.log(EpiInfoUITranslator.getErrorInvalidMSAccessTable());
					return null;
				}
				
    			try{
	    			SyncEngineUtil.downloadSchema(url, tableName, baseDirectory);
	    			consoleNotification.setEndSyncImageStatus();
	    		} catch(Throwable t){
	    			consoleNotification.setErrorImageStatus();
	    			consoleNotification.logError(t, EpiInfoUITranslator.getLabelDownloadSchemaFailed());
	    		}
    		}
    		
    		if(action == DOWNLOAD_MAPPINGS){
				String url = textFieldURL.getText();
				if(!HttpSyncAdapterFactory.isValidURL(url)){
					consoleNotification.setErrorImageStatus();
	    			consoleNotification.log(EpiInfoUITranslator.getErrorInvalidURL());
					return null;
				}
				
				String dataSource = textFieldDataSource.getText();
				String tableName = (String)comboTables.getSelectedItem();
				if(!MsAccessSyncAdapterFactory.isValidAccessTable(dataSource, tableName)){
					consoleNotification.setErrorImageStatus();
	    			consoleNotification.log(EpiInfoUITranslator.getErrorInvalidMSAccessTable());
					return null;
				}
				
    			try{
	    			SyncEngineUtil.downloadMappings(url, tableName, baseDirectory);
	    			consoleNotification.setEndSyncImageStatus();
	    		} catch(Throwable t){
	    			consoleNotification.setErrorImageStatus();
	    			consoleNotification.logError(t, EpiInfoUITranslator.getLabelDownloadMappingsFailed());
	    		}
    		}
	        return null;
	    }

		private String getModemPhoneNumber() {
			return (modem == null ? EpiInfoUITranslator.getLabelDemo() : modem.toString());
		}

		@Override
	    public void done() {
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	        enableAllButtons();
	    }
		
	}

	 private void enableAllButtons(){
		 if(!syncInProcess){
			 buttonCancel.setEnabled(true);
			 buttonSynchronize.setEnabled(true);
			 buttonClean.setEnabled(true);
			 
			 radioEndpointHTTP.setEnabled(true);
			 radioEndpointSMS.setEnabled(true);
			 
			 if(channel == SYNCHRONIZE_SMS){
				 labelSMSDevice.setEnabled(true);
				 labelPhoneNumber.setEnabled(true);
				 comboSMSDevice.setEnabled(true);
				 buttonModemDiscovery.setEnabled(true);
				 textFieldPhoneNumber.setEnabled(true);
			 }
			 
			 textFieldDataSource.setEnabled(true);
			 comboTables.setEnabled(true);
			 buttonAddDataSource.setEnabled(true);
			 buttonOpenFileDataSource.setEnabled(true);
	
			 if(channel == SYNCHRONIZE_HTTP){
				 labelUrl.setEnabled(true);
				 textFieldURL.setEnabled(true);
			 }
			 
			 buttonSaveDefaults.setEnabled(true);
			 
			 buttonKmlGenerator.setEnabled(true);
			 buttonKmlWebGenerator.setEnabled(true);
			 buttonDownloadSchema.setEnabled(true);
		 }
	 }
	 
	 private void disableAllButtons(){
		 buttonSynchronize.setEnabled(false);
		 buttonClean.setEnabled(false);
		 buttonCancel.setEnabled(false);

		 radioEndpointHTTP.setEnabled(false);
		 radioEndpointSMS.setEnabled(false);
		 
		 labelSMSDevice.setEnabled(false);
		 labelPhoneNumber.setEnabled(false);
		 textFieldPhoneNumber.setEnabled(false);
		 comboSMSDevice.setEnabled(false);
		 
		 textFieldDataSource.setEnabled(false);
		 comboTables.setEnabled(false);
		 buttonAddDataSource.setEnabled(false);
		 buttonOpenFileDataSource.setEnabled(false);

		 labelUrl.setEnabled(false);
		 textFieldURL.setEnabled(false);
		 
		 //buttonModemDiscovery.setEnabled(false);
		 
		 buttonSaveDefaults.setEnabled(false);
		 
		 buttonKmlGenerator.setEnabled(false);
		 buttonKmlWebGenerator.setEnabled(false);
		 buttonDownloadSchema.setEnabled(false);
	 }
	
	private String openFileDialog(String fileName){
		String fileNameSelected = openFileDialog(fileName, new FileNameExtensionFilter(EpiInfoUITranslator.getLabelDataSourceFileExtensions(), "mdb"));
		return fileNameSelected;
	}
	
	private String openFileDialog(String fileName, FileNameExtensionFilter filter){
		JFileChooser chooser = new JFileChooser();
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(filter);
		
		if(fileName != null && fileName.trim().length() > 0){
			File file = new File(fileName);
			chooser.setSelectedFile(file);
		}
		
		int returnVal = chooser.showOpenDialog(this.frame);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile().getAbsolutePath();
		} else{
			return null;
		}
	}
	
	private void setModeNoDiscoveryModems() {
		discoveryModems = false;
		consoleNotification.startDiscovery();
		
	}

	private void setModeDiscoveryModems() {
		discoveryModems = true;
		consoleNotification.startDiscovery();		
	}
	
	private boolean isDicoveringModems() {
		return discoveryModems;
	}

	public void setEndSync() {
		this.syncInProcess = false;
		this.enableAllButtons();		
	}
}
