package com.mesh4j.sync.ui;

import java.io.File;
import java.util.List;

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

import com.mesh4j.sync.SyncEngine;
import com.mesh4j.sync.adapters.compound.CompoundRepositoryAdapter;
import com.mesh4j.sync.adapters.file.FileSyncRepository;
import com.mesh4j.sync.adapters.kml.KMLContentAdapter;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.security.NullSecurity;

public class Mesh4jUI {  // TODO (JMT) REFACTORING: subclass Composite...

	private Display display;
	private Shell shell;
	private Text sourceFile;
	private Text targetFile;
	private Text consoleView;
	
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
		
		sourceFile = new Text (shell, SWT.BORDER);
		//sourceFile.setLayoutData (new RowData (300, SWT.DEFAULT));
		sourceFile.setLayoutData (new GridData(300, 15));
		
		Button buttonSource = new Button(shell, SWT.PUSH);
		buttonSource.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectedFileName = openFileDialog(sourceFile.getText());
				if(selectedFileName != null){
					sourceFile.setText(selectedFileName);
				}
			}
		});
		buttonSource.setText("...");
		
		Label labelTarget = new Label (shell, SWT.NONE);
		labelTarget.setText ("Endpoint 2: ");
		
		targetFile = new Text (shell, SWT.BORDER);
		//targetFile.setLayoutData (new RowData (300, SWT.DEFAULT));
		targetFile.setLayoutData (new GridData(300, 15));
		
		Button buttonTarget = new Button(shell, SWT.PUSH);
		buttonTarget.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectedFileName = openFileDialog(targetFile.getText());
				if(selectedFileName != null){
					targetFile.setText(selectedFileName);
				}
			}
		});
		buttonTarget.setText("...");
		
		Button buttonSynchronize = new Button(shell, SWT.PUSH);
		buttonSynchronize.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean okSource = validateFile(getSourceKmlFileName());
				if(!okSource){
					consoleView.append("\nPlease verify that endpoint 1 is a valid kml file to continue !!!");
				}

				boolean okTarget = validateFile(getTargetKmlFileName());
				if(!okTarget){
					consoleView.append("\nPlease verify that endpoint 2 is a valid kml file to continue !!!");
				}
				
				if(okSource && okTarget){
					if(getSourceKmlFileName().equals(getTargetKmlFileName())){
						consoleView.append("\nPlease verify that endpoint 1 and endpoint 2 are differents kml files to continue !!!");
					} else {
						synchronizeItemsInNewThread();
					}
				}
			}
		});
		buttonSynchronize.setText("Synchronize");
		
		Button buttonClean = new Button(shell, SWT.PUSH);
		buttonClean.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				consoleView.setText("");
			}
		});
		buttonClean.setText("Clean");
		
		consoleView = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		consoleView.setLayoutData(new GridData(300, 300));
		consoleView.setText("");
		
		shell.setLayout (new GridLayout());
		shell.pack ();
		shell.open();
		
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();

	}
	
	private String openFileDialog(String fileName){
		String path = "c:\\";
		String name = "";
	
		if(fileName != null && fileName.trim().length() > 0){
			File file = new File(fileName);
			path = file.getPath();
			name = file.getName();
		}
		
		FileDialog dialog = new FileDialog (shell, SWT.OPEN);
		dialog.setFilterNames (new String [] {"Kml", "All Files (*.*)"});
		dialog.setFilterExtensions (new String [] {"*.kml", "*.*"}); //Windows wild cards
		dialog.setFilterPath (path); //Windows path
		dialog.setFileName (name);
		String fileNameSelected = dialog.open();
		return fileNameSelected;
	}
	
	private void synchronizeItemsInNewThread(){
		final String sourceKmlFileName = this.getSourceKmlFileName();
		final String sourceSyncFileName = this.getSourceSyncFileName();
		final String targetKmlFileName = this.getTargetKmlFileName();
		final String targetSyncFileName = this.getTargetSyncFileName();
		
		Runnable longJob = new Runnable() {
			boolean done = false;
			public void run() {
				Thread thread = new Thread(new Runnable() {
					public void run() {
						display.syncExec(new Runnable() {
							public void run() {
								if (consoleView.isDisposed()) return;
								consoleView.append("\nStart running synchronize: ");
								consoleView.append("\n\tEndpoint 1 kml file: " + getSourceKmlFileName());
								consoleView.append("\n\tEndpoint 1 sync file:" + getSourceSyncFileName());
								consoleView.append("\n\tEndpoint 2 kml file: " + getTargetKmlFileName());
								consoleView.append("\n\tEndpoint 2 sync file: " + getTargetSyncFileName());
							}
						});
						
						final String syncResult = synchronizeItems(sourceKmlFileName, sourceSyncFileName, targetKmlFileName, targetSyncFileName);
						
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
	
	private String synchronizeItems(String sourceKmlFileName, String sourceSyncFileName, String targetKmlFileName, String targetSyncFileName){
		
		try{
			FileSyncRepository sourceSyncRepo = new FileSyncRepository(sourceSyncFileName, NullSecurity.INSTANCE);
			KMLContentAdapter sourceKmlContent = new KMLContentAdapter(sourceKmlFileName);
			
			FileSyncRepository targetSyncRepo = new FileSyncRepository(targetSyncFileName, NullSecurity.INSTANCE);
			KMLContentAdapter targetKmlContent = new KMLContentAdapter(targetKmlFileName);
			
			CompoundRepositoryAdapter sourceRepo = new CompoundRepositoryAdapter(sourceSyncRepo, sourceKmlContent, NullSecurity.INSTANCE);
			CompoundRepositoryAdapter targetRepo = new CompoundRepositoryAdapter(targetSyncRepo, targetKmlContent, NullSecurity.INSTANCE);
			
			SyncEngine syncEngine = new SyncEngine(sourceRepo, targetRepo);
			List<Item> conflicts = syncEngine.synchronize();
			if(conflicts.isEmpty()){
				return "Successfully";
			} else {
				return "Conflicts";
			}
		} catch (Exception e) {
			return "Unexpected error: " + e.getMessage();
		}
	}

	private String getTargetKmlFileName() {
		return this.targetFile.getText();
	}

	private String getTargetSyncFileName() {
		String kmlName = this.targetFile.getText();
		String syncFileName = kmlName.substring(0, kmlName.length()-4) + "_sync.xml";
		return syncFileName;
	}

	private String getSourceKmlFileName() {
		return this.sourceFile.getText();
	}

	private String getSourceSyncFileName() {
		String kmlName = this.sourceFile.getText();
		String syncFileName = kmlName.substring(0, kmlName.length()-4) + "_sync.xml";
		return syncFileName;
	}
	
	public boolean validateFile(String fileName){
		File file = new File(fileName);
		boolean okTarget = fileName != null && fileName.trim().length() > 5 && fileName.endsWith(".kml")  && file.exists() && file.canRead();
		return okTarget;
	}
}

