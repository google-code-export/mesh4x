package org.mesh4j.sync.ui;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.dom.DOMAdapter;
import org.mesh4j.sync.adapters.dom.IDOMLoader;
import org.mesh4j.sync.adapters.feed.ContentReader;
import org.mesh4j.sync.adapters.feed.ContentWriter;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.http.HttpSyncAdapter;
import org.mesh4j.sync.adapters.kml.KMLDOMLoaderFactory;
import org.mesh4j.sync.adapters.sms.SmsHelper;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.id.generator.IdGenerator;
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
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.ui.translator.Mesh4jSmsUITranslator;
import org.mesh4j.sync.ui.translator.Mesh4jUITranslator;
import org.mesh4j.sync.validations.MeshException;


public class Mesh4jSmsUI implements ISmsConnectionInboundOutboundNotification, IMessageSyncAware{

	private final static Log Logger = LogFactory.getLog(Mesh4jUI.class);

	// MODEL VARIABLES
	private Display display;
	private Shell shell;
	private Text consoleView;

// SMS UI	
	private Text textKmlFile;
	private Combo comboPhone;
	private Text textSmsNumber;
	private Text textSmsNumber2;
	private Button buttonCompress;	
	private Text textMessageToSend;

// KML UI
	private Text endpoint1;
	private Text endpoint2;
	private Text kmlFileToExternalActions;
	private String defaultEndpoint1;
	private String defaultEndpoint2;

// DEFAULTS	
	private String defaultKmlFile;
	private String defaultPhoneNumberDestination;
	private String baseDirectory;
	private IIdentityProvider identityProvider = NullIdentityProvider.INSTANCE;
	private IIdGenerator idGenerator = IdGenerator.INSTANCE;
	private MessageSyncEngine syncEngine;
	
	// BUSINESS METHODS
	public static void main (String [] args) {
		Mesh4jSmsUI meshUI = new Mesh4jSmsUI();
		try {
			meshUI.initializeDefaults();
			Modem modem = SmsHelper.getDefaultModem();
			meshUI.syncEngine = SmsHelper.createSyncEngine(meshUI, meshUI, modem);
			meshUI.openMesh(modem);
		} catch (IOException e) {
			Logger.error(e.getMessage(), e);
		}	
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
		gridLayout.numColumns = 2;
		shell.setLayout(gridLayout);
		shell.setSize(1021, 659);
		shell.setText(Mesh4jSmsUITranslator.getTitle());

		shell.open();

		final Group synchronizeGroup = new Group(shell, SWT.NONE);
		synchronizeGroup.setText(Mesh4jSmsUITranslator.getLabelSynGroup());
		final GridData gd_synchronizeGroup = new GridData(451, SWT.DEFAULT);
		synchronizeGroup.setLayoutData(gd_synchronizeGroup);
		final GridLayout gridLayout_3 = new GridLayout();
		gridLayout_3.numColumns = 3;
		synchronizeGroup.setLayout(gridLayout_3);

		final Label labelPhone = new Label(synchronizeGroup, SWT.NONE);
		labelPhone.setLayoutData(new GridData());
		labelPhone.setText(Mesh4jSmsUITranslator.getLabelPhone());

		comboPhone = new Combo (synchronizeGroup, SWT.READ_ONLY);
		final GridData gd_comboPhone = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		gd_comboPhone.widthHint = 437;
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
		gd_textKmlFile.widthHint = 462;
		textKmlFile.setLayoutData(gd_textKmlFile);
		textKmlFile.setText(this.defaultKmlFile);

		final Button buttonKmlFile = new Button(synchronizeGroup, SWT.PUSH);
		buttonKmlFile.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectedFileName = openFileDialogKMLSMS(textKmlFile.getText());
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
		gd_text.widthHint = 469;
		textSmsNumber.setLayoutData(gd_text);
		textSmsNumber.setText(this.defaultPhoneNumberDestination);
		
		new Label(synchronizeGroup, SWT.NONE);

		final Composite composite = new Composite(synchronizeGroup, SWT.NONE);
		final GridLayout gridLayout_5 = new GridLayout();
		gridLayout_5.numColumns = 5;
		composite.setLayout(gridLayout_5);
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));

		final Button buttonSynchronize = new Button(composite, SWT.PUSH);
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
		
		final Button buttonCancel = new Button(composite, SWT.PUSH);
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				executeInNewThread(SmsExecutionMode.CANCEL_SYNC);
			}
		});		
		buttonCancel.setText(Mesh4jSmsUITranslator.getLabelCancelSyn());
		buttonCancel.setEnabled(modem != null);

		final Label label = new Label(composite, SWT.NONE);
		label.setText("                       ");

		final Button buttonEmulate = new Button(composite, SWT.PUSH);
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

		buttonCompress = new Button(composite, SWT.CHECK);
		buttonCompress.setLayoutData(new GridData());
		buttonCompress.setText(Mesh4jSmsUITranslator.getLabelCompressMessage());
		buttonCompress.setSelection(true);

		final Group group_1_1 = new Group(shell, SWT.NONE);
		final GridData gd_group_1_1 = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gd_group_1_1.heightHint = 121;
		gd_group_1_1.widthHint = 491;
		group_1_1.setLayoutData(gd_group_1_1);
		final GridLayout gridLayout_4 = new GridLayout();
		gridLayout_4.numColumns = 3;
		group_1_1.setLayout(gridLayout_4);
		group_1_1.setText(Mesh4jUITranslator.getLabelGroupSync());

		final Label endpoint1Label = new Label(group_1_1, SWT.NONE);
		endpoint1Label.setText(Mesh4jUITranslator.getLabelEndpoint1());

		endpoint1 = new Text(group_1_1, SWT.BORDER);
		final GridData gd_endpoint1 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		endpoint1.setLayoutData(gd_endpoint1);
		endpoint1.setText(this.defaultEndpoint1);

		final Button buttonSource = new Button(group_1_1, SWT.NONE);
		buttonSource.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectedFileName = openFileDialog(endpoint1.getText());
				if(selectedFileName != null){
					endpoint1.setText(selectedFileName);
				}
			}
		});
		buttonSource.setText("...");

		final Label labelTarget = new Label(group_1_1, SWT.NONE);
		labelTarget.setText(Mesh4jUITranslator.getLabelEndpoint2());

		endpoint2 = new Text(group_1_1, SWT.BORDER);
		final GridData gd_endpoint2 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		endpoint2.setLayoutData(gd_endpoint2);
		endpoint2.setText(this.defaultEndpoint2);

		final Button buttonTarget = new Button(group_1_1, SWT.NONE);
		buttonTarget.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectedFileName = openFileDialog(endpoint2.getText());
				if(selectedFileName != null){
					endpoint2.setText(selectedFileName);
				}
			}
		});
		buttonTarget.setText("...");

		final Button buttonSynchronizeKML = new Button(group_1_1, SWT.NONE);
		buttonSynchronizeKML.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				boolean ok = validateEndpoints();
					if(ok){
						synchronizeItemsInNewThread();
					}
				}
			}
		);		
		buttonSynchronizeKML.setText(Mesh4jUITranslator.getLabelSyncronize());
		

		final Group sendSmsGroup = new Group(shell, SWT.NONE);
		sendSmsGroup.setText(Mesh4jSmsUITranslator.getLabelSMSGroup());
		final GridData gd_sendSmsGroup = new GridData(459, 104);
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

		final Composite composite_2 = new Composite(sendSmsGroup, SWT.NONE);
		composite_2.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 2;
		composite_2.setLayout(gridLayout_1);
		
		final Button buttonSendMessage = new Button(composite_2, SWT.PUSH);
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

		final Button button = new Button(composite_2, SWT.NONE);
		button.setText(Mesh4jSmsUITranslator.getLabelForceReceiveMessages());
		button.setEnabled(false);

		final Group group_1 = new Group(shell, SWT.NONE);
		final GridData gd_group_1 = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gd_group_1.heightHint = 89;
		gd_group_1.widthHint = 495;
		group_1.setLayoutData(gd_group_1);
		final GridLayout gridLayout_6 = new GridLayout();
		gridLayout_6.numColumns = 3;
		group_1.setLayout(gridLayout_6);
		group_1.setText(Mesh4jUITranslator.getLabelGroupMaintenance());

		final Label labelKmlFile_1_1 = new Label(group_1, SWT.NONE);
		labelKmlFile_1_1.setText(Mesh4jUITranslator.getLabelKMLFile());

		kmlFileToExternalActions = new Text(group_1, SWT.BORDER);
		final GridData gd_kmlFileToExternalActions = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd_kmlFileToExternalActions.widthHint = 550;
		kmlFileToExternalActions.setLayoutData(gd_kmlFileToExternalActions);

		final Button buttonFileDialogKml = new Button(group_1, SWT.NONE);
		buttonFileDialogKml.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectedFileName = openFileDialogKML(kmlFileToExternalActions.getText());
				if(selectedFileName != null){
					kmlFileToExternalActions.setText(selectedFileName);
				}
			}
		});
		buttonFileDialogKml.setText("...");

		final Composite composite_1 = new Composite(group_1, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		final GridLayout gridLayout_7 = new GridLayout();
		gridLayout_7.numColumns = 3;
		composite_1.setLayout(gridLayout_7);

		final Button buttonPrepareKMLToSync = new Button(composite_1, SWT.NONE);
		buttonPrepareKMLToSync.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				boolean ok = validateKMLFile(kmlFileToExternalActions.getText(), "KmlFile");
				if(ok){
					prepareKMLInNewThread();
				}
			}
		});
		buttonPrepareKMLToSync.setText(Mesh4jUITranslator.getLabelPrepareToSync());

		final Button buttonPurgeKML = new Button(composite_1, SWT.NONE);
		buttonPurgeKML.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				boolean ok = validateKMLFile(kmlFileToExternalActions.getText(), "KmlFile");
				if(ok){
					purgeKMLInNewThread();
				}
			}
		});	
		buttonPurgeKML.setText(Mesh4jUITranslator.getLabelPurge());

		final Button buttonCleanKML = new Button(composite_1, SWT.NONE);
		buttonCleanKML.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				boolean ok = validateKMLFile(kmlFileToExternalActions.getText(), "KmlFile");
				if(ok){
					cleanKMLInNewThread();
				}
			}
		});	
		buttonCleanKML.setText(Mesh4jUITranslator.getLabelClean());
		
		final Button buttonClean = new Button(shell, SWT.PUSH);
		buttonClean.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				consoleView.setText("");
			}
		});
		buttonClean.setText(Mesh4jUITranslator.getLabelClean());
		new Label(shell, SWT.NONE);

		consoleView = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		final GridData gd_text_2 = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd_text_2.widthHint = 958;
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
							: (executionMode.isSynchronize() ? synchronizeItemsSMS(kmlFileName, smsTo)
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
		}catch (IOException e) {
			Logger.error(e.getMessage(), e);
			return Mesh4jSmsUITranslator.getLabelFailed();
		} catch (RuntimeException e) {
			Logger.error(e.getMessage(), e);
			return Mesh4jSmsUITranslator.getLabelFailed();
		} catch (InterruptedException e) {
			Logger.error(e.getMessage(), e);
			return Mesh4jSmsUITranslator.getLabelFailed();
		}
	}
	
	private String synchronizeItemsSMS(String kmlFileName, String smsTo){
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
	
	private String openFileDialogKML(String fileName){
		String fileNameSelected = openFileDialog(fileName, new String [] {"Kml", "Kmz"}, new String [] {"*.kml", "*.kmz"});
		return fileNameSelected;
	}
	
	private String openFileDialog(String fileName){
		String fileNameSelected = openFileDialog(fileName, new String [] {"Kml", "Kmz", "Feed", "All Files (*.*)"}, new String [] {"*.kml", "*.kmz", "*.xml", "*.*"});
		return fileNameSelected;
	}
	
	private String openFileDialog(String fileName, String[] filterNames, String[] filterExtensions){
		String path = this.baseDirectory;
		String name = "";
	
		if(fileName != null && fileName.trim().length() > 0){
			if(isFile(fileName)){
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
	
	private String openFileDialogKMLSMS(String fileName){
		String fileNameSelected = openFileDialog(fileName, new String [] {"Kml"}, new String [] {"*.kml"});
		return fileNameSelected;
	}
	
	private void synchronizeItemsInNewThread(){
		final String endpoint1 = this.getEndpoint1();
		final String endpoint2 = this.getEndpoint2();
		
		Runnable longJob = new Runnable() {
			boolean done = false;
			public void run() {
				Thread thread = new Thread(new Runnable() {
					public void run() {
						display.syncExec(new Runnable() {
							public void run() {
								if (consoleView.isDisposed()) return;
								consoleView.append("\n"+ Mesh4jUITranslator.getMessageSyncStart());
								consoleView.append("\n\t"+ Mesh4jUITranslator.getLabelEndpoint1() + endpoint1);
								consoleView.append("\n\t"+ Mesh4jUITranslator.getLabelEndpoint2() + endpoint2);
							}
						});
						
						final String syncResult = synchronizeItems(endpoint1, endpoint2);
						
						if (display.isDisposed()) return;						
						display.syncExec(new Runnable() {
							public void run() {
								if (consoleView.isDisposed()) return;
								consoleView.append("\n"+ Mesh4jUITranslator.getMessageSyncCompleted(syncResult));
							}
						});
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
	
	private String getEndpoint1() {
		return this.endpoint1.getText();
	}
	
	private String getEndpoint2() {
		return this.endpoint2.getText();
	}

	private String synchronizeItems(String endpoint1, String endpoint2){
		try{
			ISyncAdapter sourceRepo = makeRepositoryAdapter(endpoint1);
			ISyncAdapter targetRepo = makeRepositoryAdapter(endpoint2);
			
			SyncEngine syncEngine = new SyncEngine(sourceRepo, targetRepo);
			List<Item> conflicts = syncEngine.synchronize();
			if(conflicts.isEmpty()){
				return Mesh4jUITranslator.getMessageSyncSuccessfully();
			} else {
				return Mesh4jUITranslator.getMessageSyncCompletedWithConflicts(conflicts.size());
			}
		} catch (RuntimeException e) {
			Logger.error(e.getMessage(), e);
			return Mesh4jUITranslator.getMessageSyncFailed();
		}
	}

	private ISyncAdapter makeRepositoryAdapter(String endpoint) {
		// TODO (JMT) refactoring: change for ISyncAdapterFactory
		if(isURL(endpoint)){
			return new HttpSyncAdapter(endpoint, RssSyndicationFormat.INSTANCE, this.identityProvider, this.idGenerator, ContentWriter.INSTANCE, ContentReader.INSTANCE);
		} else {
			if(isFeed(endpoint)){
				return new FeedAdapter(endpoint, this.identityProvider, this.idGenerator);
			}else{
				IDOMLoader loader = KMLDOMLoaderFactory.createDOMLoader(endpoint, this.identityProvider);
				return new DOMAdapter(loader);
			}
		}
	}

	private boolean validateEndpoints() {
		boolean okEndpoint1 = validate(getEndpoint1(), "Endpoint1");
		boolean okEndpoint2 = validate(getEndpoint2(), "Endpoint2");		
		
		if(okEndpoint1 && okEndpoint2){
			if(getEndpoint1().equals(getEndpoint2())){
				consoleView.append("\n"+ Mesh4jUITranslator.getErrorSameEndpoints());
				return false;
			}
		}
		return okEndpoint1 && okEndpoint2;

	}

	private boolean validate(String endpointValue, String endpointHeader){
		if(endpointValue ==  null || endpointValue.trim().length() == 0){
			consoleView.append("\n"+ Mesh4jUITranslator.getErrorEndpoint(endpointHeader));
			return false;
		}
		if(isURL(endpointValue)){
			return validateURL(endpointValue, endpointHeader);
		} else{
			return validateFile(endpointValue, endpointHeader);
		}
	}

	private boolean validateURL(String url, String endpointHeader){
		URL newURL;
		try {
			newURL = new URL(url);
		} catch (MalformedURLException e) {
			consoleView.append("\n"+ Mesh4jUITranslator.getErrorInvalidURL(endpointHeader));
			return false;
		}
		
		URLConnection conn = null;
		try {
			conn = newURL.openConnection();
			conn.connect();
		} catch (IOException e) {
			consoleView.append("\n"+ Mesh4jUITranslator.getErrorURLConnectionFailed(endpointHeader));
			return false;
		}
		return true;
	}
			
	private boolean validateFile(String fileName, String endpointHeader){
		if(!(fileName != null && fileName.trim().length() > 5 
				&& (fileName.toUpperCase().endsWith(".KMZ") || fileName.toUpperCase().endsWith(".KML") || fileName.toUpperCase().endsWith(".XML")))){
			consoleView.append("\n"+ Mesh4jUITranslator.getErrorFileType(endpointHeader));
			return false;
		}
		return true;
	}
	
	private boolean validateKMLFile(String fileName, String header){
		if(
			!(fileName != null && fileName.trim().length() > 5 && 
			(fileName.trim().toUpperCase().endsWith(".KML") || fileName.trim().toUpperCase().endsWith(".KMZ"))
			)
		){
			consoleView.append("\n"+ Mesh4jUITranslator.getErrorKMLType(header));
			return false;
		}
		
		File file = new File(fileName);
		if(!file.exists()){
			consoleView.append("\n"+ Mesh4jUITranslator.getErrorFileDoesNotExist(header));
			return false;
		}		
		return true;
	}
	
	private boolean isFile(String endpointValue) {
		return !isURL(endpointValue);
	}
	
	private boolean isURL(String endpointValue) {
		return endpointValue.toUpperCase().startsWith("HTTP");
	}

	private boolean isFeed(String endpointValue) {
		return endpointValue.toUpperCase().endsWith("XML");
	}
	
	private void prepareKMLInNewThread(){
		final String kmlFile = kmlFileToExternalActions.getText();
		
		Runnable longJob = new Runnable() {
			boolean done = false;
			public void run() {
				Thread thread = new Thread(new Runnable() {
					public void run() {
						display.syncExec(new Runnable() {
							public void run() {
								if (consoleView.isDisposed()) return;
								consoleView.append("\n"+ Mesh4jUITranslator.getMessagePrepareToSync(kmlFile));
							}
						});
						
						final String result = prepareKMLToSync(kmlFile);
						
						if (display.isDisposed()) return;		
						display.syncExec(new Runnable() {
							public void run() {
								if (consoleView.isDisposed()) return;
								consoleView.append("\n"+ Mesh4jUITranslator.getMessagePrepareToSyncCompleted(result));
							}
						});
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
	
	private String prepareKMLToSync(String kmlFile){
		try{
			IDOMLoader loader = KMLDOMLoaderFactory.createDOMLoader(kmlFile, this.identityProvider);
			DOMAdapter domAdapter = new DOMAdapter(loader);
			domAdapter.prepareDOMToSync();
			return Mesh4jUITranslator.getMessagePrepareToSyncSuccessfuly();
		} catch (MeshException e) {
			Logger.error(e.getMessage(), e);
			return Mesh4jUITranslator.getMessagePrepareToSyncFailed();
		}
	}
	
	private void cleanKMLInNewThread(){
		final String kmlFile = kmlFileToExternalActions.getText();
		
		Runnable longJob = new Runnable() {
			boolean done = false;
			public void run() {
				Thread thread = new Thread(new Runnable() {
					public void run() {
						display.syncExec(new Runnable() {
							public void run() {
								if (consoleView.isDisposed()) return;
								consoleView.append("\n"+ Mesh4jUITranslator.getMessageCleanKML(kmlFile));
							}
						});
						
						final String result = cleanKML(kmlFile);
						
						if (display.isDisposed()) return;		
						display.syncExec(new Runnable() {
							public void run() {
								if (consoleView.isDisposed()) return;
								consoleView.append("\n"+ Mesh4jUITranslator.getMessageCleanKMLCompleted(result));
							}
						});
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
	
	private String cleanKML(String kmlFile){
		try{
			IDOMLoader loader = KMLDOMLoaderFactory.createDOMLoader(kmlFile, this.identityProvider);
			DOMAdapter domAdapter = new DOMAdapter(loader);
			domAdapter.clean();
			return Mesh4jUITranslator.getMessageCleanKMLSuccessfuly();
		} catch (MeshException e) {
			Logger.error(e.getMessage(), e);
			return Mesh4jUITranslator.getMessageCleanKMLFailed();
		}
	}
	
	private void purgeKMLInNewThread(){
		final String kmlFile = kmlFileToExternalActions.getText();
		
		Runnable longJob = new Runnable() {
			boolean done = false;
			public void run() {
				Thread thread = new Thread(new Runnable() {
					public void run() {
						display.syncExec(new Runnable() {
							public void run() {
								if (consoleView.isDisposed()) return;
								consoleView.append("\n"+ Mesh4jUITranslator.getMessagePurgeKML(kmlFile));
							}
						});
						
						final String result = purgueKML(kmlFile);
						
						if (display.isDisposed()) return;		
						display.syncExec(new Runnable() {
							public void run() {
								if (consoleView.isDisposed()) return;
								consoleView.append("\n"+ Mesh4jUITranslator.getMessagePurgeKMLCompleted(result));
							}
						});
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
	
	private String purgueKML(String kmlFile){
		try{
			IDOMLoader loader = KMLDOMLoaderFactory.createDOMLoader(kmlFile, this.identityProvider);
			DOMAdapter domAdapter = new DOMAdapter(loader);
			domAdapter.purgue();
			return Mesh4jUITranslator.getMessagePurgeKMLSuccessfuly();
		} catch (MeshException e) {
			Logger.error(e.getMessage(), e);
			return Mesh4jUITranslator.getMessagePurgeKMLFailed();
		}
	}
	
	private void initializeDefaults() throws IOException{
		PropertiesProvider prop = new PropertiesProvider("mesh4j_sms.properties");
		this.defaultKmlFile = prop.getDefaultEnpoint1();					
		this.baseDirectory = prop.getBaseDirectory();
		this.defaultPhoneNumberDestination = prop.getString("default.sms.phone.number.destination");
		this.defaultEndpoint1 = prop.getDefaultEnpoint1();					
		this.defaultEndpoint2 = prop.getDefaultEnpoint2();			
		this.identityProvider = prop.getIdentityProvider();
		
		File dirDemo = new File(baseDirectory+"\\demo\\");
		if(!dirDemo.exists()){
			dirDemo.mkdirs();
		}
	}

	@Override
	public void notifyReceiveMessage(String endpointId, String message,
			Date date) {
		this.log("\t"+Mesh4jSmsUITranslator.getMessageNotifyReceiveMessage(endpointId, message));
	}

	@Override
	public void notifyReceiveMessageError(String endpointId, String message, Date date) {
		this.log("\t"+Mesh4jSmsUITranslator.getMessageNotifyReceiveMessageError(endpointId, message));		
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
	public void notifyCancelSyncErrorSyncSessionNotOpen(ISyncSession syncSession) {
		log(Mesh4jSmsUITranslator.getMessageCancelSyncErrorSessionNotOpen(syncSession.getTarget(), syncSession.getSourceId()));		
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
	public void notifyMessageProcessed(ISyncSession syncSession, IMessage message, List<IMessage> response) {
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
