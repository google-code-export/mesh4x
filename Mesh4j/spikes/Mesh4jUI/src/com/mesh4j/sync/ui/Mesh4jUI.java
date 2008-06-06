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

import com.mesh4j.sync.ISyncAdapter;
import com.mesh4j.sync.SyncEngine;
import com.mesh4j.sync.adapters.dom.DOMAdapter;
import com.mesh4j.sync.adapters.dom.IDOMLoader;
import com.mesh4j.sync.adapters.feed.FeedAdapter;
import com.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import com.mesh4j.sync.adapters.http.HttpSyncAdapter;
import com.mesh4j.sync.adapters.kml.DOMLoaderFactory;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.properties.PropertiesProvider;
import com.mesh4j.sync.security.IIdentityProvider;
import com.mesh4j.sync.security.NullIdentityProvider;
import com.mesh4j.sync.ui.translator.Mesh4jUITranslator;
import com.mesh4j.sync.validations.MeshException;

public class Mesh4jUI {  // TODO (JMT) REFACTORING: subclass Composite...

	private final static Log Logger = LogFactory.getLog(Mesh4jUI.class);
	
	// MODEL VARIABLES
	private Display display;
	private Shell shell;
	private Text endpoint1;
	private Text endpoint2;
	private Text consoleView;
	private Text kmlFileToExternalActions;
	private String defaultEndpoint1;
	private String defaultEndpoint2;
	private IIdentityProvider identityProvider = NullIdentityProvider.INSTANCE;
	
	// BUSINESS METHODS
	public static void main (String [] args) {
		Mesh4jUI meshUI = new Mesh4jUI();
		meshUI.initializeDefaults();
		meshUI.openMesh();
	}
	
	private void openMesh(){
		this.display = new Display();
		
		this.shell = new Shell(display);
		this.shell.setText(Mesh4jUITranslator.getTitle());
		
		Label label = new Label (shell, SWT.NONE);
		label.setText (Mesh4jUITranslator.getLabelEndpoint1());
		
		endpoint1 = new Text (shell, SWT.BORDER);
		endpoint1.setLayoutData (new GridData(600, 15));
		endpoint1.setText(this.defaultEndpoint1);
		
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
		endpoint2.setText(this.defaultEndpoint2);
		
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
		
		buttonSynchronize.setText(Mesh4jUITranslator.getLabelSyncronize());
		
		Button buttonClean = new Button(shell, SWT.PUSH);
		buttonClean.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				consoleView.setText("");
			}
		});
		buttonClean.setText(Mesh4jUITranslator.getLabelClean());
		
		consoleView = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		consoleView.setLayoutData(new GridData(600, 300));
		consoleView.setText("");
		
		
		Label labelKmlFile = new Label (shell, SWT.NONE);
		labelKmlFile.setText(Mesh4jUITranslator.getLabelKMLFile());
		
		kmlFileToExternalActions = new Text (shell, SWT.BORDER);
		kmlFileToExternalActions.setLayoutData (new GridData(600, 15));
		
		Button buttonFileDialogKml = new Button(shell, SWT.PUSH);
		buttonFileDialogKml.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectedFileName = openFileDialogKML(kmlFileToExternalActions.getText());
				if(selectedFileName != null){
					kmlFileToExternalActions.setText(selectedFileName);
				}
			}
		});
		buttonFileDialogKml.setText("...");
		
		Button buttonPrepareKMLToSync = new Button(shell, SWT.PUSH);
		buttonPrepareKMLToSync.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				boolean ok = validateKMLFile(kmlFileToExternalActions.getText(), "KmlFile");
				if(ok){
					prepareKMLInNewThread();
				}
			}
		});
		
		buttonPrepareKMLToSync.setText(Mesh4jUITranslator.getLabelPrepareToSync());
		
		
		Button buttonCleanKML = new Button(shell, SWT.PUSH);
		buttonCleanKML.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				boolean ok = validateKMLFile(kmlFileToExternalActions.getText(), "KmlFile");
				if(ok){
					cleanKMLInNewThread();
				}
			}
		});		
		buttonCleanKML.setText(Mesh4jUITranslator.getLabelClean());
		
		Button buttonPurgueKML = new Button(shell, SWT.PUSH);
		buttonPurgueKML.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				boolean ok = validateKMLFile(kmlFileToExternalActions.getText(), "KmlFile");
				if(ok){
					purgueKMLInNewThread();
				}
			}
		});		
		buttonPurgueKML.setText(Mesh4jUITranslator.getLabelPurgue());
		
		shell.setLayout (new GridLayout());
		shell.pack ();
		shell.open();
		
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();

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
		if(isURL(endpoint)){
			return new HttpSyncAdapter(endpoint, RssSyndicationFormat.INSTANCE, this.identityProvider);
		} else {
			if(isFeed(endpoint)){
				return new FeedAdapter(endpoint, this.identityProvider);
			}else{
				IDOMLoader loader = DOMLoaderFactory.createDOMLoader(endpoint, this.identityProvider);
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
		try {
			URLConnection conn = newURL.openConnection();
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
		
		File file = new File(fileName);
		if(!file.exists()){
			consoleView.append("\n"+ Mesh4jUITranslator.getErrorFileDoesNotExist(endpointHeader));
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
		return endpointValue.startsWith("http");
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
			IDOMLoader loader = DOMLoaderFactory.createDOMLoader(kmlFile, this.identityProvider);
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
			IDOMLoader loader = DOMLoaderFactory.createDOMLoader(kmlFile, this.identityProvider);
			DOMAdapter domAdapter = new DOMAdapter(loader);
			domAdapter.clean();
			return Mesh4jUITranslator.getMessageCleanKMLSuccessfuly();
		} catch (MeshException e) {
			Logger.error(e.getMessage(), e);
			return Mesh4jUITranslator.getMessageCleanKMLFailed();
		}
	}
	
	private void purgueKMLInNewThread(){
		final String kmlFile = kmlFileToExternalActions.getText();
		
		Runnable longJob = new Runnable() {
			boolean done = false;
			public void run() {
				Thread thread = new Thread(new Runnable() {
					public void run() {
						display.syncExec(new Runnable() {
							public void run() {
								if (consoleView.isDisposed()) return;
								consoleView.append("\n"+ Mesh4jUITranslator.getMessagePurgueKML(kmlFile));
							}
						});
						
						final String result = purgueKML(kmlFile);
						
						if (display.isDisposed()) return;		
						display.syncExec(new Runnable() {
							public void run() {
								if (consoleView.isDisposed()) return;
								consoleView.append("\n"+ Mesh4jUITranslator.getMessagePurgueKMLCompleted(result));
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
			IDOMLoader loader = DOMLoaderFactory.createDOMLoader(kmlFile, this.identityProvider);
			DOMAdapter domAdapter = new DOMAdapter(loader);
			domAdapter.purgue();
			return Mesh4jUITranslator.getMessagePurgueKMLSuccessfuly();
		} catch (MeshException e) {
			Logger.error(e.getMessage(), e);
			return Mesh4jUITranslator.getMessagePurgueKMLFailed();
		}
	}
	
	private void initializeDefaults(){
		PropertiesProvider prop = new PropertiesProvider();
		this.defaultEndpoint1 = prop.getDefaultEnpoint1();					
		this.defaultEndpoint2 = prop.getDefaultEnpoint2();			
		this.identityProvider = prop.getIdentityProvider();
	}
	
}

