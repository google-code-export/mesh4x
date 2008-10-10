package org.mesh4j.sync.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.mesh4j.sync.adapters.sms.SmsHelper;
import org.mesh4j.sync.message.IEndpoint;
import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncAware;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.MessageSyncEngine;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessage;
import org.mesh4j.sync.message.channel.sms.connection.ISmsConnectionInboundOutboundNotification;
import org.mesh4j.sync.message.channel.sms.connection.smslib.Modem;
import org.mesh4j.sync.message.channel.sms.core.SmsChannel;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.properties.PropertiesProvider;
import org.mesh4j.sync.ui.translator.Mesh4jSmsUITranslator;
import org.mesh4j.sync.ui.translator.Mesh4jUITranslator;


public class Mesh4jSmsUI implements ISmsConnectionInboundOutboundNotification, IMessageSyncAware{

	private final static Log Logger = LogFactory.getLog(Mesh4jUI.class);

	// MODEL VARIABLES
	private Display display;
	private Shell shell;
	private Text textKmlFile;
	private Text consoleView;
	private Combo comboPhone;
	private Text textSmsNumber;
	private Text textSmsNumber2;
	private Button buttonCompress;	
	private Text textMessageToSend;
	
	private String defaultKmlFile;
	private String defaultPhoneNumberDestination;
	private String baseDirectory;

	private MessageSyncEngine syncEngine;
	
	// BUSINESS METHODS
	public static void main (String [] args) {
		Mesh4jSmsUI meshUI = new Mesh4jSmsUI();
		meshUI.initializeDefaults();
		
		Modem modem = SmsHelper.getDefaultModem();
		meshUI.syncEngine = SmsHelper.createSyncEngine(meshUI, meshUI, modem);
		meshUI.openMesh(modem);
		
	}

	private void openMesh(Modem modem) {
		this.display = Display.getDefault();
		this.shell = new Shell(display);
		
		this.shell.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
		       shutdown();
		    }
		});
		
		final GridLayout gridLayout = new GridLayout();
		shell.setLayout(gridLayout);
		shell.setSize(815, 659);
		shell.setText(Mesh4jSmsUITranslator.getTitle());

		shell.open();

		final Group synchronizeGroup = new Group(shell, SWT.NONE);
		synchronizeGroup.setText(Mesh4jSmsUITranslator.getLabelSynGroup());
		final GridData gd_synchronizeGroup = new GridData(775, SWT.DEFAULT);
		synchronizeGroup.setLayoutData(gd_synchronizeGroup);
		final GridLayout gridLayout_3 = new GridLayout();
		gridLayout_3.numColumns = 3;
		synchronizeGroup.setLayout(gridLayout_3);

		final Label labelPhone = new Label(synchronizeGroup, SWT.NONE);
		labelPhone.setLayoutData(new GridData());
		labelPhone.setText(Mesh4jSmsUITranslator.getLabelPhone());

		comboPhone = new Combo (synchronizeGroup, SWT.READ_ONLY);
		final GridData gd_comboPhone = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		gd_comboPhone.widthHint = 675;
		comboPhone.setLayoutData(gd_comboPhone);
		comboPhone.setItems(SmsHelper.getAvailableModems(modem));
		comboPhone.select(0);
		comboPhone.setEnabled(false);
		new Label(synchronizeGroup, SWT.NONE);

		Label labelKmlFile = new Label (synchronizeGroup, SWT.NONE);
		labelKmlFile.setLayoutData(new GridData());
		labelKmlFile.setText(Mesh4jSmsUITranslator.getLabelKMLFile());

		textKmlFile = new Text (synchronizeGroup, SWT.BORDER);
		final GridData gd_textKmlFile = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		gd_textKmlFile.widthHint = 693;
		textKmlFile.setLayoutData(gd_textKmlFile);
		textKmlFile.setText(this.defaultKmlFile);

		final Button buttonKmlFile = new Button(synchronizeGroup, SWT.PUSH);
		buttonKmlFile.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectedFileName = openFileDialogKML(textKmlFile.getText());
				if(selectedFileName != null){
					textKmlFile.setText(selectedFileName);
				}
			}
		});
		buttonKmlFile.setText("...");

		final Label labelPhone1 = new Label (synchronizeGroup, SWT.NONE);
		labelPhone1.setLayoutData(new GridData());
		labelPhone1.setText(Mesh4jSmsUITranslator.getLabelPhoneDestination());

		textSmsNumber = new Text (synchronizeGroup, SWT.BORDER);
		final GridData gd_text = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		gd_text.widthHint = 691;
		textSmsNumber.setLayoutData(gd_text);
		textSmsNumber.setText(this.defaultPhoneNumberDestination);
		
		new Label(synchronizeGroup, SWT.NONE);

		final Button buttonSynchronize = new Button(synchronizeGroup, SWT.PUSH);
		buttonSynchronize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true));
		buttonSynchronize.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				boolean ok = validateInputs();
					if(ok){
						consoleView.setText("");
						executeInNewThread(SmsExecutionMode.SYNCHRONIZE);
					}
				}
			}
		);		
		buttonSynchronize.setText(Mesh4jSmsUITranslator.getLabelSynchronize());
		buttonSynchronize.setEnabled(modem != null);
		
		final Button buttonCancel = new Button(synchronizeGroup, SWT.PUSH);
		buttonCancel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true));
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				executeInNewThread(SmsExecutionMode.CANCEL_SYNC);
			}
		});		
		buttonCancel.setText(Mesh4jSmsUITranslator.getLabelCancelSyn());
		buttonCancel.setEnabled(modem != null);
		
		new Label(synchronizeGroup, SWT.NONE);

		final Group simulationGroup = new Group(shell, SWT.NONE);
		simulationGroup.setText(Mesh4jSmsUITranslator.getLabelSimulationGroup());
		final GridData gd_simulationGroup = new GridData(772, SWT.DEFAULT);
		simulationGroup.setLayoutData(gd_simulationGroup);
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 2;
		simulationGroup.setLayout(gridLayout_1);

		final Button buttonEmulate = new Button(simulationGroup, SWT.PUSH);
		buttonEmulate.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				boolean ok = validateInputs();
					if(ok){
						consoleView.setText("");
						executeInNewThread(SmsExecutionMode.EMULATE);
					}
				}
			}
		);		
		buttonEmulate.setText(Mesh4jSmsUITranslator.getLabelSimulate());

		buttonCompress = new Button(simulationGroup, SWT.CHECK);
		buttonCompress.setText(Mesh4jSmsUITranslator.getLabelCompressMessage());
		buttonCompress.setSelection(true);
		

		final Group sendSmsGroup = new Group(shell, SWT.NONE);
		sendSmsGroup.setText(Mesh4jSmsUITranslator.getLabelSMSGroup());
		final GridData gd_sendSmsGroup = new GridData(775, SWT.DEFAULT);
		sendSmsGroup.setLayoutData(gd_sendSmsGroup);
		final GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.numColumns = 2;
		sendSmsGroup.setLayout(gridLayout_2);

		final Label labelPhone2 = new Label(sendSmsGroup, SWT.NONE);
		labelPhone2.setText(Mesh4jSmsUITranslator.getLabelPhoneDestination());

		textSmsNumber2 = new Text(sendSmsGroup, SWT.BORDER);
		final GridData gd_text_3 = new GridData(SWT.RIGHT, SWT.CENTER, true, false);
		gd_text_3.widthHint = 751;
		textSmsNumber2.setLayoutData(gd_text_3);
		textSmsNumber2.setText(this.defaultPhoneNumberDestination);
		
		Label labelMessageToSend = new Label (sendSmsGroup, SWT.NONE);
		labelMessageToSend.setText(Mesh4jSmsUITranslator.getLabelMessageToSend());

		textMessageToSend = new Text (sendSmsGroup, SWT.BORDER);
		final GridData gd_text_1 = new GridData(SWT.RIGHT, SWT.CENTER, true, false);
		gd_text_1.widthHint = 728;
		textMessageToSend.setLayoutData(gd_text_1);
		textMessageToSend.setText(Mesh4jSmsUITranslator.getMessageEnterTextMessage());
		
		final Button buttonSendMessage = new Button(sendSmsGroup, SWT.PUSH);
		buttonSendMessage.setLayoutData(new GridData());
		buttonSendMessage.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				boolean ok = validateSendMessageParameters();
					if(ok){
						executeInNewThread(SmsExecutionMode.SEND);
					}
				}
			}
		);		
		buttonSendMessage.setText(Mesh4jSmsUITranslator.getLabelSendMessage());
		buttonSendMessage.setEnabled(modem != null);

		final Button button = new Button(sendSmsGroup, SWT.NONE);
		button.setLayoutData(new GridData());
		button.setText(Mesh4jSmsUITranslator.getLabelForceReceiveMessages());
		button.setEnabled(false);
		
		final Button buttonClean = new Button(shell, SWT.PUSH);
		buttonClean.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				consoleView.setText("");
			}
		});
		buttonClean.setText(Mesh4jUITranslator.getLabelClean());

		consoleView = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		final GridData gd_text_2 = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_text_2.heightHint = 265;
		consoleView.setLayoutData(gd_text_2);
		consoleView.setText("");
		
		shell.pack ();		
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private String openFileDialogKML(String fileName){
		String fileNameSelected = openFileDialog(fileName, new String [] {"Kml"}, new String [] {"*.kml"});
		return fileNameSelected;
	}
	
	private String openFileDialog(String fileName, String[] filterNames, String[] filterExtensions){
		String path = this.baseDirectory;
		String name = "";
	
		if(fileName != null && fileName.trim().length() > 0){
			if(fileName.toUpperCase().endsWith("KML")){
				File file = new File(fileName);
				path = file.getPath();
				name = file.getName();
			}
		}
		
		FileDialog dialog = new FileDialog (shell, SWT.OPEN);
		dialog.setFilterNames (filterNames);
		dialog.setFilterExtensions (filterExtensions);
		dialog.setFilterPath (path);
		dialog.setFileName (name);
		String fileNameSelected = dialog.open();
		return fileNameSelected;
	}
	
	private boolean validateInputs() {
		boolean okKML = validateFile(this.textKmlFile.getText());
		boolean okSMS = validateSmsNumber(this.textSmsNumber.getText());
		return okKML && okSMS;
	}
	
	private boolean validateSendMessageParameters() {
		boolean okKML = validateMessageToSend(this.textMessageToSend.getText());
		boolean okSMS = validateSmsNumber(this.textSmsNumber2.getText());
		return okKML && okSMS;
	}
	
	
	private boolean validateMessageToSend(String text) {
		if(text == null || text.length() == 0){
			consoleView.append("\n"+Mesh4jSmsUITranslator.getMessageErrorSMSTextToSendEmptyOrNull());
			return false;
		}
		return true;
	}

	private boolean validateFile(String fileName){
		if(!(fileName != null && fileName.trim().length() > 5 
				&& (fileName.toUpperCase().endsWith(".KML")))){
			consoleView.append("\n"+Mesh4jSmsUITranslator.getMessageErrorKMLFile());
			return false;
		}
		return true;
	}
	
	private boolean validateSmsNumber(String smsNumber){
		if(smsNumber == null || smsNumber.trim().length() == 0){
			consoleView.append("\n"+Mesh4jSmsUITranslator.getMessageErrorPhoneNumberDestination());
			return false;
		}
		return true;
	}
	
	private void executeInNewThread(final SmsExecutionMode executionMode){
		final String smsFrom = this.comboPhone.getText();
		final String smsTo = this.textSmsNumber.getText();
		final String smsTo2 = this.textSmsNumber2.getText();
		final String text = this.textMessageToSend.getText();
		final boolean useCompression = this.buttonCompress.getSelection();
		final String kmlFileName = this.textKmlFile.getText();
		
		Runnable longJob = new Runnable() {
			boolean done = false;
			public void run() {
				Thread thread = new Thread(new Runnable() {
					public void run() {
												
						final String syncResult = executionMode.isEmulate() 
							? emulateSync(smsFrom, smsTo, useCompression, kmlFileName)
							: (executionMode.isSynchronize() ? synchronizeItems(kmlFileName, smsTo)
							: (executionMode.isCancelSync() ? cancelSync(kmlFileName, smsTo) : sendMessage(text, smsTo2)));
						
						if (display.isDisposed()) return;
						
						log(syncResult);

						done = true;
						display.wake();
					}

				});
				thread.start();
				while (!done && !shell.isDisposed()) {
					if (!display.readAndDispatch())
						display.sleep();
				}
			}
		};
		BusyIndicator.showWhile(display, longJob);
	}
	
	private String emulateSync(String smsFrom, String smsTo, boolean useCompression, String kmlFileName){
		try{
			SmsHelper.emulateSync(this, this, smsFrom, smsTo, useCompression, kmlFileName);
			return "";
		} catch (RuntimeException e) {
			Logger.error(e.getMessage(), e);
			return Mesh4jSmsUITranslator.getLabelFailed();
		} catch (InterruptedException e) {
			Logger.error(e.getMessage(), e);
			return Mesh4jSmsUITranslator.getLabelFailed();
		}
	}
	
	private String synchronizeItems(String kmlFileName, String smsTo){
		try{				
			SmsHelper.synchronizeKml(this.syncEngine, kmlFileName, smsTo);
			return "";
		} catch (RuntimeException e) {
			Logger.error(e.getMessage(), e);
			return Mesh4jSmsUITranslator.getLabelFailed();
		}
	}
	
	private String cancelSync(String kmlFileName, String smsTo) {
		try{				
			SmsHelper.cancelSync(this.syncEngine, kmlFileName, smsTo);
			return "";
		} catch (RuntimeException e) {
			Logger.error(e.getMessage(), e);
			return Mesh4jSmsUITranslator.getLabelFailed();
		}
	}
	
	private String sendMessage(String text, String smsTo){
		try{				
			List<SmsMessage> messages = new ArrayList<SmsMessage>();
			messages.add(new SmsMessage(text));
			
			((SmsChannel)this.syncEngine.getChannel()).send(messages, new SmsEndpoint(smsTo));
			return "";
		} catch (RuntimeException e) {
			Logger.error(e.getMessage(), e);
			return Mesh4jSmsUITranslator.getLabelFailed();
		}
	}

	public void log(String message) {
		final String msg = message;
		display.syncExec(new Runnable() {
			public void run() {
				if (consoleView.isDisposed()) return;
				consoleView.append("\n"+msg);
			}
		});		
	}
	
	private void initializeDefaults(){
		PropertiesProvider prop = new PropertiesProvider("mesh4j_sms.properties");
		this.defaultKmlFile = prop.getDefaultEnpoint1();					
		this.baseDirectory = prop.getBaseDirectory();
		this.defaultPhoneNumberDestination = prop.getString("default.sms.phone.number.destination");
		
		File dirDemo = new File(baseDirectory+"\\demo\\");
		if(!dirDemo.exists()){
			dirDemo.mkdirs();
		}
	}

	@Override
	public void notifyReceiveMessage(String endpointId, String message,
			Date date) {
		this.log("\t"+Mesh4jSmsUITranslator.getMessageNotifyReceiveMessageError(endpointId, message));
	}

	@Override
	public void notifyReceiveMessageError(String endpointId, String message, Date date) {
		this.log("\t"+Mesh4jSmsUITranslator.getMessageNotifyReceiveMessage(endpointId, message));		
	}

	@Override
	public void notifySendMessage(String endpointId, String message) {
		this.log("\t"+Mesh4jSmsUITranslator.getMessageNotifySendMessage(endpointId, message));
	}

	@Override
	public void notifySendMessageError(String endpointId, String message) {
		this.log("\t"+Mesh4jSmsUITranslator.getMessageNotifySendMessageError(endpointId, message));		
	}

	@Override
	public void beginSync(ISyncSession syncSession) {
		log(Mesh4jSmsUITranslator.getLabelStart());		
	}

	@Override
	public void endSync(ISyncSession syncSession, List<Item> conflicts) {
		log(Mesh4jSmsUITranslator.getLabelSuccess());		
	}

	@Override
	public void beginSyncWithError(ISyncSession syncSession) {
		log(Mesh4jSmsUITranslator.getMessageErrorBeginSync(syncSession.getTarget().getEndpointId(), syncSession.getSourceId()));		
	}

	@Override
	public void notifyCancelSync(ISyncSession syncSession) {
		log(Mesh4jSmsUITranslator.getMessageCancelSync(syncSession.getSessionId(), syncSession.getTarget().getEndpointId(), syncSession.getSourceId()));		
	}

	@Override
	public void notifyCancelSyncErrorSyncSessionNotOpen(String sourceId, IEndpoint endpoint) {
		log(Mesh4jSmsUITranslator.getMessageCancelSyncErrorSessionNotOpen(endpoint, sourceId));		
	}

	@Override
	public void notifyInvalidMessageProtocol(IMessage message) {
		log(Mesh4jSmsUITranslator.getMessageInvalidMessageProtocol(message));
	}

	@Override
	public void notifyInvalidProtocolMessageOrder(IMessage message) {
		log(Mesh4jSmsUITranslator.getMessageErrorInvalidProtocolMessageOrder(message));
	}

	@Override
	public void notifyMessageProcessed(IMessage message, List<IMessage> response) {
		log(Mesh4jSmsUITranslator.getMessageProcessed(message, response));
	}

	@Override
	public void notifySessionCreationError(IMessage message, String sourceId) {
		log(Mesh4jSmsUITranslator.getMessageErrorSessionCreation(message, sourceId));
	}

	@Override
	public void notifyReceiveMessageWasNotProcessed(String endpoint, String message, Date date) {
		Logger.info("SMS - Received message was not processed, endpoint: " + endpoint + " message: " + message + " date: " + date.toString());
	}
	
	private void shutdown(){
		try{
			this.syncEngine.getChannel().shutdown();
		} catch(Throwable e){
			Logger.error(e.getMessage(), e);
		}
	}
}
