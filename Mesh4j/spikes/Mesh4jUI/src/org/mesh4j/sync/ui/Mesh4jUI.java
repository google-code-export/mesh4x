package org.mesh4j.sync.ui;

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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.dom.DOMAdapter;
import org.mesh4j.sync.adapters.dom.IDOMLoader;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.http.HttpSyncAdapter;
import org.mesh4j.sync.adapters.kml.KMLDOMLoaderFactory;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.properties.PropertiesProvider;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.ui.translator.Mesh4jUITranslator;
import org.mesh4j.sync.validations.MeshException;


public class Mesh4jUI {

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
	private IIdGenerator idGenerator = IdGenerator.INSTANCE;
	
	// BUSINESS METHODS
	public static void main (String [] args) {
		try{
			Mesh4jUI meshUI = new Mesh4jUI();
			meshUI.initializeDefaults();
			meshUI.openMesh();
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
	}
	
	private void openMesh(){
		this.display = new Display();
		
		this.shell = new Shell(display);
		this.shell.setText(Mesh4jUITranslator.getTitle());

		final Group group = new Group(shell, SWT.NONE);
		group.setText(Mesh4jUITranslator.getLabelGroupMaintenance());
		final GridData gd_group = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd_group.widthHint = 709;
		group.setLayoutData(gd_group);
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 3;
		group.setLayout(gridLayout_1);
		
		
		Label labelKmlFile = new Label (group, SWT.NONE);
		labelKmlFile.setText(Mesh4jUITranslator.getLabelKMLFile());
		
		kmlFileToExternalActions = new Text (group, SWT.BORDER);
		final GridData gd_kmlFileToExternalActions = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd_kmlFileToExternalActions.widthHint = 550;
		kmlFileToExternalActions.setLayoutData(gd_kmlFileToExternalActions);
		
		Button buttonFileDialogKml = new Button(group, SWT.PUSH);
		buttonFileDialogKml.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectedFileName = openFileDialogKML(kmlFileToExternalActions.getText());
				if(selectedFileName != null){
					kmlFileToExternalActions.setText(selectedFileName);
				}
			}
		});
		buttonFileDialogKml.setText("...");

		final Composite composite = new Composite(group, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		final GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.numColumns = 3;
		composite.setLayout(gridLayout_2);
		
		Button buttonPrepareKMLToSync = new Button(composite, SWT.PUSH);
		buttonPrepareKMLToSync.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				boolean ok = validateKMLFile(kmlFileToExternalActions.getText(), "KmlFile");
				if(ok){
					prepareKMLInNewThread();
				}
			}
		});
		
		buttonPrepareKMLToSync.setText(Mesh4jUITranslator.getLabelPrepareToSync());
		
		Button buttonPurgeKML = new Button(composite, SWT.PUSH);
		buttonPurgeKML.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				boolean ok = validateKMLFile(kmlFileToExternalActions.getText(), "KmlFile");
				if(ok){
					purgeKMLInNewThread();
				}
			}
		});		
		buttonPurgeKML.setText(Mesh4jUITranslator.getLabelPurge());
			
		Button buttonCleanKML = new Button(composite, SWT.PUSH);
		buttonCleanKML.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				boolean ok = validateKMLFile(kmlFileToExternalActions.getText(), "KmlFile");
				if(ok){
					cleanKMLInNewThread();
				}
			}
		});		
		buttonCleanKML.setText(Mesh4jUITranslator.getLabelClean());
		new Label(group, SWT.NONE);

		final Group group_1 = new Group(shell, SWT.NONE);
		group_1.setText(Mesh4jUITranslator.getLabelGroupSync());
		final GridData gd_group_1 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd_group_1.widthHint = 714;
		group_1.setLayoutData(gd_group_1);
		final GridLayout gridLayout_3 = new GridLayout();
		gridLayout_3.numColumns = 3;
		group_1.setLayout(gridLayout_3);
		
		Label label = new Label (group_1, SWT.NONE);
		label.setText (Mesh4jUITranslator.getLabelEndpoint1());
		
		endpoint1 = new Text (group_1, SWT.BORDER);
		endpoint1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		endpoint1.setText(this.defaultEndpoint1);
		
		Button buttonSource = new Button(group_1, SWT.PUSH);
		buttonSource.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectedFileName = openFileDialog(endpoint1.getText());
				if(selectedFileName != null){
					endpoint1.setText(selectedFileName);
				}
			}
		});
		buttonSource.setText("...");
		
		Label labelTarget = new Label (group_1, SWT.NONE);
		labelTarget.setText (Mesh4jUITranslator.getLabelEndpoint2());
		
		endpoint2 = new Text (group_1, SWT.BORDER);
		endpoint2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		endpoint2.setText(this.defaultEndpoint2);
		
		Button buttonTarget = new Button(group_1, SWT.PUSH);
		buttonTarget.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectedFileName = openFileDialog(endpoint2.getText());
				if(selectedFileName != null){
					endpoint2.setText(selectedFileName);
				}
			}
		});
		buttonTarget.setText("...");
		
		Button buttonSynchronize = new Button(group_1, SWT.PUSH);
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
		new Label(group_1, SWT.NONE);
		new Label(group_1, SWT.NONE);
		
		Button buttonClean = new Button(shell, SWT.PUSH);
		buttonClean.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		buttonClean.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				consoleView.setText("");
			}
		});
		buttonClean.setText(Mesh4jUITranslator.getLabelClean());
		
		consoleView = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		final GridData gd_consoleView = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_consoleView.heightHint = 300;
		gd_consoleView.widthHint = 675;
		consoleView.setLayoutData(gd_consoleView);
		consoleView.setText("");
		
		final GridLayout gridLayout = new GridLayout();
		shell.setLayout (gridLayout);
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
		// TODO (JMT) refactoring: change for ISyncAdapterFactory
		if(isURL(endpoint)){
			return new HttpSyncAdapter(endpoint, RssSyndicationFormat.INSTANCE, this.identityProvider);
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
		PropertiesProvider prop = new PropertiesProvider();
		this.defaultEndpoint1 = prop.getDefaultEnpoint1();					
		this.defaultEndpoint2 = prop.getDefaultEnpoint2();			
		this.identityProvider = prop.getIdentityProvider();
	}
	
}

