package com.mesh4j.sync.ui;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.mesh4j.sync.IRepositoryAdapter;
import com.mesh4j.sync.SyncEngine;
import com.mesh4j.sync.adapters.compound.CompoundRepositoryAdapter;
import com.mesh4j.sync.adapters.compound.IContentAdapter;
import com.mesh4j.sync.adapters.feed.FeedAdapter;
import com.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import com.mesh4j.sync.adapters.feed.url.URLFeedAdapter;
import com.mesh4j.sync.adapters.file.FileSyncRepository;
import com.mesh4j.sync.adapters.kml.KMLContentAdapter;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.security.NullSecurity;
import com.mesh4j.sync.validations.MeshException;

public class Mesh4jUI {  // TODO (JMT) REFACTORING: subclass Composite...

	private final static Log Logger = LogFactory.getLog(Mesh4jUI.class);
	
	private Display display;
	private Shell shell;
	private Text endpoint1;
	private Text endpoint2;
	private Text consoleView;
	private Text kmlToPrepareToSync;
	
	public static void main (String [] args) {
		Mesh4jUI meshUI = new Mesh4jUI();
		meshUI.openMesh();
	}
	
	private void openMesh(){
		this.display = new Display();
		
		this.shell = new Shell(display);
		this.shell.setText("Mesh Example");
		
		Label label = new Label (shell, SWT.NONE);
		label.setText ("Endpoint 1: ");
		
		endpoint1 = new Text (shell, SWT.BORDER);
		endpoint1.setLayoutData (new GridData(600, 15));
		
		Button buttonSource = new Button(shell, SWT.PUSH);
		buttonSource.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectedFileName = openFileDialog(endpoint1.getText());
				if(selectedFileName != null){
					endpoint1.setText(selectedFileName);
				}
			}
		});
		buttonSource.setText("...");
		
		Label labelTarget = new Label (shell, SWT.NONE);
		labelTarget.setText ("Endpoint 2: ");
		
		endpoint2 = new Text (shell, SWT.BORDER);
		endpoint2.setLayoutData (new GridData(600, 15));
		
		Button buttonTarget = new Button(shell, SWT.PUSH);
		buttonTarget.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectedFileName = openFileDialog(endpoint2.getText());
				if(selectedFileName != null){
					endpoint2.setText(selectedFileName);
				}
			}
		});
		buttonTarget.setText("...");
		
		Button buttonSynchronize = new Button(shell, SWT.PUSH);
		buttonSynchronize.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				boolean ok = validateEndpoints();
					if(ok){
						synchronizeItemsInNewThread();
					}
				}
			}
		);
		
		buttonSynchronize.setText("Synchronize");
		
		Button buttonClean = new Button(shell, SWT.PUSH);
		buttonClean.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				consoleView.setText("");
			}
		});
		buttonClean.setText("Clean");
		
//		Button buttonLog = new Button(shell, SWT.PUSH);
//		buttonLog.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				viewLog();
//			}
//		});
//		buttonLog.setText("Log");
		
		consoleView = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		consoleView.setLayoutData(new GridData(600, 300));
		consoleView.setText("");
		
		
		Label labelKmlFile = new Label (shell, SWT.NONE);
		labelKmlFile.setText ("KML File: ");
		
		kmlToPrepareToSync = new Text (shell, SWT.BORDER);
		kmlToPrepareToSync.setLayoutData (new GridData(600, 15));
		
		Button buttonFileDialogKml = new Button(shell, SWT.PUSH);
		buttonFileDialogKml.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectedFileName = openFileDialogKML(kmlToPrepareToSync.getText());
				if(selectedFileName != null){
					kmlToPrepareToSync.setText(selectedFileName);
				}
			}
		});
		buttonFileDialogKml.setText("...");
		
		Button buttonPrepareKMLToSync = new Button(shell, SWT.PUSH);
		buttonPrepareKMLToSync.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				boolean ok = validateKMLFile(kmlToPrepareToSync.getText(), "Kml file");
				if(ok){
					prepareKMLInNewThread();
				}
			}
		});
		
		buttonPrepareKMLToSync.setText("Prepare file to sync");
		
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
	
	private String openFileDialog(String fileName){
		String fileNameSelected = openFileDialog(fileName, new String [] {"Kml", "Feed", "All Files (*.*)"}, new String [] {"*.kml", "*.xml", "*.*"});
		return fileNameSelected;
	}
	
	private String openFileDialog(String fileName, String[] filterNames, String[] filterExtensions){
		String path = "c:\\";
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
								consoleView.append("\nStart running synchronize: ");
								consoleView.append("\n\tEndpoint 1: " + endpoint1);
								consoleView.append("\n\tEndpoint 2: " + endpoint2);
							}
						});
						
						final String syncResult = synchronizeItems(endpoint1, endpoint2);
						
						if (display.isDisposed()) return;						
						display.syncExec(new Runnable() {
							public void run() {
								if (consoleView.isDisposed()) return;
								consoleView.append("\nCompleted running synchronize: " + syncResult);
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
			IRepositoryAdapter sourceRepo = makeRepositoryAdapter(endpoint1);
			IRepositoryAdapter targetRepo = makeRepositoryAdapter(endpoint2);
			
			SyncEngine syncEngine = new SyncEngine(sourceRepo, targetRepo);
			List<Item> conflicts = syncEngine.synchronize();
			if(conflicts.isEmpty()){
				return "Successfully";
			} else {
				return "Conflicts";
			}
		} catch (MeshException e) {
			Logger.error(e.getMessage(), e);
			return "Unexpected error";
		}
	}

	private IRepositoryAdapter makeRepositoryAdapter(String endpoint) {
		if(isURL(endpoint)){
			return new URLFeedAdapter(endpoint, RssSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		} else {
			if(isFeed(endpoint)){
				return new FeedAdapter(endpoint, NullSecurity.INSTANCE);
			}else{
				String endpointSync = getSyncFileName(endpoint);
				FileSyncRepository sourceSyncRepo = new FileSyncRepository(endpointSync, NullSecurity.INSTANCE);
				IContentAdapter sourceContent = new KMLContentAdapter(endpoint);
				return new CompoundRepositoryAdapter(sourceSyncRepo, sourceContent, NullSecurity.INSTANCE);
			}
		}
	}

	private String getSyncFileName(String endpointFileName) {
		String syncFileName = endpointFileName.substring(0, endpointFileName.length()-4) + "_sync.xml";
		return syncFileName;
	}

	private boolean validateEndpoints() {
		boolean okEndpoint1 = validate(getEndpoint1(), "Endpoint1");
		boolean okEndpoint2 = validate(getEndpoint2(), "Endpoint2");		
		
		if(okEndpoint1 && okEndpoint2){
			if(getEndpoint1().equals(getEndpoint2())){
				consoleView.append("\nPlease verify that endpoint 1 and endpoint 2 are differents to continue !!!");
				return false;
			}
		}
		return okEndpoint1 && okEndpoint2;

	}
	
	private boolean validate(String endpointValue, String endpointHeader){
		if(endpointValue ==  null || endpointValue.trim().length() == 0){
			consoleView.append("\nPlease complete " + endpointHeader + " , it is required to continue (Example kml file: C:\\MyFile.kml, Example feed file: C:\\MyFeed.xml, Example URL: http://localhost:7777/feeds/KML).");
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
			consoleView.append("\nPlease verify "+ endpointHeader + ": the url is not a valid URL (Example: http://localhost:7777/feeds/KML).");
			return false;
		}
		try {
			URLConnection conn = newURL.openConnection();
			conn.connect();
		} catch (IOException e) {
			consoleView.append("\nPlease verify "+ endpointHeader + ": connection failed.");
			return false;
		}
		return true;
	}
			
	private boolean validateFile(String fileName, String endpointHeader){
		if(!(fileName != null && fileName.trim().length() > 5 && (fileName.endsWith(".kml") || fileName.endsWith(".xml")))){
			consoleView.append("\nPlease verify "+ endpointHeader + ": complete with a kml file (example C:\\MyFile.kml) or feed file (example c:\\MyFeed.xml).");
			return false;
		}
		
		File file = new File(fileName);
		if(!file.exists()){
			consoleView.append("\nPlease verify "+ endpointHeader + ": the file does not exist.");
			return false;
		}		
		return true;
	}
	
	private boolean validateKMLFile(String fileName, String header){
		if(!(fileName != null && fileName.trim().length() > 5 && (fileName.endsWith(".kml")))){
			consoleView.append("\nPlease verify "+ header + ": complete with a kml file (example C:\\MyFile.kml).");
			return false;
		}
		
		File file = new File(fileName);
		if(!file.exists()){
			consoleView.append("\nPlease verify "+ header + ": the file does not exist.");
			return false;
		}		
		return true;
	}
	
	private boolean isFile(String endpointValue) {
		return !isURL(endpointValue);
	}
	
	private boolean isURL(String endpointValue) {
		return endpointValue.startsWith("http");
	}

	private boolean isFeed(String endpointValue) {
		return endpointValue.endsWith("xml");
	}
	
//	private void viewLog(){
//		String fileName = "mesh4j.log";
//		try {
//			consoleView.append("\n\nLog:\n");
//			FileReader reader = new FileReader(fileName);
//			BufferedReader br = new BufferedReader(reader);
//			String line;
//			while((line = br.readLine()) != null) {
//				consoleView.append(line);
//				consoleView.append("\n");
//			}
//			reader.close(); 
//		} catch (Exception e) {
//			consoleView.append("\n\nError reading mesh4j.log\n");
//		}
//	}
	
	private void prepareKMLInNewThread(){
		final String kmlFile = kmlToPrepareToSync.getText();
		
		Runnable longJob = new Runnable() {
			boolean done = false;
			public void run() {
				Thread thread = new Thread(new Runnable() {
					public void run() {
						display.syncExec(new Runnable() {
							public void run() {
								if (consoleView.isDisposed()) return;
								consoleView.append("\nStart preparing kml: " + kmlFile);
							}
						});
						
						final String result = prepareKMLToSync(kmlFile);
						
						if (display.isDisposed()) return;		
						display.syncExec(new Runnable() {
							public void run() {
								if (consoleView.isDisposed()) return;
								consoleView.append("\nCompleted preparing kml: " + result);
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
			KMLContentAdapter.prepareKMLToSync(kmlFile);
			return "Successfully";
		} catch (MeshException e) {
			Logger.error(e.getMessage(), e);
			return "Unexpected error";
		}
	}
}

