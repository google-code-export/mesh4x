package com.mesh4j.sync.ui;

import java.io.File;
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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.mesh4j.sync.adapters.sms.SmsHelper;
import com.mesh4j.sync.message.ISyncSession;
import com.mesh4j.sync.message.MessageSyncEngine;
import com.mesh4j.sync.message.channel.sms.connection.ISmsConnectionInboundOutboundNotification;
import com.mesh4j.sync.message.channel.sms.connection.smslib.Modem;
import com.mesh4j.sync.message.core.IMessageSyncAware;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.properties.PropertiesProvider;
import com.mesh4j.sync.ui.translator.Mesh4jSmsUITranslator;

public class MeshSmsUI implements ISmsConnectionInboundOutboundNotification, IMessageSyncAware{

	private final static Log Logger = LogFactory.getLog(Mesh4jUI.class);
	
	// MODEL VARIABLES
	private Display display;
	private Shell shell;
	private Text textKmlFile;
	private Text consoleView;
	private Combo comboPhone;
	private Text textSmsNumber;
	private Button buttonCompress;
	
	private String defaultKmlFile;
	private String defaultPhoneNumberDestination;
	private String baseDirectory;

	private MessageSyncEngine syncEngine;
	
	// BUSINESS METHODS
	public static void main (String [] args) {
		MeshSmsUI meshUI = new MeshSmsUI();
		meshUI.initializeDefaults();
		
		Modem modem = SmsHelper.getDefaultModem();
		meshUI.syncEngine = SmsHelper.createSyncEngine(meshUI, meshUI, modem);
		meshUI.openMesh(modem);
		
	}

	private void openMesh(Modem modem) {
		this.display = new Display();
		
		this.shell = new Shell(display);
		this.shell.setText(Mesh4jSmsUITranslator.getTitle());
		
		Label labelPhone = new Label (shell, SWT.NONE);
		labelPhone.setText(Mesh4jSmsUITranslator.getLabelPhone());

		comboPhone = new Combo (shell, SWT.READ_ONLY);
		comboPhone.setItems(SmsHelper.getAvailableModems(modem));
		comboPhone.setSize (200, 200);
		comboPhone.select(0);
		comboPhone.setEnabled(false);
		
		Label labelKmlFile = new Label (shell, SWT.NONE);
		labelKmlFile.setText(Mesh4jSmsUITranslator.getLabelKMLFile());

		textKmlFile = new Text (shell, SWT.BORDER);
		textKmlFile.setLayoutData (new GridData(600, 15));
		textKmlFile.setText(this.defaultKmlFile);
		
		Button buttonKmlFile = new Button(shell, SWT.PUSH);
		buttonKmlFile.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectedFileName = openFileDialogKML(textKmlFile.getText());
				if(selectedFileName != null){
					textKmlFile.setText(selectedFileName);
				}
			}
		});
		buttonKmlFile.setText("...");

		Label labelSms = new Label (shell, SWT.NONE);
		labelSms.setText(Mesh4jSmsUITranslator.getLabelPhoneDestination());

		textSmsNumber = new Text (shell, SWT.BORDER);
		textSmsNumber.setLayoutData (new GridData(600, 15));
		textSmsNumber.setText(this.defaultPhoneNumberDestination);
		
		Button buttonSynchronize = new Button(shell, SWT.PUSH);
		buttonSynchronize.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				boolean ok = validateInputs();
					if(ok){
						consoleView.setText("");
						executeInNewThread(false);
					}
				}
			}
		);		
		buttonSynchronize.setText(Mesh4jSmsUITranslator.getLabelSynchronize());
		buttonSynchronize.setEnabled(modem != null);
		
		new Label(shell, SWT.LINE_SOLID);
		
		buttonCompress = new Button(shell, SWT.CHECK);
		buttonCompress.setText(Mesh4jSmsUITranslator.getLabelCompressMessage());
		buttonCompress.setSelection(true);
		
		Button buttonEmulate = new Button(shell, SWT.PUSH);
		buttonEmulate.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				boolean ok = validateInputs();
					if(ok){
						consoleView.setText("");
						executeInNewThread(true);
					}
				}
			}
		);		
		buttonEmulate.setText(Mesh4jSmsUITranslator.getLabelEmulate());
		
		consoleView = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		consoleView.setLayoutData(new GridData(900, 300));
		consoleView.setText("");
		
		shell.setLayout (new GridLayout());
		shell.pack ();
		shell.open();
		
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();		
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
	
	private void executeInNewThread(final boolean emulate){
		final String smsFrom = this.comboPhone.getText();
		final String smsTo = this.textSmsNumber.getText();
		final boolean useCompression = this.buttonCompress.getSelection();
		final String kmlFileName = this.textKmlFile.getText();
		
		Runnable longJob = new Runnable() {
			boolean done = false;
			public void run() {
				Thread thread = new Thread(new Runnable() {
					public void run() {
												
						final String syncResult = emulate 
							? emulateSync(smsFrom, smsTo, useCompression, kmlFileName)
							: synchronizeItems(kmlFileName, smsTo);
						
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
		this.log(Mesh4jSmsUITranslator.getMessageNotifyReceiveMessageError(endpointId, message));
	}

	@Override
	public void notifyReceiveMessageError(String endpointId, String message, Date date) {
		this.log(Mesh4jSmsUITranslator.getMessageNotifyReceiveMessage(endpointId, message));		
	}

	@Override
	public void notifySendMessage(String endpointId, String message) {
		this.log(Mesh4jSmsUITranslator.getMessageNotifySendMessage(endpointId, message));
	}

	@Override
	public void notifySendMessageError(String endpointId, String message) {
		this.log(Mesh4jSmsUITranslator.getMessageNotifySendMessageError(endpointId, message));		
	}

	@Override
	public void beginSync(ISyncSession syncSession) {
		log(Mesh4jSmsUITranslator.getLabelStart());		
	}

	@Override
	public void endSync(ISyncSession syncSession, List<Item> conflicts) {
		log(Mesh4jSmsUITranslator.getLabelSuccess());		
	}

	
}
