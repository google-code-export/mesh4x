package org.mesh4j.phone.ui;

import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.phone.ui.properties.PropertiesProvider;
import org.mesh4j.phone.ui.translator.PhoneUITranslator;
import org.mesh4j.sync.message.channel.sms.connection.smslib.Modem;
import org.mesh4j.sync.message.channel.sms.connection.smslib.ModemHelper;
import org.mesh4j.sync.message.channel.sms.connection.smslib.SmsLibAsynchronousConnection;
import org.mesh4j.sync.message.core.NonMessageEncoding;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.swtdesigner.SwingResourceManager;

public class PhoneUI{

	private final static Log Logger = LogFactory.getLog(PhoneUI.class);

	// CONSTANTS
	private static final int DISCOVERY_MODEMS = 0;
	private static final int SEND_MESSAGE = 1;
	private static final int READ_MESSAGES = 2;
	private static final int CHANGE_DEVICE = 3;
	private static final int SAVE_DEFAULTS = 4;
	
	// MODEL VARIABLES
	private JFrame frame;
	private JComboBox comboBoxDevice;
	private JButton buttonAutoDetectModem;
	private JButton buttonReadMessages;
	private JTextField textFieldPhoneNumber;
	private JTextArea textAreaMessage;
	private JButton buttonSendMessage;
	private JButton buttonCleanConsole;
	private JTextArea textAreaConsoleView;
	
	private ConsoleProgressMonitor consoleProgressMonitor;
	private SmsLibAsynchronousConnection smsConnection;
	private boolean discoveryModems = false;
	
	private String portName;
	int baudRate;
	private String defaultPhoneNumber;
	
	// BUSINESS METHODS
	
	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PhoneUI window = new PhoneUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					Logger.error(e.getMessage(), e);
				}
			}
		});
	}

	public PhoneUI() throws Exception {
		this.initializeProperties();
		this.createUI();
		this.consoleProgressMonitor = new ConsoleProgressMonitor(this.textAreaConsoleView);
		
		if(comboBoxDevice.getSelectedItem() != null){
			startUpSmsConnection((Modem)comboBoxDevice.getSelectedItem());
		}
	}

	private void createUI() {
		frame = new JFrame();
				
		WindowAdapter windowAdapter = new WindowAdapter() {
			public void windowClosed(final WindowEvent e) {
				shutdown();
			}
		};
		
		frame.addWindowListener(windowAdapter);
		frame.setIconImage(SwingResourceManager.getImage(PhoneUI.class, "/phone.jpg"));
		frame.getContentPane().setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("336dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("20dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("90dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("123dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC}));
		frame.setResizable(false);
		frame.setTitle(PhoneUITranslator.getLabelTitle());
		frame.setBounds(100, 100, 692, 466);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final JScrollPane scrollPaneConsoleView = new JScrollPane();
		frame.getContentPane().add(scrollPaneConsoleView, new CellConstraints(2, 6, CellConstraints.FILL, CellConstraints.FILL));

		textAreaConsoleView = new JTextArea();
		textAreaConsoleView.setToolTipText(PhoneUITranslator.getToolTipConsoleView());
		scrollPaneConsoleView.setViewportView(textAreaConsoleView);

		final JPanel panelDevice = new JPanel();
		panelDevice.setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("26dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("187dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("45dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("66dlu")},
			new RowSpec[] {
				FormFactory.DEFAULT_ROWSPEC}));
		frame.getContentPane().add(panelDevice, new CellConstraints(2, 2, CellConstraints.FILL, CellConstraints.FILL));

		final JLabel labelDevice = new JLabel();
		labelDevice.setText(PhoneUITranslator.getLabelDevice());
		panelDevice.add(labelDevice, new CellConstraints());

		ActionListener deviceActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Task task = new Task(CHANGE_DEVICE);
				task.execute();
			}
		};
		
		comboBoxDevice = new JComboBox();
		comboBoxDevice.setToolTipText(PhoneUITranslator.getToolTipDevice());
		comboBoxDevice.addActionListener(deviceActionListener);
		
		Modem modem = getDefaultModem();
		if(modem != null){
			comboBoxDevice.setModel(new DefaultComboBoxModel(new Modem[]{modem}));
		}
		panelDevice.add(comboBoxDevice, new CellConstraints(3, 1));

		ActionListener modemDiscoveryActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(isDicoveringModems()){
					consoleProgressMonitor.stopDiscovery();
				} else {
					Task task = new Task(DISCOVERY_MODEMS);
					task.execute();
				}
			}
		};
		
		buttonAutoDetectModem = new JButton();
		buttonAutoDetectModem.setToolTipText(PhoneUITranslator.getToolTipAutoDetect());
		buttonAutoDetectModem.setText(PhoneUITranslator.getLabelModemDiscovery());
		buttonAutoDetectModem.addActionListener(modemDiscoveryActionListener);
		
		panelDevice.add(buttonAutoDetectModem, new CellConstraints(5, 1));

		ActionListener readActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Task task = new Task(READ_MESSAGES);
				task.execute();
			}
		};
		
		buttonReadMessages = new JButton();
		buttonReadMessages.setToolTipText(PhoneUITranslator.getToolTipReadMessages());
		buttonReadMessages.setText(PhoneUITranslator.getLabelReadMessages());
		buttonReadMessages.addActionListener(readActionListener);
		panelDevice.add(buttonReadMessages, new CellConstraints(7, 1));

		final JPanel panelSendMessage = new JPanel();
		panelSendMessage.setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("237dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("64dlu")},
			new RowSpec[] {
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("55dlu"),
				FormFactory.RELATED_GAP_ROWSPEC}));
		frame.getContentPane().add(panelSendMessage, new CellConstraints(2, 4, CellConstraints.LEFT, CellConstraints.FILL));

		final JLabel labelPhoneNumber = new JLabel();
		labelPhoneNumber.setText(PhoneUITranslator.getLabelPhoneNumber());
		panelSendMessage.add(labelPhoneNumber, new CellConstraints());

		final JLabel labelMessage = new JLabel();
		labelMessage.setText(PhoneUITranslator.getLabelMessage());
		panelSendMessage.add(labelMessage, new CellConstraints(1, 3));

		textFieldPhoneNumber = new JTextField();
		textFieldPhoneNumber.setToolTipText(PhoneUITranslator.getToolTipPhoneNumber());
		textFieldPhoneNumber.setText(this.defaultPhoneNumber);
		panelSendMessage.add(textFieldPhoneNumber, new CellConstraints(3, 1));

		final JScrollPane scrollPaneMessage = new JScrollPane();
		panelSendMessage.add(scrollPaneMessage, new CellConstraints(3, 3, 3, 3, CellConstraints.FILL, CellConstraints.FILL));

		textAreaMessage = new JTextArea();
		textAreaMessage.setToolTipText(PhoneUITranslator.getToolTipMessage());
		scrollPaneMessage.setViewportView(textAreaMessage);

		ActionListener sendActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Task task = new Task(SEND_MESSAGE);
				task.execute();
			}
		};
		
		buttonSendMessage = new JButton();
		buttonSendMessage.setToolTipText(PhoneUITranslator.getToolTipSendMessage());
		buttonSendMessage.setText(PhoneUITranslator.getLabelSendMessage());
		buttonSendMessage.addActionListener(sendActionListener);
		panelSendMessage.add(buttonSendMessage, new CellConstraints(5, 1));

		ActionListener cleanActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				textAreaConsoleView.setText("");
			}
		};
		
		final JPanel panel = new JPanel();
		panel.setLayout(new FormLayout(new ColumnSpec[] {FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC}, new RowSpec[] {FormFactory.DEFAULT_ROWSPEC}));
		frame.getContentPane().add(panel, new CellConstraints(2, 8));

		ActionListener saveDefaultsActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Task task = new Task(SAVE_DEFAULTS);
				task.execute();
			}
		};
		
		final JButton buttonSaveDefaults = new JButton();
		buttonSaveDefaults.setText(PhoneUITranslator.getLabelSaveDefaults());
		buttonSaveDefaults.setToolTipText(PhoneUITranslator.getToolTipSaveDefaults());
		buttonSaveDefaults.addActionListener(saveDefaultsActionListener);
		panel.add(buttonSaveDefaults, new CellConstraints(3, 1));
		
		buttonCleanConsole = new JButton();
		panel.add(buttonCleanConsole, new CellConstraints());
		buttonCleanConsole.setToolTipText(PhoneUITranslator.getToolTipCleanConsole());
		buttonCleanConsole.setText(PhoneUITranslator.getLabelCleanConsole());
		buttonCleanConsole.addActionListener(cleanActionListener);
		
		disableAllButtons();
		enableAllButtons();
	}

	private void shutdown(){
		try{
			shutdownSmsConnection();
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
			
    		if(action == DISCOVERY_MODEMS){
    			consoleProgressMonitor.log(PhoneUITranslator.getMessageBeginModemDiscovery());
    			setModeDiscoveryModems();
    			buttonAutoDetectModem.setText(PhoneUITranslator.getLabelStopModemDiscovery());
    			buttonAutoDetectModem.setToolTipText(PhoneUITranslator.getToolTipStopAutoDetect());    			
    			
    			Modem[] modems = PhoneUtils.getAvailableModems(consoleProgressMonitor);
    			comboBoxDevice.setModel(new DefaultComboBoxModel(modems));
    			    			
    			setModeNoDiscoveryModems();
    			buttonAutoDetectModem.setText(PhoneUITranslator.getLabelModemDiscovery());
    			buttonAutoDetectModem.setToolTipText(PhoneUITranslator.getToolTipAutoDetect());
    			
    			consoleProgressMonitor.log(PhoneUITranslator.getMessageEndModemDiscovery(modems.length));
    		}
    		
    		if(action == SEND_MESSAGE){
    			consoleProgressMonitor.log(PhoneUITranslator.getMessageSendWasAddedToOutboxQueue(textFieldPhoneNumber.getText(), textAreaMessage.getText()));
    			smsConnection.send(textFieldPhoneNumber.getText(), textAreaMessage.getText(), false);
    		}
    		
    		if(action == READ_MESSAGES){
    			consoleProgressMonitor.log(PhoneUITranslator.getMessageBeginReadMessages());
    			smsConnection.readAll();
    			consoleProgressMonitor.log(PhoneUITranslator.getMessageEndReadMessages());
    		}
    		
    		if(action == CHANGE_DEVICE){
    			shutdownSmsConnection();
    			Modem modem = (Modem)comboBoxDevice.getSelectedItem();				
				startUpSmsConnection(modem);
    		}
    		
    		if(action == SAVE_DEFAULTS){
    			Modem modem = (Modem)comboBoxDevice.getSelectedItem();
				String phoneNum = textFieldPhoneNumber.getText();
				
				PropertiesProvider propertiesProvider = new PropertiesProvider();
				propertiesProvider.setDefaults(modem, phoneNum);
				propertiesProvider.store();
    		}
    		
    		return null;
	    }

		@Override
	    public void done() {
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	        enableAllButtons();
	    }
	}

	 private void enableAllButtons(){
		if(comboBoxDevice.getSelectedItem() != null){
			buttonReadMessages.setEnabled(true);
			textFieldPhoneNumber.setEnabled(true);
			textAreaMessage.setEnabled(true);
			buttonSendMessage.setEnabled(true);
		}
	 }
		 
	 private void disableAllButtons(){
		buttonReadMessages.setEnabled(false);
		textFieldPhoneNumber.setEnabled(false);
		textAreaMessage.setEnabled(false);
		buttonSendMessage.setEnabled(false);
	 }
	
	private void startUpSmsConnection(Modem modem) {
		smsConnection = new SmsLibAsynchronousConnection("mesh4x", modem.getComPort(), modem.getBaudRate(), 
				modem.getManufacturer(), modem.getModel(), 160, NonMessageEncoding.INSTANCE, consoleProgressMonitor, null);
		smsConnection.startService();
	}

	private void shutdownSmsConnection() {
		if(smsConnection != null){
			smsConnection.shutdown();
		}
	}	
	

	private boolean isDicoveringModems() {
		return discoveryModems;
	}
	
	private void setModeNoDiscoveryModems() {
		discoveryModems = false;
		consoleProgressMonitor.startDiscovery();
		
	}

	private void setModeDiscoveryModems() {
		discoveryModems = true;
		consoleProgressMonitor.startDiscovery();		
	}
	
	private Modem getDefaultModem() {
		if(portName.length() > 0 && baudRate > 0){
			return ModemHelper.getModem(portName, baudRate);
		} else {
			return null;
		}
	}

	private void initializeProperties() throws Exception {
		PropertiesProvider propertiesProvider = new PropertiesProvider();
		this.portName = propertiesProvider.getDefaultPort();
		this.baudRate = propertiesProvider.getDefaultBaudRate();
		this.defaultPhoneNumber = propertiesProvider.getDefaultPhoneNumber();
	}
}
