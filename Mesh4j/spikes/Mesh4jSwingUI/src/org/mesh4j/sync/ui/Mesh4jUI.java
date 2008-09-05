package org.mesh4j.sync.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.properties.PropertiesProvider;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.ui.translator.Mesh4jUITranslator;
import org.mesh4j.sync.utils.SyncEngineUtil;

import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.swtdesigner.FocusTraversalOnArray;

public class Mesh4jUI {

	// MODEL VARIABLES
	private JLabel labelEndpoint1;
	private JButton buttonClean;
	private JButton buttonSynchronize;
	private JPanel panelSyncButtons;
	private JLabel labelEndpoint2;
	private JTextArea textAreaConsole;
	private JScrollPane scrollPane;
	private JTextField textFieldEndpoint1;
	private JButton buttonOpenFileEndpoint2;
	private JTextField textFieldEndpoint2;
	private JButton buttonOpenFileEndpoint1;
	private JFrame frame;
	private String defaultEndpoint1;
	private String defaultEndpoint2;
	private JButton buttonPrepareFileToSync;
	private JButton buttonOpenFileKml;
	private JButton buttonCleanSyncInfo;
	private JButton buttonPurgeKml;
	private JTextField textFieldKmlFile;
	private IIdentityProvider identityProvider = NullIdentityProvider.INSTANCE;
	private IIdGenerator idGenerator = IdGenerator.INSTANCE;	
	
	// BUSINESS METHODS
	
	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Mesh4jUI window = new Mesh4jUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Mesh4jUI() {
		this.initializeDefaults();
		createContents();
	}

	private void createContents() {
		frame = new JFrame();
		frame.getContentPane().setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("410dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("293dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC}));
		frame.setResizable(false);
		frame.setTitle(Mesh4jUITranslator.getTitle());
		frame.setBounds(100, 100, 846, 627);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final JPanel panelSync = new JPanel();
		panelSync.setFocusCycleRoot(true);
		panelSync.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED), Mesh4jUITranslator.getGroupSync(), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		panelSync.setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("338dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("20dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("214dlu")}));
		frame.getContentPane().add(panelSync, new CellConstraints(2, 2, 2, 1));

		textFieldEndpoint1 = new JTextField();
		panelSync.add(textFieldEndpoint1, new CellConstraints(4, 2));
		textFieldEndpoint1.setText(this.defaultEndpoint1);

		textFieldEndpoint2 = new JTextField();
		panelSync.add(textFieldEndpoint2, new CellConstraints(4, 4));
		textFieldEndpoint2.setText(this.defaultEndpoint2);
		
		ActionListener fileChooserEndpoint1ActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String selectedFileName = openFileDialog(textFieldEndpoint1.getText());
				if(selectedFileName != null){
					textFieldEndpoint1.setText(selectedFileName);
				}
			}
		};		
		buttonOpenFileEndpoint1 = new JButton();
		buttonOpenFileEndpoint1.setText(" . . . ");
		panelSync.add(buttonOpenFileEndpoint1, new CellConstraints(6, 2));
		buttonOpenFileEndpoint1.addActionListener(fileChooserEndpoint1ActionListener);

		
		ActionListener fileChooserEndpoint2ActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String selectedFileName = openFileDialog(textFieldEndpoint2.getText());
				if(selectedFileName != null){
					textFieldEndpoint2.setText(selectedFileName);
				}
			}
		};
		buttonOpenFileEndpoint2 = new JButton();
		buttonOpenFileEndpoint2.setText(" . . . ");
		panelSync.add(buttonOpenFileEndpoint2, new CellConstraints(6, 4));
		buttonOpenFileEndpoint2.addActionListener(fileChooserEndpoint2ActionListener);

		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBorder(new BevelBorder(BevelBorder.RAISED));
		panelSync.add(scrollPane, new CellConstraints("2, 8, 5, 1, default, default"));

		textAreaConsole = new JTextArea();
		
		textAreaConsole.setLineWrap(true);
		scrollPane.setViewportView(textAreaConsole);
		textAreaConsole.setToolTipText("console");
		textAreaConsole.setEditable(false);
		textAreaConsole.setPreferredSize(new Dimension(200, 320));

		labelEndpoint1 = DefaultComponentFactory.getInstance().createLabel(Mesh4jUITranslator.getLabelEndpoint1());
		panelSync.add(labelEndpoint1, new CellConstraints(2, 2));

		labelEndpoint2 = DefaultComponentFactory.getInstance().createLabel(Mesh4jUITranslator.getLabelEndpoint2());
		panelSync.add(labelEndpoint2, new CellConstraints(2, 4));

		panelSyncButtons = new JPanel();
		panelSyncButtons.setLayout(new FormLayout(new ColumnSpec[] {FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC}, new RowSpec[] {FormFactory.DEFAULT_ROWSPEC}));
		panelSync.add(panelSyncButtons, new CellConstraints(2, 6, 5, 1));

		ActionListener synchronizeActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				boolean ok = validateEndpoints(textAreaConsole, textFieldEndpoint1.getText(), textFieldEndpoint2.getText());
				if(ok){
					Task task = new Task(Task.SYNCHRONIZE);
					task.execute();	
				}
			}
		};		
		buttonSynchronize = new JButton();
		buttonSynchronize.setText(Mesh4jUITranslator.getLabelSyncronize());
		buttonSynchronize.addActionListener(synchronizeActionListener);
		panelSyncButtons.add(buttonSynchronize, new CellConstraints());

		
		ActionListener cleanActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				textAreaConsole.setText("");
			}
		};	
		buttonClean = new JButton();
		buttonClean.setText(Mesh4jUITranslator.getLabelClean());
		buttonClean.addActionListener(cleanActionListener);
		panelSyncButtons.add(buttonClean, new CellConstraints(3, 1));
		panelSync.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[] {labelEndpoint1, textFieldEndpoint1, buttonOpenFileEndpoint1, labelEndpoint2, textFieldEndpoint2, buttonOpenFileEndpoint2, buttonSynchronize, panelSyncButtons, buttonClean, scrollPane, textAreaConsole}));

		final JPanel panelKml = new JPanel();
		panelKml.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED), Mesh4jUITranslator.getGroupKML(), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		panelKml.setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("356dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("22dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC}));
		frame.getContentPane().add(panelKml, new CellConstraints(2, 4, 2, 1));

		final JLabel labelKmlFile = DefaultComponentFactory.getInstance().createLabel(Mesh4jUITranslator.getLabelKMLFile());
		panelKml.add(labelKmlFile, new CellConstraints(2, 2));

		textFieldKmlFile = new JTextField();
		panelKml.add(textFieldKmlFile, new CellConstraints(4, 2));

		ActionListener fileChooserKmlFileActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String selectedFileName = openFileDialogKML(textFieldKmlFile.getText());
				if(selectedFileName != null){
					textFieldKmlFile.setText(selectedFileName);
				}
			}
		};
		buttonOpenFileKml = new JButton();
		buttonOpenFileKml.setText(" . . . ");
		buttonOpenFileKml.addActionListener(fileChooserKmlFileActionListener);
		panelKml.add(buttonOpenFileKml, new CellConstraints(6, 2));

		final JPanel panelKmlButtons = new JPanel();
		panelKmlButtons.setLayout(new FormLayout(new ColumnSpec[] {FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC}, new RowSpec[] {FormFactory.DEFAULT_ROWSPEC}));
		panelKml.add(panelKmlButtons, new CellConstraints(2, 4, 5, 1));

		ActionListener prepareFileToSyncActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				boolean ok = validateKMLFile(textAreaConsole, textFieldKmlFile.getText(), "KmlFile");
				if(ok){
					Task task = new Task(Task.PREPARE_KML_TO_SYNC);
					task.execute();	
				}
			}
		};
		buttonPrepareFileToSync = new JButton();
		buttonPrepareFileToSync.setText(Mesh4jUITranslator.getLabelPrepareToSync());
		buttonPrepareFileToSync.addActionListener(prepareFileToSyncActionListener);
		panelKmlButtons.add(buttonPrepareFileToSync, new CellConstraints());

		ActionListener cleanSyncInfoActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				boolean ok = validateKMLFile(textAreaConsole, textFieldKmlFile.getText(), "KmlFile");
				if(ok){
					Task task = new Task(Task.CLEAN_KML);
					task.execute();	
				}
			}
		};
		buttonCleanSyncInfo = new JButton();
		buttonCleanSyncInfo.setText(Mesh4jUITranslator.getLabelClean());
		buttonCleanSyncInfo.addActionListener(cleanSyncInfoActionListener);
		panelKmlButtons.add(buttonCleanSyncInfo, new CellConstraints(3, 1));

		ActionListener purgeKmlActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				boolean ok = validateKMLFile(textAreaConsole, textFieldKmlFile.getText(), "KmlFile");
				if(ok){
					Task task = new Task(Task.PURGE_KML);
					task.execute();	
				}
			}
		};
		buttonPurgeKml = new JButton();
		buttonPurgeKml.setText(Mesh4jUITranslator.getLabelPurgue());
		buttonPurgeKml.addActionListener(purgeKmlActionListener);
		panelKmlButtons.add(buttonPurgeKml, new CellConstraints(5, 1));
	}
	
	private String openFileDialogKML(String fileName){
		String fileNameSelected = openFileDialog(fileName, new FileNameExtensionFilter("Kml/Kmz (*.kmz/*.kml)", "kml", "kmz"));
		return fileNameSelected;
	}
	
	private String openFileDialog(String fileName){
		String fileNameSelected = openFileDialog(fileName, new FileNameExtensionFilter("Feed Rss/Atom (*.xml) or Kml/Kmz (*.kmz/*.kml)", "kml", "kmz", "xml"));
		return fileNameSelected;
	}
	
	private String openFileDialog(String fileName, FileNameExtensionFilter filter){
		JFileChooser chooser = new JFileChooser();
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(filter);
		
		if(fileName != null && fileName.trim().length() > 0){
			if(SyncEngineUtil.isFile(fileName)){
				File file = new File(fileName);
				chooser.setSelectedFile(file);
			}
		}
		
		int returnVal = chooser.showOpenDialog(this.frame);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile().getAbsolutePath();
		} else{
			return null;
		}
	}
	
	private boolean validateKMLFile(JTextArea consoleView, String fileName, String header){
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
	
	private boolean validateEndpoints(JTextArea consoleView, String endpoint1, String endpoint2) {
		boolean okEndpoint1 = validate(consoleView, endpoint1, "Endpoint1");
		boolean okEndpoint2 = validate(consoleView, endpoint2, "Endpoint2");		
		
		if(okEndpoint1 && okEndpoint2){
			if(endpoint1.equals(endpoint2)){
				consoleView.append("\n"+ Mesh4jUITranslator.getErrorSameEndpoints());
				return false;
			}
		}
		return okEndpoint1 && okEndpoint2;

	}

	private boolean validate(JTextArea consoleView, String endpointValue, String endpointHeader){
		if(endpointValue ==  null || endpointValue.trim().length() == 0){
			consoleView.append("\n"+ Mesh4jUITranslator.getErrorEndpoint(endpointHeader));
			return false;
		}
		if(SyncEngineUtil.isURL(endpointValue)){
			return validateURL(consoleView, endpointValue, endpointHeader);
		} else{
			return validateFile(consoleView, endpointValue, endpointHeader);
		}
	}

	private boolean validateURL(JTextArea consoleView, String url, String endpointHeader){
		URL newURL;
		try {
			newURL = new URL(url);
		} catch (MalformedURLException e) {
			consoleView.append("\n"+ Mesh4jUITranslator.getErrorInvalidURL(endpointHeader));
			return false;
		}
		
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection)newURL.openConnection();
			conn.connect();
		} catch (Exception e) {
			consoleView.append("\n"+ Mesh4jUITranslator.getErrorURLConnectionFailed(endpointHeader));
			return false;
		}finally{
	    	if(conn != null){
	    		conn.disconnect();
	    	}
		}
		return true;
	}
	
	private boolean validateFile(JTextArea consoleView, String fileName, String endpointHeader){
		if(!(fileName != null && fileName.trim().length() > 5 
				&& (fileName.toUpperCase().endsWith(".KMZ") || fileName.toUpperCase().endsWith(".KML") || fileName.toUpperCase().endsWith(".XML")))){
			consoleView.append("\n"+ Mesh4jUITranslator.getErrorFileType(endpointHeader));
			return false;
		}
		return true;
	}
	
	private void initializeDefaults(){
		PropertiesProvider prop = new PropertiesProvider();
		this.defaultEndpoint1 = prop.getDefaultEnpoint1();					
		this.defaultEndpoint2 = prop.getDefaultEnpoint2();			
		this.identityProvider = prop.getIdentityProvider();
	}

	 private class Task extends SwingWorker<Void, Void> {
		 
		// CONSTANTS
		private final static int SYNCHRONIZE = 0;
		private final static int PREPARE_KML_TO_SYNC = 1;
		private final static int CLEAN_KML = 2;
		private final static int PURGE_KML = 3;
		 
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
        	if(action == SYNCHRONIZE){
        		textAreaConsole.append("\n"+ Mesh4jUITranslator.getMessageSyncStart());
				textAreaConsole.append("\n\t"+ Mesh4jUITranslator.getLabelEndpoint1() + textFieldEndpoint1.getText());
				textAreaConsole.append("\n\t"+ Mesh4jUITranslator.getLabelEndpoint2() + textFieldEndpoint2.getText());
        		String result = SyncEngineUtil.synchronizeItems(textFieldEndpoint1.getText(), textFieldEndpoint2.getText(), identityProvider, idGenerator);
        		textAreaConsole.append("\n"+ Mesh4jUITranslator.getMessageSyncCompleted(result));
        	} else if(action == PREPARE_KML_TO_SYNC){
        		textAreaConsole.append("\n"+ Mesh4jUITranslator.getMessagePrepareToSync(textFieldKmlFile.getText()));
        		String result = SyncEngineUtil.prepareKMLToSync(textFieldKmlFile.getText(), identityProvider);
        		textAreaConsole.append("\n"+ Mesh4jUITranslator.getMessagePrepareToSyncCompleted(result));
        	}else if(action == CLEAN_KML){
        		textAreaConsole.append("\n"+ Mesh4jUITranslator.getMessageCleanKML(textFieldKmlFile.getText()));
        		String result = SyncEngineUtil.cleanKML(textFieldKmlFile.getText(), identityProvider);   
        		textAreaConsole.append("\n"+ Mesh4jUITranslator.getMessageCleanKMLCompleted(result));
        	}else if(action == PURGE_KML){
        		textAreaConsole.append("\n"+ Mesh4jUITranslator.getMessagePurgueKML(textFieldKmlFile.getText()));
        		String result = SyncEngineUtil.purgueKML(textFieldKmlFile.getText(), identityProvider);
        		textAreaConsole.append("\n"+ Mesh4jUITranslator.getMessagePurgueKMLCompleted(result));
        	}
            return null;
        }

		@Override
        public void done() {
            enableAllButtons();
        }
    }

	 private void enableAllButtons(){
		 this.buttonClean.setEnabled(true);
		 this.buttonSynchronize.setEnabled(true);
		 this.buttonOpenFileEndpoint2.setEnabled(true);
		 this.buttonOpenFileEndpoint1.setEnabled(true);
		 this.buttonOpenFileKml.setEnabled(true);
		 this.buttonPrepareFileToSync.setEnabled(true);
		 this.buttonCleanSyncInfo.setEnabled(true);
		 this.buttonPurgeKml.setEnabled(true);
	 }
	 
	 private void disableAllButtons(){
		 this.buttonClean.setEnabled(false);
		 this.buttonSynchronize.setEnabled(false);
		 this.buttonOpenFileEndpoint2.setEnabled(false);
		 this.buttonOpenFileEndpoint1.setEnabled(false);
		 this.buttonOpenFileKml.setEnabled(false);
		 this.buttonPrepareFileToSync.setEnabled(false);
		 this.buttonCleanSyncInfo.setEnabled(false);
		 this.buttonPurgeKml.setEnabled(false);
	 }


}
